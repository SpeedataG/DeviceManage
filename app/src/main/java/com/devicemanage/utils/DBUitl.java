package com.devicemanage.utils;


import com.devicemanage.base.MyAplicatin;
import com.devicemanage.db.Datas;
import com.devicemanage.db.DatasDao;

import java.util.List;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author :孙天伟 in  2018/5/4   15:54.
 *         联系方式:QQ:420401567
 *         功能描述:
 */
public class DBUitl {
    public DBUitl() {
    }

    DatasDao mDao = MyAplicatin.getsInstance().getTDaoSession().getDatasDao();

    /**
     * 添加一条数据
     *
     * @param body
     */
    public void insertDtata(Datas body) {
        mDao.insertOrReplace(body);
    }

    public void insertDtatas(Datas body) {
        MyAplicatin.getsInstance().getTDaoSession().getDatasDao().insertOrReplace(body);
    }

    public Datas queryTAGID(String id) {
        Datas user = mDao.queryBuilder().where(DatasDao.Properties.TagId.eq(id)).build().unique();
        if (user != null) {
            return null;
        } else {
            return user;
        }
    }

    public void delete(String id) {
        Datas user = mDao.queryBuilder().where(DatasDao.Properties.TagId.eq(id)).build().unique();
        if (user != null) {
            mDao.deleteByKey(user.getId());
        }
    }

    public boolean queryID(String id) {
        Datas user = mDao.queryBuilder().where(DatasDao.Properties.TagId.eq(id)).build().unique();
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找所有数据
     *
     * @return
     */
    public List<Datas> queryAll() {
        List<Datas> kuaiShouDatas = mDao.loadAll();
        if (kuaiShouDatas != null && kuaiShouDatas.size() > 0)
            return kuaiShouDatas;
        return kuaiShouDatas;
    }

    /**
     * 根据体id修改数据
     */
    public void cahageData(String runNum) {
        Datas user = mDao.queryBuilder().where(
                DatasDao.Properties.TagId.eq(runNum)).build().unique();
        if (user != null) {
            user.setStorage("R库");
            mDao.update(user);
        }
    }

    /**
     * 根据体id修改数据
     */
    public void cahageID(String runNum, List<String> LpieceDatas, List<String> BpieceDatas) {
        Datas user = mDao.queryBuilder().where(
                DatasDao.Properties.TagId.eq(runNum)).build().unique();
        if (user != null) {
            if (LpieceDatas==null){
                LpieceDatas=null;
            }
            if (BpieceDatas==null){
                BpieceDatas = null;
            }
            user.setLpieceDatas(LpieceDatas);
            user.setBpieceDatas(BpieceDatas);
            mDao.update(user);
        }
    }

    public void deleAll() {
        mDao.deleteAll();
    }
}
