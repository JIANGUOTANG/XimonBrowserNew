package jian.com.ad.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jian.pus.R;

/**
 * Created by ying on 17-10-31.
 */

public class ImageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_image,null,false);
        ImageView view = v.findViewById(R.id.imageAD);

        if (getArguments()!=null){
            //图片地址
            String url =  getArguments().getString("url");

            Glide.with(getActivity()).load(url)
                    .into(view);
        }
        return v;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //fragment可见时加载数据，用一个方法来实现

        } else {
            //不可见时不执行操作
        }
        super.setUserVisibleHint(isVisibleToUser);

    }
    public static  ImageFragment newInstance(String url){
        ImageFragment fragmentOne = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        //fragment保存参数，传入一个Bundle对象
        fragmentOne.setArguments(bundle);
        return fragmentOne;
    }
}
