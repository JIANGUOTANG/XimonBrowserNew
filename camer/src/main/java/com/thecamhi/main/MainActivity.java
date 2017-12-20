package com.thecamhi.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.hichip.base.HiLog;
import com.hichip.sdk.HiChipSDK;
import com.hichip.sdk.HiChipSDK.HiChipInitCallback;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.thecamhi.base.DatabaseManager;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.ximon.light.R;

import java.util.Timer;


public class MainActivity extends FragmentActivity {

	private final static int HANDLE_MESSAGE_INIT_END = 0x90000001;
	private Class<?> fraList[]={CameraFragment.class,PictureFragment.class,VideoFragment.class,AboutFragment.class};
	private int drawable[]={R.drawable.tab_camera_icon,R.drawable.tab_pic_icon,R.drawable.tab_voideo_icon,R.drawable.tab_about_icon};

	private long initSdkTime;
	private int count=0;
	Timer timer;
	private long exitTime = 0;
	private int REQUEST_CODE_ASK_PERMISSON=0*991;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initview();
		initTabHost();
		initSDK();
		initXGPushSDK();
	}



	//初始化SDK
	private void initSDK() {
		initSdkTime=System.currentTimeMillis();
		HiChipSDK.init(new HiChipInitCallback() {

			@Override
			public void onSuccess() {
				initCamera();
			}
			@Override
			public void onFali(int arg0, int arg1) {
				initCamera();
			}
		});

	}

	//搭载4个fragment
	private void initTabHost() {
		String[] tabString=getResources().getStringArray(R.array.tab_name);
		FragmentTabHost tabHost=findViewById(R.id.main_fragment_tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.fragment_main_content);
		LayoutInflater inflater=LayoutInflater.from(this);
		for(int i=0;i<fraList.length;i++){
			View view=inflater.inflate(R.layout.fragment_tabhost_switch_image, null);
			ImageView iv=(ImageView)view.findViewById(R.id.main_tabhost_imv);
			TextView tv=(TextView)view.findViewById(R.id.main_tabhost_tv);
			iv.setImageResource(drawable[i]);
			tv.setText(tabString[i]);
			TabSpec tabItem=tabHost.newTabSpec(tabString[i]).setIndicator(view);
			tabHost.addTab(tabItem,fraList[i],null);
		}
	}


	private void initXGPushSDK() {
		// 开启logcat输出，方便debug，发布时请关闭
		XGPushConfig.enableDebug(this, false);
		XGPushManager.registerPush(this, new XGIOperateCallback() {
			@Override
			public void onSuccess(Object data, int flag) {
				HiLog.v("注册成功，设备token为：" + data);
				String token = (String) data;
				HiDataValue.XGToken = token;
			}

			@Override
			public void onFail(Object data, int errCode, String msg) {
				HiLog.v("注册失败，为：" + msg);
			}
		});
	}

	//获取本地数据
	private void initCamera() {
		DatabaseManager manager=new DatabaseManager(this);
		SQLiteDatabase db=manager.getReadableDatabase();
		Cursor cursor=db.query(DatabaseManager.TABLE_DEVICE, 
				new String[] { "_id", "dev_nickname", "dev_uid", "view_acc", "view_pwd",
				"dev_videoQuality", "dev_alarmState" ,"dev_pushState","snapshot" },
				null, null, null, null, null);

		while(cursor.moveToNext()){
			long id=cursor.getLong(cursor.getColumnIndex("_id"));
			String dev_nickname=cursor.getString(cursor.getColumnIndex("dev_nickname"));
			String dev_uid=cursor.getString(cursor.getColumnIndex("dev_uid"));
			String dev_name=cursor.getString(cursor.getColumnIndex("view_acc"));
			String  dev_pwd=cursor.getString(cursor.getColumnIndex("view_pwd"));
			int dev_videoQuality=cursor.getInt(cursor.getColumnIndex("dev_videoQuality"));
			int dev_alarmState=cursor.getInt(cursor.getColumnIndex("dev_alarmState"));
			int  dev_pushState=cursor.getInt(cursor.getColumnIndex("dev_pushState"));
			byte[] byteSnapshot=cursor.getBlob(cursor.getColumnIndex("snapshot"));

			Bitmap snapshot=(byteSnapshot != null && byteSnapshot.length > 0) ?
					DatabaseManager.getBitmapFromByteArray(byteSnapshot) : null;
					HiLog.v(  "  dev_uid:"+dev_uid
							+ "  view_acc:" + dev_name
							+ "  view_pwd:" + dev_pwd
							+ "  dev_nickname:" + dev_nickname
							+ "  videoQuality:" +dev_videoQuality
							+ "  alarmState:" +dev_alarmState
							+ " snapshot==null:"+snapshot==null?"true":""
							);

					MyCamera camera=new MyCamera(dev_nickname,
							dev_uid, dev_name, dev_pwd);
					camera.setDbID(id);
					camera.setVideoQuality(dev_videoQuality);
					camera.setAlarmState(dev_alarmState);
					camera.setPushState(dev_pushState);
					camera.snapshot = snapshot;
					camera.saveInCameraList();
					camera.connect();

					
					
					if(camera.getPushState() == 0) {
						String pDID = camera.getUid();
						SharedPreferences setting = MainActivity.this.getSharedPreferences("Subid_"+pDID,MainActivity.MODE_PRIVATE);
						int subID = setting.getInt("pushon", -1);
						if(subID == 1) {
							camera.setPushState(1);
						}
						else {
							camera.setPushState(0);
						}
					}


		}
		cursor.close();
		db.close();

		requestEnd();

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();


	}

	public void requestEnd(){
		//获取数据完毕，发送广播到CameraFragment界面去刷新adapter
		Intent intent = new Intent();
		intent.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
		sendBroadcast(intent);
	}

	private void initview() {
		ActivityCompat.
		requestPermissions(MainActivity.this,
				new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.RECORD_AUDIO,
				Manifest.permission.CAMERA,
		},  
		REQUEST_CODE_ASK_PERMISSON);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		for(MyCamera camera: HiDataValue.CameraList) {
			//			camera.registerIOSessionListener(MainActivity.this);
			if(camera.isSetValueWithoutSave()) {
				camera.updateInDatabase(this);
			}
			camera.disconnect();
		}

		HiChipSDK.uninit();

		//		Log.v("HiChip", "-------------HIP2PDeInit:"+ ret);
		/*	NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(HiDataValue.NOTICE_RUNNING_ID);
		notificationManager.cancel(HiDataValue.NOTICE_ALARM_ID);
		 */
	}



	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				finish();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			}
		}
	};
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//
////		switch (keyCode) {
////		case KeyEvent.KEYCODE_BACK:
////			AlertDialog.Builder builder = new AlertDialog.Builder(
////					MainActivity.this);
////			builder
////			.setTitle(getString(R.string.tips_warning))
////			.setIcon(android.R.drawable.ic_dialog_alert)
////			.setMessage(getString(R.string.sure_to_exit))
////			.setPositiveButton(
////					getResources().getString(R.string.btn_yes),
////					dialogClickListener)
////					.setNegativeButton(
////							getResources().getString(R.string.btn_no),
////							dialogClickListener).show();
////
////			break;
////		}
//		return true;
//	}
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:

				if((System.currentTimeMillis()-exitTime) > 2000){  
				HiToast.showToast(getApplicationContext(), getString(R.string.press_again_to_exit));                               
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
			moveTaskToBack(true);

			break;
		case KeyEvent.KEYCODE_MENU:
			AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.exit_the_project)

			.setMessage(getResources().getString(R.string.sure_to_exit))
			.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {   		    
				@Override   
				public void onClick(DialogInterface dialog, int which) {   
					//MainActivity.this.finish();
					moveTaskToBack(true);
				}   
			})
			.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {   		    
				@Override   
				public void onClick(DialogInterface dialog, int which) {   
					dialog.dismiss();
				}   
			})
			.create();        
			dialog.show();  

			break;
		}
		return true;
	}*/



}
