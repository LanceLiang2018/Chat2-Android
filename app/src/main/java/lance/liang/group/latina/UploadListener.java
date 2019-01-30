package lance.liang.group.latina;
import com.google.gson.*;
import com.lzy.okserver.task.*;
import com.lzy.okserver.upload.*;
import com.lzy.okgo.model.*;

class GlobalUploadListener implements XExecutor.OnTaskEndListener
{
	@Override
	public void onTaskEnd(Runnable p1)
	{
	}
}

class SendMessage {
	static class MessageToSend extends MessageData {
		public String upTag;
		public void setUpTag(String iTag) {upTag = iTag; }
		MessageToSend() {
			upTag = "" + this.hashCode();
		}
		public String toJson() {
			return new Gson().toJson(this);
		}
	}
	MessageToSend tosend;
	public void send() {
		Communication.getComm(MyApplication.getMyApplication().getContext()).upload(
			Communication.UPLOAD,
			tosend.upTag,
			Utils.ContentPut("auth", Config.get(MyApplication.getMyApplication().getContext()).data.user.auth), 
			new UploadListener<String>(tosend.upTag) {
				@Override
				public void onStart(Progress p1)
				{
					// TODO: Implement this method
				}
				@Override
				public void onProgress(Progress p1)
				{
					// TODO: Implement this method
				}
				@Override
				public void onError(Progress p1)
				{
					// TODO: Implement this method
				}
				@Override
				public void onFinish(String p1, Progress p2)
				{
					// TODO: Implement this method
				}
				@Override
				public void onRemove(Progress p1)
				{
					// TODO: Implement this method
				}
			});
	}
}

