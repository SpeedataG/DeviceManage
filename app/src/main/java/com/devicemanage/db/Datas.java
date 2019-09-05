package com.devicemanage.db;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

@Entity
public class Datas {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String tagId;
    private String name;
    private int num;
    private String changdi;
    private String Storage;
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> LpieceDatas = null;//零件id数据
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> BpieceDatas = null;//包装id数据

    public Datas(String tagId, String name, int num, String changdi, String storage, List<String> lpieceDatas, List<String> bpieceDatas) {
        this.tagId = tagId;
        this.name = name;
        this.num = num;
        this.changdi = changdi;
        Storage = storage;
        LpieceDatas = lpieceDatas;
        BpieceDatas = bpieceDatas;
    }

    @Generated(hash = 1331338006)
    public Datas(Long id, String tagId, String name, int num, String changdi,
            String Storage, List<String> LpieceDatas, List<String> BpieceDatas) {
        this.id = id;
        this.tagId = tagId;
        this.name = name;
        this.num = num;
        this.changdi = changdi;
        this.Storage = Storage;
        this.LpieceDatas = LpieceDatas;
        this.BpieceDatas = BpieceDatas;
    }
    @Generated(hash = 1287820439)
    public Datas() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public List<String> getLpieceDatas() {
        return this.LpieceDatas;
    }
    public void setLpieceDatas(List<String> LpieceDatas) {
        this.LpieceDatas = LpieceDatas;
    }
    public List<String> getBpieceDatas() {
        return this.BpieceDatas;
    }
    public void setBpieceDatas(List<String> BpieceDatas) {
        this.BpieceDatas = BpieceDatas;
    }



}
