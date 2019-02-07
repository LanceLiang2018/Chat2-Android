package lance.liang.group.latina;

import android.content.*;
import android.util.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;

class ConfigData
{
	/*
	static class UserData
	{
		public String username = "", auth = "", head = "", motto = "Motto~";
		public int uid = 0, lastest = 0;
	}*/
	static class Settings
	{
		//public String server = "https://lance-chatroom2.herokuapp.com/";
		public String server = "https://lance-chatroom2.herokuapp.com/v3/api";
		public int theme = R.style.AppTheme07;
		//public int colorBg = 0xFF9BAEC8;
		//public int colorFt = 0xFF6AAFE6;
		public int colorBg = R.color.colorBg07;
		public int colorFt = R.color.colorFt07;
		public int isShowSplash = 1;
		public int firstStart = 1;
		public int count_today = 0, count_total = 0;
		public String lastPrintDate = "00";
		public String defaultPrinter = "Printer";
		public String savePath = "Latina/";
		public int unreadMid = 0;
		public String font = "miao.ttf";
		
		public String remoteFontFamily = "Microsoft YaHei Mono";
		public int remoteFontSize = 10;
	}
	public Settings settings = new Settings();
	public PersonData user = new PersonData();
	//public List<RoomData> rooms = new ArrayList<RoomData>();
	//public String rooms_str = "[]";
	//['微软雅黑', '宋体', '仿宋', '黑体',
	// 'Microsoft YaHei Mono', '幼圆', '楷体', '隶书']
}

public class Config
{
	public static ConfigData data;

	public static String FILENAME = "";
	public final static String NAME = "settings.json";
	
	private Context pcontext;
	
	public Config(Context context)
	{
		pcontext = context;
		//FILENAME = pcontext.getFilesDir().getAbsolutePath() + "/" + NAME;
		FILENAME = pcontext.getExternalFilesDir("Chat2").getAbsolutePath() + "/" + NAME;
		//Log.v("Chat 2", "Config FileName: " + FILENAME);
		load();
	}
	
	public static Config get(Context context)
	{
		return new Config(context);
	}
	
	public void load()
	{
		try {
			File file = new File(FILENAME);
			Reader reader = new FileReader(file);
			char[] buf = new char[(int)file.length()];
			reader.read(buf, 0, (int)file.length());
			reader.close();
			String str = String.valueOf(buf).toString();
			//String str = "{}";
			//Log.v("Chat 2", "Load(): " + str);
			//Toast.makeText(context, str, Toast.LENGTH_LONG).show();
			data = (new Gson()).fromJson(str, ConfigData.class);
			
			if (data.settings.savePath.equals("Latina/"))
				data.settings.savePath = data.settings.savePath + data.user.username;
			File saveFile = new File(data.settings.savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			}
		}
		catch (Exception e) {
			init();
			load();
			Log.e("Chat 2", e.getMessage());
		}
	}
	
	public void init()
	{
		try {
			File file = new File(FILENAME);
			if (!file.exists())
				file.createNewFile();
			Writer writer = new FileWriter(file);
			String str = (new Gson()).toJson(new ConfigData(), ConfigData.class);
			writer.write(str);
			writer.close();
			
			//ConfigData d = (new Gson()).fromJson(str, ConfigData.class);
		}
		catch (IOException e) {
			Log.e("Chat 2", e.getMessage());
		}
	}
	
	public void save()
	{
		try {
			File file = new File(FILENAME);
			Writer writer = new FileWriter(file);
			String str = (new Gson()).toJson(data, ConfigData.class);
			writer.write(str);
			writer.close();
			//Log.v("Chat 2", "Save(): " + str);
		}
		catch (IOException e) {
			Log.e("Chat 2", e.getMessage());
		}
	}
	
	public void test()
	{
		init();
		load();
		data.user.username = "Changed";
		Log.w("Chat 2", (new Gson()).toJson(data, ConfigData.class));
		save();
		load();
		Log.w("Chat 2", (new Gson()).toJson(data, ConfigData.class));
		
	}
}

/*
public class Config extends SQLiteOpenHelper
{
	public static final String DB_NAME = "settings.db";
	public static final String TB_NAME = "settings";
	public static final String TB_NAME_USER= "userdata";
	public static final int VER = 1;
	
	public UserData user = new UserData();
	public Settings settings = new Settings();
	
	public Config(Context context) {
		super(context, DB_NAME, null, VER);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		ContentValues val = new ContentValues();
		val.put("server", settings.server);
		val.put("theme", settings.theme);
		val.put("flag", "FLAG");
		db.execSQL("CREATE TABLE " + TB_NAME + 
				   " (server VARCHAR(512), theme int, flag VARCHAR(32))");
		db.insert(TB_NAME, null, val);
		
		val = new ContentValues();
		val.put("username", user.username);
		val.put("auth", user.auth);
		val.put("head", user.head);
		val.put("lastest", user.lastest);
		val.put("uid", user.uid);
		val.put("flag", "FLAG");
		db.execSQL("CREATE TABLE " + TB_NAME_USER + 
				   " (uid INT, username VARCHAR(512), auth VARCHAR(64), head VARCHAR(1024), lastest INT, flag VARCHAR(32))");
		db.insert(TB_NAME_USER, null, val);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
		db.execSQL("DROPTABLE IF EXISTS " + TB_NAME);
		db.execSQL("DROPTABLE IF EXISTS " + TB_NAME_USER);
		onCreate(db);
	}
	
	public static Config get(Context context)
	{
		return (new Config(context)).load();
	}

	public Config load() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TB_NAME, new String[] {"server", "theme"}, "flag = FLAG", null, null, null, null);
		if (!cursor.equals(null)){
			if (cursor.moveToFirst()){
				settings.server = cursor.getString(0);
				settings.theme = cursor.getInt(1);
			}
		}
		cursor = db.query(TB_NAME_USER, new String[] {"uid", "username", "auth", "head", "lastest"}, 
						  "flag = FLAG", null, null, null, null);
		if (!cursor.equals(null)) {
			if (cursor.moveToFirst()) {
				user.uid = cursor.getInt(0);
				user.username = cursor.getString(1);
				user.auth = cursor.getString(2);
				user.head = cursor.getString(3);
				user.lastest = cursor.getInt(4);
			}
		}
		return this;
	}
	public Config save() {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues val = new ContentValues();
		val.put("server", settings.server);
		val.put("theme", settings.theme);
		db.update(TB_NAME, val, "flag = FLAG", null);
		
		val = new ContentValues();
		val.put("username", user.username);
		val.put("auth", user.auth);
		val.put("head", user.head);
		val.put("lastest", user.lastest);
		val.put("uid", user.uid);
		db.update(TB_NAME_USER, val, "flag = FLAG", null);
		
		return this;
	}
}

*/
