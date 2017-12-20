package jian.com.ximon.lamp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;
import jian.com.ximon.BaseActivity;
import jian.com.ximon.R;

/**
 * Created by 11833 on 2017/12/4.
 */

public class LampActivity extends BaseActivity{
    int state =0;
    private LampView  lampView;
    private Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);
        setupToolbar("灯光控制");
        initView();
        chargeLamp();
    }

    private void initView() {
        button = findViewById(R.id.button);
        lampView = findViewById(R.id.lampView);
        lampView.setLampState(1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lampView.setLampState();
            }
        });
    }

    //开关灯
    public void chargeLamp(){
        RestClient.builder()
                .url("")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(LampActivity.this,"成功",Toast.LENGTH_SHORT);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(LampActivity.this,"失败",Toast.LENGTH_SHORT);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(LampActivity.this,"失败",Toast.LENGTH_SHORT);
                    }
                })
                .build();
    }
}
