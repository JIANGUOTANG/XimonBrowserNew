package jian.com.ximon.ad.file;

import android.app.Activity;
import android.util.Log;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import java.io.File;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;

import static jian.com.ximon.machine.MachineActivity.TAG;
import static jian.com.ximon.util.Constant.BASH_PATH;

/**
 * Created by ying on 17-11-9.
 */

public class UploadFileModel {
    /**
     * 查询文件
     */
    private Activity activity;

    public UploadFileModel(Activity activity) {
        this.activity = activity;
    }

    public void loadFile() {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频 true or false
                .compress(true)
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .isGif(true)// 是否显示gif图片 true or false
                .openClickSound(true)// 是否开启点击声音 true or false
                // 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .videoMaxSecond(160)// 显示多少秒以内的视频or音频也可适用 int
                .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * @param path 上传文件的路径
     * @param id   广告机的id
     * @param time 播放的时间
     * @param name 广告名称
     */
    public void upLoadFile(String path, int id, String time, String name,int type) {
        File file = new File(path);
        //上传的路径
        String postUrl = BASH_PATH + "/index/upload_file/" + id;
        RestClient.builder()
                .file(file)
                .loader(activity)
                .url(postUrl)//上传路径

                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Log.i(TAG, "success");
                        upLoadListener.success();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Log.i(TAG, "failure");
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.i(TAG, "error");
                    }
                })
                //设置文件名称
                .name(name)
                .time(time)
                .type(type)
                .build()
                .upload();

    }

    private upLoadListener upLoadListener;

    /**
     * @param upLoadListener
     */
    public void setUpLoadListener(UploadFileModel.upLoadListener upLoadListener) {
        this.upLoadListener = upLoadListener;
    }

    //接口监听文件上传状态
    public interface upLoadListener {
        void success();//上传成功

        void failure();//失败

        void progress(int progress);//上传进度
    }
}
