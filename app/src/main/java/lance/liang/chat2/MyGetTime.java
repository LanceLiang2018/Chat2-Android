package lance.liang.chat2;

import java.text.*;
import java.util.*;

public class MyGetTime
{
	public String format0 = "HH:mm",
		format1 = "MM-dd HH:mm",
		format2 = "yyyy-MM-dd HH:mm";
		
	private String format(int time) {
		int now = getInt();
		Long stime = Long.parseLong("" + time) * 1000;
		Date sdate = new Date(stime);
		Long ntime = Long.parseLong("" + now) * 1000;
		Date ndate = new Date(ntime);
		if (!new SimpleDateFormat("yyyy", Locale.CHINA).format(sdate)
			.equals(new SimpleDateFormat("yyyy", Locale.CHINA).format(ndate))) {
			return format2;
		}
		if (!new SimpleDateFormat("MM-dd", Locale.CHINA).format(sdate)
			.equals(new SimpleDateFormat("MM-dd", Locale.CHINA).format(ndate))) {
			return format1;
		}
		return format0;
	}
	
	public String local() {
		return new SimpleDateFormat(format(getInt()), Locale.CHINA).format(new Date());
	}
	public int getInt() {
		return Integer.valueOf(String.valueOf(new Date().getTime() / 1000));
	}
	public String remote(int time_s) {
		Long stime = Long.parseLong("" + time_s) * 1000;
		Date date = new Date(stime);
		return new SimpleDateFormat(format(time_s), Locale.CHINA).format(date);
	}
}
