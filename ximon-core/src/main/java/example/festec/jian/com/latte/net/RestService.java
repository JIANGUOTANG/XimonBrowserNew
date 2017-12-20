package example.festec.jian.com.latte.net;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by ying on 17-8-19.
 */

public interface RestService {
    @GET
    Call<String> get(@Url String url, @QueryMap Map<String, Object> params);

    @FormUrlEncoded
    @POST
    Call<String> post(@Url String url, @FieldMap Map<String, Object> params);

    //原始数据不能加FormUrlEncoded
    @POST
    Call<String> postRaw(@Url String url, @Body RequestBody body);

    //原始数据不能加FormUrlEncoded
    @PUT
    Call<String> putRaw(@Url String url, @Body RequestBody body);

    @FormUrlEncoded
    @PUT
    Call<String> put(@Url String url, @FieldMap Map<String, Object> params);

    @DELETE
    Call<String> delete(@Url String url, @QueryMap Map<String, Object> params);

    @Streaming//边下载，边在内存写入，防止内存溢出
    @GET
    Call<ResponseBody> downLoad(@Url String url, @QueryMap Map<String, Object> params);
    @Multipart
    @POST
    Call<String> upLoad(@Url String url, @PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file);

}
