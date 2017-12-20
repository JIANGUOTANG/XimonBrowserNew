package jian.com.ad.view.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import jian.com.ad.bean.LinkFragment;

/**
 * Created by ying on 17-10-31.
 */

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private List<LinkFragment> fragments;

    public MyPagerAdapter(FragmentManager fm, List<LinkFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public void setFragments(List<LinkFragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position).fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
