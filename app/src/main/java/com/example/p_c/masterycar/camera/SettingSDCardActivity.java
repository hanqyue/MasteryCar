package com.example.p_c.masterycar.camera;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.bean.bean.SdcardBean;


public class SettingSDCardActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener, BridgeService.SDCardInterface {
	private TextView tvSdTotal = null;
	private TextView tvSdRemain = null;
	private TextView tvSdStatus = null;
	private Button btnFormat = null;
	private CheckBox cbxConverage = null;
	private EditText editRecordLength = null;
	private CheckBox cbxRecordTime = null;
	private Button btnBack = null;
	private Button btnOk = null;
	private final int TIMEOUT = 3000;
	private String strDID = null;// camera id
	// private String cameraName = null;
	private ProgressDialog progressDialog = null;
	private boolean successFlag = false;// 获取和设置的结果
	private final int FAILED = 0;
	private final int SUCCESS = 1;
	private final int PARAMS = 2;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FAILED:
				showToast(R.string.sdcard_set_failed);
				break;
			case SUCCESS:
				showToast(R.string.sdcard_set_success);
				finish();
				break;
			case PARAMS:
				successFlag = true;
				progressDialog.dismiss();
				tvSdTotal.setText(sdcardBean.getSdtotal() + "MB");
				tvSdRemain.setText(sdcardBean.getSdfree() + "MB");
				if (sdcardBean.getRecord_sd_status() == 1)
				{
					tvSdStatus
							.setText(SettingSDCardActivity.this.getResources()
									.getString(R.string.sdcard_inserted));
				} 
				else if (sdcardBean.getRecord_sd_status() == 2)
				{
					tvSdStatus.setText(getString(R.string.sdcard_video));
				}
				else if(sdcardBean.getRecord_sd_status() == 3)
				{
					tvSdStatus.setText(getString(R.string.sdcard_file_error));
				}
				else if(sdcardBean.getRecord_sd_status() == 4)
				{
					tvSdStatus.setText(getString(R.string.sdcard_isformatting));
				}
				else {
					tvSdStatus.setText(SettingSDCardActivity.this
							.getResources().getString(
									R.string.sdcard_status_info));
				}
				cbxConverage.setChecked(true);
				if (sdcardBean.getRecord_time_enable() == 1) {
					cbxRecordTime.setChecked(true);
				} else {
					cbxRecordTime.setChecked(false);
				}
				// editRecordLength.setText(sdcardBean.getRecord_timer() + "");
				editRecordLength.setText(15 + "");
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);//动画
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingsdcard);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.sdcard_getparams));
		progressDialog.show();
		sdcardBean = new SdcardBean();
		handler.postDelayed(runnable, TIMEOUT);
		findView();
		setLister();
		BridgeService.setSDCardInterface(this);
		vstc2.nativecaller.NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_RECORD);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		Log.i("info", "did:" + strDID);
		// cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				successFlag = false;
				progressDialog.dismiss();
			}
		}
	};
	private SdcardBean sdcardBean;

	private void setLister() {
		btnBack.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnFormat.setOnClickListener(this);
		cbxConverage.setOnCheckedChangeListener(this);
		cbxRecordTime.setOnCheckedChangeListener(this);

	}

	private void findView() {
		tvSdTotal = (TextView) findViewById(R.id.tv_sd_total);
		tvSdRemain = (TextView) findViewById(R.id.tv_sd_remain);
		tvSdStatus = (TextView) findViewById(R.id.tv_state);
		btnFormat = (Button) findViewById(R.id.btn_format);
		cbxConverage = (CheckBox) findViewById(R.id.cbx_coverage);
		editRecordLength = (EditText) findViewById(R.id.edit_record_length);
		cbxRecordTime = (CheckBox) findViewById(R.id.cbx_record_time);
		btnBack = (Button) findViewById(R.id.back);
		btnOk = (Button) findViewById(R.id.ok);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		layout.setBackgroundDrawable(drawable);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.ok:
			setSDCardSchedule();
			break;
		case R.id.btn_format:
			showFormatDialog();
			break;
		default:
			break;
		}
	}

	void showFormatDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(R.string.sdcard_formatsd);
		adb.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.i("info", "格式化");
						vstc2.nativecaller.NativeCaller.FormatSD(strDID);
						dialog.dismiss();
					}

				})
				.setNegativeButton(R.string.str_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}).create().show();
	}

	// 20140226修改测试

	private void setSDCardSchedule() {

		if (sdcardBean.getRecord_time_enable() == 0) {
			sdcardBean.setSun_0(0);
			sdcardBean.setSun_1(0);
			sdcardBean.setSun_2(0);
			sdcardBean.setMon_0(0);
			sdcardBean.setMon_1(0);
			sdcardBean.setMon_2(0);
			sdcardBean.setTue_0(0);
			sdcardBean.setTue_1(0);
			sdcardBean.setTue_2(0);
			sdcardBean.setWed_0(0);
			sdcardBean.setWed_1(0);
			sdcardBean.setWed_2(0);
			sdcardBean.setThu_0(0);
			sdcardBean.setThu_1(0);
			sdcardBean.setThu_2(0);
			sdcardBean.setFri_0(0);
			sdcardBean.setFri_1(0);
			sdcardBean.setFri_2(0);
			sdcardBean.setSat_0(0);
			sdcardBean.setSat_1(0);
			sdcardBean.setSat_2(0);
		} else {
			sdcardBean.setSun_0(-1);
			sdcardBean.setSun_1(-1);
			sdcardBean.setSun_2(-1);
			sdcardBean.setMon_0(-1);
			sdcardBean.setMon_1(-1);
			sdcardBean.setMon_2(-1);
			sdcardBean.setTue_0(-1);
			sdcardBean.setTue_1(-1);
			sdcardBean.setTue_2(-1);
			sdcardBean.setWed_0(-1);
			sdcardBean.setWed_1(-1);
			sdcardBean.setWed_2(-1);
			sdcardBean.setThu_0(-1);
			sdcardBean.setThu_1(-1);
			sdcardBean.setThu_2(-1);
			sdcardBean.setFri_0(-1);
			sdcardBean.setFri_1(-1);
			sdcardBean.setFri_2(-1);
			sdcardBean.setSat_0(-1);
			sdcardBean.setSat_1(-1);
			sdcardBean.setSat_2(-1);
		}

		sdcardBean.setRecord_timer(15);
		vstc2.nativecaller.NativeCaller.PPPPSDRecordSetting(strDID,
				sdcardBean.getRecord_conver_enable(),
				sdcardBean.getRecord_timer(), sdcardBean.getRecord_size(),sdcardBean.getRecord_chnl(),
				sdcardBean.getRecord_time_enable(), sdcardBean.getSun_0(),
				sdcardBean.getSun_1(), sdcardBean.getSun_2(),
				sdcardBean.getMon_0(), sdcardBean.getMon_1(),
				sdcardBean.getMon_2(), sdcardBean.getTue_0(),
				sdcardBean.getTue_1(), sdcardBean.getTue_2(),
				sdcardBean.getWed_0(), sdcardBean.getWed_1(),
				sdcardBean.getWed_2(), sdcardBean.getThu_0(),
				sdcardBean.getThu_1(), sdcardBean.getThu_2(),
				sdcardBean.getFri_0(), sdcardBean.getFri_1(),
				sdcardBean.getFri_2(), sdcardBean.getSat_0(),
				sdcardBean.getSat_1(), sdcardBean.getSat_2());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		switch (v.getId()) {
		// case R.id.cbx_coverage:
		// if (isChecked) {
		// sdcardBean.setRecord_conver_enable(1);
		// } else {
		// sdcardBean.setRecord_conver_enable(0);
		// }
		// break;
		case R.id.cbx_record_time:
			if (isChecked) {
				sdcardBean.setRecord_time_enable(1);
			} else {
				sdcardBean.setRecord_time_enable(0);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void callBackRecordSchParams(String did, int record_cover_enable,
										int record_timer, int record_size, int record_time_enable,
										int record_schedule_sun_0, int record_schedule_sun_1,
										int record_schedule_sun_2, int record_schedule_mon_0,
										int record_schedule_mon_1, int record_schedule_mon_2,
										int record_schedule_tue_0, int record_schedule_tue_1,
										int record_schedule_tue_2, int record_schedule_wed_0,
										int record_schedule_wed_1, int record_schedule_wed_2,
										int record_schedule_thu_0, int record_schedule_thu_1,
										int record_schedule_thu_2, int record_schedule_fri_0,
										int record_schedule_fri_1, int record_schedule_fri_2,
										int record_schedule_sat_0, int record_schedule_sat_1,
										int record_schedule_sat_2, int record_sd_status, int sdtotal,
										int sdfree) {
		Log.i("info", "---record_cover_enable" + record_cover_enable
				+ "---record_time_enable" + record_time_enable
				+ "---record_timer" + record_timer);
		Log.i("info", "record_schedule_sun_0:" + record_schedule_sun_0
				+ ",record_schedule_sun_1:" + record_schedule_sun_1
				+ ",record_schedule_sun_2:" + record_schedule_sun_2
				+ ",record_schedule_mon_0:" + record_schedule_mon_0
				+ ",record_schedule_mon_1:" + record_schedule_mon_1
				+ ",record_schedule_mon_2:" + record_schedule_mon_2);
		sdcardBean.setDid(did);
		sdcardBean.setRecord_conver_enable(record_cover_enable);
		sdcardBean.setRecord_timer(record_timer);
		sdcardBean.setRecord_size(record_size);
		sdcardBean.setRecord_time_enable(record_time_enable);
		sdcardBean.setRecord_sd_status(record_sd_status);
		sdcardBean.setSdtotal(sdtotal);
		sdcardBean.setSdfree(sdfree);
		sdcardBean.setSun_0(record_schedule_sun_0);
		sdcardBean.setSun_1(record_schedule_sun_1);
		sdcardBean.setSun_2(record_schedule_sun_2);
		sdcardBean.setMon_0(record_schedule_mon_0);
		sdcardBean.setMon_1(record_schedule_mon_1);
		sdcardBean.setMon_2(record_schedule_mon_2);
		sdcardBean.setTue_0(record_schedule_tue_0);
		sdcardBean.setTue_1(record_schedule_tue_1);
		sdcardBean.setTue_2(record_schedule_tue_2);
		sdcardBean.setWed_0(record_schedule_wed_0);
		sdcardBean.setWed_1(record_schedule_wed_1);
		sdcardBean.setWed_2(record_schedule_wed_2);
		sdcardBean.setThu_0(record_schedule_thu_0);
		sdcardBean.setThu_1(record_schedule_thu_1);
		sdcardBean.setThu_2(record_schedule_thu_2);
		sdcardBean.setFri_0(record_schedule_fri_0);
		sdcardBean.setFri_1(record_schedule_fri_1);
		sdcardBean.setFri_2(record_schedule_fri_2);
		sdcardBean.setSat_0(record_schedule_sat_0);
		sdcardBean.setSat_1(record_schedule_sat_1);
		sdcardBean.setSat_2(record_schedule_sat_2);
		handler.sendEmptyMessage(PARAMS);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
											  int result) {
		Log.d("tag", "result:" + result + " paramType:" + paramType);
		if (strDID.equals(did)) {
			handler.sendEmptyMessage(result);
		}
	}
}
