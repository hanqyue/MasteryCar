package com.example.p_c.masterycar.ActivityLife;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李思言 on 2016/7/23.
 */
public class ActivityLife extends Application {

    private static Map<String,Activity>destroyMap=new HashMap<>();

    private ActivityLife(){
   Log.d("QWERT","构造函数");
    }


    public static void addActivity(Activity activity,String activityName){
        Log.d("QWERT","添加函数");

        destroyMap.put(activityName,activity);

    }
    public static boolean searchActivity(String activityName){

        if (destroyMap.get(activityName)==null){
            return true;
        }else {
            return false;
        }
    }

    public static void destoryActivity(String activityName) {
        Log.d("QWERT","删除函数");
            destroyMap.get(activityName).finish();
        }


}
