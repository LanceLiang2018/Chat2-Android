package lance.liang.group.latina;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;

public class Splash extends AppCompatActivity
{
	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏 
		getSupportActionBar().hide();//隐藏标题栏 
		setContentView(R.layout.splash);
		
		//ImageView im = (ImageView) findViewById(R.id.splashImageView);
		if (Config.get(this).data.settings.isShowSplash == 1)
		{
			Thread myThread = new Thread() {//创建子线程 
				@Override public void run() {
					try { 
						sleep(5000);//使程序休眠五秒 
						Intent it=  new Intent(getApplicationContext(), MainActivity.class);//启动MainActivity 
						startActivity(it);
						finish();//关闭当前活动 
					}
					catch (Exception e)
					{ e.printStackTrace(); } 
				}
			};
		
			myThread.start();//启动线程 
		}
	}
}