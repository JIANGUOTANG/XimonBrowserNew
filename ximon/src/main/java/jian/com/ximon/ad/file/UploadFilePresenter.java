package jian.com.ximon.ad.file;

import android.app.Activity;

/**
 * Created by ying on 17-11-9.
 */

public class UploadFilePresenter implements UploadFileModel.upLoadListener {
    private UploadFileModel uploadFileModle;
    private UpLoadFileView upLoadFileView;
    private final int SEARCH_FINISHED = 2;
    private final int UPDATE_LIST = 1;
    private Activity activity;

    /**
     *
     * @param upLoadFileView view，用于更新activity中的信息，mvp中的view
     * @param activity
     */
    public UploadFilePresenter(UpLoadFileView upLoadFileView, Activity activity) {
        this.uploadFileModle = new UploadFileModel(activity);
        this.upLoadFileView = upLoadFileView;
        uploadFileModle.setUpLoadListener(this);
        this.activity = activity;
    }

    /**
     * 查询文件
     */
    public void loadFile() {
        uploadFileModle.loadFile();
    }

    /**
     * 文件上传
     * @param path 上传的文件地址，路径
     * @param id 当前的广告机的id
     */
    public void upLoadFile(String path,int id,String time,String name,int type) {
        upLoadFileView.startAnim();
        uploadFileModle.upLoadFile(path,id,time,name,type);
        upLoadFileView.stopAnim();
    }

    @Override
    public void success() {
        //上传成功
        upLoadFileView.uploadFinish();
    }

    @Override
    public void failure() {
        //上传失败,回调失败方法
        upLoadFileView.upLoadFailure();

    }

    @Override
    public void progress(int progress) {
        //进度
        upLoadFileView.upLoadProgress(progress);
    }
}
