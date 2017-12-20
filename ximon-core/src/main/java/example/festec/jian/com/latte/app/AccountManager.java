package example.festec.jian.com.latte.app;

import example.festec.jian.com.latte.util.storage.LattePreference;

/**
 * Created by ying on 17-8-21.
 */

public class AccountManager {
    private enum SignTag{
        SIGN_TAG
    }
    //保存用户登录状态，登录后调用
    public static void setSignState(boolean state){
        LattePreference.setAppFlag(SignTag.SIGN_TAG.name(),state);

    }
    private static boolean isSignIn(){
        return LattePreference.getAppFlag(SignTag.SIGN_TAG.name());

    }
    public static void checkAccount(IUserChecker checker){
        //如果已经登录
        if(isSignIn()){
            checker.onSignIn();

        }
        else{
            //未登录
            checker.onUnSignIn();
        }
    }
}
