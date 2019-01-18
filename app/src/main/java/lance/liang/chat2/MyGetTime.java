package lance.liang.chat2;

import java.text.*;
import java.util.*;

public class MyGetTime
{
	public String format = "MM-dd HH:mm";
	
	public String local() {
		return new SimpleDateFormat(format, Locale.CHINA).format(new Date());
	}
	public int getInt() {
		return Integer.valueOf(String.valueOf(new Date().getTime() / 1000));
	}
	public String remote(int time_s) {
		Long stime = Long.parseLong("" + time_s) * 1000;
		Date date = new Date(stime);
		return new SimpleDateFormat(format, Locale.CHINA).format(date);
	}
}
