package com.example.p_c.masterycar.GMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.Util.AMapUtil;
import com.example.p_c.masterycar.Util.ToastUtil;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class  ItoAim extends Activity implements View.OnClickListener,GeocodeSearch.OnGeocodeSearchListener,
        Inputtips.InputtipsListener,TextWatcher{
    private AutoCompleteTextView myposition;
    private AutoCompleteTextView arrivepositon;
    private ImageView change;
    private TextView view;
    private String addressName;
    private double startLon;
    private double startlat;
    private double endlon;
    private double endlat;
    private String startname;
    private String arrivename;
    private String cityname;
    private static int a=0;
    private List list;
    private GeocodeSearch geocoderSearch;
    private ProgressDialog progDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ito_aim);
        cityname=getIntent().getExtras().get("city").toString();
        startname=getIntent().getExtras().get("name").toString();
        init();
    }
    public void init(){

        myposition= (AutoCompleteTextView) findViewById(R.id.myposition);
        myposition.addTextChangedListener(this);
        myposition.setText(startname);
        arrivepositon= (AutoCompleteTextView) findViewById(R.id.array_position);
        arrivepositon.addTextChangedListener(this);
        change= (ImageView) findViewById(R.id.change);
        change.setOnClickListener(this);
        view= (TextView) findViewById(R.id.route);
        view.setOnClickListener(this);
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
        list=new LinkedList<Double>();


    }
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change:
                startname= String.valueOf(myposition.getText());
                arrivename= String.valueOf(arrivepositon.getText());
                arrivepositon.setText(startname);
                myposition.setText(arrivename);
                break;
            case R.id.route:
                getLatlon(myposition.getText().toString());
                getLatlon(arrivepositon.getText().toString());
                break;

        }
    }

    public void getLatlon(String name) {
        showDialog();
        GeocodeQuery query = new GeocodeQuery(name,cityname);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";

                ToastUtil.show(ItoAim.this, addressName);
            } else {
                ToastUtil.show(ItoAim.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(ItoAim.this, rCode);
        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
               if (a==0){
                   Log.d("startname",address.getFormatAddress());
                   lwx(address.getLatLonPoint().getLatitude(),address.getLatLonPoint().getLongitude(),a);
               }else {
                   lwx(address.getLatLonPoint().getLatitude(),address.getLatLonPoint().getLongitude(),a);
               }

            } else {
                ToastUtil.show(ItoAim.this, R.string.no_result);
            }

        } else {
            ToastUtil.showerror(ItoAim.this, rCode);
        }

    }
    public void lwx(double x,double y,double z){

        if (z==0){
            startlat=x;
            startLon=y;
            list.add(startlat);
            list.add(startLon);
            a=1;
        }
        if (z==1){
            endlat=x;
            endlon=y;
            list.add(endlat);
            list.add(endlon);
               a=0;
               Bundle bundle=new Bundle();
                bundle.putDouble("MyLat", (Double) list.get(0));
                bundle.putDouble("MyLon", (Double) list.get(1));
                bundle.putDouble("Lat_data", (Double) list.get(2));
                bundle.putDouble("Lon_data", (Double) list.get(3));
            list.clear();
                Intent intentRoute=new Intent(ItoAim.this,RouteActivity.class);
                intentRoute.putExtras(bundle);
                startActivity(intentRoute);
        }

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            myposition.setAdapter(aAdapter);
            arrivepositon.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(ItoAim.this, rCode);
        }


    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
          String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText,cityname);
            Inputtips inputTips = new Inputtips(ItoAim.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }


    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
