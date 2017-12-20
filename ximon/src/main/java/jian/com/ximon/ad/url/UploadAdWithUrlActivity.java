package jian.com.ximon.ad.url;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jian.com.ximon.BaseActivity;
import jian.com.ximon.R;
import jian.com.ximon.util.Constant;
import jian.com.ximon.util.InputChecker;

/**
 * Created by ying on 17-11-23.
 */

public class UploadAdWithUrlActivity extends BaseActivity implements UploadUrlView{
    private EditText  edtUrl,edtName,edtTime;//图片或者url的地址,广告名称，播放是时间

    private Button btFinish;//点击完成按钮
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_with_url);
        initView();
    }

    private void initView() {
        edtName = findViewById(R.id.edtName);
        edtUrl = findViewById(R.id.edtUrl);
        edtTime = findViewById(R.id.edtTime);
        btFinish= findViewById(R.id.btFinish);
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InputChecker.isCorrect(edtName,edtUrl,edtTime)){
                    //当输入的都不为空的时候


                }
            }
        });
    }

    @Override
    protected void onResume() {
        //绑定eventbus
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        //解绑eventbus
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * 上传成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void uploadState(int state) {
        //上传成功关掉当前的activity
        if (state== Constant.SUCCESS) {
            Toast.makeText(this,"上传成功",Toast.LENGTH_SHORT).show();
            finish();

        }
        else{

            /**
             * 上传失败
             */
            Toast.makeText(this,"上传失败",Toast.LENGTH_SHORT).show();
        }
    }


}
