package lance.liang.group.latina;

import android.content.*;
import com.google.gson.*;
import com.lzy.okgo.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;

public class ShiCi
{
	static private String url_token = "https://v2.jinrishici.com/token",
		url_one = "https://v2.jinrishici.com/one.json";
	
	static public void GetToken(final Context context) {
		Communication.getComm(context).get(url_token, new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					Config config = Config.get(context);
					ShiCiToken token = new Gson().fromJson(p1.body(), ShiCiToken.class);
					if (!"success".equals(token.status))
						return;
					config.data.settings.cToken = token.data;
					config.save();
				}
			});
	}
	
	static public void One(Context context, StringCallback callback) {
		String token = Config.get(context).data.settings.cToken;
		if ("".equals(token)) {
			token = "CW9FT4FjCmdNmv+5ONKOLjckDtcZIRHp";
			// back up token
			GetToken(context);
		}
		OkGo.<String>get(url_one)
			.headers("X-User-Token", token)
			.execute(callback);
	}
}
