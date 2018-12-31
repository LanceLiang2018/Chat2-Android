package lance.liang.chat2;

import java.util.*;

public class ResultData
{
	public static class Data {
		public static class Message {
			public String username, head, name, type, text;
			public int gid, send_time;
		}
		public static class Info {
			public int gid, create_time, member_number, last_post_time;
			public String name;
		}
		public static class RoomData {
			public String name;
			public int gid;
		}
		public List<Message> message;
		public Info info;
		public String auth;
		public List<RoomData> room_data;
	}
	public int code;
	public String message;
	public Data data;
}


