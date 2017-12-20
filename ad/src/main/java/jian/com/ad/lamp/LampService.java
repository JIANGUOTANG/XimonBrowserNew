package jian.com.ad.lamp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.softwinner.Gpio;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 11833 on 2017/12/7.
 */

public class LampService extends Service{
    @Override
    public void onCreate() {

        super.onCreate();
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chargeLamp(LampEvent lampEvent){
        int state = Gpio.readGpio('E',7);//读取E7的电平
        Gpio.writeGpio('E',7,state == 0?1:0);//改变串口数据
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }
}
