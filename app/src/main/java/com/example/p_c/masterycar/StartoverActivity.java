package com.example.p_c.masterycar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.example.p_c.masterycar.camera.BridgeService;


/**
 * Created by p-c on 2016/4/29.
 */
public class StartoverActivity extends AppCompatActivity {

    private final int SHOW_TIME_MIN = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.startpage);

        LoadingActivity();
    }


    void LoadingActivity(){
         new AsyncTask<Void, Void, Integer>() {

             @Override
             protected Integer doInBackground(Void... params) {
                 int result;
                 long startTime = System.currentTimeMillis();
                 result = loadingCache();
                 long loadingTime = System.currentTimeMillis() - startTime;
                 if (loadingTime < SHOW_TIME_MIN) {
                     try {
                         Thread.sleep(SHOW_TIME_MIN - loadingTime);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
                 return result;
             }

             @Override
             protected void onPostExecute(Integer result) {


                 Intent intent = new Intent(StartoverActivity.this,StartActivity.class);
                 startActivity(intent);
                 finish();
                 //两个参数分别表示进入的动画,退出的动画
                 overridePendingTransition(R.anim.fase_in, R.anim.fade_out);

             }
         }.execute(new Void[]{});
     }

    private int loadingCache() {
        //执行耗时操作
        try {
            Intent intent=new Intent(this,BridgeService.class);
            startService(intent);
            Log.d("tag","haha");
            vstc2.nativecaller.NativeCaller.PPPPInitial("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
            vstc2.nativecaller.NativeCaller.Init();
            new Thread(new StartPPPPThread()).start();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    private void startCameraPPPP() {

        int result = vstc2.nativecaller.NativeCaller.StartPPPP("VSTB505027DTEXX", "admin",
                "888888",1,"");

        Log.i("ip", "result:"+result);
        Log.d("tag","haha");
    }
    class StartPPPPThread implements Runnable {
        @Override
        public void run() {
            try {

                startCameraPPPP();
            } catch (Exception e) {

            }
        }
    }
}


