package lance.liang.group.latina;
import android.app.*;
import android.content.*;
import java.util.*;

public class MyApplication extends Application
{
	Map map = new HashMap();
	private static MyApplication instance;

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
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
}
