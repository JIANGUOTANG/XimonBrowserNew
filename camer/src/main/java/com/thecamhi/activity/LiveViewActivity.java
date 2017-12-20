package com.thecamhi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hichip.base.HiLog;
import com.hichip.base.HiThread;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.callback.ICameraPlayStateCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.sdk.HiChipP2P;
import com.thecamhi.base.DatabaseManager;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.HiTools;
import com.thecamhi.base.MyLiveViewGLMonitor;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.ximon.light.R;

import java.io.File;
import java.text.SimpleDateFormat;


public class LiveViewActivity extends HiActivity implements ICameraIOSessionCallback,ICameraPlayStateCallback,OnClickListener,OnTouchListener{

	//	private final static int 
	private final static int HANDLE_MESSAGE_PLAY_STATE = 0x90000001;
	private final static int HANDLE_MESSAGE_PROGRESSBAR_RUN = 0x90000002;
	private MyCamera mCamera;
	private MyLiveViewGLMonitor mMonitor;

	//	private CamHiApplication mApplication;

	private HiChipDefines.HI_P2P_S_MD_PARAM md_param = null;

	//	private HiChipDefines.HI_P2P_S_AUDIO_ATTR audio_attr = null;

	private int video_width;
	private int video_height;

	//private ProgressThread pthread = null;

	private ProgressBar prs_loading;
	private ImageView img_shade;

	private TextView btn_live_temp;
	private Button btn_live_rotation;
	private ImageView btn_live_listen;
	private ImageView btn_live_exit;
	private ImageView btn_live_mirror_flip;
	private ImageView btn_live_preset;
	private ImageView btn_live_record;
	private Button btn_live_setting;
	private ImageView btn_live_snapshot;
	private ImageView resolution_ratio;
	private ImageView btn_live_zoom_focus;
	private Button btn_microphone;
	//	private Button btn_ptz_left;
	//	private Button btn_ptz_right;
	//	private Button btn_ptz_up;
	//	private Button btn_ptz_down;
	private Button btn_live_alert;
	private Button btn_live_sun;
	private Button btn_live_music;
	private LinearLayout linearLayout1,lay_live_tools_bottom;
	private RadioGroup radio_quality;
	private TextView txt_recording;
	private RadioButton[] radio_quality_list;
	private PopupWindow mPopupWindow;

	private boolean isListening = false;
	private boolean isTalking = false;

	//private int mToolsBarVisibility = View.VISIBLE;


	private final static int RECORDING_STATUS_NONE = 0;
	private final static int RECORDING_STATUS_LOADING = 1;
	private final static int RECORDING_STATUS_ING = 2;
	private int mRecordingState = RECORDING_STATUS_NONE;
	private boolean visible=false;
	private int style;
	FrameLayout	live_view_screen=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		String	uid = bundle.getString(HiDataValue.EXTRAS_KEY_UID);
		//	style=bundle.getInt(HiDataValue.STYLE);


		//		mApplication = (CamHiApplication) getApplication(); 

		//通过遍历uid获取相应的摄像机
		for(MyCamera camera: HiDataValue.CameraList) {
			if(camera.getUid().equals(uid)) {
				mCamera = camera;
				mCamera.registerIOSessionListener(this);
				mCamera.registerPlayStateListener(this);

				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_DISPLAY_PARAM, null);
				HiChipDefines.HI_P2P_S_MD_PARAM mdparam = new HiChipDefines.HI_P2P_S_MD_PARAM(
						0,new HiChipDefines.HI_P2P_S_MD_AREA(HiChipDefines.HI_P2P_MOTION_AREA_1,0,0,0,0,0,0)
						);
				//获取移动侦测
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_MD_PARAM, mdparam.parseContent());

				//音频
				//	mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_AUDIO_ATTR, null);


				break;
			}
		}

		initView();

		//showLoadingShade();

	}

	private void setViewVisible(boolean visible){
		linearLayout1.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
		lay_live_tools_bottom.setVisibility(visible?View.VISIBLE:View.INVISIBLE);

		this.visible=!visible;

	}


	private void initView() {

		//int paper_resid = 0;


		/*	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.activity_live_view_portrait);
			paper_resid = R.drawable.img_baby_one;

		}*/
		//	else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		setContentView(R.layout.activity_live_view_landscape);
	//	paper_resid = R.drawable.img_baby_two;

		//	}
		/*	live_view_screen=(FrameLayout)findViewById(R.id.live_view_screen);

		live_view_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				//setViewVisible(visible);

			}
		});*/

		//监控的控件绑定camera
		mMonitor = (MyLiveViewGLMonitor)findViewById(R.id.monitor_live_view);
		mMonitor.setOnTouchListener(this);
		mMonitor.setCamera(mCamera);
		//开始播放
		mCamera.setLiveShowMonitor(mMonitor);


		btn_live_exit = (ImageView)findViewById(R.id.btn_live_exit);
		btn_live_exit.setOnClickListener(this);

		linearLayout1=(LinearLayout)findViewById(R.id.linearLayout1);
		lay_live_tools_bottom=(LinearLayout)findViewById(R.id.lay_live_tools_bottom);

		btn_live_snapshot = (ImageView)findViewById(R.id.btn_live_snapshot);
		btn_live_snapshot.setOnClickListener(this);

		btn_live_record = (ImageView)findViewById(R.id.btn_live_record);
		btn_live_record.setOnClickListener(this);

		txt_recording = (TextView)findViewById(R.id.txt_recording);

		btn_live_listen = (ImageView)findViewById(R.id.btn_live_listen);
		btn_live_listen.setOnClickListener(this);

		btn_microphone = (Button)findViewById(R.id.btn_microphone);
		btn_microphone.setOnTouchListener(this);
		btn_microphone.setVisibility(View.GONE);

		resolution_ratio=(ImageView)findViewById(R.id.resolution_ratio);
		resolution_ratio.setOnClickListener(this);

		btn_live_zoom_focus = (ImageView)findViewById(R.id.btn_live_zoom_focus);
		btn_live_zoom_focus.setOnClickListener(this);

		btn_live_preset = (ImageView)findViewById(R.id.btn_live_preset);
		btn_live_preset.setOnClickListener(this);

		btn_live_mirror_flip = (ImageView)findViewById(R.id.btn_live_mirror_flip);
		btn_live_mirror_flip.setOnClickListener(this);

		img_shade = (ImageView)findViewById(R.id.img_shade);

		/*btn_live_temp=(TextView)findViewById(R.id.btn_live_temp);
		prs_loading = (ProgressBar)findViewById(R.id.prs_loading);
		img_shade = (ImageView)findViewById(R.id.img_shade);
		btn_live_rotation = (Button)findViewById(R.id.btn_live_rotation);
		btn_live_rotation.setOnClickListener(this);




		btn_live_record = (Button)findViewById(R.id.btn_live_record);
		btn_live_record.setOnClickListener(this);
		btn_live_setting = (Button)findViewById(R.id.btn_live_setting);
		btn_live_setting.setOnClickListener(this);

		btn_live_alert = (Button)findViewById(R.id.btn_live_alert);
		btn_live_alert.setOnClickListener(this);
		btn_live_sun = (Button)findViewById(R.id.btn_live_sun);
		btn_live_sun.setOnClickListener(this);
		btn_live_music=(Button)findViewById(R.id.btn_live_music);
		btn_live_music.setOnClickListener(this);
		btn_live_zoom_focus = (Button)findViewById(R.id.btn_live_zoom_focus);
		btn_live_zoom_focus.setOnClickListener(this);

			btn_ptz_left = (Button)findViewById(R.id.btn_ptz_left);
		btn_ptz_left.setOnTouchListener(this);
		btn_ptz_right = (Button)findViewById(R.id.btn_ptz_right);
		btn_ptz_right.setOnTouchListener(this);
		btn_ptz_up = (Button)findViewById(R.id.btn_ptz_up);
		btn_ptz_up.setOnTouchListener(this);
		btn_ptz_down = (Button)findViewById(R.id.btn_ptz_down);
		btn_ptz_down.setOnTouchListener(this);

		lay_live_tools_bottom = (LinearLayout)findViewById(R.id.lay_live_tools_bottom);

		radio_quality = (RadioGroup)findViewById(R.id.radio_quality);



			if(style==MyCamera.STYLE_TIO){
			btn_live_sun.setVisibility(View.GONE);
			btn_live_snapshot.setVisibility(View.GONE);
			btn_live_record.setVisibility(View.GONE);
		}else if(style==MyCamera.STYLE_VIO){
			btn_live_preset.setVisibility(View.GONE);
		}*/

		/*	radio_quality_list = new RadioButton[2];
		radio_quality_list[0] = (RadioButton)findViewById(R.id.radio_quality_hd);
		radio_quality_list[0].setOnClickListener(this);

		radio_quality_list[1] = (RadioButton)findViewById(R.id.radio_quality_sd);
		radio_quality_list[1].setOnClickListener(this);

		for(int i=0;i<2;i++) {
			if(i == mCamera.getVideoQuality()) {
				radio_quality_list[i].setChecked(true);
			}
		}
		 */
		/*	if(mCamera.getStyle() == MyCamera.STYLE_TIO) {
			live_view_screen.removeView(mMonitor);
			img_shade.setVisibility(View.VISIBLE);
			prs_loading.setVisibility(View.GONE);

			img_shade.setBackgroundResource(paper_resid);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			img_shade.setLayoutParams(lp);

			//			android:id="@+id/radio_quality"

		}*/

		//setViewVisible(visible);


	}





	private void showLoadingShade() {

		//	prs_loading.setMax(100);
		//	prs_loading.setProgress(10);
		//	pthread = new ProgressThread();
		//	pthread.startThread();
		//		mCamera.startLiveShow(1, mMonitor);
	}

	private void displayLoadingShade() {


		/*if(pthread != null)
			pthread.stopThread();
		pthread = null;*/
		//	prs_loading .setVisibility(View.GONE);
		img_shade.setVisibility(View.GONE);
	}




	@Override
	protected void onResume() {
		super.onResume();
		//		HiLog.e("LiveViewActivity onResume");
		//		HiDataValue.isOnLiveView = true;
		if(mCamera != null) {
			//			HiLog.e("startLiveShow startLiveShow");
			mCamera.startLiveShow(mCamera.getVideoQuality(), mMonitor);
			mCamera.registerIOSessionListener(this);
			mCamera.registerPlayStateListener(this);
		}
	}




	@Override
	public void onPause() {
		super.onPause();

		HiLog.e("LiveViewActivity onPause");
		if(mCamera != null) {
			//			HiDataValue.isOnLiveView = false;
			//			mCamera.setAlarmState(0);	
			HiLog.e("LiveViewActivity onPause2");

			mCamera.stopLiveShow();
			mCamera.unregisterPlayStateListener(this);
			mCamera.unregisterIOSessionListener(this);
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();


		saveSnapshot();
		mCamera.stopListening();
		HiLog.e("----------------------LiveViewActivity onDestroy--------------------------");


	}




	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {


		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			HiLog.v("KEYCODE_BACK  finish");
			finish();
			break;


		}
		return super.onKeyDown(keyCode, event);
	}



	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {

			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL:
			{
				if(msg.arg2==0) {
					MyCamera camera = (MyCamera)msg.obj;
					Bundle bundle = msg.getData();
					byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
					
					switch (msg.arg1) {

					case HiChipDefines.HI_P2P_GET_DISPLAY_PARAM:
						
						display_param = new HiChipDefines.HI_P2P_S_DISPLAY(data);
					
						
						
						break;
					case HiChipDefines.HI_P2P_GET_MD_PARAM:
						HiChipDefines.HI_P2P_S_MD_PARAM md_param_temp = new HiChipDefines.HI_P2P_S_MD_PARAM(data);

						if(md_param_temp.struArea.u32Area == HiChipDefines.HI_P2P_MOTION_AREA_1)
						{
							md_param = md_param_temp;
						}
						break;

						//
						/*	case HiChipDefines.HI_P2P_GET_AUDIO_ATTR:
						audio_attr = new HiChipDefines.HI_P2P_S_AUDIO_ATTR(data);
						break;*/

					}
				}
			}
			break;

			//播放状态
			case HANDLE_MESSAGE_PLAY_STATE:
				Bundle bundle = msg.getData();
				int command = bundle.getInt("command");
				switch(command) {
				//接收到了开始的状态
				case ICameraPlayStateCallback.PLAY_STATE_START:

					video_width = bundle.getInt("width");
					video_height = bundle.getInt("height");

					//	resetMonitorSize();


					saveSnapshot();


					break;

					//本地录像开始
				case ICameraPlayStateCallback.PLAY_STATE_RECORDING_START:
					mRecordingState = RECORDING_STATUS_ING;
					txt_recording.setVisibility(View.VISIBLE);
					break;
					//本地录像结束
				case ICameraPlayStateCallback.PLAY_STATE_RECORDING_END:
					mRecordingState = RECORDING_STATUS_NONE;
					txt_recording.setVisibility(View.GONE);
					break;
				}



				break;
				/*	case HANDLE_MESSAGE_PROGRESSBAR_RUN:
				int cur = prs_loading.getProgress();
				//				HiLog.v("HANDLE_MESSAGE_PROGRESSBAR_RUN:"+cur);
				if(cur>=100) {
					prs_loading.setProgress(10);
				}
				else {
					prs_loading.setProgress(cur + 8);
				}
				break;*/
			}



		}
	};



	//保存国科的快照
	private void saveSnapshot() {
		

			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... arg0) {

					Bitmap frame = mCamera != null ? mCamera.getSnapshot(): null;
					DatabaseManager manager = new DatabaseManager(LiveViewActivity.this);
					if (frame!= null) {
						manager.updateDeviceSnapshotByUID(mCamera.getUid(), frame);
						mCamera.snapshot = frame;
					}

					return null;
				}


				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub

					//					Log.e("hichip", "onPostExecute");
					Intent intent = new Intent();
					intent.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
					sendBroadcast(intent);


					super.onPostExecute(result);
				}
			}.execute();

		
	}


	int moveX;
	int moveY;


	private void resetMonitorSize(boolean large,double move) {


		if(mMonitor.height==0&&mMonitor.width==0){

			initMatrix((int)mMonitor.screen_width,(int)mMonitor.screen_height);
		}

		moveX=(int) (move/2);
		moveY=(int) ((move*mMonitor.screen_height/mMonitor.screen_width)/2);

		if(large){
			HiLog.e( " larger and larger ");
			if(mMonitor.width<=2*mMonitor.screen_width&&mMonitor.height<=2*mMonitor.screen_height){

				mMonitor.left-=(moveX/2);
				mMonitor.bottom-=(moveY/2);
				mMonitor.width+=(moveX);
				mMonitor.height+=(moveY);
			}
		}else{
			HiLog.e( " smaller and smaller ");

			mMonitor.left+=(moveX/2);
			mMonitor.bottom+=(moveY/2);
			mMonitor.width-=(moveX);
			mMonitor.height-=(moveY);
		}

		if(mMonitor
				.left>0||mMonitor.width<(int)mMonitor.screen_width||
				mMonitor.height<(int)mMonitor.screen_height||mMonitor.bottom>0){
			initMatrix((int)mMonitor.screen_width,(int)mMonitor.screen_height);
		}


		HiLog.e("mMonitor.left="+mMonitor.left+" mMonitor.bottom="+mMonitor.bottom
				+"\n mMonitor.width="+mMonitor.width+" mMonitor.height="+mMonitor.height);

		/*	if(mMonitor.left<=(-mMonitor.width/2)){
			mMonitor.left=(-mMonitor.width/2);
		}
		if(mMonitor.left>0){
			mMonitor.left=0;
		}
		if(mMonitor.bottom<=(-mMonitor.height/2)){
			mMonitor.bottom=(-mMonitor.height/2);
		}
		if(mMonitor.bottom>0){
			mMonitor.bottom=0;
		}*/



		if(mMonitor.width>(int)mMonitor.screen_width){
			mMonitor.setState(1);
		}else{
			mMonitor.setState(0);
		}



		mMonitor.setMatrix(mMonitor.left, 
				mMonitor.bottom,
				mMonitor.width,
				mMonitor.height
				);


	}

	private void initMatrix(int screen_width,int screen_height){
		mMonitor.left=0;
		mMonitor.bottom=0;

		mMonitor.width=screen_width;
		mMonitor.height=screen_height;
	}



	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.btn_live_listen) {
			if (!checkPermission(Manifest.permission.RECORD_AUDIO) ||
					!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				Toast.makeText(LiveViewActivity.this, getString(R.string.tips_no_permission), Toast.LENGTH_SHORT).show();
				return;
			}


			clickListen((ImageView) v);

		} else if (i == R.id.btn_live_exit) {
			finish();

		} else if (i == R.id.btn_live_mirror_flip) {
			clickMirrorFlip((ImageView) v);

		} else if (i == R.id.btn_live_preset) {
			clickPreset((ImageView) v);


		} else if (i == R.id.resolution_ratio) {
			clickRatio((ImageView) v);


		} else if (i == R.id.btn_live_record) {
			if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
					) {
				Toast.makeText(LiveViewActivity.this, getString(R.string.tips_no_permission), Toast.LENGTH_SHORT).show();
				return;
			}

			clickRecording((ImageView) v);

		} else if (i == R.id.btn_live_zoom_focus) {
			clickZoomFocus((ImageView) v);

		} else if (i == R.id.btn_live_snapshot) {
			if (!checkPermission(Manifest.permission.CAMERA) || !checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				Toast.makeText(LiveViewActivity.this, getString(R.string.tips_no_permission), Toast.LENGTH_SHORT).show();
				return;
			}
			clickSnapshot();

			/*		case R.id.btn_live_setting:
			Bundle extras = new Bundle();
			extras.putString(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			Intent intent = new Intent();
			intent.putExtras(extras);
			//intent.setClass(LiveViewActivity.this, AliveSettingActivity.class);
			startActivity(intent);

			break;*/
			/*case R.id.radio_quality_hd:
		case R.id.radio_quality_sd:

			int videoQuality = mCamera.getVideoQuality();
			if(v.getId() == R.id.radio_quality_hd) {
				videoQuality = 0;
			}
			else if(v.getId() == R.id.radio_quality_sd){
				videoQuality = 1;
			}

			if(videoQuality == mCamera.getVideoQuality()) {

				return;
			}
			mCamera.setVideoQuality(videoQuality);
			//	mCamera.updataInDatabase(LiveViewActivity.this);
			mCamera.stopLiveShow();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mCamera.startLiveShow(mCamera.getVideoQuality(), mMonitor);
				}
			}, 500);

			mCamera.disconnect();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mCamera.connect();
				}
			}, 500);

			break;
		case R.id.btn_live_alert:

			//clickAlert((Button)v);
			//	setToolsButtonSelected(R.id.btn_live_alert);
			break;

		case R.id.btn_live_sun:

			//	clickInfrared((Button)v);
			//	setToolsButtonSelected(R.id.btn_live_sun);
			break;

		case R.id.btn_live_music:
			//musicChoice((Button)v);
			//	setToolsButtonSelected(R.id.btn_live_music);
			break;*/
		} else {
		}
	}

	private boolean checkPermission(String permission){
		int checkCallPhonePermission = ContextCompat.
				checkSelfPermission(LiveViewActivity.this,permission);
		if(checkCallPhonePermission == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		return false;
	}
	

	//private int isTouchMoved;  //not move=0,  move=1, two point=2
	//	private int state=0; //normal=0, larger=1.arrow=2;
	private float action_down_x;
	private float action_down_y;

	float lastX;
	float lastY;

	int xlenOld;
	int ylenOld;

	float move_x ;
	float move_y;

	public float left;
	public float width;
	public float height;
	public float bottom;

	double nLenStart = 0;
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if(v.getId()==R.id.monitor_live_view) {

			int nCnt = event.getPointerCount();

			//	int n = event.getAction();
			if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt)//<span style="color:#ff0000;">2表示两个手指</span>
			{

				HiLog.e(" MotionEvent.ACTION_POINTER_DOWN ");
				mMonitor.setTouchMove(2);
				//	mMonitor.setState(3);
				for(int i=0; i< nCnt; i++)
				{
					float x = event.getX(i);
					float y = event.getY(i);

					Point pt = new Point((int)x, (int)y);
				}

				xlenOld = Math.abs((int)event.getX(0) - (int)event.getX(1));
				ylenOld = Math.abs((int)event.getY(0) - (int)event.getY(1));

				//	HiLog.e("event.getX(0):"+(int)event.getX(0)+"(int)event.getX(1):"+(int)event.getX(1));


				nLenStart = Math.sqrt((double)xlenOld*xlenOld + (double)ylenOld * ylenOld);


			}else if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE  && 2 == nCnt)
			{


				HiLog.e(" MotionEvent.ACTION_MOVE ");
				mMonitor.setTouchMove(2);
				//	mMonitor.setState(3);
				for(int i=0; i< nCnt; i++)
				{
					float x = event.getX(i);
					float y = event.getY(i);

					Point pt = new Point((int)x, (int)y);

				}

				int xlen =Math.abs((int)event.getX(0) - (int)event.getX(1));
				int ylen =Math.abs((int)event.getY(0) - (int)event.getY(1));

				int moveX=Math.abs(xlen-xlenOld);
				int moveY=Math.abs(ylen-ylenOld);



				double nLenEnd = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
				if(moveX<20 && moveY<20){

					return false;
				}

				if(nLenEnd > nLenStart){
					resetMonitorSize(true,nLenEnd);
				}else{
					resetMonitorSize(false,nLenEnd);
				}


				xlenOld=xlen;
				ylenOld=ylen;
				nLenStart=nLenEnd;



				return true;
			}else if(nCnt==1){


				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					HiLog.e("ACTION_DOWN");
					action_down_x = event.getRawX();
					action_down_y = event.getRawY();

					lastX=action_down_x;
					lastY=action_down_y;

					//	HiLog.e("ACTION_DOWN");
					mMonitor.setTouchMove(0);
					break;
				case MotionEvent.ACTION_MOVE:

					if(mMonitor.getTouchMove()!=0)
						break;
					HiLog.e("ACTION_MOVE");

					move_x = event.getRawX();
					move_y = event.getRawY();

					if(Math.abs(move_x - action_down_x)>40 || Math.abs(move_y - action_down_y)>40) {
						mMonitor.setTouchMove(1);	
						//		HiLog.e("ACTION_MOVE");
					}

					/*float offsetX = move_x - lastX;  
					float offsetY = move_y - lastY;

					left += offsetX/2;
					bottom -= offsetY/2;

					if(left>0){
						left=0;
					}
					if(bottom>0){
						bottom=0;
					}

					if((left+width<mMonitor.screen_width/2)){
						left=(int) (mMonitor.screen_width/2-width);
					}

					if(bottom+height<mMonitor.screen_height/2){
						bottom=(int) (mMonitor.screen_height/2-height);
					}

					mMonitor.setMatrix(HiTools.dip2px(LiveViewActivity.this,left),  
							HiTools.dip2px(LiveViewActivity.this,bottom),  
							HiTools.dip2px(LiveViewActivity.this,width),  
							HiTools.dip2px(LiveViewActivity.this,height));  
					//移动过后，更新lastX与lastY  
					lastX = move_x;  
					lastY = move_y;  
					 */


					break;
				case MotionEvent.ACTION_UP: {
					//	HiLog.v("monitor_live_view MotionEvent.ACTION_UP");
					if(mMonitor.getTouchMove()!=0 ){
						break;
					}

					//				if(mToolsBarVisibility == View.VISIBLE) {
					//					setToolsBarsVisibility(View.GONE);
					//				}
					//				else if(mToolsBarVisibility == View.GONE) {
					//					setToolsBarsVisibility(View.VISIBLE);
					//				}


					setViewVisible(visible);


					break;
				}
				default:
					break;
				}
			}
		}
		else if(v.getId()== R.id.btn_microphone) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				//HiLog.v("MotionEvent.ACTION_DOWN");

				mCamera.stopListening();
				mCamera.startTalk();
				isTalking = true;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				break;
			}
			case MotionEvent.ACTION_UP: {
				//	HiLog.v("MotionEvent.ACTION_UP");
				mCamera.stopTalk();
				mCamera.startListening();
				isTalking = false;
				break;
			}
			default:
				break;
			}
		}
		return false;
	}




	@Override
	public void receiveIOCtrlData(HiCamera arg0, int arg1, byte[] arg2, int arg3) {

		if(arg0 != mCamera)
			return;
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

	private int select_preset = 0;

	//预设位
	private void clickPreset(ImageView iv) {
		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_preset,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);               
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    

		//w:210  h:40+5*3+35*2 = 125

		/*	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			int offsetx = -HiTools.dip2px(this, 80);
			int offsety = HiTools.dip2px(this, 40);
			mPopupWindow.showAsDropDown(iv,offsetx,offsety);
		}
		else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
		 */
		int offsetx = HiTools.dip2px(this, 20);
		int location[] = new int[2];
		iv.getLocationOnScreen(location);
		int offsety = HiTools.dip2px(this, 90);
		mPopupWindow.showAtLocation(iv, 0 , location[0] - offsetx, offsety-location[1]);
		//	}


		RadioGroup radio_group_preset = (RadioGroup)customView.findViewById(R.id.radio_group_preset);
		radio_group_preset.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				if(checkedId == R.id.radio_quality_0) {
					select_preset = 0;
				}
				if(checkedId == R.id.radio_quality_1) {
					select_preset = 1;
				}
				if(checkedId == R.id.radio_quality_2) {
					select_preset = 2;
				}
				if(checkedId == R.id.radio_quality_3) {
					select_preset = 3;
				}
				if(checkedId == R.id.radio_quality_4) {
					select_preset = 4;
				}
				if(checkedId == R.id.radio_quality_5) {
					select_preset = 5;
				}
				if(checkedId == R.id.radio_quality_6) {
					select_preset = 6;
				}
				if(checkedId == R.id.radio_quality_7) {
					select_preset = 7;
				}

				HiLog.v("onCheckedChanged:"+select_preset);
			}
		});


		Button btn_set = (Button)customView.findViewById(R.id.btn_preset_set);
		btn_set.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//				isSetPTZReset = true;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_PRESET, HiChipDefines.HI_P2P_S_PTZ_PRESET.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_PRESET_ACT_SET, select_preset));
				HiToast.showToast(LiveViewActivity.this, getString(R.string.tips_preset_set_btn));
			}
		});


		Button btn_call = (Button)customView.findViewById(R.id.btn_preset_call);
		btn_call.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_PRESET, HiChipDefines.HI_P2P_S_PTZ_PRESET.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_PRESET_ACT_CALL, select_preset));
			}
		});

	}

	HiChipDefines.HI_P2P_S_DISPLAY display_param = null;

	//镜像，翻转的转换
	private void clickMirrorFlip(ImageView iv) {

		if(display_param == null) {
			return;
		}
		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_mirror_flip,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);               
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    


		// height = 80

		int offsetx = HiTools.dip2px(this, 0);
		int location[] = new int[2];
		iv.getLocationOnScreen(location);
		int btnh = HiTools.dip2px(this, 50 + 80/2);

		mPopupWindow.showAtLocation(iv, 0 , location[0] - offsetx,  btnh-location[1]);


		ToggleButton toggle_flip = (ToggleButton)customView.findViewById(R.id.toggle_flip);
		toggle_flip.setChecked(display_param.u32Flip==1?true:false);

		toggle_flip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				display_param.u32Flip = arg1?1:0;
			
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_DISPLAY_PARAM, display_param.parseContent());
			}
		});


		ToggleButton toggle_mirror = (ToggleButton)customView.findViewById(R.id.toggle_mirror);
		toggle_mirror.setChecked(display_param.u32Mirror==1?true:false);

		toggle_mirror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				display_param.u32Mirror = arg1?1:0;
			
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_DISPLAY_PARAM, display_param.parseContent());

			}
		});

	}


	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {
		if(arg1 == HiCamera.CAMERA_CONNECTION_STATE_LOGIN && arg0 == mCamera) {
			mCamera.startLiveShow(mCamera.getVideoQuality(), mMonitor);
		}
	}

	/*	@Override
	public void callbackState(int arg0, int arg1, int arg2) {

		Bundle bundle = new Bundle();
		bundle.putInt("command", arg0);
		bundle.putInt("width", arg1);
		bundle.putInt("height", arg2);
		Message msg = handler.obtainMessage();
		msg.what = HANDLE_MESSAGE_PLAY_STATE;
		msg.setData(bundle);
		handler.sendMessage(msg);

	}
	 */


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initView();
		//resetMonitorSize();


	}


	private class ProgressThread extends HiThread {
		public void run() {
			while(isRunning) {
				sleep(100);

				Message msg = handler.obtainMessage();
				msg.what = HANDLE_MESSAGE_PROGRESSBAR_RUN;
				handler.sendMessage(msg);
			}
		}
	}

	private PopupWindow.OnDismissListener mOnDismissListener = new PopupWindow.OnDismissListener() {

		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			//	setToolsButtonSelected(0);
		}
	};

	//拍照，保存到本地文件夹 :/storage/sdcard1/CamHigh/Snapshot/Camera的UID/IMG_+时间+.jpg
	private void clickSnapshot() {
		if (mCamera != null) {

			if (HiTools.isSDCardValid()) {

				String appname = getString(R.string.app_name);

				File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
				File sargetFolder = new File(rootFolder.getAbsolutePath() + "/Snapshot/");
				File yargetFolder=new File(sargetFolder.getAbsolutePath()+"/"+mCamera.getUid()+"/");
				//	File targetFolder=new File(yargetFolder.getAbsolutePath()+"/"+getTimeForNow()+"/");
				if (!rootFolder.exists()) {
					rootFolder.mkdir();
				}
				if (!sargetFolder.exists()) {
					sargetFolder.mkdir();
				}
				if (!yargetFolder.exists()) {
					yargetFolder.mkdir();
				}
				//	if (!targetFolder.exists()) {
				//		targetFolder.mkdir();
				//	}

				String filename = HiTools.getFileNameWithTime(0);
				final String file = yargetFolder.getAbsoluteFile() + "/" + filename;

				HiLog.v("btn_live_snapshot:"+file);

				Bitmap frame = mCamera != null ? mCamera.getSnapshot() : null;
				if (frame != null && HiTools.saveImage(file, frame)){
					Toast.makeText(LiveViewActivity.this, getText(R.string.tips_snapshot_success), Toast.LENGTH_SHORT).show();
					HiLog.v("btn_live_snapshot:"+true);
				} else {
					Toast.makeText(LiveViewActivity.this, getText(R.string.tips_snapshot_failed), Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(LiveViewActivity.this, getText(R.string.tips_no_sdcard).toString(), Toast.LENGTH_SHORT).show();
			}
		}

	}


	/*
	private void clickPresetTIO(Button btn) {
		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_preset_tio,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		mPopupWindow.setOnDismissListener(mOnDismissListener);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);                  
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    

		//w:210  h:40+5*3+35*2 = 125
		//		mPopupWindow.setAnimationStyle(R.style.anim_from_right_btm);
		int offsetx = -HiTools.dip2px(this, 65);
		int offsety = HiTools.dip2px(this, 5);
		mPopupWindow.showAsDropDown(btn,offsetx,offsety);


		final int[] resid =  {R.id.btn_color_off,R.id.btn_color_1,R.id.btn_color_2,R.id.btn_color_3,R.id.btn_color_4,R.id.btn_color_5,R.id.btn_color_6,R.id.btn_color_7,R.id.btn_color_loop};

		for(int i=0;i<9;i++){
			Button btnTemp = (Button) customView.findViewById(resid[i]);
			btnTemp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int id = v.getId();
					for(int j=0;j<resid.length;j++) {
						if(id == resid[j]) {
							mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_TIO_SET, TattouHiChipDefine.HI_P2P_TIO_INFO.parseContent(j, 300));
							//mPopupWindow.dismiss();
							break;
						}
					}

				}
			});
		}

		Button btn_light_continously = (Button) customView.findViewById(R.id.btn_light_continously);
		Button btn_light_15min = (Button) customView.findViewById(R.id.btn_light_15min);
		btn_light_continously.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_TIO_SET, TattouHiChipDefine.HI_P2P_TIO_INFO.parseContent(9, 0));
//				mPopupWindow.dismiss();
			}
		});
		btn_light_15min.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_TIO_SET, TattouHiChipDefine.HI_P2P_TIO_INFO.parseContent(9, 900));
//				mPopupWindow.dismiss();
			}
		});
	}
	 */



	/*	private void clickPresetPIO(Button btn) {
		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_live_preset,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		mPopupWindow.setOnDismissListener(mOnDismissListener);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);            
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    


		int offsetx =150;
		int offsety = 10;
		mPopupWindow.showAsDropDown(btn,offsetx,offsety);



		Button still=(Button)customView.findViewById(R.id.btn_light_still);
		still.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_PIO_SET, TattouHiChipDefine.HI_P2P_PIO_INFO.parseContent(1, 300));
//				mPopupWindow.dismiss();
			}
		});
		Button move=(Button)customView.findViewById(R.id.btn_light_move);
		move.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_PIO_SET, TattouHiChipDefine.HI_P2P_PIO_INFO.parseContent(2, 300));
//				mPopupWindow.dismiss();
			}
		});
		Button off=(Button)customView.findViewById(R.id.btn_light_off);
		off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_PIO_SET, TattouHiChipDefine.HI_P2P_PIO_INFO.parseContent(0, 300));
//				mPopupWindow.dismiss();
			}
		});
		Button continously=(Button)customView.findViewById(R.id.btn_light_continously);
		continously.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_PIO_SET, TattouHiChipDefine.HI_P2P_PIO_INFO.parseContent(3, 0));
//				mPopupWindow.dismiss();
			}
		});
		Button min15=(Button)customView.findViewById(R.id.btn_light_15min);
		min15.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_PIO_SET, TattouHiChipDefine.HI_P2P_PIO_INFO.parseContent(3, 900));
//				mPopupWindow.dismiss();
			}
		});


	}*/

	private void clickRatio(ImageView iv) {

		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_resolution_ratio,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		mPopupWindow.setOnDismissListener(mOnDismissListener);
		ColorDrawable cd = new ColorDrawable(-000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);               
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    


		//width = 210 height = 90
		int offsetx = HiTools.dip2px(this, 10);
		int location[] = new int[2];
		iv.getLocationOnScreen(location);
		int offsety = HiTools.dip2px(this, 90 + 100/2);

		//		mPopupWindow.setAnimationStyle(R.style.anim_from_right_btm);
		mPopupWindow.showAtLocation(iv, 0 , location[0] - offsetx, location[1] - offsety);

		final TextView ratio_high=(TextView)customView.findViewById(R.id.ratio_high);
		final TextView ratio_fluent=(TextView)customView.findViewById(R.id.ratio_fluent);
		int videoQuality = mCamera.getVideoQuality();
		if(videoQuality==0){
			ratio_high.setSelected(true);
			ratio_fluent.setSelected(false);
		}else if(videoQuality==1){
			ratio_fluent.setSelected(true);
			ratio_high.setSelected(false);
		}
		ratio_high.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ratio_high.setSelected(true);
				ratio_fluent.setSelected(false);
				switchVideoQuality(0);
				mPopupWindow.dismiss();
			}
		});


		ratio_fluent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ratio_fluent.setSelected(true);
				ratio_high.setSelected(false);
				switchVideoQuality(1);
				mPopupWindow.dismiss();
			}
		});
	}

	private void switchVideoQuality(int quality){
		if(mCamera==null){
			return;
		}
		int videoQuality = mCamera.getVideoQuality();
		videoQuality=quality;

		if(videoQuality == mCamera.getVideoQuality()) {

			return;
		}
		mCamera.setVideoQuality(videoQuality);
		mCamera.updateInDatabase(LiveViewActivity.this);

		mCamera.disconnect();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mCamera.connect();
			}
		}, 500);

	}

	//拉近拉远聚焦等操作
	private void clickZoomFocus(ImageView iv) {
		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_zoom_focus,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);               
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    


		//width = 210 height = 90
		int offsetx = HiTools.dip2px(this, 10);
		int location[] = new int[2];
		iv.getLocationOnScreen(location);
		int offsety = HiTools.dip2px(this, 90);

		mPopupWindow.showAtLocation(iv, 0 , location[0] - offsetx, offsety-location[1] );

		//拉近操作
		Button btnZoomin = (Button)customView.findViewById(R.id.btn_zoomin);
		btnZoomin.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_ZOOMIN, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_STOP, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));
				}
				return false;
			}
		});
		//拉远按钮
		Button btnZoomout = (Button)customView.findViewById(R.id.btn_zoomout);
		btnZoomout.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					HiLog.i("TAGLiveActivity");
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_ZOOMOUT, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_STOP, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));
				}
				return false;
			}
		});
		//聚焦+
		Button btnFocusin = (Button)customView.findViewById(R.id.btn_focusin);
		btnFocusin.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_FOCUSIN, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_STOP, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));
				}
				return false;
			}
		});
		//聚焦-
		Button btnFocusout = (Button)customView.findViewById(R.id.btn_focusout);
		btnFocusout.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_FOCUSOUT, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_STOP, HiChipDefines.HI_P2P_PTZ_MODE_RUN,(short)50,(short)10));
				}
				return false;
			}
		});
	}

	/*private void musicChoice(Button btn){
		if(mp3_list == null || audio_attr == null)
			return;

		View customView = getLayoutInflater().inflate(R.layout.popview_chooce_music,
				null, false);
		mPopupWindow = new PopupWindow(customView);
		mPopupWindow.setOnDismissListener(mOnDismissListener);
		ColorDrawable cd = new ColorDrawable(-000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);    
		//		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screen_height = dm.heightPixels;


		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}
		else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

			int windHeight = screen_height - HiTools.dip2px(this, 100);
			mPopupWindow.setHeight(windHeight);

		}



		//width = 210 height = 90
		int offsetx = -HiTools.dip2px(this, 60);
		int offsety = HiTools.dip2px(this, 5);
		mPopupWindow.showAsDropDown(btn,offsetx,offsety);

		String[] str_music_list = new String[mp3_list.sMP3Name.length];
		for(int i=0;i<mp3_list.sMP3Name.length;i++){
			str_music_list[i] = Packet.getString(mp3_list.sMP3Name[i]);
		}

		final class UserAdapter extends BaseAdapter {

			private LayoutInflater mInflater;

			private String[] arraylist;
			public UserAdapter(Context context,String[] str_array) {
				this.mInflater = LayoutInflater.from(context);
				arraylist = str_array;
			}

			public int getCount() {
				return arraylist.length;
			}

			public Object getItem(int position) {
				return arraylist[position];
			}

			public long getItemId(int position) {
				return position;
			}


			@SuppressLint("InflateParams")
			public View getView(int position, View convertView, ViewGroup parent) {


				ViewHolder holder = null;

				if (convertView == null) {

					convertView = mInflater.inflate(R.layout.list_music, null);

					holder = new ViewHolder();
					holder.text = (TextView) convertView.findViewById(R.id.txt_music);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}


				holder.text.setText(arraylist[position]);

				return convertView;

			}// getView()

			final class ViewHolder {
				public TextView text;
			}
		}

		UserAdapter music_adapter = new UserAdapter(this, str_music_list);
		ListView list=(ListView)customView.findViewById(R.id.list_music);
		list.setAdapter(music_adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_MP3_PLAY, TattouHiChipDefine.HI_P2P_MP3_PLAY_INFO.parseContent(3, position+1));
				//mPopupWindow.dismiss();
			}
		});


		Button btn_stop_music=(Button)customView.findViewById(R.id.btn_stop_music);
		btn_stop_music.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(TattouHiChipDefine.HI_P2P_MP3_PLAY, TattouHiChipDefine.HI_P2P_MP3_PLAY_INFO.parseContent(0, 0));
			}
		});

		VerticalSeekBar seekbar_music_vol = (VerticalSeekBar)customView.findViewById(R.id.seekbar_music_vol);
		//		audio_attr
		seekbar_music_vol.setMax(50);
		seekbar_music_vol.setProgress(audio_attr.u32OutVol - 50);
		seekbar_music_vol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if(audio_attr.u32OutVol <= 0) {
					audio_attr.u32OutVol = 1;
				}
				else {
					audio_attr.u32OutVol = seekBar.getProgress()+50;
				}

				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_AUDIO_ATTR, HiChipDefines.HI_P2P_S_AUDIO_ATTR.parseContent(0, audio_attr.u32Enable, audio_attr.u32Stream, audio_attr.u32AudioType, audio_attr.u32InMode, audio_attr.u32InVol, audio_attr.u32OutVol));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				HiLog.v("DragEvent.onProgressChanged:"+seekBar.getProgress());
			}
		});


	}
	 */
	/*
	private void clickInfrared(Button btn) {
		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_click_sun,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		mPopupWindow.setOnDismissListener(mOnDismissListener);
		ColorDrawable cd = new ColorDrawable(-000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);               
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    


		//width = 210 height = 90
		int offsetx = HiTools.dip2px(this, 20);
		int location[] = new int[2];
		btn.getLocationOnScreen(location);
		int offsety = HiTools.dip2px(this, 100 + 90/2);

		//		mPopupWindow.setAnimationStyle(R.style.anim_from_right_btm);
		mPopupWindow.showAtLocation(btn, 0 , location[0] - offsetx, location[1] - offsety);

		Button auto=(Button)customView.findViewById(R.id.btn_infrared_auto);
		auto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_INFRARED, HiChipDefines.HI_P2P_S_INFRARED.parseContent(0, HiChipDefines.HI_P2P_INFRARED_AUTO));

			}
		});
		Button on=(Button)customView.findViewById(R.id.btn_infrared_on);
		on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_INFRARED, HiChipDefines.HI_P2P_S_INFRARED.parseContent(0, HiChipDefines.HI_P2P_INFRARED_ON));

			}
		});
		Button off=(Button)customView.findViewById(R.id.btn_infrared_off);
		off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_INFRARED, HiChipDefines.HI_P2P_S_INFRARED.parseContent(0, HiChipDefines.HI_P2P_INFRARED_OFF));

			}
		});

	}



	HiChipDefines.HI_P2P_S_DISPLAY display_param = null;
	private void clickMirrorFlip(Button btn) {

		if(display_param == null) {
			return;
		}
		@SuppressLint("InflateParams")
		View customView = getLayoutInflater().inflate(R.layout.popview_mirror_flip,
				null, false);

		mPopupWindow = new PopupWindow(customView);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);               
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);    


		// height = 80

		int offsetx = HiTools.dip2px(this, 90);
		int location[] = new int[2];
		btn.getLocationOnScreen(location);
		int btnh = HiTools.dip2px(this, 100 + 80/2);

		mPopupWindow.showAtLocation(btn, 0 , location[0] - offsetx, location[1] - btnh);


		ToggleButton toggle_flip = (ToggleButton)customView.findViewById(R.id.toggle_flip);
		toggle_flip.setChecked(display_param.u32Flip==1?true:false);

		toggle_flip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				display_param.u32Flip = arg1?1:0;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_DISPLAY_PARAM, display_param.parseContent());
			}
		});


		ToggleButton toggle_mirror = (ToggleButton)customView.findViewById(R.id.toggle_mirror);
		toggle_mirror.setChecked(display_param.u32Mirror==1?true:false);

		toggle_mirror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				display_param.u32Mirror = arg1?1:0;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_DISPLAY_PARAM, display_param.parseContent());
			}
		});

	}











	 */

	//点击录像按钮，保存录像文件
	private void clickRecording(ImageView  v) {
		if(mRecordingState == RECORDING_STATUS_NONE) {
			mRecordingState = RECORDING_STATUS_LOADING;
			String appname = getString(R.string.app_name);
			File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
			File targetFolder = new File(rootFolder.getAbsolutePath() + "/VideoRecoding/");
			File cameraFolder = new File(targetFolder.getAbsolutePath() + "/" + mCamera.getUid());
			if (!rootFolder.exists()) {
				rootFolder.mkdir();
			}
			if (!targetFolder.exists()) {
				targetFolder.mkdir();
			}
			if (!cameraFolder.exists()) {
				cameraFolder.mkdir();
			}


			final String file = cameraFolder.getAbsoluteFile() + "/" + HiTools.getFileNameWithTime(1);
			mCamera.startRecording(file);
			v.setImageResource(R.drawable.ptz_takevideo_pressed);

		}
		else if(mRecordingState == RECORDING_STATUS_ING) {
			mRecordingState = RECORDING_STATUS_LOADING;
			mCamera.stopRecording();
			v.setImageResource(R.drawable.ptz_takevideo_nor);
		}
	}

	/*private void setToolsBarsVisibility(int visibility) {
		mToolsBarVisibility = visibility;
		btn_microphone.setVisibility(visibility);

		//		btn_ptz_left.setVisibility(visibility);
		//		btn_ptz_right.setVisibility(visibility);
		//		btn_ptz_up.setVisibility(visibility);
		//		btn_ptz_down.setVisibility(visibility);

		lay_live_tools_bottom.setVisibility(visibility);
		radio_quality.setVisibility(visibility);
	}*/


	@Override
	public void finish() {
		// TODO Auto-generated method stub

		saveSnapshot();

		super.finish();
	}

	//点击声音按钮开始监听语音，按住喇叭说话，松开接收
	private void clickListen(ImageView iv) {
		btn_microphone = (Button)findViewById(R.id.btn_microphone);
		if(isListening)
		{
			iv.setImageResource(R.drawable.speaker_off);
			mCamera.stopListening();
			btn_microphone.setVisibility(View.GONE);
		}
		else {
			iv.setImageResource(R.drawable.speaker_on);

			btn_microphone.setVisibility(View.VISIBLE);

			mCamera.startListening();
		}
		isListening = !isListening;

	}





	/*private void setToolsButtonSelected(int resid) {
		if(resid == 0) {
			btn_live_alert.setBackgroundResource(R.drawable.btn_live_alert);
			btn_live_sun.setBackgroundResource(R.drawable.btn_live_sun);
			btn_live_preset.setBackgroundResource(R.drawable.btn_live_preset);
			btn_live_music.setBackgroundResource(R.drawable.btn_live_music);
		}
		else if(resid == R.id.btn_live_alert) {
			btn_live_alert.setBackgroundResource(R.drawable.btn_live_alert_ing);
		}
		else if(resid == R.id.btn_live_sun) {
			btn_live_sun.setBackgroundResource(R.drawable.btn_live_sun_ing);
		}
		else if(resid == R.id.btn_live_preset) {
			btn_live_preset.setBackgroundResource(R.drawable.btn_live_preset_ing);
		}
		else if(resid == R.id.btn_live_music) {
			btn_live_music.setBackgroundResource(R.drawable.btn_live_music_ing);
		}
	}*/

	public String getTimeForNow(){
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(System.currentTimeMillis());
	}

	@Override
	public void callbackPlayUTC(HiCamera arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callbackState(HiCamera camera, int arg1, int arg2, int arg3) {
		if(mCamera!=camera)return;
		Bundle bundle = new Bundle();
		bundle.putInt("command", arg1);
		bundle.putInt("width", arg2);
		bundle.putInt("height", arg3);
		Message msg = handler.obtainMessage();
		msg.what = HANDLE_MESSAGE_PLAY_STATE;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

}
