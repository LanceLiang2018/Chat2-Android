package lance.liang.chat2;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
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
import com.zhihu.matisse.listener.*;
import io.reactivex.annotations.*;
import io.reactivex.disposables.*;
import java.text.*;
import java.util.*;
import lance.liang.chat2.*;
//import android.support.v7.app.*;
//import lance.liang.chat2.
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
	private final int code_pick = 0x91;

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
								ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
								if (result.code == 0)
								{
									String date = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
									adp.insert(new ItemBeanChat(0, Config.get(Chat.this).data.user.username, date, 
																text, Config.get(Chat.this).data.user.head, "text"));
									adp.notifyDataSetChanged();
									text_message.setText("");

								}
								else
								{
									AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
									builder.setMessage(result.message + " (Code: " + result.code + ")");
									builder.show();
								}
							}
						});
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
														.countable(true)
														.capture(false)
														.maxSelectable(9)
														.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
														.thumbnailScale(0.85f)
														.originalEnable(true)
														.maxOriginalSize(10)
														.autoHideToolbarOnSingleTap(true)
														.forResult(code_pick);
												}
											}});
								}
								else if (p2 == 1)
								{ // file

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
			case code_pick:
				if (resultCode == RESULT_OK)
				{
					//for (String s: Matisse.obtainPathResult(data))
					//	Toast.makeText(this, s, Toast.LENGTH_LONG).show();
					//final String choose = Matisse.obtainPathResult(data).get(0);
					for (final String choose: Matisse.obtainPathResult(data))
					{
						ContentValues parames = new ContentValues();
						parames.put("auth", Config.get(Chat.this).data.user.auth);
						parames.put("text", choose);
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
									ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
									if (result.code == 0)
									{
										String date = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
										adp.insert(new ItemBeanChat(0, Config.get(Chat.this).data.user.username, date, 
																	choose, Config.get(Chat.this).data.user.head, "image"));
										adp.notifyDataSetChanged();
									}
									else
									{
										AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
										builder.setMessage(result.message + " (Code: " + result.code + ")");
										builder.show();
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
								Long stime = Long.parseLong(message.send_time) * 1000;
								String date = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date(stime));
								adp.insert_back(new ItemBeanChat(message.mid, message.username, date, 
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
