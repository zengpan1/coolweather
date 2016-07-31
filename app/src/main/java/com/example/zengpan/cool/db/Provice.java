package com.example.zengpan.cool.db;

/**
 * Created by Administrator on 2016/7/13.
 */
public class Provice {
    private int id = 0;
    private String prov_name;
    private String prov_code;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getProv_code() {
        return prov_code;
    }


    public void setProv_code(String prov_code) {
        this.prov_code = prov_code;
    }

    public String getProv_name() {
        return prov_name;
    }

    public void setProv_name(String prov_name) {
        this.prov_name = prov_name;
    }
}
