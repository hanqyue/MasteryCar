package com.example.p_c.masterycar.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.bean.utils.SystemValue;


/**
 * 
 * @author
 * 设备系统设置
 */
public class SettingActivity extends Activity implements OnClickListener
{

	private String strDID;
	private String cameraName;
	private String cameraPwd;
	//控件声明
	private RelativeLayout wifi_Relat,pwd_Relat,alarm_Relat,time_Relat,sd_Relat,tf_Relat,update,sensor;
	private Button back_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		getDataFromOther();
		initView();		
	}

	//获取activity传过来的数据
	private void getDataFromOther()
	{
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		cameraPwd=intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
	}
	//初始化控件
	private void initView()
	{
		wifi_Relat=(RelativeLayout) findViewById(R.id.wifi_setting);
		pwd_Relat=(RelativeLayout) findViewById(R.id.pwd_setting);
		alarm_Relat=(RelativeLayout) findViewById(R.id.alarm_setting);
		time_Relat=(RelativeLayout) findViewById(R.id.time_setting);
		sd_Relat=(RelativeLayout) findViewById(R.id.sd_setting);
		tf_Relat=(RelativeLayout) findViewById(R.id.tf_setting);
		update=(RelativeLayout) findViewById(R.id.update_firmware);
		back_btn=(Button) findViewById(R.id.back);
		sensor=(RelativeLayout) findViewById(R.id.setting_sensor);
		
		
		wifi_Relat.setOnClickListener(this);
		pwd_Relat.setOnClickListener(this);
		alarm_Relat.setOnClickListener(this);
		time_Relat.setOnClickListener(this);
		sd_Relat.setOnClickListener(this);
		tf_Relat.setOnClickListener(this);
		update.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		sensor.setOnClickListener(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId())
	    {
		case R.id.wifi_setting:
			Intent intent1 = new Intent(SettingActivity.this,SettingWifiActivity.class);
			intent1.putExtra(ContentCommon.STR_CAMERA_ID, SystemValue.deviceId);
			intent1.putExtra(ContentCommon.STR_CAMERA_NAME,SystemValue.deviceName);
			intent1.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;
		case R.id.pwd_setting:
			Intent intent2 = new Intent(SettingActivity.this,SettingUserActivity.class);
			intent2.putExtra(ContentCommon.STR_CAMERA_ID,SystemValue.deviceId);
			intent2.putExtra(ContentCommon.STR_CAMERA_NAME,SystemValue.deviceName);
			intent2.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
			startActivity(intent2);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);	
					break;
		case R.id.alarm_setting:
			Intent intent3 = new Intent(SettingActivity.this,SettingAlarmActivity.class);
			intent3.putExtra(ContentCommon.STR_CAMERA_ID,SystemValue.deviceId);
			intent3.putExtra(ContentCommon.STR_CAMERA_NAME,SystemValue.deviceName);
			intent3.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
			startActivity(intent3);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;
		case R.id.time_setting:
			Intent intent4 = new Intent(SettingActivity.this,SettingDateActivity.class);
			intent4.putExtra(ContentCommon.STR_CAMERA_ID,SystemValue.deviceId);
			intent4.putExtra(ContentCommon.STR_CAMERA_NAME,SystemValue.deviceName);
			intent4.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
			startActivity(intent4);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;
		case R.id.sd_setting:
			Intent intent5= new Intent(SettingActivity.this,SettingSDCardActivity.class);
			intent5.putExtra(ContentCommon.STR_CAMERA_ID,SystemValue.deviceId);
			intent5.putExtra(ContentCommon.STR_CAMERA_NAME,SystemValue.deviceName);
			intent5.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
			startActivity(intent5);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;
		case R.id.tf_setting:
			Intent intentVid = new Intent(SettingActivity.this,PlayBackTFActivity.class);
			intentVid.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intentVid.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intentVid.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			intentVid.putExtra(ContentCommon.STR_CAMERA_USER, "admin");
			startActivity(intentVid);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;
		case R.id.update_firmware:
			Intent intentup= new Intent(SettingActivity.this,FirmwareUpdateActiviy.class);
			intentup.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intentup.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			startActivity(intentup);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;		
		case R.id.setting_sensor:
			Intent intentsen=new Intent(SettingActivity.this,SensorListActivty.class);
			intentsen.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intentsen.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			intentsen.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			startActivity(intentsen);
			break;
		case R.id.back:
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		default:
			break;
		}
	}
	
}