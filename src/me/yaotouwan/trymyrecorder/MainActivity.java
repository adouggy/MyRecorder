package me.yaotouwan.trymyrecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private static final String VIDEO_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "ade";

	private Button mStart;
	private Button mStop;
	private ListView mList;
	
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss", Locale.CHINA);  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d("DEBUG", VIDEO_PATH);
		
		File f = new File(VIDEO_PATH);
		if( f!=null && !f.exists() ){
			f.mkdir();
		}

		mStart = (Button) findViewById(R.id.txt_click_start);
		mStart.setOnClickListener(this);

		mStop = (Button) findViewById(R.id.txt_click_stop);
		mStop.setOnClickListener(this);

		mList = (ListView) findViewById(R.id.list);
		mList.setAdapter(new VideoAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_click_start:
			MyRecorder.startRecording(VIDEO_PATH + File.separator + format.format(new Date(System.currentTimeMillis())) + ".mp4");
			break;
		case R.id.txt_click_stop:
			MyRecorder.stopRecording();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					((VideoAdapter) mList.getAdapter()).refreshData();
				}
			}, 100);
			break;
		}
	}

	private class VideoAdapter extends BaseAdapter {
		private ArrayList<String> data = null;

		public VideoAdapter() {
			refreshData();
		}

		public void refreshData() {
			if (data == null)
				data = new ArrayList<String>();
			else
				data.clear();

			File dir = new File(VIDEO_PATH);
			File[] files = null;
			if (dir.exists() && dir.isDirectory()) {
				files = dir.listFiles();
			}
			if (files != null) {
				for (File f : files) {
					try {
						Log.d("DEBUG", f.getCanonicalPath());
						if (f != null && f.exists() && !f.isDirectory() && f.getCanonicalPath().endsWith(".mp4")) {
							data.add(f.getCanonicalPath());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			Collections.sort(data);
			Collections.reverse(data);

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (data == null)
				return 0;
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_video, mList, false);
				holder.name = (TextView) convertView.findViewById(R.id.txt_video_name);
				holder.play = (Button) convertView.findViewById(R.id.btn_play);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final File f = new File(data.get(position));
			holder.name.setText(f.getName() + "(" + String.format("%.2f", f.length()/1024f) + "K)");
			holder.play.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri contentUri = null;
					try {
						contentUri = Uri.parse("file://" + f.getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(contentUri, "video/mp4");
					
					
					MainActivity.this.startActivity(intent);
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					new AlertDialog.Builder(MainActivity.this).setTitle("Delete " + f.getName() + "?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							f.delete();
							refreshData();
						}
					}).show();
					return false;
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView name;
			Button play;
		}

	}

}
