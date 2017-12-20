package com.thecamhi.main;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hichip.base.HiLog;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.data.HiDeviceInfo;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushManager;
import com.thecamhi.activity.AddCameraActivity;
import com.thecamhi.activity.AliveSettingActivity;
import com.thecamhi.activity.EditCameraActivity;
import com.thecamhi.activity.LiveViewActivity;
import com.thecamhi.base.DatabaseManager;
import com.thecamhi.base.HiToast;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.widget.swipe.SwipeMenuListView;
import com.ximon.light.R;

import java.util.Calendar;
import java.util.TimeZone;

public class CameraFragment extends ListFragment implements ICameraIOSessionCallback{
	private View layoutView;
	private static final int REQUEST_CODE_CAMERA_ADD = 0;
	private static final int REQUEST_CODE_CAMERA_EDIT = 1;
	private static final int REQUEST_CODE_CAMERA_LIVE_VIEW = 2;


	private static final int MOTION_ALARM=0;      //移动侦测
	private static final int IO_ALARM	=1; 	  //外置报警
	private static final int AUDIO_ALARM=2;       //声音报警
	private static final int UART_ALARM=3  ;      //外置报警

	private CameraListAdapter adapter;
	private CameraBroadcastReceiver receiver;
	private SwipeMenuListView listView;

	private long lastAlarmTime = 0;
	private String str_state[];
	private boolean delModel=false;
	private Button btn_edit_camera_fragment;
	int ranNum;

	public interface OnButtonClickListener{
		void onButtonClick(int btnId,MyCamera camera);
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (receiver == null) {
			receiver = new CameraBroadcastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(HiDataValue.ACTION_CAMERA_INIT_END);
			getActivity().registerReceiver(receiver, filter);
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {

		layoutView=inflater.inflate(R.layout.fragment_camera,null);
		initView();
		ranNum=(int) (Math.random()*10000);
		return layoutView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		listView = (SwipeMenuListView)getListView();

		/*		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(180);
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		listView.setMenuCreator(creator);

		// step 2. listener item click event
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {

				MyCamera camera = HiDataValue.CameraList.get(position);
				switch (index) {
				case 0:
					camera.disconnect();
					camera.deleteInCameraList();
					camera.deleteInDatabase(getActivity());
					adapter.notifyDataSetChanged();
					break;
				}
			}
		});*/
	}

	private void initView() {

		/*TitleView title_top=(TitleView)
				layoutView.findViewById(R.id.title_top);
		title_top.setTitle(getString(R.string.title_camera_fragment));*/

		TextView title_middle=(TextView)layoutView.findViewById(R.id.title_middle_camera_fragment);
		title_middle.setText(getString(R.string.title_camera_fragment));


		LinearLayout add_camera_ll=(LinearLayout)layoutView.findViewById(R.id.add_camera_ll);
		add_camera_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),AddCameraActivity.class);
				startActivityForResult(intent,REQUEST_CODE_CAMERA_ADD);
			}
		});

		str_state = getActivity().getResources().getStringArray(R.array.connect_state);

		adapter = new CameraListAdapter(getActivity());
		this.setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		//设置右边设置按钮和编辑按钮的监听
		adapter.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onButtonClick(int btnId, MyCamera camera) {
				if (btnId == R.id.setting_camera_item) {
					if (delModel) {
						Intent intent = new Intent();
						intent.putExtra(HiDataValue.EXTRAS_KEY_UID, camera.getUid());
						intent.setClass(getActivity(), EditCameraActivity.class);
						startActivityForResult(intent, REQUEST_CODE_CAMERA_EDIT);
					} else {
						if (camera.getConnectState() == HiCamera.CAMERA_CONNECTION_STATE_LOGIN) {
							HiLog.v("BUTTON_SETTING.setOnClickListener");
							Intent intent = new Intent();
							intent.putExtra(HiDataValue.EXTRAS_KEY_UID, camera.getUid());
							intent.setClass(getActivity(), AliveSettingActivity.class);
							startActivity(intent);
						} else {
							HiToast.showToast(getActivity(), getString(R.string.click_offline_setting));
						}
					}

				} else if (btnId == R.id.delete_icon_camera_item) {
					showDeleteDialog(camera);


				} else {
				}

			}
		});

		btn_edit_camera_fragment=(Button)layoutView.findViewById(R.id.btn_edit_camera_fragment);
		btn_edit_camera_fragment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if(delModel){
					btn_edit_camera_fragment.setText(getString(R.string.btn_edit_camera_fragment));	
				}else{
					btn_edit_camera_fragment.setText(getString(R.string.finish));	
				}

				delModel=!delModel;
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	protected void showDeleteDialog(final MyCamera camera) {
		Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog
		.setTitle(R.string.tips_warning)
		.setMessage(R.string.tips_msg_delete_camera)
		.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {  

			@Override  
			public void onClick(DialogInterface dialog, int which) {  

			}  
		}).setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				camera.bindPushState(false, null);
				camera.disconnect();
				camera.deleteInCameraList();
				camera.deleteInDatabase(getActivity());
				adapter.notifyDataSetChanged();
				
				showSuccessDialog();
			}
		});
		alertDialog.show(); 


	}
	protected void showSuccessDialog() {

		Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog
		.setTitle(R.string.tips_warning)
		.setMessage(R.string.tips_remove_success)
		.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alertDialog.show(); 
	}
	//Item的点击监听
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		HiLog.v("onListItemClick:"+position);
		MyCamera selectedCamera =  HiDataValue.CameraList.get(position);

		if(delModel){
			Intent intent = new Intent();
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, selectedCamera.getUid());
			intent.setClass(getActivity(), EditCameraActivity.class);
			startActivityForResult(intent,REQUEST_CODE_CAMERA_EDIT);
		}else{


			if(selectedCamera.getConnectState()==HiCamera.CAMERA_CONNECTION_STATE_LOGIN){
				Bundle extras = new Bundle();
				extras.putString(HiDataValue.EXTRAS_KEY_UID, selectedCamera.getUid());

				Intent intent = new Intent();
				intent.putExtras(extras);
				intent.setClass(getActivity(), LiveViewActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CAMERA_LIVE_VIEW);

				HiDataValue.isOnLiveView = true;
				selectedCamera.setAlarmState(0);

				adapter.notifyDataSetChanged();
			}else{
				HiLog.v("jian"+"来来来");
				selectedCamera.connect();
			}
		}
	}


	//刷新界面的广播
	private class CameraBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			if(intent.getAction().equals(HiDataValue.ACTION_CAMERA_INIT_END)) {
				if (adapter!=null) {
					adapter.notifyDataSetChanged();
				}

				for(MyCamera camera: HiDataValue.CameraList) {

					camera.registerIOSessionListener(CameraFragment.this);
				}
			}
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		delToNor();

	}

	public void delToNor(){
		delModel=false;
		btn_edit_camera_fragment.setText(getString(R.string.btn_edit_camera_fragment));	
		if(adapter!=null){
			adapter.notifyDataSetChanged();
		}
	}


	//camera的adapter
	public class CameraListAdapter extends BaseAdapter{
		Context context;
		private LayoutInflater mInflater;
		OnButtonClickListener mListener;
		private String strState;

		public void setOnButtonClickListener(OnButtonClickListener listener) {
			mListener = listener;
		}

		public CameraListAdapter(Context context){

			mInflater=LayoutInflater.from(context);
			this.context=context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return HiDataValue.CameraList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return HiDataValue.CameraList.get(position);
		}

		@Override
		public long getItemId(int arg0) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final MyCamera camera=HiDataValue.CameraList.get(position);
			if(camera==null){
				return null;
			}
			ViewHolder holder = null;
			if(convertView==null){
				convertView=mInflater.inflate(R.layout.camera_main_item, null);
				holder=new ViewHolder();
				holder.setting=(ImageView)convertView.findViewById(R.id.setting_camera_item);
				holder.img_snapshot=(ImageView)convertView.findViewById(R.id.snapshot_camera_item);
				holder.txt_nikename=(TextView)convertView.findViewById(R.id.nickname_camera_item);
				holder.txt_uid=(TextView)convertView.findViewById(R.id.uid_camera_item);
				holder.txt_state=(TextView)convertView.findViewById(R.id.state_camera_item);
				holder.img_alarm = (ImageView) convertView.findViewById(R.id.img_alarm);

				holder.delete_icon=(ImageView)convertView.findViewById(R.id.delete_icon_camera_item);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			if(holder!=null){

				holder.img_snapshot.setImageBitmap(camera.snapshot);
				/*	HiLog.e( "uid="+camera.getUid()+" snapshot==null:"+camera.snapshot==null?"true":"");
				HiLog.e("username: "+camera.getUsername());*/
				holder.txt_nikename.setText(camera.getNikeName());
				holder.txt_uid.setText(camera.getUid());
				int state=camera.getConnectState();
				if(state>=0 && state<=4) {
					strState="("+str_state[state]+")";
					holder.txt_state.setText(strState);
				}

				holder.setting.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(mListener!=null){
							mListener.onButtonClick(R.id.setting_camera_item, camera);
						}

					}
				});

				holder.delete_icon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if(mListener!=null){
							mListener.onButtonClick(R.id.delete_icon_camera_item, camera);
						}

					}
				});



				if(delModel){
					holder.delete_icon.setVisibility(View.VISIBLE);
				}else{
					holder.delete_icon.setVisibility(View.GONE);
				}

				if(camera.getAlarmState() == 0) {
					holder.img_alarm.setVisibility(View.GONE);
				}
				else {
					holder.img_alarm.setVisibility(View.VISIBLE);
				}
			}

			return convertView;
		}

		public class ViewHolder{
			public ImageView img_snapshot;
			public TextView txt_nikename;
			public TextView txt_uid;
			public TextView txt_state;
			public ImageView img_alarm;

			public ImageView setting;
			public ImageView delete_icon;

		}

	}



	//所有发送命令的回调接口receiveIOCtrlData
	@Override
	public void receiveIOCtrlData(HiCamera arg0, int arg1, byte[] arg2, int arg3) {
		if(arg1 == HiChipDefines.HI_P2P_GET_SNAP && arg3 == 0) {
			MyCamera camera = (MyCamera)arg0;
			if(!camera.reciveBmpBuffer(arg2)) {
				return;
			}
		}

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
	public void receiveSessionState(HiCamera arg0, int arg1) {
		Message msg = handler.obtainMessage();
		msg.what = HiDataValue.HANDLE_MESSAGE_SESSION_STATE;
		msg.arg1 = arg1;
		msg.obj = arg0;
		handler.sendMessage(msg);

	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			//摄像机登录的回调
			case HiDataValue.HANDLE_MESSAGE_SESSION_STATE:
				if(adapter!=null)
					adapter.notifyDataSetChanged();
				switch(msg.arg1) {
				case HiCamera.CAMERA_CONNECTION_STATE_DISCONNECTED:
					break;
				case HiCamera.CAMERA_CONNECTION_STATE_LOGIN:
					MyCamera camera=(MyCamera)msg.obj;
					setTime(camera);
					cameraLogin((MyCamera)msg.obj);
					HiLog.e("uid:"+camera.getUid());
					camera.sendIOCtrl(HiChipDefines.HI_P2P_GET_TIME_ZONE, new byte[0]);


					break;
				case HiCamera.CAMERA_CONNECTION_STATE_WRONG_PASSWORD:
					break;
				case HiCamera.CAMERA_CONNECTION_STATE_CONNECTING:
					break;
				}
				break;

			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL:
			{
				if(msg.arg2==0) {

					MyCamera camera = (MyCamera)msg.obj;

					switch (msg.arg1) {
					case HiChipDefines.HI_P2P_GET_SNAP:
						//						Bundle bundle = msg.getData();
						//						byte[] data = bundle.getByteArray("data");
						adapter.notifyDataSetChanged();


						DatabaseManager manager = new DatabaseManager(getActivity());

						//						byte[] buff = camera.getBmpBuffer();
						if (camera.snapshot != null) {
							manager.updateDeviceSnapshotByUID(camera.getUid(), camera.snapshot);
							//							Bitmap snapshot = null;
							//							snapshot = manager.getDeviceSnapshotByUid(uid);
							//							camera.Snapshot = snapshot;
						}else{
							HiLog.e("camera.snapshot =null");
						}


						break;

					case HiChipDefines.HI_P2P_GET_TIME_ZONE:
					{	
						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);



						HiChipDefines.HI_P2P_S_TIME_ZONE timezone = new HiChipDefines.HI_P2P_S_TIME_ZONE(data);

						if(timezone.u32DstMode == 1 ) {
							camera.setSummerTimer(true);
						}else{
							camera.setSummerTimer(false);
						}

					}	

					break;


					//服务器直推的回调
					case HiChipDefines.HI_P2P_ALARM_EVENT:
					{

						if(camera.getPushState()==0){
							return;
						}

						/*	//相对摄像机时间的每30秒一次回调，
						if(System.currentTimeMillis() - camera.getLastAlarmTime() < 30000) {

							HiLog.e("Time lastAlarmTime:"+(System.currentTimeMillis() - lastAlarmTime));

							return;
						}
						 */
						camera.setLastAlarmTime(System.currentTimeMillis());

						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
						HiChipDefines.HI_P2P_EVENT event = new HiChipDefines.HI_P2P_EVENT(data);

						//						showP2PPushMessage(camera.getUid(),event.u32Event);
						showAlarmNotification(camera,event.u32Event,event.u32Time);
						//						HiLog.v("alarm time:"+event.u32Time);
						saveAlarmData(camera,event.u32Event,event.u32Time);
						camera.setAlarmState(1);
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
		}
			break;
			}
		}
	};

	//报警推送到通知栏
	@SuppressWarnings("deprecation")
	private void showAlarmNotification(MyCamera camera, int evtType, int evtTime) {

		try {

			NotificationManager manager = (NotificationManager)  getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

			Bundle extras = new Bundle();
			extras.putString(HiDataValue.EXTRAS_KEY_UID, camera.getUid());
			extras.putInt("type",1);


			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.putExtras(extras);


			PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent,0);

			//			Calendar cal = Calendar.getInstance();
			//			cal.setTimeZone(TimeZone.getDefault());
			//			cal.setTimeInMillis(evtTime);
			//			cal.add(Calendar.MONTH, 0);

			Notification notification = new Notification(R.drawable.ic_launcher, camera.getNikeName(),(long)evtTime*1000 );
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults = Notification.DEFAULT_ALL;

			String[] alarmList=getResources().getStringArray(R.array.tips_alarm_list_array);

			//			notification.setLatestEventInfo(this, camera.getNikeName(), "baby!", pendingIntent);
//			notification.setLatestEventInfo(getActivity(), camera.getUid(),alarmList[evtType], pendingIntent);

			ranNum++;
			manager.notify(ranNum, notification);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void saveAlarmData(MyCamera camera, int evtType, int evtTime) {

		DatabaseManager manager = new DatabaseManager(getActivity());
		manager.addAlarmEvent(camera.getUid(),evtTime,evtType);


	}


	private void setTime(MyCamera camera){
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());

		byte[] time = HiChipDefines.HI_P2P_S_TIME_PARAM.parseContent(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));


		camera.sendIOCtrl(HiChipDefines.HI_P2P_SET_TIME_PARAM, time);
	}

	private void cameraLogin(MyCamera camera) { 
		HiLog.v("mainactivity cameraLogin:"+camera.getUid());
		if(camera.isFirstLogin()) {
			camera.setFirstLogin(false);

			/*if(camera.getDeciveInfo()!=null){
				String info=Packet.getString(camera.getDeciveInfo().aszSystemName);
			}*/
			//海思 的快照在登录后获取，（国科的快照在进入监控界面退出时获取）
			if(camera.getChipVersion() == HiDeviceInfo.CHIP_VERSION_HISI)
				camera.sendIOCtrl(HiChipDefines.HI_P2P_GET_SNAP, HiChipDefines.HI_P2P_S_SNAP_REQ.parseContent(0,HiChipDefines.HI_P2P_STREAM_2));
		}

	}



	private void showP2PPushMessage(String uid,int type) {

		if(HiDataValue.isOnLiveView)
			return;

		if(HiDataValue.CameraList.size()>0) {
			for(MyCamera camera: HiDataValue.CameraList) {
				if(camera.getUid().equals(uid)) {

					if(camera.getPushState() <= 0)
						return;

					camera.setAlarmState(1);
					adapter.notifyDataSetChanged();
					break;
				}
			}
		}

		String strAlarmType[] = getResources().getStringArray(R.array.tips_alarm_list_array);

		XGLocalMessage local_msg = new XGLocalMessage();
		// 设置本地消息类型，1:通知，2:消息
		local_msg.setType(1);
		// 设置消息标题
		local_msg.setTitle(uid);
		// 设置消息内容

		if(type < strAlarmType.length && type >=0)
			local_msg.setContent(strAlarmType[type]);
		// 设置消息日期，格式为：20140502
		//		local_msg.setDate("20140930");
		// 设置消息触发的小时(24小时制)，例如：22代表晚上10点
		//		local_msg.setHour("14");
		// 获取消息触发的分钟，例如：05代表05分
		//		local_msg.setMin("16");
		// 设置消息样式，默认为0或不设置
		// local_msg.setBuilderId(6);
		// 设置拉起应用页面
		// local_msg.setActivity("com.qq.xgdemo.SettingActivity");
		// 设置动作类型：1打开activity或app本身，2打开浏览器，3打开Intent ，4通过包名打开应用
		// local_msg.setAction_type(1);
		// 设置URL
		// local_msg.setUrl("http://www.baidu.com");
		// 设置Intent
		// local_msg.setIntent("intent:10086#Intent;scheme=tel;action=android.intent.action.DIAL;S.key=value;end");
		// 自定义本地通知样式
		// local_msg.setIcon_type(0);
		// local_msg.setIcon_res("right");
		// 是否覆盖原先build_id的保存设置。1覆盖，0不覆盖
		// local_msg.setStyle_id(1);
		// 设置音频资源
		// local_msg.setRing_raw("mm");
		// 设置key,value
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("key", "v1");
		// map.put("key2", "v2");
		// local_msg.setCustomContent(map);
		// 设置下载应用URL
		// local_msg.setPackageDownloadUrl("http://softfile.3g.qq.com:8080/msoft/179/1105/10753/MobileQQ1.0(Android)_Build0198.apk");
		// 设置要打开的应用包名
		// local_msg.setPackageName("com.example.com.qq.feedback");
		XGPushManager.addLocalNotification(getActivity(), local_msg);
	}




}
