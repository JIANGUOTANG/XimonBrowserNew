package example.festec.jian.com.latte.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import example.festec.jian.com.latte.net.RestClient;
import example.festec.jian.com.latte.net.callback.IError;
import example.festec.jian.com.latte.net.callback.IFailure;
import example.festec.jian.com.latte.net.callback.ISuccess;

/**
 * Created by ying on 17-8-22.
 */

public abstract class BaseWXEntryActivity extends BaseActivity {

    //用户登录成功的回调
    protected abstract void OnSignInSuccess(String response);

    //w微信发送请求到第三方应用的回调
    @Override
    public void onReq(BaseReq baseReq) {

    }

    //第三方应用发送请求到微信后的回调

    @Override
    public void onResp(BaseResp baseResp) {
        final String code = ((SendAuth.Resp)baseResp).code;
        final  StringBuilder authUrl = new StringBuilder();
        authUrl
                .append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
                .append(LatteWeChat.APP_ID)
                .append("&secret=")
                .append(LatteWeChat.APP_SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");


        getAuth(authUrl.toString());
    }


    private void getAuth(final String authUrl){
        RestClient.builder()
                .url(authUrl)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final JSONObject authObj = JSON.parseObject(response);
                        String accessToken = authObj.getString("access_token");
                        String openId = authObj.getString("openid");

                        final StringBuilder userInfoUrl = new StringBuilder();

                        userInfoUrl
                                .append("https://api.weixin.qq.com/sns/userinfo?access_token=")
                                .append(accessToken)
                                .append("&openid=")
                                .append(openId)
                                .append("&lang=")
                                .append("zh_CN");

                    }
                })
                .build()
                .get();
    }
//获取正真的用户信息
    private void getUserInfo(String userInfoUrl) {
        RestClient
                .builder()
                .url(userInfoUrl)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        OnSignInSuccess(response);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {

                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {

                    }
                })
                .build()
                .get();
    }
}
