package com.example.p_c.masterycar.Gson;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.p_c.masterycar.R;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 李思言 on 2016/4/13.
 */
public class MainTestActivity extends AppCompatActivity{

    private Context context=MainTestActivity.this;
    private String  url="http://apis.juhe.cn/oil/local";
    private double lon;
    private double lat;
    private String MapPoi=null;
    private TextView gasname;
    private ListView gasmoney;
    private TextView gasadress;
    private Button   ordergas;
    private String   adress;
    private String   name;
    private String   E90=null;
    private String   E93=null;
    private String   E97=null;
    private LinkedList<gas_price> gasprice=null;
    private gasAdapter gasAdapter;
    private ProgressDialog progressDialog=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas);
        lat= (double) getIntent().getExtras().get("Lat_data");
        lon= (double) getIntent().getExtras().get("Lon_data");
        MapPoi= (String) getIntent().getExtras().get("PoiAdress");
        GetCar();
        init();

    }
    public void GetCar (){
        RequestQueue queue= Volley.newRequestQueue(context);
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson=new Gson();
                JavaBean javaBean=gson.fromJson(s,JavaBean.class);
                List<DataBean> dataBeans=javaBean.getResult().getData();
                if (MapPoi!=null) {
                    dismissDialog();
                    CharCompare compare = new CharCompare(MapPoi, dataBeans);
                    compare.ResultName();
                    javaBean.getResult().getData().get(compare.ResultPosition()).getPrice();
                    adress=javaBean.getResult().getData().get(compare.ResultPosition()).getAddress();
                    name=javaBean.getResult().getData().get(compare.ResultPosition()).getName();
                    E90=String.valueOf(javaBean.getResult().getData().get(compare.ResultPosition()).getPrice().getE90());
                    E93=String.valueOf(javaBean.getResult().getData().get(compare.ResultPosition()).getPrice().getE93());
                    E97=String.valueOf(javaBean.getResult().getData().get(compare.ResultPosition()).getPrice().getE97());
                    gasprice=new LinkedList<gas_price>();
                    gasprice.add(new gas_price("E90价格",E90,R.drawable.mode_driving_off));
                    gasprice.add(new gas_price("E93价格",E93,R.drawable.mode_driving_off));
                    gasprice.add(new gas_price("E97价格",E97,R.drawable.mode_driving_off));
                    initmessage(name,gasprice,adress);
                }else {
                    Toast.makeText(getApplicationContext(),"No Area",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Log.d("Tag","Error="+volleyError.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>map =new HashMap<String,String>();
                map.put("lon", String.valueOf(lon));
                map.put("lat", String.valueOf(lat));
                map.put("key","0be062ba82b1cba93eab73fd5f18bdc6");
                return map;
            }
        };

        queue.add(request);

    }
    public void init(){

        gasadress= (TextView) findViewById(R.id.gas_adress);
        gasname= (TextView) findViewById(R.id.gas_name);
        gasmoney= (ListView) findViewById(R.id.gas_money);
        progressDialog = new ProgressDialog(this);
        showDialog();

    }


    public void initmessage(String name,List<gas_price> price,String adress){
        gasadress.setText(adress);
        gasname.setText(name);
        gasAdapter=new gasAdapter((LinkedList<gas_price>) price,context);
        gasmoney.setAdapter(gasAdapter);
    }
    public void showDialog() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在获取地址");
        progressDialog.show();
    }
    public void dismissDialog() {
        if (progressDialog!= null) {
            progressDialog.dismiss();
        }
    }

}