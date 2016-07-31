package com.example.zengpan.cool.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zengpan.cool.R;
import com.example.zengpan.cool.db.City;
import com.example.zengpan.cool.db.Country;
import com.example.zengpan.cool.db.Provice;
import com.example.zengpan.cool.db.dbmanager;
import com.example.zengpan.cool.util.HttpUtil;


import java.util.ArrayList;
import java.util.List;

import static com.example.zengpan.cool.util.HttpUtil.sendhttpcmd;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int SELECT_PROVICE = 1;
    public static final int SELECT_CITY = 2;
    public static final int SELECT_COUNTRY = 3;
    private int currentSelect;
    private List<String> datalist = new ArrayList<String>();

    private List<Provice> provincelist;
    private List<City> citylist;
    private List<Country> countrylist;

    private Provice selectpro;
    private City selectcity;
    private Country selectcountry;

    private dbmanager mdbmanager;
    private ListView listview;
    private ArrayAdapter<String> adapter;
    private ProgressDialog pDialog;

    private Boolean isFromshow;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "6");
            String rep;
            switch (msg.what) {
                case HttpUtil.PRO_RESPONSE:
                    Log.i(TAG, "msg.obj = " + msg.obj);
                    rep = msg.obj.toString();
                    String[] prolist = rep.split(",");
                    Provice protmp = new Provice();
                    for (String pro : prolist) {
                        Log.i(TAG, "pro:" + pro);
                        String para[] = pro.split("\\|");
                        protmp.setProv_code(para[0]);
                        Log.i(TAG, "para0:" + para[0]);
                        protmp.setProv_name(para[1]);
                        Log.i(TAG, "para1:" + para[1]);
                        mdbmanager.saveProvice(protmp);
                    }
                    loadprovice();
                    break;
                case HttpUtil.CIT_RESPONSE:
                    Log.i(TAG, "msg.obj = " + msg.obj);
                    rep = msg.obj.toString();
                    String[] citylist = rep.split(",");
                    City citytmp = new City();
                    for (String city : citylist) {
                        Log.i(TAG, "city:" + city);
                        String para[] = city.split("\\|");
                        citytmp.setCity_code(para[0]);
                        Log.i(TAG, "para0:" + para[0]);
                        citytmp.setCity_name(para[1]);
                        Log.i(TAG, "para1:" + para[1]);
                        citytmp.setProvice_id(selectpro.getId());
                        mdbmanager.saveCity(citytmp);
                    }
                    loadCity();
                    break;
                case HttpUtil.COU_RESPONSE:
                    Log.i(TAG, "msg.obj = " + msg.obj);
                    rep = msg.obj.toString();
                    String[] countrylist = rep.split(",");
                    Country countmp = new Country();
                    for (String coun : countrylist) {
                        Log.i(TAG, "country:" + coun);
                        String para[] = coun.split("\\|");
                        countmp.setCountry_code(para[0]);
                        countmp.setCountry_name(para[1]);
                        countmp.setCity_id(selectcity.getId());
                        mdbmanager.saveCountry(countmp);
                    }
                    loadCountry();
                    break;
                case HttpUtil.WEA_RESPONSE:
                    Log.i(TAG, "msg.obj = " + msg.obj);
                    rep = msg.obj.toString();
                    String []arg = rep.split("\\|");
                    Intent intent = new Intent();
                    intent.putExtra("weather", arg[1]);
                    intent.setClass(MainActivity.this, ShowWeather.class);
                    startActivity(intent);
                    finish();
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        isFromshow = getIntent().getBooleanExtra("come_from_showweather", false);

        SharedPreferences data = getSharedPreferences("data", 0);
        if(data.getBoolean("choosecity", false ) && !isFromshow)
        {
            Intent itent = new Intent(this, ShowWeather.class);
            startActivity(itent);
            finish();
            return;
        }
        //showSOUSUO();
        mdbmanager = dbmanager.getInstance(this);
        listview = (ListView) findViewById(R.id.list_cool);
        Log.i(TAG, "1");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentSelect == SELECT_PROVICE)
                {
                    selectpro = provincelist.get(position);
                    loadCity();
                }
                else if(currentSelect == SELECT_CITY)
                {
                    selectcity = citylist.get(position);
                    loadCountry();
                }
                else if(currentSelect == SELECT_COUNTRY)
                {
                    selectcountry = countrylist.get(position);
                    sendhttpcmd("http://www.weather.com.cn/data/list3/city" + selectcountry.getCountry_code()
                            + ".xml", "WEATHER" ,handler);

                }
            }
        });
        loadprovice();
    }

    private void loadprovice()
    {
        Log.i(TAG, "2");
         provincelist = mdbmanager.queryProvice();
        if(provincelist.size() > 0)
        {
            closeprogress();
            datalist.clear();
            for (Provice pro : provincelist)
            {
                datalist.add(pro.getProv_name());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentSelect = SELECT_PROVICE;
            closeprogress();
        } else {
            showprogress();
            queryfromnet();
    }
    }

    private void loadCity()
    {
        Log.i(TAG, "8");
        if (selectpro == null)
        {
            Log.e(TAG, "selectpro is null");
        }

        citylist = mdbmanager.queryCity(selectpro.getId());
        if (citylist == null)
        {
            Log.e(TAG, "citylist is null");
        }

        if(citylist.size() > 0)
        {
            closeprogress();
            datalist.clear();
            for (City  city: citylist)
            {
                datalist.add(city.getCity_name());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentSelect = SELECT_CITY;
        } else {
            showprogress();
            queryfromnetforcity();
        }
    }

    private void loadCountry()
    {
        Log.i(TAG, "9");
        if (selectpro == null)
        {
            Log.e(TAG, "selectpro is null");
        }

        countrylist = mdbmanager.queryCountry(selectcity.getId());
        if (countrylist == null)
        {
            Log.e(TAG, "countrylist is null");
        }

        if(countrylist.size() > 0)
        {
            closeprogress();
            datalist.clear();
            for (Country  country: countrylist)
            {
                datalist.add(country.getCountry_name());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentSelect = SELECT_COUNTRY;
        } else {
            showprogress();
            queryfromnetforcountry();
        }
    }

    private void queryfromnet()
    {
        Log.i(TAG, "net provice");
        sendhttpcmd("http://www.weather.com.cn/data/list3/city.xml", "PROVICE", handler);
    }

    private void queryfromnetforcity()
    {
        Log.i(TAG, "net city");
        sendhttpcmd("http://www.weather.com.cn/data/list3/city" + selectpro.getProv_code()
                + ".xml", "CITY" ,handler);
    }

    private void queryfromnetforcountry()
    {
        Log.i(TAG, "net city");
        sendhttpcmd("http://www.weather.com.cn/data/list3/city" + selectcity.getCity_code()
                + ".xml", "COUNTRY", handler);
    }

    @Override
    public void onBackPressed() {
        switch (currentSelect)
        {
            case SELECT_CITY:
                loadprovice();
                return;
            case SELECT_COUNTRY:
                loadCity();
                return;
        }

        if(isFromshow)
        {
            Intent intent = new Intent(this, ShowWeather.class);
            startActivity(intent);
            finish();
        }
    }

    private void showprogress()
    {
        if(pDialog == null)
        {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
        }

        pDialog.show();
    }

    private void closeprogress()
    {
        if(pDialog != null)
        {
            pDialog.dismiss();
        }
    }
}
