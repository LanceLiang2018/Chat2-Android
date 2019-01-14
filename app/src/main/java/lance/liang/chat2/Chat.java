package lance.liang.chat2;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.tbruyelle.rxpermissions2.*;
import com.zhihu.matisse.*;
import io.reactivex.*;
import io.reactivex.disposables.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import io.reactivex.Observer;
import org.apache.http.impl.auth.*;

import java.security.MessageDigest;
import android.net.*;
import android.service.chooser.*;
/*
class MD5Utils
{ private static final String hexDigIts[] = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
	public static String MD5Encode(String origin, String charsetname)
	{ String resultString = null; try
		{ resultString = new String(origin); MessageDigest md = MessageDigest.getInstance("MD5"); if (null == charsetname || "".equals(charsetname))
			{ resultString = byteArrayToHexString(md.digest(resultString.getBytes())); }
			else
			{ resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname))); } }
		catch (Exception e)
		{ } return resultString; }
	public static String byteArrayToHexString(byte b[])
	{ StringBuffer resultSb = new StringBuffer(); for (int i = 0; i < b.length; i++)
		{ resultSb.append(byteToHexString(b[i])); } return resultSb.toString(); }
	public static String byteToHexString(byte b)
	{ int n = b; if (n < 0)
		{ n += 256; }
	int d1 = n / 16; int d2 = n % 16; return hexDigIts[d1] + hexDigIts[d2]; } }
*/
public class Chat extends AppCompatActivity
{
	private EditText text_message;
	private Button btn_send, btn_more;
	private ListView list_message;
	private ChatAdapter adp;
	private List<ItemBeanChat> data = new ArrayList<>();
	private SwipeRefreshLayout srl;
	private String gid = "0";
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
				adp.insert(new ItemBeanChat(0, Config.get(Chat.this).data.user.username, new MyGetTime().local(), 
											text_message.getText().toString(), Config.get(Chat.this).data.user.head, "text"));
				adp.notifyDataSetChanged();
				text_message.setText("");
				//text_message.setText("");
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
		gid = bundle.getString("gid");

		try
		{
			setContentView(R.layout.chat);

//			Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//			toolbar.setTitle((CharSequence)bundle.getString("name"));
//			toolbar.setBackgroundColor(Color.parseColor("#FFC03546"));
//			
		}
		catch (Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

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

		Refresh();

		//srl.setColorSchemeResources(android.R.color.holo_blue_dark);
		srl.setColorSchemeResources(Config.get(this).data.settings.colorFt);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				@Override
				public void onRefresh()
				{
					Toast.makeText(Chat.this, "Refreshing...", Toast.LENGTH_SHORT).show();
					adp.insert_back(new ItemBeanChat(0, "Lance", "12:00", "Refreshed.", 
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
					Refresh();
				}
			}, 0, 5000);

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
									/*
									EditText edit = new EditText(Chat.this);
									edit.setText(result.data.url);
									new AlertDialog.Builder(Chat.this).setView(edit).show();*/
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
				}
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
						String b64 = Base64.encodeToString(buf2, Base64.DEFAULT);
						
						ContentValues parames = new ContentValues();
						parames.put("auth", Config.get(Chat.this).data.user.auth);
						parames.put("data", b64);
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
														adp.insert(new ItemBeanChat(0, Config.get(Chat.this).data.user.username, new MyGetTime().local(), 
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

	public void Refresh()
	{
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(Chat.this).data.user.auth);
		parames.put("gid", gid);
		parames.put("limit", "30");
		try
		{
			Communication.getComm(Chat.this).post(Communication.GET_MESSAGE, parames,  
				new StringCallback() {
					@Override
					public void onSuccess(Response<String> p1)
					{
						ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
						if (result.code == 0)
						{
							adp.list.clear();
							for (ResultData.Data.Message message: result.data.message)
							{
								adp.insert_back(new ItemBeanChat(message.mid, message.username, new MyGetTime().remote(message.send_time), 
																 message.text, message.head, message.type));
							}
							adp.notifyDataSetChanged();
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
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				this.finish();
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
}
