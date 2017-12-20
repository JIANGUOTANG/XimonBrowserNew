package com.thecamhi.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hichip.base.HiLog;
import com.hichip.callback.ICameraDownloadCallback;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.thecamhi.base.HiTools;
import com.thecamhi.base.TitleView;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.ximon.light.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
//import android.content.DialogInterface.OnClickListener;

public class VideoOnlineActivity extends HiActivity implements ICameraIOSessionCallback,OnItemClickListener ,ICameraDownloadCallback{


	public final static int SEARCH_ACTIVITY_RESULT=1;
	public final static String SEARCH_ACTIVITY_START_TIME="START TIME";
	public final static String SEARCH_ACTIVITY_END_TIME="END TIME";

	public final static String VIDEO_PLAYBACK_START_TIME="VIDEO START TIME";
	public final static String VIDEO_PLAYBACK_END_TIME="VIDEO END TIME";


	private MyCamera mCamera;

	private ListView list_video_online;

	private List<HiChipDefines.HI_P2P_FILE_INFO> file_list = Collections.synchronizedList(new ArrayList<HiChipDefines.HI_P2P_FILE_INFO>());
	private static List<HiChipDefines.HI_P2P_FILE_INFO> backgroundFile_list= new ArrayList<HiChipDefines.HI_P2P_FILE_INFO>();

	private VideoOnlineListAdapter adapter;
	private String path;
	private boolean isBackground=false;
	private int recordposition;
	private String download_path;
	private String fileName;
	private LinearLayout layout_search_no_result,layout_search_duration;
	private TextView tv_search_duration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_online);

		Bundle bundle = this.getIntent().getExtras();
		String uid  = bundle.getString(HiDataValue.EXTRAS_KEY_UID);

		for(MyCamera camera: HiDataValue.CameraList) {
			if(camera.getUid().equals(uid)) {
				mCamera = camera;
				break;
			}
		}
		initView();
		searchVideo();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mCamera != null) {
			mCamera.registerIOSessionListener(this);
			mCamera.registerDownloadListener(this);

		}
	}




	@Override
	public void onPause() {
		super.onPause();
		if(mCamera != null) {
			mCamera.unregisterIOSessionListener(this);
			mCamera.unregisterDownloadListener(this);
		}
	}


	private void initView() {
		TitleView nb = (TitleView)findViewById(R.id.title_top);

		nb.setTitle(getString(R.string.title_online_video));
		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);
		nb.setRightBtnText(getString(R.string.btn_search));

		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:
					Intent intent=new Intent(VideoOnlineActivity.this,SearchSDCardVideoActivity.class);
					intent.putExtra(HiDataValue.EXTRAS_KEY_UID,mCamera.getUid());
					startActivityForResult(intent, SEARCH_ACTIVITY_RESULT);
					break;
				}
			}
		});

		adapter = new VideoOnlineListAdapter(this);
		list_video_online = (ListView) findViewById(R.id.list_video_online);
		list_video_online.setAdapter(adapter);
		list_video_online.setOnItemClickListener(this);

		layout_search_no_result=(LinearLayout)findViewById(R.id.layout_search_no_result);
		layout_search_duration=(LinearLayout)findViewById(R.id.layout_search_duration);

	}


	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mCamera.stopDownloadRecording();
		dismissLoadingProgress();
	}

	private void searchVideo() {


		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		//		HiLog.v("yeatearday  time: " + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH)
		//				+ " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));

		tv_search_duration=(TextView)findViewById(R.id.tv_search_duration);
		String timeStr=	HiTools.sdfTimeDay(System.currentTimeMillis())  +" - "+	 HiTools.sdfTimeSec(System.currentTimeMillis());
		tv_search_duration.setText(timeStr);

		if(mCamera!=null){
			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_PB_QUERY_START, HiChipDefines.HI_P2P_S_PB_LIST_REQ.parseContent
					(0, cal.getTimeInMillis(), System.currentTimeMillis(),HiChipDefines.HI_P2P_EVENT_ALL));
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode==RESULT_OK){
			Bundle bundle=intent.getBundleExtra(HiDataValue.EXTRAS_KEY_DATA);
			long startTime=bundle.getLong(SEARCH_ACTIVITY_START_TIME);
			long endTime=bundle.getLong(SEARCH_ACTIVITY_END_TIME);
			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_PB_QUERY_START, HiChipDefines.HI_P2P_S_PB_LIST_REQ.parseContent(0, startTime, endTime,HiChipDefines.HI_P2P_EVENT_ALL));

			file_list.clear();
			adapter.notifyDataSetChanged();

			String timeStr=	HiTools.sdfTimeSec(startTime)  +" - "+	 HiTools.sdfTimeSec(endTime);
			tv_search_duration.setText(timeStr);

			mCamera.registerIOSessionListener(this);
		}
	}

	@Override
	public void receiveIOCtrlData(HiCamera arg0, int arg1, byte[] arg2, int arg3) {

		Bundle bundle = new Bundle();
		bundle.putByteArray(HiDataValue.EXTRAS_KEY_DATA, arg2);
		Message msg = handler.obtainMessage();
		msg.what = HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL;
		msg.obj = arg0;
		msg.arg1 = arg1;
		msg.arg2 = arg3;
		msg.setData(bundle);
		handler.sendMessage(msg);

	}

	@Override
	public void callbackDownloadState(HiCamera camera, int total, int curSize,
			int state, String path) {
		if(camera!=mCamera)return;
		Bundle bundle=new Bundle();
		bundle.putInt("total", total);
		bundle.putInt("curSize", curSize);
		bundle.putString("path", path);

		Message msg=handler.obtainMessage();
		msg.what = HiDataValue.HANDLE_MESSAGE_DOWNLOAD_STATE;
		msg.arg1=state;
		msg.setData(bundle);
		handler.sendMessage(msg);

	}


	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {

		//		Message msg = handler.obtainMessage();
		//		msg.what = HiDataValue.HANDLE_MESSAGE_SESSION_STATE;
		//		msg.arg1 = arg1;
		//		msg.obj = arg0;
		//		handler.sendMessage(msg);
	}    


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HiDataValue.HANDLE_MESSAGE_SESSION_STATE:
				break;
			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL:
			{
				if(msg.arg2==0) {

					//					MyCamera camera = (MyCamera)msg.obj;
					Bundle bundle = msg.getData();
					byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);

					switch (msg.arg1) {
					case HiChipDefines.HI_P2P_PB_QUERY_START:
						if (data.length >= 12) {

							byte flag = data[8];
							int cnt = data[9];
							//							Log.v("camera", "------------------flag:"+flag);
							if (cnt > 0) {
								for (int i = 0; i < cnt; i++) {

									int pos = 12;
									int size = HiChipDefines.HI_P2P_FILE_INFO.sizeof();
									byte[] t = new byte[24];
									System.arraycopy(data, i*size + pos, t, 0, 24);
									HiChipDefines.HI_P2P_FILE_INFO file_info = new HiChipDefines.HI_P2P_FILE_INFO(t);

									file_list.add(file_info);
								}
								adapter.notifyDataSetChanged();
							}

							if(file_list!=null&&file_list.size()>0){
								layout_search_no_result.setVisibility(View.GONE);
								layout_search_duration.setVisibility(View.VISIBLE);
							}

							if(flag == 1) {
								//								mIsSearchingEvent = false;
								//								eventListView.removeFooterView(loadingView);
								//								eventListView.removeFooterView(noResultView);
								//								if (file_list.size() == 0)
								//									Toast.makeText(EventListActivity.this, EventListActivity.this.getText(R.string.tips_search_event_no_result), Toast.LENGTH_SHORT).show();
								//								Log.v("hichip", "list count:"+ list.size() + "          datalen:"+datalen);
							}
						}
						break;
					}
				}
			}

			break;
			case HiDataValue.HANDLE_MESSAGE_DOWNLOAD_STATE:
			{
				Bundle bundle=msg.getData();
				switch (msg.arg1) {
				case DOWNLOAD_STATE_START:
					dismissLoadingProgress();
					showingProgressDialog();

					path=bundle.getString("path");
					break;
				case DOWNLOAD_STATE_DOWNLOADING:




					float d=bundle.getInt("curSize")*100/bundle.getInt("total");
					if(d>100){
						d=100;
					}
					int rate=Math.round(d);
					String rateStr="";
					if(rate<10){
						rateStr=" "+rate+"%";
					}else{
						rateStr=rate+"%";
					}
					prs_loading.setProgress(rate);

					rate_loading_video.setText(rateStr);

					break;
				case DOWNLOAD_STATE_END:
					dlgBuilder.dismiss();
					break;
				case DOWNLOAD_STATE_ERROR_PATH:

					break;

				}



			}
			break;

			}
		}
	};

	private AlertDialog dlgBuilder;
	private SeekBar prs_loading;
	private TextView rate_loading_video;
	private AlertDialog.Builder dlg ;
	protected void showingProgressDialog() {
		View customView = getLayoutInflater().inflate(R.layout.popview_loading_video,
				null, false);

		dlg = new AlertDialog.Builder(this);
		dlgBuilder = dlg.create();
		dlgBuilder.setView(customView);
		dlgBuilder.setTitle(getString(R.string.tips_warning));
		dlgBuilder.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent keyEvent) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if(keyEvent.getAction()==KeyEvent.ACTION_UP){
						cancelDownloadVideo();
					}

					break;
				}
				return true;
			}

		});

		/*mPopupWindow = new PopupWindow(customView);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);               
		mPopupWindow.setHeight(LayoutParams.MATCH_PARENT);    */

		dlgBuilder.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface d) {

				mCamera.stopDownloadRecording();
			}
		});


		// height = 80

		int offsetx = HiTools.dip2px(this, 0);
		int location[] = new int[2];

		int btnh = HiTools.dip2px(this, 50 + 80/2);

		prs_loading = (SeekBar)customView.findViewById(R.id.sb_downloading_video);
		prs_loading.setEnabled(false);
		prs_loading.setMax(100);
		prs_loading.setProgress(0);

		Button download_background=(Button)customView.findViewById(R.id.download_background_btn_downloading_video);
		download_background.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				//	StartBgService();

			}
		});


		rate_loading_video=(TextView)customView.findViewById(R.id.rate_loading_video);
		Button cancel_btn_downloading_video=(Button)customView.findViewById(R.id.cancel_btn_downloading_video);
		cancel_btn_downloading_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				cancelDownloadVideo();

			}
		});


		dlgBuilder.show();	


	}


	private void deleteLoadingFile() {
		File file=new File(path);
		file.delete();
	}

	public void cancelDownloadVideo(){

		showAlertDialog();

	}

	public void showAlertDialog(){
		AlertDialog.Builder builder=new AlertDialog.Builder(VideoOnlineActivity.this);

		builder.setTitle(getString(R.string.tips_warning));
		builder.setMessage(getResources().getString(R.string.tips_cancel_download_file));
		builder.setPositiveButton(getString(R.string.btn_ok),new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				deleteLoadingFile();
				dlgBuilder.dismiss();
			}
		});
		builder.setNegativeButton(getString(R.string.cancel),null);
		builder.show();
	}


	public class VideoOnlineListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public VideoOnlineListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return file_list.size();
		}

		public Object getItem(int position) {
			return file_list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {

			if (file_list.size() == 0)
				return false;
			return super.isEnabled(position);
		}

		@SuppressLint("InflateParams")
		public View getView(int position, View convertView, ViewGroup parent) {

			final HiChipDefines.HI_P2P_FILE_INFO evt = (HiChipDefines.HI_P2P_FILE_INFO) getItem(position);

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.list_video_online, null);

				holder = new ViewHolder();
				holder.event = (TextView) convertView.findViewById(R.id.txt_event);
				holder.time = (TextView) convertView.findViewById(R.id.txt_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final String[] mTextArray = getResources().getStringArray(R.array.online_recording_type);

			if(evt.EventType <= 3 && evt.EventType>=0)
				holder.event.setText(mTextArray[evt.EventType]);
			else {
				holder.event.setText("");
			}

			holder.time.setText(evt.sStartTime.toString() + " - "+evt.sEndTime.toString());

			return convertView;

		}// getView()

		private final class ViewHolder {
			public TextView event;
			public TextView time;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position,
			long id) {
		playbackRecording(position);

		/*@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_video_online,
				null, false);
		final PopupWindow mPopupWindow = new PopupWindow(customView);;
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);               
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    

		mPopupWindow.showAtLocation(view, Gravity.CENTER , 0, 0);

		Button btn_play_video_online = (Button)customView.findViewById(R.id.btn_play_video_online);
		Button btn_downlinad_video_online = (Button)customView.findViewById(R.id.btn_downlinad_video_online);

		btn_play_video_online.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
				playbackRecording(position);
			}
		});

		btn_downlinad_video_online.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
				showLoadingProgress();
				downloadRecording(position);
			}
		});*/
	}



	private void downloadRecording(int position) {
		HiChipDefines.HI_P2P_FILE_INFO file_infos = file_list.get(position);

		if(HiTools.isSDCardValid()){

			File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()  + "/");

			File downloadFolder = new File(rootFolder.getAbsolutePath() + "/download/");
			File uidFolder=new File(downloadFolder.getAbsolutePath()+"/"+mCamera.getUid()+"");
			if(!rootFolder.exists()){
				rootFolder.mkdir();
			}
			if(!downloadFolder.exists()){
				downloadFolder.mkdir();
			}
			if(!uidFolder.exists()){
				uidFolder.mkdir();
			}

			download_path = uidFolder.getAbsoluteFile() + "/" ;


			//	String[] time=file_info.sStartTime.toString().split(" ");

			//����UID�ļ���
			fileName=splitFileName(file_infos.sStartTime.toString());


			/*if(oldFile.exists()){
			HiToast.showToast(VideoOnlineActivity.this, str);
			return;
		}*/

			mCamera.startDownloadRecording(file_infos.sStartTime, download_path, fileName);

			HiLog.e("download path:"+download_path+fileName);
		}else{
			Toast.makeText(VideoOnlineActivity.this, getText(R.string.tips_no_sdcard).toString(), Toast.LENGTH_SHORT).show();
		}
	}

	private String splitFileName(String str){

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time=0;
		try {
			time=sdf.parse(str).getTime();
		} catch (ParseException e) {

			e.printStackTrace();
		}


		SimpleDateFormat sf2=new SimpleDateFormat("yyyyMMdd_HHmmss");

		return sf2.format(time);
	}


	private void playbackRecording(int position) {

		recordposition=position;
		if(mCamera == null)
			return;

		HiChipDefines.HI_P2P_FILE_INFO file_info = file_list.get(position);

		Bundle extras = new Bundle();
		extras.putString(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
		byte[] b_startTime = file_info.sStartTime.parseContent();
		//		byte[] b_startTime = STimeDay.parseContent(file_info.sStartTime.year, file_info.sStartTime.month, file_info.sStartTime.day, file_info.sStartTime.wday, file_info.sStartTime.hour, file_info.sStartTime.minute, file_info.sStartTime.second);
		extras.putByteArray("st", b_startTime);

		long startTimeLong=file_info.sStartTime.getTimeInMillis();
		long endTimeLong=file_info.sEndTime.getTimeInMillis();

		long pbtime = startTimeLong-endTimeLong;
		extras.putLong("pb_time", pbtime);
		extras.putLong(VIDEO_PLAYBACK_START_TIME, startTimeLong);
		extras.putLong(VIDEO_PLAYBACK_END_TIME, endTimeLong);


		Intent intent = new Intent();
		intent.putExtras(extras);
		intent.setClass(VideoOnlineActivity.this,PlaybackOnlineActivity.class);
		startActivity(intent);
	}


}
