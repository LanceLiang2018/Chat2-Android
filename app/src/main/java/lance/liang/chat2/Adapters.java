package lance.liang.chat2;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.support.v4.graphics.drawable.*;
import android.support.v7.appcompat.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.request.*;
import com.bumptech.glide.request.target.*;
import com.bumptech.glide.request.transition.*;
import java.util.*;

import android.support.v7.appcompat.R;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.drawable.*;
import com.bumptech.glide.load.resource.bitmap.*;
import com.bumptech.glide.load.engine.*;
import com.bumptech.glide.load.*;
import android.view.View.*;
import android.app.*;

class MainAdapter extends BaseAdapter
{
	public List<ItemBeanMain> list;
	private LayoutInflater inflater;

	MainAdapter(Context context, List<ItemBeanMain> ilist) {
		this.list = ilist;
		inflater = LayoutInflater.from(context);
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
		
		try {
			RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(view.getResources(), BitmapFactory.decodeResource(view.getResources(), R.drawable.image_1));
			roundedBitmapDrawable1.setCircular(true);
			im.setImageDrawable(roundedBitmapDrawable1);
		}
		catch (Exception e) {
			Log.e("Chat 2", e.getMessage());
		}
		
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
		View view = null;
		ItemBeanChat bean = list.get(position);
		ImageView head = new ImageView(pcontext);
		
		if (bean.type.equals("text")) {
			view = inflater.inflate(R.layout.item_chat_text, null);
			
			((TextView) view.findViewById(R.id.itemchatTextView_username)).setText(bean.username);
			((TextView) view.findViewById(R.id.itemchatTextView_time)).setText(bean.time);
			TextView message = (TextView) view.findViewById(R.id.itemchatTextView_message);
			LinearLayout box = (LinearLayout) view.findViewById(R.id.itemchatLinearLayout_box);
			head = (ImageView) view.findViewById(R.id.itemchatImageView_head);
			
			message.setText(bean.message);
			box.setBackgroundResource(Config.get(pcontext).data.settings.colorBg);
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
			view = inflater.inflate(R.layout.item_chat_image, null);

			((TextView) view.findViewById(R.id.itemchatimageTextView_username)).setText(bean.username);
			((TextView) view.findViewById(R.id.itemchatimageTextView_time)).setText(bean.time);
			head = (ImageView) view.findViewById(R.id.itemchatimageImageView_head);
			final ImageView image = (ImageView) view.findViewById(R.id.itemchatimageImageView_image);
			image.setTag(bean.message);
			
			image.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View p1) {
						View inview = inflater.inflate(R.layout.image_view, null);
						
						String url = (String) p1.getTag();
						AlertDialog.Builder builder = new AlertDialog.Builder(pcontext);
						ImageView imview = (ImageView) inview.findViewById(R.id.imageviewImageView);
						
						Glide.with(builder.getContext()).load(url)
							.apply(new RequestOptions().placeholder(R.drawable.image_1)
								.encodeQuality(100))
							.into(imview);
						final AlertDialog dialog = builder.setView(inview).show();
						imview.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View p1) {
									dialog.hide();
								}
							});
					}
				});
			
			SimpleTarget target = new SimpleTarget<Drawable>() {
				@Override
				public void onResourceReady(Drawable p1, Transition<? super Drawable> p2) {
					RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(pcontext.getResources(), 
						((BitmapDrawable) p1).getBitmap());
					roundedBitmapDrawable1.setCornerRadius(30);
					image.setImageDrawable(roundedBitmapDrawable1);
				}
			};
			
			Glide.with(pcontext)
				//.asBitmap()
				.load(bean.message)
				.apply(new RequestOptions()
					.placeholder(R.drawable.image_box_bg)
					.override(300))
				.into(target);
		}
		Glide.with(pcontext)
			.load(bean.head_url)
			.apply(new RequestOptions().circleCrop().placeholder(R.drawable.image_1))
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(head);
		
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

