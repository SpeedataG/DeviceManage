package com.devicemanage.utils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class BDatas {
    @Id(autoincrement = true)
    private Long id;
    private String Tid;
    @Unique
    private String tagId;
    private String name;
    private int num;
    private String changdi;
    private String Storage;

    public BDatas(String tid, String tagId, String name, int num, String changdi, String storage) {
        Tid = tid;
        this.tagId = tagId;
        this.name = name;
        this.num = num;
        this.changdi = changdi;
        Storage = storage;
    }

    @Generated(hash = 191235541)
    public BDatas(Long id, String Tid, String tagId, String name, int num, String changdi,
            String Storage) {
        this.id = id;
        this.Tid = Tid;
        this.tagId = tagId;
        this.name = name;
        this.num = num;
        this.changdi = changdi;
        this.Storage = Storage;
    }

    @Generated(hash = 2020937871)
    public BDatas() {
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

    public String getTagId() {
        return this.tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
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
