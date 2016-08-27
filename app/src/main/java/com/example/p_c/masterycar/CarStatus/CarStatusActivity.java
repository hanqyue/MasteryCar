package com.example.p_c.masterycar.CarStatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.p_c.masterycar.ConnectWebServer.FaultCodeInfo;
import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.ServiceMange.CarSocketService;
import com.example.p_c.masterycar.ServiceMange.CarStatusInfo;

import java.util.ArrayList;
import java.util.List;

public class CarStatusActivity extends AppCompatActivity {

    private carinfoReceiver mCarinfoReceiver;
    private CarStatusInfo carStatus;
    private TextView faultCode;
    private TextView carSpeed;
    private TextView RevcarSpeed;
    private TextView waterTerm;
    private TextView gasNumber;
    private TextView batteryVoltage;
    private ImageView btnBack;
    private TextView txtTitle;
    public static List<FaultCodeInfo> sList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_status);
        initView();
    }

    private void initView(){
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtTitle.setText("车载参数");
        txtTitle.setVisibility(View.VISIBLE);

        faultCode = (TextView)findViewById(R.id.faultCode);
     /*   if(!sList.isEmpty()){
            faultCode.setText(Integer.toString(sList.size()));
        }else{
            faultCode.setText("0");
        }*/
        if(CarSocketService.minfo != null){
            faultCode.setText(CarSocketService.minfo.getFaultCode());
            faultCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转详情
                    Intent intent = new Intent(CarStatusActivity.this, FaultcodeActivity.class);
                    startActivity(intent);
                }
            });
        }
        carSpeed = (TextView)findViewById(R.id.carSpeed);
        RevcarSpeed = (TextView)findViewById(R.id.RevcarSpeed);
        waterTerm = (TextView)findViewById(R.id.waterTerm);
        gasNumber = (TextView)findViewById(R.id.gasNumber);
        batteryVoltage = (TextView)findViewById(R.id.batteryVoltage);
        /*if(!sList.isEmpty()) {
            faultCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转详情
                    Intent intent = new Intent(CarStatusActivity.this, FaultCodeListActivity.class);
                    startActivity(intent);
                }
            });
       } */
    }
private void updatefaultview(){
    faultCode.setText(CarSocketService.minfo.getFaultCode());
    faultCode.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //跳转详情
            Intent intent = new Intent(CarStatusActivity.this, FaultCodeListActivity.class);
            startActivity(intent);
        }
    });
}
    private void updateview(){
        carSpeed.setText(Float.toString(carStatus.getCarSpeed()));
        RevcarSpeed.setText(Float.toString(carStatus.getRevSpeed()));
        waterTerm.setText(Float.toString(carStatus.getWaterTemperature()));
        gasNumber.setText(Float.toString(carStatus.getGasNum()));
        batteryVoltage.setText(Float.toString(carStatus.getBatteryVoltage()));
    }

    public void onResume(){
        super.onResume();
        registerReceiver();
    }
    public void onPause(){
        super.onPause();
        unregisterReceiver(mCarinfoReceiver);
    }

    private void registerReceiver(){
        mCarinfoReceiver = new carinfoReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CarSocketService.ACTION_UPDATE_CAR_INFO);
        intentFilter.addAction(CarSocketService.ACTION_UPDATE_FAULT);
        registerReceiver(mCarinfoReceiver, intentFilter);
    }
    class carinfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
          if(CarSocketService.ACTION_UPDATE_CAR_INFO.equals(action)){
                carStatus = (CarStatusInfo)intent.getSerializableExtra(CarSocketService.ACTION_UPDATE_CAR_INFO);
                updateview();
            }else if(CarSocketService.ACTION_UPDATE_FAULT.equals(action)){
              updatefaultview();
          }
        }
    }
}
