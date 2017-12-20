package com.thecamhi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.sdk.HiChipP2P;
import com.hichip.tools.Packet;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.ximon.light.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WifiSettingActivity extends HiActivity implements ICameraIOSessionCallback,OnItemClickListener,OnClickListener{

	private static final int SET_WIFI_END=0X999;
	private MyCamera mCamera;
	private TextView wifi_setting_wifi_name;
	private TextView wifi_setting_safety;
	private TextView wifi_setting_signal_intensity;
	private TextView wifi_setting_wifi_state;
	private LinearLayout wifi_setting_signal_intensity_ll;
	private LinearLayout wifi_setting_password_ll;
	private Button manager_wifi_btn;
	private String[] videoApenc;
	private List<HiChipDefines.SWifiAp> wifi_list = Collections.synchronizedList(new ArrayList<HiChipDefines.SWifiAp>());
	HiChipDefines.HI_P2P_S_WIFI_PARAM wifi_param;
	private String ssid=" ";
	HiChipDefines.SWifiAp wifiSelect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_setting);

		String uid = getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_WIFI_PARAM, null);
				break;
			}
		}

		initView();
		
	}
	
	private void initView() {

		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_wifi_setting));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					WifiSettingActivity.this.finish();
					break;
				}
			}
		});

		wifi_setting_wifi_name=(TextView)findViewById(R.id.wifi_setting_wifi_name);
		wifi_setting_safety=(TextView)findViewById(R.id.wifi_setting_safety);

		wifi_setting_signal_intensity_ll=(LinearLayout)findViewById(R.id.wifi_setting_signal_intensity_ll);
		wifi_setting_signal_intensity=(TextView)findViewById(R.id.wifi_setting_signal_intensity);

		wifi_setting_wifi_state=(TextView)findViewById(R.id.wifi_setting_wifi_state);

		manager_wifi_btn=(Button)findViewById(R.id.manager_wifi_btn);
		manager_wifi_btn.setOnClickListener(this);
		videoApenc=getResources().getStringArray(R.array.video_apenc);
		wifi_setting_password_ll=(LinearLayout)findViewById(R.id.wifi_setting_password_ll);

		final EditText wifi_setting_password_et=(EditText)findViewById(R.id.wifi_setting_password_et);
		CheckBox wifi_setting_show_psw_cb=(CheckBox)findViewById(R.id.wifi_setting_show_psw_cb);
		wifi_setting_show_psw_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					wifi_setting_password_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}else{
					wifi_setting_password_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
		
		Button connectBtn=(Button)findViewById(R.id.wifi_setting_connect_wifi);
		connectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String ssid=wifi_setting_wifi_name.getText().toString().trim();
				String psw=wifi_setting_password_et.getText().toString().trim();
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_WIFI_PARAM , 
						HiChipDefines.HI_P2P_S_WIFI_PARAM.parseContent(
								HiChipP2P.HI_P2P_SE_CMD_CHN, 0, 
								wifiSelect.Mode, wifiSelect.EncType, ssid.getBytes(), 
								psw.getBytes()));
			

				showLoadingProgress();
			
				handler.sendEmptyMessageDelayed(SET_WIFI_END, 2000);

			}
		});

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL:
			{
				if(msg.arg2==0) {
					//					MyCamera camera = (MyCamera)msg.obj;
					Bundle bundle = msg.getData();
					byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
					switch (msg.arg1) {
					//获取wifi信息回调
					case HiChipDefines.HI_P2P_GET_WIFI_PARAM:
						wifi_param = new HiChipDefines.HI_P2P_S_WIFI_PARAM(data);

						ssid = Packet.getString(wifi_param.strSSID);
						String password = Packet.getString(wifi_param.strKey);

						String safe=videoApenc[wifi_param.EncType];
						Log.v("hichip", "ssid:"+ssid);



						wifi_setting_wifi_name.setText(ssid);
						wifi_setting_safety.setText(safe);

						//						setSelectedWifiInfo(ssid, wifi_param.EncType, (byte)0, (byte)1);

						if(ssid.length()>0) {
							//txt_choose_wifi.setVisibility(View.GONE);
							//lay_wifi_setting.setVisibility(View.VISIBLE);

							//txt_wifi_ssid.setText(ssid);
							//edit_wifi_password.setText(password);

						}


						break;
						//获取wifi列表回调
					case HiChipDefines.HI_P2P_GET_WIFI_LIST:

						int cnt = Packet.byteArrayToInt_Little(data,0);
						int size = HiChipDefines.SWifiAp.getTotalSize();
						wifi_list.clear();

						if (cnt > 0 && data.length >= 40) {

							int pos = 4;
							for (int i = 0; i < cnt; i++) {
								byte[] bty_ssid = new byte[32];
								System.arraycopy(data, i * size + pos, bty_ssid, 0, 32);
								byte mode = data[i * size + pos + 32];
								byte enctype = data[i * size + pos + 33];
								byte signal = data[i * size + pos + 34];
								byte status = data[i * size + pos + 35];
								wifi_list.add(new HiChipDefines.SWifiAp(bty_ssid, mode, enctype, signal, status));
							}
						}

						setListView();

						dismissLoadingProgress();

						break;
						
					case HiChipDefines.HI_P2P_SET_WIFI_PARAM:
						
						
						break;


					}
				}
			}
			break;
			case SET_WIFI_END:
				
				dismissLoadingProgress();
				HiToast.showToast(WifiSettingActivity.this, getString(R.string.tips_wifi_setting));

				showAlert(getString(R.string.tips_msg_wifi_set_restart_app));
				break;

			}
		}


	};



	private void setListView() {
		ListView wifiList=(ListView)findViewById(R.id.wifi_setting_wifi_list);
		wifiList.setVisibility(View.VISIBLE);
		WiFiListAdapter wifiAdapter=new WiFiListAdapter(this);
		wifiList.setAdapter(wifiAdapter);
		wifiList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		wifiSelect = wifi_list.get(position);
		String strSignal=wifiSelect.Signal+""+"%";
		wifi_setting_signal_intensity.setText(strSignal);
		wifi_setting_signal_intensity_ll.setVisibility(View.VISIBLE);
		wifi_setting_safety.setText(videoApenc[wifiSelect.EncType]);
		wifi_setting_wifi_name.setText(Packet.getString(wifiSelect.strSSID));
		wifi_setting_wifi_state.setVisibility(View.VISIBLE);
		if(Packet.getString(wifiSelect.strSSID).equals(Packet.getString(wifi_param.strSSID))){
			wifi_setting_wifi_state.setText(getResources().getString(R.string.wifi_setting_connected));
		}else{
			wifi_setting_wifi_state.setText(getResources().getString(R.string.wifi_setting_not_connect));
		}
		wifi_setting_password_ll.setVisibility(View.VISIBLE);
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
	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void onResume() {
		super.onResume();
		if(mCamera != null) {
			mCamera.registerIOSessionListener(this);
		}
	}




	@Override
	public void onPause() {
		super.onPause();
		if(mCamera != null) {
			mCamera.unregisterIOSessionListener(this);
		}
	}
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.manager_wifi_btn) {
			showLoadingProgress();
			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_WIFI_LIST, new byte[0]);


		} else {
		}

	}

	public class WiFiListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public WiFiListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wifi_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return wifi_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final HiChipDefines.SWifiAp wifi = wifi_list.get(position);

			if (wifi == null)
				return null;

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.list_setting_wifi, null);

				holder = new ViewHolder();
				holder.txt_ssid = (TextView) convertView.findViewById(R.id.wifi_setting_item_ssid);
				holder.txt_safety = (TextView) convertView.findViewById(R.id.wifi_setting_item_safety);
				holder.txt_intensity = (TextView) convertView.findViewById(R.id.wifi_setting_item_signal_intensity);
				holder.txt_state=(TextView)convertView.findViewById(R.id.wifi_setting_item_state);    
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			if(Packet.getString(wifi.strSSID).equals(Packet.getString(wifi_param.strSSID))){
				holder.txt_state.setText(getResources().getString(R.string.wifi_setting_connected));
			}else{
				holder.txt_state.setText(getResources().getString(R.string.wifi_setting_not_connect));
			}

			if (holder != null) {

				holder.txt_ssid.setText(Packet.getString(wifi.strSSID));
				holder.txt_safety.setText(videoApenc[wifi.EncType]);
				String strSignal=wifi.Signal+""+"%";
				holder.txt_intensity.setText(strSignal);
			}

			return convertView;

		}

		public final class ViewHolder {
			//			public ImageView img;
			public TextView txt_ssid;
			//public ImageView img_lock;
			public TextView txt_safety;
			public TextView txt_intensity;
			public TextView txt_state;
			//			public TextView txt_;
		}

	}



}
