package lance.liang.group.latina;


import android.content.*;
import android.graphics.*;
import android.support.v4.view.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class ExamplePagerAdapter extends PagerAdapter
{
    private List<String> mDataList;

    public ExamplePagerAdapter(List<String> dataList) {
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView textView = new TextView(container.getContext());
        textView.setText(mDataList.get(position));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(24);
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        TextView textView = (TextView) object;
        String text = textView.getText().toString();
        int index = mDataList.indexOf(text);
        if (index >= 0) {
            return index;
        }
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDataList.get(position);
    }
}

class MainPagerAdapter extends PagerAdapter
{
    private List<View> mDataList;

    public MainPagerAdapter(List<View> dataList) {
        mDataList = dataList;
    }
	public MainPagerAdapter() {
        mDataList = new ArrayList<>();
    }
	
    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
		View view = mDataList.get(position);
		container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
		int index = 0;
        for (View v: mDataList) {
			if (v.equals(object))
				return index;
			index = index + 1;
		}
		return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Title" + position;
    }
}
