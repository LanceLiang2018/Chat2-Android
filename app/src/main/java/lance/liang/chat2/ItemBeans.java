package lance.liang.chat2;

class ItemBeanMain {
	String title, content;
	int image;
	int gid;

	ItemBeanMain(int gid,int image, String title, String content) {
		this.gid = gid;
		this.image = image;
		this.title = title;
		this.content = content;
	}
}

class ItemBeanLeft {
	String title;
	int image;

	ItemBeanLeft(int image, String title) {
		this.image = image;
		this.title = title;
	}
}

class ItemBeanChat {
	String time, head_url, message, username;
	int mid;

	ItemBeanChat(int mid, String usrname, String time, String message, String head_url) {
		this.mid = mid; this.username = usrname; this.time = time;
		this.head_url = head_url; this.message = message;
	}
}

