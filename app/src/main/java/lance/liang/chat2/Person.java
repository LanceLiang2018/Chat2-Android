package lance.liang.chat2;
import android.os.*;
import android.support.v7.app.*;
import android.widget.*;

public class Person extends AppCompatActivity
{
	String username = "", head_url = "", 
		motto = "", email = "", last_time = "", uid = "";
	
	ImageView image_head;
	TextView text_motto, text_email, text_username, text_last_time, text_uid;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person);
		
		Bundle bundle = getIntent().getExtras();
		username = bundle.getString("username");
		
		image_head = (ImageView) findViewById(R.id.personImageView_head);
		text_username = (TextView) findViewById(R.id.personTextView_username);
		text_motto = (TextView) findViewById(R.id.personTextView_sign);
		text_last_time = (TextView) findViewById(R.id.personTextView_last_login);
		text_uid = (TextView) findViewById(R.id.personTextView_uid);
		
		text_username.setText(username);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
