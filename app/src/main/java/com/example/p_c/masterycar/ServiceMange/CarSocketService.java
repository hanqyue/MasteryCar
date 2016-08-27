package com.example.p_c.masterycar.ServiceMange;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.p_c.masterycar.ActivityLife.ActivityLife;
import com.example.p_c.masterycar.CarStatus.CarStatusActivity;
import com.example.p_c.masterycar.CarStatus.FaultcodeActivity;
import com.example.p_c.masterycar.ConnectWebServer.ConnectSocket;
import com.example.p_c.masterycar.ConnectWebServer.FaultCodeInfo;
import com.example.p_c.masterycar.LockCar.LockcarActivity;
import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.PlayActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Created by p-c on 2016/6/27.
 */
public class CarSocketService extends Service implements Runnable {

    private Socket mSocket;
    private BufferedReader mReader;
    private PrintWriter mWriter;
    private Binder mBinder = new CarBinder();;
    private Thread td;//线程，获取服务器端发来的消息
    private String workStatus;//当前工作情况，null表示正在处理，success表示处理成功，failure表示处理失败
    private String currAction;//标记当前请求头信息，在获取服务器端反馈的数据后，进行验证，
    // 以免出现反馈信息和当前请求不一致问题。比如现在发送第二个请求，但服务器此时才响应第一个请求
   public static boolean isconnect = false;
    private final int SHOW_TIME_MIN = 2000;

    private final int PORT = 8899;//硬件端口号
    private StringRequest mStringRequest;
    private static CarStatusInfo mcarstatusinfo;
    private static final ConnectSocket sConnectSocket = new ConnectSocket();
    private RequestQueue mQueue;
    private  String fault;
    public static final String ACTION_UPDATE_LOCK_CAR = "com.example.carSocket.UPDATE_LOCK_CAR";
    public static final String ACTION_LOGIN_OTHER = "com.example.carSocket.ACTION_LOGIN_OTHER";
    public static final String ACTION_START_REVERSING = "com.example.carSocket.START_REVERSING";
    public static final String ACTION_STOP_REVERSING = "com.example.carSocket.STOP_REVERSING";
    public static final String ACTION_UPDATE_CAR_INFO = "com.example.carSocket.UPDATE_CAR_INFO";
    public static final String ACTION_UPDATE_LOCK_CAR_failure = "com.example.carSocket.UPDATE_LOCK_CAR_FAILURE";

    public static final String ACTION_CONNECT= "com.example.carSocket.CONNECT";
    public static final String ACTION_UPDATE_FAULT= "com.example.carSocket.ACTION_UPDATE_FAULT";
    public static final String ACTION_UPDATE_BACK_Timer = "com.example.carSocket.UPDATE_BACK_Timer";
    private final String openfailureinfo = "开车门失败";
    private final String closefailureinfo = "关车门失败";
    private LockCarInfo mLockCarInfo = new LockCarInfo();
    private CarStatusInfo mCarStatusInfo = new CarStatusInfo();
    private boolean speed = false;
    private boolean gasnum = false;
    private int i = 0;
    public static FaultCodeInfo minfo;

    public static final Integer Car_Propect_Notification_ID = 1010;
    private CarConnectServer.CarBinder mCarBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("QWE","ServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCarBinder = (CarConnectServer.CarBinder) service;
        }
    };
    private void connectToNatureService(){
        Intent intent = new Intent(CarSocketService.this, CarConnectServer.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 向服务器发送请求
     */

    public void sendRequest(String action) {
        try {
            workStatus = null;
            currAction = action;
            sendMessage(action);
        } catch (Exception e) {
            workStatus = "failure";
            e.printStackTrace();
        }

    }

    /**
     * 返回当前workStatus的值
     */
    public String getWorkStatus() {
        return workStatus;
    }

    /**
     * 记录服务器正在处理的数据
     */
    private void dealUploadSuperviseTask(String action) {
        try {
            //服务器消息result标记头信息
            workStatus = action;
        } catch (Exception e) {
            e.printStackTrace();
            workStatus = "failure";
        }
    }

    /**
     * 退出程序时，关闭socket连接
     */
    public void closeConnection() {

        try {
            //向硬件端口发送断开连接请求
            sendMessage("E");
            isconnect=false;
            toCONNRCTSuccessorFailure();
                mWriter.close();
                mReader.close();
                mSocket.close();
            td.interrupt();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接服务器
     */
    private void connectService() {
        long startTime = System.currentTimeMillis();

        try {

            mSocket = new Socket("192.168.1.101", PORT);
            Log.d("QWERT","msocket为："+mSocket.toString());
            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mWriter = new PrintWriter(mSocket.getOutputStream());

            isconnect = true;

        } catch (SocketException ex) {
            ex.printStackTrace(); Log.e("QWER","0进来了了");
            Log.e("QWERT",ex.toString());
            workStatus = "connectfailure";// 如果是网络连接出错了，则提示网络连接错误

        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();Log.e("QWER","1进来了了");
            Log.e("QWERT",ex.toString());
            workStatus = "connectfailure";// 如果是网络连接出错了，则提示网络连接错误

        } catch (Exception ex) {
            ex.printStackTrace();Log.e("QWER","2进来了了");
            Log.e("QWERT",ex.toString());
            workStatus = "connectfailure";// 如果是网络连接出错了，则提示网络连接错误

        } finally {
           long loadingTime = System.currentTimeMillis() - startTime;
            if (loadingTime < SHOW_TIME_MIN) {
                try {
                    Thread.sleep(SHOW_TIME_MIN - loadingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            toCONNRCTSuccessorFailure();
            return;
        }
    }

    /**
     * 向服务器发送传入的数据信息
     *
     * @param info
     */
    private void sendMessage(String info) {
      /*  if (!isNetworkConnected())// 如果当前网络连接不可用,直接提示网络连接不可用，并退出执行。
        {
            Log.v("QLQ", "workStatus is not connected!111");
            workStatus = "connectfailure";
            return;
        }*/
        if (mSocket == null)// 如果未连接到服务器，创建连接
            connectService();
        if (!CarSocketService.this.td.isAlive()) {
            closeConnection();// 如果当前线程没有处于存活状态，重启线程
            td = new Thread(CarSocketService.this);
            td.start();
        }
        if (!mSocket.isConnected() || mSocket.isClosed())
        // isConnected（）返回的是是否曾经连接过，isClosed()返回是否处于关闭状态，只有当isConnected（）返回true，isClosed（）返回false的时候，网络处于连接状态
        {
            for (int i = 0; i < 3 && workStatus == null; i++) {// workStatus作用尚不清楚。如果连接处于关闭状态，重试三次，如果连接正常了，跳出循环
                mSocket = null;
                connectService();
                if (mSocket.isConnected() && (!mSocket.isClosed())) {
                    break;
                }
            }
            if (!mSocket.isConnected() || (mSocket.isClosed()))// 如果此时连接还是不正常，提示错误，并跳出循环
            {
                workStatus = "connectfailure";
                return;
            }

        }

        if (!mSocket.isOutputShutdown()) {// 输入输出流是否关闭
            try {
                Log.e("QWERT","发出信息"+info);
                mWriter.println(info);
                mWriter.flush();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                workStatus = "failure";
            }
        } else {
            workStatus = "connectfailure";
        }
    }

    /**
     * 处理服务器端传来的消息，并通过头信息判断，传递给相应处理方法
     */
    private void getMessage(char[] str) {

        try {
            // 提取信息，获取当前JSON响应的是哪个操作。
            if (str[0]=='a'){
                //收到的是倒车时雷达信号

                if(str[1]=='h'&&str[2]=='t'){
                    //开始倒车
                    dealUploadSuperviseTask("倒车开始");
                    Log.d("QWERT","倒车开始");
                    toStartReversing();
                    //开启倒车接收服务
                    connectToNatureService();
                }else if(str[1]=='t'&&str[2]=='z'){
                    //发出通知，倒车结束
                    //关闭服务
                    dealUploadSuperviseTask("倒车结束");
                    Intent intent = new Intent(this, CarConnectServer.class);
                    stopService(intent);
                    toEndReversing();
                }
            }else if(str[0]=='b'){

                //开关车门，收到的是关开车门成功失败？，是否有人砸车？
                if(str[1]=='O'&& str[2]=='Y'){
                    // 开车门成功，改属性
                  mLockCarInfo.setIslock_car(false);
                    toUpdateLockcar();
                }else if(str[1]=='O'&& str[2]=='N'){
                    toLockcarOperationFailure(openfailureinfo);
                }else if(str[1]=='C'&& str[2]=='Y'){
                    // 关车门成功，改属性
                    mLockCarInfo.setIslock_car(true);
                    toUpdateLockcar();
                }else if(str[1]=='C'&& str[2]=='N'){
                    toLockcarOperationFailure(closefailureinfo);

                }
                if(str[1]=='W'&& LockcarActivity.isprotect){
                    //发出警示，有人盗车
                    carprotectNotification();
                }
                dealUploadSuperviseTask("开关车门和监控汽车处理");

            }else if(str[0]=='c'){
                //车载参数，传到相关activity，再上传服务器
                if(str.length!=19) {
                    dealUploadSuperviseTask("车载参数处理");
                    GroupCount(str);
                    CarStatusNotification();
                    if(mCarStatusInfo.getCarSpeed()!=0){
                        Log.d("QWERT","速度不为0");
                        if(ActivityLife.searchActivity("CarVideo")){
                            totiaozhuanvideo();
                        }

                    }else {
                        Log.d("QWERT","速度为0，应退出了");
                        if(!ActivityLife.searchActivity("CarVideo")){
                            ActivityLife.destoryActivity("CarVideo");
                        }

                    }
                    //给服务器车载参数
                    Log.d("QWERT","上传服务器");
                    String url = "http://123.206.55.200/" + "push.php";
                    mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("QWERT", "请求结果:" + response);
                                CarGsonToInfo(response,mCarStatusInfo);
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

                            map.put("carSpeed",Integer.toString(mCarStatusInfo.getCarSpeed()));
                            map.put("gasNum",Float.toString(mCarStatusInfo.getGasNum()));

                            return map;
                        }

                    };
                    //   mStringRequest.setRetryPolicy( new DefaultRetryPolicy( 500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                    //  DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                    //   DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
                    mQueue.add(mStringRequest);
               //   ConnectSocket.getConnectSocket().carinfo_volley_post(this,"push.php", mCarStatusInfo);

                }
            }else if(str[0]=='d'){
                if(str[1]=='Q'){
                    //给服务器故障码
                   fault = Integer.toString(++i);
                    if(i==1||i==2||i==3){
                       // String backinfo =  ConnectSocket.getConnectSocket().faultcode_volley_post(this,"check.php",fault);
                        String url = "http://123.206.55.200/" + "check.php";

                        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                Log.e("QWERT", "请求结果:" + convert(response));
                                FaultGsonToInfo(response);

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


                    }

                }
            }else  {
                dealUploadSuperviseTask("未知信息不处理");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            workStatus = "failure";
        }
        return;
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

    private void CarGsonToInfo(String backinfo, CarStatusInfo minfo){
        Gson gson = new Gson();
        CarStatusInfo carInfo = gson.fromJson(backinfo , CarStatusInfo.class);
        //发送一个警告
        if(carInfo.getCarSpeed()==1){
            if(!speed){
                speed = true;
                toSendWarning("速度达到",minfo.getCarSpeed());
            }
        }

        if(carInfo.getGasNum()==1){
            if(!gasnum){
                gasnum = true;
                toSendWarning("油量剩余",minfo.getGasNum());
            }

        }


    }
    private void FaultGsonToInfo(String backinfo){
        Gson gson = new Gson();
        FaultCodeInfo faultinfo = gson.fromJson(backinfo , FaultCodeInfo.class);
        CarStatusActivity.sList.add(faultinfo);
        minfo = faultinfo;
        //发送一个警告
        toUPDATEfaultcode();
        toFaultNotification();

    }
    private void toSendWarning(String text,float num){
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(
                getApplicationContext());


        builder.setSmallIcon(R.drawable.jingao);// 设置图标
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        // builder.setAutoCancel(true);
        builder.setContentTitle("警告");// 设置通知的标题
        builder.setContentText(text+" "+num);// 设置通知的内容
     //   builder.setTicker("点击可查看车附近情况");// 状态栏上显示
        builder.setOngoing(true);
        long[] vibrates = {0, 1000, 1000, 1000};
        builder.setVibrate(vibrates);
			/*
			 * // 设置声音(手机中的音频文件) String path =
			 * Environment.getExternalStorageDirectory() .getAbsolutePath() +
			 * "/Music/a.mp3"; File file = new File(path);
			 * builder.setSound(Uri.fromFile(file));
			 */

        // 获取Android多媒体库内的铃声


        //                builder.setVibrate(new long[]{2000,1000,4000}); //需要真机测试
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = Color.GREEN;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 1000;
        // notification.flags =Notification.FLAG_ONGOING_EVENT;
if(text.equals("速度达到")){

    nm.notify(1, notification);
}else{
    nm.notify(2, notification);
}
    }

    private void toFaultNotification(){
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(
                getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), FaultcodeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

     //   String guzhang = "";
       // for(int i = CarStatusActivity.sList.size();i>0;i--){
       //     guzhang+=CarStatusActivity.sList.get(--i).getFaultCode()+" ";
     //   }
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.jingao);// 设置图标
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        // builder.setAutoCancel(true);
        builder.setContentTitle("警告");// 设置通知的标题
        builder.setContentText("有故障码"+ minfo.getFaultCode()+"!");// 设置通知的内容
        builder.setTicker("点击查看详情");// 状态栏上显示
        builder.setOngoing(true);
        long[] vibrates = {0, 1000, 1000, 1000};
        builder.setVibrate(vibrates);
			/*
			 * // 设置声音(手机中的音频文件) String path =
			 * Environment.getExternalStorageDirectory() .getAbsolutePath() +
			 * "/Music/a.mp3"; File file = new File(path);
			 * builder.setSound(Uri.fromFile(file));
			 */

        // 获取Android多媒体库内的铃声


        //                builder.setVibrate(new long[]{2000,1000,4000}); //需要真机测试
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = Color.GREEN;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 1000;
        // notification.flags =Notification.FLAG_ONGOING_EVENT;
        nm.notify(3, notification);

    }

    private void  GroupCount(char[] str){
    Log.e("QWERT","数据为："+str[0]+str[1]+str[2]+str[3]+str[4]+str[5]+str[6]+str[7]+str[8]+str[9]+str[10]+str[11]+str[12]+str[13]+str[14]+str[15]+str[16]+str[17]+str[18]);
   //速度
    String count = new String();
    // c075157463361209600
    String groupcount = String.valueOf(str);
    count = groupcount.substring(1,4);
    int shudu = Integer.parseInt(count);
    if(shudu == 0){
        TimerBackNotification();
    }
    mCarStatusInfo.setCarSpeed(shudu);
    Log.d("QWERT",Integer.toString(shudu));
    //水温

    count = groupcount.substring(4,7);
        mCarStatusInfo.setWaterTemperature(Integer.parseInt(count));
   //油量
    count = groupcount.substring(7,12);
        mCarStatusInfo.setGasNum(((float) (Integer.parseInt(count)/100))/10);

    count = groupcount.substring(12,14);
        mCarStatusInfo.setBatteryVoltage(Integer.parseInt(count));


    count = groupcount.substring(14,19);
        mCarStatusInfo.setRevSpeed(((float) (Integer.parseInt(count)/100))/10);

}

    private void  CarStatusNotification(){
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_CAR_INFO);
        intent.putExtra(ACTION_UPDATE_CAR_INFO,mCarStatusInfo);
        sendBroadcast(intent);
    }

    private void TimerBackNotification(){
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_BACK_Timer);
        sendBroadcast(intent);
    }


    private void  totiaozhuanvideo(){
        Intent intent = new Intent();
        intent.setAction(ACTION_LOGIN_OTHER);
        sendBroadcast(intent);
        //  Intent intent = new Intent("com.jay.mybcreceiver.LOGIN_OTHER");
        // localBroadcastManager.sendBroadcast(intent);
    }
/**
 * update islockcarinfo
 */
  private void toUpdateLockcar(){
      Intent intent = new Intent();
      intent.setAction(ACTION_UPDATE_LOCK_CAR);
      intent.putExtra(ACTION_UPDATE_LOCK_CAR,mLockCarInfo.islock_car());
      sendOrderedBroadcast(intent,null);
  }

    private void toStartReversing(){
        Intent intent = new Intent();
        intent.setAction(ACTION_START_REVERSING);
        sendOrderedBroadcast(intent,null);
    }

    private void toEndReversing(){
        Intent intent = new Intent();
        intent.setAction(ACTION_STOP_REVERSING);
        sendOrderedBroadcast(intent,null);
    }
    /**
     * Lock car operation failure
     */
    private void toLockcarOperationFailure(String failureinfo){
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_LOCK_CAR_failure);
        intent.putExtra(ACTION_UPDATE_LOCK_CAR_failure,failureinfo);
        sendOrderedBroadcast(intent,null);
    }

    private void toCONNRCTSuccessorFailure(){
        Intent intent = new Intent();
        intent.setAction(ACTION_CONNECT);
        intent.putExtra(ACTION_CONNECT,isconnect);
        sendBroadcast(intent);
    }

    private void toUPDATEfaultcode(){
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_FAULT);
        sendBroadcast(intent);
    }


    private void carprotectNotification(){
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(
                getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
        intent.putExtra("data","zache");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.jingao);// 设置图标
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        // builder.setAutoCancel(true);
        builder.setContentTitle("警告");// 设置通知的标题
        builder.setContentText("有人砸车！");// 设置通知的内容
        builder.setTicker("点击可查看车附近情况");// 状态栏上显示
        builder.setOngoing(true);
        long[] vibrates = {0, 1000, 1000, 1000};
        builder.setVibrate(vibrates);
			/*
			 * // 设置声音(手机中的音频文件) String path =
			 * Environment.getExternalStorageDirectory() .getAbsolutePath() +
			 * "/Music/a.mp3"; File file = new File(path);
			 * builder.setSound(Uri.fromFile(file));
			 */

        // 获取Android多媒体库内的铃声


//                builder.setVibrate(new long[]{2000,1000,4000}); //需要真机测试
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = Color.GREEN;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 1000;
        // notification.flags =Notification.FLAG_ONGOING_EVENT;

        nm.notify(0, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }
    public class CarBinder extends Binder{

        public void senderInfo(String info){
            sendRequest(info);
        }
        public void closeConnection(){
            closeConnection();
        }
        public boolean islock_car(){
             return mLockCarInfo.islock_car();
        }

        public void notifyActivity(){
            toUpdateLockcar();
            //toUpdateCarInfo();
        }


    }

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = Volley.newRequestQueue(this);
        td = new Thread(CarSocketService.this);// 启动线程
        td.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll(this);
        closeConnection();
        Log.v("QLQ", "Service is on destroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("QLQ", "service on onUnbind");
        return super.onUnbind(intent);
    }

    /**
     * 循环，接收从服务器端传来的数据
     */
    public void run() {
        connectService();
        try {
            while (true) {
                Thread.sleep(500);// 休眠0.5s
                if (mSocket != null && !mSocket.isClosed()) {
                    if (mSocket.isConnected()) {
                        if (!mSocket.isInputShutdown()) {
                            char[] buffer = new char[20];
                            int content = mReader.read(buffer);
                             //   Log.e("QWERT","数据为"+content+"&"+buffer[0]+buffer[1]+buffer[2]+buffer[3]+buffer[4]);
                            getMessage(buffer);


                        }
                    }
                }
            }
        } catch (Exception ex) {

            try {
                mWriter.close();
                mReader.close();
                mSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            workStatus = "connectfailure";// 如果出现异常，提示网络连接出现问题。
            ex.printStackTrace();
        }
    }

}
