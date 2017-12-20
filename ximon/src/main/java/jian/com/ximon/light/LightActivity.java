package jian.com.ximon.light;

import android.os.Bundle;
import android.support.annotation.Nullable;

import jian.com.ximon.BaseActivity;
import jian.com.ximon.R;

/**
 * Created by 11833 on 2017/12/18.
 */

public class LightActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        initMapButton();
    }
}
