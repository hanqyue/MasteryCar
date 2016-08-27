package com.example.p_c.masterycar.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.adapter.SearchListAdapter;
import com.example.p_c.masterycar.camera.utils.SystemValue;

import java.util.Map;
import java.util.TimerTask;


public class AddCameraActivity extends Activity implements OnClickListener,BridgeService.AddCameraInterface
     , OnItemSelectedListener, BridgeService.IpcamClientInterface,BridgeService.CallBackMessageInterface
{
	private EditText userEdit = null;
	private EditText pwdEdit = null;
	private EditText didEdit = null;
	private TextView textView_top_show = null;
	private Button done;
	private static final int SEARCH_TIME = 3000;
	private int option = ContentCommon.INVALID_OPTION;
	private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
	private Button btnSearchCamera;
	private SearchListAdapter listAdapter = null;
	private ProgressDialog progressdlg = null;
	private boolean isSearched;
	private MyBroadCast receiver;
	private WifiManager manager = null;
	private ProgressBar progressBar = null;
	private static final String STR_DID = "did";
	private static final String STR_MSG_PARAM = "msgparam";
	private MyWifiThread myWifiThread = null;
	private boolean blagg = false;
	private Intent intentbrod = null;
	private WifiInfo info = null;
	boolean bthread = true;
	private Button button_play = null;
	private Button button_setting = null;
	private Button pic_video=null;
	private Button button_linkcamera=null;
	private int tag = 0;

	class MyTimerTask extends TimerTask {

		public void run() {
			updateListHandler.sendEmptyMessage(100000);
		}
	};

	class MyWifiThread extends Thread {
		@Override
		public void run() {
			while (blagg == true) {
				super.run();

				updateListHandler.sendEmptyMessage(100000);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			
			AddCameraActivity.this.finish();
			Log.d("ip", "AddCameraActivity.this.finish()");
		}

	}

		class StartPPPPThread implements Runnable {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
					startCameraPPPP();
				} catch (Exception e) {

				}
			}
		}
	private void startCameraPPPP() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		int result = vstc2.nativecaller.NativeCaller.StartPPPP(SystemValue.deviceId, SystemValue.deviceName,
				SystemValue.devicePass,1,"");
		Log.i("ip", "result:"+result);
	}

	private void stopCameraPPPP()
	{
		vstc2.nativecaller.NativeCaller.StopPPPP(SystemValue.deviceId);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_camera);
		progressdlg = new ProgressDialog(this);
		progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressdlg.setMessage(getString(R.string.searching_tip));
		listAdapter = new SearchListAdapter(this);
		findView();
		manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		InitParams();

		BridgeService.setAddCameraInterface(this);
		BridgeService.setCallBackMessage(this);
		receiver = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("finish");
		registerReceiver(receiver, filter);
		intentbrod = new Intent("drop");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		blagg = true;
	}

	private void InitParams() {

		done.setOnClickListener(this);
		btnSearchCamera.setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (myWifiThread != null) {
			blagg = false;
		}
		progressdlg.dismiss();
		vstc2.nativecaller.NativeCaller.StopSearch();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		vstc2.nativecaller.NativeCaller.Free();
		Intent intent = new Intent();
		intent.setClass(this, BridgeService.class);
		stopService(intent);
		tag = 0;
	}

	Runnable updateThread = new Runnable() {

		public void run() {
			vstc2.nativecaller.NativeCaller.StopSearch();
			progressdlg.dismiss();
			Message msg = updateListHandler.obtainMessage();
			msg.what = 1;
			updateListHandler.sendMessage(msg);
		}
	};
	
	Handler updateListHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				listAdapter.notifyDataSetChanged();
				if (listAdapter.getCount() > 0) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							AddCameraActivity.this);
					dialog.setTitle(getResources().getString(
							R.string.add_search_result));
					dialog.setPositiveButton(
							getResources().getString(R.string.refresh),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startSearch();
								}
							});
					dialog.setNegativeButton(
							getResources().getString(R.string.str_cancel), null);
					dialog.setAdapter(listAdapter,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int arg2) {
									Map<String, Object> mapItem = (Map<String, Object>) listAdapter.getItemContent(arg2);
									if (mapItem == null) {
										return;
									}

									String strName = (String) mapItem
											.get(ContentCommon.STR_CAMERA_NAME);
									String strDID = (String) mapItem
											.get(ContentCommon.STR_CAMERA_ID);
									String strUser = ContentCommon.DEFAULT_USER_NAME;
									String strPwd = ContentCommon.DEFAULT_USER_PWD;
									userEdit.setText(strUser);
									pwdEdit.setText(strPwd);
									didEdit.setText(strDID);

								}
							});

					dialog.show();
				} else {
					Toast.makeText(AddCameraActivity.this,
							getResources().getString(R.string.add_search_no),
							Toast.LENGTH_LONG).show();
					isSearched = false;// 
				}
			}
			
			
			if(msg.what==2){
				button_linkcamera.setVisibility(View.VISIBLE);
			}
			
		}
	};

	public static String int2ip(long ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	private void startSearch() {
		listAdapter.ClearAll();
		progressdlg.setMessage(getString(R.string.searching_tip));
		progressdlg.show();
		new Thread(new SearchThread()).start();
		updateListHandler.postDelayed(updateThread, SEARCH_TIME);
	}

	private class SearchThread implements Runnable {
		@Override
		public void run() {
			Log.d("tag", "startSearch");
			vstc2.nativecaller.NativeCaller.StartSearch();
		}
	}

	private void findView() {
		progressBar = (ProgressBar) findViewById(R.id.main_model_progressBar1);
		textView_top_show = (TextView) findViewById(R.id.login_textView1);
		button_play = (Button) findViewById(R.id.play);
		button_setting = (Button) findViewById(R.id.setting);
		pic_video=(Button) findViewById(R.id.location_pics_videos);
		pic_video.setOnClickListener(this);
		done = (Button) findViewById(R.id.done);
		done.setText("连接");
		userEdit = (EditText) findViewById(R.id.editUser);
		pwdEdit = (EditText) findViewById(R.id.editPwd);
		didEdit = (EditText) findViewById(R.id.editDID);
		btnSearchCamera = (Button) findViewById(R.id.btn_searchCamera);
		button_linkcamera=(Button)findViewById(R.id.btn_linkcamera);
		
		button_linkcamera.setOnClickListener(this);
		button_play.setOnClickListener(this);
		button_setting.setOnClickListener(this);
	}
	/**
	 * 摄像机在线时可以获取一张摄像机当前的画面图
	 */
	/*private void getSnapshot(){
		String msg="snapshot.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
		 + "&user=admin&pwd=" + SystemValue.devicePass;
		NativeCaller.TransferMessage(SystemValue.deviceId, msg, 1);
	}
*/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			Intent intent = new Intent(AddCameraActivity.this,PlayActivity.class);
			startActivity(intent);
			break;
		case R.id.setting:
			if (tag == 1) {
				Intent intent1 = new Intent(AddCameraActivity.this,
						SettingActivity.class);
				intent1.putExtra(ContentCommon.STR_CAMERA_ID,
						SystemValue.deviceId);
				intent1.putExtra(ContentCommon.STR_CAMERA_NAME,
						SystemValue.deviceName);
				intent1.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
				startActivity(intent1);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			} else {
				Toast.makeText(AddCameraActivity.this,getResources().getString(R.string.main_setting_prompt), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.location_pics_videos://本地视频图像
			if(SystemValue.deviceId!=null)
			{
				Intent intent1 = new Intent(AddCameraActivity.this,LocalPictureAndVideoActivity.class);
				intent1.putExtra(ContentCommon.STR_CAMERA_ID,
						SystemValue.deviceId);
				intent1.putExtra(ContentCommon.STR_CAMERA_NAME,
						SystemValue.deviceName);
				intent1.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
				startActivity(intent1);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}else{
				Toast.makeText(AddCameraActivity.this,"请确认是否选择设备", Toast.LENGTH_SHORT).show();
			}
	       break;
		case R.id.done:
			if (tag == 1) {
				Toast.makeText(AddCameraActivity.this,"设备已经是在线状态了", Toast.LENGTH_SHORT).show();
			} else if (tag == 2) {
				Toast.makeText(AddCameraActivity.this, "设备不在线", Toast.LENGTH_SHORT).show();
			} else {
				done();
			}

			break;
		case R.id.btn_searchCamera:
			stopCameraPPPP();
			//把相机状态，设备id置空
			tag=0;
			textView_top_show.setText(R.string.login_stuta_camer);
			SystemValue.deviceId=null;
			searchCamera();
			break;
		case R.id.btn_linkcamera:
			Intent it=new Intent(AddCameraActivity.this,LinkCameraSettingActivity.class);
			it.putExtra(ContentCommon.STR_CAMERA_ID,
					SystemValue.deviceId);
			it.putExtra(ContentCommon.STR_CAMERA_NAME,
					SystemValue.deviceName);
			it.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass);
			startActivity(it);
			break;
		default:
			break;
		}
	}
	

	private void searchCamera() {
		if (!isSearched) {
			isSearched = true;
			startSearch();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					AddCameraActivity.this);
			dialog.setTitle(getResources()
					.getString(R.string.add_search_result));
			dialog.setPositiveButton(
					getResources().getString(R.string.refresh),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startSearch();

						}
					});
			dialog.setNegativeButton(
					getResources().getString(R.string.str_cancel), null);
			dialog.setAdapter(listAdapter,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg2) {
							Map<String, Object> mapItem = (Map<String, Object>) listAdapter
									.getItemContent(arg2);
							if (mapItem == null) {
								return;
							}

							String strName = (String) mapItem
									.get(ContentCommon.STR_CAMERA_NAME);
							String strDID = (String) mapItem
									.get(ContentCommon.STR_CAMERA_ID);
							String strUser = ContentCommon.DEFAULT_USER_NAME;
							String strPwd = ContentCommon.DEFAULT_USER_PWD;
							userEdit.setText(strUser);
							pwdEdit.setText(strPwd);
							didEdit.setText(strDID);

						}
					});
			dialog.show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AddCameraActivity.this.finish();
			return false;
		}
		return false;
	}

	private void done() {
		Intent in = new Intent();
		String strUser = userEdit.getText().toString();
		String strPwd = pwdEdit.getText().toString();
		String strDID = didEdit.getText().toString();

		if (strDID.length() == 0) 
		{
			Toast.makeText(AddCameraActivity.this,
					getResources().getString(R.string.input_camera_id), Toast.LENGTH_SHORT).show();
			return;
		}

		if (strUser.length() == 0)
		{
			Toast.makeText(AddCameraActivity.this,
					getResources().getString(R.string.input_camera_user), Toast.LENGTH_SHORT).show();
			return;
		}
		if (option == ContentCommon.INVALID_OPTION) {
			option = ContentCommon.ADD_CAMERA;
		}
		in.putExtra(ContentCommon.CAMERA_OPTION, option);
		in.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
		in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
		in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
		in.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType);
		progressBar.setVisibility(View.VISIBLE);
		SystemValue.deviceName = strUser;
		SystemValue.deviceId = strDID;
		SystemValue.devicePass = strPwd;
		BridgeService.setIpcamClientInterface(this);
		vstc2.nativecaller.NativeCaller.Init();
		new Thread(new StartPPPPThread()).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			didEdit.setText(scanResult);
		}
	}

	/**
	 * BridgeService callback
	 * **/
	@Override
	public void callBackSearchResultData(int sysver, String strMac,
										 String strName, String strDeviceID, String strIpAddr, int port) {
		Log.e("AddCameraActivity", strDeviceID+strName+"   123");
		if (!listAdapter.AddCamera(strMac, strName, strDeviceID)) {
			return;
		}
	}

	public String getInfoSSID() {

		info = manager.getConnectionInfo();
		String ssid = info.getSSID();
		return ssid;
	}

	public int getInfoIp() {

		info = manager.getConnectionInfo();
		int ip = info.getIpAddress();
		return ip;
	}

	private Handler PPPPMsgHandler = new Handler() {
		public void handleMessage(Message msg) {

			Bundle bd = msg.getData();
			int msgParam = bd.getInt(STR_MSG_PARAM);
			int msgType = msg.what;
			Log.i("aaa", "===="+msgType+"--msgParam:"+msgParam);
			String did = bd.getString(STR_DID);
			switch (msgType) {
			case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
				int resid;
				switch (msgParam) {
				case ContentCommon.PPPP_STATUS_CONNECTING://0
					resid = R.string.pppp_status_connecting;
					progressBar.setVisibility(View.VISIBLE);
					tag = 2;
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
					resid = R.string.pppp_status_connect_failed;
					progressBar.setVisibility(View.GONE);
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_DISCONNECT://4
					resid = R.string.pppp_status_disconnect;
					progressBar.setVisibility(View.GONE);
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_INITIALING://1
					resid = R.string.pppp_status_initialing;
					progressBar.setVisibility(View.VISIBLE);
					tag = 2;
					break;
				case ContentCommon.PPPP_STATUS_INVALID_ID://5
					resid = R.string.pppp_status_invalid_id;
					progressBar.setVisibility(View.GONE);
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态
					resid = R.string.pppp_status_online;
					progressBar.setVisibility(View.GONE);
					//摄像机在线之后读取摄像机类型
					String cmd="get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
							+ "&user=admin&pwd=" + SystemValue.devicePass;
					vstc2.nativecaller.NativeCaller.TransferMessage(did, cmd, 1);
					tag = 1;
					break;
				case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
					resid = R.string.device_not_on_line;
					progressBar.setVisibility(View.GONE);
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
					resid = R.string.pppp_status_connect_timeout;
					progressBar.setVisibility(View.GONE);
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
					resid =R.string.pppp_status_pwd_error;
					progressBar.setVisibility(View.GONE);
					tag = 0;
					break;
				default:
					resid = R.string.pppp_status_unknown;
				}
				textView_top_show.setText(getResources().getString(resid));
				if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
					vstc2.nativecaller.NativeCaller.PPPPGetSystemParams(did,ContentCommon.MSG_TYPE_GET_PARAMS);
				}
				if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
						|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
						|| msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
						|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
						|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
					vstc2.nativecaller.NativeCaller.StopPPPP(did);
				}
				break;
			case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
				break;

			}

		}
	};

	@Override
	public void BSMsgNotifyData(String did, int type, int param) {
		Log.d("ip", "type:" + type + " param:" + param);
		Bundle bd = new Bundle();
		Message msg = PPPPMsgHandler.obtainMessage();
		msg.what = type;
		bd.putInt(STR_MSG_PARAM, param);
		bd.putString(STR_DID, did);
		msg.setData(bd);
		PPPPMsgHandler.sendMessage(msg);
		if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
			intentbrod.putExtra("ifdrop", param);
			sendBroadcast(intentbrod);
		}

	}

	@Override
	public void BSSnapshotNotify(String did, byte[] bImage, int len) {
		// TODO Auto-generated method stub
		Log.i("ip", "BSSnapshotNotify---len"+len);
	}

	@Override
	public void callBackUserParams(String did, String user1, String pwd1,
								   String user2, String pwd2, String user3, String pwd3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void CameraStatus(String did, int status) {

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
							   long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void CallBackGetStatus(String did, String resultPbuf, int cmd) {
		// TODO Auto-generated method stub
		if (cmd == ContentCommon.CGI_IEGET_STATUS) {
			String cameraType = spitValue(resultPbuf, "upnp_status=");
			int intType = Integer.parseInt(cameraType);
			int type14 = (int) (intType >> 16) & 1;// 14位 来判断是否报警联动摄像机
			if (intType == 2147483647) {// 特殊值
				type14 = 0;
			}
			
			if(type14==1){
				updateListHandler.sendEmptyMessage(2);
			}
			
		}
	}
	
	private String spitValue(String name, String tag) {
		String[] strs = name.split(";");
		for (int i = 0; i < strs.length; i++) {
			String str1 = strs[i].trim();
			if (str1.startsWith("var")) {
				str1 = str1.substring(4, str1.length());
			}
			if (str1.startsWith(tag)) {
				String result = str1.substring(str1.indexOf("=") + 1);
				return result;
			}
		}
		return -1 + "";
	}

}
