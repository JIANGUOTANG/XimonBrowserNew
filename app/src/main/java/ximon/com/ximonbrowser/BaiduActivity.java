package ximon.com.ximonbrowser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ximon.com.ximonbrowser.SonicRuntime.SonicJavaScriptInterface;
import ximon.com.ximonbrowser.SonicRuntime.SonicRuntimeImpl;
import ximon.com.ximonbrowser.SonicRuntime.SonicSessionClientImpl;

public class BaiduActivity extends AppCompatActivity {
    private Button btSearch;//查询按钮
    private EditText edtSearch;//搜索框
    private WebView webView;
    private ProgressBar progressBar;
    private AppCompatImageView imgHome;
    String url;
    public final static String PARAM_URL = "param_url";

    public final static String PARAM_MODE = "param_mode";

    private SonicSession sonicSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        //获取需要访问的url
        url = intent.getStringExtra("url");
        int mode = intent.getIntExtra(PARAM_MODE, -1);
        if (TextUtils.isEmpty(url) || -1 == mode) {
            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }
        SonicSessionClientImpl sonicSessionClient = null;
        // if it's sonic mode , startup sonic session at first time
        if (MainActivity.MODE_DEFAULT != mode) { // sonic mode
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();

            // if it's offline pkg mode, we need to intercept the session connection
            if (MainActivity.MODE_SONIC_WITH_OFFLINE_CACHE == mode) {
                sessionConfigBuilder.setCacheInterceptor(new SonicCacheInterceptor(null) {
                    @Override
                    public String getCacheData(SonicSession session) {
                        return null; // offline pkg does not need cache
                    }
                });

                sessionConfigBuilder.setConnectionIntercepter(new SonicSessionConnectionInterceptor() {
                    @Override
                    public SonicSessionConnection getConnection(SonicSession session, Intent intent) {
                        return new OfflinePkgSessionConnection(BaiduActivity.this, session, intent);
                    }
                });
            }

            // create sonic session and run sonic flow
            sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            } else {
                // this only happen when a same sonic session is already running,
                // u can comment following codes to feedback as a default mode.
                // throw new UnknownError("create session fail!");
                Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_LONG).show();
            }
        }
        setContentView(R.layout.activity_baidu);
        webView = (WebView) findViewById(R.id.wv_internet);
        // init webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
            }
            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
                return null;
            }
        });
        WebSettings webSettings = webView.getSettings();
        // step 4: bind javascript
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.setJavaScriptEnabled(true);
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");

        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        //启用数据库
        webSettings.setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setJavaScriptEnabled(true);//开启JavaScript支持
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setAppCacheEnabled(true);//设置H5的缓存打开,默认关闭
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//js和android交互
        webSettings.setDomStorageEnabled(true);//设置可以使用localStorage
        final String cachePath = getApplicationContext().getDir("cache",Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(cachePath);
        webSettings.setAppCacheMaxSize(5*1024*1024);
        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView);
            sonicSessionClient.clientReady();
        } else { // default mode
            webView.loadUrl(url);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //webview的简单设置
        initWebView();
        initView();
    }

    private void initView() {
        imgHome = (AppCompatImageView) findViewById(R.id.img_Home);
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView() {
//        webView.loadUrl(url);
//        WebSettings websettings = webView.getSettings();
//        websettings.setSupportZoom(true);
//        //启用数据库
//        websettings.setDatabaseEnabled(true);
//        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        websettings.setJavaScriptEnabled(true);//开启JavaScript支持
//        //启用地理定位
//        websettings.setGeolocationEnabled(true);
//        //设置定位的数据库路径
//        websettings.setGeolocationDatabasePath(dir);
//        websettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
//        websettings.setLoadWithOverviewMode(true);
//        websettings.setDomStorageEnabled(true);//开启缓存
//        websettings.setBuiltInZoomControls(true);
//        websettings.setUseWideViewPort(false);  //将图片调整到适合webview的大小
//        websettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//        websettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
//        webView.requestFocusFromTouch();
//
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                  //重写此方法，用于捕捉页面上的跳转链接
//                view.clearView();
//                view.loadUrl(url);
//                return true;
//            }
//        });
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);// 启用JS脚本
//        // 这里可以有很多设置
//        // webSettings.setSupportZoom(true); // 支持缩放
//        // webSettings.setBuiltInZoomControls(true); // 启用内置缩放装置

        webView.setWebChromeClient(new WebChromeClient() {
            // 当WebView进度改变时更新窗口进度
            //配置权限（同样在WebChromeClient中实现）
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                // 自己实现
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    //后退键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static class OfflinePkgSessionConnection extends SonicSessionConnection {

        private final WeakReference<Context> context;

        public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
            super(session, intent);
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected int internalConnect() {
            Context ctx = context.get();
            if (null != ctx) {
                try {
                    InputStream offlineHtmlInputStream = ctx.getAssets().open("sonic-demo-index.html");
                    responseStream = new BufferedInputStream(offlineHtmlInputStream);
                    return SonicConstants.ERROR_CODE_SUCCESS;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return SonicConstants.ERROR_CODE_UNKNOWN;
        }

        @Override
        protected BufferedInputStream internalGetResponseStream() {
            return responseStream;
        }

        @Override
        public void disconnect() {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getResponseCode() {
            return 200;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new HashMap<>(0);
        }

        @Override
        public String getResponseHeaderField(String key) {
            return "";
        }
    }

}
