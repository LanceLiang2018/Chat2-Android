package lance.liang.chat2;
import android.app.*;
import android.content.*;
import android.net.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import okhttp3.*;
import android.util.*;
import android.widget.*;
import android.support.v7.app.*;
import kotlin.annotation.*;

public class CommunicationService
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
	SEND_MESSAGE = SERVER + "send_messsage",
	CLEAR_ALL = SERVER + "clear_all",

	SET_USER = SERVER + "set_user",
	JOIN_IN = SERVER + "join_in",

	CREATE_ROOM = SERVER + "create_room",
	SET_ROOM = SERVER + "set_room",
	GET_ROOMS = SERVER + "get_room_all",
	GET_ROOM_INFO = SERVER + "get_room_info",
	SET_ROOM_INFO = SERVER + "set_room_info",
	GET_MEMBERS = SERVER + "get_room_members",
	
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

	CommunicationService(Context context)
	{
		init(context);
	}
	
	private void init(Context context)
	{
		this.SERVER = Config.get(context).settings.server;
	}

	public static CommunicationService getComm(Context context)
	{
		return new CommunicationService(context);
	}
	
	public static boolean isOnline(Activity act)
	{
		ConnectivityManager conn = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conn.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected())
			return true;
		else return false;
	}

	public void get(String url, Callback callback)
	{
		OkHttpClient okHttpClient = new OkHttpClient();
		final Request request = new Request.Builder().url(url).get().build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(callback);
	}

	public void post(String url, HashMap<String, String> map, Callback callback)
	{
		OkHttpClient okHttpClient = new OkHttpClient();
		FormBody.Builder builder = new FormBody.Builder();


		Iterator iterator = map.keySet().iterator(); 
		while (iterator.hasNext())
		{
			Object key = iterator.next(); 
			//System.out.println("map.get(key) is :" + map.get(key));
			builder.add(key.toString(), map.get(key).toString());
		}

		RequestBody requestBody = builder.build();
		Request request = new Request.Builder() 
			.url(url) 
			.post(requestBody) 
			.build();
		/*
		okHttpClient.newCall(request).enqueue(new Callback() {
				private String TAG;
				@Override public void onFailure(Call call, IOException e)
				{ Log.d(TAG, "onFailure: " + e.getMessage()); }
				@Override public void onResponse(Call call, Response response) throws IOException
				{
					Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
					Headers headers = response.headers();
					for (int i = 0; i < headers.size(); i++)
					{
						Log.d(TAG, headers.name(i) + ":" + headers.value(i));
					}
					Log.d(TAG, "onResponse: " + response.body().string());
				}
			});*/
		okHttpClient.newCall(request).enqueue(callback);
		/*
		 OkHttpClient okHttpClient = new OkHttpClient(); RequestBody requestBody = new FormBody.Builder() .add("search", "Jurassic Park") .build(); Request request = new Request.Builder() .url("https://en.wikipedia.org/w/index.php") .post(requestBody) .build(); okHttpClient.newCall(request).enqueue(new Callback() { @Override public void onFailure(Call call, IOException e) { Log.d(TAG, "onFailure: " + e.getMessage()); } @Override public void onResponse(Call call, Response response) throws IOException { Log.d(TAG, response.protocol() + " " +response.code() + " " + response.message()); Headers headers = response.headers(); for (int i = 0; i < headers.size(); i++) { Log.d(TAG, headers.name(i) + ":" + headers.value(i)); } Log.d(TAG, "onResponse: " + response.body().string()); } });
		 */
	}

	public void test() throws Exception
	{
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(USERNAME, "Tony");
		data.put(PASSWORD, "");
		data.put(EMAIL, "TonyLiang2018@163.com");
		data.put("name", "Tony");
		post(SERVER + SIGNUP, data, new Callback(){
				@Override
				public void onFailure(Call p1, IOException p2)
				{
					Log.e("Chat-2-err", p2.getMessage());
				}
				@Override
				public void onResponse(Call p1, Response p2) throws IOException
				{
					Log.d("Chat-2-Sign-up", p2.body().string());
				}
			});
	}
}
