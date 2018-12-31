package lance.liang.chat2;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;

class MainAdapter extends BaseAdapter
{
	private List<ItemBeanMain> list;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_main, null);
		ItemBeanMain bean = list.get(position);
		((ImageView) view.findViewById(R.id.itemmainImageView)).setImageResource(bean.image);
		((TextView) view.findViewById(R.id.itemmainTextView_title)).setText(bean.title);
		((TextView) view.findViewById(R.id.itemmainTextView_content)).setText(bean.content);
		return null;
	}
}
