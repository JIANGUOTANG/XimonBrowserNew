package example.festec.jian.com.latte.net.interceptors;

import android.support.annotation.RawRes;

import java.io.IOException;

import example.festec.jian.com.latte.util.file.FileUtil;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ying on 17-8-20.
 */

public class DebugInterceptor  extends BaseInterceptor
{
    private final String DEBUG_URL;//模拟的URL
    private final int DEBUT_RAW_ID;

    public DebugInterceptor(String DEBUG_URL, int DEBUT_RAW_ID) {
        this.DEBUG_URL = DEBUG_URL;
        this.DEBUT_RAW_ID = DEBUT_RAW_ID;
    }


    private Response getResponse(Chain chain,String json){
        return new Response.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body(ResponseBody.create(MediaType.parse("application/json"), json))
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .build();

    }
    /**
     * 获取raw的文件
     */
    private Response debugResponse(Chain chain, @RawRes int rawID){
        final String json = FileUtil.getRawFile(rawID);
        return getResponse(chain,json);
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        final String url = chain.request().url().toString();
        if(url.contains(DEBUG_URL))
        {
            return debugResponse(chain,DEBUT_RAW_ID);

        }
        return chain.proceed(chain.request());
    }
}
