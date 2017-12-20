package example.festec.jian.com.latte.activityes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ContentFrameLayout;

import example.festec.jian.com.latte.R;
import example.festec.jian.com.latte.delegates.LatteDelegate;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by ying on 17-8-19.
 * SupportActivity是framgtation的activity
 *
 */

public  abstract class ProxyActivity extends SupportActivity {

    public abstract LatteDelegate setRootDelegate();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContainer(savedInstanceState);
    }

    private void initContainer(@Nullable Bundle savedInstanceState){
        final ContentFrameLayout container = new ContentFrameLayout(this);
        container.setId(R.id.delegate_container);
        setContentView(container);
        //第一次加载

        if(savedInstanceState == null){
            loadRootFragment(R.id.delegate_container,setRootDelegate());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //垃圾回收
        System.gc();;
        System.runFinalization();
    }
}
