package lance.liang.group.latina;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.lzy.okserver.upload.*;
import com.tbruyelle.rxpermissions2.*;
import com.zhihu.matisse.*;
import io.reactivex.*;
import io.reactivex.disposables.*;
import java.io.*;
import java.util.*;

import io.reactivex.Observer;
import android.widget.AdapterView.*;
import com.zhihu.matisse.internal.entity.*;
import kotlin.math.*;

public class Chat extends AppCompatActivity
{
	private EditText text_message;
	private Button btn_send, btn_more;
	private ListView list_message;
	private ChatAdapter adp;
	private List<ItemBeanChat> data = new ArrayList<>();
	private SwipeRefreshLayout srl;
	private String gid = "0";
	private int gid_int = 0;
	private ActionBar bar;
	AlertDialog dialog;
	Timer timer;
	private final int code_pick = 0x91, code_file = 0x92, code_pick_room_head = 0x93;
	
	private StringCallback callback_text = new StringCallback() {
		public void onStart(Response<String> p1)
		{}
		@Override
		public void onError(Response<String> p1)
		{
			dialog.hide();
			AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
			builder.setMessage("网络错误. (Code: " + p1.code() + ")");
			builder.show();
		}
		@Override
		public void onSuccess(Response<String> p1)
		{
			dialog.hide();
			ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
			if (result.code == 0)
			{
				//getMessage();
				text_message.setText("");
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
				builder.setMessage(result.message + " (错误码: " + result.code + ")");
				builder.show();
			}
		}
	};
	
	static final String baseDir = Environment.getExternalStorageDirectory().getPath();//"/storage/emulated/0/";//"/sdcard/";

	private String[] getFileList(String path) {
		File dir = new File(path);
		List<String> res = new ArrayList<String>();
		if (!dir.exists()) {
			return res.toArray(new String[res.size()]);
		}
		File[] files = dir.listFiles();
		for (File f: files) {
			res.add(f.getAbsolutePath());
		}
		Collections.sort(res);
		return res.toArray(new String[res.size()]);
	}
	private void startAlertDialog(String path) {
		final String[] files = getFileList(path);
		String[] names = new String[files.length];
		for (int i=0; i<files.length; i++) {
			names[i] = files[i].substring(files[i].lastIndexOf("/") + 1, files[i].length());
		}
		new AlertDialog.Builder(this).setTitle("选择一个文件")
			.setItems(names, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					File inFile = new File(files[p2]);
					if (inFile.isDirectory()) {
						startAlertDialog(files[p2]);
						return;
					}
					Toast.makeText(getApplicationContext(), "Choose: " + files[p2], Toast.LENGTH_LONG).show();
					String path = files[p2];
					Uri uri = Uri.parse("file://" + path);
					int latest_mid = MyDB.get(Chat.this).getLatestMid(gid_int);
					ItemBeanChat message = new ItemBeanChat(latest_mid, gid_int, Config.get(Chat.this).data.user.username, new MyGetTime().local(), 
														   path.substring(path.lastIndexOf("/") + 1, path.length()), 
														   Config.get(Chat.this).data.user.head, "file", ItemBeanChat.PRESEND).setSendTime(new MyGetTime().getInt())
						.setTag(path);
					new MyDB(Chat.this).saveMessage(message);
					ContentResolver cr = Chat.this.getContentResolver();
					String b64 = null;
					try {
						InputStream is = cr.openInputStream(uri);
						byte[] buf = new byte[is.available()];
						is.read(buf);
						b64 = Base64.encodeToString(buf, Base64.DEFAULT);
					} catch (Exception e) {
						Log.e("Chat 2", e.getMessage());
						Toast.makeText(Chat.this, e.getMessage(), Toast.LENGTH_LONG).show();
						return;
					}

					ContentValues params = new ContentValues();
					params.put("auth", Config.get(Chat.this).data.user.auth);
					params.put("data", b64);
					params.put("filename", path.substring(path.lastIndexOf("/") + 1, path.length()));
					UploadTask task = Communication.getComm(Chat.this).uploadNoListener(Communication.UPLOAD, message.tag, params);
					task
						//.extra1(new SendMessage.MessageToSend().toJson())
						.save().start();
					//List<ItemBeanChat> tmp = new ArrayList<>();
					//tmp.add(message);
					//refreshNowBean(tmp);
					
				}
			})
			.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);

		final Bundle bundle = getIntent().getExtras();
		gid_int = bundle.getInt("gid");
		gid = "" + gid_int;
		
		setContentView(R.layout.chat);
		
		RxPermissions rxPermissions = new RxPermissions(Chat.this);
		rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
			.subscribe(new Observer<Boolean>() {
				@Override
				public void onError(Throwable p1)
				{Chat.this.finish();}
				@Override
				public void onComplete()
				{}
				@Override
				public void onSubscribe(Disposable d)
				{}
				@Override
				public void onNext(Boolean aBoolean) {
					try {
						int willPrint = bundle.getInt("print");
						if (willPrint == 1) {
							Matisse.from(Chat.this)
								.choose(MimeType.ofImage(), false)
								.countable(false)
								.capture(true)
								.captureStrategy(new CaptureStrategy(true, "lance.liang.group.latina.provider"))
								.maxSelectable(1)
								.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
								.thumbnailScale(0.85f)
								//.originalEnable(true)
								//.maxOriginalSize(10)
								.autoHideToolbarOnSingleTap(true)
								.forResult(code_pick);
						}
					} catch (Exception e) {}

				}});
		
		bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setTitle((CharSequence)bundle.getString("name"));

		text_message = (EditText)findViewById(R.id.chatEditText);
		btn_send = (Button)findViewById(R.id.chatButton_send);
		btn_more = (Button)findViewById(R.id.chatButton_more);
		list_message = (ListView)findViewById(R.id.chatListView);
		srl = (SwipeRefreshLayout)findViewById(R.id.chatSwipeRefreshLayout);
		
		//btn_send.setBackgroundColor(Config.get(this).data.settings.colorFt);
		//btn_more.setBackgroundResource(Config.get(this).data.settings.colorFt);
		
		/*
		RxPermissions rxPermissions = new RxPermissions(this);
		rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
			.subscribe(new Observer<Boolean>() {
				@Override
				public void onSubscribe(Disposable p1) {}
				@Override
				public void onNext(Boolean p1) {}
				@Override
				public void onError(Throwable p1) {
					Chat.this.finish();
				}
				@Override
				public void onComplete() {}
			});
		*/

		adp = new ChatAdapter(this, data);
		list_message.setAdapter(adp);
		
		list_message.setDividerHeight(0);

		//refresh();
		//getMessage();
		initMessages();
		
		//srl.setColorSchemeResources(android.R.color.holo_blue_dark);
		srl.setColorSchemeResources(Config.get(this).data.settings.colorFt);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				@Override
				public void onRefresh()
				{
					Toast.makeText(Chat.this, "正在刷新...", Toast.LENGTH_SHORT).show();
					adp.insert(new ItemBeanChat(0, gid_int, "Lance", "12:00", "Refreshed.", 
												"https://s.gravatar.com/avatar/cb135b9ed779f242373ab3a8db99f25a?s=144", "text"));
					adp.notifyDataSetChanged();
					//adp.notifyDataSetInvalidated();
					srl.setRefreshing(false);
				}
			});

		btn_send.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					final String text = text_message.getText().toString();
					ContentValues parames = new ContentValues();
					parames.put("auth", Config.get(Chat.this).data.user.auth);
					parames.put("message_type", "text");
					parames.put("text", text);
					parames.put("gid", gid);
					AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
					builder.setMessage("发送中...");
					builder.setCancelable(false);
					dialog = builder.create();
					dialog.show();
					Communication.getComm(Chat.this).post(Communication.SEND_MESSAGE, parames, callback_text);
				}
			});

		timer = new Timer();
		timer.schedule(new TimerTask() {
				@Override
				public void run()
				{
					getMessage();
					//refresh();
				}
			}, 0, 3000);

		btn_more.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
					builder.setItems(new String[] {"图片", "文件"}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								if (p2 == 0)
								{ // image
									RxPermissions rxPermissions = new RxPermissions(Chat.this);
									rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
										.subscribe(new Observer<Boolean>() {
											@Override
											public void onError(Throwable p1)
											{}
											@Override
											public void onComplete()
											{}
											@Override
											public void onSubscribe(Disposable d)
											{}
											@Override
											public void onNext(Boolean aBoolean)
											{
												if (aBoolean)
												{
													Matisse.from(Chat.this)
														.choose(MimeType.ofImage(), false)
														.countable(false)
														.capture(true)
														.captureStrategy(new CaptureStrategy(true, "lance.liang.group.latina.provider"))
														//.captureStrategy(new CaptureStrategy(true,  Constants.))
														.maxSelectable(1)
														.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
														.thumbnailScale(0.85f)
														//.originalEnable(true)
														//.maxOriginalSize(10)
														.autoHideToolbarOnSingleTap(true)
														.forResult(code_pick);
												}
											}});
								}
								else if (p2 == 1)
								{ // file
									//Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
									//intent.setType("*/*");
									//intent.addCategory(Intent.CATEGORY_OPENABLE);
									//Chat.this.startActivityForResult(intent, code_file);
									RxPermissions rxPermissions = new RxPermissions(Chat.this);
									rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
										.subscribe(new Observer<Boolean>() {
											@Override
											public void onError(Throwable p1)
											{}
											@Override
											public void onComplete()
											{}
											@Override
											public void onSubscribe(Disposable d)
											{}
											@Override
											public void onNext(Boolean aBoolean)
											{ if (aBoolean) startAlertDialog(baseDir); }});
								}
							}
						});
					builder.show();
				}
			});
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode)
		{
			case code_pick_room_head:
				if (resultCode != RESULT_OK)
					break;
				String head_path = Matisse.obtainPathResult(data).get(0);
				Uri uri_head = Uri.parse("file://" +  head_path);
				ContentResolver crh = this.getContentResolver();  
				InputStream ish = null;
				byte[] bufh = null;
				try {
					ish = crh.openInputStream(uri_head);
					bufh = new byte[ish.available()];
					ish.read(bufh);
				}
				catch (Exception e) { break; }
				String b64_head = Base64.encodeToString(bufh, Base64.DEFAULT);

				ContentValues params2 = new ContentValues();
				params2.put("auth", Config.get(Chat.this).data.user.auth);
				params2.put("data", b64_head);
				params2.put("filename", uri_head.getLastPathSegment());
				Communication.getComm(Chat.this).post(Communication.UPLOAD, params2, 
					new StringCallback() {
						@Override
						public void onSuccess(Response<String> p1) {
							if (p1.code() != 200)
								return;
							final ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
							if (result.code == 0) {
								Communication.getComm(getApplicationContext()).postWithAuth(Communication.SET_ROOM_INFO, Utils.ContentPut(Utils.ContentPut("gid", gid),
									"head", result.data.upload_result.url), 
									new StringCallback() {
										@Override
										public void onSuccess(Response<String> p1) {
											ResultData rd = new Gson().fromJson(p1.body(), ResultData.class);
											Toast.makeText(getApplicationContext(), rd.message, Toast.LENGTH_SHORT);
										}
									});
							}
						}
					});
				break;
			case code_pick:
				if (resultCode == RESULT_OK)
				{
					//for (String s: Matisse.obtainPathResult(data))
					//	Toast.makeText(this, s, Toast.LENGTH_LONG).show();
					//final String choose = Matisse.obtainPathResult(data).get(0);
					
					//for (final Uri choose: Matisse.obtainResult(data))
					for (final String choose_path: Matisse.obtainPathResult(data))
					{
						final Uri choose = Uri.parse("file://" + choose_path);
						ContentResolver cr2 = this.getContentResolver();  
						InputStream is = null;
						byte[] buf2 = null;
						try
						{
							is = cr2.openInputStream(choose);
							buf2 = new byte[is.available()];
							is.read(buf2);
						}
						catch (Exception e)
						{
							break;
						}
						//String md5 = MD5Utils.byteArrayToHexString(buf2);
						String b64_image = Base64.encodeToString(buf2, Base64.DEFAULT);
						
						ContentValues parames = new ContentValues();
						parames.put("auth", Config.get(Chat.this).data.user.auth);
						parames.put("data", b64_image);
						parames.put("filename", choose.getLastPathSegment());
						//parames.put("md5", md5);
						Communication.getComm(getApplicationContext()).upload(Communication.UPLOAD, choose_path, parames, "image", 
							new UploadListener<String>(choose_path) {
								@Override
								public void onStart(Progress p1){
									Toast.makeText(Chat.this, choose_path + " 开始上传", Toast.LENGTH_LONG).show();
								}
								@Override
								public void onProgress(Progress p1){}
								@Override
								public void onError(Progress p1){}
								@Override
								public void onFinish(String p1, Progress p2){
									Toast.makeText(Chat.this, choose_path + " 上传完成", Toast.LENGTH_LONG).show();
									final ResultData result = new Gson().fromJson(p1, ResultData.class);
									if (result.code == 0) {
										ContentValues parames = new ContentValues();
										parames.put("auth", Config.get(Chat.this).data.user.auth);
										//parames.put("text", result.data.pre_upload.url + 
										//			Config.get(getApplicationContext()).data.user.username + "/" + choose.getLastPathSegment());
										parames.put("text", result.data.upload_result.url);
										parames.put("message_type", "image");
										parames.put("gid", gid);
										AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
										builder.setMessage("正在发送");
										builder.setCancelable(false);
										dialog = builder.create();
										dialog.show();
										Communication.getComm(getApplicationContext()).post(Communication.SEND_MESSAGE, parames, 
											new StringCallback() {
												public void onStart(Response<String> p1)
												{}
												@Override
												public void onError(Response<String> p1)
												{
													dialog.hide();
													AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
													builder.setMessage("网络错误. (Code: " + p1.code() + ")");
													builder.show();
												}
												@Override
												public void onSuccess(Response<String> p1)
												{
													dialog.hide();
													ResultData result2 = (new Gson()).fromJson(p1.body(), ResultData.class);
													if (result2.code == 0)
													{
														//adp.insert(new ItemBeanChat(0, gid_int, Config.get(Chat.this).data.user.username, new MyGetTime().local(), 
														//							result.data.upload_result.url, Config.get(Chat.this).data.user.head, "image"));
														//adp.notifyDataSetChanged();
													}
													else
													{
														AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
														builder.setMessage(result2.message + " (错误码: " + result2.code + ")");
														builder.show();
													}
												}
											});
									}
								}
								@Override
								public void onRemove(Progress p1) {
									Toast.makeText(Chat.this, choose_path + " 任务移除", Toast.LENGTH_LONG).show();
								}
							}).start();
						}
				}
				break;
			default:
				break;
		}
	}

	public void refresh()
	{
		List<MessageData> messages = MyDB.get(this).getNewMessages(gid_int);
		for (MessageData m: messages) {
			adp.insert(new ItemBeanChat(m));
		}
		if (messages.size() > 0)
			adp.notifyDataSetChanged();
	}
	public void refreshNow(List<MessageData> messages)
	{
		for (MessageData m: messages) {
			adp.insert(new ItemBeanChat(m));
		}
		if (messages.size() > 0)
			adp.notifyDataSetChanged();
	}
	public void refreshNowBean(List<ItemBeanChat> messages)
	{
		for (ItemBeanChat m: messages) {
			adp.insert(m);
		}
		if (messages.size() > 0)
			adp.notifyDataSetChanged();
	}
	
	public void initMessages() {
		//TODO: bugs!!
		List<MessageData> messages = MyDB.get(this).getMessages(gid_int, 65535, 0);
		adp.list.clear();
		for (MessageData m: messages) {
			adp.insert(new ItemBeanChat(m));
		}
		adp.notifyDataSetChanged();
	}
	
	void getMessage() {
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(Chat.this).data.user.auth);
		parames.put("gid", gid);
		parames.put("since", new MyDB(Chat.this).getLatestMid(gid_int));
		parames.put("limit", "30");
		try
		{
			Communication.getComm(Chat.this).post(Communication.GET_MESSAGES, parames,  
				new StringCallback() {
					@Override
					public void onSuccess(Response<String> p1)
					{
						ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
						if (result.code == 0)
						{
							//adp.list.clear();
							//for (MessageData message: result.data.message)
							//{
								//adp.insert_back(new ItemBeanChat(message.mid, message.username, new MyGetTime().remote(message.send_time), 
								//								 message.text, message.head, message.type));
							//}
							//adp.notifyDataSetChanged();
							Collections.sort(result.data.message, new Comparator<MessageData>() {
									@Override
									public int compare(MessageData p1, MessageData p2) {
										return ("" + p1.mid).compareTo("" + p2.mid);
									}
								});
							List<MessageData> tmp = new ArrayList<>();
							int max_mid = 0;
							for (MessageData m: result.data.message)
							{
								if (m.type.equals("file")) {
									m.tag = m.text;
									m.text = m.text.substring(m.text.lastIndexOf("/") + 1, m.text.length());
								}
								tmp.add(m);
								max_mid = Utils.max(max_mid, m.mid);
							}
							MyDB.get(Chat.this).saveMessage(tmp);
							refreshNow(tmp);
							if (max_mid != 0) {
								Config config = Config.get(getApplicationContext());
								config.data.settings.unreadMid = max_mid;
								config.save();
							}
						}
					}
				});
		}
		catch (Exception e)
		{
			Log.e("Chat 2", e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				this.finish();
				break;
			case R.id.option_room_info:
				ContentValues params = new ContentValues();
				params.put("auth", Config.get(this).data.user.auth);
				params.put("gid", gid);
				Communication.getComm(this).post(Communication.GET_ROOM_INFO, params, 
					new StringCallback() {
						@Override
						public void onSuccess(Response<String> p1) {
							final ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
							if (result.code == 0) {
								List<String> items = new ArrayList<String>();
								items.add("GID号: " + result.data.info.gid);
								items.add("房间名字: " + result.data.info.name);
								items.add("头像: " + result.data.info.head);
								items.add("上次活跃时间: " + new MyGetTime().remote(result.data.info.last_post_time));
								items.add("创建于: " + new MyGetTime().remote(result.data.info.create_time));
								items.add("房间类型: " + result.data.info.room_type);
								if (result.data.info.room_type.equals("public"))
									items.add("房间成员: " + result.data.info.member_number);
								
								new AlertDialog.Builder(Chat.this)
									.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface p1, int p2) {
											if (result.data.info.room_type.equals("printer") || result.data.info.room_type.equals("private")) { return; }
											switch (p2) {
												case 1:
													final EditText room_name = new EditText(Chat.this);
													new AlertDialog.Builder(Chat.this).setTitle("设置房间名字:")
													.setView(room_name)
														.setPositiveButton("确定", new DialogInterface.OnClickListener() {
															@Override
															public void onClick(DialogInterface p1, int p2) {
																Communication.getComm(getApplicationContext()).postWithAuth(Communication.SET_ROOM_INFO, 
																	Utils.ContentPut(Utils.ContentPut("gid", gid), "name", room_name.getText().toString()), 
																	new StringCallback() {
																		@Override
																		public void onSuccess(Response<String> p1) {
																			ResultData rd = new Gson().fromJson(p1.body(), ResultData.class);
																			Toast.makeText(Chat.this, rd.message, Toast.LENGTH_SHORT);
																		}
																	});
															}
														})
													.setNegativeButton("取消", null)
													.show();
													break;
												case 2:
													Matisse.from(Chat.this)
														.choose(MimeType.ofImage(), false)
														.countable(false)
														.capture(true)
														.captureStrategy(new CaptureStrategy(true, "lance.liang.group.latina.provider"))
														.maxSelectable(1)
														.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
														.thumbnailScale(0.85f)
														//.originalEnable(true)
														//.maxOriginalSize(10)
														.autoHideToolbarOnSingleTap(true)
														.forResult(code_pick_room_head);
													break;
												default:
													break;
											}
										}
									})
									.show();
							}
						}
					});
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}
	
	public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            //Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Files.FileColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}


