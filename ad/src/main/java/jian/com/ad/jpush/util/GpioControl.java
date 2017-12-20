package jian.com.ad.jpush.util;

import com.softwinner.Gpio;

/**
 * Created by 11833 on 2017/12/12.
 */

public class GpioControl {

    public static boolean lampControl() {
        int state = Gpio.readGpio('E',7);
        Gpio.writeGpio('E',7,state==0?1:0);
        return true;
    }

    public static boolean chargeControl() {
        int state = Gpio.readGpio('E',6);
        Gpio.writeGpio('E',6,state==0?1:0);
        return true;
    }
}
