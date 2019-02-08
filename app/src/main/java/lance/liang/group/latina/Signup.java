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
import android.view.View.*;

public class Signup extends AppCompatActivity
{
	private Button btn;
	private EditText text_username;
	private EditText text_password;
	private EditText text_email;
	private AlertDialog dialog;
	private EditText text_conform;
	private TextView text_license;
	private CheckBox check_read;
	private CheckBox check_printer;
	
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
		text_license = (TextView) findViewById(R.id.signupTextView_license);
		
		check_read = (CheckBox) findViewById(R.id.signupCheckBox_read_it);
		check_printer = (CheckBox) findViewById(R.id.signupCheckBox_is_printer);
		
		check_read.setChecked(true);
		//check_read.setText("I have read License");
		text_license.setTextColor(Utils.getAccentColor(Signup.this));
		text_license.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					String server = Config.get(getApplicationContext()).data.settings.server;
					// https://lance-chatroom2.herokuapp.com/v3/api
					String url = server.substring(0, server.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com/v3
					url = url.substring(0, url.lastIndexOf("/"));
					// https://lance-chatroom2.herokuapp.com
					url = url + "/license";
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					startActivity(new Intent().setClass(Signup.this, AboutPage.class).putExtras(bundle));
				}
			});
		
		ActionBar bar = getSupportActionBar();
		bar.setTitle("用户注册");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		
		ImageView bg = (ImageView) findViewById(R.id.signupImageView_bg);
		Glide.with(this).load(R.drawable.image_background_2)
			.apply(new RequestOptions()
				   .centerCrop()
				   .transform(new BlurTransformation(20)))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(bg);
		
		btn.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1) {
					if (!check_read.isChecked()) {
						new AlertDialog.Builder(Signup.this).setMessage("请先阅读用户协议").show();
						return;
					}
					String s1 = text_password.getText().toString();
					String s2 = text_conform.getText().toString();
					if (!s1.equals(s2)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
						builder.setMessage("两次密码不一致");
						builder.show();
						return;
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
					builder.setMessage("正在加载");
					builder.setCancelable(false);
					dialog = builder.create();
					dialog.show();
					ContentValues parames = new ContentValues();
					parames.put(Communication.USERNAME, text_username.getText().toString());
					parames.put(Communication.EMAIL, text_email.getText().toString());
					parames.put(Communication.PASSWORD, text_password.getText().toString());
					parames.put(Communication.NAME, text_username.getText().toString());
					if (check_printer.isChecked())
						parames.put("user_type", "printer");
					Communication.getComm(Signup.this).post(Communication.SIGNUP, parames, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> response) {
								dialog.hide();
								ResultData result = (new Gson()).fromJson(response.body().toString(), ResultData.class);
								if (result.code == 0)
								{
									AlertDialog.Builder build = new AlertDialog.Builder(Signup.this);
									build.setMessage("注册成功");
									build.setPositiveButton("确认", new AlertDialog.OnClickListener() {
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
									build.setMessage(result.message + " (错误码: " + result.code + ")");
									build.setPositiveButton("了解", null);
									build.show();
								}
							}
							@Override
							public void onError(Response<String> response) {
								dialog.hide();
								AlertDialog.Builder build = new AlertDialog.Builder(Signup.this);
								build.setMessage("网络连接错误。检查网络。");
								build.setPositiveButton("了解", null);
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

