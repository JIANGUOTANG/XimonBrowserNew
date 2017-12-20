package jian.com.ximon.ad.ad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;

import jian.com.ximon.R;

/**
 * 图片预览
 * Created by ying on 17-11-21.
 */

public class VideoPreViewActivity extends AppCompatActivity implements OnPreparedListener {
    private final String TAG = "VideoPreViewActivity";
    com.devbrackets.android.exomedia.ui.widget.VideoView videoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        videoView = findViewById(R.id.videoplayer);
        videoView.setOnPreparedListener(this);
        Intent intent = getIntent();
        //视频的路径
        String url = intent.getStringExtra("url");
        //For now we just picked an arbitrary item to play
        videoView.setVideoURI(Uri.parse(url));
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    public void onPrepared() {
        //Starts the video playback as soon as it is ready
        videoView.start();
    }
}
