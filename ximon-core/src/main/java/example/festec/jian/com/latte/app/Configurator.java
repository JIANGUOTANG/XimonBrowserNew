package example.festec.jian.com.latte.app;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.joanzapata.iconify.IconFontDescriptor;
import com.joanzapata.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Interceptor;

/**
 * Created by ying on 17-8-18.
 */

public class Configurator {
    //使用weakHashMap会自动回收,但是是在键值对不在被系统应用的时候自行进行回收，
    // 但是在这里显然是不合理的。因为伴随着整个activity的生命周期。
    // 所以应该使用HashMap，这是一个地雷
    private static final HashMap<Object,Object> LATTE_CONFIGS = new HashMap<>();
    private static final Handler HANDLER = new Handler();
    private static final ArrayList<IconFontDescriptor> ICONS = new ArrayList<>();
    //拦截器
    private static final ArrayList<Interceptor> INTERCEPTORS = new ArrayList<>();

    private Configurator(){
       //配置开始了，但是还没有完成
        LATTE_CONFIGS.put(ConfigKeys.CONFIG_READY,false);
        LATTE_CONFIGS.put(ConfigKeys.HANDLER, HANDLER);
    }
    public static Configurator getInstance(){
        return Holder.INSTANCE;
    }
    final HashMap<Object,Object> getLatteConfigs(){
        return LATTE_CONFIGS;
    }

    /**
     * 使用静态内部类来初始化
     */
    private static class Holder{
        private static final  Configurator INSTANCE = new Configurator();
    }

    /**
     * 配置完成
     */
    public final void configure()

    {
        initIcons();
        LATTE_CONFIGS.put(ConfigKeys.CONFIG_READY,true);

    }


    public final Configurator withAiHost(String host){
        LATTE_CONFIGS.put(ConfigKeys.API_HOST,host);
        return this;

    }
    //添加自定义图标
    public final Configurator withIcon(IconFontDescriptor descriptor){

         ICONS.add(descriptor);
        return this;

    }
    //初始化字体图标库
    private void initIcons(){
        if (ICONS.size() > 0) {
            //初始化第一个
            final Iconify.IconifyInitializer initializer = Iconify.with(ICONS.get(0));
            for(int i = 0 ;i<ICONS.size();i++){
                initializer.with(ICONS.get(i));
            }


        }
    }

    public final Configurator withInterceptor(Interceptor interceptor){
        INTERCEPTORS.add(interceptor);

        LATTE_CONFIGS.put(ConfigKeys.INTERCEPTORS,INTERCEPTORS);
        return this;
    }

    public final Configurator withIWeChatApppId(String appId){

        LATTE_CONFIGS.put(ConfigKeys.WE_ChAT_App_ID,appId);
        return this;
    }
    public final Configurator withIWeChatApppSecret(String apppSecret){

        LATTE_CONFIGS.put(ConfigKeys.WE_ChAT_App_SECRET,apppSecret);
        return this;
    }

    public final Configurator withIActivity(Activity activity){

        LATTE_CONFIGS.put(ConfigKeys.ACTIVITY,activity);
        return this;
    }
    public final Configurator withInterceptors(ArrayList<Interceptor> interceptors){
        INTERCEPTORS.addAll(interceptors);

        LATTE_CONFIGS.put(ConfigKeys.INTERCEPTORS,INTERCEPTORS);
        return this;
    }

    /**
     * 检查是否完成配置
     */
    private void checkConfiguration(){
        final boolean isReady = (boolean) LATTE_CONFIGS.get(ConfigKeys.CONFIG_READY);

        if(!isReady){
        //如果配置未有完成，又急着去完成动作，那么抛出异常
            throw new RuntimeException("Configuration is not ready,call configure");

        }

    }
    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key) {
        checkConfiguration();
        final Object value = LATTE_CONFIGS.get(key);
        Log.d("jianTAG______",key.toString());
        if (value == null) {
            throw new NullPointerException(key.toString() + " IS NULL");
        }
        return (T) LATTE_CONFIGS.get(key);
    }
}
