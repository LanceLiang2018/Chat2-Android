package lance.liang.chat2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.*;
import android.os.*;
import okhttp3.internal.http2.*;

class UserData
{
	public String username = "", auth = "", head = "";
	public int uid = 0, lastest = 0;
}

class Settings
{
	//public String server = "https://lance-chatroom2.herokuapp.com/";
	public String server = "http://0.0.0.0:5000/";
	public int theme = R.style.AppTheme;
}

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
