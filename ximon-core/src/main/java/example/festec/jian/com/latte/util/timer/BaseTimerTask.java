package example.festec.jian.com.latte.util.timer;

import java.util.TimerTask;

/**
 * Created by ying on 17-8-20.
 */

public class BaseTimerTask extends TimerTask {
private ITimerListener iTimerListener;

    public BaseTimerTask(ITimerListener iTimerListener) {
        this.iTimerListener = iTimerListener;
    }

    @Override
    public void run() {
        if(iTimerListener!=null) {
            iTimerListener.onTimer();
        }
    }
}
