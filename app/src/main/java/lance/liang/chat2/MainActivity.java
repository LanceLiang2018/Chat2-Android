package lance.liang.chat2;

import android.content.*;
import android.os.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.lzy.okgo.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import java.util.*;
import lance.liang.chat2.*;
import com.google.gson.*;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout srl;
	private ListView list;
	private ListView left;
	private MainAdapter adp;
	List<ItemBeanMain> data = new ArrayList<ItemBeanMain>();
	private EditText edit;
	
	public static int code_login = 0x80, code_signup = 0x81, code_chat = 0x82;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
		Log.d("Chat 2:: main", "Started.");
		setTheme(Config.get(this).settings.theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		list = (ListView)findViewById(R.id.list_rooms);
		left = (ListView)findViewById(R.id.list_left);
		
//		for (int i=1; i <= 100; i++)
//			data.add(new ItemBeanMain(0, R.mipmap.ic_launcher, "Title " + i, "Content" + i));
		
		adp = new MainAdapter(this, data);
		
		Refresh();
		
		list.setAdapter(adp);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
					Intent intent_room = new Intent();
					intent_room.setClass(MainActivity.this, Chat.class);
					Bundle bundle=new Bundle();
					bundle.putString("name", data.get(p3).title);
					bundle.putInt("gid", data.get(p3).gid);
					intent_room.putExtras(bundle);
					startActivityForResult(intent_room, code_chat);
				}
			});

		srl = (SwipeRefreshLayout)findViewById(R.id.slr);
		srl.setEnabled(true);
		
		List<ItemBeanLeft> left_data = new ArrayList<ItemBeanLeft>();
		for (int i=1; i<=20; i++)
			left_data.add(new ItemBeanLeft(R.drawable.image_1, "Title"));
		
		left.setAdapter(new LeftAdapter(this, left_data));
		
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).user.auth);
		Communication.getComm(this).post(Communication.BEAT, parames, 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> response) {
					ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
					if (result.code != 0)
					{
						Toast.makeText(MainActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
						Intent intent_login = new Intent();
						intent_login.setClass(MainActivity.this, Login.class);
						startActivityForResult(intent_login, code_login);
					}
				}
			});
	}
	
	public void Refresh()
	{
		adp.list.clear();
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).user.auth);
		Communication.getComm(this).post(Communication.GET_ROOMS, parames, 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> response) {
					ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
					if (result.code == 0) {
						for (ResultData.Data.RoomData room_data: result.data.room_data) {
							adp.insert(new ItemBeanMain(room_data.gid, R.mipmap.ic_launcher, room_data.name, "Content"));
							adp.notifyDataSetChanged();
						}
					}
				}
			});
	}

	/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode)
		{
			case R.layout.login:
				try {
					String str = data.getStringExtra("command");
					if (str == "Refresh") {
						this.recreate();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}
	*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.option_exit:
				this.finish();
				break;
			case R.id.option_about:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("关于");
				builder.setMessage(R.string.app_name);
				builder.show();
				break;
			case R.id.option_login:
				Intent intent_login = new Intent();
				intent_login.setClass(MainActivity.this, Login.class);
				startActivityForResult(intent_login, code_login);
				break;
			case R.id.option_signup:
				Intent intent_signup = new Intent();
				intent_signup.setClass(MainActivity.this, Signup.class);
				startActivityForResult(intent_signup, code_signup);
				break;
			case R.id.option_new_room:
				edit = new EditText(this);
				AlertDialog.Builder build = new AlertDialog.Builder(this);
				build.setTitle("Input the name of Room:");
				build.setView(edit);
				build.setPositiveButton("OK", new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface p1, int p2) {
							ContentValues parames = new ContentValues();
							parames.put("auth", Config.get(MainActivity.this).user.auth);
							parames.put("name", edit.getText().toString());
							Communication.getComm(MainActivity.this).post(Communication.CREATE_ROOM, parames, 
								new StringCallback() {
									@Override
									public void onSuccess(Response<String> response) {
										ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
										if (result.code == 0) {
											Log.d("Chat 2", "Create Room: name:" + result.data.info.name + " gid: " + result.data.info.gid);
										}
										else {
											Log.e("Chat 2", result.message + "(Code: " + result.code + ")");
										}
									}
								});
						}
					});
				build.setNegativeButton("Cancel", null);
				build.show();
				
				break;
			case R.id.option_settings:
				try {
					Communication.getComm(this).test(this);
				}
				catch (Exception e) {
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
