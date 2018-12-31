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
	private Button btn;
	private EditText text_username;
	private EditText text_password;
	private EditText text_email;
	private AlertDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Config config = Config.get(this);
		//config.settings.server = "https://lance-chatroom2.herokuapp.com/";
		config.settings.theme = R.style.AppTheme2;
		config.save();
		setTheme(config.settings.theme);
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
					AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
					builder.setMessage("Please wait...");
					builder.setCancelable(false);
					dialog = builder.create();
					dialog.show();
					HashMap<String, String> data = new HashMap<String, String>();
					data.put(CommunicationService.USERNAME, text_username.getText().toString());
					data.put(CommunicationService.EMAIL, text_email.getText().toString());
					data.put(CommunicationService.PASSWORD, text_password.getText().toString());
					data.put(CommunicationService.NAME, text_username.getText().toString());
					CommunicationService.getComm(Signup.this).post(CommunicationService.SIGNUP, data, new okhttp3.Callback() {
							@Override
							public void onFailure(Call p1, IOException p2)
							{
								
							}
							@Override
							public void onResponse(Call p1, Response p2) throws IOException
							{
								
							}
						});
				}
		});
	}

	@Override
	protected void onDestroy()
	{
		dialog.dismiss();
		super.onDestroy();
	}
}

