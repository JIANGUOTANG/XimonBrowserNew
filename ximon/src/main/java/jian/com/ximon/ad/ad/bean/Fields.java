/**
  * Copyright 2017 bejson.com 
  */
package jian.com.ximon.ad.ad.bean;
/**
 * Auto-generated: 2017-12-07 18:5:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Fields {

    private int machine;
    private String name;
    private String url;
    private int time;
    private int type;
    public void setMachine(int machine) {
         this.machine = machine;
     }
     public int getMachine() {
         return machine;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }
    public String getUrl() {
        return "/media/"+ url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setTime(int time) {
         this.time = time;
     }
     public int getTime() {
         return time;
     }
    public void setType(int type) {
         this.type = type;
     }
     public int getType() {
         return type;
     }

}