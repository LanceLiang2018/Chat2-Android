package lance.liang.chat2;

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
import lance.liang.chat2.*;

import android.support.v7.app.AlertDialog;

public class ImagePreView extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
		//String url = (String) p1.getTag();
		String url = getIntent().getExtras().getString("url");

		final ImageView imview = (ImageView) findViewById(R.id.imageviewImageView);
		imview.setImageResource(R.drawable.image_person_bg);

		Glide.with(this).load(url)
			.apply(new RequestOptions().placeholder(R.drawable.image_1)
				   .encodeQuality(100).override(1024, 1024))
			.into(imview);
		imview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					ImagePreView.this.finish();
				}
			});
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
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}

