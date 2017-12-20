package example.festec.jian.com.latte.wechat;

import android.app.Activity;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import example.festec.jian.com.latte.app.ConfigKeys;
import example.festec.jian.com.latte.app.Latte;
import example.festec.jian.com.latte.wechat.callbacks.IWeChatSignInCallBack;

/**
 * Created by ying on 17-8-22.
 */

public class LatteWeChat {
    public static final String APP_ID = Latte.getConfiguration(ConfigKeys.WE_ChAT_App_ID);
    public static final String APP_SECRET = Latte.getConfiguration(ConfigKeys.WE_ChAT_App_SECRET);
    private final IWXAPI WXAPI;
    private IWeChatSignInCallBack mSignInCallback = null;

    private static final class Holder {
        private static final LatteWeChat INSTANCE = new LatteWeChat();
    }

    public static LatteWeChat getInstance() {
        return Holder.INSTANCE;
    }

    private LatteWeChat() {
        final Activity activity = Latte.getConfiguration(ConfigKeys.ACTIVITY);
        WXAPI = WXAPIFactory.createWXAPI(activity, APP_ID, true);
        WXAPI.registerApp(APP_ID);
    }

    public final IWXAPI getWXAPI() {
        return WXAPI;
    }

    public LatteWeChat onSignSuccess(IWeChatSignInCallBack callback) {
        this.mSignInCallback = callback;
        return this;
    }

    public IWeChatSignInCallBack getSignInCallback() {
        return mSignInCallback;
    }

    public final void signIn() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "random_state";
        WXAPI.sendReq(req);
    }

}
