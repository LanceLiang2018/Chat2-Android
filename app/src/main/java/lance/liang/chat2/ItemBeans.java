package lance.liang.chat2;

class ItemBeanMain {
	public String title, content, time = "";
	public int image;
	public int gid, unread = 0;

	ItemBeanMain(int gid, int image, String title, String content) {
		this.gid = gid;
		this.image = image;
		this.title = title;
		this.content = content;
	}
	ItemBeanMain(int gid, int image, String title, String content, String time, int unread) {
		this.gid = gid;
		this.image = image;
		this.title = title;
		this.content = content;
		this.unread = unread;
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
	int mid;

	/*
	ItemBeanChat(int mid, String usrename, String time, String message, String head_url) {
		this.mid = mid; this.username = usrename; this.time = time;
		this.head_url = head_url; this.message = message;
	}*/
	ItemBeanChat(int mid, String username, String time, String message, String head_url, String type) {
		this.mid = mid; this.username = username; this.time = time;
		this.head_url = head_url; this.message = message; this.type = type;
	}
	ItemBeanChat(MessageData m) {
		this.mid = m.mid; this.username = m.username; //this.time = m.send_time;
		this.time = new MyGetTime().remote(m.send_time);
		this.head_url = m.head; this.message = m.text; this.type = m.type;
	}
	
}

