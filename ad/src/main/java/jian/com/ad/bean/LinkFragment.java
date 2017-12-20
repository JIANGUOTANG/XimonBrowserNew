package jian.com.ad.bean;

import android.support.v4.app.Fragment;

/**
 * Created by ying on 17-11-24.
 */

public class LinkFragment {
    public Long id;
    public Fragment fragment;

    public LinkFragment(Long id, Fragment fragment) {
        this.id = id;
        this.fragment = fragment;
    }
}
