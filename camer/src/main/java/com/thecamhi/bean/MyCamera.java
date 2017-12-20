package com.thecamhi.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hichip.base.HiLog;
import com.hichip.control.HiCamera;
import com.hichip.push.HiPushSDK;
import com.hichip.tools.Packet;
import com.thecamhi.base.DatabaseManager;

public class MyCamera extends HiCamera {

	private long db_id;
	private String nikeName = "";
	private int videoQuality = HiDataValue.DEFAULT_VIDEO_QUALITY;
	private int alarmState = HiDataValue.DEFAULT_ALARM_STATE;
	private int pushState = HiDataValue.DEFAULT_PUSH_STATE;
	
	private boolean hasSummerTimer;

	private boolean isFirstLogin = true;

	private byte[] bmpBuffer = null;
	public Bitmap snapshot = null;

	private long lastAlarmTime;

	private boolean isSetValueWithoutSave = false;
	private int style;


	public MyCamera(String nikename,String uid, String username, String password) {
		super(uid, username, password);
		// TODO Auto-generated constructor stub
		this.nikeName = nikename;
	}

	public void saveInDatabase(Context context){
		DatabaseManager db=new DatabaseManager(context);
		db_id=db.addDevice(nikeName, getUid(), getUsername(), getPassword(), videoQuality, alarmState, pushState);
	}
	
	public void setSummerTimer(boolean hasSummerTimer){
		this.hasSummerTimer=hasSummerTimer;
	}
	public boolean getSummerTimer(){
		return this.hasSummerTimer;
	}
	
	public void saveInCameraList(){
		HiDataValue.CameraList.add(this);
	}

	public void deleteInCameraList(){
		HiDataValue.CameraList.remove(this);
		this.unregisterIOSessionListener();
		snapshot=null;
	}

	public long getLastAlarmTime(){
		return lastAlarmTime;
	}
	public void setLastAlarmTime(long lastAlarmTime){
		this.lastAlarmTime=lastAlarmTime;
	}

	public void updateInDatabase(Context context){
		DatabaseManager db=new DatabaseManager(context);
		db.updateDeviceByDBID(db_id,nikeName,
				getUid(), getUsername(), getPassword(), videoQuality, 
				HiDataValue.DEFAULT_ALARM_STATE, pushState);

		isSetValueWithoutSave = false;
	}

	public void deleteInDatabase(Context context){
		DatabaseManager db=new DatabaseManager(context);
		db.removeDeviceByUID(this.getUid());
		db.removeDeviceAlartEvent(this.getUid());
	}

	public int getAlarmState() {
		return alarmState;
	}
	public void setAlarmState(int alarmState) {
		this.alarmState = alarmState;
	}
	public int getPushState() {
		return pushState;
	}
	public void setPushState(int pushState) {
		this.pushState = pushState;
	}
	public void setStyle(int style){
		this.style=style;
	}
	public int getStyle(){

		return style;
	}
	public int getVideoQuality() {
		return videoQuality;
	}
	public void setVideoQuality(int videoQuality) {
		this.videoQuality = videoQuality;
	}
	public String getNikeName() {
		return nikeName;
	}
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}
	public long getDbID() {
		return db_id;
	}
	public void setDbID(long db_id) {
		this.db_id = db_id;
	}

	private int curbmpPos = 0;
	public boolean reciveBmpBuffer(byte[] byt) {

		if(byt.length < 10) {
			return false;
		}

		if(bmpBuffer == null) {
			curbmpPos = 0;
			int buflen = Packet.byteArrayToInt_Little(byt,0);
			if(buflen <=0){
				return false;
			}
			bmpBuffer = new byte[buflen];
		//	HiLog.e(bmpBuffer==null?"bmpBuffer is null":"bmpBuffer not null"+
		//			"\n"+"buflen:"+buflen+"");
		}

		//Arrays.fill(bmpBuffer, (byte) 0);

		int datalen = Packet.byteArrayToInt_Little(byt,4);
	//	HiLog.e("bytLen:"+byt.length + "     datalen:"+datalen);


		if(curbmpPos + datalen <= bmpBuffer.length)
			System.arraycopy(byt, 10, bmpBuffer, curbmpPos, datalen);

	//	HiLog.e("byt="+Packet.getHex(byt, byt.length)+" \n"+"bmpBuffer="+
	//			Packet.getHex(bmpBuffer,bmpBuffer.length)+" ");

		curbmpPos += (datalen);

	//	HiLog.e("curbmpPos="+curbmpPos+"");
		//		Log.e("camera", "recivebmpbuffer");
		short flag = Packet.byteArrayToShort_Little(byt, 8); 

		if(flag == 1) {
	//		HiLog.e("flag=:"+1 );

			createSnapshot();
			return true;
		}

		return false;
	}











	private void createSnapshot() {
		/*ByteArrayInputStream stream=new ByteArrayInputStream(bmpBuffer);
		HiLog.e("stream is null:"+stream);


		//HiLog.e(Packet.getHex(bmpBuffer, bmpBuffer.length)+"");
	
		snapshot=BitmapFactory.decodeStream(stream, null, null);*/

	
		
		Bitmap snapshot_temp=BitmapFactory.decodeByteArray(bmpBuffer, 0, bmpBuffer.length);

		if(snapshot_temp!=null)
			snapshot=snapshot_temp;


		bmpBuffer=null;
		curbmpPos = 0;
//		HiLog.e(snapshot==null?"sure is null":"snapshot not null");

	}


	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public boolean isSetValueWithoutSave() {
		return isSetValueWithoutSave;
	}

	
	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
		if(getUid() != null && getUid().length()>4){
			String temp = getUid().substring(0, 4);
			if(!temp.equalsIgnoreCase("AAAA")&&!temp.equalsIgnoreCase("BBBB")&&
					!temp.equalsIgnoreCase("CCCC")&&!temp.equalsIgnoreCase("DDDD")
					&&!temp.equalsIgnoreCase("EEEE")&&!temp.equalsIgnoreCase("ZZZZ")) {
				HiLog.v("temp:"+temp);
				return;
			}
		}
		else {
			return;
		}
		super.connect();
	}
	
	public interface OnBindPushResult {
		 void onBindSuccess();
		 void onBindFail();
		 void onUnBindSuccess();
		 void onUnBindFail();
		
	}

	private OnBindPushResult onBindPushResult;
	
	
	private HiPushSDK push;
	private HiPushSDK.OnPushResult pushResult= new HiPushSDK.OnPushResult() {

		@Override
		public void pushBindResult(int subID, int type, int result) {
			// TODO Auto-generated method stub
			isSetValueWithoutSave = true;
			
			if(type == HiPushSDK.PUSH_TYPE_BIND) {
				if(HiPushSDK.PUSH_RESULT_SUCESS == result) {
					pushState = subID;
					if(onBindPushResult != null) 
						onBindPushResult.onBindSuccess();
				}
				else if(HiPushSDK.PUSH_RESULT_FAIL == result) {
					if(onBindPushResult != null)
						onBindPushResult.onBindFail();
				}
			}
			else if(type == HiPushSDK.PUSH_TYPE_UNBIND) {
				if(HiPushSDK.PUSH_RESULT_SUCESS == result) {
					pushState = HiDataValue.DEFAULT_PUSH_STATE;
					if(onBindPushResult != null)
						onBindPushResult.onUnBindSuccess();
				}
				else if(HiPushSDK.PUSH_RESULT_FAIL == result) {
					if(onBindPushResult != null)
						onBindPushResult.onUnBindFail();
				}

			}
				
		}
	};
	
	
	public void bindPushState(boolean isBind,OnBindPushResult bindPushResult) {
		if(HiDataValue.XGToken == null) {
			return;
		}
		if(push == null) {
			push = new HiPushSDK(HiDataValue.XGToken, getUid(), HiDataValue.company, pushResult);
		}
		onBindPushResult = bindPushResult;
		
		if(isBind)
			push.bind();
		else
			push.unbind();
	}

}
