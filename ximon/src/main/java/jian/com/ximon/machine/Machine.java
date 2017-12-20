
package jian.com.ximon.machine;


public class Machine {

    private String model;
    private int pk;
    private Fields fields;
    public void setModel(String model) {
        this.model = model;
    }
    public String getModel() {
        return model;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }
    public int getPk() {
        return pk;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }
    public Fields getFields() {
        return fields;
    }
}