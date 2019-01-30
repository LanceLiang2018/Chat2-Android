package lance.liang.group.latina;
import android.app.*;
import android.content.*;
import android.net.*;
import android.widget.*;
import com.google.gson.*;
import com.lzy.okgo.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import android.nfc.*;
import com.lzy.okgo.request.*;
import java.io.*;
import com.lzy.okgo.db.*;
import com.lzy.okserver.upload.*;
import com.lzy.okserver.*;
import android.util.*;
import com.lzy.okgo.convert.*;

/*
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
	
	MAKE_FRIENDS,
	
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
	private Context pcontext = null;

	Communication(Context context)
	{
		pcontext = context;
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
		
		MAKE_FRIENDS = SERVER + "make_friends";
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
	
	public UploadTask upload(String url, String tag, ContentValues parames, UploadListener listener)
	{
		PostRequest<String> request = OkGo.<String>post(url).tag(this);
		for (String key: parames.keySet()) {
			request.params(key, parames.get(key).toString());
		}
		request.converter(new StringConvert());
		//request.execute(callback);
		UploadTask<String> task = OkUpload.request(tag, request)
			.save()
			.register(listener);
		return task;
	}
	public UploadTask uploadNoListener(String url, String tag, ContentValues parames)
	{
		PostRequest<String> request = OkGo.<String>post(url).tag(this);
		for (String key: parames.keySet()) {
			request.params(key, parames.get(key).toString());
		}
		request.converter(new StringConvert());
		//request.execute(callback);
		UploadTask<String> task = OkUpload.request(tag, request)
			.save();
		return task;
	}
	
	static public void test(final Context context) {
		ContentValues parames = new ContentValues();
		parames.put("auth", Config.get(context).data.user.auth);
		UploadListener listener = new UploadListener<String>("myTag") {
			@Override
			public void onStart(Progress p1){
				Toast.makeText(context, "Upload Started.", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onProgress(Progress p1) {
				Log.i("Chat 2 Upload", "" + (int)(p1.fraction * 100) + "%");
			}

			@Override
			public void onError(Progress p1) {
			}

			@Override
			public void onFinish(String p1, Progress p2) {
				Log.i("Chat 2 Upload", p1);
			}

			@Override
			public void onRemove(Progress p1) {
			}
		};
		Communication.getComm(context).upload(Communication.BEAT, "myTag", parames, listener).start();
	}
}
*/

//V3
public class Communication
{
	public static String SERVER = "http://0.0.0.0:5000/v3/api";
	public static int TIMEOUT = 20000;
	public int CommVer = 3;
	public static String ABOUT = "about",
	BEAT = "beat",
	LOGIN = "login",
	SIGNUP = "signup",
	GET_MESSAGES = "get_messages",
	SEND_MESSAGE = "send_message",
	GET_HEAD = "get_head",
	CLEAR_ALL = "clear_all",

	GET_USER = "get_user",
	SET_USER = "set_user",
	JOIN_IN = "join_in",

	CREATE_ROOM = "create_room",
	GET_ROOM = "get_room",
	GET_ROOM_ALL = "get_room_all",
	GET_ROOM_INFO = "get_room",
	SET_ROOM_INFO = "set_room",
	GET_FILES = "get_files",
	
	MAKE_FRIENDS = "make_friends",
	
	UPLOAD = "upload",
	
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
	private Context pcontext = null;

	Communication(Context context)
	{
		pcontext = context;
		init(context);
	}

	private void init(Context context)
	{
		this.SERVER = Config.get(context).data.settings.server;
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
	
	public void post(String action, ContentValues parames, StringCallback callback)
	{
		String url = SERVER;
		PostRequest<String> request = OkGo.<String>post(url).tag(this);
		request.params("ver", "" + CommVer);
		request.params("action", action);
		for (String key: parames.keySet()) {
			request.params(key, parames.get(key).toString());
		}
		request.execute(callback);
	}
	
	public void postWithAuth(String action, ContentValues parames, StringCallback callback)
	{
		parames.put("auth", Config.get(pcontext).data.user.auth);
		post(action, parames, callback);
	}
	
	public UploadTask upload(String action, String tag, ContentValues parames, UploadListener listener)
	{
		parames.put("action", action);
		PostRequest<String> request = OkGo.<String>post(SERVER).tag(this);
		for (String key: parames.keySet()) {
			request.params(key, parames.get(key).toString());
		}
		request.converter(new StringConvert());
		//request.execute(callback);
		UploadTask<String> task = OkUpload.request(tag, request)
			.save()
			.register(listener);
		return task;
	}
	public UploadTask uploadNoListener(String action, String tag, ContentValues parames)
	{
		parames.put("action", action);
		PostRequest<String> request = OkGo.<String>post(SERVER).tag(this);
		for (String key: parames.keySet()) {
			request.params(key, parames.get(key).toString());
		}
		request.converter(new StringConvert());
		//request.execute(callback);
		UploadTask<String> task = OkUpload.request(tag, request)
			.save();
		return task;
	}
	
	static public void test(final Context context) {
		ContentValues params = new ContentValues();
		Communication.getComm(context).postWithAuth(Communication.BEAT, params, new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					Toast.makeText(context, p1.body(), Toast.LENGTH_LONG);
				}
		});
	}
}


