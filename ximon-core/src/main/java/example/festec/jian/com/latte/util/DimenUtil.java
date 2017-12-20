package example.festec.jian.com.latte.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import example.festec.jian.com.latte.app.Latte;

/**
 * Created by ying on 17-8-19.
 */

public class DimenUtil {
    /**
     * 获得屏幕的宽
     * @return
     */
    public static int getScreenWidth(){
        final Resources resources = Latte.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获得屏幕的高
     * @return
     */
    public static int getScreenHeight(){
        final Resources resources = Latte.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
