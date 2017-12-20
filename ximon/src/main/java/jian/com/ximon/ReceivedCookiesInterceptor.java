package jian.com.ximon;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import example.festec.jian.com.latte.net.interceptors.BaseInterceptor;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Response;

/**
 * Created by ying on 17-11-15.
 */

public class ReceivedCookiesInterceptor extends BaseInterceptor{

    private Context context;
    SharedPreferences sharedPreferences;

    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            Log.d("http", "Receivedchain == null");
        Response originalResponse = chain.proceed(chain.request());
        Log.d("http", "originalResponse" + originalResponse.toString());
        if (!originalResponse.headers("set-cookie").isEmpty()) {
            final StringBuffer cookieBuffer = new StringBuffer();

            Observable.fromArray(originalResponse.headers("set-cookie"))
                    .map(new Function<List<String>, String>() {

                        @Override
                        public String apply(List<String> strings) throws Exception {
                            return strings.get(0);
                        }
                    })

                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String cookie) throws Exception {
                            Log.d("tag____tag",cookie);
                            cookieBuffer.append(cookie).append(";");
                        }
                    });
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cookie", cookieBuffer.toString());
            Log.d("http", "ReceivedCookiesInterceptor" + cookieBuffer.toString());
            editor.commit();
        }
        return originalResponse;
    }
}
