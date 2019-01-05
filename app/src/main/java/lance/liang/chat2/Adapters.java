package lance.liang.chat2;
import android.content.*;
import android.graphics.*;
import android.support.v4.graphics.drawable.*;
import android.support.v7.appcompat.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import java.util.*;

import android.support.v7.appcompat.R;
import android.graphics.drawable.*;
import android.util.*;
import com.lzy.okgo.request.*;

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

	ChatAdapter(Context context, List<ItemBeanChat> ilist) {
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
	
	public void insert(ItemBeanChat data) {
		list.add(data);
	}
	
	public void insert_back(ItemBeanChat data) {
		list.add(0, data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_chat, null);
		ItemBeanChat bean = list.get(position);
		
		((TextView) view.findViewById(R.id.itemchatTextView_username)).setText(bean.username);
		((TextView) view.findViewById(R.id.itemchatTextView_time)).setText(bean.time);
		((TextView) view.findViewById(R.id.itemchatTextView_message)).setText(bean.message);
		ImageView im = (ImageView) view.findViewById(R.id.itemchatImageView_head);
		
		Glide.with(view).load(bean.head_url)
			.into(im);
		try {
			RoundedBitmapDrawable roundedBitmapDrawable1 = RoundedBitmapDrawableFactory.create(view.getResources(), ((BitmapDrawable) im.getDrawable()).getBitmap());
			roundedBitmapDrawable1.setCircular(true);
			im.setImageDrawable(roundedBitmapDrawable1);
		}
		catch (Exception e) {
			Log.e("Chat 2", e.getMessage());
		}
		
		return view;
	}
}

class LeftAdapter extends BaseExpandableListAdapter
{
	private ItemBeanLeft[] list = {
		new ItemBeanLeft(R.drawable.image_1, "Me"),
		new ItemBeanLeft(R.drawable.image_2, "Settings"),
	};
	private ItemBeanLeft[][] child = {
		{
			new ItemBeanLeft(R.drawable.image_1, "I"),
			new ItemBeanLeft(R.drawable.image_2, "Logout"),
		},
		{
			new ItemBeanLeft(R.drawable.image_1, "Theme"),
			new ItemBeanLeft(R.drawable.image_2, "Host"),
		},
	};
	private LayoutInflater inflater;

	@Override
	public int getGroupCount()
	{
		return list.length;
	}

	@Override
	public int getChildrenCount(int p1)
	{
		return child[p1].length;
	}

	@Override
	public Object getGroup(int p1)
	{
		return list[p1];
	}

	@Override
	public Object getChild(int p1, int p2)
	{
		return child[p1][p2];
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
		im.setImageResource(list[groupPosition].image);
		text.setText(list[groupPosition].title);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		View view = inflater.inflate(R.layout.item_left_child, null);
		TextView text = (TextView) view.findViewById(R.id.itemleftchildTextView);
		text.setText(child[groupPosition][childPosition].title);
		return view;
	}

	@Override
	public boolean isChildSelectable(int p1, int p2)
	{
		return true;
	}

	LeftAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}
}

