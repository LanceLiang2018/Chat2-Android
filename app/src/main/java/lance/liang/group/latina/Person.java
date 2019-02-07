package lance.liang.group.latina;
import android.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.drawable.*;
import com.bumptech.glide.request.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.tbruyelle.rxpermissions2.*;
import com.zhihu.matisse.*;
import io.reactivex.*;
import io.reactivex.disposables.*;
import java.io.*;
import java.text.*;
import java.util.*;
import jp.wasabeef.glide.transformations.*;

import io.reactivex.Observer;
import jp.wasabeef.glide.transformations.gpu.*;
import com.zhihu.matisse.internal.entity.*;

public class Person extends AppCompatActivity
{
	String username = "", head_url = "", 
		motto = "", email = "", last_time = "", uid = "";
	
	ImageView image_head;
	TextView text_motto, text_email, text_username, text_last_time, text_uid;
	ImageView bg;
	Button make_friends;
	
	final int code_pick = 0x61;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person);
		
		//Bundle bundle = getIntent().getExtras();
		//username = bundle.getString("username");
		//head_url = bundle.getString("head_url");
		username = (String) MyApplication.getMyApplication().getObject("username");
		//head_url = (String) MyApplication.getMyApplication().getObject("head_url");
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View frame = inflater.inflate(R.layout.person_frame, null);

		image_head = (ImageView) frame.findViewById(R.id.personImageView_head);
		text_username = (TextView) frame.findViewById(R.id.personTextView_username);
		text_motto = (TextView) frame.findViewById(R.id.personTextView_motto);
		
		
		LinearLayout head_view = (LinearLayout) findViewById(R.id.personLinearLayout_headview);
		head_view.addView(frame);

		//text_motto = (TextView) findViewById(R.id.personTextView_motto);
		text_email = (TextView) findViewById(R.id.personTextView_email);
		text_last_time = (TextView) findViewById(R.id.personTextView_last_login);
		text_uid = (TextView) findViewById(R.id.personTextView_uid);
		bg = (ImageView) findViewById(R.id.personImageView_bg);
		make_friends = (Button) findViewById(R.id.personButton_say);
		
		if (username.equals(Config.get(this).data.user.username))
			make_friends.setVisibility(View.GONE);
		
		text_username.setText(username);
		
		Communication.getComm(this).postWithAuth(Communication.GET_USER, new Content().put("username", username).val, 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
					if (result.code != 0) {
						Toast.makeText(Person.this, result.message, Toast.LENGTH_LONG).show();
						return;
					}
					head_url = result.data.user_info.head;
					Glide.with(Person.this).load(head_url)
						.apply(new RequestOptions().placeholder(R.drawable.image_blank)
							   .circleCrop())
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(image_head);
					Glide.with(Person.this).load(head_url)
						.apply(new RequestOptions().placeholder(R.drawable.image_blank)
							   .centerCrop().fitCenter().transform(new BlurTransformation(20)))
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(bg);
					text_motto.setText(result.data.user_info.motto);
					text_email.setText(result.data.user_info.email);
					text_last_time.setText(new MyGetTime().remote(result.data.user_info.last_active_time));
					text_uid.setText("" + result.data.user_info.uid);
				}
			});
			
		image_head.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					if (username.equals(Config.get(getApplicationContext()).data.user.username)) {
						RxPermissions rxPermissions = new RxPermissions(Person.this);
						rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
							.subscribe(new Observer<Boolean>() {
								@Override
								public void onError(Throwable p1)
								{}
								@Override
								public void onComplete()
								{}
								@Override
								public void onSubscribe(Disposable d)
								{}
								@Override
								public void onNext(Boolean aBoolean)
								{
									if (aBoolean) {
										Matisse.from(Person.this)
											.choose(MimeType.ofImage(), false)
											.countable(false)
											.capture(true)
											.captureStrategy(new CaptureStrategy(true, "lance.liang.group.latina.provider"))
											.maxSelectable(1)
											.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
											.thumbnailScale(0.85f)
											//.originalEnable(true)
											.maxOriginalSize(2)
											.autoHideToolbarOnSingleTap(true)
											.forResult(code_pick);
									}
								}});
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("url", head_url);
						startActivity(new Intent().setClass(Person.this, ImagePreView.class).putExtras(bundle));
					}
				}
			});
		
		make_friends.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					new AlertDialog.Builder(Person.this)
						.setMessage("和Ta加为好友？")
						.setNegativeButton("不要", null)
						.setPositiveButton("好的", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								Communication.getComm(getApplicationContext()).postWithAuth(Communication.MAKE_FRIENDS, 
									Utils.ContentPut("friend", text_username.getText().toString()), 
									new StringCallback() {
										@Override
										public void onSuccess(Response<String> p1) {
											ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
											if (result.code == 0)
												new AlertDialog.Builder(getApplicationContext())
													.setMessage("好了")
													.show();
										}
									});
							}
						})
						.show();
				}
			});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == code_pick) {
			//Uri uri = data.getData();
			Uri uri = Matisse.obtainResult(data).get(0);
			String path = Matisse.obtainPathResult(data).get(0);
			Toast.makeText(this, "Got Uri: " + uri, Toast.LENGTH_LONG).show();
			ContentResolver cr = this.getContentResolver();  
			try {
				InputStream is = cr.openInputStream(uri);
				byte[] buf = new byte[is.available()];
				is.read(buf);
				//String md5 = MD5Utils.byteArrayToHexString(buf);
				String b64 = Base64.encodeToString(buf, Base64.DEFAULT);

				ContentValues parames = new ContentValues();
				parames.put("auth", Config.get(Person.this).data.user.auth);
				parames.put("filename", path.substring(path.lastIndexOf("/")+1, path.length()));
				parames.put("data", b64);
				//parames.put("md5", md5);
				Communication.getComm(Person.this).post(Communication.UPLOAD, parames, 
					new StringCallback() {
						@Override
						public void onSuccess(Response<String> p1) {
							if (p1.code() != 200)
								return;
							final ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
							if (result.code == 0) {
								ContentValues params2 = new ContentValues();
								params2.put("head", result.data.upload_result.url);
								Communication.getComm(Person.this).postWithAuth(Communication.SET_USER, params2, 
									new StringCallback() {
										@Override
										public void onSuccess(Response<String> p1) {
											Config config = Config.get(getApplicationContext());
											config.data.user.head = result.data.upload_result.url;
											config.save();
											Person.this.recreate();
										}
									});
							}
						}
					});
			}
			catch (Exception e) {
				Toast.makeText(Person.this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
			
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}

