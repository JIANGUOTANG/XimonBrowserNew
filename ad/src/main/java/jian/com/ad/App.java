package jian.com.ad;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import jian.com.ad.bean.Machine;
import jian.com.ad.bean.eventbus.EventCodeNumber;
import jian.com.ad.db.DaoMaster;
import jian.com.ad.db.DaoSession;
import jian.com.ad.db.SharePeferenceUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by ying on 17-10-25.
 */
public class App extends MultiDexApplication {
    private String TAG = "Application ";
    //视频缓存
    private HttpProxyCacheServer proxy;
    public static final boolean ENCRYPTED = true;
    private static DaoSession daoSession;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //先执行上面代码再执行初始化
        initDatabase();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行

        isFroground = false;
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取标识码，注册
        isFroground = true;
        thread.start();
    }

    private HttpProxyCacheServer newProxy() {

        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)// 1 Gb for cache
                .maxCacheFilesCount(20)

                .build();
    }


    private void initDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "users-db-encrypted" : "users-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {

        return daoSession;
    }


    //用于标记停止线程
    private boolean isFroground = true;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (SharePeferenceUtil.getCodeNumberId(App.this) == 0 && isFroground) {
                getCodeNumber();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JPushInterface.setDebugMode(true);
            //注册广告机到推送
            JPushInterface.setAlias(App.this, SharePeferenceUtil.getCodeNumberId(App.this) + "", null);// 设置开启日志,发布时请关闭日志
            JPushInterface.init(App.this);            // 初始化 JPush
        }
    });

    private void getCodeNumber() {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (SharePeferenceUtil.getCodeNumberId(this) == 0) {
            //当前的广告机没有注册，进行注册
            Request request = new Request.Builder()
                    .url(Constans.BASH_PATH + "/index/getCodeNumber/")
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String sp = response.body().string();
                    // data = [{'state': 1, 'id':codeNumber.id}]
                    if (response.code() == 200) {
                        //获取成功
                        //解析返回的数据
                        Gson gson = new Gson();
                        Log.d(TAG,sp);
                        Machine machine = gson.fromJson(sp, Machine.class);
                        //保存广告机信息到本地
                        SharePeferenceUtil.setCodeNumberId(App.this, machine.getId());
                        SharePeferenceUtil.setCodeNumber(App.this, machine.getCode_number());
                        EventBus.getDefault().post(new EventCodeNumber(machine.getCode_number()));
                    }
                }
            });
        }
    }
}