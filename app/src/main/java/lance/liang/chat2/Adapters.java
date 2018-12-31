package lance.liang.chat2;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import com.bumptech.glide.*;

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
		((ImageView) view.findViewById(R.id.itemmainImageView)).setImageResource(bean.image);
		((TextView) view.findViewById(R.id.itemmainTextView_title)).setText(bean.title);
		((TextView) view.findViewById(R.id.itemmainTextView_content)).setText(bean.content);
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
		
		Glide.with(view).load("https://avatars0.githubusercontent.com/u/41908064?s=460&v=4")
			.into((ImageView) view.findViewById(R.id.itemchatImageView_head));
		
		return view;
	}
}

class LeftAdapter extends BaseAdapter
{
	private List<ItemBeanLeft> list;
	private LayoutInflater inflater;

	LeftAdapter(Context context, List<ItemBeanLeft> ilist) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_left, null);
		ItemBeanLeft bean = list.get(position);
		((ImageView) view.findViewById(R.id.itemleftImageView)).setImageResource(bean.image);
		((TextView) view.findViewById(R.id.itemleftTextView)).setText(bean.title);
		return view;
	}
}

