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
import io.reactivex.*;
import io.reactivex.disposables.*;
import lance.liang.group.latina.MenuData.*;

import android.app.AlertDialog;
import android.support.v7.app.ActionBar;
import java.io.*;

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
					Toast.makeText(Settings.this, bean.item.title, Toast.LENGTH_LONG).show();
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
				case ID.SETTINGS_SERVER:
					mySetServer();
					setResult(0, new Intent().putExtra("command", "Recreate"));
					//Settings.this.finish();
					break;
				case ID.SETTINGS_FONT:
					Toast.makeText(Settings.this, bean.item.title, Toast.LENGTH_LONG).show();
					setResult(0, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.SETTINGS_SPLASH:
					mySetSplash();
					setResult(0, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.SETTINGS_THEME:
					mySetTheme();
					setResult(0, new Intent().putExtra("command", "Recreate"));
					//Settings.this.finish();
					break;
				case ID.PRINTER_ADD:
					myAddPrinter();
					setResult(0, new Intent().putExtra("command", "Refresh"));
					//Settings.this.finish();
					break;
				case ID.PRINTER_DEFAULT:
					mySetDefaultPrinter();
					setResult(0, new Intent().putExtra("command", "Refresh"));
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
									.capture(false)
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
	
	public void myFiles() {
		startActivity(new Intent().setClass(Settings.this, MyFiles.class));
	}
	
	public void mySetDefaultPrinter() {
		final EditText edit = new EditText(this);
		edit.setText(Config.get(this).data.settings.defaultPrinter);
		new AlertDialog.Builder(this).setTitle("Enter Defalut Printer Name:")
			.setView(edit)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(getApplicationContext());
					config.data.settings.defaultPrinter = edit.getText().toString();
					config.save();
				}
			})
			.setNegativeButton("Cancel", null)
			.show();
	}
	
	public void myAddPrinter() {
		makeFriends();
	}
	
	public static void myNewRoom(final Context context) {
		final EditText edit = new EditText(context);
		final CheckBox check = new CheckBox(context);
		check.setText("Establish a public room for all users");
		check.setSelected(false);
		LinearLayout view = new LinearLayout(context);
		AlertDialog.Builder build = new AlertDialog.Builder(context);
		view.setOrientation(LinearLayout.VERTICAL);
		view.addView(edit);
		view.addView(check);
		build.setTitle("Input the name of Room:");
		build.setView(view);
		build.setPositiveButton("OK", new AlertDialog.OnClickListener() {
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
			.setMessage("Input gid:")
			.setView(edit)
			.setPositiveButton("OK", new AlertDialog.OnClickListener() {
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
			.setNegativeButton("Cancel", null)
			.show();
	}
	
	private void myJoinIn() {
		final EditText edit = new EditText(this);
		final Context context = getApplicationContext();
		new AlertDialog.Builder(this)
			.setMessage("Input gid:")
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
									Settings.this.recreate();
								}
								else {
									new AlertDialog.Builder(context)
										.setMessage(result.message + " (Code: " + result.code + ")");
								}
							}
						});
				}
			})
			.setNegativeButton("Cancel", null)
			.show();
	}
	
	private void myNewRoom() {
		final EditText edit = new EditText(Settings.this);
		final CheckBox check = new CheckBox(this);
		check.setText("Establish a public room for all users");
		check.setSelected(false);
		LinearLayout view = new LinearLayout(this);
		AlertDialog.Builder build = new AlertDialog.Builder(Settings.this);
		view.setOrientation(LinearLayout.VERTICAL);
		view.addView(edit);
		view.addView(check);
		build.setTitle("Input the name of Room:");
		build.setView(view);
		build.setPositiveButton("OK", new AlertDialog.OnClickListener() {
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
									Settings.this.recreate();
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
			.setTitle("Enter username:")
			.setView(edit)
			.setPositiveButton("Yes", listener)
			.setNegativeButton("No", null)
			.show();
		
	}
	
	private void mySetTheme() {
		final String[] disp = {"默认", "荷月", "惊蛰", "霜序", "岁馀", "纯净", "Pure Blue"};
		final int[] vals = {R.style.AppTheme01, R.style.AppTheme02, R.style.AppTheme03, R.style.AppTheme04, R.style.AppTheme05, R.style.AppTheme06, R.style.AppTheme07};
		final int[] valsBg = {R.color.colorBg01, R.color.colorBg02, R.color.colorBg03, R.color.colorBg04, R.color.colorBg05, R.color.colorBg06, R.color.colorBg07};
		final int[] valsFt = {R.color.colorFt01, R.color.colorFt02, R.color.colorFt03, R.color.colorFt04, R.color.colorFt05, R.color.colorFt06, R.color.colorFt07};

		AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
		builder.setTitle("Set Theme")
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
		String[] disp = {"Remote", "Debug", "Local", "Lance's Server"};
		final String[] vals = {
			"https://lance-chatroom2.herokuapp.com/v3/api", 
			"https://lance-latina-debug.herokuapp.com/v3/api", 
			"http://0.0.0.0:5000/v3/api",
			"http://lanceliang2018.xyz:5000/v3/api"};
		AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
		builder.setTitle("Set Host")
			.setItems(disp, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(Settings.this);
					config.data.settings.server = vals[p2];
					config.save();
					Toast.makeText(Settings.this, "Change into host: " + config.data.settings.server, Toast.LENGTH_LONG).show();
				}
			});
		builder.show();
	}
	
	private void myAbout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
		builder.setTitle("About");
		builder.setMessage("https://github.com/LanceLiang2018/Chat2-Android/");
		builder.show();
	}
	
	private void myPersonInfo() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("username", Config.get(getApplicationContext()).data.user.username);
		bundle.putString("head_url", Config.get(getApplicationContext()).data.user.head);
		intent.putExtras(bundle);
		intent.setClass(getApplicationContext(), Person.class);
		this.startActivity(intent);
	}
	
	private void mySetSplash() {
		new AlertDialog.Builder(Settings.this)
			.setTitle("Set Splash")
			.setPositiveButton("Show", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					Config config = Config.get(getApplicationContext());
					config.data.settings.isShowSplash = 1;
					config.save();
				}
			})
			.setNegativeButton("Hide", new DialogInterface.OnClickListener() {
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
				Toast.makeText(this, "Restart the app please...", Toast.LENGTH_LONG).show();
			}
			catch (Exception e) { return; }
		}
	}
}

