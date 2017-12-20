package jian.com.ximon.ad.file;

/**
 * Created by jian on 2017/4/3.
 * MVP模式中的View
 */

public interface UpLoadFileView {
    void uploadFinish();//上传完成
    void upLoadProgress(int progress);//上传进度
    void upLoadFailure();//失败
    void stopAnim();
    void startAnim();
}
