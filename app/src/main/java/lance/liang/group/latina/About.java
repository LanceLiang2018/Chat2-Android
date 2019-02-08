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
					startActivity(new Intent().setClass(About.this, AboutPage.class));
					break;
				case ID.ABOUT_UPDATE:
					//Comm
					break;
				case ID.ABOUT_LEARN:
					break;
				case ID.ABOUT_FEEDBACK:
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

