package jian.com.ad.bean;

/**
 * Created by ying on 17-11-23.
 */

public class Machine {
    private int state;
    private int id;
    private String code_number;
    public String getCode_number() {
        return code_number;
    }
    public void setCode_number(String code_number) {
        this.code_number = code_number;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
