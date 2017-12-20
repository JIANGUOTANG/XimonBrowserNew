package example.festec.jian.com.latte.net.callback;

import android.os.Handler;

import example.festec.jian.com.latte.ui.loader.LatteLoader;
import example.festec.jian.com.latte.ui.loader.LoaderStyle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ying on 17-8-19.
 */

public class RequestCallbacks implements Callback<String> {
    private final IRequest REQUEST;//开始请求，借宿请求回调
    private final ISuccess SUCCES; //请求成功
    private final IFailure FAILURE;//请求失败
    private final IError ERROR;//发生错误
    private final LoaderStyle LOADER_STYLE;

    private static final Handler HANDLER = new Handler() ;

    public RequestCallbacks(IRequest request, ISuccess success, IFailure failure, IError error,LoaderStyle loaderStyle) {
        this.REQUEST = request;
        this.SUCCES = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.LOADER_STYLE = loaderStyle;
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if(LOADER_STYLE!=null) {
            HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LatteLoader.stopLoading();
                }
            },1000 );
        }
        if (response.isSuccessful()) {
            if (call.isExecuted()) {
                if (SUCCES != null) {
                    //请求成功
                    SUCCES.onSuccess(response.body());
                }
            }
        } else {
            if (ERROR != null) {
                //请求出错
                ERROR.onError(response.code(), response.message());
            }
        }

    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        if (FAILURE != null) {
            //请求失败
            FAILURE.onFailure();
        }
        if (REQUEST != null) {
          REQUEST.onRequestEnd();
        }

    }
}


