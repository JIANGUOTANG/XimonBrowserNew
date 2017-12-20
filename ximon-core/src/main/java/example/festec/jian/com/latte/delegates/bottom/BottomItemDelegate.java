package example.festec.jian.com.latte.delegates.bottom;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import example.festec.jian.com.latte.R;
import example.festec.jian.com.latte.delegates.LatteDelegate;

/**
 * Created by ying on 17-8-23.
 */

public abstract class BottomItemDelegate extends LatteDelegate implements View.OnKeyListener{
    private long mExitTime = 0;
    private static final int EXIT_TIME = 2000;//两秒时间判断是否退出

    @Override
    public void onResume() {
        super.onResume();
        View rootView = getView();
        if (rootView != null) {
            //这样我们的KeyListener才生效
            rootView.setFocusableInTouchMode(true);
            rootView.requestFocus();
            rootView.setOnKeyListener(this);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
         //按下返回键
        if(keyCode == KeyEvent.KEYCODE_BACK&&event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-mExitTime)>EXIT_TIME){
                //超过时间了，退出失效、
                Toast.makeText(getContext(),"双击退出"+getString(R.string.app_name),Toast.LENGTH_LONG).show();
                //时间复位
                mExitTime = System.currentTimeMillis();


            }
            else{
                //快速点击退出Actviity
                _mActivity.finish();
                if(mExitTime!=0){
                    mExitTime = 0;
                }
            }
            return true;

        }
        return false;
    }
}
