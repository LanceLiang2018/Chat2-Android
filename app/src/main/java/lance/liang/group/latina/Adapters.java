package lance.liang.group.latina;
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

import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.google.gson.*;
import jp.wasabeef.glide.transformations.*;
import lance.liang.group.latina.MenuData.*;
import java.text.*;
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
		if (!data.room_type.equals("printer"))
			list.add(data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_main, null);
		final ItemBeanMain bean = list.get(position);
		if (bean.room_type.equals("printer"))
			return new View(pcontext);
		final ImageView im = (ImageView) view.findViewById(R.id.itemmainImageView);
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
		
		SimpleTarget target_main = new SimpleTarget<Drawable>() {
			@Override
			public void onResourceReady(Drawable p1, Transition<? super Drawable> p2) {
				im.setImageDrawable(p1);
				MyApplication.getMyApplication().imageMap.put("(GROUP)" + bean.gid, ((BitmapDrawable)p1).getBitmap());
			}
		};
		
		Glide.with(pcontext).load(bean.head)
			.apply(new RequestOptions().circleCrop())
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(target_main);

		return view;
	}
}


class PeopleAdapter extends BaseAdapter
{
	public List<ItemBeanMain> list;
	private LayoutInflater inflater;
	private Context pcontext;

	PeopleAdapter(Context context, List<ItemBeanMain> ilist) {
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
		if (data.room_type.equals("printer"))
			list.add(data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_main, null);
		ItemBeanMain bean = list.get(position);
		if (!bean.room_type.equals("printer"))
			return new View(pcontext);
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
	private NumberFormat numberFormat;

	ChatAdapter(Context context, List<ItemBeanChat> ilist) {
		this.list = ilist;
		inflater = LayoutInflater.from(context);
		pcontext = context;
		numberFormat = NumberFormat.getPercentInstance();
		numberFormat.setMinimumFractionDigits(2);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null, frame = null;
		final ItemBeanChat bean = list.get(position);
		
		if (bean.status == ItemBeanChat.GONE)
			return new View(pcontext);
		
		if (bean.username.equals(Config.get(pcontext).data.user.username))
			view = inflater.inflate(R.layout.item_chat_frame_me, null);
		else
			view = inflater.inflate(R.layout.item_chat_frame, null);
		
		((TextView) view.findViewById(R.id.itemchatframeTextView_username)).setText(bean.username);
		//((TextView) view.findViewById(R.id.itemchatframeTextView_time)).setText(bean.time);
		final ImageView head = (ImageView) view.findViewById(R.id.itemchatframeImageView_head);
		
		String str_time = bean.time;
		TextView text_time = (TextView) view.findViewById(R.id.itemchatframeTextView_time);
		text_time.setText(str_time);
		if (position != 0) {
			ItemBeanChat prebean = list.get(position - 1);
			if (prebean.send_time + 5 * 60 > bean.send_time) {
				text_time.setVisibility(View.GONE);
			}
		}
		
		SimpleTarget target_head = new SimpleTarget<Drawable>() {
			@Override
			public void onResourceReady(Drawable p1, Transition<? super Drawable> p2) {
				MyApplication.getMyApplication().imageMap.put(bean.username, ((BitmapDrawable)p1).getBitmap());
				head.setImageDrawable(p1);
			}
		};
		
		Glide.with(pcontext)
			.load(bean.head_url)
			.apply(new RequestOptions().circleCrop().placeholder(R.drawable.image_blank))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(target_head);

		if (bean.type.equals("text")) {
			frame = inflater.inflate(R.layout.item_frame_text, null);
			TextView message = (TextView) frame.findViewById(R.id.itemframetextTextView_message);
			LinearLayout box = (LinearLayout) frame.findViewById(R.id.itemframetextLinearLayout_box);
			
			message.setText(bean.message);
			if (MyApplication.getMyApplication().font_text != null)
				message.setTypeface(MyApplication.getMyApplication().font_text);
			else
				message.setTypeface(Typeface.DEFAULT);
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
					   .placeholder(R.drawable.image_blank)
					   .override(300)
					   .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(target);
		}
		if (bean.type.equals("file")) {
			frame = inflater.inflate(R.layout.item_frame_file, null);
			TextView filename = (TextView) frame.findViewById(R.id.itemframefileTextView_filename);
			TextView filesize = (TextView) frame.findViewById(R.id.itemframefileTextView_filesize);
			
			filename.setText(bean.message);
			filesize.setText("0.34 Kb");
			//frame.setBackgroundResource(Config.get(pcontext).data.settings.colorBg);
			
			frame.setTag(bean.tag);
			
			frame.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View p1) {
						//if (bean.username.equals(Config.get(pcontext).data.user.username))
						//	return;
						DownloadManager downloadManager = (DownloadManager) pcontext.getSystemService(Context.DOWNLOAD_SERVICE);
						String tag = (String)(p1.getTag());
						if (tag == null || tag.charAt(0) == '/')
							return;
						Uri url = Uri.parse(tag);
						DownloadManager.Request request = new DownloadManager.Request(url);
						request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
						request.setDestinationInExternalPublicDir(Config.get(pcontext).data.settings.savePath, tag.substring(tag.lastIndexOf("/") + 1, tag.length()));
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
					//Bundle bundle = new Bundle();
					//bundle.putString("username", bean.username);
					//bundle.putString("head_url", bean.head_url);
					MyApplication.getMyApplication().putObject("username", bean.username);
					Intent intent = new Intent();
					intent.setClass(pcontext, Person.class);
					//intent.putExtras(bundle);
					pcontext.startActivity(intent);
				}
			});
		
		View gview = inflater.inflate(R.layout.item_loading, null);
		LinearLayout back = (LinearLayout) gview.findViewById(R.id.itemloadingLinearLayout_back);
		back.addView(frame);
		final RelativeLayout loading_main = (RelativeLayout) gview.findViewById(R.id.itemloadingRelativeLayout_main);
		if (bean.status == ItemBeanChat.DONE) {
			loading_main.setVisibility(View.GONE);
		}
		else {
			loading_main.setVisibility(View.VISIBLE);
		}
		final TextView progress = (TextView) gview.findViewById(R.id.itemloadingTextView_progress);
		((LinearLayout) view.findViewById(R.id.itemchatframeLinearLayout_content)).addView(gview);
		
		/*
		if (bean.tag != null) {
			if (bean.status == bean.PRESEND) {
				OkUpload ok = OkUpload.getInstance();
				if (ok.hasTask(bean.tag)) {
					UploadTask task = ok.getTask(bean.tag);
					task.register(new UploadListener<String>(bean.tag) {
							@Override
							public void onStart(Progress p1){}
							@Override
							public void onProgress(Progress p1){
								//progress.setText("" + (int)(p1.fraction * 100) + "%");
								progress.setText(numberFormat.format(p1.fraction));
							}
							@Override
							public void onError(Progress p1){progress.setText("" + (int)(p1.fraction * 100) + "E");
							}
							@Override
							public void onFinish(String p1, Progress p2){
								bean.status = ItemBeanChat.GONE;
								MyDB.get(pcontext).updateMessage(new MessageData(bean));
								list.set(position, bean);
								notifyDataSetChanged();
								
								ResultData result = new Gson().fromJson(p1, ResultData.class);
								ContentValues params = new ContentValues();
								params.put("auth", Config.get(pcontext).data.user.auth);
								params.put("text", result.data.upload_result.url);
								params.put("message_type", "file");
								params.put("gid", bean.gid);
								Communication.getComm(pcontext).post(Communication.SEND_MESSAGE, params, 
									new StringCallback() {
										@Override
										public void onSuccess(Response<String> p1) {
											if (p1.code() != 200) return;
											ResultData result2 = new Gson().fromJson(p1.body(), ResultData.class);
											if (result2.code != 0)
												new AlertDialog.Builder(pcontext)
													.setMessage(result2.message + " Code: " + result2.code).show();
										}
									});
							}
							@Override
							public void onRemove(Progress p1){}
						});
					task.save();
					bean.status = ItemBeanChat.SENDING;
					MyDB.get(pcontext).updateMessage(new MessageData(bean));
				}
			}
		}*/
		
		return view;
	}
}
/*
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
		//im.setImageResource(list.get(groupPosition).image);
		//Bitmap image = BitmapFactory.decodeResource(parent.getContext().getResources(), list.get(groupPosition).image);
		//int convertColor = Color.parseColor("#FF333333");
		int toColor = Utils.getAccentColor(parent.getContext());
		//Bitmap res = Utils.replaceBitmapColor(image, convertColor, toColor);
		//Glide.with(parent.getContext()).load(res)
		Bitmap image = (Bitmap) MyApplication.getMyApplication().getObject(list.get(groupPosition).image);
		//BitmapDrawable drawable = new BitmapDrawable(image);
		//drawable.setColorFilter(toColor, PorterDuff.Mode.MULTIPLY);
		Glide.with(parent.getContext()).load(image)
			.apply(new RequestOptions().override(80)
			.transform(new ColorFilterTransformation(toColor)))
			.into(im);
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

*/
/*
class LeftAdapter extends BaseAdapter
{
	public List<ItemBeanLeft> list;
	private LayoutInflater inflater;
	private Context pcontext;

	LeftAdapter(Context context, List<ItemBeanLeft> ilist) {
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

	public void insert(ItemBeanLeft data) {
		list.add(data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_left, null);
		ItemBeanLeft bean = list.get(position);
		ImageView im = (ImageView) view.findViewById(R.id.itemleftImageView);
		//im.setImageResource(bean.image);
		((TextView) view.findViewById(R.id.itemleftTextView)).setText(bean.title);
		

		Glide.with(pcontext).load(bean.image)
			.apply(new RequestOptions())//.transform(new ColorFilterTransformation(MyApplication.getMyApplication().getObject(bean.image))))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(im);

		return view;
	}
}
*/
class LeftAdapter extends BaseAdapter
{
	public List<MenuData.LeftMenu> list;
	public List<ImageView> buf;
	private LayoutInflater inflater;
	private Context pcontext;

	LeftAdapter(Context context) {
		//this.list = ilist;
		list = new ArrayList<MenuData.LeftMenu>();
		buf = new ArrayList<ImageView>();
		for (MenuData.LeftMenu menu: MenuData.listLeft) {
			list.add(menu);
			buf.add(null);
		}
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_left, null);
		MenuData.LeftMenu bean = list.get(position);
		//ImageView im = buf.get(position) == null ? ((ImageView) view.findViewById(R.id.itemleftImageView)) : buf.get(position);
		//buf.add(position, im);
		((TextView) view.findViewById(R.id.itemleftTextView)).setText(bean.item.title);


		/*
		Glide.with(pcontext).load(bean.item.image)
			.apply(new RequestOptions())//.transform(new ColorFilterTransformation(MyApplication.getMyApplication().getObject(bean.image))))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(im);
		*/
		ImageView im = null;//buf.get(position);
		if (im == null) {
			im = (ImageView) view.findViewById(R.id.itemleftImageView);
			buf.remove(position);
			buf.add(position, im);
		}
		im.setImageResource(bean.item.image);
		view.setTag(bean);

		return view;
	}
}


class FilesAdapter extends BaseAdapter {
	public List<FileData> list;
	private LayoutInflater inflater;
	private Context pcontext;

	FilesAdapter(Context context, List<FileData> ilist) {
		this.list = ilist;
		//list = new ArrayList<FileData>();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_files, null);
		final FileData bean = list.get(position);
		TextView filename = (TextView) view.findViewById(R.id.itemfilesTextView_filename);
		TextView url = (TextView) view.findViewById(R.id.itemfilesTextView_url);
		
		filename.setText(bean.filename);
		url.setText(bean.filename);
		
		view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					DownloadManager downloadManager = (DownloadManager) pcontext.getSystemService(Context.DOWNLOAD_SERVICE);
					Uri url = Uri.parse(bean.url);
					DownloadManager.Request request = new DownloadManager.Request(url);
					request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
					request.setDestinationInExternalPublicDir(Config.get(pcontext).data.settings.savePath, bean.filename);
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					downloadManager.enqueue(request);
				}
			});

		return view;
	}

}

class SettingsAdapter extends BaseAdapter {
	MenuData.Settings[] list;
	private LayoutInflater inflater;
	private Context pcontext;

	SettingsAdapter(Context context, MenuData.Settings[] ilist) {
		this.list = ilist;
		inflater = LayoutInflater.from(context);
		pcontext = context;
	}

	@Override
	public int getCount() {
		return list.length;
	}

	@Override
	public Object getItem(int p1) {
		return list[p1];
	}

	@Override
	public long getItemId(int p1) {
		return p1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_settings, null);
		MenuData.Settings bean = list[position];
		ImageView im = (ImageView) view.findViewById(R.id.itemsettingsImageView);
		TextView title = (TextView) view.findViewById(R.id.itemsettingsTextView);

		title.setText(bean.item.title);
		Glide.with(pcontext).load(bean.item.image)
			.apply(new RequestOptions())//.transform(new ColorFilterTransformation(MyApplication.getMyApplication().getObject(bean.image))))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(im);

		view.setTag(bean);

		return view;
	}

}
