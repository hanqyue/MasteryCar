package com.example.p_c.masterycar;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.p_c.masterycar.CarInfo.CarInfo;
import com.example.p_c.masterycar.CarInfo.CarInfoList;
import com.example.p_c.masterycar.CarInfo.CarListActivity;
import com.example.p_c.masterycar.CarStatus.CarStatusActivity;
import com.example.p_c.masterycar.CarVideo.MyVideo;
import com.example.p_c.masterycar.GMap.PoiSerchActivity;
import com.example.p_c.masterycar.LockCar.LockcarActivity;
import com.example.p_c.masterycar.Peccancy.PeccancyActivity;
import com.example.p_c.masterycar.ServiceMange.CarSocketService;
import com.example.p_c.masterycar.ViewVideo.ViewVideoActivity;
import com.example.p_c.masterycar.camera.PlayActivity;
import com.example.p_c.masterycar.offlinemap.OfflineMapActivity;

public class StartActivity extends AppCompatActivity {

    private SeismicWaveView seismicWaveView;
    private connectReceiver mConnectReceiver;
    private CarSocketService.CarBinder mCarBinder;
    private  Boolean isconnect = false;
    private  ImageButton lockcar;
    private  ImageButton weizhang;
    private  ImageButton drivingVideo;
    private  ImageButton carinfo;
    private  ImageButton daohang;
    private  ImageButton addgas;
    private  ImageView imagecar;
    private ImageView carsign;
    private ImageView xizai;
    private Button begin_addcar;
    private Button cartitle;
    private static final int BEGIN_SHOECAR = 0;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("QWE","ServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCarBinder = (CarSocketService.CarBinder) service;
            mCarBinder.senderInfo("dsd");
        }
    };
    private void connectToNatureService(){
        Intent intent = new Intent(StartActivity.this, CarSocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        seismicWaveView = (SeismicWaveView) findViewById(R.id.seismicwaveview);
        imagecar = (ImageView) findViewById(R.id.imageCar);
        begin_addcar = (Button) findViewById(R.id.begin_addcar);
        cartitle = (Button) findViewById(R.id.cartitle);
        carsign = (ImageView)findViewById(R.id.carsign);
        xizai = (ImageView)findViewById(R.id.bar_xizai);
        titlebarinit();
        setButtonOnLister();
    }

    private void titlebarinit(){
        xizai.setVisibility(View.VISIBLE);
        if(CarInfoList.getCarinfolist().isEmpty()) {
            begin_addcar.setVisibility(View.VISIBLE);
        }else {
            CarInfo mcarinfo = CarInfoList.getCarinfolist().getcarinfo(CarInfoList.getCarinfolist().getDefaultcarposition());
            cartitle.setText(mcarinfo.getCarBrand()+" "+mcarinfo.getCarModel());
            if(mcarinfo.getCarSign().equals("宝马")){
                carsign.setImageResource(R.drawable.baoma);
            }else if(mcarinfo.getCarSign().equals("奥迪")){
                carsign.setImageResource(R.drawable.aodi);
            }
            carsign.setVisibility(View.VISIBLE);
            cartitle.setVisibility(View.VISIBLE);
        }


    }
    //按钮监听
    private void setButtonOnLister(){
        xizai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, OfflineMapActivity.class);
                startActivity(intent);

            }
        });
        begin_addcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, CarListActivity.class);
                startActivityForResult(intent,BEGIN_SHOECAR);
            }
        });
        cartitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, CarListActivity.class);
                startActivityForResult(intent,BEGIN_SHOECAR);
            }
        });

        imagecar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!seismicWaveView.isStarting()) {
                    if(!isconnect) {
                        seismicWaveView.start();
                        connectToNatureService();
                    }else {
                        Toast.makeText(StartActivity.this,"已处于连接状态",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(StartActivity.this,"正在连接，请勿多次操作",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //导航
        daohang = (ImageButton) findViewById(R.id.DaoHang);
        daohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartActivity.this, PoiSerchActivity.class);
                startActivity(intent);

                Log.e("QWERT","跳转");

            }
        });

        //手动行车记录
        addgas = (ImageButton) findViewById(R.id.AddGas);
        addgas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(StartActivity.this, MyVideo.class);
               startActivity(intent);

            }
        });

        //行车记录仪
        drivingVideo = (ImageButton) findViewById(R.id.drivingVideo);
        drivingVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, ViewVideoActivity.class);
                //离线地图      OfflineMapActivity
                startActivity(intent);

            }
        });

        lockcar = (ImageButton) findViewById(R.id.lockcar);
        lockcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("QWERT","跳转");
                Intent intent = new Intent(StartActivity.this, LockcarActivity.class);
                startActivity(intent);
            }
        });

        carinfo = (ImageButton) findViewById(R.id.car_info);
        carinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("QWERT","跳转");
                Intent intent = new Intent(StartActivity.this, CarStatusActivity.class);
                startActivity(intent);
            }
        });

        weizhang = (ImageButton) findViewById(R.id.weizhang);
        weizhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("QWERT","跳转");
                Intent intent = new Intent(StartActivity.this, PeccancyActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onResume(){
        super.onResume();
        registerReceiver();
    }
    public void onPause(){
        super.onPause();
        unregisterReceiver(mConnectReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  if(mCarBinder!=null) {
      //      mCarBinder.closeConnection();
    //    }
        Intent intent = new Intent(this, CarSocketService.class);
        stopService(intent);
    }


    private void registerReceiver(){
        mConnectReceiver = new connectReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CarSocketService.ACTION_CONNECT);
        intentFilter.addAction(CarSocketService.ACTION_START_REVERSING);
        registerReceiver(mConnectReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode) {
            case BEGIN_SHOECAR:
                if(data == null){
                    return;
                }
                boolean isupdatecar = CarListActivity.wasAnswerShow(data);

                if (isupdatecar){
                   carsign.setVisibility(View.GONE);
                    begin_addcar.setVisibility(View.GONE);
                    cartitle.setVisibility(View.GONE);
                    titlebarinit();
                }
                break;
        }

    }

    class connectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("QWERT","没车辆数据");
            if(CarSocketService.ACTION_CONNECT.equals(action)){
                isconnect  = intent.getBooleanExtra(CarSocketService.ACTION_CONNECT,false);
                if(seismicWaveView.isStarting()) {
                    seismicWaveView.stop();
                    if (isconnect) {
                        Toast.makeText(StartActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StartActivity.this, "连接失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                }
            }else  if(CarSocketService.ACTION_START_REVERSING.equals(action)){
                Log.d("QWERT","接收到倒车广播");
                Intent beginrevering=new Intent(StartActivity.this,PlayActivity.class);
                beginrevering.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                beginrevering.putExtra("data","daoche");
                startActivity(beginrevering);
            }
        }
   }
}
