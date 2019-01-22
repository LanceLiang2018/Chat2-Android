package lance.liang.group.latina;
import android.content.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;

/*
 {
 "id": 578,
 "hitokoto": "Eloim Essaim Eloim Essaim 请聆听我的请求。",
 "type": "a",
 "from": "四月是你的谎言",
 "creator": "yeye",
 "created_at": "1473561915"
 }
*/
class HitokotoData {
	public int id;
 	public String hitokoto, creater, from, created_at, type;
}

public class Hitokoto
{
	private final static String URL = "https://v1.hitokoto.cn/?c=a";
	public static void get(Context context, StringCallback callback) {
		Communication.getComm(context).get(URL, callback);
	}
}
