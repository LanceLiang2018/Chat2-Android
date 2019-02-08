package lance.liang.group.latina;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.request.*;

import android.support.v7.app.AlertDialog;
import com.bm.library.*;
//import com.bm.library.*;

public class ImagePreView extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏 
		
		setContentView(R.layout.image_view);
		//String url = (String) p1.getTag();
		String url = getIntent().getExtras().getString("url");

		final PhotoView imview = (PhotoView) findViewById(R.id.photo_view);
		imview.enable();
		//imview.setImageResource(R.drawable.image_person_bg);
		//final ImageView imview = (ImageView) findViewById(R.id.photo_view);

		Glide.with(this).load(url)
			.apply(new RequestOptions().placeholder(R.drawable.image_blank))
			.into(imview);
		//imview.setOnClickListener(new OnClickListener() {
		//		@Override
		//		public void onClick(View p1) {
		//			ImagePreView.this.finish();
		//		}
		//	});
		
		//imview.setOnTouchListener(new ImageZoomListenter());
		/*
		imview.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View p1) {
					new AlertDialog.Builder(ImagePreView.this).setItems(new String[] {"Save", "Star"}, 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								if (p2 == 0) {
									BitmapDrawable bd = (BitmapDrawable) imview.getDrawable();

								} else if (p2 == 1) {

								}
							}
						}).show();
					return false;
				}
			});
		*/
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}

class ImageZoomListenter implements OnTouchListener
{
	private int mode = 0;
	float oldDist;
	float sx = 0, sy = 0;
	ImageView imageView = null;
	float ax = 0, ay = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		imageView = (ImageView) v;
		if (sx == 0 || sy == 0)
		{
			sx = imageView.getScaleX();
			sy = imageView.getScaleY();
			ax = imageView.getTranslationX();
			ay = imageView.getTranslationY();
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
				if (mode == 1) {
					imageView.setTranslationX(event.getX(0));
					imageView.setTranslationY(event.getY(0));
					
				}
				break;
		}
		return true;
	}

	private void zoom(float f)
	{
		//imageView.setTextSize(textSize *= f);
		imageView.setScaleX(sx *= f);
		imageView.setScaleY(sy *= f);
	}

	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		//return new FloatMath.sqrt(x * x + y * y);
		return (float)Math.sqrt(x * x + y * y);
	}
}

