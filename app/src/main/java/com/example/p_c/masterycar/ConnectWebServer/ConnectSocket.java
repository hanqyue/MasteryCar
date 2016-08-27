package com.example.p_c.masterycar.ConnectWebServer;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.p_c.masterycar.ServiceMange.CarStatusInfo;

import java.util.HashMap;

/**
 * Created by p-c on 2016/7/28.
 */
public class ConnectSocket {

    private StringRequest mStringRequest;
    private static CarStatusInfo mcarstatusinfo;
    public static String backinfo = "";
    private static String carNum = "";
    private static String faultinfo;
    private static final ConnectSocket sConnectSocket = new ConnectSocket();
    private RequestQueue mQueue;

    public static ConnectSocket getConnectSocket() {
        return sConnectSocket;
    }

    // 利用Volley实现Post请求
    public void carinfo_volley_post(Context mcontent,String filename, final CarStatusInfo mCarststusinfo) {
        backinfo = "";
        String url = "http://123.206.55.200/" + filename;
        mQueue = Volley.newRequestQueue(mcontent);
        mcarstatusinfo = mCarststusinfo;
        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("QWERT", "请求结果:" + response);
                backinfo = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  backinfo = error.toString();
                Log.e("QWERT", "请求错误:" + error.toString());
            }
        }) {
            // 携带参数
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                HashMap map = new HashMap();

                map.put("carSpeed",Integer.toString(mCarststusinfo.getCarSpeed()));
                map.put("gasNum",Float.toString(mCarststusinfo.getGasNum()));

                return map;
            }

        };
     //   mStringRequest.setRetryPolicy( new DefaultRetryPolicy( 500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
              //  DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
             //   DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        mQueue.add(mStringRequest);

    }

    // 利用Volley实现Post请求
    public String faultcode_volley_post(Context mcontent,String filename, final String fault) {
        backinfo = "";
        faultinfo = fault;
        String url = "http://123.206.55.200/" + filename;
        mQueue = Volley.newRequestQueue(mcontent);
        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("QWERT", "请求结果:" + response);
                backinfo = convert(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   backinfo = error.toString();
                Log.e("QWERT", "请求错误:" + error.toString());
            }
        }) {
            // 携带参数
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                HashMap map = new HashMap();
                map.put("id",fault);

                return map;
            }

        };
        mQueue.add(mStringRequest);
        mStringRequest.setRetryPolicy( new DefaultRetryPolicy( 500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        return backinfo;

    }
    public String convert(String utfString){
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while((i=utfString.indexOf("\\u", pos)) != -1){
            sb.append(utfString.substring(pos, i));
            if(i+5 < utfString.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
            }
        }

        return sb.toString();
    }


}
