package lance.liang.chat2;

import java.util.*;

public class ResultData
{
	public static class Data {
		public static class NewMessages {
			public int gid;
			public int lastest_mid;
		}
		public static class Message {
			public String username, head, type, text;
			public int send_time;
			int mid;
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
		public List<Message> message;
		public Info info;
		public String auth;
		public String head;
		public String username;
		public List<RoomData> room_data;
		public List<NewMessages> new_messages;
		
		public String md5, filrname, etag, url;
	}
	public int code;
	public String message;
	public Data data;
}


