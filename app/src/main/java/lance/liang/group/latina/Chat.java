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
	private final int code_pick = 0x91, code_file = 0x92;
	
	private StringCallback callback_text = new StringCallback() {
		public void onStart(Response<String> p1)
		{}
		@Override
		public void onError(Response<String> p1)
		{
			dialog.hide();
			AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
			builder.setMessage("Error. (Code: " + p1.code() + ")");
			builder.show();
		}
		@Override
		public void onSuccess(Response<String> p1)
		{
			dialog.hide();
			ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
			if (result.code == 0)
			{
				getMessage();
				text_message.setText("");
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
				builder.setMessage(result.message + " (Code: " + result.code + ")");
				builder.show();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		gid_int = bundle.getInt("gid");
		gid = "" + gid_int;

		setContentView(R.layout.chat);
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

		adp = new ChatAdapter(this, data);
		list_message.setAdapter(adp);

		//refresh();
		//getMessage();
		initMessages();
		
		//srl.setColorSchemeResources(android.R.color.holo_blue_dark);
		srl.setColorSchemeResources(Config.get(this).data.settings.colorFt);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				@Override
				public void onRefresh()
				{
					Toast.makeText(Chat.this, "Refreshing...", Toast.LENGTH_SHORT).show();
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
					builder.setMessage("Sending");
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
					builder.setItems(new String[] {"Image", "File"}, new DialogInterface.OnClickListener() {
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
														.capture(false)
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
									Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
									intent.setType("*/*");
									intent.addCategory(Intent.CATEGORY_OPENABLE);
									Chat.this.startActivityForResult(intent, code_file);
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
			case code_file:
				if (resultCode != RESULT_OK)
					break;
				/*
				//String str = URLDecoder.decode(data.getData().toString());
				//String path = Uri.parse().
				Uri uri = data.getData();
				ContentResolver cr = this.getContentResolver();  
				//String str = cr.openInputStream().;
				Toast.makeText(Chat.this, uri.toString(), Toast.LENGTH_LONG).show();
				//File file = new File(uri);
				try {
					//BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
					InputStream is = cr.openInputStream(uri);
					byte[] buf = new byte[is.available()];
					is.read(buf);
					//String md5 = MD5Utils.byteArrayToHexString(buf);
					String b64 = Base64.encodeToString(buf, Base64.DEFAULT);
					
					ContentValues parames = new ContentValues();
					parames.put("auth", Config.get(Chat.this).data.user.auth);
					parames.put("data", b64);
					parames.put("filename", uri.getLastPathSegment());
					
					//parames.put("md5", md5);
					Communication.getComm(Chat.this).post(Communication.UPLOAD, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> p1) {
								if (p1.code() != 200)
									return;
								final ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
								if (result.code == 0) {
									ContentValues parames = new ContentValues();
									parames.put("auth", Config.get(Chat.this).data.user.auth);
									parames.put("text", result.data.upload_result.url);
									parames.put("message_type", "file");
									parames.put("gid", gid);
									AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
									builder.setMessage("Sending");
									builder.setCancelable(false);
									dialog = builder.create();
									dialog.show();
									Communication.getComm(Chat.this).post(Communication.SEND_MESSAGE, parames, 
										new StringCallback() {
											public void onStart(Response<String> p1)
											{}
											@Override
											public void onError(Response<String> p1)
											{
												dialog.hide();
												AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
												builder.setMessage("Error. (Code: " + p1.code() + ")");
												builder.show();
											}
											@Override
											public void onSuccess(Response<String> p1)
											{
												dialog.hide();
												ResultData result2 = (new Gson()).fromJson(p1.body(), ResultData.class);
												if (result2.code == 0)
												{
													adp.insert(new ItemBeanChat(0, Config.get(Chat.this).data.user.username, new MyGetTime().local(), 
																				result.data.upload_result.url, Config.get(Chat.this).data.user.head, "file"));
													adp.notifyDataSetChanged();
												}
												else
												{
													AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
													builder.setMessage(result2.message + " (Code: " + result2.code + ")");
													builder.show();
												}
											}
										});
								}
							}
						});
				}
				catch (Exception e) {
					Toast.makeText(Chat.this, e.getMessage(), Toast.LENGTH_LONG).show();
				}*/
				
				Uri uri = data.getData();
				String path = getRealFilePath(Chat.this, uri);
				Toast.makeText(Chat.this, path, Toast.LENGTH_LONG).show();
				//File file = new File(uri);
				int latest_mid = MyDB.get(Chat.this).getLatestMid(gid_int);
				ItemBeanChat message =new ItemBeanChat(latest_mid, gid_int, Config.get(Chat.this).data.user.username, new MyGetTime().local(), 
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
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
					break;
				}

				ContentValues params = new ContentValues();
				params.put("auth", Config.get(Chat.this).data.user.auth);
				params.put("data", b64);
				params.put("filename", path.substring(path.lastIndexOf("/") + 1, path.length()));
				UploadTask task = Communication.getComm(Chat.this).uploadNoListener(Communication.UPLOAD, message.tag, params);
				task.save();
				task.start();
				List<ItemBeanChat> tmp = new ArrayList<>();
				tmp.add(message);
				refreshNowBean(tmp);
				break;
			case code_pick:
				if (resultCode == RESULT_OK)
				{
					//for (String s: Matisse.obtainPathResult(data))
					//	Toast.makeText(this, s, Toast.LENGTH_LONG).show();
					//final String choose = Matisse.obtainPathResult(data).get(0);
					for (final Uri choose: Matisse.obtainResult(data))
					{
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
						Communication.getComm(Chat.this).post(Communication.UPLOAD, parames, 
							new StringCallback() {
								@Override
								public void onSuccess(Response<String> p1) {
									if (p1.code() != 200)
										return;
									final ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
									if (result.code == 0) {
										ContentValues parames = new ContentValues();
										parames.put("auth", Config.get(Chat.this).data.user.auth);
										parames.put("text", result.data.upload_result.url);
										parames.put("message_type", "image");
										parames.put("gid", gid);
										AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
										builder.setMessage("Sending");
										builder.setCancelable(false);
										dialog = builder.create();
										dialog.show();
										Communication.getComm(Chat.this).post(Communication.SEND_MESSAGE, parames, 
											new StringCallback() {
												public void onStart(Response<String> p1)
												{}
												@Override
												public void onError(Response<String> p1)
												{
													dialog.hide();
													AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
													builder.setMessage("Error. (Code: " + p1.code() + ")");
													builder.show();
												}
												@Override
												public void onSuccess(Response<String> p1)
												{
													dialog.hide();
													ResultData result2 = (new Gson()).fromJson(p1.body(), ResultData.class);
													if (result2.code == 0)
													{
														adp.insert(new ItemBeanChat(0, gid_int, Config.get(Chat.this).data.user.username, new MyGetTime().local(), 
																					result.data.upload_result.url, Config.get(Chat.this).data.user.head, "image"));
														adp.notifyDataSetChanged();
													}
													else
													{
														AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
														builder.setMessage(result2.message + " (Code: " + result2.code + ")");
														builder.show();
													}
												}
											});
									}
								}
							});
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
		List<MessageData> messages = MyDB.get(this).getMessages(gid_int, 30, 0);
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
			Communication.getComm(Chat.this).post(Communication.GET_NEW_MESSAGE, parames,  
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
							List<MessageData> tmp = new ArrayList<>();
							for (MessageData m: result.data.message)
							{
								if (m.type.equals("file"))
									m.tag = m.text;
								tmp.add(m);
							}
							MyDB.get(Chat.this).saveMessage(tmp);
							refreshNow(tmp);
						}
						else
						{

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
							ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
							if (result.code == 0) {
								List<String> items = new ArrayList<String>();
								items.add("GID: " + result.data.info.gid);
								items.add("Name: " + result.data.info.name);
								items.add("Number of members: " + result.data.info.member_number);
								items.add("Active time: " + new MyGetTime().remote(result.data.info.last_post_time));
								items.add("Create at: " + new MyGetTime().remote(result.data.info.create_time));
								items.add("Head: " + result.data.info.head);
								
								new AlertDialog.Builder(Chat.this)
									.setItems(items.toArray(new String[items.size()]), null)
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
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
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


