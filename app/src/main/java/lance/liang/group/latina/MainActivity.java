package lance.liang.group.latina;

import android.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.drawable.*;
import com.bumptech.glide.request.*;
import com.google.gson.*;
import com.lzy.okgo.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.lzy.okserver.*;
import com.lzy.okserver.task.*;
import com.zhihu.matisse.*;
import java.util.*;
import jp.wasabeef.glide.transformations.*;
import lance.liang.group.latina.*;
import net.lucode.hackware.magicindicator.*;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.*;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.*;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.*;

import lance.liang.group.latina.Config;
import android.support.v4.graphics.drawable.*;
import android.graphics.drawable.*;
import android.widget.AdapterView.*;

/*
                Bitmap btm = BitmapFactory.decodeResource(getResources(),
														  R.drawable.image_2);
                Intent intent = new Intent(MainActivity.this,
										   MainActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(
					MainActivity.this, 0, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);

                Notification noti = new NotificationCompat.Builder(
					MainActivity.this)
					.setSmallIcon(R.drawable.image_1)
					.setLargeIcon(btm)
					.setNumber(13)
					.setDefaults(Notification.DEFAULT_LIGHTS)
					.setContentIntent(pendingIntent)
					.setStyle(
					new NotificationCompat.InboxStyle()
					.addLine("M.Twain Lunch?")
					.setBigContentTitle("6 new message"))
					.build();

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, noti);
				*/

				

public class MainActivity extends AppCompatActivity {
    private MainPagerAdapter mainPagerAdapter;
    private ViewPager mViewPager;
	private ActionBar bar;
	private ImageView im_head;
	private TextView text_title;
	private EditText edit;
	public static final int code_login = 0x80, code_signup = 0x81, code_chat = 0x82, code_pick = 0x83;
	private DrawerLayout drawer;
	private MainAdapter adp_rooms;
	private SwipeRefreshLayout srl;
	private ListView list_rooms;
	private ListView list_left;
	List<ItemBeanMain> data = new ArrayList<ItemBeanMain>();
	private View head_view;
	private TextView head_username, head_motto;
	private ImageView head_head, head_bg;
	List<View> page_array = new ArrayList<>();
	
	
	
	private ItemBeanLeft[] left_data = {
		new ItemBeanLeft(R.drawable.icon_index, "我"),
		new ItemBeanLeft(R.drawable.icon_people, "联系人"),
		new ItemBeanLeft(R.drawable.icon_settings, "设置"),
		new ItemBeanLeft(R.drawable.icon_net_disk, "网盘"),
		new ItemBeanLeft(R.drawable.icon_add_ones, "插件"),
		new ItemBeanLeft(R.drawable.icon_logout, "退出"),
	};
	private ItemBeanLeft[][] left_child_data = {
		{
			new ItemBeanLeft("我的信息"),
			new ItemBeanLeft("新用户"),
			new ItemBeanLeft("新 Room"),
			new ItemBeanLeft("新朋友"),
			new ItemBeanLeft("注销"),
		},
		{},
		{
			new ItemBeanLeft("主题"),
			new ItemBeanLeft("服务器地址"),
			new ItemBeanLeft("文件保存目录"),
			new ItemBeanLeft("关于"),
		},
		{},
		{},
		{},
	};
	
	private OnItemClickListener left_onClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			MenuData.LeftMenu bean = (MenuData.LeftMenu) p2.getTag();
			if (bean == null)
				return;
			switch (bean.id) {
				case MenuData.ID.LEFT_ME:
					MyApplication.getMyApplication().putObject("data", MenuData.listMe);
					startActivity(new Intent().setClass(MainActivity.this, Settings.class));
					break;
				case MenuData.ID.LEFT_SETTINGS:
					MyApplication.getMyApplication().putObject("data", MenuData.listSettings);
					startActivity(new Intent().setClass(MainActivity.this, Settings.class));
					break;
				default:
					break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.i("Chat 2", "Started.");
		setTheme(Config.get(this).data.settings.theme);
		
		OkGo.getInstance().init(getApplication());

		OkUpload okupload = OkUpload.getInstance();
		okupload.getThreadPool().setCorePoolSize(3);
		okupload.addOnAllTaskEndListener(new XExecutor.OnAllTaskEndListener() {
				@Override
				public void onAllTaskEnd() {
					Toast.makeText(MainActivity.this, "All end.", Toast.LENGTH_SHORT).show();
				}
			});
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_viewpager);
		
		MyApplication myapp = (MyApplication) this.getApplication();

		//Init icon
		int[] icons = {R.drawable.icon_add_ones, R.drawable.icon_bad_image, R.drawable.icon_chat_room, R.drawable.icon_exit, 
			R.drawable.icon_loading, R.drawable.icon_loading_2, R.drawable.icon_login, R.drawable.icon_logout, R.drawable.icon_make_friends, 
			R.drawable.icon_music_downloader, R.drawable.icon_net_disk, R.drawable.icon_people, R.drawable.icon_place_holder, 
			R.drawable.icon_printer, R.drawable.icon_settings, R.drawable.icon_sign_up, R.drawable.icon_sign_up_2};
		
		for (int id: icons) {
			Bitmap image = BitmapFactory.decodeResource(this.getResources(), id);
			int convertColor = Color.parseColor("#FF333333");
			int toColor = Utils.getAccentColor(this);
			//Bitmap res = Utils.replaceBitmapColor(image, convertColor, toColor);
			Bitmap res = image;
			myapp.putObject(id, res);
		}
		
		LinearLayout index_base = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.index_page, null);
		LinearLayout index = (LinearLayout) index_base.findViewById(R.id.index_base);
		index.removeView(index);
		page_array.add(index);
		
		LinearLayout counter_bg = (LinearLayout) index.findViewById(R.id.indexpage_counter_bg);
		final LinearLayout hitokoto_bg = (LinearLayout) index.findViewById(R.id.indexpage_hitokoto_bg);
		Bitmap bitmap1 = Bitmap.createBitmap(new int[] {Utils.getPrimaryColor(this), },
											 1, 1, Bitmap.Config.ARGB_8888);
		Bitmap bitmap2 = Bitmap.createBitmap(new int[] {Utils.getPrimaryColor(this), },
											 1, 1, Bitmap.Config.ARGB_8888);
		
		final TextView hitokoto = (TextView) index.findViewById(R.id.indexpage_hitokoto);
		final TextView hitokoto_from = (TextView) index.findViewById(R.id.indexpage_hitokoto_from);
		final StringCallback hitokoto_callback = new StringCallback() {
			@Override
			public void onSuccess(Response<String> p1) {
				if (p1.code() != 200) {
					hitokoto.setText("网络错误");
					hitokoto_from.setText("");
					return;
				}
				HitokotoData data = new Gson().fromJson(p1.body().toString(), HitokotoData.class);
				hitokoto.setText(data.hitokoto);
				hitokoto_from.setText(" —— " + data.from);
				hitokoto_bg.setTranslationY(-hitokoto_bg.getHeight());
				hitokoto_bg.animate().translationY(0);
			}
			public void onError(Response<String> p1) {
				hitokoto.setText("网络错误");
				hitokoto_from.setText("");
			}
				
		};
		hitokoto_from.setText("");
		hitokoto.setText("正在加载...");
		Hitokoto.get(this, hitokoto_callback);
		hitokoto_bg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					hitokoto_from.setText("");
					hitokoto.setText("正在加载...");
					Hitokoto.get(MainActivity.this, hitokoto_callback);
				}
			});
		
		RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(getResources(), bitmap1);
		roundedBitmapDrawable1.setCornerRadius(35);
		counter_bg.setBackground(roundedBitmapDrawable1);
		RoundedBitmapDrawable roundedBitmapDrawable2 = RoundedBitmapDrawableFactory.create(getResources(), bitmap2);
		roundedBitmapDrawable2.setCornerRadius(35);
		hitokoto_bg.setBackground(roundedBitmapDrawable2);
		
		LinearLayout inview = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main, null);
		RelativeLayout mainLayout = (RelativeLayout) inview.findViewById(R.id.mainRelativeLayout_main);
		mainLayout.removeView(mainLayout);
		page_array.add(mainLayout);
		
		bar = getSupportActionBar();
		View title_view = LayoutInflater.from(this).inflate(R.layout.title_main, null);
		im_head = (ImageView)title_view.findViewById(R.id.titlemainImageButton_head);
		text_title = (TextView)title_view.findViewById(R.id.titlemainTextView_title);
		
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
		//RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.image_2));
		//roundedBitmapDrawable1.setCircular(true);
		//im_head.setImageDrawable(roundedBitmapDrawable1);

		bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayHomeAsUpEnabled(false);
		bar.setDisplayShowCustomEnabled(true);
		bar.setCustomView(title_view);

		list_rooms = (ListView) mainLayout.findViewById(R.id.list_rooms);
		list_left = (ListView) findViewById(R.id.list_left);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		adp_rooms = new MainAdapter(this, data);

		list_rooms.setAdapter(adp_rooms);
		list_rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
		//LinearLayout main_empty_base = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main_empry, null);
		//RelativeLayout main_empty = (RelativeLayout) main_empty_base.findViewById(R.id.mainempryRelativeLayout);
		//main_empty_base.removeView(main_empty);
		//((ViewGroup) list.getParent()).addView(main_empty);
		//list.setEmptyView(main_empty);
		//LinearLayout empty_bg = new LinearLayout(this);
		TextView empty_text = new TextView(this);
		empty_text.setText("啊嘞？没有内容哦~(*σ´∀`)σ 下拉刷新");
		//empty_bg.addView(empty_text);
		list_rooms.setEmptyView(empty_text);

		srl = (SwipeRefreshLayout) mainLayout.findViewById(R.id.slr);
		srl.setEnabled(true);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					MainActivity.this.Refresh();
					srl.setRefreshing(false);
				}
			});
		srl.setColorSchemeResources(Config.get(this).data.settings.colorFt);

		//List<ItemBeanLeft> left_list = new ArrayList<ItemBeanLeft>(Arrays.asList(left_data));
		//List<ItemBeanLeft[]> left_child = new ArrayList<ItemBeanLeft[]>(Arrays.asList(left_child_data));

		LayoutInflater inflater = LayoutInflater.from(this);
		head_view = inflater.inflate(R.layout.person_frame, null);
		head_head = (ImageView) head_view.findViewById(R.id.personImageView_head);
		head_username = (TextView) head_view.findViewById(R.id.personTextView_username);
		head_motto = (TextView) head_view.findViewById(R.id.personTextView_motto);
		head_bg = (ImageView) head_view.findViewById(R.id.personImageView_bg);

		list_left.setAdapter(new LeftAdapter(this));
		list_left.addHeaderView(head_view);
		list_left.setOnItemClickListener(left_onClickListener);
		/*
		list_left.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
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
		list_left.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				@Override
				public boolean onChildClick(ExpandableListView p1, View p2, int p3, int p4, long p5) {
					if (p3 == 0) { // me
						if (p4 == 0) { // info
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							bundle.putString("username", Config.get(MainActivity.this).data.user.username);
							bundle.putString("head_url", Config.get(MainActivity.this).data.user.head);
							intent.putExtras(bundle);
							intent.setClass(MainActivity.this, Person.class);
							MainActivity.this.startActivity(intent);
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
						} else if (p4 == 3) { // make friends
							final EditText edit = new EditText(MainActivity.this);
							DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface p1, int p2) {
									ContentValues params = new ContentValues();
									params.put("auth", Config.get(MainActivity.this).data.user.auth);
									params.put("friend", String.valueOf(edit.getText()));
									Communication.getComm(MainActivity.this).post(Communication.MAKE_FRIENDS, params, 
										new StringCallback() {
											@Override
											public void onSuccess(Response<String> p1) {
												ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
												if (result.code != 0) {
													new AlertDialog.Builder(MainActivity.this)
														.setMessage(result.message + " (Code: " + result.code + ")")
														.show();
													return;
												}
												MainActivity.this.recreate();
											}
										});
								}
							};
							new AlertDialog.Builder(MainActivity.this)
								.setTitle("New friends")
								.setMessage("Enter username:")
								.setView(edit)
								.setPositiveButton("Yes", listener)
								.setNegativeButton("No", null)
								.show();
						} else if (p4 == 4) { // logout
							Config config = Config.get(MainActivity.this);
							config.data.user.auth = "";
							config.save();
							MainActivity.this.recreate();
						}
					} else if (p3 == 2) { // settings
						if (p4 == 0) { // theme
							final String[] disp = {"默认", "荷月", "惊蛰", "霜序", "岁馀", "纯净", "夜行"};
							final int[] vals = {R.style.AppTheme01, R.style.AppTheme02, R.style.AppTheme03, R.style.AppTheme04, R.style.AppTheme05, R.style.AppTheme06, R.style.AppTheme07};
							final int[] valsBg = {R.color.colorBg01, R.color.colorBg02, R.color.colorBg03, R.color.colorBg04, R.color.colorBg05, R.color.colorBg06, R.color.colorBg07};
							final int[] valsFt = {R.color.colorFt01, R.color.colorFt02, R.color.colorFt03, R.color.colorFt04, R.color.colorFt05, R.color.colorFt06, R.color.colorFt07};

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
		*/

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

		Refresh();
		
		/*
		for (int i=0; i<3; i++) {
			inview = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main, null);
			mainLayout = (RelativeLayout) inview.findViewById(R.id.mainRelativeLayout_main);
			inview.removeView(mainLayout);
			page_array.add(inview);
		}*/
		
		mainPagerAdapter = new MainPagerAdapter(page_array);

		//mainPagerAdapter = new MainPagerAdapter();
		
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mainPagerAdapter);

        initMagicIndicator1();
    }
	
    private void initMagicIndicator1() {
		int color = Utils.getPrimaryColor(this);

        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator1);
        magicIndicator.setBackgroundColor(color);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
				private List<String> mDataList = Arrays.asList(new String[] {"主页", "打印机", "联系人"});
				private int[] mImages = {R.drawable.icon_index, R.drawable.icon_printer, R.drawable.icon_chat_room};
				
				@Override
				public int getCount() {
					return mDataList.size();
				}

				@Override
				public IPagerTitleView getTitleView(Context context, final int index) {
					CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);

					// load custom layout
					View customLayout = LayoutInflater.from(context).inflate(R.layout.pager_title_layout, null);
					final ImageView titleImg = (ImageView) customLayout.findViewById(R.id.title_img);
					final TextView titleText = (TextView) customLayout.findViewById(R.id.title_text);
					//titleImg.setImageResource(mImages[index]);
					Glide.with(context).load(MyApplication.getMyApplication().getObject(mImages[index]))
						.apply(new RequestOptions().transform(new ColorFilterTransformation(Utils.getAccentColor(context))))
						.into(titleImg);
					titleText.setText(mDataList.get(index));
					commonPagerTitleView.setContentView(customLayout);

					commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

							@Override
							public void onSelected(int index, int totalCount) {
								titleText.setTextColor(Color.WHITE);
							}

							@Override
							public void onDeselected(int index, int totalCount) {
								titleText.setTextColor(Color.LTGRAY);
							}

							@Override
							public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
								titleImg.setScaleX(1.3f + (0.8f - 1.3f) * leavePercent);
								titleImg.setScaleY(1.3f + (0.8f - 1.3f) * leavePercent);
							}

							@Override
							public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
								titleImg.setScaleX(0.8f + (1.3f - 0.8f) * enterPercent);
								titleImg.setScaleY(0.8f + (1.3f - 0.8f) * enterPercent);
							}
						});

					commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mViewPager.setCurrentItem(index);
							}
						});

					return commonPagerTitleView;
				}

				@Override
				public IPagerIndicator getIndicator(Context context) {
					return null;
				}
			});
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }
	
	public void Refresh()
	{
		// Here errors.
		RequestOptions options = new RequestOptions()
			.circleCrop()
			.placeholder(R.drawable.image_1)
			.dontAnimate();

		Glide.with(this).load(Config.get(this).data.user.head)
			.apply(options)
			.into(im_head);
		text_title.setText(Config.get(this).data.user.username);
		head_username.setText(Config.get(this).data.user.username);
		head_motto.setText(Config.get(this).data.user.motto);
		Glide.with(this).load(Config.get(this).data.user.head)
			.apply(new RequestOptions().placeholder(R.drawable.image_blank)
				   .circleCrop())
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(head_head);
		Glide.with(this).load(Config.get(this).data.user.head)
			.apply(new RequestOptions().placeholder(R.drawable.image_blank)
				   .centerCrop()
				   .transform(new BlurTransformation()))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(head_bg);

		adp_rooms.list.clear();
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).data.user.auth);
		Communication.getComm(this).post(Communication.GET_ROOMS, parames, 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> response) {
					ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
					if (result.code == 0) {
						for (ResultData.Data.Info room_data: result.data.room_data) {
							adp_rooms.insert(new ItemBeanMain(room_data.gid, room_data.head, room_data.name, 
														//room_data.latest_msg == null ? "Latest Messages" : room_data.latest_msg, 
														"Latest message",
														//room_data.latest_time == null ? "" : room_data.latest_time, 
														new MyGetTime().remote(room_data.last_post_time)));
							adp_rooms.notifyDataSetChanged();
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
			case code_pick:
				//mAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data));
				if (resultCode == RESULT_OK)
				{
					for (String s: Matisse.obtainPathResult(data))
						Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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
			case R.id.option_join_in:
				edit = new EditText(this);
				new AlertDialog.Builder(this)
					.setMessage("Input gid:")
					.setView(edit)
					.setPositiveButton("OK", new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface p1, int p2) {
							ContentValues parames = new ContentValues();
							parames.put("auth", Config.get(MainActivity.this).data.user.auth);
							parames.put("gid", edit.getText().toString());
							Communication.getComm(MainActivity.this).post(Communication.CREATE_ROOM, parames, 
								new StringCallback() {
									@Override
									public void onSuccess(Response<String> response) {
										ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
										if (result.code == 0) {
											MainActivity.this.Refresh();
										}
										else {
											new AlertDialog.Builder(MainActivity.this)
												.setMessage(result.message + " (Code: " + result.code + ")");
										}
									}
								});
						}
					})
					.setNegativeButton("Cancel", null)
					.show();
				break;
			case R.id.option_clear_all:
				Communication.test(this);
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
				
