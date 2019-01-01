package lance.liang.chat2;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;

public class Login extends Activity
{
	private TextView text_username;
	private TextView text_password;
	private Button btn_login;
	private Button btn_signup;
	private AlertDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).settings.theme);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		//Toast.makeText(this, "Login Activity", Toast.LENGTH_SHORT);

		text_username = (TextView)findViewById(R.id.loginEditText_username);
		text_password = (TextView)findViewById(R.id.loginEditText_password);
		btn_login = (Button)findViewById(R.id.loginButton_login);
		btn_signup = (Button)findViewById(R.id.loginButton_signup);

		btn_signup.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					Intent intent = new Intent();
					intent.setClass(Login.this, Signup.class);
					startActivity(intent);
				}
			});
		btn_login.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1){
					AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
					builder.setMessage("Please wait...");
					builder.setCancelable(false);
					dialog = builder.create();
					dialog.show();
					ContentValues parames = new ContentValues();
					parames.put(Communication.USERNAME, text_username.getText().toString());
					parames.put(Communication.PASSWORD, text_password.getText().toString());
					parames.put(Communication.NAME, text_username.getText().toString());
					Communication.getComm(Login.this).post(Communication.LOGIN, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> response) {
								dialog.hide();
//								Toast.makeText(Login.this, response.body().toString(), Toast.LENGTH_LONG).show();
								ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
								if (result.code == 0)
								{
									Config config = Config.get(Login.this);
									config.user.auth = result.data.auth;
									config.user.username = text_username.getText().toString();
									config.user.head = result.data.head;
									config.save();
									
									Intent intent = new Intent();
									intent.putExtra("command", "Refresh");
									setResult(0, intent);
									Toast.makeText(Login.this, "Login successfully. Got auth: " + config.user.auth, Toast.LENGTH_SHORT).show();
									Login.this.finish();
								}
								else
								{
									AlertDialog.Builder build = new AlertDialog.Builder(Login.this);
									build.setMessage(result.message + " (Code: " + result.code + ")");
									build.setPositiveButton("OK", null);
									build.show();
								}
							}
							@Override
							public void onError(Response<String> response) {
								dialog.hide();
								AlertDialog.Builder build = new AlertDialog.Builder(Login.this);
								build.setMessage("Connection Errors.");
								build.setPositiveButton("OK", null);
								build.show();
							}
						});
				}
			});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
