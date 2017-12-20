package jian.com.ximon.machine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.ISuccess;
import jian.com.ximon.R;
import jian.com.ximon.util.InputChecker;

/**
 * Created by ying on 17-11-15.
 */

public class MachineAddActivity extends AppCompatActivity {
    private EditText edtName, edtAddress, edtCodeNumber;//名称，地址，标识码
    private Button btFinish;
    private int MachineId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_machine);
        Intent intent = getIntent();
        MachineId = intent.getIntExtra("machine_id",0);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        edtCodeNumber = findViewById(R.id.edtCodeNumber);
        btFinish = findViewById(R.id.btFinish);
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InputChecker.isCorrect(edtName, edtAddress, edtCodeNumber)) {
                    //输入都正确
                    if(MachineId==0) {
                        //添加
                        RestClient.builder()
                                .url("/index/addMachine/")
                                .loader(MachineAddActivity.this)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        setResult(2);
                                        finish();
                                    }
                                })
                                .params("name", edtName.getText().toString())
                                .params("number", edtCodeNumber.getText().toString())
                                .params("location", edtAddress.getText().toString())

                                .build()
                                .post();
                    }else{
                        //修改
                        RestClient.builder()
                                .url("/index/editMachine/")
                                .loader(MachineAddActivity.this)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {

                                    }
                                })
                                .params("name", edtName.getText().toString())
                                .params("machine_id", MachineId)
                                .params("number", edtCodeNumber.getText().toString())
                                .params("location", edtAddress.getText().toString())
                                .build()
                                .post();
                    }
                }
            }
        });
    }

}
