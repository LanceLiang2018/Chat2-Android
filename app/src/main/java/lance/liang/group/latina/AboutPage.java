package lance.liang.group.latina;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.drawable.*;
import com.bumptech.glide.request.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import jp.wasabeef.glide.transformations.*;
import android.webkit.*;

public class AboutPage extends AppCompatActivity
{
	private WebView web;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_view);
		
		ActionBar bar = getSupportActionBar();
		bar.setTitle("About us");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		
		String url = "http://lance-chatroom2.herokuapp.com/";
		try {
			url = getIntent().getExtras().getString("url");
		} catch (Exception e) { return; }
		
		web = (WebView) findViewById(R.id.aboutviewWebView);
		
		//支持javascript
		web.getSettings().setJavaScriptEnabled(true);
		// 设置可以支持缩放
		web.getSettings().setSupportZoom(true);
		// 设置出现缩放工具
		web.getSettings().setBuiltInZoomControls(true);
		//扩大比例的缩放
		web.getSettings().setUseWideViewPort(true);
		//自适应屏幕
		web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		
		//web.loadUrl(Config.get(this).data.settings.server);
		web.loadUrl(url);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
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
