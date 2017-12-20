package ximon.com.ximonbrowser;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import ximon.com.ximonbrowser.SonicRuntime.SonicJavaScriptInterface;
import ximon.com.ximonbrowser.function.Function;
import ximon.com.ximonbrowser.function.FunctionAdapter;
import static android.R.attr.mode;
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFunction;
    private FunctionAdapter functionAdapter;
    private List<Function> functions = new ArrayList<>();
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_SONIC = 1;
    public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 1;
    private Button btSearch;//查询按钮
    private EditText edtSearch;//搜索框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        recyclerViewFunction = (RecyclerView) findViewById(R.id.recyclerViewFunction);
        initFunction();
        initView();
    }

    private void initView() {
        btSearch = (Button) findViewById(R.id.bt_search);
        edtSearch = (EditText) findViewById(R.id.et_search_view);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                int s = editable.toString().length();
                if (s > 0) {
                    //搜索按钮可以点击
                    btSearch.setClickable(true);

                } else {
                    //搜索按钮不可以点击
                    btSearch.setClickable(false);
                }
            }
        });
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = baiduBaseUrl + edtSearch.getText().toString();
                startBrowserActivity(url);
            }
        });
    }

    String baiduBaseUrl = "https://www.baidu.com/s?wd=";

    //通过映射来创建activity，方便修改功能
    private void initFunction() {
        Function function;
        function = new Function("百度", R.mipmap.baidu, "ximon.com.ximonbrowser.BaiduActivity", "http://www.baidu.com");
        functions.add(function);
//        //搜狗地图
//        function = new Function("地图", R.mipmap.map, "ximon.com.ximonbrowser.BaiduActivity", "http://map.sogou.com/m/webapp/m.html");
//        functions.add(function);
        //炒股
        function = new Function("炒股", R.mipmap.stock, "ximon.com.ximonbrowser.BaiduActivity", "http://m.10jqka.com.cn/");
        functions.add(function);
        function = new Function("航班查询", R.mipmap.flight, "ximon.com.ximonbrowser.BaiduActivity", "http://www.variflight.com/h5?AE71649A58c77=&token=74e5d4cac3179fc076af4f401fd4ebe3");
        functions.add(function);

        functionAdapter = new FunctionAdapter(functions);
        recyclerViewFunction.setAdapter(functionAdapter);
        recyclerViewFunction.setLayoutManager(new GridLayoutManager(this, 3));
        functionAdapter.setOnFunctionClick(new FunctionAdapter.onFunctionClick() {
            @Override
            public void onClick(Function function, int position) {
                startBrowserActivity(function, position, MODE_SONIC);

            }
        });
    }

    private void startBrowserActivity(Function function, int position, int mode) {
        String name = function.getClassName();
        try {
            Class c = Class.forName(name);
            Intent intent = new Intent(MainActivity.this, c);
            intent.putExtra("url", function.getUrl());
            intent.putExtra(BaiduActivity.PARAM_MODE, mode);
            intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
            startActivity(intent);
        } catch (ClassNotFoundException e) {

        }
    }

    private void startBrowserActivity(String url) {
        Intent intent = new Intent(MainActivity.this, BaiduActivity.class);
        intent.putExtra("url", url);
        intent.putExtra(BaiduActivity.PARAM_MODE, mode);
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        startActivity(intent);
    }
}
