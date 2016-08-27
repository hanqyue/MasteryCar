package com.example.p_c.masterycar.CarVideo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.anderson.dashboardview.view.DashboardView;
import com.example.p_c.masterycar.ActivityLife.ActivityLife;
import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.ServiceMange.CarSocketService;
import com.example.p_c.masterycar.ServiceMange.CarStatusInfo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 李思言 on 2016/7/21.
 */
public class CarVideo extends AppCompatActivity implements SurfaceHolder.Callback {
    private android.hardware.Camera camera;

    private DashboardView speedView;
    private DashboardView revSpeedView;
    private DashboardView waterTempView;
    private carinfoReceiver mCarinfoReceiver;
    private Chronometer timer;
    private CarStatusInfo carStatus;
    private TextView gasNum;
    private TextView batteryVoltage;
    private boolean istimerbegin = false;
    private Button start;// 开始录制按钮
    private Button stop;// 停止录制按钮
    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceView surfaceview;// 显示视频的控件
    // 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看
    private SurfaceHolder surfaceHolder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_driving_status);
        ActivityLife.addActivity(this,"CarVideo");
        init();

    }
    private void init() {
        timer = (Chronometer)this.findViewById(R.id.chronometer);
        speedView = (DashboardView) findViewById(R.id.speedView);
        waterTempView = (DashboardView) findViewById(R.id.waterView);
        revSpeedView = (DashboardView) findViewById(R.id.turnSpeedView);
        gasNum = (TextView)findViewById(R.id.gasNum);
        batteryVoltage = (TextView)findViewById(R.id.batteryVoltage);

        speedView.setPercent(0);
        waterTempView.setPercent(0);
        revSpeedView.setPercent(0);

//        camera= android.hardware.Camera.open();
        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        SurfaceHolder holder = surfaceview.getHolder();// 取得holder
        holder.addCallback(this); // holder加入回调接口
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        File file=new File("/sdcard/行车记录仪/");
        if (!file.exists()){
            Log.d("QWERT","进来了没？");
            file.mkdir();
        }

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                VideoStart();
                Log.d("CarVideo","start");

            }
        };
        timer.schedule(task, 1000 * 3/2); //1.5秒后




    }

    public void VideoStart(){


        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        // 设置录制视频源为Camera(相机)


//        mediarecorder.setCamera(camera);
        mediarecorder.reset();
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder
                .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
//        mediarecorder.setVideoSize(176, 144);
//        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
//        mediarecorder.setVideoFrameRate(20);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediarecorder.setOrientationHint(90);
        // 设置视频文件输出的路径
        mediarecorder.setOutputFile("/sdcard/行车记录仪/"+getFileName()+".mp4");
        Log.d("VP",getFileName());
        try {
            // 准备录制

            mediarecorder.prepare();
//            camera.stopPreview();
//            camera.release();
//
//            camera=null;
//             开始录制
            mediarecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }


    class TestVideoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == start) {

                VideoStart();
            }
            if (v == stop) {
                if (mediarecorder != null) {
                    // 停止录制
                   if (camera!=null){
                       camera.stopPreview();
                       camera.release();
                       camera=null;
                   }
                    mediarecorder.stop();
                    // 释放资源
                    mediarecorder.release();
                    mediarecorder = null;

                }
            }

        }

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
        if (surfaceHolder.getSurface()==null){
            return;
        }
//        camera.stopPreview();
//        try {
//            camera.setPreviewDisplay(surfaceHolder);
//            camera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;

//        try {
//            camera.setPreviewDisplay(holder);
//            camera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceDestroyed的时候同时对象设置为null
        surfaceview = null;
        surfaceHolder = null;
        mediarecorder = null;
//        if (camera!=null){
//            camera.stopPreview();
//            camera.release();
//            camera=null;
//        }

    }

    public  String getCharacterAndNumber() {
        String rel="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }

    public String getFileName() {

        String fileNameRandom = getCharacterAndNumber();
        return fileNameRandom;
    }

    //硬件来信息，更新显示函数
    private void updateview(){
        gasNum.setText(carStatus.getGasNum()+" L");
        batteryVoltage.setText(carStatus.getBatteryVoltage()+" V");
        speedView.setPercent(carStatus.getCarSpeed()/2.7f);
       if(carStatus.getWaterTemperature()>=0){
            waterTempView.setPercent(carStatus.getWaterTemperature()/2.7f);
       }else {
           waterTempView.setPercent((carStatus.getWaterTemperature()+40)/2.7f);
       }
        revSpeedView.setPercent(carStatus.getRevSpeed()/0.18f);
    }

    public void onResume(){
        super.onResume();
        registerReceiver();
    }
    public void onPause(){
        super.onPause();
        timer.setBase(SystemClock.elapsedRealtime());
        unregisterReceiver(mCarinfoReceiver);
    }
   @Override
   protected void onDestroy() {
        super.onDestroy();
        if (mediarecorder != null) {
            // 停止录制
            if (camera!=null){
                camera.stopPreview();
                camera.release();
                camera=null;
            }
            mediarecorder.stop();
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;

        }
    }
    private void registerReceiver(){
        mCarinfoReceiver = new carinfoReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CarSocketService.ACTION_UPDATE_CAR_INFO);
        intentFilter.addAction(CarSocketService.ACTION_UPDATE_BACK_Timer);
        registerReceiver(mCarinfoReceiver, intentFilter);
    }
    class carinfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(CarSocketService.ACTION_UPDATE_BACK_Timer.equals(action)) {
                if (istimerbegin) {
                    timer.stop();
                    istimerbegin = false;
                }
            }else if(CarSocketService.ACTION_UPDATE_CAR_INFO.equals(action)){
                carStatus = (CarStatusInfo)intent.getSerializableExtra(CarSocketService.ACTION_UPDATE_CAR_INFO);
                //更新界面显示
                if(!istimerbegin) {
                    //开始计时
                    timer.start();
                    istimerbegin = true;
                }
                updateview();
            }
        }
    }
}

