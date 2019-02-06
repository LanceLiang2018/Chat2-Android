package lance.liang.group.latina;

import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class Splash extends AppCompatActivity
{
	@Override protected void onCreate(Bundle savedInstanceState){
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏 
		getSupportActionBar().hide();//隐藏标题栏 
		setContentView(R.layout.splash);
		
		VideoView vv = (VideoView) findViewById(R.id.splashVideoView);
		
		if (Config.get(this).data.settings.isShowSplash == 1)
		{
			Thread myThread = new Thread() {//创建子线程 
				@Override public void run() {
					try { 
						sleep(5000);//使程序休眠五秒 
						Intent it=  new Intent(getApplicationContext(), MainActivity.class);//启动MainActivity 
						startActivity(it);
						
						//Never Show
						Config config = Config.get(getApplicationContext());
						config.data.settings.isShowSplash = 0;
						config.save();
						
						finish();//关闭当前活动 
					}
					catch (Exception e)
					{ e.printStackTrace(); } 
				}
			};
			myThread.start();//启动线程 
		} else {
			Intent it = new Intent(getApplicationContext(), MainActivity.class);//启动MainActivity 
			startActivity(it);
			finish();//关闭当前活动 
			//return;
		}
		File video = new File(getExternalFilesDir("Splash").getAbsolutePath() + "/splash.mp4");
		if (!video.exists()) {
			try {
				InputStream is = getResources().getAssets().open("splash.mp4");
				byte[] buf = new byte[is.available()];
				is.read(buf);
				is.close();
				video.createNewFile();
				BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(video));
				bis.write(buf);
				bis.close();
			}
			catch (IOException e)
			{
				Log.e("Latina", e.getMessage());
				return;
			}
		}
		vv.setVideoPath(video.getAbsolutePath());
		vv.start();
		File bg = new File(getExternalFilesDir("Background").getAbsolutePath() + "/background");
		if (!bg.exists()) {
			try {
				InputStream is = getResources().getAssets().open("background");
				byte[] buf = new byte[is.available()];
				is.read(buf);
				is.close();
				bg.createNewFile();
				BufferedOutputStream bis = new BufferedOutputStream(new FileOutputStream(bg));
				bis.write(buf);
				bis.close();
			}
			catch (IOException e) {
				Log.e("Latina", e.getMessage());
				return;
			}
		}
		
	}

	@Override
	protected void onResume()
	{
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}
}
