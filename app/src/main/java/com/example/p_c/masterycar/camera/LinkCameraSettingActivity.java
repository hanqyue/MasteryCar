package com.example.p_c.masterycar.camera;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.example.p_c.masterycar.R;

/**
 *
 * @author Administrator
 * demo中只演示了部分功能，其他需要可参考sdk开发文档中的说明
 *
 */
public class LinkCameraSettingActivity extends Activity implements OnClickListener {
private String did, pwd;
private Button btn_open,btn_close,btn_getsensor;

@Override
protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.linkcamera_layout);
    Intent it=getIntent();
    did=it.getStringExtra(ContentCommon.STR_CAMERA_ID);
    pwd=it.getStringExtra(ContentCommon.STR_CAMERA_PWD);

    btn_open=(Button)findViewById(R.id.open_alarm);
    btn_close=(Button)findViewById(R.id.close_alarm);
    btn_getsensor=(Button)findViewById(R.id.get_sensor);
    btn_getsensor.setOnClickListener(this);
    btn_open.setOnClickListener(this);
    btn_close.setOnClickListener(this);

}

@Override
public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
    case R.id.open_alarm:
        vstc2.nativecaller.NativeCaller.TransferMessage(did,
                "set_sensorstatus.cgi?cmd=0&loginuse=admin&loginpas=" + pwd
                        + "&user=admin&pwd=" + pwd, 1);
        break;
    case R.id.close_alarm:
        vstc2.nativecaller.NativeCaller.TransferMessage(did,
                "set_sensorstatus.cgi?cmd=1&loginuse=admin&loginpas=" + pwd
                        + "&user=admin&pwd=" + pwd, 1);
        break;
    case R.id.get_sensor:
        vstc2.nativecaller.NativeCaller.TransferMessage(did,
                "get_sensorlist.cgi?loginuse=admin&loginpas=" + pwd
                + "&user=admin&pwd=" + pwd, 1);
        break;
    default:
        break;
    }
}


}
