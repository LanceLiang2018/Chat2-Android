package lance.liang.chat2;

import android.content.*;
import android.database.sqlite.*;
import java.util.*;
import android.database.*;
import android.util.*;

public class MyDB extends SQLiteOpenHelper
{
	public static final String DB_NAME = "chat2.db";
	public static final String TB_MESSAGE = "messages";
	public final String[] tables = {TB_MESSAGE, };
	public static final int VER = 1;

	public MyDB(Context context) {
		super(context, DB_NAME, null, VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		//ContentValues val = new ContentValues();
		//for (String name: tables)
		//	db.execSQL("DROPTABLE IF EXISTS " + name);
		db.execSQL("CREATE TABLE " + TB_MESSAGE + 
				   " (gid INT, username VARCHAR(522), mid INT, text VARCHAR(8192), " + 
				   "type VARCHAR(32), send_time INT, head VARCHAR(2048), tag VARCHAR(2048), status INT)");
		//db.insert(TB_MESSAGE, null, val);
		//db.close();
	}
	
	void init() {
		SQLiteDatabase db = getWritableDatabase();
		for (String name: tables)
			db.execSQL("DROP TABLE IF EXISTS " + name);
		onCreate(db);
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
		for (String name: tables)
			db.execSQL("DROP TABLE IF EXISTS " + name);
		onCreate(db);
	}

	public static MyDB get(Context context)
	{
		return new MyDB(context);
	}
	
	public List<MessageData> getMessages(int gid, int limit, int offset) {
		List<MessageData> data = new ArrayList<MessageData>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT mid, gid, send_time, username, text, type, head, tag, status FROM " + TB_MESSAGE + 
									" WHERE gid = ? ORDER BY mid LIMIT ? OFFSET ?", new String[] {"" + gid, "" + limit, "" + offset});
		if (!cursor.equals(null)){
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
				data.add(new MessageData(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
										 cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6))
										 .setTag(cursor.getString(7)).setStatus(cursor.getInt(8)));
			}
		}
		cursor.close();
		db.close();
		return data;
	}
	public List<MessageData> updateMessage(MessageData message) {
		List<MessageData> data = new ArrayList<MessageData>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("UPDATE " + TB_MESSAGE + " SET gid = ?, send_time = ?, username = ?, " + 
									"text = ?, type = ?, head = ?, tag = ?, status = ?" +
									" WHERE mid = ?", new String[] {"" + message.gid, "" + message.send_time, message.username, 
										message.text, message.type, message.head, message.tag, "" + message.status, "" + message.mid});
		cursor.close();
		db.close();
		return data;
	}
	
	public List<MessageData> getNewMessages(int gid) {
		List<MessageData> data = new ArrayList<MessageData>();
		int latest = getLatestMid(gid);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT mid, gid, send_time, username, text, type, head, status FROM " + TB_MESSAGE + 
									" WHERE gid = ? AND mid > ? ORDER BY mid", new String[] {"" + gid, "" + latest});
		if (!cursor.equals(null)){
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
				data.add(new MessageData(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
										 cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6))
										 .setStatus(cursor.getInt(7)));
			}
		}
		cursor.close();
		db.close();
		return data;
	}
	
	public void saveMessage(List<MessageData> data) {
		SQLiteDatabase db = getWritableDatabase();
		for (MessageData d: data) {
			ContentValues val = new ContentValues();
			val.put("mid", d.mid);
			val.put("gid", d.gid);
			val.put("send_time", d.send_time);
			val.put("username", d.username);
			val.put("text", d.text);
			val.put("type", d.type);
			val.put("head", d.head);
			val.put("status", d.status);
			db.insert(TB_MESSAGE, null, val);
		}
		db.close();
	}
	
	public void saveMessage(MessageData data) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues val = new ContentValues();
		val.put("mid", data.mid);
		val.put("gid", data.gid);
		val.put("send_time", data.send_time);
		val.put("username", data.username);
		val.put("text", data.text);
		val.put("type", data.type);
		val.put("head", data.head);
		val.put("status", data.status);
		db.insert(TB_MESSAGE, null, val);
		db.close();
	}
	
	public void saveMessage(ItemBeanChat data) {
		saveMessage(new MessageData(data));
	}
	
	public void moduleTest() {
		
	}
	
	public int getLatestMid(int gid) {
		SQLiteDatabase db = getReadableDatabase();
		int latest = 0;
		Cursor cursor = db.rawQuery("SELECT mid FROM " + TB_MESSAGE + 
									" WHERE gid = ? ORDER BY mid", new String[] {"" + gid, });
		if (!cursor.equals(null)){
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
				//latest = cursor.getInt(0);
				latest = max(latest, cursor.getInt(0));
			}
		}
		cursor.close();
		db.close();
		return latest;
	}

	private int max(int a, int b) {
		return a > b ? a : b;
	}

	@Override
	public void close()
	{
		super.close();
	}
}


