package lance.liang.group.latina;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.request.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import jp.wasabeef.glide.transformations.*;

public class Signup extends AppCompatActivity
{
	private Button btn;
	private EditText text_username;
	private EditText text_password;
	private EditText text_email;
	private AlertDialog dialog;
	private EditText text_conform;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		btn = (Button)findViewById(R.id.signupButton_signup);
		text_username = (EditText)findViewById(R.id.signupEditText_username);
		text_password = (EditText)findViewById(R.id.signupEditText_password);
		text_email = (EditText)findViewById(R.id.signupEditText_email);
		text_conform = (EditText)findViewById(R.id.signupEditText_conform);
		
		ActionBar bar = getSupportActionBar();
		bar.setTitle("Sign up");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		
		ImageView bg = (ImageView) findViewById(R.id.signupImageView_bg);
		Glide.with(this).load(R.drawable.image_background_2)
			.apply(new RequestOptions()
				   .centerCrop()
				   .transform(new BlurTransformation(20)))
			.into(bg);
		
		btn.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1) {
					String s1 = text_password.getText().toString();
					String s2 = text_conform.getText().toString();
					if (!s1.equals(s2)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
						builder.setMessage("Password do not ... Please Check.");
						builder.show();
						return;
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
					builder.setMessage("Please wait...");
					builder.setCancelable(false);
					dialog = builder.create();
					dialog.show();
					ContentValues parames = new ContentValues();
					parames.put(Communication.USERNAME, text_username.getText().toString());
					parames.put(Communication.EMAIL, text_email.getText().toString());
					parames.put(Communication.PASSWORD, text_password.getText().toString());
					parames.put(Communication.NAME, text_username.getText().toString());
					Communication.getComm(Signup.this).post(Communication.SIGNUP, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> response) {
								dialog.hide();
								ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
								if (result.code == 0)
								{
									AlertDialog.Builder build = new AlertDialog.Builder(Signup.this);
									build.setMessage("Sign up successfully.");
									build.setPositiveButton("OK", new AlertDialog.OnClickListener() {
											@Override
											public void onClick(DialogInterface p1, int p2) {
												Signup.this.finish();
											}
										});
									build.show();
								}
								else
								{
									AlertDialog.Builder build = new AlertDialog.Builder(Signup.this);
									build.setMessage(result.message + " (Code: " + result.code + ")");
									build.setPositiveButton("OK", null);
									build.show();
								}
							}
							@Override
							public void onError(Response<String> response) {
								dialog.hide();
								AlertDialog.Builder build = new AlertDialog.Builder(Signup.this);
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
		//dialog.dismiss();
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

