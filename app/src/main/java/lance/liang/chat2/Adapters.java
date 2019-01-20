package lance.liang.chat2;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.v4.graphics.drawable.*;
import android.support.v7.appcompat.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.engine.*;
import com.bumptech.glide.load.resource.drawable.*;
import com.bumptech.glide.request.*;
import com.bumptech.glide.request.target.*;
import com.bumptech.glide.request.transition.*;
import com.lzy.okserver.*;
import com.lzy.okserver.upload.*;
import java.io.*;
import java.util.*;

import android.support.v7.appcompat.R;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
//import android.transition.*;
//import android.support.v7.app.*;

class MainAdapter extends BaseAdapter
{
	public List<ItemBeanMain> list;
	private LayoutInflater inflater;
	private Context pcontext;

	MainAdapter(Context context, List<ItemBeanMain> ilist) {
		this.list = ilist;
		inflater = LayoutInflater.from(context);
		pcontext = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int p1) {
		return list.get(p1);
	}

	@Override
	public long getItemId(int p1) {
		return p1;
	}
	
	public void insert(ItemBeanMain data) {
		list.add(data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_main, null);
		ItemBeanMain bean = list.get(position);
		ImageView im = (ImageView) view.findViewById(R.id.itemmainImageView);
		//im.setImageResource(bean.image);
		((TextView) view.findViewById(R.id.itemmainTextView_title)).setText(bean.title);
		((TextView) view.findViewById(R.id.itemmainTextView_content)).setText(bean.content);
		TextView text_unread = (TextView) view.findViewById(R.id.itemmainTextView_unread);
		TextView text_time = (TextView) view.findViewById(R.id.itemmainTextView_time);
		
		
		text_time.setText(bean.time);
		if (bean.unread != 0)
			text_unread.setText("" + bean.unread);
		else
			text_unread.setVisibility(View.GONE);
			
		Glide.with(pcontext).load(bean.head)
			.apply(new RequestOptions().circleCrop())
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(im);
		
		return view;
	}
}

class ChatAdapter extends BaseAdapter
{
	public List<ItemBeanChat> list;
	private LayoutInflater inflater;
	Context pcontext;

	ChatAdapter(Context context, List<ItemBeanChat> ilist) {
		this.list = ilist;
		inflater = LayoutInflater.from(context);
		pcontext = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int p1) {
		return list.get(p1);
	}

	@Override
	public long getItemId(int p1) {
		return p1;
	}
	
	public void insert(ItemBeanChat data) {
		list.add(data);
	}
	
	public void insert_back(ItemBeanChat data) {
		list.add(0, data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null, frame = null;
		final ItemBeanChat bean = list.get(position);
		
		if (bean.username.equals(Config.get(pcontext).data.user.username))
			view = inflater.inflate(R.layout.item_chat_frame_me, null);
		else
			view = inflater.inflate(R.layout.item_chat_frame, null);
		
		((TextView) view.findViewById(R.id.itemchatframeTextView_username)).setText(bean.username);
		((TextView) view.findViewById(R.id.itemchatframeTextView_time)).setText(bean.time);
		ImageView head = (ImageView) view.findViewById(R.id.itemchatframeImageView_head);
		
		Glide.with(pcontext)
			.load(bean.head_url)
			.apply(new RequestOptions().circleCrop().placeholder(R.drawable.image_blank))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(head);

		if (bean.type.equals("text")) {
			frame = inflater.inflate(R.layout.item_frame_text, null);
			TextView message = (TextView) frame.findViewById(R.id.itemframetextTextView_message);
			LinearLayout box = (LinearLayout) frame.findViewById(R.id.itemframetextLinearLayout_box);
			
			message.setText(bean.message);
			//box.setBackgroundResource(Config.get(pcontext).data.settings.colorBg);
			//message.setTextColor(Config.get(pcontext).data.settings.colorFt);

			try {
				RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(pcontext.getResources(), 
							new BitmapFactory().decodeResource(pcontext.getResources(), R.drawable.image_box_bg));
				roundedBitmapDrawable1.setCornerRadius(35);
				box.setBackgroundDrawable(roundedBitmapDrawable1);
			}
			catch (Exception e) {
				Log.e("Chat 2", e.getMessage());
			}
		}
		if (bean.type.equals("image"))
		{
			frame = inflater.inflate(R.layout.item_frame_image, null);
			final ImageView image = (ImageView) frame.findViewById(R.id.itemframeimageImageView);
			//image.setTag(bean.message);
			
			frame.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View p1) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("url", bean.message);
						intent.setClass(pcontext, ImagePreView.class);
						intent.putExtras(bundle);
						pcontext.startActivity(intent);
					}
				});

			SimpleTarget target = new SimpleTarget<Drawable>() {
				@Override
				public void onResourceReady(Drawable p1, Transition<? super Drawable> p2) {
					if (bean.message.toLowerCase().endsWith(".gif")) {
						image.setImageDrawable(p1);
					}
					try {
						RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(pcontext.getResources(), 
																										   ((BitmapDrawable) p1).getBitmap());
						roundedBitmapDrawable1.setCornerRadius(30);
						image.setImageDrawable(roundedBitmapDrawable1);
					} catch (Exception e) {
						image.setImageDrawable(p1);
					}
				}
			};

			Glide.with(pcontext)
				//.asBitmap()
				.load(bean.message)
				.apply(new RequestOptions()
					   .placeholder(R.drawable.image_box_bg)
					   .override(300)
					   .diskCacheStrategy(DiskCacheStrategy.ALL))
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(target);
		}
		if (bean.type.equals("file")) {
			frame = inflater.inflate(R.layout.item_frame_file, null);
			TextView filename = (TextView) frame.findViewById(R.id.itemframefileTextView_filename);
			TextView filesize = (TextView) frame.findViewById(R.id.itemframefileTextView_filesize);
			
			filename.setText(bean.message);
			filesize.setText("0.34 Kb");
			frame.setBackgroundResource(Config.get(pcontext).data.settings.colorBg);
			
			frame.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View p1) {
						DownloadManager downloadManager = (DownloadManager) pcontext.getSystemService(Context.DOWNLOAD_SERVICE);
						if (bean.tag == null)
							return;
						Uri url = Uri.parse(bean.tag);
						DownloadManager.Request request = new DownloadManager.Request(url);
						request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
						request.setDestinationInExternalPublicDir("/sdcard/", "filename");
						request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
						downloadManager.enqueue(request);
					}
				});

			try {
				RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(pcontext.getResources(), 
																								   new BitmapFactory().decodeResource(pcontext.getResources(), R.drawable.image_box_bg));
				roundedBitmapDrawable1.setCornerRadius(35);
				frame.setBackgroundDrawable(roundedBitmapDrawable1);
			}
			catch (Exception e) {
				Log.e("Chat 2", e.getMessage());
			}
		}
		
		head.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					Bundle bundle = new Bundle();
					bundle.putString("username", bean.username);
					bundle.putString("head_url", bean.head_url);
					Intent intent = new Intent();
					intent.setClass(pcontext, Person.class);
					intent.putExtras(bundle);
					pcontext.startActivity(intent);
				}
			});
		
		View gview = inflater.inflate(R.layout.item_loading, null);
		LinearLayout back = (LinearLayout) gview.findViewById(R.id.itemloadingLinearLayout_back);
		back.addView(frame);
		//((TextView) gview.findViewById(R.id.itemloadingTextView_progress)).setTextColor(Config.get(pcontext).data.settings.colorFt);
		if (bean.status == ItemBeanChat.DONE) {
			((RelativeLayout) gview.findViewById(R.id.itemloadingRelativeLayout_main)).setVisibility(View.GONE);
		}
		((LinearLayout) view.findViewById(R.id.itemchatframeLinearLayout_content)).addView(gview);
		
		if (bean.tag != null) {
			if (bean.status == bean.SENDING) {
				OkUpload ok = OkUpload.getInstance();
				UploadTask task = ok.getTask(bean.tag);
				if (task == null) {
					ContentResolver cr = pcontext.getContentResolver();
					Uri uri = Uri.parse(bean.tag);
					String b64 = null;
					try {
						InputStream is = cr.openInputStream(uri);
						byte[] buf = new byte[is.available()];
						is.read(buf);
						b64 = Base64.encodeToString(buf, Base64.DEFAULT);
					} catch (Exception e) {
						Log.e("Chat 2", e.getMessage());
						Toast.makeText(pcontext, e.getMessage(), Toast.LENGTH_LONG).show();
						return view;
					}

					ContentValues params = new ContentValues();
					params.put("auth", Config.get(pcontext).data.user.auth);
					params.put("data", b64);
					task = Communication.getComm(pcontext).upload(bean.tag, Communication.UPLOAD, params, 
						new StringCallback() {
							@Override
							public void onSuccess(Response<String> p1) {
								
							}
						}, 
						new UploadListener<String>("Tag") {
							@Override
							public void onStart(Progress p1){
								Toast.makeText(pcontext, "Upload Started.", Toast.LENGTH_LONG).show();
							}

							@Override
							public void onProgress(Progress p1) {
								Log.i("Chat 2 Upload Progress", "" + p1.fraction * 100 + "%");
							}

							@Override
							public void onError(Progress p1) {}
							@Override
							public void onFinish(String p1, Progress p2) {}
							@Override
							public void onRemove(Progress p1) {}
						});
					task.start();
				}
			}
		}
		
		return view;
	}
}

class LeftAdapter extends BaseExpandableListAdapter
{
	private List<ItemBeanLeft> list;
	private List<ItemBeanLeft[]> child;
	private LayoutInflater inflater;

	@Override
	public int getGroupCount()
	{
		return list.size();
	}

	@Override
	public int getChildrenCount(int p1)
	{
		return child.get(p1).length;
	}

	@Override
	public Object getGroup(int p1)
	{
		return list.get(p1);
	}

	@Override
	public Object getChild(int p1, int p2)
	{
		return child.get(p1)[p2];
	}

	@Override
	public long getGroupId(int p1)
	{
		return p1;
	}

	@Override
	public long getChildId(int p1, int p2)
	{
		return p2;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View view = inflater.inflate(R.layout.item_left, null);
		ImageView im = (ImageView) view.findViewById(R.id.itemleftImageView);
		TextView text = (TextView) view.findViewById(R.id.itemleftTextView);
		im.setImageResource(list.get(groupPosition).image);
		text.setText(list.get(groupPosition).title);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		View view = inflater.inflate(R.layout.item_left_child, null);
		TextView text = (TextView) view.findViewById(R.id.itemleftchildTextView);
		text.setText(child.get(groupPosition)[childPosition].title);
		return view;
	}

	@Override
	public boolean isChildSelectable(int p1, int p2)
	{
		return true;
	}

	LeftAdapter(Context context, List<ItemBeanLeft> ilist, List<ItemBeanLeft[]> ichild) {
		inflater = LayoutInflater.from(context);
		this.list = ilist;
		this.child = ichild;
	}
}


