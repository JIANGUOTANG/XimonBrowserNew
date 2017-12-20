package jian.com.ximon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import example.festec.jian.com.latte.app.Latte;
import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;

/**
 * Created by ying on 17-11-8.
 */

public class LoginActivity extends AppCompatActivity{

    private EditText edtName;//用户名
    private EditText edtPassword;//密码
    private Button btLogin;//登录按钮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Latte.getConfigurator().withIActivity(this);
        initView();
    }

    private void initView() {
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        btLogin = findViewById(R.id.btLogin);
        //点击登录
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput()){
                    //输入正确，进行登录逻辑
                    RestClient.builder()
                            .url("/login/")
                            .loader(LoginActivity.this)
                            .success(new ISuccess() {
                                @Override
                                public void onSuccess(String response) {
                                    Log.d("jian",response);
                                    if(Integer.valueOf(response)==1){
                                        //登录成功
                                        Log.d("jian",response);
                                        Intent intent = new Intent(LoginActivity.this,FirstMainActivity.class);
                                        startActivity(intent);
                                    }

                                    Log.d("jian",response);
                                }
                            })
                            .failure(new IFailure() {
                                @Override
                                public void onFailure() {
                                    Log.d("jian","登录失败");

                                }
                            })
                            .error(new IError() {
                                @Override
                                public void onError(int code, String msg) {
                                    Log.d("jian","登录失败");

                                }
                            })
                            .params("username","ximon")
                            .params("password","jian")
                            .params("tag","phone")
                            .build()
                            .post();

                }
            }
        });
    }

    //检查输入是否正确
    private boolean checkInput(){
        //输入不能为空
        if(edtName.getText().toString().length()>0&&edtPassword.getText().toString().length()>0){
            return true;
        }
        return false;
    }

}
