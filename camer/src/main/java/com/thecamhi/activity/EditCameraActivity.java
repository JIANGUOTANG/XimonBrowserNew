package com.thecamhi.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import com.thecamhi.base.TitleView;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.ximon.light.R;



public class EditCameraActivity extends HiActivity {

	
	private MyCamera mCamera;
	
	private EditText edt_nikename;
	private EditText edt_uid;
	private EditText edt_username;
	private EditText edt_password;
//	private ToggleButton togbtn_delete_camera;
	
	
	private String strNikname;
	private String strUid;
	private String strUsername;
	private String strPassword;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_camera);
		
		Bundle bundle = this.getIntent().getExtras();
		String uid = bundle.getString(HiDataValue.EXTRAS_KEY_UID);
		
		for(MyCamera camera: HiDataValue.CameraList) {
			if(camera.getUid().equals(uid)) {
				mCamera = camera;
				break;
			}
		}
		
		initView();
	}
	
	private void initView() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		TitleView nb = (TitleView)findViewById(R.id.title_top);
		
		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);
		nb.setTitle(getString(R.string.title_add_camera));
		nb.setRightBtnText(getString(R.string.finish));
		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {
			
			@Override
			public void OnNavigationButtonClick(int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					EditCameraActivity.this.finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:
					chickDone();
					break;
				}
			}
		});
		
		
		edt_nikename = (EditText)findViewById(R.id.edt_nikename);
		edt_uid = (EditText)findViewById(R.id.edt_uid);
		edt_username = (EditText)findViewById(R.id.edt_username);
		edt_password = (EditText)findViewById(R.id.edt_password);
		
		
		strNikname = mCamera.getNikeName();
		strUid = mCamera.getUid();
		strUsername = mCamera.getUsername();
		strPassword = mCamera.getPassword();
		
		edt_nikename.setText(strNikname);
		edt_uid.setText(strUid);
		edt_username.setText(strUsername);
		edt_password.setText(strPassword);
		
	/*	togbtn_delete_camera = (ToggleButton)findViewById(R.id.togbtn_delete_camera);
		togbtn_delete_camera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteCamera();
			}
		});*/
		
	}

	private void chickDone() {
		String str_nike = edt_nikename.getText().toString();
		String str_uid = edt_uid.getText().toString().trim();
		String str_password = edt_password.getText().toString().trim();
		String str_username = edt_username.getText().toString();
		
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
		
		
		strNikname = mCamera.getNikeName();
		strUid = mCamera.getUid();
		strUsername = mCamera.getUsername();
		strPassword = mCamera.getPassword();
		
//		HiLog.v(
//		"   strUid:"+strUid
//		+"   strUsername:"+strUsername
//		+"   strPassword:"+strPassword
//		+"   str_uid:"+str_uid
//		+"   str_username:"+str_username
//		+"   str_password:"+str_password
//		);
		if(!strNikname.equals(str_nike)) {
			mCamera.setNikeName(str_nike);
		}
		if( !strUid.equals(str_uid) || !strUsername.equals(str_username)|| !strPassword.equals(str_password)) {
			mCamera.setUid(str_uid);
			mCamera.setPassword(str_password);
			mCamera.setUsername(str_username);
			
			mCamera.disconnect();
			mCamera.connect();
		}		
		
		mCamera.updateInDatabase(this);
		
		Intent intent = new Intent();
		intent.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
		sendBroadcast(intent);
		
		this.finish();
	}
	
	
/*	private void deleteCamera() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getText(R.string.tips_warning)).setMessage(getText(R.string.tips_msg_delete_camera)).setPositiveButton(getText(R.string.btn_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				mCamera.disconnect();
				mCamera.deleteInCameraList();
				mCamera.deleteInDatabase(EditCameraActivity.this);
				
				Intent intent = new Intent();
				intent.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
				sendBroadcast(intent);
				EditCameraActivity.this.finish();
			}
		}).setNegativeButton(getText(R.string.btn_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				togbtn_delete_camera.setChecked(false);
			}
		}).show();
	
	}*/

}
