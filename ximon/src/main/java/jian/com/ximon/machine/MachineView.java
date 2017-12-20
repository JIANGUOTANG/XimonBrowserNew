package jian.com.ximon.machine;

/**
 * Created by ying on 17-11-13.
 */

public interface MachineView {
    void startLoading();
    //删除失败
    void deleteSuccess();
    //删除成功
    void deleteFailure();

    void finishLoading(MachineEvent event);

}
