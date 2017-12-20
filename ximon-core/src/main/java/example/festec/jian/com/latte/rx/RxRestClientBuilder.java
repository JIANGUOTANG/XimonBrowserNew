package example.festec.jian.com.latte.rx;

import android.content.Context;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

import example.festec.jian.com.latte.net.RestCreator;
import example.festec.jian.com.latte.ui.loader.LoaderStyle;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by ying on 17-8-19.
 */

public class RxRestClientBuilder {

    private String mUrl = null; //请求的URL
    private static final Map<String, Object> PARAMS = RestCreator.getParams();//请求键值对

    private RequestBody mBody = null;//请求体
    private Context mContext = null;
    private LoaderStyle mLoaderStyle = null;
    private File mfile = null;

    RxRestClientBuilder() {

    }

    /**
     * 设置访问的URL
     *
     * @param url
     * @return
     */
    public final RxRestClientBuilder url(String url) {
        this.mUrl = url;
        return this;
    }


    public final RxRestClientBuilder params(WeakHashMap<String, Object> params) {
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
    public final RxRestClientBuilder params(String key, Object values) {
        this.PARAMS.put(key, values);
        return this;
    }

    /**
     * 通过文件上传文件
     */
    public final RxRestClientBuilder file(File file) {
        this.mfile = file;
        return this;
    }

    /**
     * 通过路径上传文件
     */
    public final RxRestClientBuilder file(String filePhth) {
        this.mfile = new File(filePhth);
        return this;
    }


    public final RxRestClientBuilder raw(String raw) {
        this.mBody = RequestBody.create(MediaType.parse("application/json;ccharset-utf-8"), raw);
        return this;
    }

    /**
     * 使用自定义的Style的加载动画
     *
     * @param context
     * @return
     */
    public final RxRestClientBuilder loader(Context context, LoaderStyle loaderStyle) {
        this.mContext = context;
        this.mLoaderStyle = loaderStyle;
        return this;
    }


    /**
     * 使用默认的Style的加载动画
     *
     * @param context
     * @return
     */
    public final RxRestClientBuilder loader(Context context) {
        this.mContext = context;
        this.mLoaderStyle = LoaderStyle.BallClipRotatePulseIndicator;
        return this;
    }

    public final RxRestClient builder() {
        return new RxRestClient(mUrl, PARAMS, mBody, mLoaderStyle, mContext, mfile);
    }
}
