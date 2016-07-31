package com.example.zengpan.cool.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
public class dbmanager {

    public static final String TAG = "dbmanager";

    public static final String CREATE_PROVICE = "create table Provice ("
            + "id integer primary key autoincrement, "
            + "provice_name text, "
            + "provice_code text)";

    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "provice_id integer)";

    public static final String CREATE_COUNTRY = "create table Country ("
            + "id integer primary key autoincrement, "
            + "country_name text, "
            + "country_code text, "
            + "city_id integer)";

    private static final String DB_NAME = "Cool_weather";
    private static final int VERSION = 1;

    private SQLiteDatabase mdb;
    private static dbmanager dbmanager;

    private dbmanager(Context context)
    {
        mdb = new Cooldbhelper(context, DB_NAME, null, VERSION).getWritableDatabase();
    }
    public static dbmanager getInstance(Context context)
    {
        if(null == dbmanager)
        {
            dbmanager = new dbmanager(context);
        }
        return dbmanager;
    }

    /*将省数据存储到数据库*/
    public boolean saveProvice(Provice province)
    {
        if(null == province)
        {
            Log.e(TAG, "saveProvice: the paramter is error!");
            return false;
        }

        try
        {
            ContentValues values = new ContentValues();
            values.put("provice_name",  province.getProv_name());
            values.put("provice_code",  province.getProv_code());
            mdb.insert("Provice", null, values);
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveProvice:exception!");
            return false;
        }
        return true;
    }

    /*将城市数据存储到数据库*/
    public boolean saveCity(City city)
    {
        if(null == city)
        {
            Log.e(TAG, "saveCity: the paramter is error!");
            return false;
        }

        try
        {
            ContentValues values = new ContentValues();
            values.put("city_name",  city.getCity_name());
            values.put("city_code",  city.getCity_code());
            values.put("provice_id",  city.getProvice_id());
            mdb.insert("City", null, values);
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveCity:exception!");
            return false;
        }
        return true;
    }

    /*将县数据存储到数据库*/
    public boolean saveCountry(Country country)
    {
        if(null == country)
        {
            Log.e(TAG, "saveCountry: the paramter is error!");
            return false;
        }

        try
        {
            ContentValues values = new ContentValues();
            values.put("country_name",  country.getCountry_name());
            values.put("country_code",  country.getCountry_code());
            values.put("city_id",  country.getCity_id());
            mdb.insert("Country", null, values);
        }
        catch (Exception e)
        {
            Log.e(TAG, "saveCountry:exception!");
            return false;
        }
        return true;
    }

    public List<Provice> queryProvice()
    {
        List<Provice> prolist = new ArrayList<Provice>();
        try {
            Cursor cursor = mdb.query("Provice", null, null, null, null, null, null);
            if(cursor.moveToFirst())
            {
                do {
                    Provice prov = new Provice();
                    prov.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    prov.setProv_code(cursor.getString(cursor.getColumnIndex("provice_code")));
                    prov.setProv_name(cursor.getString(cursor.getColumnIndex("provice_name")));
                    prolist.add(prov);
                }while(cursor.moveToNext());
            }

            if (cursor !=  null)
            {
                cursor.close();
            }

            return prolist;
        }
        catch (Exception e)
        {
            Log.e(TAG, "queryProvice:exception!");
            return null;
        }
    }

    public List<City> queryCity(int prov_id)
    {
        List<City> citylist = new ArrayList<City>();
        try {
            String arg[] = {String.valueOf(prov_id)};
            Cursor cursor = mdb.rawQuery("select * from City where provice_id = ?", arg );
            if(cursor.moveToFirst())
            {
                do {
                    City city = new City();
                    city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                    city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                    city.setProvice_id(prov_id);
                    citylist.add(city);
                }while(cursor.moveToNext());
            }

            if (cursor !=  null)
            {
                cursor.close();
            }

            return citylist;
        }
        catch (Exception e)
        {
            Log.e(TAG, "queryCity:exception!");
            return null;
        }
    }

    public List<Country> queryCountry(int city_id)
    {
        List<Country> countrylist = new ArrayList<Country>();
        try {
            String arg[] = {String.valueOf(city_id)};
            Cursor cursor = mdb.rawQuery("select * from Country where city_id = ?", arg );
            if(cursor.moveToFirst())
            {
                do {
                    Country country = new Country();
                    country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    country.setCountry_name(cursor.getString(cursor.getColumnIndex("country_name")));
                    country.setCountry_code(cursor.getString(cursor.getColumnIndex("country_code")));
                    country.setCity_id(city_id);
                    countrylist.add(country);
                }while(cursor.moveToNext());
            }

            if (cursor !=  null)
            {
                cursor.close();
            }

            return countrylist;
        }
        catch (Exception e)
        {
            Log.e(TAG, "queryCity:exception!");
            return null;
        }
    }


    private class Cooldbhelper extends SQLiteOpenHelper {

        public Cooldbhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name ,factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PROVICE);
            db.execSQL(CREATE_CITY);
            db.execSQL(CREATE_COUNTRY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
