package jian.com.ximon.ad.ad;

import java.util.List;

import jian.com.ximon.ad.ad.bean.AD;

/**
 * Created by ying on 17-11-13.
 */

public class AdEvent {
    public final List<AD> adList;

    public AdEvent(List<AD> adList) {
        this.adList = adList;
    }
}
