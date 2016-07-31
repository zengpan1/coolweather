package com.example.zengpan.cool.db;

/**
 * Created by Administrator on 2016/7/13.
 */
public class City {
    private int id;
    private String city_name;
    private String city_code;
    private int provice_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvice_id() {
        return provice_id;
    }

    public void setProvice_id(int provice_id) {
        this.provice_id = provice_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }
}
