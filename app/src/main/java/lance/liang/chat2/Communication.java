package lance.liang.chat2;
import android.app.*;
import android.content.*;
import android.net.*;
import android.widget.*;
import com.google.gson.*;
import com.lzy.okgo.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.lzy.okgo.request.*;
import com.lzy.okserver.*;
import com.lzy.okserver.upload.*;
import lance.liang.chat2.*;

public class Communication
{
	public static String SERVER = "http://0.0.0.0:5000/";
	//public String SERVER = "http://www.baidu.com/";
	public static int TIMEOUT = 20000;
	public int CommVer = 1;
	public static String MAIN = SERVER + "",
	ABOUT = SERVER + "about",
	BEAT = SERVER + "beat",
	LOGIN = SERVER + "login",
	SIGNUP = SERVER + "signup",
	GET_MESSAGE = SERVER + "get_message",
	GET_NEW_MESSAGE = SERVER + "get_new_message",
	SEND_MESSAGE = SERVER + "send_message",
	GET_HEAD = SERVER + "get_head",
	CLEAR_ALL = SERVER + "clear_all",

	SET_USER = SERVER + "set_user",
	JOIN_IN = SERVER + "join_in",

	CREATE_ROOM = SERVER + "create_room",
	SET_ROOM = SERVER + "set_room",
	GET_ROOMS = SERVER + "get_room_all",
	GET_ROOM_INFO = SERVER + "get_room_info",
	SET_ROOM_INFO = SERVER + "set_room_info",
	GET_MEMBERS = SERVER + "get_room_members",
	
	UPLOAD = SERVER + "upload",
	
	UID = "uid",
	MID = "mid",
	GID = "gid",
	AUTH = "auth",
	TEXT = "text",
	MESSAGE_TYPE = "message_type",
	USERNAME = "username",
	PASSWORD = "password",
	EMAIL = "email",
	NAME = "name";

	Communication(Context context)
	{
		init(context);
	}
	
	private void init(Context context)
	{
		this.SERVER = Config.get(context).data.settings.server;
		MAIN = SERVER + "";
		ABOUT = SERVER + "about";
		BEAT = SERVER + "beat";
		LOGIN = SERVER + "login";
		SIGNUP = SERVER + "signup";
		GET_MESSAGE = SERVER + "get_message";
		SEND_MESSAGE = SERVER + "send_message";
		GET_NEW_MESSAGE = SERVER + "get_new_message";
		GET_HEAD = SERVER + "get_head";
		CLEAR_ALL = SERVER + "clear_all";

		SET_USER = SERVER + "set_user";
		JOIN_IN = SERVER + "join_in";

		CREATE_ROOM = SERVER + "create_room";
		SET_ROOM = SERVER + "set_room";
		GET_ROOMS = SERVER + "get_room_all";
		GET_ROOM_INFO = SERVER + "get_room_info";
		SET_ROOM_INFO = SERVER + "set_room_info";
		GET_MEMBERS = SERVER + "get_room_members";
		UPLOAD = SERVER + "upload";
	}

	public static Communication getComm(Context context)
	{
		return new Communication(context);
	}
	
	public static boolean isOnline(Activity act)
	{
		ConnectivityManager conn = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conn.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected())
			return true;
		else return false;
	}
	
	public void get(String url, StringCallback callback)
	{
		OkGo.<String>get(url)
			.tag(this)
			.execute(callback);
	}
	
	public void post(String url, ContentValues parames, StringCallback callback)
	{
		PostRequest<String> request = OkGo.<String>post(url).tag(this);
		for (String key: parames.keySet())
		{
			request.params(key, parames.get(key).toString());
		}
		request.execute(callback);
	}
	
	public UploadTask upload(String url, String tag, ContentValues parames, StringCallback callback, UploadListener listener)
	{
		PostRequest<String> request = OkGo.<String>post(url).tag(this);
		for (String key: parames.keySet()) {
			request.params(key, parames.get(key).toString());
		}
		//request.execute(callback);
		UploadTask<String> task = OkUpload.request(tag, request)
			.save()
			.register(listener);
		return task;
	}
	

	public void test(final Context context)
	{
		ContentValues parames = new ContentValues();
		parames.put("username", "Lan");
		parames.put("password", "");
		parames.put("email", "LanceLiang2018@163.com");
		parames.put("name", "Lan");
		Communication.getComm(context).post(Communication.CLEAR_ALL, parames, 
		new StringCallback() {
				@Override
				public void onSuccess(Response<String> response) {
					String str = response.body().toString();
					Gson gson = new Gson();
					ResultData result = gson.fromJson(str, ResultData.class);
					Toast.makeText(context, "Clear All: " + result.message, Toast.LENGTH_LONG).show();
				}
			});
	}
}
