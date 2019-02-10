package lance.liang.group.latina;

import android.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import com.google.gson.*;
import com.lzy.okgo.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.tbruyelle.rxpermissions2.*;
import io.reactivex.disposables.*;
import io.reactivex.Observer;
import java.util.*;
import lance.liang.group.latina.*;

import android.support.v7.app.ActionBar;
import android.widget.*;

public class MusicDownloader extends AppCompatActivity
{
	private Button btn_search_songs, btn_search_playlists;
	private EditText text_search_key;
	private ListView list;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_downloader);

		int int_playlist_id = 0;
		try {
			int_playlist_id = getIntent().getExtras().getInt("playlist_id");
		} catch (Exception e) {}
		
		ActionBar bar = getSupportActionBar();
		bar.setTitle("Music Downloader");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);

		btn_search_songs = (Button) findViewById(R.id.musicdownloaderButton_search_songs);
		btn_search_playlists = (Button) findViewById(R.id.musicdownloaderButton_search_playlists);
		text_search_key = (EditText) findViewById(R.id.musicdownloaderEditText_text);
		list = (ListView) findViewById(R.id.musicdownloaderListView);
		
		if (int_playlist_id != 0)
			getPlaylistSongs(int_playlist_id);
	}
	
	void getPlaylistSongs(int id)
	{
		NetEaseAPI.getPlaylist(this, id, new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					NetEasePlaylistContentData data = new Gson().fromJson(p1.body(), NetEasePlaylistContentData.class);
					
				}
			});
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}

class MusicDownloaderList extends AppCompatActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_downloader);

		ActionBar bar = getSupportActionBar();
		bar.setTitle("Playlist Downloader");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);

	}

	@Override
	protected void onDestroy()
	{
		//dialog.dismiss();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}

class NetEaseMusicData {
	public int code;
	public Result result;
	public static class Result {
		public static class Songs {
			public static class Artist {
				public String id, name, img1v1Url;
			}
			public String name;
			public int id;
			public List<Artist> artists;	
		}
		public int songCount;
	}
}

class NetEasePlaylistData {
	public int code;
	public Result result;
	public static class Result {
		public static class PlayList {
			public static class Creator {
				public String nickname;
			}
			public String name, coverImgUrl, description;
			public int id;
			public Creator creator;
		}
		public int playlistCount;
		public List<PlayList> playlists;
	}
}

class NetEasePlaylistContentData {
	public int code;
	static public class Playlist {
		static public class Track {
			static public class Ar {
				public int id;
				public String name;
			}
			static public class Al {
				public String name, picUrl;
				public int id;
			}
			public String name;
			public int id;
			public List<Ar> ar;
			public Al al;
		}
		public String coverImgUrl, description, name;
		public int id;
		public List<Track> tracks;
	}
	public Playlist playlist;
}

class NetEaseMusicDownloadData {
	public int code;
	static public class Data {
		public String url;
	}
	public Data data;
}

class NetEaseAPI {
	static String SONG = "SONG", PLAYLIST = "PLAYLIST",
		url_search = "https://v1.hitokoto.cn/nm/search/", 
		url_playlist = "https://v1.hitokoto.cn/nm/playlist/", 
		url_download = "https://v1.hitokoto.cn/nm/url/";
	
	static public void getSongs(Context context, String key, int offset, StringCallback callback) {
		OkGo.<String>get(url_search + key)
			.params("type", SONG)
			.params("limit", "30")
			.params("offset", offset)
			.execute(callback);
	}
	
	static public void getPlaylists(Context context, String key, int offset, StringCallback callback) {
		OkGo.<String>get(url_search + key)
			.params("type", PLAYLIST)
			.params("limit", "30")
			.params("offset", offset)
			.execute(callback);
	}
	
	static public void getPlaylist(Context context, int id, StringCallback callback) {
		OkGo.<String>get(url_playlist + id)
			.execute(callback);
	}
	
	static public void download(final Activity context, int id, final String filename) {
		StringCallback callback = new StringCallback() {
			@Override
			public void onSuccess(Response<String> p1) {
				final NetEaseMusicDownloadData data = new Gson().fromJson(p1.body(), NetEaseMusicDownloadData.class);
				if (data.code == 200) {
					RxPermissions rxPermissions = new RxPermissions(context);
					rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
						.subscribe(new Observer<Boolean>() {
							@Override
							public void onError(Throwable p1) {}
							@Override
							public void onComplete() {}
							@Override
							public void onSubscribe(Disposable d) {}
							@Override
							public void onNext(Boolean aBoolean) {
								DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
								Uri uri = Uri.parse(data.data.url);
								DownloadManager.Request request = new DownloadManager.Request(uri);
								request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
								request.setDestinationInExternalPublicDir("Music/", filename);
								request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
								downloadManager.enqueue(request);
							}});
				}
			}
		};
		OkGo.<String>get(url_download + id)
			.execute(callback);		
	}
	
}

