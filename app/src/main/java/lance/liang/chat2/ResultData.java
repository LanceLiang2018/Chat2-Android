package lance.liang.chat2;

import java.util.*;

class MessageData {
	public int mid, gid, send_time;
	public String username, text, type, head;
	MessageData() {}
	MessageData(int _mid, int _gid, int _send_time, String _username, String _text, String _type, String _head) {
		mid = _mid; gid = _gid; send_time = _send_time; username = _username; text = _text; type = _type; head = _head;
	}
}

public class ResultData
{
	public static class Data {
		public static class NewMessages {
			public int gid;
			public int lastest_mid;
		}
		public static class Info {
			public int gid, create_time, member_number, last_post_time;
			public String name;
		}
		public static class RoomData {
			public String name;
			public int gid;
			public int latest_mid;
			public String latest_msg;
			public String latest_time;
		}
		public static class UploadResult {
			public String filename, etag, url;
		}
		public List<MessageData> message;
		public Info info;
		public String auth;
		public String head;
		public String username;
		public List<RoomData> room_data;
		public List<NewMessages> new_messages;
		public UploadResult upload_result;
		
		//public String md5, filrname, etag, url;
	}
	public int code;
	public String message;
	public Data data;
}


