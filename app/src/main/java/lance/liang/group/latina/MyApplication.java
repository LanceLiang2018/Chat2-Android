package lance.liang.group.latina;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.lzy.okgo.*;
import java.util.*;

public class MyApplication extends Application
{
	Map map = new HashMap();
	private static MyApplication instance;
	//public List<String> uploadTags = new ArrayList<String>();
	String defaultPrinterGid = "0";
	public Map imageMap = new HashMap<String, Bitmap>();
	public List<RoomData> list_rooms = null;
	public String tmp_select_font = null;
	public List<RoomData> list_printer = new ArrayList<RoomData>();

	//@Override
	Typeface font_text;

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		OkGo.getInstance().init(this);
		//Toast.makeText(this, "MyApplication Started.", Toast.LENGTH_LONG).show();
	}
	
	public String getDefaultPrinterGid()
	{
		return defaultPrinterGid;
	}

	public Object getObject(Object obj) {
		return map.get(obj);
	}
	
	public void putObject(Object key, Object value) {
		map.put(key, value);
	}
	
	public static MyApplication getMyApplication() {
		return instance;
	}
	
	public Context getContext() {
		return getApplicationContext();
	}
	
	//public List<String> getUploadTags() {
	//	return uploadTags;
	//}
}
