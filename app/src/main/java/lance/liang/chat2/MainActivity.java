package lance.liang.chat2;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.support.v4.widget.*;
import android.widget.*;
import android.os.*;
import android.content.*;
import android.view.*;
import android.support.v7.app.*;
import java.util.*;
import android.util.*;
import lance.liang.chat2.MainActivity.*;
import lance.liang.chat2.MainAdapter;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout srl;
	private ListView list;
	private ListView left;
	private MainAdapter adp;
	private Config config = new Config(this);
	private CommunicationService comm;
	List<ItemBeanMain> data = new ArrayList<ItemBeanMain>();
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
		Log.d("Chat 2:: main", "Started.");
		config.load();
		//setTheme(config.THEME);

		comm = new CommunicationService();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		list = (ListView)findViewById(R.id.list_rooms);
		left = (ListView)findViewById(R.id.list_left);
		
		for (int i=1; i <= 100; i++)
			data.add(new ItemBeanMain(0, R.mipmap.ic_launcher, "Title " + i, "Content" + i));

		adp = new MainAdapter(this, data);
		
		list.setAdapter(adp);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
					Intent intent_room = new Intent();
					intent_room.setClass(MainActivity.this, Chat.class);
					Bundle bundle=new Bundle();
					bundle.putString("name", data.get(p3).title);
					bundle.putInt("gid", data.get(p3).gid);
					intent_room.putExtras(bundle);
					startActivityForResult(intent_room, 0);
				}
			});

		srl = (SwipeRefreshLayout)findViewById(R.id.slr);
		srl.setEnabled(true);
		
		List<ItemBeanLeft> left_data = new ArrayList<ItemBeanLeft>();
		for (int i=1; i<=20; i++)
			left_data.add(new ItemBeanLeft(R.drawable.image_1, "Title"));
		
		left.setAdapter(new LeftAdapter(this, left_data));
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
			case R.id.option_about:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("关于");
				builder.setMessage(R.string.app_name);
				builder.show();
				break;
			case R.id.option_login:
				Intent intent_login = new Intent();
				intent_login.setClass(MainActivity.this, Login.class);
				startActivityForResult(intent_login, 0);
				break;
			case R.id.option_signup:
				Intent intent_signup = new Intent();
				intent_signup.setClass(MainActivity.this, Signup.class);
				startActivityForResult(intent_signup, 0);
				break;
			case R.id.option_settings:
				try
				{
					//task = new TestTask();
					Toast.makeText(this, "Start...", Toast.LENGTH_LONG).show();
					comm.test();
				}
				catch (Exception e)
				{
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
