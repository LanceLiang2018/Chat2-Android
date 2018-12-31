package lance.liang.chat2;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.util.concurrent.*;
import android.view.Window.*;
import java.io.*;
import okhttp3.*;
import android.util.*;

public class Signup extends Activity
{
	private Config config = new Config(this);
	private Button btn;
	private EditText text_username;
	private EditText text_password;
	private EditText text_email;
	private ProgressDialog.Builder builder;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		config.load();
		//setTheme(config.THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		btn = (Button)findViewById(R.id.signupButton_signup);
		text_username = (EditText)findViewById(R.id.signupEditText_username);
		text_password = (EditText)findViewById(R.id.signupEditText_password);
		text_email = (EditText)findViewById(R.id.signupEditText_email);
		
		btn.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1){
					//Toast.makeText(Signup.this, "TODO: Sign up " + String.valueOf(text_username.getText()), Toast.LENGTH_SHORT).show();
					builder = new ProgressDialog.Builder(Signup.this);
					builder.setMessage("Please wait...");
					builder.setCancelable(false);
					builder.show();
					HashMap<String, String> data = new HashMap<String, String>();
					data.put(CommunicationService.USERNAME, text_username.getText().toString());
					data.put(CommunicationService.EMAIL, text_email.getText().toString());
					data.put(CommunicationService.PASSWORD, text_password.getText().toString());
					data.put(CommunicationService.NAME, text_username.getText().toString());
					CommunicationService.getComm().post(CommunicationService.SIGNUP, data, new okhttp3.Callback() {
							@Override
							public void onFailure(Call p1, IOException p2)
							{
								Toast.makeText(Signup.this, "Failed. Check your Internet connections.", Toast.LENGTH_SHORT).show();
								
							}
							@Override
							public void onResponse(Call p1, Response p2) throws IOException
							{
								Log.d("Chat-2-Sign-up", p2.body().string());
							}
						});
				}
		});
	}
	
}

