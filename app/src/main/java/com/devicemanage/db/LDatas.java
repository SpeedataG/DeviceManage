package com.devicemanage.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LDatas {
    @Id(autoincrement = true)
    private Long id;
    private String Tid;
    @Unique
    private String Lid;
    private String name;
    private int num;
    private String changdi;
    private String Storage;

    public LDatas(String tid, String lid, String name, int num, String changdi, String storage) {
        Tid = tid;
        Lid = lid;
        this.name = name;
        this.num = num;
        this.changdi = changdi;
        Storage = storage;
    }

    @Generated(hash = 410738256)
    public LDatas(Long id, String Tid, String Lid, String name, int num, String changdi,
            String Storage) {
        this.id = id;
        this.Tid = Tid;
        this.Lid = Lid;
        this.name = name;
        this.num = num;
        this.changdi = changdi;
        this.Storage = Storage;
    }

    @Generated(hash = 1190027554)
    public LDatas() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTid() {
        return this.Tid;
    }

    public void setTid(String Tid) {
        this.Tid = Tid;
    }

    public String getLid() {
        return this.Lid;
    }

    public void setLid(String Lid) {
        this.Lid = Lid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getChangdi() {
        return this.changdi;
    }

    public void setChangdi(String changdi) {
        this.changdi = changdi;
    }

    public String getStorage() {
        return this.Storage;
    }

    public void setStorage(String Storage) {
        this.Storage = Storage;
    }
}
