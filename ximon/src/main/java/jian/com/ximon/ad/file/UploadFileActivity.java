package jian.com.ximon.ad.file;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.List;

import jian.com.ximon.R;
import jian.com.ximon.util.InputChecker;

/**
 * Created by ying on 17-11-8.
 */

public class UploadFileActivity extends AppCompatActivity implements UpLoadFileView {

    private static final String TAG = "AdManagerActivity";
    List<LocalMedia> selectList;//媒体信息（图片视频）
    private UploadFilePresenter presenter;
    private ImageView imgPreview;//图片预览
    private VideoView videoPreview;//视频预览
    private Button btChoiceFile;//选择文件按钮
    private String path;//上传文件的路径
    private Button btFinish;//完成按钮
    private int MachineId ;//广告机的Id
    private TextView tvFileName;//文件名
    private EditText edtName;//输入的文件名
    private EditText edtTime;//输入播放时间
    private final int IMAGE =1;//图片
    private final int VIDEO = 2;//视频
    private int type = IMAGE;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, TAG + "onCreate");
        setContentView(R.layout.activity_upload_with_file);
        initView();
        initData();
        presenter = new UploadFilePresenter(this, this);

    }

    private void initData() {
        Intent intent  = getIntent();
        //获取广告机的id
        MachineId = intent.getIntExtra("machine_id",0);
    }

    /**
     * 初始化
     */
    private void initView() {
        btChoiceFile = findViewById(R.id.btChoiceFile);
        imgPreview = findViewById(R.id.img_preview);
        videoPreview = findViewById(R.id.video_preview);
        btFinish = findViewById(R.id.btFinish);
        tvFileName = findViewById(R.id.tvFileName);
        edtName = findViewById(R.id.edtName);
        edtTime = findViewById(R.id.edtTime);
        btChoiceFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击选择文件按钮
                presenter.loadFile();
            }
        });
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击完成按钮
                if(tvFileName.getText().toString().length()>0) {
                    final String name = edtName.getText().toString();
                    final String time = edtTime.getText().toString();
                    if (InputChecker.isCorrect(edtName, edtTime)) {

                        presenter.upLoadFile(path, MachineId, time, name, type);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    //文件上传
                    path = selectList.get(0).getPath();
                    File file = new File(path);
                    tvFileName.setText(file.getName());
                    LocalMedia item = selectList.get(0);
                    //预览图
                    if (item.getPictureType().contains("image")) {
                        videoPreview.setVisibility(View.GONE);//视频预览的界面去掉
                        imgPreview.setVisibility(View.VISIBLE);
                        Glide.with(UploadFileActivity.this).load(selectList.get(0).getCompressPath()).into(imgPreview);
                        type= IMAGE;
                    }
                    else{
                        imgPreview.setVisibility(View.GONE);
                        videoPreview.setVisibility(View.VISIBLE);//显示视频预览
                        videoPreview.setVideoPath(path);//设置显示的视频
                        videoPreview.start();//开始播放
                        type= VIDEO;
                    }

                    break;
            }
        }
    }

    @Override
    public void uploadFinish() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UploadFileActivity.this, TAG + "uploadFinish" + "", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(2);
                finish();
            }
        });
    }

    @Override
    public void upLoadProgress(final int progress) {

    }

    @Override
    public void upLoadFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UploadFileActivity.this, TAG + "upLoadFailure", Toast.LENGTH_LONG).show();

            }
        });
    }
    @Override
    public void stopAnim() {


    }

    @Override
    public void startAnim() {

    }
}
