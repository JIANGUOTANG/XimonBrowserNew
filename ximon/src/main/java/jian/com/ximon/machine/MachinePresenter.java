package jian.com.ximon.machine;

import android.content.Context;

/**
 * Created by ying on 17-11-13.
 */

public class MachinePresenter {
    private MachineView machineView;
    private MachineModel machineModel;

    public MachinePresenter(MachineView machineView, Context context) {
        this.machineView = machineView;
        this.machineModel = new MachineModel(context);
    }
    public void loadMachine(){
        machineView.startLoading();
        machineModel.loadMachine("/index/machine/");
    }
    public  void delete(int machine_id){
        machineModel.delete("/index/deleteMachine/",machine_id);

    }
}
