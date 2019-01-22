package lance.liang.group.latina;
import android.support.v7.widget.*;

class ItemBeanMain {
	public String title, content, time = "", head;
	public int image = R.drawable.image_1;
	public int gid, unread = 0;

	ItemBeanMain(int gid, int image, String title, String content) {
		this.gid = gid;
		this.image = image;
		this.title = title;
		this.content = content;
	}
	ItemBeanMain(int gid, String head, String title, String content, String time, int unread) {
		this.gid = gid;
		this.head = head;
		this.title = title;
		this.content = content;
		this.unread = unread;
		this.time = time;
	}
	ItemBeanMain(int gid, String head, String title, String content, String time) {
		this.gid = gid;
		this.head = head;
		this.title = title;
		this.content = content;
		this.time = time;
	}
}

class ItemBeanLeft {
	String title;
	int image;

	ItemBeanLeft(int image, String title) {
		this.image = image;
		this.title = title;
	}
	
	ItemBeanLeft(String title) {
		this.image = R.drawable.image_blank;
		this.title = title;
	}
}

class ItemBeanChat {
	String time, head_url, message, username, type = "text";
	static public final int DONE = 0, SENDING = 1, RECEIVING = 2, PRESEND = 3, GONE = 4;
	int mid, gid, status = DONE, send_time = 0;
	String tag = null;

	/*
	ItemBeanChat(int mid, String usrename, String time, String message, String head_url) {
		this.mid = mid; this.username = usrename; this.time = time;
		this.head_url = head_url; this.message = message;
	}*/
	ItemBeanChat(int mid, int gid, String username, String time, String message, String head_url, String type) {
		this.mid = mid; this.username = username; this.time = time;
		this.head_url = head_url; this.message = message; this.type = type;
		this.gid = gid;
	}
	ItemBeanChat(int mid, int gid, String username, String time, String message, String head_url, String type, int status) {
		this.mid = mid; this.username = username; this.time = time;
		this.head_url = head_url; this.message = message; this.type = type;
		this.status = status;
		this.gid = gid;
	}
	public ItemBeanChat setStatus(int stat) {
		status = stat;
		return this;
	}
	public ItemBeanChat setTag(String tag) {
		this.tag = tag;
		return this;
	}
	public ItemBeanChat setSendTime(int stime) {
		send_time = stime;
		return this;
	}
	ItemBeanChat(MessageData m) {
		this.mid = m.mid; this.username = m.username; //this.time = m.send_time;
		this.time = new MyGetTime().remote(m.send_time);
		this.head_url = m.head; this.message = m.text; this.type = m.type;
	}
	
}

