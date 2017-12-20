package example.festec.jian.com.latte.net;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.IRequest;
import example.festec.jian.com.latte.net.callback.ISuccess;
import example.festec.jian.com.latte.net.callback.RequestCallbacks;
import example.festec.jian.com.latte.net.download.DownloadHandler;
import example.festec.jian.com.latte.ui.loader.LatteLoader;
import example.festec.jian.com.latte.ui.loader.LoaderStyle;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by ying on 17-8-19.
 */
//请求的具体实现类
public class RestClient {
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
    private final LoaderStyle LOADER_STYLE;
    private final Context CONTEXT;

    private final File FILE;
    private final String TIME;
    private final int TYPE;


    public RestClient(String url,
                      Map<String,
                              Object> params,
                      IRequest request,
                      ISuccess success, IFailure failure,
                      IError error,
                      RequestBody body,
                      LoaderStyle loaderStyle,
                      Context context,
                      File file,
                      String downloadDir,
                      String extension,
                      String name,
                      String time,
                      int type
                      ) {
        this.URL = url;
        PARANS.putAll(params);
        this.REQUEST = request;
        this.SUCCES = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.BODY = body;
        this.LOADER_STYLE = loaderStyle;
        this.CONTEXT = context;
        this.FILE = file;
        this.DOWNLOAD_DIR = downloadDir;
        this.EXTENSION = extension;
        this.NAME = name;
        this.TIME = time;
        this.TYPE = type;
    }

    /**
     * 利用构造者模式来创建
     *
     * @return
     */
    public static RestClientBuilder builder() {
        return new RestClientBuilder();
    }

    private void request(HttpMethod method) {
        final RestService service = RestCreator.getRestService();
        Call<String> call = null;
        if (REQUEST != null) {
            REQUEST.onRequestStart();//开始请求
        }
        if (LOADER_STYLE != null) {
            //显示loading
            LatteLoader.showLoading(CONTEXT, LOADER_STYLE);
        }

        switch (method) {
            case GET:
                call = service.get(URL, PARANS);
                break;
            case POST:
                call = service.post(URL, PARANS);
                break;
            case POST_RAW:
                call = service.postRaw(URL, BODY);

                break;
            case PUT_RAW:
                call = service.putRaw(URL, BODY);
                break;
            case PUT:
                call = service.put(URL, PARANS);
                break;
            case DELETE:
                call = service.delete(URL, PARANS);
                break;
            case UPLOAD:
                //上传文件
                final RequestBody requestBody =
                        RequestBody.create(MediaType.parse(MultipartBody.FORM.toString()), FILE);
                Map<String, RequestBody> map = new HashMap<>();
                map.put("name", RequestBody.create(null, NAME));
                map.put("time", RequestBody.create(null, String.valueOf(TIME)));
                map.put("type", RequestBody.create(null, String.valueOf(TYPE)));
                final MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", FILE.getName(), requestBody);
                call = RestCreator.getRestService().upLoad(URL,map, body);
                break;
            default:
                break;

        }
        if (call != null) {
            call.enqueue(getQuestCallback());
        }
    }

    private Callback<String> getQuestCallback() {
        return new RequestCallbacks(
                REQUEST, SUCCES, FAILURE, ERROR, LOADER_STYLE
        );
    }

    public final void get() {
        request(HttpMethod.GET);
    }

    public final void put() {
        if (BODY == null) {
            request(HttpMethod.PUT);
        } else {
            if (!PARANS.isEmpty()) {
                //提交原始数据这里一定要是空
                throw new RuntimeException("params must be null");

            }
            request(HttpMethod.PUT_RAW);
        }
    }

    public final void post() {
        if (BODY == null) {
            request(HttpMethod.POST);
        } else {
            if (!PARANS.isEmpty()) {
                //提交原始数据这里一定要是空
                throw new RuntimeException("params must be null");

            }
            request(HttpMethod.POST_RAW);

        }

    }

    public final void delete() {
        request(HttpMethod.DELETE);
    }

    public final void upload() {
        request(HttpMethod.UPLOAD);
    }

    public final void download() {
        new DownloadHandler(URL, REQUEST, SUCCES, FAILURE, ERROR, BODY, DOWNLOAD_DIR, EXTENSION, NAME).handlerDownload();
    }
}
