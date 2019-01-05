package lance.liang.chat2;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.graphics.drawable.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.appcompat.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import java.util.*;

import android.support.v7.appcompat.R;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout srl;
	private ListView list;
	private ExpandableListView left;
	private MainAdapter adp;
	List<ItemBeanMain> data = new ArrayList<ItemBeanMain>();
	private EditText edit;
	private TextView text_title;
	private ImageView im_head;
	
	public static final int code_login = 0x80, code_signup = 0x81, code_chat = 0x82;

	private ActionBar bar;
	private DrawerLayout drawer;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("Chat 2", "Started.");
		setTheme(Config.get(this).data.settings.theme);
		//Config.get(this).test();
		
		//Config config = Config.get(this);
		//config.settings.server = "https://lance-chatroom2.herokuapp.com/";
		//config.save();
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		bar = getSupportActionBar();
		//bar.setLogo(R.drawable.image_1);
		//bar.setIcon(R.drawable.image_2);
		//bar.setDisplayHomeAsUpEnabled(true);
		//bar.setDisplayShowHomeEnabled(true);
		//bar.setHomeButtonEnabled(true);
		//bar.setTitle("");
		//bar.setDisplayOptions(bar.DISPLAY_USE_LOGO);
		
		//Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
		//toolbar.setNavigationIcon(R.drawable.image_2);
		//toolbar.setTitle("Lance");
		
		View view = LayoutInflater.from(this).inflate(R.layout.title_main, null);
		im_head = (ImageView)view.findViewById(R.id.titlemainImageButton_head);
		text_title = (TextView)view.findViewById(R.id.titlemainTextView_title);
		
		View.OnClickListener onclick = new OnClickListener() {
			@Override
			public void onClick(View p1) {
				if (drawer.isDrawerOpen(Gravity.LEFT))
					drawer.closeDrawers();
				else
					drawer.openDrawer(Gravity.LEFT);
			}
		};
		
		im_head.setOnClickListener(onclick);
		text_title.setOnClickListener(onclick);
		//Glide.with(this).load(Config.get(MainActivity.this).data.user.head).into(im_head);
		//Glide.with(this).load(R.drawable.image_2).into(im_head);
		RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.image_2));
		roundedBitmapDrawable1.setCircular(true);
		im_head.setImageDrawable(roundedBitmapDrawable1);
		
		bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayHomeAsUpEnabled(false);
		bar.setDisplayShowCustomEnabled(true);
		bar.setCustomView(view);

		list = (ListView)findViewById(R.id.list_rooms);
		left = (ExpandableListView)findViewById(R.id.list_left);
		drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		
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
					bundle.putString("gid", "" + data.get(p3).gid);
					intent_room.putExtras(bundle);
					startActivityForResult(intent_room, code_chat);
				}
			});

		srl = (SwipeRefreshLayout)findViewById(R.id.slr);
		srl.setEnabled(true);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					MainActivity.this.Refresh();
					srl.setRefreshing(false);
				}
		});
		
		left.setAdapter(new LeftAdapter(this));
		
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).data.user.auth);
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
		//Glide.with(this).load(Config.get(this).data.user.head).into(im_head);
		text_title.setText(Config.get(this).data.user.username);
		
		adp.list.clear();
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).data.user.auth);
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
					else {
						if (result.code == 2) {
							Toast.makeText(MainActivity.this, "(Refresh(): ) Login Failed.", Toast.LENGTH_SHORT).show();
							Intent intent_login = new Intent();
							intent_login.setClass(MainActivity.this, Login.class);
							startActivityForResult(intent_login, code_login);
						}
						else {
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setMessage(result.message + " (Code: " + result.code + ")");
						}
					}
				}
			});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode)
		{
			case code_login:
				try {
					String str = data.getStringExtra("command");
					if (str == "Refresh") {
						this.Refresh();
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
							parames.put("auth", Config.get(MainActivity.this).data.user.auth);
							parames.put("name", edit.getText().toString());
							Communication.getComm(MainActivity.this).post(Communication.CREATE_ROOM, parames, 
								new StringCallback() {
									@Override
									public void onSuccess(Response<String> response) {
										ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
										if (result.code == 0) {
											Log.d("Chat 2", "Create Room: name:" + result.data.info.name + " gid: " + result.data.info.gid);
											MainActivity.this.Refresh();
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
