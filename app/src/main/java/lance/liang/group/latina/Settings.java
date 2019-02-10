package lance.liang.group.latina;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.tbruyelle.rxpermissions2.*;
import com.zhihu.matisse.*;
import com.zhihu.matisse.internal.entity.*;
import io.reactivex.*;
import io.reactivex.disposables.*;
import java.io.*;
import lance.liang.group.latina.MenuData.*;

import android.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.database.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class Settings extends AppCompatActivity
{
	ListView list;
	int pick_bg = 0x60;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Bundle bundle = getIntent().getExtras();
		MenuData.Settings[] data = (MenuData.Settings[]) MyApplication.getMyApplication().getObject("data");
		//MenuData.Settings title = (MenuData.Settings) MyApplication.getMyApplication().getObject("title");
		
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setTitle(bundle.getString("title"));
		
		list = (ListView) findViewById(R.id.settingsListView);
		list.setAdapter(new SettingsAdapter(this, data));
		list.setOnItemClickListener(listener);
	}
	
	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			MenuData.Settings bean = (MenuData.Settings) p2.getTag();
			if (bean == null)
				return;
			switch (bean.id) {
				case ID.ME_MY_INFO:
					myPersonInfo();
					break;
				case ID.ME_LOGOUT:
					Config config = Config.get(getApplicationContext());
					config.data.user.auth = "";
					config.save();
					setResult(0, new Intent().putExtra("command", "Recreate"));
					Settings.this.finish();
					break;
				case ID.ME_SET_INFO:
					//Toast.makeText(Settings.this, bean.item.title, Toast.LENGTH_LONG).show();
					mySetInfo();
					setResult(0, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.ME_MAKE_FRIENDS:
					makeFriends();
					setResult(0, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.ME_NEW_ROOM:
					myNewRoom();
					setResult(0, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.ME_NEW_USER:
					Intent intent_signup = new Intent();
					intent_signup.setClass(Settings.this, Signup.class);
					startActivity(intent_signup);
					break;
				case ID.ME_LOGIN:
					Intent intent_login = new Intent();
					intent_login.setClass(Settings.this, Login.class);
					startActivity(intent_login);
					break;
				case ID.ABOUT_PAGE:
					Intent intent_about = new Intent();
					intent_about.setClass(Settings.this, AboutPage.class);
					startActivity(intent_about);
					break;
				case ID.ADDS_MUSIC_DOWNLOADER:
					Intent intent_music = new Intent();
					intent_music.setClass(Settings.this, MusicDownloader.class);
					startActivity(intent_music);
					break;
				case ID.SETTINGS_SERVER:
					mySetServer();
					setResult(1, new Intent().putExtra("command", "Recreate"));
					//Settings.this.finish();
					break;
				case ID.SETTINGS_FONT:
					myFont();
					setResult(1, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.SETTINGS_SPLASH:
					mySetSplash();
					setResult(2, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.SETTINGS_THEME:
					mySetTheme();
					setResult(1, new Intent().putExtra("command", "Recreate"));
					//Settings.this.finish();
					break;
				case ID.PRINTER_ADD:
					myAddPrinter();
					setResult(2, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.PRINTER_DEFAULT:
					mySetDefaultPrinter();
					setResult(2, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.ADDS_MY_FILES:
					myFiles();
					break;
				case ID.SETTINGS_BG:
					RxPermissions rxPermissions = new RxPermissions(Settings.this);
					rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
						.subscribe(new Observer<Boolean>() {
							@Override
							public void onError(Throwable p1)
							{Settings.this.finish();}
							@Override
							public void onComplete()
							{}
							@Override
							public void onSubscribe(Disposable d)
							{}
							@Override
							public void onNext(Boolean aBoolean)
							{
								Matisse.from(Settings.this)
									.choose(MimeType.ofImage(), false)
									.countable(false)
									.capture(true)
									.captureStrategy(new CaptureStrategy(true, "lance.liang.group.latina.provider"))
									.maxSelectable(1)
									.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
									.thumbnailScale(0.85f)
									//.originalEnable(true)
									//.maxOriginalSize(10)
									.autoHideToolbarOnSingleTap(true)
									.forResult(pick_bg);
								
							}});
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	public void mySetInfo() {
		Communication.getComm(this).postWithAuth(Communication.GET_USER, new Content().put("username", Config.get(this).data.user.username).val, 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
					if (result.code != 0) { Toast.makeText(Settings.this, result.message, Toast.LENGTH_LONG).show(); return; }
					PersonData user_info = result.data.user_info;
					View dialogView = LayoutInflater.from(Settings.this).inflate(R.layout.dialog_set_info, null);
					final EditText email = (EditText) dialogView.findViewById(R.id.dialogsetinfoEditText_email), 
						motto = (EditText) dialogView.findViewById(R.id.dialogsetinfoEditText_motto);
					email.setText(user_info.email);
					motto.setText(user_info.motto);
					new AlertDialog.Builder(Settings.this)
						.setTitle("设置我的个人信息")
						.setView(dialogView)
						.setNegativeButton("取消", null)
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								Communication.getComm(getApplicationContext()).postWithAuth(Communication.SET_USER, 
									new Content().put("email", email.getText().toString()).put("motto", motto.getText().toString()).val, 
									new StringCallback() {
										@Override
										public void onSuccess(Response<String> p1) {
											ResultData result2 = new Gson().fromJson(p1.body(), ResultData.class);
											Toast.makeText(Settings.this, result2.message, Toast.LENGTH_LONG);
										}
									});
							}
						})
						.show();
				}
			});
	}
	
	public void myFont() {
		String[] select_disp = {"本地显示字体", "远程打印字体"};
		new AlertDialog.Builder(Settings.this)
			.setItems(select_disp, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					if (p2 == 0)
						myFontLocal();
					if (p2 == 1)
						myFontRemote();
				}
			})
			.show();
	}
	
	public void myFontRemote() {
		final List<String> data = Arrays.asList(new String[] {"微软雅黑", "宋体", "仿宋", "黑体",
											  "Microsoft YaHei Mono", "幼圆", "楷体", "隶书"});
		View sview = LayoutInflater.from(Settings.this).inflate(R.layout.settings_font_remote, null);
		Spinner spinner = (Spinner) sview.findViewById(R.id.settingsfontremoteSpinner);
		final EditText edit = (EditText) sview.findViewById(R.id.settingsfontremoteEditText);
		//edit.setText("" + Config.get(getApplicationContext()).data.settings.remoteFontSize);
		edit.setText("" + 10);
		
		final SpinnerAdapter sadp = new SpinnerAdapter() {
			@Override
			public void registerDataSetObserver(DataSetObserver p1) {}
			@Override
			public void unregisterDataSetObserver(DataSetObserver p1) {}
			@Override
			public int getCount() { return data.size(); }
			@Override
			public Object getItem(int p1) { return data.get(p1); }
			@Override
			public long getItemId(int p1) { return p1; }
			@Override
			public boolean hasStableIds() { return true; }
			@Override
			public View getView(int p1, View p2, ViewGroup p3) {
				LinearLayout ll = new LinearLayout(p3.getContext());
				TextView text = new TextView(p3.getContext());
				ll.addView(text);
				text.setText(data.get(p1));
				ll.setPadding(0, 10, 0, 10);
				return ll;
			}
			@Override
			public int getItemViewType(int p1) { return 1; }
			@Override
			public int getViewTypeCount() { return 1; }
			@Override
			public boolean isEmpty() { return false; }
			@Override
			public View getDropDownView(int p1, View p2, ViewGroup p3) {
				LinearLayout ll = new LinearLayout(p3.getContext());
				TextView text = new TextView(p3.getContext());
				ll.addView(text);
				text.setText(data.get(p1));
				ll.setPadding(0, 10, 0, 10);
				return ll;
			}
		};
		spinner.setAdapter(sadp);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
					//Config config = Config.get(getApplicationContext());
					//config.data.settings.remoteFontFamily = data.get(p3);
					//config.save();
					MyApplication.getMyApplication().tmp_select_font = data.get(p3);
				}
				@Override
				public void onNothingSelected(AdapterView<?> p1) {}
			});
		new AlertDialog.Builder(Settings.this)
			.setTitle("远程打印字体")
			.setView(sview)
			.setNegativeButton("取消", null)
			.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					List<RoomData> list_rooms = new ArrayList<RoomData>();
					//for (RoomData r: MyApplication.getMyApplication().list_rooms)
					for (int i=0; i<MyApplication.getMyApplication().list_rooms.size(); i++)
						if (MyApplication.getMyApplication().list_rooms.get(i).room_type.equals("printer"))
							list_rooms.add(MyApplication.getMyApplication().list_rooms.get(i));
					//String family = (String) sadp.getItem(p2);
					int size = Integer.parseInt(edit.getText().toString());
					if (size == 0) {
						Toast.makeText(Settings.this, "文字大小不能为零", Toast.LENGTH_LONG).show();
						return;
					}
					FontSetting option = new FontSetting();
					//String family = Config.get(getApplicationContext()).data.settings.remoteFontFamily;
					String family = MyApplication.getMyApplication().tmp_select_font;
					if (family == null)
						family = data.get(0);
					option.option.font_size = size;
					option.option.font_family = family;
					String text = new Gson().toJson(option, FontSetting.class);
					for (RoomData r: list_rooms) {
						Communication.getComm(getApplicationContext()).postWithAuth(Communication.SEND_MESSAGE, 
							Utils.ContentPut(Utils.ContentPut("gid", "" + r.gid), "text", text), 
							new StringCallback() {
								@Override
								public void onSuccess(Response<String> p1){}
							});
					}
				}
			})
			.show();
	}
	
	public void myFontLocal() {
		String[] disp = {"系统默认", "我也不知道什么体", "手写文字体"};
		final String[] fonts = {"default", "miao.ttf", "num.ttf"};
		new AlertDialog.Builder(Settings.this)
			.setTitle("设置本地显示文字")
			.setItems(disp, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(Settings.this);
					config.data.settings.font = fonts[p2];
					config.save();
				}
			})
			.show();
	}
	
	public void myFiles() {
		startActivity(new Intent().setClass(Settings.this, MyFiles.class));
	}
	
	public void mySetDefaultPrinter() {
		final EditText edit = new EditText(this);
		edit.setText(Config.get(this).data.settings.defaultPrinter);
		new AlertDialog.Builder(this).setTitle("设置默认打印机").setMessage("输入打印机用户名：")
			.setView(edit)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ContentValues params = new ContentValues();
					params.put("auth", Config.get(Settings.this).data.user.auth);
					params.put("friend", String.valueOf(edit.getText()));
					Communication.getComm(Settings.this).post(Communication.MAKE_FRIENDS, params, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> p1) {
								ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
								if (result.code == 0 || result.code == 8) {
									Config config = Config.get(getApplicationContext());
									config.data.settings.defaultPrinter = edit.getText().toString();
									config.save();
									new AlertDialog.Builder(Settings.this)
										.setMessage("设置成功")
										.show();
								}
								else {
									new AlertDialog.Builder(Settings.this)
										.setMessage(result.message + " (错误码: " + result.code + ")")
										.show();									
								}
							}
						});
				}
			})
			.setNegativeButton("取消", null)
			.show();
	}
	
	public void myAddPrinter() {
		makeFriends();
	}
	
	public static void myNewRoom(final Context context) {
		final EditText edit = new EditText(context);
		final CheckBox check = new CheckBox(context);
		check.setText("建立一个公共房间");
		check.setSelected(false);
		LinearLayout view = new LinearLayout(context);
		AlertDialog.Builder build = new AlertDialog.Builder(context);
		view.setOrientation(LinearLayout.VERTICAL);
		view.addView(edit);
		view.addView(check);
		build.setTitle("创建房间");
		build.setMessage("设置房间名字：");
		build.setView(view);
		build.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ContentValues parames = new ContentValues();
					parames.put("auth", Config.get(context).data.user.auth);
					parames.put("room_type", check.isChecked()?"all":"public");
					parames.put("name", edit.getText().toString());
					Communication.getComm(context).post(Communication.CREATE_ROOM, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> response) {
								ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
								if (result.code == 0) {
									Log.d("Chat 2", "Create Room: name:" + result.data.info.name + " gid: " + result.data.info.gid);
									//Settings.this.recreate();
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
	}
	
	public static void myJoinIn(final Context context) {
		final EditText edit = new EditText(context);
		new AlertDialog.Builder(context)
			.setTitle("加入房间")
			.setMessage("输入房间号码(gid):")
			.setView(edit)
			.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ContentValues parames = new ContentValues();
					parames.put("auth", Config.get(context).data.user.auth);
					parames.put("gid", edit.getText().toString());
					Communication.getComm(context).post(Communication.JOIN_IN, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> response) {
								ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
								if (result.code == 0) {
									//MainActivity.this.refresh();
								}
								else {
									new AlertDialog.Builder(context)
										.setMessage(result.message + " (Code: " + result.code + ")");
								}
							}
						});
				}
			})
			.setNegativeButton("取消", null)
			.show();
	}
	
	private void myJoinIn() {
		final EditText edit = new EditText(this);
		final Context context = getApplicationContext();
		new AlertDialog.Builder(this)
			.setTitle("加入房间")
			.setMessage("输入房间号码(gid):")
			.setView(edit)
			.setPositiveButton("OK", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ContentValues parames = new ContentValues();
					parames.put("auth", Config.get(Settings.this).data.user.auth);
					parames.put("gid", edit.getText().toString());
					Communication.getComm(context).post(Communication.JOIN_IN, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> response) {
								ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
								if (result.code == 0) {
									//Settings.this.recreate();
									new AlertDialog.Builder(Settings.this)
										.setMessage("加入成功")
										.show();
								}
								else {
									new AlertDialog.Builder(context)
										.setMessage(result.message + " (错误码: " + result.code + ")");
								}
							}
						});
				}
			})
			.setNegativeButton("取消", null)
			.show();
	}
	
	private void myNewRoom() {
		final EditText edit = new EditText(Settings.this);
		final CheckBox check = new CheckBox(this);
		check.setText("建立一个公共房间");
		check.setSelected(false);
		LinearLayout view = new LinearLayout(this);
		AlertDialog.Builder build = new AlertDialog.Builder(Settings.this);
		view.setOrientation(LinearLayout.VERTICAL);
		view.addView(edit);
		view.addView(check);
		build.setTitle("创建房间");
		build.setMessage("设置房间名字：");
		build.setView(view);
		build.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					ContentValues parames = new ContentValues();
					parames.put("auth", Config.get(Settings.this).data.user.auth);
					parames.put("room_type", check.isChecked()?"all":"public");
					parames.put("name", edit.getText().toString());
					Communication.getComm(Settings.this).post(Communication.CREATE_ROOM, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> response) {
								ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
								if (result.code == 0) {
									Log.d("Chat 2", "Create Room: name:" + result.data.info.name + " gid: " + result.data.info.gid);
									//Settings.this.recreate();
									new AlertDialog.Builder(Settings.this)
										.setMessage("创建房间成功，名字："+result.data.info.name+"，gid号："+result.data.info.gid).show();
								}
								else {
									Log.e("Chat 2", result.message + "(Code: " + result.code + ")");
									new AlertDialog.Builder(Settings.this)
										.setMessage(result.message + " (错误码: " + result.code + ")");
								}
							}
						});
				}
			});
		build.setNegativeButton("取消", null);
		build.show();
	}
	
	private void makeFriends() {
		final EditText edit = new EditText(Settings.this);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface p1, int p2) {
				ContentValues params = new ContentValues();
				params.put("auth", Config.get(Settings.this).data.user.auth);
				params.put("friend", String.valueOf(edit.getText()));
				Communication.getComm(Settings.this).post(Communication.MAKE_FRIENDS, params, 
					new StringCallback() {
						@Override
						public void onSuccess(Response<String> p1) {
							ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
							if (result.code != 0) {
								new AlertDialog.Builder(Settings.this)
									.setMessage(result.message + " (Code: " + result.code + ")")
									.show();
								return;
							}
							Settings.this.recreate();
						}
					});
			}
		};
		new AlertDialog.Builder(Settings.this)
			.setTitle("新朋友").setMessage("输入用户名：")
			.setView(edit)
			.setPositiveButton("确定", listener)
			.setNegativeButton("取消", null)
			.show();
		
	}
	
	private void mySetTheme() {
		final String[] disp = {"立春", "荷月", "惊蛰", "霜序", "岁馀", "小寒", "寒露"};
		final int[] vals = {R.style.AppTheme01, R.style.AppTheme02, R.style.AppTheme03, R.style.AppTheme04, R.style.AppTheme05, R.style.AppTheme06, R.style.AppTheme07};
		final int[] valsBg = {R.color.colorBg01, R.color.colorBg02, R.color.colorBg03, R.color.colorBg04, R.color.colorBg05, R.color.colorBg06, R.color.colorBg07};
		final int[] valsFt = {R.color.colorFt01, R.color.colorFt02, R.color.colorFt03, R.color.colorFt04, R.color.colorFt05, R.color.colorFt06, R.color.colorFt07};

		AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
		builder.setTitle("设置主题")
			.setItems(disp, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(Settings.this);
					config.data.settings.theme = vals[p2];
					config.data.settings.colorBg = valsBg[p2];
					config.data.settings.colorFt = valsFt[p2];
					config.save();
					Toast.makeText(Settings.this, "Change into theme: " + disp[p2], Toast.LENGTH_LONG).show();
					Settings.this.recreate();
				}
			});
		builder.show();
	}
	private void mySetServer() {
		String[] disp = {"远程(Release)", "调试(旧)", "本地(调试)", "Lance的远程服务器"};
		final String[] vals = {
			"https://lance-latina-debug.herokuapp.com/v3/api", 
			"https://lance-chatroom2.herokuapp.com/v3/api", 
			"http://0.0.0.0:5000/v3/api",
			"http://lanceliang2018.xyz:5000/v3/api"};
		AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
		builder.setTitle("设置服务器")
			.setItems(disp, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(Settings.this);
					config.data.settings.server = vals[p2];
					config.save();
					Toast.makeText(Settings.this, "服务器设置为 " + config.data.settings.server, Toast.LENGTH_LONG).show();
				}
			});
		builder.show();
	}
	
	private void myPersonInfo() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("username", Config.get(getApplicationContext()).data.user.username);
		bundle.putString("head_url", Config.get(getApplicationContext()).data.user.head);
		intent.putExtras(bundle);
		intent.setClass(getApplicationContext(), Person.class);
		MyApplication.getMyApplication().putObject("username", Config.get(getApplicationContext()).data.user.username);
		this.startActivity(intent);
	}
	
	private void mySetSplash() {
		new AlertDialog.Builder(Settings.this)
			.setTitle("设置开始动画")
			.setPositiveButton("显示", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(getApplicationContext());
					config.data.settings.isShowSplash = 1;
					config.save();
				}
			})
			.setNegativeButton("隐藏", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(getApplicationContext());
					config.data.settings.isShowSplash = 0;
					config.save();
				}
			})
			.show();
			
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == pick_bg && resultCode == RESULT_OK) {
			Uri uri = Matisse.obtainResult(data).get(0);
			ContentResolver cr2 = this.getContentResolver();  
			InputStream is = null;
			byte[] buf2 = null;
			try {
				is = cr2.openInputStream(uri);
				buf2 = new byte[is.available()];
				is.read(buf2);
				File bg = new File(getExternalFilesDir("Background").getAbsolutePath() + "/background");
				if (!bg.exists())
					bg.createNewFile();
				BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(bg));
				bis.write(buf2);
				bis.close();
				Toast.makeText(this, "请(完全)重启应用...", Toast.LENGTH_LONG).show();
			}
			catch (Exception e) { return; }
		}
	}
}

