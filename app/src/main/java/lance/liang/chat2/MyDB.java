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
		db.execSQL("CREATE TABLE " + TB_MESSAGE + 
				   " (gid INT, username VARCHAR(522), mid INT primary key, text VARCHAR(8192), " + 
				   "type VARCHAR(32), send_time INT, head VARCHAR(2048))");
		//db.insert(TB_MESSAGE, null, val);
		//db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
		for (String name: tables)
			db.execSQL("DROPTABLE IF EXISTS " + name);
		onCreate(db);
	}

	public static MyDB get(Context context)
	{
		return new MyDB(context);
	}
	
	public class MessageData {
		public int mid, gid, send_time;
		public String username, text, type, head;
		MessageData() {}
		MessageData(int _mid, int _gid, int _send_time, String _username, String _text, String _type, String _head) {
			mid = _mid; gid = _gid; send_time = _send_time; username = _username; text = _text; type = _type; head = _head;
		}
	}
	
	public List<MessageData> getMessages(int gid, int limit, int offset) {
		List<MessageData> data = new ArrayList<MessageData>();
		SQLiteDatabase db = getReadableDatabase();
		//Cursor cursor = db.query(TB_MESSAGE, new String[] {"mid", "gid", "send_time", "username", "text", "type", "head"},
		//	"gid = " + gid, null, null, "ORDERD BY mid DESC", "LIMIT " + limit);
		Cursor cursor = db.rawQuery("SELECT mid, gid, send_time, username, text, type, head FROM " + TB_MESSAGE + 
			" WHERE gid = ? ORDER BY mid DESC", new String[] {"" + gid, });
		if (!cursor.equals(null)){
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
				data.add(new MessageData(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
					cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
			}
		}
		cursor.close();
		//db.close();
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
			db.insert(TB_MESSAGE, null, val);
			//db.execSQL("INSERT INTO " + TB_MESSAGE + " (mid, gid, send_time, username, text, type, head) VALUES ( ?, ?, ?, ?, ?, ?, ?)", new Object[] {});
		}
		//db.close();
	}
	
	public void moduleTest() {
		MessageData m = new MessageData(0, 0, 0, "", "", "", "");
		List<MessageData> li = new ArrayList<>();
		li.add(m);
		saveMessage(li);
		li = getMessages(0, 30, 0);
		for (MessageData i: li) {
			Log.i("ChatDB", "Got message mid: " +i.mid);
		}
	}

	@Override
	public void close()
	{
		super.close();
	}
}


