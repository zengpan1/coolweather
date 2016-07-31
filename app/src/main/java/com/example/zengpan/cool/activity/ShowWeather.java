package com.example.zengpan.cool.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zengpan.cool.R;
import com.example.zengpan.cool.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowWeather extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ShowWeather";
    /*显示发布时间*/
    private TextView time;
    /*显示城市*/
    private TextView city;
    /*显示天气*/
    private TextView weather;
    /*显示温度*/
    private TextView temp;

    /*重新选择城市*/
    private Button but_choose;
    /*刷新天气信息*/
    private Button but_refresh;

    private RelativeLayout layout1;
    private LinearLayout layout2;

    Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case HttpUtil.WEA_RESPONSE:
                    try
                    {
                        Log.i("Show Weather", "msg.obj = " + msg.obj);
                        JSONObject json = new JSONObject(msg.obj.toString());
                        JSONObject weatherInfo = json.getJSONObject("weatherinfo");
                        String city = weatherInfo.getString("city");
                        String cityid = weatherInfo.getString("cityid");
                        String temp1 = weatherInfo.getString("temp1");
                        String temp2 = weatherInfo.getString("temp2");
                        String weather = weatherInfo.getString("weather");
                        String ptime = weatherInfo.getString("ptime");
                        if(TextUtils.isEmpty(ptime)) {
                            Toast.makeText(ShowWeather.this, "所选城市没有预报天气", Toast.LENGTH_SHORT);
                            showWeather();
                            break;

                        }

                        saveWeatherInfo(city, cityid, temp1, temp2, weather, ptime);
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        showWeather();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_weather);
        time = (TextView)findViewById(R.id.time);
        city = (TextView)findViewById(R.id.city);
        weather = (TextView)findViewById(R.id.weather);
        temp = (TextView)findViewById(R.id.temp);
        but_choose = (Button)findViewById(R.id.choose);
        but_choose.setOnClickListener(this);
        but_refresh = (Button)findViewById(R.id.refresh);
        but_refresh.setOnClickListener(this);
        layout1 = (RelativeLayout)findViewById(R.id.lay_out_1);
        layout2 = (LinearLayout)findViewById(R.id.lay_out_2);

        String weathercode = getIntent().getStringExtra("weather");
        if(!TextUtils.isEmpty(weathercode))
        {
            time.setText("天气信息获取中。。。");
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            queryWeather(weathercode);
        }
        else
        {
            showWeather();
        }


    }

    private void queryWeather(String weather)
    {
        String addr = "http://www.weather.com.cn/data/cityinfo/"
                + weather + ".html";
        HttpUtil.sendhttpcmd(addr , "WEATHER", handle);

    }

    private void saveWeatherInfo(String city, String cityid, String temp1, String temp2,
                                 String weather, String ptime)
    {
        SharedPreferences.Editor data = getSharedPreferences("data", 0).edit();
        data.putBoolean("choosecity", true);
        data.putString("city", city);
        data.putString("cityid", cityid);
        data.putString("temp1", temp1);
        data.putString("temp2", temp2);
        data.putString("weather", weather);
        data.putString("ptime", ptime);
        data.commit();
    }

    private void showWeather()
    {
        Log.i(TAG, "showWeather begin");
        SharedPreferences data = getSharedPreferences("data", 0);
        if(!data.getBoolean("choosecity", false))
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        time.setText("今天由国家气象局于" + data.getString("ptime", "") + "发布");
        city.setText(data.getString("city", ""));
        weather.setText(data.getString("weather", ""));
        temp.setText(data.getString("temp1", "") + "~~" + data.getString("temp2", ""));
        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.VISIBLE);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.refresh:
                SharedPreferences data = getSharedPreferences("data", 0);
                String cityid = data.getString("cityid", "");
                queryWeather(cityid);
                break;
            case R.id.choose:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("come_from_showweather", true);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
