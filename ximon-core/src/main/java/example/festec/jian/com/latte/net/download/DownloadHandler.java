package example.festec.jian.com.latte.net.download;

import android.os.AsyncTask;

import java.util.WeakHashMap;

import example.festec.jian.com.latte.net.RestCreator;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.IRequest;
import example.festec.jian.com.latte.net.callback.ISuccess;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ying on 17-8-20.
 */

public class DownloadHandler {
    private final String URL; //请求的URL
    private static final WeakHashMap<String, Object> PARANS = RestCreator.getParams();//请求键值对
    private final IRequest REQUEST;//开始请求，借宿请求回调
    private final ISuccess SUCCES; //请求成功
    private final IFailure FAILURE;//请求失败
    private final IError ERROR;//发生错误
    private final RequestBody BODY;//请求体

    private final String DOWNLOAD_DIR;//下载目录
    private final String EXTENSION;//文件扩长名（后缀）
    private final String NAME;//完整的文件名称

    public DownloadHandler(String url,
                           IRequest request,
                           ISuccess success,
                           IFailure failure,
                           IError error,
                           RequestBody body,
                           String downloadDir,
                           String extention,
                           String name) {
        this.URL = url;
        this.REQUEST = request;
        this.SUCCES = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.BODY = body;
        this.DOWNLOAD_DIR = downloadDir;
        this.EXTENSION = extention;
        this.NAME = name;
    }

    public final void handlerDownload() {
        if (REQUEST != null) {
            REQUEST.onRequestStart();//开始下载
        }
        RestCreator.getRestService().downLoad(URL, PARANS)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            final ResponseBody responseBody = response.body();
                            final SaveFileTask task = new SaveFileTask(REQUEST, SUCCES);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    DOWNLOAD_DIR, EXTENSION, responseBody, NAME);
                            //这里一定要判断，否者下载不全
                            if (task.isCancelled()) {
                                //已经结束
                                if (REQUEST != null) {
                                    REQUEST.onRequestEnd();
                                }
                            }
                        } else {
                            if (ERROR != null) {
                                ERROR.onError(response.code(), response.message());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (FAILURE != null) {
                            FAILURE.onFailure();
                        }
                    }
                });
    }
}
