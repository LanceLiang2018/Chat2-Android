package lance.liang.group.latina;

import android.*;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.lzy.okgo.callback.*;
import com.lzy.okgo.model.*;
import com.tbruyelle.rxpermissions2.*;
import io.reactivex.disposables.*;
import io.reactivex.Observer;
import java.util.*;

public class MyFiles extends AppCompatActivity
{
	List<FileData> data_files = new ArrayList<FileData>();
	ListView list_files;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(Config.get(this).data.settings.theme);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfiles);
		
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setTitle("我的文件");
		
		RxPermissions rxPermissions = new RxPermissions(MyFiles.this);
		rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
			.subscribe(new Observer<Boolean>() {
				@Override
				public void onError(Throwable p1)
				{MyFiles.this.finish();}
				@Override
				public void onComplete()
				{}
				@Override
				public void onSubscribe(Disposable d)
				{}
				@Override
				public void onNext(Boolean aBoolean)
				{}});
		
		
		list_files = (ListView) findViewById(R.id.myfilesListView_files);
		
		Communication.getComm(this).postWithAuth(Communication.GET_FILES, new ContentValues(), 
			new StringCallback() {
				@Override
				public void onSuccess(Response<String> p1) {
					ResultData result = new Gson().fromJson(p1.body(), ResultData.class);
					if (result.code != 0) {
						new AlertDialog.Builder(MyFiles.this).setMessage(result.message).show();
						return;
					}
					for (FileData file: result.data.files)
						data_files.add(file);
					list_files.setAdapter(new FilesAdapter(getApplicationContext(), data_files));
				}
			});
		
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
