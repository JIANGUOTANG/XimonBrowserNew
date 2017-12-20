package ximon.com.ximonbrowser;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ying on 17-10-9.
 */

public class SogouActivity extends AppCompatActivity {
    String url  = "http://map.sogou.com/m/webapp/m.html";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_baidu);
        //webview的简单设置
        webView=(WebView) findViewById(R.id.wv_internet);
        webView.loadUrl(url);
        WebSettings websettings=webView.getSettings();
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }
            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);// 启用JS脚本
        // 这里可以有很多设置
        // webSettings.setSupportZoom(true); // 支持缩放
        // webSettings.setBuiltInZoomControls(true); // 启用内置缩放装置

        webView.setWebChromeClient(new WebChromeClient() {
            // 当WebView进度改变时更新窗口进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                // 自己实现
                super.onProgressChanged(view, newProgress);
            }
        });

    }
    //后退键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
