package jian.com.ad.jpush.receiver;

/**
 * Created by 11833 on 2017/12/12.
 */

public class ReceiverBean {

    private int state ;
    private String info;//消息

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
