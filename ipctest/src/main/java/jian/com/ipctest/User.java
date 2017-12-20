package jian.com.ipctest;

import java.io.Serializable;

/**
 * Created by ying on 17-11-6.
 */

public class User implements Serializable
{
    private int id;
    private String info;
    private boolean isLogin;

    public User(int id, String info, boolean isLogin) {
        this.id = id;
        this.info = info;
        this.isLogin = isLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", info='" + info + '\'' +
                ", isLogin=" + isLogin +
                '}';
    }
}
