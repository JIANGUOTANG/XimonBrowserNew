package jian.com.ximon.machine;

/**
 * Created by ying on 17-11-15.
 */

public class MachineOnclickEvent {

    public final Machine machine;
    public final int position;
    public static final int LONG_CLICK =2;
    public static final int CLICK = 1;
    public final int currentClick;
    public MachineOnclickEvent(Machine machine, int position,int currentClick) {
        this.machine = machine;
        this.position = position;
        this.currentClick = currentClick;

    }
}
