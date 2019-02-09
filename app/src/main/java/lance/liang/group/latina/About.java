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
import lance.liang.group.latina.MenuData.ID;

public class About extends AppCompatActivity
{
	ListView list;
	private TextView license;
	private TextView ver;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setTitle("About");

		list = (ListView) findViewById(R.id.aboutListView);
		license = (TextView) findViewById(R.id.aboutTextView_license);
		ver = (TextView) findViewById(R.id.aboutTextView_version);
		
		ver.setText("V " + Utils.getVerName(this));
		license.setTextColor(Utils.getAccentColor(this));
		license.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					String server = Config.get(getApplicationContext()).data.settings.server;
					// https://lance-chatroom2.herokuapp.com/v3/api
					String url = server.substring(0, server.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com/v3
					url = url.substring(0, url.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com
					url = url + "/license";
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					startActivity(new Intent().setClass(About.this, AboutPage.class).putExtras(bundle));
				}
			});
		
		list.setAdapter(new SettingsAdapter(this, MenuData.listAbout));
		list.setOnItemClickListener(listener);
	}

	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			MenuData.Settings bean = (MenuData.Settings) p2.getTag();
			if (bean == null)
				return;
			switch (bean.id) {
				case ID.ABOUT_PAGE:
					String server3 = Config.get(getApplicationContext()).data.settings.server;
					// https://lance-chatroom2.herokuapp.com/v3/api
					String url3 = server3.substring(0, server3.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com/v3
					url3 = url3.substring(0, url3.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com
					url3 = url3 + "/about";
					Bundle bundle3 = new Bundle();
					bundle3.putString("url", url3);
					startActivity(new Intent().setClass(About.this, AboutPage.class).putExtras(bundle3));
					break;
				case ID.ABOUT_UPDATE:
					Communication.getComm(getApplicationContext()).postWithAuth(Communication.GET_VERSION, 
						new Content().val, new StringCallback() {
							@Override
							public void onSuccess(Response<String> p1) {
								ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
								if (result.code != 0) { Toast.makeText(About.this, result.message, Toast.LENGTH_LONG).show(); return; }
								float ver_new = Float.parseFloat(result.data.version);
								float ver_me = Float.parseFloat(Utils.getVerName(getApplicationContext()));
								if (ver_new <= ver_me) { Toast.makeText(About.this, "应用已是最新版本", Toast.LENGTH_LONG).show(); return; }
								Toast.makeText(About.this, "应用有更新: " + result.data.version, Toast.LENGTH_LONG).show();
								
								new AlertDialog.Builder(About.this)
									.setTitle("应用更新")
									.setMessage("是否下载应用?")
									.setNegativeButton("不要", null)
									.setPositiveButton("好的", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface p1, int p2) {
											DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
											String server = Config.get(getApplicationContext()).data.settings.server;
											// https://lance-chatroom2.herokuapp.com/v3/api
											String url = server.substring(0, server.lastIndexOf("/"));
											// https://lance-chatroom2.herokuapp.com/v3
											url = url.substring(0, url.lastIndexOf("/"));
											// https://lance-chatroom2.herokuapp.com
											url = url + "/update";
											Uri uri = Uri.parse(url);
											DownloadManager.Request request = new DownloadManager.Request(uri);
											request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
											request.setDestinationInExternalPublicDir(Config.get(getApplicationContext()).data.settings.savePath, "release.apk");
											request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
											downloadManager.enqueue(request);
										}
									})
									.show();
							}
						});
					break;
				case ID.ABOUT_LEARN:
					String server = Config.get(getApplicationContext()).data.settings.server;
					// https://lance-chatroom2.herokuapp.com/v3/api
					String url = server.substring(0, server.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com/v3
					url = url.substring(0, url.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com
					url = url + "/learn";
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					startActivity(new Intent().setClass(About.this, AboutPage.class).putExtras(bundle));
					break;
				case ID.ABOUT_SOURCE:
					String server2 = Config.get(getApplicationContext()).data.settings.server;
					// https://lance-chatroom2.herokuapp.com/v3/api
					String url2 = server2.substring(0, server2.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com/v3
					url2 = url2.substring(0, url2.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com
					url2 = url2 + "/source";
					Bundle bundle2 = new Bundle();
					bundle2.putString("url", url2);
					startActivity(new Intent().setClass(About.this, AboutPage.class).putExtras(bundle2));
					break;
				case ID.ABOUT_FEEDBACK:
					ContentValues params = new ContentValues();
					params.put("auth", Config.get(About.this).data.user.auth);
					params.put("friend", "Lance");
					Communication.getComm(About.this).post(Communication.MAKE_FRIENDS, params, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> p1) {
								ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
								if (result.code == 0 || result.code == 8) {
									Communication.getComm(About.this).postWithAuth(Communication.GET_ROOM_ALL, new Content().val, 
										new StringCallback() {
											@Override
											public void onSuccess(Response<String> p1) {
												final ResultData result2 = new Gson().fromJson(p1.body(), ResultData.class);
												if (result2.code != 0) { Toast.makeText(About.this, result2.message, Toast.LENGTH_LONG).show(); return; }
												int gid = 0;
												for (RoomData r: result2.data.room_data) {
													if (r.name.endsWith("|Lance")) {gid = r.gid; break; }
												}
												if (gid == 0) { Toast.makeText(About.this, "Error...", Toast.LENGTH_LONG).show(); return; }
												
												Bundle bundle = new Bundle();
												bundle.putInt("gid", gid);
												bundle.putString("name", "Feedback");
												startActivity(new Intent().setClass(About.this, Chat.class).putExtras(bundle));
											}
										});
								}
								else {
									new AlertDialog.Builder(About.this)
										.setMessage(result.message + " (错误码: " + result.code + ")")
										.show();									
								}
							}
						});
					break;
				default:
					break;
			}
		}
	};

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

