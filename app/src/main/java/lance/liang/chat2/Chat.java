package lance.liang.chat2;

import android.support.v7.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.view.View.*;
import android.support.v4.*;
//import android.support.v13.*;
import android.support.v4.widget.*;
import org.json.*;
import android.content.*;
import android.util.*;
import android.content.res.Resources.*;
import android.graphics.*;
import android.graphics.drawable.*;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.google.gson.*;
import java.text.*;
//import android.support.v7.app.*;
//import lance.liang.chat2.

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
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).settings.theme);
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		gid = bundle.getString("gid");
		
		try {
			setContentView(R.layout.chat);
			
//			Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//			toolbar.setTitle((CharSequence)bundle.getString("name"));
//			toolbar.setBackgroundColor(Color.parseColor("#FFC03546"));
//			
		}
		catch (Exception e) {
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
		
		srl.setColorSchemeResources(android.R.color.holo_blue_dark);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				@Override
				public void onRefresh()
				{
					Toast.makeText(Chat.this, "Refreshing...", Toast.LENGTH_SHORT).show();
					adp.insert_back(new ItemBeanChat(0, "Lance", "12:00", "Refreshed.", 
													 "https://s.gravatar.com/avatar/cb135b9ed779f242373ab3a8db99f25a?s=144"));
					adp.notifyDataSetChanged();
					//adp.notifyDataSetInvalidated();
					srl.setRefreshing(false);
				}
			});
		
		btn_send.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					final String text = text_message.getText().toString();
					ContentValues parames = new ContentValues();
					parames.put("auth", Config.get(Chat.this).user.auth);
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
							public void onStart(Response<String> p1) {
								
							}
							@Override
							public void onError(Response<String> p1) {
								dialog.hide();
								AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
								builder.setMessage("Error. (Code: " + p1.code() + ")");
								builder.show();
							}
							@Override
							public void onSuccess(Response<String> p1) {
								dialog.hide();
								ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
								if (result.code == 0) {
									String date = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
									adp.insert(new ItemBeanChat(0, Config.get(Chat.this).user.username, date, 
																text, Config.get(Chat.this).user.head));
									adp.notifyDataSetChanged();
									text_message.setText("");
									
								} else {
									AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
									builder.setMessage(result.message+ " (Code: " + result.code + ")");
									builder.show();
								}
							}
						});
				}
		});
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Refresh();
				}
		}, 0, 5000);
	}
	
	public void Refresh()
	{
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).user.auth);
		parames.put("gid", gid);
		parames.put("limit", "30");
		try {
			Communication.getComm(this).post(Communication.GET_MESSAGE, parames,  
				new StringCallback() {
					@Override
					public void onSuccess(Response<String> p1)
					{
						ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
						if (result.code == 0) {
							adp.list.clear();
							for (ResultData.Data.Message message: result.data.message) {
								Long stime = Long.parseLong(message.send_time) * 1000;
								String date = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date(stime));
								adp.insert_back(new ItemBeanChat(0, message.username, date, 
									message.text, message.head));
							}
							adp.notifyDataSetChanged();
						} else {
							
						}
					}
				});
		}
		catch (Exception e) {
			Log.e("Chat 2", e.getMessage());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
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
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
