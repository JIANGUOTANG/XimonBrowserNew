package jian.com.ximon.ad.file;

/**
 * Created by ying on 17-11-8.
 */

public interface ProgressListener {
    //已完成的 总的文件长度 是否完成
    void onProgress(long currentBytes, long contentLength, boolean done);
}