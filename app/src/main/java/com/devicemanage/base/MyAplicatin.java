package com.devicemanage.base;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.devicemanage.db.DaoMaster;
import com.devicemanage.db.DaoSession;
import com.devicemanage.db.DbHelper;

import org.greenrobot.greendao.database.Database;


public class MyAplicatin extends Application {

    //greendao
    private DaoSession TdaoSession;
    private DaoSession LdaoSession;
    private DaoSession BdaoSession;
    private static MyAplicatin sInstance;

    //********
    @Override
    public void onCreate() {
        Log.d("tw", "程序创的时候执行");
        super.onCreate();
        sInstance = this;
        DbHelper helper = new DbHelper(this, "tuopan", null);
        Database db = helper.getWritableDb();
        TdaoSession = new DaoMaster(db).newSession();
        DbHelper Lhelper = new DbHelper(this, "lingjian", null);
        Database Ldb = Lhelper.getWritableDb();
        LdaoSession = new DaoMaster(Ldb).newSession();
        DbHelper Bhelper = new DbHelper(this, "baozhuang", null);
        Database Bdb = Bhelper.getWritableDb();
        BdaoSession = new DaoMaster(Bdb).newSession();
    }

    public static MyAplicatin getsInstance() {
        return sInstance;
    }

    public DaoSession getLDaoSession() {
        return LdaoSession;
    }

    public DaoSession getBDaoSession() {
        return BdaoSession;
    }

    public DaoSession getTDaoSession() {
        return TdaoSession;
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d("tw", "程序在内存清理的时候执行");
        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d("tw", "程序终止的时候执行");
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("tw", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d("tw", "低内存的时候执行");
        super.onLowMemory();
    }
}
