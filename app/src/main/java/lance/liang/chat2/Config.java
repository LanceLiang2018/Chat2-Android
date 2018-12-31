package lance.liang.chat2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.*;

public class Config extends SQLiteOpenHelper
{
	public static final String DB_NAME = "settings.db";
	public static final String TB_NAME = "settings";

	//public String SERVER = "https://lance-chatroom2.herokuapp.com/";
	public String SERVER = "http://0.0.0.0:5000/";
	public int THEME = android.R.style.Theme_Material;
	//public int a =  android.R.style.Theme_Material_Dialog_NoActionBar;
	
	public static final int VER = 1;
	
	public Config(Context context) {
		super(context, DB_NAME, null, VER);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		ContentValues val = new ContentValues();
		val.put("server", SERVER);
		val.put("theme", THEME);
		val.put("flag", "FLAG");
		db.execSQL("CREATE TABLE settings (server VARCHAR(512), theme int, flag VARCHAR(32))");
		db.insert(TB_NAME, null, val);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
		db.execSQL("DROPTABLE IF EXISTS settings");
		onCreate(db);
	}

	public void load(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TB_NAME, new String[] {"server", "theme"}, "flag = FLAG", null, null, null, null);
		if (!cursor.equals(null)){
			if (cursor.moveToFirst()){
				SERVER = cursor.getString(0);
				THEME = cursor.getInt(1);
			}
		}
	}
	public void save(){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues val = new ContentValues();
		val.put("server", SERVER);
		val.put("theme", THEME);
		db.update(TB_NAME, val, "flag = FLAG", null);
	}
}
