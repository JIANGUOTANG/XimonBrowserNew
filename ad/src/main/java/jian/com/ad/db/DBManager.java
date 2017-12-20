package jian.com.ad.db;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;

import jian.com.ad.App;

/**
 * Created by ying on 17-11-2.
 */

public class DBManager {

    private DaoSession daoSession ;
    private FileBeanDao fileBeanDao ;
    private Query<FileBean> fileBeanQuery ;

    public DBManager() {
        this.daoSession =App.getDaoSession();
        this.fileBeanDao = daoSession.getFileBeanDao();
        this.fileBeanQuery = fileBeanDao.queryBuilder().orderAsc(FileBeanDao.Properties.Id).build();
    }

    //插入数据
    public void insertAD(FileBean fileBean){
        fileBeanDao.insertOrReplace(fileBean);
    }
    //删除广告
    public void deleAD(FileBean fileBean){
        fileBeanDao.delete(fileBean);
    }
    public boolean showAdd(FileBean fileBean){
        Long id = fileBean.getId();
        if (fileBeanDao.queryBuilder().where(FileBeanDao.Properties.Id.eq(id)).build().unique()==null){
            return true;
        }else{
            return false;
        }
    }
    public ArrayList<FileBean> getAD(){

        ArrayList<FileBean> list = (ArrayList<FileBean>) fileBeanQuery.list();
        return list;
    }
    public void deleteAd(Long id){


       FileBean fileBean = fileBeanDao.queryBuilder().where(FileBeanDao.Properties.Id.eq(id)).build().unique();
        fileBeanDao.delete(fileBean);
    }
}
