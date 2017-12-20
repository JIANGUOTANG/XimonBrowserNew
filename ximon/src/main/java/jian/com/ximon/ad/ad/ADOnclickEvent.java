package jian.com.ximon.ad.ad;

import jian.com.ximon.ad.ad.bean.AD;

/**
 * Created by ying on 17-11-15.
 */

public class ADOnclickEvent {
    public final AD ad;
    public final int position;
    public static final int LONGCLICK = 2;
    public static final int CLICK = 1;
    public final int currentClick;

    public ADOnclickEvent(AD ad, int position, int currentClick) {
        this.ad = ad;
        this.position = position;
        this.currentClick = currentClick;
    }
}
  