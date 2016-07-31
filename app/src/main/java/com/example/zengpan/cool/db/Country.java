package com.example.zengpan.cool.db;

/**
 * Created by Administrator on 2016/7/13.
 */
public class Country {
    private int id;
    private String country_name;
    private String country_code;
    private int city_id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public String getCountry_name() {
        return country_name;
    }
}
