package com.example.p_c.masterycar.GMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.p_c.masterycar.CarVideo.CarVideo;

/**
 * Created by 李思言 on 2016/7/20.
 */
public class MyBcReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

                        intent = new Intent(context,CarVideo.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
        Log.d("POI","test");
    }
}