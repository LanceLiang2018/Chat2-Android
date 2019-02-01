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
import android.content.pm.*;
import com.tbruyelle.rxpermissions2.*;
import com.zhihu.matisse.*;
import io.reactivex.*;
import io.reactivex.Observer;
import io.reactivex.disposables.*;
import com.bumptech.glide.load.engine.*;				

public class MainActivity extends AppCompatActivity {
    private MainPagerAdapter mainPagerAdapter;
    private ViewPager mViewPager;
	private ActionBar bar;
	private ImageView im_head;
	private TextView text_title;
	public static final int code_login = 0x80, code_signup = 0x81, 
							code_chat = 0x82, code_pick = 0x83, 
							code_settings = 0x84;
	private DrawerLayout drawer;
	private MainAdapter adp_rooms;
	private SwipeRefreshLayout srl;
	private ListView list_rooms;
	private ListView list_left;
	List<ItemBeanMain> data = new ArrayList<ItemBeanMain>();
	List<ItemBeanMain> data_printer = new ArrayList<ItemBeanMain>();
	private View head_view;
	private TextView head_username, head_motto;
	private ImageView head_head, head_bg;
	List<View> page_array = new ArrayList<>();
	LinearLayout index_base;
	LinearLayout index;
	LinearLayout index_box;
	TextView hitokoto;
	TextView hitokoto_from;
	Button index_photo;
	private ListView list_printer;
	private PeopleAdapter adp_rooms_printer;
	private SwipeRefreshLayout srl_printer;
	
	
	private OnItemClickListener left_onClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			MenuData.LeftMenu bean = (MenuData.LeftMenu) p2.getTag();
			if (bean == null)
				return;
			
			Bundle bundle = new Bundle();
			bundle.putString("title", bean.item.title);
			
			switch (bean.id) {
				case MenuData.ID.LEFT_ME:
					MyApplication.getMyApplication().putObject("data", MenuData.listMe);
					break;
				case MenuData.ID.LEFT_SETTINGS:
					MyApplication.getMyApplication().putObject("data", MenuData.listSettings);
					break;
				case MenuData.ID.LEFT_PEOPLE:
					MyApplication.getMyApplication().putObject("data", MenuData.listPeople);
					break;
				case MenuData.ID.LEFT_PRINTER:
					MyApplication.getMyApplication().putObject("data", MenuData.listPrinter);
					break;
				case MenuData.ID.LEFT_MORE:
					MyApplication.getMyApplication().putObject("data", MenuData.listAdds);
					break;
				default:
					return;
					//break;
			}
			startActivityForResult(new Intent().setClass(MainActivity.this, Settings.class).putExtras(bundle), code_settings);
		}
	};

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.i("Chat 2", "Started.");
		setTheme(Config.get(this).data.settings.theme);
		
		RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
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
				{}});
		
		
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
		
		//Communication.test(this);
		
		// Index
		
		index_base = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.index_page, null);
		index = (LinearLayout) index_base.findViewById(R.id.index_base);
		index_box = (LinearLayout) index.findViewById(R.id.indexpage_base_box);
		hitokoto = (TextView) index.findViewById(R.id.indexpage_hitokoto);
		hitokoto_from = (TextView) index.findViewById(R.id.indexpage_hitokoto_from);
		index_photo = (Button) index.findViewById(R.id.indexpage_photo);
		
		MyApplication myapp = (MyApplication) this.getApplication();

		//Init icon
		int[] icons = {R.drawable.icon_add_ones, R.drawable.icon_bad_image, R.drawable.icon_chat_room, R.drawable.icon_exit, 
			R.drawable.icon_loading, R.drawable.icon_loading_2, R.drawable.icon_login, R.drawable.icon_logout, R.drawable.icon_make_friends, 
			R.drawable.icon_music_downloader, R.drawable.icon_net_disk, R.drawable.icon_people, R.drawable.icon_place_holder, 
			R.drawable.icon_printer, R.drawable.icon_settings, R.drawable.icon_sign_up, R.drawable.icon_sign_up_2};
		
		for (int id: icons) {
			Bitmap image = BitmapFactory.decodeResource(this.getResources(), id);
			//int convertColor = Color.parseColor("#FF333333");
			//int toColor = Utils.getAccentColor(this);
			//Bitmap res = Utils.replaceBitmapColor(image, convertColor, toColor);
			Bitmap res = image;
			myapp.putObject(id, res);
		}
		
		index_base.removeView(index);
		page_array.add(index);
		
		LinearLayout counter_bg = (LinearLayout) index.findViewById(R.id.indexpage_counter_bg);
		final LinearLayout hitokoto_bg = (LinearLayout) index.findViewById(R.id.indexpage_hitokoto_bg);
		//Bitmap bitmap1 = Bitmap.createBitmap(new int[] {Utils.getPrimaryColor(this), },
		Bitmap bitmap1 = Bitmap.createBitmap(new int[] {Color.parseColor("#7e7e7e7e"), }, 
											 1, 1, Bitmap.Config.ARGB_8888);
		//Bitmap bitmap2 = Bitmap.createBitmap(new int[] {Utils.getPrimaryColor(this), },
		Bitmap bitmap2 = Bitmap.createBitmap(new int[] {Color.parseColor("#7e7e7e7e"), }, 
											 1, 1, Bitmap.Config.ARGB_8888);
		Bitmap bitmap3 = Bitmap.createBitmap(new int[] {Color.parseColor("#3e3e3e3e"), }, 
											 1, 1, Bitmap.Config.ARGB_8888);
		int accentColor = Utils.getAccentColor(MainActivity.this);
		Bitmap bitmap4 = Bitmap.createBitmap(new int[] {accentColor }, 
											 1, 1, Bitmap.Config.ARGB_8888);
		
		final StringCallback hitokoto_callback = new StringCallback() {
			@Override
			public void onSuccess(Response<String> p1) {
				if (p1.code() != 200) {
					hitokoto.setText("网络错误");
					hitokoto_from.setText("");
					hitokoto_bg.setAlpha(0);
					hitokoto_bg.animate().alpha(1);
					return;
				}
				HitokotoData data = new HitokotoData();
				try {
					data = new Gson().fromJson(p1.body().toString(), HitokotoData.class);
				} catch (JsonSyntaxException e) {
					hitokoto.setText("API错误");
					hitokoto_from.setText("");
					hitokoto_bg.setAlpha(0);
					hitokoto_bg.animate().alpha(1);
					return;
				}
				hitokoto.setText(data.hitokoto);
				hitokoto_from.setText(" —— " + data.from);
				hitokoto_bg.setAlpha(0);
				hitokoto_bg.animate().alpha(1);
				int hitokoto_count = (int)(MyApplication.getMyApplication().getObject("hitokoto_click"));
				if (hitokoto_count > 20) {
					new AlertDialog.Builder(MainActivity.this).setMessage("so kawayi kodo~").show();
				}
				hitokoto_count = hitokoto_count + 1;
				MyApplication.getMyApplication().putObject("hitokoto_click", hitokoto_count);
			}
			public void onError(Response<String> p1) {
				hitokoto.setText("网络错误");
				hitokoto_from.setText("");
				hitokoto_bg.setAlpha(0);
				hitokoto_bg.animate().alpha(1);
			}
				
		};
		hitokoto_from.setText("");
		hitokoto.setText("正在加载...");
		hitokoto_bg.setAlpha(1);
		hitokoto_bg.animate().alpha(0);
		Hitokoto.get(this, hitokoto_callback);
		MyApplication.getMyApplication().putObject("hitokoto_click", 0);
		hitokoto_bg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					hitokoto_from.setText("");
					hitokoto.setText("正在加载...");
					Hitokoto.get(MainActivity.this, hitokoto_callback);
					hitokoto_bg.setAlpha(1);
					hitokoto_bg.animate().alpha(0);
				}
			});
		
		RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(getResources(), bitmap1);
		roundedBitmapDrawable1.setCornerRadius(15);
		counter_bg.setBackground(roundedBitmapDrawable1);
		RoundedBitmapDrawable roundedBitmapDrawable2 = RoundedBitmapDrawableFactory.create(getResources(), bitmap2);
		roundedBitmapDrawable2.setCornerRadius(15);
		hitokoto_bg.setBackground(roundedBitmapDrawable2);
		RoundedBitmapDrawable roundedBitmapDrawable3 = RoundedBitmapDrawableFactory.create(getResources(), bitmap3);
		roundedBitmapDrawable3.setCornerRadius(15);
		index_box.setBackground(roundedBitmapDrawable3);
		RoundedBitmapDrawable roundedBitmapDrawable4 = RoundedBitmapDrawableFactory.create(getResources(), bitmap4);
		roundedBitmapDrawable4.setCornerRadius(15);
		index_photo.setBackground(roundedBitmapDrawable4);
		
		// Main Printer Layout
		
		LinearLayout inview_printer = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main_printer, null);
		RelativeLayout mainLayout_printer = (RelativeLayout) inview_printer.findViewById(R.id.mainRelativeLayout_printer);
		inview_printer.removeView(mainLayout_printer);
		page_array.add(mainLayout_printer);
		
		// Main People Layout

		LinearLayout inview = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main, null);
		RelativeLayout mainLayout = (RelativeLayout) inview.findViewById(R.id.mainRelativeLayout_main);
		inview.removeView(mainLayout);
		page_array.add(mainLayout);
		
		// ActionBar
		
		bar = getSupportActionBar();
		View title_view = LayoutInflater.from(this).inflate(R.layout.title_main, null);
		im_head = (ImageView)title_view.findViewById(R.id.titlemainImageButton_head);
		text_title = (TextView)title_view.findViewById(R.id.titlemainTextView_title);
		
		View.OnClickListener title_onclick = new OnClickListener() {
			@Override
			public void onClick(View p1) {
				if (drawer.isDrawerOpen(Gravity.LEFT))
					drawer.closeDrawers();
				else
					drawer.openDrawer(Gravity.LEFT);
			}
		};

		im_head.setOnClickListener(title_onclick);
		text_title.setOnClickListener(title_onclick);
		
		bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayHomeAsUpEnabled(false);
		bar.setDisplayShowCustomEnabled(true);
		bar.setCustomView(title_view);
		
		// List rooms

		list_rooms = (ListView) mainLayout.findViewById(R.id.list_rooms);
		list_printer = (ListView) mainLayout_printer.findViewById(R.id.list_rooms_printer);
		
		//list_rooms.setDividerHeight(0);
		//list_printer.setDividerHeight(0);
		
		list_left = (ListView) findViewById(R.id.list_left);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		adp_rooms = new MainAdapter(this, data);
		adp_rooms_printer = new PeopleAdapter(this, data_printer);

		list_rooms.setAdapter(adp_rooms);
		list_printer.setAdapter(adp_rooms_printer);
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
		list_printer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
					Intent intent_room = new Intent();
					intent_room.setClass(MainActivity.this, Chat.class);
					Bundle bundle=new Bundle();
					bundle.putString("name", data_printer.get(p3).title);
					bundle.putInt("gid", data_printer.get(p3).gid);
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
			// #!# Some thing wrong
		empty_text.setText("啊嘞？没有内容哦~(*σ´∀`)σ 下拉刷新");
		//empty_bg.addView(empty_text);
		list_rooms.setEmptyView(empty_text);

		// SRL
		
		srl = (SwipeRefreshLayout) mainLayout.findViewById(R.id.slr);
		srl.setEnabled(true);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					MainActivity.this.refresh();
					srl.setRefreshing(false);
				}
			});
		srl.setColorSchemeResources(Config.get(this).data.settings.colorFt);
		
		srl_printer = (SwipeRefreshLayout) mainLayout_printer.findViewById(R.id.slr_printer);
		srl_printer.setEnabled(true);
		srl_printer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					MainActivity.this.refresh();
					srl_printer.setRefreshing(false);
				}
			});
		srl_printer.setColorSchemeResources(Config.get(this).data.settings.colorFt);
		
		//List<ItemBeanLeft> left_list = new ArrayList<ItemBeanLeft>(Arrays.asList(left_data));
		//List<ItemBeanLeft[]> left_child = new ArrayList<ItemBeanLeft[]>(Arrays.asList(left_child_data));
		
		// Left person

		LayoutInflater inflater = LayoutInflater.from(this);
		head_view = inflater.inflate(R.layout.person_frame, null);
		head_head = (ImageView) head_view.findViewById(R.id.personImageView_head);
		head_username = (TextView) head_view.findViewById(R.id.personTextView_username);
		head_motto = (TextView) head_view.findViewById(R.id.personTextView_motto);
		head_bg = (ImageView) head_view.findViewById(R.id.personImageView_bg);
		
		head_head.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					//Bundle bundle = new Bundle();
					//bundle.putString("username", bean.username);
					//bundle.putString("head_url", bean.head_url);
					MyApplication.getMyApplication().putObject("username", Config.get(getApplicationContext()).data.user.username);
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, Person.class);
					//intent.putExtras(bundle);
					startActivity(intent);
				}
			});

		list_left.setAdapter(new LeftAdapter(this));
		list_left.addHeaderView(head_view);
		list_left.setOnItemClickListener(left_onClickListener);
		
		/*
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
			});*/

		refresh();
		
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
		
		ImageView bgimage = (ImageView) findViewById(R.id.mainviewpagerImageView_bgimage);
		Glide.with(this).load(getExternalFilesDir("Background").getAbsolutePath() + "/background")
			.apply(new RequestOptions().transform(new BlurTransformation(20))
				.diskCacheStrategy(DiskCacheStrategy.NONE))
			.into(bgimage);

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
				private int[] mImages = {R.drawable.icon_index_2, R.drawable.icon_printer, R.drawable.icon_chat_room};
				
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
					/*
					Glide.with(context).load(MyApplication.getMyApplication().getObject(mImages[index]))
						.apply(new RequestOptions().transform(new ColorFilterTransformation(Utils.getAccentColor(context))))
						.into(titleImg);
					*/
					titleImg.setImageResource(mImages[index]);
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
	
	public void refresh()
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
		adp_rooms_printer.list.clear();
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(this).data.user.auth);
		Communication.getComm(this).post(Communication.GET_ROOM_ALL, parames, 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> response) {
					ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
					if (result.code == 0) {
						for (RoomData room_data: result.data.room_data) {
							String name = room_data.name;
							String[] split = name.split("\\|");
							String[] heads = room_data.head.split("\\|");
							String head = room_data.head;
							String name_me = Config.get(getApplicationContext()).data.user.username, name_friend = "Friend";
							if (split.length > 0) {
								if (split[0].equals(name_me)) {
									name_friend = split[1];
									head = heads[0];
								} else {
									name_friend = split[0];
									head = heads[1];
								}
							}

							adp_rooms.insert(new ItemBeanMain(room_data.gid, head, name_friend, 
															  //room_data.latest_msg == null ? "Latest Messages" : room_data.latest_msg, 
															  "Latest message",
															  //room_data.latest_time == null ? "" : room_data.latest_time, 
															  new MyGetTime().remote(room_data.last_post_time))
															  .setRoomType(room_data.room_type)
															  .setTimeSrc(room_data.last_post_time));
							adp_rooms.notifyDataSetChanged();
							adp_rooms_printer.insert(new ItemBeanMain(room_data.gid, head, name_friend, 
																	 //room_data.latest_msg == null ? "Latest Messages" : room_data.latest_msg, 
																	 "Latest message",
																	 //room_data.latest_time == null ? "" : room_data.latest_time, 
																	 new MyGetTime().remote(room_data.last_post_time))
																	 .setRoomType(room_data.room_type)
																	 .setTimeSrc(room_data.last_post_time));
							adp_rooms_printer.notifyDataSetChanged();
						}
						Collections.sort(adp_rooms.list, new Comparator<ItemBeanMain>() {
								@Override
								public int compare(ItemBeanMain p1, ItemBeanMain p2) {
									return ("" + p2.timesrc).compareTo("" + p1.timesrc);
								}
							});
						Collections.sort(adp_rooms_printer.list, new Comparator<ItemBeanMain>() {
								@Override
								public int compare(ItemBeanMain p1, ItemBeanMain p2) {
									return ("" + p2.timesrc).compareTo("" + p1.timesrc);
								}
							});
					}
					if (result.code == 2)
					{
						Toast.makeText(MainActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
						Intent intent_login = new Intent();
						intent_login.setClass(MainActivity.this, Login.class);
						startActivityForResult(intent_login, code_login);
					}
				}
			});
		
		Communication.getComm(getApplicationContext()).postWithAuth(Communication.GET_USER, new ContentValues(), 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					if (p1.code() != 200) {return; }
					ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
					if (result.code == 0) {
						Config config = Config.get(getApplicationContext());
						config.data.user.username = result.data.user_info.username;
						
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
					if (str.equals("Refresh")) {
						this.refresh();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case code_pick:
				//mAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data));
				if (resultCode == RESULT_OK) {
					for (String s: Matisse.obtainPathResult(data))
						Toast.makeText(this, s, Toast.LENGTH_LONG).show();
				}
				break;
			case code_settings:
				try {
					String str = data.getStringExtra("command");
					if (str.equals("Recreate")) {
						this.recreate();
					}
					if (str.equals("Refresh")) {
						this.refresh();
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
				Settings.myNewRoom(this);
				break;
			case R.id.option_join_in:
				Settings.myJoinIn(this);
				break;
			case R.id.option_clear_all:
				//Communication.test(this);
				Communication.getComm(this).get(Communication.SERVER + "/clear_all", null);
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume()
	{
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
		//	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}
}
				
