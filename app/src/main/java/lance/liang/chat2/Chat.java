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
		
		Refresh();
		
		list_message.setAdapter(adp);
		
		srl.setColorSchemeResources(android.R.color.holo_blue_dark);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				@Override
				public void onRefresh()
				{
					Toast.makeText(Chat.this, "Refreshing...", Toast.LENGTH_SHORT).show();
					adp.insert_back(new ItemBeanChat(0, "Lance", "12:00", "Refreshed.", 
													 "https://avatars0.githubusercontent.com/u/41908064?s=460&v=4"));
					adp.notifyDataSetChanged();
					//adp.notifyDataSetInvalidated();
					srl.setRefreshing(false);
				}
			});
	}
	
	public void Refresh()
	{
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).user.auth);
		parames.put("gid", gid);
		parames.put("limit", "30");
		Communication.getComm(this).post(Communication.GET_MESSAGE, parames,
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> response) {
					Toast.makeText(Chat.this, response.body().toString(), Toast.LENGTH_LONG);
//					ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
//					if (result.code == 0) {
//						for (ResultData.Data.Message message: result.data.message) {
//							adp.insert_back(new ItemBeanChat(0, message.username, "" + message.send_time, message.text, message.head));
//							adp.notifyDataSetChanged();
//						}
//					} else {
//						Log.e("Chat 2", result.message + "(Code: " + result.code + ")");
//					}
				}
			});
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
}
