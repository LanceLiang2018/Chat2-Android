package lance.liang.group.latina;

import android.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AbsListView.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.engine.*;
import com.bumptech.glide.load.resource.drawable.*;
import com.bumptech.glide.request.*;
import com.bumptech.glide.request.target.*;
import com.bumptech.glide.request.transition.*;
import com.google.gson.*;
import com.lzy.okgo.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.tbruyelle.rxpermissions2.*;
import io.reactivex.*;
import io.reactivex.disposables.*;
import java.util.*;
import jp.wasabeef.glide.transformations.*;

import android.support.v7.app.ActionBar;
import android.view.View.OnClickListener;
import io.reactivex.Observer;

public class MusicDownloader extends AppCompatActivity
{
	private Button btn_search_songs, btn_search_playlists;
	private EditText text_search_key;
	private ListView list;
	private MusicAdapter madp;
	ModeCallback mcallback;
	
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
		
		madp = new MusicAdapter(this);
		mcallback = new ModeCallback();

		btn_search_songs = (Button) findViewById(R.id.musicdownloaderButton_search_songs);
		btn_search_playlists = (Button) findViewById(R.id.musicdownloaderButton_search_playlists);
		text_search_key = (EditText) findViewById(R.id.musicdownloaderEditText_text);
		list = (ListView) findViewById(R.id.musicdownloaderListView);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		list.setMultiChoiceModeListener(mcallback);
		
		btn_search_songs.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					getSongs();
				}
			});
		text_search_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView p1, int p2, KeyEvent p3) {
					if (text_search_key.getText().toString().endsWith("\n"))
						text_search_key.setText(text_search_key.getText().toString().substring(0, text_search_key.getText().toString().length()));
					getSongs();
					return false;
				}
			});
		list.setAdapter(madp);
		
		if (int_playlist_id != 0)
			getPlaylistSongs(int_playlist_id);
	}
	
	void getPlaylistSongs(int id)
	{
		NetEaseAPI.getPlaylist(this, id, new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					NetEasePlaylistContentData data = new Gson().fromJson(p1.body(), NetEasePlaylistContentData.class);
					if (data.code != 200) return;
					View inview = LayoutInflater.from(MusicDownloader.this).inflate(R.layout.playlist_head, null);
					TextView text_name = (TextView) inview.findViewById(R.id.playlistheadTextView_name),
						text_id = (TextView) inview.findViewById(R.id.playlistheadTextView_id), 
						text_desc = (TextView) inview.findViewById(R.id.playlistheadTextView_desc), 
						text_creator = (TextView) inview.findViewById(R.id.playlistheadTextView_creator);
					ImageView image = (ImageView) inview.findViewById(R.id.playlistheadImageView_image);
					final RelativeLayout bg = (RelativeLayout) inview.findViewById(R.id.playlistheadRelativeLayout_bg);
					
					text_name.setText(data.playlist.name);
					text_id.setText("" + data.playlist.id);
					text_creator.setText(data.playlist.creator.nickname);
					text_desc.setText(data.playlist.description);
					
					Glide.with(MusicDownloader.this).load(data.playlist.coverImgUrl)
						.apply(new RequestOptions().centerCrop()
							.diskCacheStrategy(DiskCacheStrategy.DATA)
							.transform(new BlurTransformation(25)))
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(new SimpleTarget<Drawable>() {
							@Override
							public void onResourceReady(Drawable p1, Transition<? super Drawable> p2) {
								bg.setBackgroundDrawable(p1);
							}
						});
					
					Glide.with(MusicDownloader.this).load(data.playlist.coverImgUrl)
						.apply(new RequestOptions().centerCrop()
							   .diskCacheStrategy(DiskCacheStrategy.DATA))
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(image);
					list = (ListView) findViewById(R.id.musicdownloaderListView);
					list.addHeaderView(inview);
				}
			});
	}
	
	AdapterView.OnItemClickListener music_click = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			if (p3 < 0) return;
			Song bean = madp.list_data.get(p3);
//			NetEaseAPI.download(MusicDownloader.this, bean.id, bean.name);
			NetEaseAPI.lisentOnline(MusicDownloader.this, bean.id, new StringCallback() {
					@Override
					public void onSuccess(Response<String> p1) {
						NetEaseMusicDownloadData data = new Gson().fromJson(p1.body(), NetEaseMusicDownloadData.class);
						if (data.code != 200) return;
						Intent mIntent = new Intent();
						mIntent.setAction(android.content.Intent.ACTION_VIEW);
						Uri uri = Uri.parse(data.data.get(0).url);
						mIntent.setDataAndType(uri , "audio/mp3");
						startActivity(mIntent);
					}
				});
		}
	};
	
	void getSongs() {
		NetEaseAPI.getSongs(this, text_search_key.getText().toString(), 0, new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					NetEaseMusicData data = new Gson().fromJson(p1.body(), NetEaseMusicData.class);
					if (data.code != 200) return;
					list.setAdapter(madp);
					madp.list_data.clear();
					for (Song s: data.result.songs) {
						madp.list_data.add(s);
					}
					madp.notifyDataSetChanged();
					list.setOnItemClickListener(music_click);
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
		inflater.inflate(R.menu.music_menu, menu);
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
			case R.id.enter_mode:
				list.setItemChecked(0, true);
				list.clearChoices();
				mcallback.updateSelectedCount();
				break;
			default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class ModeCallback implements MultiChoiceModeListener {

        View actionBarView;
        TextView selectedNum;

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        //退出多选模式时调用
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            list.clearChoices();
        }

        //进入多选模式调用，初始化ActionBar的菜单和布局
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.multiple_mode_menu, menu);
            if(actionBarView == null) {
                actionBarView = LayoutInflater.from(MusicDownloader.this).inflate(R.layout.actionbar_view, null);
                selectedNum = (TextView) actionBarView.findViewById(R.id.selected_num);
            }
            mode.setCustomView(actionBarView);
            return true;
        }

        //ActionBar上的菜单项被点击时调用
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.select_all:
                    for(int i = 0; i < madp.getCount(); i++) {
                        list.setItemChecked(i, true);
                    }
                    updateSelectedCount();
                    madp.notifyDataSetChanged();
                    break;
                case R.id.unselect_all:
                    list.clearChoices();
                    updateSelectedCount();
                    madp.notifyDataSetChanged();
                    break;
				case R.id.select_download:
					for (int i=0; i<madp.list_data.size(); i++) {
						if (!list.isItemChecked(i)) continue;
						final Song bean = madp.list_data.get(i);
						NetEaseAPI.download(MusicDownloader.this, bean.id, Song.getFilename(bean));
					}
					list.setItemChecked(0, false);
					list.clearChoices();
					mcallback.updateSelectedCount();
					break;
            }
            return true;
        }

        //列表项的选中状态被改变时调用
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
											  long id, boolean checked) {
            updateSelectedCount();
            mode.invalidate();
            madp.notifyDataSetChanged();
        }

        public void updateSelectedCount() {
            int selectedCount = list.getCheckedItemCount();
            selectedNum.setText(selectedCount + "");
        }
    }
	
	class MusicAdapter extends BaseAdapter
	{
		public List<Song> list_data = new ArrayList<Song>();
		Context context;
		MusicAdapter(Context context) {
			this.context = context;
		}
		@Override
		public int getCount() {
			return list_data.size();
		}
		@Override
		public Object getItem(int p1) {
			return list_data.get(p1);
		}
		@Override
		public long getItemId(int p1) {
			return p1;
		}
		@Override
		public View getView(int p1, View p2, ViewGroup p3) {
			//if (p1 < 0) return new View(context);
			Song bean = list_data.get(p1);
			LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_music, null);
			TextView text_name = (TextView) view.findViewById(R.id.itemmusicTextView_name),
				text_id = (TextView) view.findViewById(R.id.itemmusicTextView_id),
				text_artists = (TextView) view.findViewById(R.id.itemmusicTextView_artist);
//		ImageView image = (ImageView) view.findViewById(R.id.itemmusicImageView_image);
			text_name.setText(bean.name);
			text_id.setText("" + bean.id);
			String artists = "";
			for (Song.Artist a: bean.artists)
				artists = artists + a.name + "/";
			artists = artists.substring(0, artists.length() - 1);
			text_artists.setText(artists);
//		Glide.with(context).load(bean.album.artist.img1v1Url)
//			.apply(new RequestOptions().centerCrop())
//			.transition(DrawableTransitionOptions.withCrossFade())
//			.into(image);
			if (list.isItemChecked(p1))
				view.setBackgroundColor(Color.parseColor("#C0C030"));
			else
				view.setBackgroundColor(Color.TRANSPARENT);
			return view;
		}
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

class Song {
	public static class Artist {
		public String id, name, img1v1Url;
	}
	public static class Album {
		public static class Artist2 {
			public String img1v1Url;
		}
		public Artist2 artist;
		public String name;
	}
	public String name;
	public int id;
	public List<Artist> artists;
	public Album album;
	static public String getFilename(Song s) {
		String filename = "";
		for (Song.Artist a: s.artists)
			filename = filename + a.name + "、";
		filename = filename.substring(0, filename.length() - 1);
		filename = filename + " - " + s.name + ".mp3";
		return filename;
	}
}
	

class NetEaseMusicData {
	public int code;
	public Result result;
	public static class Result {
		public List<Song> songs;
		public int songCount;
	}
}

class PlayList {
	public static class Creator {
		public String nickname;
	}
	public String name, coverImgUrl, description;
	public int id;
	public Creator creator;
}
	

class NetEasePlaylistData {
	public int code;
	public Result result;
	public static class Result {
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
		static public class Creator {
			public String nickname;
		}
		public Creator creator;
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
	public List<Data> data;
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
								Uri uri = Uri.parse(data.data.get(0).url);
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
	
	static public void lisentOnline(Context context, int id, StringCallback callback) {
		OkGo.<String>get(url_download + id)
			.execute(callback);		
	}
	
}

