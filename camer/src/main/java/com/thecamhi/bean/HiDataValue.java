package com.thecamhi.bean;

import java.util.ArrayList;
import java.util.List;

public class HiDataValue {
	public final static int DEFAULT_VIDEO_QUALITY = 1;
	public final static int DEFAULT_ALARM_STATE = 0;
	public final static int DEFAULT_PUSH_STATE = 0;//0¹Ø±Õ£¬1¿ªÆô

	public final static int NOTICE_RUNNING_ID = 20001;
	public final static int NOTICE_ALARM_ID = 20002;

	public static int model=0;

	public final static String EXTRAS_KEY_UID = "uid";
	public final static String EXTRAS_KEY_DATA = "data";

	public final static String ACTION_CAMERA_INIT_END= "camera_init_end";

	public final static int HANDLE_MESSAGE_SESSION_STATE = 0x90000001;
	public final static int HANDLE_MESSAGE_RECEIVE_IOCTRL = 0x90000003;
	public final static int HANDLE_MESSAGE_SCAN_RESULT = 0x90000005;
	public final static int HANDLE_MESSAGE_DOWNLOAD_STATE=0x90000007;

	public static List<MyCamera> CameraList = new ArrayList<MyCamera>();
	public static String[] zifu={"&","'","~","*","(",")","/","\"","%","!",":",";",".","<",">",",","'"};

	public static boolean isOnLiveView = false;
	public static final String STYLE="style";

	public static String XGToken;
	

	public static final String company = "camhidemo";
}
