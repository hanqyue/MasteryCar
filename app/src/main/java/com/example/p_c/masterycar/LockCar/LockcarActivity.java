package com.example.p_c.masterycar.LockCar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.ServiceMange.CarSocketService;
import com.example.p_c.masterycar.ServiceMange.CarSocketService.CarBinder;

public class LockcarActivity extends AppCompatActivity {
    private CarBinder mCarBinder;
    private LockcarReceiver mLockcarReceiver;
    private ImageView lock_car;
    private ImageView warn_car;
    private ImageView  btnBack;
    private TextView txtTitle;
    private SlideSwitch sSwitch;
    private final int SHOW_TIME_MIN = 5;
    public static boolean isprotect = true;//默认开启保护

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("QWE","ServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCarBinder = (CarBinder) service;
            if(mCarBinder.islock_car()) {
                lock_car.setImageResource(R.drawable.lock_off);
            }
            if(!isprotect){
               //防盗关闭
                sSwitch.setStatus(false);
            }
        }
    };

    private void connectToNatureService(){
        Intent intent = new Intent(LockcarActivity.this, CarSocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lockcar);
        connectToNatureService();
        init();
    }
    public void onResume(){
        super.onResume();
        registerReceiver();
    }
    public void onPause(){
        super.onPause();
        unregisterReceiver(mLockcarReceiver);
    }

    public void onDestroy(){
        super.onDestroy();
        if(mCarBinder != null){
            unbindService(serviceConnection);
        }
    }
    private void init(){
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtTitle.setText("锁车保护");
        txtTitle.setVisibility(View.VISIBLE);

        lock_car = (ImageView) findViewById(R.id.islock_car);
        lock_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( CarSocketService.isconnect){
                if(!mCarBinder.islock_car()){
                    //设置已锁
                  //  for(int i=0;i<10;i++){
                   // long startTime = System.currentTimeMillis();
                        mCarBinder.senderInfo("CCC");
                  //  long loadingTime = System.currentTimeMillis() - startTime;
                   // if (loadingTime < SHOW_TIME_MIN) {
                    //    try {
                       //     Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    //    } catch (InterruptedException e) {
                      //      e.printStackTrace();
                     //   }
                   // }
                //    }
                }else {

                        mCarBinder.senderInfo("OOO");
                }
               }else{
                    Toast.makeText(LockcarActivity.this,"请检查是否连接车载网络",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sSwitch = (SlideSwitch) this.findViewById(R.id.warn_car);
        sSwitch.setOnSwitchChangedListener(new SlideSwitch.OnSwitchChangedListener() {
            @Override
            public void onSwitchChanged(SlideSwitch obj, int status) {
                if(status == 0){
                    //关闭
                    Toast.makeText(LockcarActivity.this, "防盗已关闭", Toast.LENGTH_SHORT).show();
                    isprotect = false;
                }else {
                    Toast.makeText(LockcarActivity.this, "防盗已打开", Toast.LENGTH_SHORT).show();
                    isprotect = true;
                }
            }
        });

    }
    private void registerReceiver(){
        mLockcarReceiver = new LockcarReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CarSocketService.ACTION_UPDATE_LOCK_CAR);
        intentFilter.addAction(CarSocketService.ACTION_UPDATE_LOCK_CAR_failure);
        registerReceiver(mLockcarReceiver, intentFilter);
    }
    class LockcarReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(CarSocketService.ACTION_UPDATE_LOCK_CAR.equals(action)){
                Boolean islockcarinfo  = intent.getBooleanExtra(CarSocketService.ACTION_UPDATE_LOCK_CAR,false);
                if(!islockcarinfo){
                    Toast.makeText(LockcarActivity.this,"车门已解锁",Toast.LENGTH_SHORT).show();
                   lock_car.setImageResource(R.drawable.lock_on);
                }else {
                    Toast.makeText(LockcarActivity.this,"车门已上锁",Toast.LENGTH_SHORT).show();
                    lock_car.setImageResource(R.drawable.lock_off);//已锁车门
                }
            }else if(CarSocketService.ACTION_UPDATE_LOCK_CAR_failure.equals(action)){
                String failureinfo = intent.getStringExtra(CarSocketService.ACTION_UPDATE_LOCK_CAR_failure);
                Toast.makeText(LockcarActivity.this,failureinfo,Toast.LENGTH_SHORT).show();

            }
        }
    }
}
