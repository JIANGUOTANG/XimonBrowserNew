package jian.com.ximon.machine;

import java.util.List;

/**
 * Created by ying on 17-11-13.
 */

public class MachineEvent {
    public final List<Machine> machines;

    public MachineEvent(List<Machine> machines) {
        this.machines = machines;
    }
}
