package lance.liang.chat2;

import android.content.*;
import android.content.pm.*;
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
import com.zhihu.matisse.*;
import com.zhihu.matisse.engine.impl.*;
import java.util.*;
import lance.liang.chat2.*;

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
	
	public static final int code_login = 0x80, code_signup = 0x81, code_chat = 0x82, code_pick = 0x83;

	private ActionBar bar;
	private DrawerLayout drawer;
	
	private ItemBeanLeft[] left_data = {
		new ItemBeanLeft(R.drawable.image_1, "我"),
		new ItemBeanLeft(R.drawable.image_2, "联系人"),
		new ItemBeanLeft(R.drawable.image_2, "设置"),
		new ItemBeanLeft(R.drawable.image_2, "网盘"),
		new ItemBeanLeft(R.drawable.image_2, "插件"),
		new ItemBeanLeft(R.drawable.image_2, "退出"),
	};
	private ItemBeanLeft[][] left_child_data = {
		{
			new ItemBeanLeft(R.drawable.image_1, "我的信息"),
			new ItemBeanLeft(R.drawable.image_1, "新用户"),
			new ItemBeanLeft(R.drawable.image_1, "新 Room"),
			new ItemBeanLeft(R.drawable.image_2, "注销"),
		},
		{},
		{
			new ItemBeanLeft(R.drawable.image_1, "主题"),
			new ItemBeanLeft(R.drawable.image_2, "服务器地址"),
			new ItemBeanLeft(R.drawable.image_1, "文件保存目录"),
			new ItemBeanLeft(R.drawable.image_1, "关于"),
		},
		{},
		{},
		{},
	};
	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("Chat 2", "Started.");
		setTheme(Config.get(this).data.settings.theme);
		
		//todo: init glide
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		bar = getSupportActionBar();
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
		srl.setColorSchemeResources(Config.get(this).data.settings.colorFt);
		
		List<ItemBeanLeft> left_list = new ArrayList<ItemBeanLeft>(Arrays.asList(left_data));
		List<ItemBeanLeft[]> left_child = new ArrayList<ItemBeanLeft[]>(Arrays.asList(left_child_data));
		
		left.setAdapter(new LeftAdapter(this, left_list, left_child));
		left.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
				@Override
				public boolean onGroupClick(ExpandableListView p1, View p2, int p3, long p4) {
					if (p3 == 5) { // exit
						MainActivity.this.finish();
					} else if (p3 == 4) { // add-ones
						Toast.makeText(MainActivity.this, "No add-ones yet.", Toast.LENGTH_SHORT).show();
					}
					return false;
				}
		});
		left.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				@Override
				public boolean onChildClick(ExpandableListView p1, View p2, int p3, int p4, long p5) {
					if (p3 == 0) { // me
						if (p4 == 0) { // info
							
						} else if (p4 == 1) { // new user
							Intent intent_signup = new Intent();
							intent_signup.setClass(MainActivity.this, Signup.class);
							startActivityForResult(intent_signup, code_signup);
						} else if (p4 == 2) { // new room
							edit = new EditText(MainActivity.this);
							AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
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
						} else if (p4 == 3) { // logout
							Config config = Config.get(MainActivity.this);
							config.data.user.auth = "";
							config.save();
							MainActivity.this.recreate();
						}
					} else if (p3 == 2) { // settings
						if (p4 == 0) { // theme
							final String[] disp = {"默认", "荷月", "惊蛰", "霜序", "岁馀"};
							final int[] vals = {R.style.AppTheme01, R.style.AppTheme02, R.style.AppTheme03, R.style.AppTheme04, R.style.AppTheme05};
							final int[] valsBg = {R.color.colorBg01, R.color.colorBg02, R.color.colorBg03, R.color.colorBg04, R.color.colorBg05};
							final int[] valsFt = {R.color.colorFt01, R.color.colorFt02, R.color.colorFt03, R.color.colorFt04, R.color.colorFt05};
							
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle("Set Theme")
								.setItems(disp, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface p1, int p2) {
										Config config = Config.get(MainActivity.this);
										config.data.settings.theme = vals[p2];
										config.data.settings.colorBg = valsBg[p2];
										config.data.settings.colorFt = valsFt[p2];
										config.save();
										Toast.makeText(MainActivity.this, "Change into theme: " + disp[p2], Toast.LENGTH_LONG).show();
										MainActivity.this.recreate();
									}
								});
							builder.show();
						} else if (p4 == 1) { // host
							String[] disp = {"Remote", "Local"};
							final String[] vals = {
								"https://lance-chatroom2.herokuapp.com/", 
								"http://0.0.0.0:5000/"};
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle("Set Host")
								.setItems(disp, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface p1, int p2) {
										Config config = Config.get(MainActivity.this);
										config.data.settings.server = vals[p2];
										config.save();
										Toast.makeText(MainActivity.this, "Change into host: " + config.data.settings.server, Toast.LENGTH_LONG).show();
										MainActivity.this.recreate();
									}
								});
							builder.show();
						} else if (p4 == 2) { // save-dir
							
						} else if (p4 == 3) { // about
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle("About");
							builder.setMessage("https://github.com/LanceLiang2018/Chat2-Android/");
							builder.show();
						}
					} else if (p3 == 4) { // add-ones
						
					}
					return false;
				}
			});
		
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
		// Here errors.
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
					/*
					//Delete
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
					}*/
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
			case R.id.option_clear_all:
				/*
				Communication.getComm(this).get(Communication.CLEAR_ALL, 
					new StringCallback() {
						@Override
						public void onSuccess(Response<String> p1) {
							ResultData result = (new Gson()).fromJson(p1.body(), ResultData.class);
							Toast.makeText(MainActivity.this, result.message, Toast.LENGTH_SHORT).show();
						}
					});
				*/
				Matisse.from(MainActivity.this)
					.choose(MimeType.ofAll())
					.countable(true)
					.maxSelectable(9)
					//.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
					//.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
					.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
					.thumbnailScale(0.85f)
					.imageEngine(new GlideEngine())
					.forResult(code_pick);
				
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
