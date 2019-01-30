package lance.liang.group.latina;

import android.app.AlertDialog;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.util.concurrent.*;
import android.view.Window.*;
import java.io.*;
import android.util.*;
import android.content.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.google.gson.*;
import android.support.v7.app.*;
import android.widget.AdapterView.*;
import lance.liang.group.latina.MenuData.ID;

public class Settings extends AppCompatActivity
{
	ListView list;
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
					Settings.this.finish();
					break;
				case ID.ME_SET_INFO:
					Toast.makeText(Settings.this, bean.item.title, Toast.LENGTH_LONG).show();
					break;
				case ID.ME_MAKE_FRIENDS:
					makeFriends();
					break;
				case ID.ME_NEW_ROOM:
					myNewRoom();
					break;
				case ID.ME_NEW_USER:
					Intent intent_signup = new Intent();
					intent_signup.setClass(Settings.this, Signup.class);
					startActivity(intent_signup);
					break;
				case ID.SETTINGS_SERVER:
					mySetServer();
					break;
				case ID.SETTINGS_FONT:
					Toast.makeText(Settings.this, bean.item.title, Toast.LENGTH_LONG).show();
					break;
				case ID.SETTINGS_SPLASH:
					mySetSplash();
					break;
				case ID.SETTINGS_THEME:
					mySetTheme();
					break;
				case ID.PRINTER_ADD:
					myAddPrinter();
					break;
				case ID.PRINTER_DEFAULT:
					mySetDefaultPrinter();
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
		final String[] disp = {"默认", "荷月", "惊蛰", "霜序", "岁馀", "纯净", "夜行"};
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
		String[] disp = {"Remote", "Local", "Lance's Server"};
		final String[] vals = {
			"https://lance-chatroom2.herokuapp.com/v3/api", 
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
}

