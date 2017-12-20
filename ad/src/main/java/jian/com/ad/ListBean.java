package jian.com.ad;

import android.widget.VideoView;

/**
 * Created by ying on 17-10-25.
 */

public class ListBean {
    private String bannerName;

    public String getBannerName() {
        return bannerName;
    }
    private VideoView videoView;

    public VideoView getVideoView() {
        return videoView;
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getUrlType() {
        return urlType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }

    private String bannerUrl;
    private int playTime;
    private int urlType;
}
