package com.thecamhi.base;

import android.content.Context;

import com.hichip.base.HiLog;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.thecamhi.bean.HiDataValue;
import com.ximon.light.R;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class PushMessageReceiver  extends XGPushBaseReceiver{

//	private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");

//	public interface OnMessageReceiverCallback {
//	        void onNotifactionShowedResult();
//    }
//	
//	private static OnMessageReceiverCallback cbk = null;
//	public static void loadSJThread(OnMessageReceiverCallback onCallback) {
//		cbk = onCallback; 
//	}
	
	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onDeleteTagResult");
	}

	@Override
	public void onNotifactionClickedResult(Context arg0,
			XGPushClickedResult arg1) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onNotifactionClickedResult");
		
	}

	@Override
	public void onNotifactionShowedResult(Context arg0, XGPushShowedResult arg1) {
		// TODO Auto-generated method stub
//		HiLog.e("XingeDemo  onNotifactionShowedResult   :");
	}

	@Override
	public void onRegisterResult(Context arg0, int arg1, XGPushRegisterResult arg2) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onRegisterResult");
	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onSetTagResult");
	}

	@Override
	public void onTextMessage(Context arg0, XGPushTextMessage arg1) {
		// TODO Auto-generated method stub
		HiLog.e("XingeDemo  onTextMessage:");
		
		
		String key = arg1.getCustomContent();
		String uid = null;
		int type = 0;
		int time = 0;
		if(key!= null) {
			try {
				JSONObject arrJson = new JSONObject(key);
				String jsonc = arrJson.getString("content");
				JSONObject conJson = new JSONObject(jsonc);
				uid = conJson.getString("uid");
				type = conJson.getInt("type");
				time = conJson.getInt("time");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(uid == null)
				return;
			
	
			if(HiDataValue.CameraList.size() > 0) {
				return;
	//			for(MyCamera camera: HiDataValue.CameraList) {
	//				if(camera.getUid().equals(uid)) {
	//					camera.setAlarmState(1);
	//					camera.updataInDatabase(arg0);
	//				}
	//			}
			}
			
			String strAlarmType[] = arg0.getResources().getStringArray(R.array.tips_alarm_list_array);
			
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
			XGPushManager.addLocalNotification(arg0, local_msg);
			
		}
			
//		XGLocalMessage msg = new XGLocalMessage();
//		msg.setBuilderId(100);
//		msg.setAction_type(1);
//		msg.setContent("Alarm");
//		msg.setActivity("com.hichip.camhi.MainActivity");
//		msg.setPackageName("com.yelaw.ROCAM");
//		
//		
//		String key = arg1.getCustomContent();
////		Log.v("ouyang", "XingeDemo  onTextMessage   :"+key);
//		String uid = null;
//		int type = 0;
//		int time = 0;
//		if(key!= null) {
////			Log.v("ouyang", "XingeDemo  onTextMessage   222:"+key);
//			try {
//				JSONObject arrJson = new JSONObject(key);
//				String jsonc = arrJson.getString("content");
//				JSONObject conJson = new JSONObject(jsonc);
//				uid = conJson.getString("uid");
//				type = conJson.getInt("type");
//				time = conJson.getInt("time");
////				
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
////			Log.v("ouyang", "XingeDemo  onNotifactionShowedResult   uid:"+uid);
//			if(uid!= null) {
//				HashMap<String, Object> custom = new HashMap<String, Object>();
//				custom.put("content", "{\"uid\":\""+uid+"\",\"type\":"+type+",\"time\":"+time+"}");
//				
//				msg.setCustomContent(custom);
//				XGPushManager.addLocalNotification(arg0, msg);
//			}
//		}
	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onUnregisterResult");
	}

}