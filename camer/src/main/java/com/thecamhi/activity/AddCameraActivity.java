package com.thecamhi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hichip.base.HiLog;
import com.hichip.tools.HiSearchSDK;
import com.hichip.tools.HiSearchSDK.HiSearchResult;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.thecamhi.zxing.QRCodeCaptureActivity;
import com.ximon.light.R;

import java.util.ArrayList;
import java.util.List;

public class AddCameraActivity extends HiActivity implements OnClickListener{
	private final static int REQUEST_CODE_SCAN_RESULT = 0;
	private final static int REQUEST_SCANNIN_GREQUEST_CODE = 1;
	private final static int REQUEST_WIFI_CODE=2;
	private ScanResultAdapter adapter;
	private EditText add_camera_uid_edt,add_camera_name_et,add_camera_username_et,add_camera_psw_et;
	private HiSearchSDK searchSDK;
	private List<HiSearchResult> list = new ArrayList<HiSearchResult>();
	private MyCamera camera;

	private boolean isSearch;//用于记录是否正在搜索的状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_camera_view);
		initView();
	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.add_camera));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);
		title.setRightBtnText(getResources().getString(R.string.finish));
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
					case TitleView.NAVIGATION_BUTTON_LEFT:
						AddCameraActivity.this.finish();
						break;
					case TitleView.NAVIGATION_BUTTON_RIGHT:
						chickDone();
						break;

				}
			}
		});

		//扫描二维码
		LinearLayout scanner_QRcode_ll=(LinearLayout)findViewById(R.id.scanner_QRcode_ll);
		scanner_QRcode_ll.setOnClickListener(this);
		//局域网搜索
		LinearLayout search_in_lan_ll=(LinearLayout)findViewById(R.id.search_in_lan_ll);
		search_in_lan_ll.setOnClickListener(this);
		//wifi一键设置
		LinearLayout one_key_setting_wifi_ll=(LinearLayout)findViewById(R.id.one_key_setting_wifi_ll);
		one_key_setting_wifi_ll.setOnClickListener(this);


		add_camera_name_et=(EditText)findViewById(R.id.add_camera_name_et);
		add_camera_username_et=(EditText)findViewById(R.id.add_camera_username_et);
		add_camera_uid_edt=(EditText)findViewById(R.id.add_camera_uid_edt);
		add_camera_psw_et=(EditText)findViewById(R.id.add_camera_psw_et);

		setOnLoadingProgressDismissListener(new MyDismiss() {

			@Override
			public void OnDismiss() {
				isSearch=false;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			switch (requestCode) {
				//扫描条码的回调
				case REQUEST_SCANNIN_GREQUEST_CODE:
					Bundle extras = data.getExtras();
					String uid = extras.getString(HiDataValue.EXTRAS_KEY_UID).trim();

					add_camera_uid_edt.setText(uid);

					break;
				//wifi一键设置的回调
				case REQUEST_WIFI_CODE:

					showLoadingProgress();
					isSearch=true;
					initSDK();
					break;

				default:
					break;
			}
		}

	}


	private boolean checkPermission(String permission){
		int checkCallPhonePermission = ContextCompat.
				checkSelfPermission(AddCameraActivity.this,permission);
		if(checkCallPhonePermission == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.scanner_QRcode_ll) {
			if (!checkPermission(Manifest.permission.CAMERA)) {
				Toast.makeText(AddCameraActivity.this, getString(R.string.tips_no_permission), Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			intent.setClass(AddCameraActivity.this, QRCodeCaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, REQUEST_SCANNIN_GREQUEST_CODE);

		} else if (i == R.id.search_in_lan_ll) {//局域网搜索UID
			isSearch = true;
			showLoadingProgress();
			initSDK();

		} else if (i == R.id.one_key_setting_wifi_ll) {
			if (isWifiConnected(AddCameraActivity.this)) {
				Intent intent = new Intent(AddCameraActivity.this, WifiOneKeySettingActivity.class);
				startActivityForResult(intent, REQUEST_WIFI_CODE);
			} else {
				HiToast.showToast(AddCameraActivity.this, getString(R.string.connect_to_WIFI_first));
			}


		}

	}

	//判断wifi是否连接
	public boolean isWifiConnected(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(wifiNetworkInfo.isConnected())
		{
			return true ;
		}

		return false ;
	}


	private void initSDK() {

		//使用searchSDK进行搜索
		searchSDK = new HiSearchSDK(new HiSearchSDK.OnSearchResult() {
			@Override
			public void searchResult(List<HiSearchResult> arg0) {
				// TODO Auto-generated method stub
				HiLog.e("list size:"+arg0.size());
				list = arg0;
				Message msg = handler.obtainMessage();
				msg.what = HiDataValue.HANDLE_MESSAGE_SCAN_RESULT;
				handler.sendMessage(msg);

			}
		});

		searchSDK.search();
	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case HiDataValue.HANDLE_MESSAGE_SCAN_RESULT:

					if(!isSearch) {

						return;
					}
					dismissLoadingProgress();
					if(list==null||list.size()==0)
					{
						HiToast.showToast(AddCameraActivity.this, getString(R.string.tips_internet_no_cameras));
						return;
					}


					showResultDialog(AddCameraActivity.this);



					break;

			}
		}
	};



	private void showResultDialog(Context context) {


		LayoutInflater layoutInflater = LayoutInflater.from(context);
		//   final Dialog dialog = new Dialog(context);
		View popView = layoutInflater.inflate(R.layout.dialog_list_search_result, null);
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialog.setContentView(popView);

		// dialog.setCancelable(true);
		ListView listView=(ListView) popView.findViewById(R.id.search_list_camera);
		adapter=new ScanResultAdapter(context);
		listView.setAdapter(adapter);

		// dialog.show();
		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.search_in_lan)
				.setView(popView).setCancelable(true)
				.setNegativeButton(getResources().getString(R.string.refresh), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						showLoadingProgress();
						isSearch=true;
						initSDK();

					}
				}).setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();
					}
				}).show();
		dismissLoadingProgress();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				HiSearchResult result=list.get(position);
				add_camera_uid_edt.setText(result.uid.toString());
				alertDialog.dismiss();
			}
		});

	}

	private void chickDone() {

		String str_nike = add_camera_name_et.getText().toString();
		String str_uid = add_camera_uid_edt.getText().toString().trim();
		String str_password = add_camera_psw_et.getText().toString().trim();
		String str_username = add_camera_username_et.getText().toString();



		if (str_nike.length() == 0) {
			showAlert(getText(R.string.tips_null_nike));
			return;
		}

		if (str_uid.length() == 0) {
			showAlert(getText(R.string.tips_null_uid));
			return;
		}

		if(str_username.length()== 0 ) {
			showAlert(getText(R.string.tips_null_username));
			return;
		}




		for(int i=0;i<HiDataValue.zifu.length;i++){
			if(str_uid.contains(HiDataValue.zifu[i])){
				Toast.makeText(AddCameraActivity.this, getText(R.string.tips_illegal_uid).toString(), Toast.LENGTH_SHORT).show();
				return;
			}
		}


		for(MyCamera camera :HiDataValue.CameraList) {
			if(str_uid.equalsIgnoreCase(camera.getUid())) {
				showAlert(getText(R.string.tips_add_camera_exists));
				return;
			}
		}


		camera = new MyCamera(str_nike, str_uid, str_username, str_password);
		camera.saveInDatabase(this);
		camera.saveInCameraList();
		camera.connect();

		Intent broadcast = new Intent();
		broadcast.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
		sendBroadcast(broadcast);


		Bundle extras = new Bundle();
		extras.putString(HiDataValue.EXTRAS_KEY_UID, str_uid);
		Intent intent = new Intent();
		intent.putExtras(extras);
		this.setResult(RESULT_OK,intent);
		this.finish();

	}




	private class ScanResultAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ViewHolder holder;

		public ScanResultAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if(convertView == null) {
				convertView = inflater.inflate(R.layout.list_scan_result, null);
				holder = new ViewHolder();
				holder.uid = (TextView)convertView.findViewById(R.id.txt_camera_uid);
				holder.ip=(TextView)convertView.findViewById(R.id.txt_camera_ip);

				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			HiSearchResult result = list.get(position);

			holder.ip.setText(result.ip);
			holder.uid.setText(getString(R.string.list_txt_uid)+result.uid);

			return convertView;
		}

		public final class ViewHolder {
			public TextView uid;
			public TextView ip;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isSearch=false;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				isSearch=false;
				finish();
				break;
		}

		return true;
	}



}