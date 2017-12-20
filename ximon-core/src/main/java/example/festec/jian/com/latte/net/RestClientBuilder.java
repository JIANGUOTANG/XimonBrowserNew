package example.festec.jian.com.latte.net;

import android.content.Context;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.IRequest;
import example.festec.jian.com.latte.net.callback.ISuccess;
import example.festec.jian.com.latte.ui.loader.LoaderStyle;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by ying on 17-8-19.
 */

public class RestClientBuilder {

    private String mUrl = null; //请求的URL
    private static final Map<String, Object> PARAMS = RestCreator.getParams();//请求键值对
    private IRequest mIRequest = null;//开始请求，借宿请求回调
    private ISuccess mISuccess = null; //请求成功
    private IFailure mIFailure = null;//请求失败
    private IError mIError = null;//发生错误
    private RequestBody mBody = null;//请求体
    private Context mContext = null;
    private LoaderStyle mLoaderStyle = null;
    private File mfile = null;

    private  String mDownloadDir;//下载目录
    private  String mExtension;//文件扩长名（后缀）
    private  String mName;//完整的文件名称
    private  int type;//完整的文件名称
    private  String mTime;//完整的文件名称
    RestClientBuilder() {

    }

    /**
     * 设置访问的URL
     * @param url
     * @return
     */
    public final RestClientBuilder url(String url) {
        this.mUrl = url;
        return this;
    }


    public final RestClientBuilder params(WeakHashMap<String, Object> params) {
        PARAMS.putAll(params);
        return this;
    }

    /**
     * 传入的是原始数据
     *
     * @param key
     * @param values
     * @return
     */
    public final RestClientBuilder params(String key, Object values) {
        this.PARAMS.put(key, values);
        return this;
    }

    /**
     *  通过文件上传文件
     */
    public final RestClientBuilder file(File file) {
       this.mfile = file;
        return this;
    }

    public final RestClientBuilder time(String time) {
       this.mTime= time;
        return this;
    }

    /**
     *  通过路径上传文件
     */
    public final RestClientBuilder file(String  filePhth) {
        this.mfile = new File(filePhth);
        return this;
    }


    public final RestClientBuilder raw(String raw) {
        this.mBody = RequestBody.create(MediaType.parse("application/json;ccharset-utf-8"), raw);
        return this;
    }

    public final RestClientBuilder onRequest(IRequest iRequest) {
        this.mIRequest = iRequest;
        return this;
    }

    /**
     * 成功回调
     * @param iSuccess
     * @return
     */
    public final RestClientBuilder success(ISuccess iSuccess) {
        this.mISuccess = iSuccess;
        return this;
    }

    /**
     * 失败回调
     * @param iFailure
     * @return
     */
    public final RestClientBuilder failure(IFailure iFailure) {
        this.mIFailure = iFailure;
        return this;
    }
    /**
     * 错误回调
     * @param iError
     * @return
     */
    public final RestClientBuilder error(IError iError) {
        this.mIError = iError;
        return this;
    }
    /**
     * 使用自定义的Style的加载动画
     * @param context
     * @return
     */
    public final RestClientBuilder loader(Context context,LoaderStyle loaderStyle) {
       this.mContext = context;
        this.mLoaderStyle = loaderStyle;
        return this;
    }

    /**
     * 下载的文件名
     * @param name
     * @return
     */
    public final RestClientBuilder name(String name) {
        this.mName = name;
        return this;
    }
    public final RestClientBuilder type(int type) {
        this.type = type;
        return this;
    }
    /**
     * 设置下载路径
     * @param dir
     * @return
     */
    public final RestClientBuilder dir(String dir) {
        this.mDownloadDir = dir;
        return this;
    }

    /**
     * 文件扩展名
     * @param extension
     * @return
     */
    public final RestClientBuilder extension(String extension) {
        this.mExtension = extension;
        return this;
    }

    /**
     * 使用默认的Style的加载动画
     * @param context
     * @return
     */
    public final RestClientBuilder loader(Context context) {
        this.mContext = context;
        this.mLoaderStyle = LoaderStyle.BallClipRotatePulseIndicator;
        return this;
    }

    public  final  RestClient build(){
        return new RestClient(mUrl,PARAMS,mIRequest,mISuccess,mIFailure,mIError,mBody, mLoaderStyle, mContext,mfile,mDownloadDir,mExtension,mName,mTime,type);
    }
}
