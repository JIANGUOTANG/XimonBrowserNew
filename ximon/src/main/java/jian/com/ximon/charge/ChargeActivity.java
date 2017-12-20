package jian.com.ximon.charge;

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

public class ChargeActivity extends BaseActivity{
    int state =0;
    private Button btSwitch;
    private ChargeView chargeView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        setupToolbar("充电管理");
        charge();
        initView();
    }

    private void initView() {
        btSwitch=findViewById(R.id.btSwitch);
        chargeView=findViewById(R.id.chargeView);

        btSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chargeView.update();
            }
        });
    }

    //开关充电
    public void charge(){
        RestClient.builder()
                .url("")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(ChargeActivity.this,"成功",Toast.LENGTH_SHORT);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(ChargeActivity.this,"失败",Toast.LENGTH_SHORT);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(ChargeActivity.this,"失败",Toast.LENGTH_SHORT);
                    }
                })
                .build();
    }
}
