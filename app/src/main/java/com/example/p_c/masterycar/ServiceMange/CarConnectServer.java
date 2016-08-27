package com.example.p_c.masterycar.ServiceMange;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.p_c.masterycar.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by p-c on 2016/8/9.
 */
public class CarConnectServer extends Service implements Runnable  {

    private BufferedReader mReader;
    public static final String ACTION_BACK_DISTANCE = "com.example.carSocket.BACK_DISTANCE";
    private PrintWriter mWriter;
    private ServerSocket ss=null;
    private Socket s;

    private Binder mBinder = new CarBinder();;
    private Thread td;//线程，获取服务器端发来的消息
    private String workStatus;//当前工作情况，null表示正在处理，success表示处理成功，failure表示处理失败
    private String currAction;//标记当前请求头信息，在获取服务器端反馈的数据后，进行验证，
    // 以免出现反馈信息和当前请求不一致问题。比如现在发送第二个请求，但服务器此时才响应第一个请求
    public static boolean isconnect = false;
    private final int SHOW_TIME_MIN = 2000;
    private LockCarInfo mLockCarInfo = new LockCarInfo();
    private final int ServerPort = 8888;

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
            isconnect=false;
            mWriter.close();
            mReader.close();
            s.close();
            ss.close();
            td.interrupt();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接服务器
     */
    private void connectService() {
        try {
            Log.e("QWERT","建立连接中");
            ss = new ServerSocket(ServerPort);
            s = ss.accept();
          //  InputStream in = s.getInputStream();
          //  DataInputStream din=new DataInputStream(in);
          //  OutputStream out = s.getOutputStream();
          //  DataOutputStream don=new DataOutputStream(out);

            mReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            mWriter = new PrintWriter(s.getOutputStream());

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
        if (s== null)// 如果未接收到客户端，创建连接
            connectService();
        if (!CarConnectServer.this.td.isAlive()) {
            closeConnection();// 如果当前线程没有处于存活状态，重启线程
            td = new Thread(CarConnectServer.this);
            td.start();
        }
        if (!s.isConnected() || s.isClosed())
        // isConnected（）返回的是是否曾经连接过，isClosed()返回是否处于关闭状态，只有当isConnected（）返回true，isClosed（）返回false的时候，网络处于连接状态
        {
            for (int i = 0; i < 3 && workStatus == null; i++) {// workStatus作用尚不清楚。如果连接处于关闭状态，重试三次，如果连接正常了，跳出循环
                s = null;
                connectService();
                if (s.isConnected() && (!s.isClosed())) {
                    break;
                }
            }
            if (!s.isConnected() || (s.isClosed()))// 如果此时连接还是不正常，提示错误，并跳出循环
            {
                workStatus = "connectfailure";
                return;
            }

        }

        if (!s.isOutputShutdown()) {// 输入输出流是否关闭
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

    private void toUpdateBackingDistance(String info){
        Intent intent = new Intent();
        intent.setAction(ACTION_BACK_DISTANCE);
        intent.putExtra(ACTION_BACK_DISTANCE,info);
        sendOrderedBroadcast(intent,null);
    }

    /**
     * 处理服务器端传来的消息，并通过头信息判断，传递给相应处理方法
     */
    private void getMessage(char[] str) {
        Log.e("QWERT","雷达距离："+str[0]+str[1]+str[2]+str[3]+str[4]+str[5]+str[6]+str[7]);
        if (str[0]=='a'&&str[1] == 'S') {
            //接收到倒车信号，发出通知，每个界面都负责接收，谁接收到就跳转到倒车影像界面，如果已经在倒车界面则直接实时显示倒车雷达距离
            String count = new String();
            String groupcount = String.valueOf(str);
            count = groupcount.substring(4,7);
            Log.e("QWERT","雷达距离："+count);
            if(Float.parseFloat(count)<0.5){
                toBackingWarning(count,str[2]);
            }
            toUpdateBackingDistance(count+str[2]);
        }
    }


    private void toBackingWarning(String text,char fangxiang){
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(
                getApplicationContext());

        builder.setSmallIcon(R.drawable.jingao);// 设置图标
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        // builder.setAutoCancel(true);
        builder.setContentTitle("警告");// 设置通知的标题
        builder.setContentText("距离"+fangxiang+"边 "+text+"米!");// 设置通知的内容
        //   builder.setTicker("点击可查看车附近情况");// 状态栏上显示
        builder.setOngoing(true);
     //   long[] vibrates = {0, 1000, 1000, 1000};
     //   builder.setVibrate(vibrates);
			/*
			 * // 设置声音(手机中的音频文件) String path =
			 * Environment.getExternalStorageDirectory() .getAbsolutePath() +
			 * "/Music/a.mp3"; File file = new File(path);
			 * builder.setSound(Uri.fromFile(file));
			 */

        // 获取Android多媒体库内的铃声
        //                builder.setVibrate(new long[]{2000,1000,4000}); //需要真机测试
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = Color.GREEN;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 1000;
        // notification.flags =Notification.FLAG_ONGOING_EVENT;

        nm.notify(4, notification);
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

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (td == null) {
            td = new Thread(CarConnectServer.this);// 启动线程
            td.start();
        }
        Log.e("QWERT","启动视频");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                Thread.sleep(500);
                Log.e("QWERT","正在run中");// 休眠0.5s
               if (s != null && !s.isClosed()) {
                  if (s.isConnected()) {
                        Log.e("QWERT","s连接成功");// 判断socket是否连接成功
                       if (!s.isInputShutdown()) {
                            Log.e("QWERT","雷达有数据");
                            char[] buffer = new char[20];
                            int content = mReader.read(buffer);
                           Log.e("QWERT",Integer.toString(content));
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
                s.close();
                ss.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            workStatus = "connectfailure";// 如果出现异常，提示网络连接出现问题。
            ex.printStackTrace();
        }
    }


}
