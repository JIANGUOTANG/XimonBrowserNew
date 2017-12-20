package jian.com.ximon;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.danikula.videocache.HttpProxyCacheServer;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import example.festec.jian.com.latte.app.Latte;

import static jian.com.ximon.util.Constant.BASH_PATH;

/**
 * Created by ying on 17-8-18.
 */

public class ExampleApp extends MultiDexApplication {
    //视频缓存
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        ExampleApp app = (ExampleApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        Latte.init(this)
               .withIcon(new FontAwesomeModule())
                .withAiHost(BASH_PATH)
                .withIWeChatApppId("")
                .withInterceptor(new AddCookiesInterceptor(this,"ch"))
                .withInterceptor(new ReceivedCookiesInterceptor(this))
                .withIWeChatApppSecret("")
                .configure();
    }
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

}
