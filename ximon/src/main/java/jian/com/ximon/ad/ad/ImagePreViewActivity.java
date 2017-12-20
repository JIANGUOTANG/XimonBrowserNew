package jian.com.ximon.ad.ad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import jian.com.ximon.R;

/**
 * 图片预览
 * Created by ying on 17-11-21.
 */

public class ImagePreViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        PhotoView photoView = findViewById(R.id.photoView);
        Intent intent = getIntent();
        //图片的路径
        String url  = intent.getStringExtra("url");
        //设置图片
        Glide.with(this).load(url).into(photoView);
        //点击返回按钮
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
