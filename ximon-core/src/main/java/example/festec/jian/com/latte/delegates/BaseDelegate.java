package example.festec.jian.com.latte.delegates;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import example.festec.jian.com.latte.activityes.ProxyActivity;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

/**
 * Created by ying on 17-8-19.
 */

public abstract class BaseDelegate extends SwipeBackFragment {
    //ButterKinfe的一个类型
    private Unbinder mUnbinder = null;

    /**
     * 传入布局，可以是view
     * 也可一个Id，所以使用Object
     *
     * @return
     */
    public abstract Object setLayout();

    //强制子类实现
    public abstract void onBingView(@Nullable Bundle savedInstanceState, View rootView);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView;
        if (setLayout() instanceof Integer) {
            //传入的是一个LayoutId
            rootView = inflater.inflate((Integer) setLayout(), container, false);
        } else if (setLayout() instanceof View) {
            //传入的是一个View
            rootView = (View) setLayout();
        } else {
            throw new ClassCastException("setLayout() type must be int or View");

        }

        mUnbinder = ButterKnife.bind(this, rootView);
        onBingView(savedInstanceState, rootView);

        return rootView;
    }
    public final ProxyActivity getProxyActivity(){
        return (ProxyActivity) _mActivity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
