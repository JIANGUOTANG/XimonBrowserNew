package jian.com.ad.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ying on 17-11-1.
 */
@Entity
public class FileBean {

    /**
     * Copyright 2017 bejson.com
     */
    @Unique
    @Id
    private Long id;
    private String name;
    @Unique
    private String url;
    private String time;
    private int machine_id;
    @Generated(hash = 664008418)
    public FileBean(Long id, String name, String url, String time, int machine_id) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.time = time;
        this.machine_id = machine_id;
    }
    @Generated(hash = 1910776192)
    public FileBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return "/media/"+ url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getMachine_id() {
        return this.machine_id;
    }
    public void setMachine_id(int machine_id) {
        this.machine_id = machine_id;
    }
   


}
