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

public class Settings extends AppCompatActivity
{
	ListView list;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		
		//Bundle bundle = getIntent().getExtras();
		//MenuData.Settings[] data = (MenuData.Settings[]) bundle.get("data");
		MenuData.Settings[] data = (MenuData.Settings[]) MyApplication.getMyApplication().getObject("data");
		
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
				case MenuData.ID.ME_MY_INFO:
					Toast.makeText(Settings.this, bean.item.title, Toast.LENGTH_LONG).show();
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
}

