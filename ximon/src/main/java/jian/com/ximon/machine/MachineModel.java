package jian.com.ximon.machine;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;

import static jian.com.ximon.util.Constant.BASH_PATH;

/**
 * Created by ying on 17-11-13.
 */

public class MachineModel {
    private Context context;

    public MachineModel(Context context) {
        this.context = context;
    }

    public void loadMachine(String url){
        //获取后台广告机数据
        RestClient.builder()
                .url(BASH_PATH+url)
                .loader(context)//显示加载中
                .params("tag","phone")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        //获取成功
                        //解析返回的数据
                        int frist = response.indexOf("\"");//第一个“的位置
                        int last = response.lastIndexOf("\"");//最后一个“的位置
                        response =response.substring(frist+1,last);
                        response = response.replaceAll("\\\\","");
                        Log.d(MachineActivity.TAG,response);
                        Log.d(MachineActivity.TAG,"[{\"model\": \"ad.machine\", \"pk\": 1, \"fields\": {\"user\": 1, \"name\": \"222\", \"address\": \"333\", \"add_time\": \"2017-12-05T11:53:42Z\", \"code_numderId\": 1}}, {\"model\": \"ad.machine\", \"pk\": 2, \"fields\": {\"user\": 1, \"name\": \"22\", \"address\": \"33\", \"add_time\": \"2017-12-07T11:54:46Z\", \"code_numderId\": 2}}]\n");
                        List<Machine> machines=new ArrayList<>(JSONArray.parseArray(response,Machine.class));
                        MachineEvent machineEvent = new MachineEvent(machines);
                        EventBus.getDefault().post(machineEvent);
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
    public void delete(String url,int machineId){
        RestClient.builder()
                .url(BASH_PATH+url)
                .loader(context)
                .params("tag","phone")
                .params("machine_id",machineId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        //删除成功
                        //解析返回的数据
                        Log.d(MachineActivity.TAG,"删除成功 "+response);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        //删除失败
                        Log.d(MachineActivity.TAG,"删除失败");
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        //删除出错
                        Log.d(MachineActivity.TAG,"删除出错");
                    }
                })
                .build()
                .post();
    }
}
