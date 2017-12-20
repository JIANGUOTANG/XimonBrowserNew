package jian.com.ad.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danikula.videocache.HttpProxyCacheServer;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.jian.pus.R;

import static jian.com.ad.App.getProxy;

/**
 * Created by ying on 17-10-31.
 */

public class VideoFragment extends LazyFragment{

    private String url;
    private VideoView videoView;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    String proxyUrl;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frg_video,null,false);

        videoView = v.findViewById(R.id.videoView);
        Log.d("jian", "onCreateView");
        //图片地址

        url = getArguments().getString("url");
        HttpProxyCacheServer proxy = getProxy(getContext());
        proxyUrl = proxy.getProxyUrl(url);
        //For now we just picked an arbitrary item to play
        videoView.setVideoPath(proxyUrl);
        //初始化view的各控件
        videoView.start();
        isPrepared = true;
        lazyLoad();
        return v;
    }

    PagerListener pagerListener;

    public void setPagerListener(PagerListener pagerListener) {
        this.pagerListener = pagerListener;
    }


    //下一页回到接口
    public interface PagerListener {
        void nextPager();
    }

    public static VideoFragment newInstance(String url) {
        VideoFragment fragmentOne = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        //fragment保存参数，传入一个Bundle对象
        fragmentOne.setArguments(bundle);
        return fragmentOne;
    }
    /**
     * 当前该页面是否可见
     */
    private int i = 0;
    @Override
    protected void lazyLoad() {
        Log.i("VideoFragment",isPrepared+""+isVisible);
        if(!isPrepared || !isVisible) {
            return;
        }
        Log.i("VideoFragment","play");
        //播放视频
        //For now we just picked an arbitrary item to play
        videoView.setVideoPath(proxyUrl);
        //初始化view的各控件
        videoView.start();
//        if (!videoView.isPlaying()){
//            videoView.restart();
//        }

    }

    @Override
    protected void unVisible() {
        if(isPrepared) {
            videoView.reset();
        }
    }
}
