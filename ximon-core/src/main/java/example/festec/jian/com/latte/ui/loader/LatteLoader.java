package example.festec.jian.com.latte.ui.loader;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import example.festec.jian.com.latte.R;
import example.festec.jian.com.latte.util.DimenUtil;

/**
 * Created by ying on 17-8-19.
 */

public class LatteLoader {
    private static final int LOADER_SIZE_SCALE = 8;

    private static final int LOADER_OFFSET_SCALE = 10;
     //管理所有的loader
    private static final ArrayList<AppCompatDialog> LOADERS = new ArrayList<>();

    //默认的样式

    private static final String DEFAULT_LOADER = LoaderStyle.BallClipRotatePulseIndicator.name();

    public static void showLoading(Context context, Enum<LoaderStyle> type){
        showLoading(context,type.name());

    }
    public static void showLoading(Context context, String type) {
        final AppCompatDialog dialog = new AppCompatDialog(context, R.style.dialog);

        final AVLoadingIndicatorView avLoadingIndicatorView = LoaderCreator.create(type, context);
        dialog.setContentView(avLoadingIndicatorView);
        //屏幕宽度
        int deviceWidth = DimenUtil.getScreenWidth();
        //高
        int deviceHeight = DimenUtil.getScreenHeight();
        //获得对话框窗口
        final Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            //获取对话框窗口的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            //设置宽度
            lp.width = deviceWidth / LOADER_SIZE_SCALE;
            //设置高度
            lp.height = deviceHeight / LOADER_SIZE_SCALE;
            //高度设置偏移量
            lp.height = lp.height + deviceHeight / LOADER_OFFSET_SCALE;

            //设置居中
            lp.gravity = Gravity.CENTER;
        }
        LOADERS.add(dialog);
        dialog.show();
    }

    /**
     *
     * @param context 需要传入当前context，因为在webView等中使用applicationContext会报错
     */
    public static void showLoading(Context context){
          showLoading(context,DEFAULT_LOADER);

    }
    //停止加载Loading动画
    public static void stopLoading(){
        for (AppCompatDialog dialog:LOADERS){
            if(dialog!=null){
                if(dialog.isShowing()){
                    try {
                        dialog.cancel();
                    }catch (Exception e){

                    }
                }
            }
        }
    }
}
