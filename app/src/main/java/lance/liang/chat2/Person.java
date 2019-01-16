package lance.liang.chat2;
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
import lance.liang.chat2.*;

import io.reactivex.Observer;
import lance.liang.chat2.Config;
import jp.wasabeef.glide.transformations.gpu.*;

public class Person extends AppCompatActivity
{
	String username = "", head_url = "", 
		motto = "", email = "", last_time = "", uid = "";
	
	ImageView image_head;
	TextView text_motto, text_email, text_username, text_last_time, text_uid;
	ImageView bg;
	
	final int code_pick = 0x61;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person);
		
		Bundle bundle = getIntent().getExtras();
		username = bundle.getString("username");
		head_url = bundle.getString("head_url");
		
		image_head = (ImageView) findViewById(R.id.personImageView_head);
		text_username = (TextView) findViewById(R.id.personTextView_username);
		text_motto = (TextView) findViewById(R.id.personTextView_sign);
		text_last_time = (TextView) findViewById(R.id.personTextView_last_login);
		text_uid = (TextView) findViewById(R.id.personTextView_uid);
		bg = (ImageView) findViewById(R.id.personImageView_bg);
		
		text_motto.setOnTouchListener(new TextZoomListenter());
		
		text_username.setText(username);
		
		Glide.with(this).load(head_url)
			.apply(new RequestOptions().placeholder(R.drawable.image_head)
				   .circleCrop())
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(image_head);
		Glide.with(this).load(head_url)
			.apply(new RequestOptions().placeholder(R.drawable.image_head)
				   .centerCrop().fitCenter().transform(new PixelationFilterTransformation()))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(bg);
			
		image_head.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
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
								if (aBoolean)
								{
									Matisse.from(Person.this)
										.choose(MimeType.ofImage(), false)
										.countable(false)
										.capture(false)
										.maxSelectable(1)
										.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
										.thumbnailScale(0.85f)
										//.originalEnable(true)
										//.maxOriginalSize(10)
										.autoHideToolbarOnSingleTap(true)
										.forResult(code_pick);
								}
							}});
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
			Uri uri = data.getData();
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
				parames.put("data", b64);
				//parames.put("md5", md5);
				Communication.getComm(Person.this).post(Communication.UPLOAD, parames, 
					new StringCallback() {
						@Override
						public void onSuccess(Response<String> p1) {
							if (p1.code() != 200)
								return;
							ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
							if (result.code == 0) {
								
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

class TextZoomListenter implements OnTouchListener
{
	private int mode = 0;
	float oldDist;
	float textSize = 0;
	TextView textView = null;

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		textView = (TextView) v;
		if (textSize == 0)
		{
			textSize = textView.getTextSize();
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				mode = 1;
				break;
			case MotionEvent.ACTION_UP:
				mode = 0;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				mode -= 1;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				mode += 1;
				break;

			case MotionEvent.ACTION_MOVE:
				if (mode >= 2)
				{
					float newDist = spacing(event);
					if (newDist > oldDist + 1)
					{
						zoom(newDist / oldDist);
						oldDist = newDist;
					}
					if (newDist < oldDist - 1)
					{
						zoom(newDist / oldDist);
						oldDist = newDist;
					}
				}
				break;
		}
		return true;
	}

	private void zoom(float f)
	{
		textView.setTextSize(textSize *= f);
	}

	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		//return new FloatMath.sqrt(x * x + y * y);
		return (float)Math.sqrt(x * x + y * y);
	}
}

