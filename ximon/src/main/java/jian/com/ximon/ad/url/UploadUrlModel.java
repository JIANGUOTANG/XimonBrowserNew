package jian.com.ximon.ad.url;

import android.app.Activity;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;
import jian.com.ximon.util.Constant;

import static jian.com.ximon.machine.MachineActivity.TAG;
import static jian.com.ximon.util.Constant.BASH_PATH;

/**
 * Created by ying on 17-11-26.
 */

public class UploadUrlModel {

    /**
     * @param path 上传文件的路径
     * @param id   广告机的id
     * @param time 播放的时间
     * @param name 广告名称
     */

    Activity activity;

    public UploadUrlModel(Activity activity) {
        this.activity = activity;
    }

    /**
     *
     * @param url //广告的地址
     * @param id //对应的广告机的id
     * @param time //播放时间
     * @param name //广告播放是时间
     * @param type
     */
    public void upLoadFile(String url, int id, String time, String name, int type) {

        //上传的路径
        String postUrl = BASH_PATH + "/index/upload_file/" + id;
        RestClient.builder()
                .loader(activity)
                .url(postUrl)//上传路径
                .params("name",name)
                .params("time",time)
                .params("url",url)
                .params("type",type)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Log.i(TAG, "success");
                        EventBus.getDefault().post(Constant.SUCCESS);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Log.i(TAG, "failure");
                        EventBus.getDefault().post(Constant.FAILURE);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.i(TAG, "error");
                        EventBus.getDefault().post(Constant.FAILURE);
                    }
                })
                //设置文件名称
                .build()
                .upload();

    }

}
