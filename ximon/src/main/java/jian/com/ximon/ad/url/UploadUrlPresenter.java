package jian.com.ximon.ad.url;

import android.app.Activity;

/**
 * Created by ying on 17-11-26.
 */

public class UploadUrlPresenter {
    private UploadUrlModel uploadUrlModel;
    private Activity context;

    public UploadUrlPresenter(Activity context) {
        this.context = context;
        uploadUrlModel = new UploadUrlModel(context);
    }

    public void uploadFile(String url, int id, String time, String name, int type){
        uploadUrlModel.upLoadFile(url,id,time,name,type);
    }
}
