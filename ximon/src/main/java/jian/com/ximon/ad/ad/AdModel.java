package jian.com.ximon.ad.ad;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;
import jian.com.ximon.ad.ad.bean.AD;
import jian.com.ximon.machine.MachineActivity;

import static jian.com.ximon.util.Constant.BASH_PATH;

/**
 * Created by ying on 17-11-13.
 */

public class AdModel {
    private AdActivity context;

    public AdModel(AdActivity context) {
        this.context = context;
    }

    public void loadAD(String url){
        //获取后台广告机数据
        RestClient.builder()
                .url(BASH_PATH+url)
                .params("tag","phone")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        //获取成功
                        //解析返回的数据
                        int first = response.indexOf("\"");//第一个“的位置
                        int last = response.lastIndexOf("\"");//最后一个“的位置
                        response =response.substring(first+1,last);
                        response = response.replaceAll("\\\\","");
                        List<AD> adList;
                        Type type = new TypeToken<ArrayList<AD>>() {}.getType();
                        Gson gson = new Gson();
                        Log.d(MachineActivity.TAG,response);
                        adList = gson.fromJson(response,type);
                        Log.d(MachineActivity.TAG,adList.size()+"");
                        AdEvent adEvent = new AdEvent(adList);
                        Log.d("ADmodle",adList.toString());
                        EventBus.getDefault().post(adEvent);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        //获取失败
                        Log.d(MachineActivity.TAG,"获取失败");
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        //获取出错
                        Log.d(MachineActivity.TAG,"获取出错");
                    }
                })
                .build()
                .post();
    }

    public void deleteAD(String url){
        //获取后台广告机数据
        RestClient.builder()
                .url(url)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        //获取成功
                        //解析返回的数据
                        Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(response);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        //获取失败
                        Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();

                        Log.d(MachineActivity.TAG,"失败");
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        //获取出错
                        Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();

                        Log.d(MachineActivity.TAG,"出错");
                    }
                })
                .build()
                .post();
    }

    /**
     * 推送广告
     * @param url
     * @param machine_id 广告机的id
     */
    public void SendAD(String url,int machine_id){

        RestClient.builder()
                .url(url)
                .params("machine_id",machine_id)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        //推送成功
                        //推送返回的数据
                        Log.d(MachineActivity.TAG,"成功");
                        Toast.makeText(context,"推送成功",Toast.LENGTH_SHORT).show();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        //推送失败
                        Log.d(MachineActivity.TAG,"失败");
                        Toast.makeText(context,"推送失败,请重新推送",Toast.LENGTH_SHORT).show();

                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        //推送出错
                        Log.d(MachineActivity.TAG,"出错");
                        Toast.makeText(context,"推送出错,请重新推送",Toast.LENGTH_SHORT).show();

                    }
                })
                .build()
                .post();
    }
}
