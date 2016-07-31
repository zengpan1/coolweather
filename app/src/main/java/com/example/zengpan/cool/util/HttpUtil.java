package com.example.zengpan.cool.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/7/14.
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    public static final int PRO_RESPONSE = 1;
    public static final int CIT_RESPONSE = 2;
    public static final int COU_RESPONSE = 3;
    public static final int WEA_RESPONSE = 4;
    public static void sendhttpcmd(final String addr, final String type, final Handler handler)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "the addr is " + addr);
                    URL url = new URL(addr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    Log.i(TAG, "5");
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(8000);
                    urlConnection.setReadTimeout(8000);
                    if(urlConnection.getResponseCode() == 200) {
                        InputStream in = urlConnection.getInputStream();
                        Log.i(TAG, "6");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder StrBuff = new StringBuilder();

                        Log.i(TAG, "7");
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            StrBuff.append(line);
//                            Log.i(TAG, "StrBuff = " + StrBuff);
//                        }
                        char data[] = new char[2048];
                        int len = reader.read(data);
                        StrBuff.append(String.valueOf(data, 0, len));
                        Log.i(TAG, "StrBuff = " + StrBuff);


                        Message msg = new Message();
                        switch (type) {
                            case "PROVICE":
                                msg.what = PRO_RESPONSE;
                                break;
                            case "CITY":
                                msg.what = CIT_RESPONSE;
                                break;
                            case "COUNTRY":
                                msg.what = COU_RESPONSE;
                                break;
                            case "WEATHER":
                                msg.what = WEA_RESPONSE;
                                break;
                            default:
                                break;
                        }
                        msg.obj = StrBuff;
                        handler.sendMessage(msg);
                        in.close();
                    } else
                    {
                        Log.e(TAG, "the response code is " + urlConnection.getResponseCode());
                    }
                    urlConnection.disconnect();
                }catch(Exception e)
                {
                    Log.e("HttpUtil", "sendhttpcmd is error for " + e.toString());
                }
            }
        }).start();

    }
}
