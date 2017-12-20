package example.festec.jian.com.latte.wechat.template;

import example.festec.jian.com.latte.wechat.BaseWXEntryActivity;
import example.festec.jian.com.latte.wechat.LatteWeChat;

/**
 * Created by ying on 17-8-22.
 */


public class WXEntryTempLate extends BaseWXEntryActivity {

    @Override
    protected void onResume() {
        super.onResume();
        finish();
        overridePendingTransition(0,0);
    }

    @Override
    protected void OnSignInSuccess(String response) {
        LatteWeChat.getInstance().getSignInCallback().OnSignInSuccess(response);
    }
}
