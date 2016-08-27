package com.example.p_c.masterycar.camera;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.bean.utils.AudioPlayer;
import com.example.p_c.masterycar.camera.bean.utils.CustomBuffer;
import com.example.p_c.masterycar.camera.bean.utils.CustomBufferData;
import com.example.p_c.masterycar.camera.bean.utils.CustomBufferHead;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 远程录像回放
 */
public class PlayBackActivity extends BaseActivity implements BridgeService.PlayBackInterface, BridgeService.DateTimeInterface
{
	private ImageView playImg;
	private String strDID;
	private String strFilePath;
	private byte[] videodata = null;
	private int videoDataLen = 0;
	private int nVideoWidth = 0;
	private int nVideoHeight = 0;
	private boolean isPlaySeekBar = false;
	private LinearLayout layoutConnPrompt;
	private SeekBar playSeekBar;
	private GLSurfaceView myGlSurfaceView;
	private MyRender myRender;
	private int i1 = 0;
	private int i2 = 0;
	boolean exit = false;
	private TextView textTimeStamp;
	private String tzStr = "GMT+08:00";
	private long time;
	private long time1;
	private String timeShow = " ";
	
	private CustomBuffer AudioBuffer = null;
	private AudioPlayer audioPlayer = null;
	private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;
	
	private class MyThread extends Thread {
		@Override
		public void run() {
			while (exit == true) {
				i2 = i1;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i2 == i1) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							exit = false;
							PlayBackActivity.this.finish();
						}
					});
				}
			}
			super.run();
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == 1 || msg.what == 2) {
				if (isOneShow) {
					layoutConnPrompt.setVisibility(View.GONE);
					isOneShow = false;
				}
			}
			switch (msg.what) {
			case 1: {// h264
				textTimeStamp.setText(timeShow);
				myRender.writeSample(videodata, nVideoWidth, nVideoHeight);
				playImg.setVisibility(View.GONE);
				int width = getWindowManager().getDefaultDisplay().getWidth();
		
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width, width * 3 / 4);
				lp.gravity = Gravity.CENTER;
				myGlSurfaceView.setLayoutParams(lp);
			}
				break;
			case 2: {// jpeg
				textTimeStamp.setText(timeShow);
				Bitmap bmp = BitmapFactory.decodeByteArray(videodata, 0,videoDataLen);
				if (bmp == null) {
					return;
				}
				Bitmap bitmap = null;
				int width = getWindowManager().getDefaultDisplay().getWidth();
				int height = getWindowManager().getDefaultDisplay().getHeight();
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width, width * 3 / 4);
				lp.gravity = Gravity.CENTER;
				playImg.setLayoutParams(lp);
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					bitmap = Bitmap.createScaledBitmap(bmp, width,
							width * 3 / 4, true);
				} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					bitmap = Bitmap.createScaledBitmap(bmp, width, height, true);
				}
				playImg.setVisibility(View.VISIBLE);
				playImg.setImageBitmap(bitmap);
			}
				break;
			default:
				break;
			}
		}
	};

	private boolean isOneShow = true;

	private Runnable mVideoTimeOut = new Runnable() {
		public void run() {
			if (isOneShow) {//
				BridgeService.setPlayBackInterface(PlayBackActivity.this);
				vstc2.nativecaller.NativeCaller.StartPlayBack(strDID, strFilePath, 0,0);
				vstc2.nativecaller.NativeCaller.PPPPGetSystemParams(strDID,ContentCommon.MSG_TYPE_GET_PARAMS);
				mHandler.postDelayed(mVideoTimeOut, 3000);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.playback);
		BridgeService.setPlayBackInterface(this);
		vstc2.nativecaller.NativeCaller.StartPlayBack(strDID, strFilePath, 0,0);
		//音频数据
		AudioBuffer = new CustomBuffer();
		audioPlayer = new AudioPlayer(AudioBuffer);
		mHandler.postDelayed(mVideoTimeOut, 3000);
		findView();
		BridgeService.setDateTimeInterface(this);
		vstc2.nativecaller.NativeCaller.PPPPGetSystemParams(strDID,ContentCommon.MSG_TYPE_GET_PARAMS);
		
		StartAudio();//开启声音
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (isPlaySeekBar) {
				isPlaySeekBar = false;
				//playSeekBar.setVisibility(View.GONE);
			} else {
				isPlaySeekBar = true;
				//playSeekBar.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra("did");
		strFilePath = intent.getStringExtra("filepath");

		Log.d("getDataFromOther", "strDID:" + strDID);
		Log.d("getDataFromOther", "strFilePath:" + strFilePath);
	}

	private void findView() {
		playImg = (ImageView) findViewById(R.id.playback_img);
		layoutConnPrompt = (LinearLayout) findViewById(R.id.layout_connect_prompt);
		playSeekBar = (SeekBar) findViewById(R.id.playback_seekbar);
		textTimeStamp = (TextView) findViewById(R.id.textTimeStamp);
		myGlSurfaceView = (GLSurfaceView) findViewById(R.id.myhsurfaceview);
		myRender = new MyRender(myGlSurfaceView);
		myGlSurfaceView.setRenderer(myRender);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		vstc2.nativecaller.NativeCaller.StopPlayBack(strDID);
		StopAudio();
		exit = false;
	}

	private String setDeviceTime(long millisutc, String tz) {

		TimeZone timeZone = TimeZone.getTimeZone(tz);
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(millisutc);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String months = "";
		if (month < 10) {
			months = "0" + month;
		} else {
			months = String.valueOf(month);
		}
		String strDay = "";
		if (day < 10) {
			strDay = "0" + day;
		} else {
			strDay = String.valueOf(day);
		}
		String strHour = "";
		if (hour < 10) {
			strHour = "0" + hour;
		} else {
			strHour = String.valueOf(hour);
		}
		String strMinute = "";
		if (minute < 10) {
			strMinute = "0" + minute;
		} else {
			strMinute = String.valueOf(minute);
		}
		String strSecond = "";
		if (second < 10) {
			strSecond = "0" + second;
		} else {
			strSecond = String.valueOf(second);
		}
		return year + "-" + months + "-" + strDay + "   " + strHour + ":"
				+ strMinute + ":" + strSecond;
	}

	@Override
	public void callBackDatetimeParams(String did, int now, int tz,
									   int ntp_enable, String ntp_svr) {
		setTimeZone(tz);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
											  int result) {
	}

	private void setTimeZone(int tz) {
		switch (tz) {
		case 39600:
			tzStr = "GMT-11:00";
			break;
		case 36000:
			tzStr = "GMT-10:00";
			break;
		case 32400:
			tzStr = "GMT-09:00";
			break;
		case 28800:
			tzStr = "GMT-08:00";
			break;
		case 25200:
			tzStr = "GMT-07:00";
			break;
		case 21600:
			tzStr = "GMT-06:00";
			break;
		case 18000:
			tzStr = "GMT-05:00";
			break;
		case 14400:
			tzStr = "GMT-04:00";
			break;
		case 12600:
			tzStr = "GMT-03:30";
			break;
		case 10800:
			tzStr = "GMT-03:00";
			break;
		case 7200:
			tzStr = "GMT-02:00";
			break;
		case 3600:
			tzStr = "GMT-01:00";
			break;
		case 0:
			tzStr = "GMT";
			break;
		case -3600:
			tzStr = "GMT+01:00";
			break;
		case -7200:
			tzStr = "GMT+02:00";
			break;
		case -10800:
			tzStr = "GMT+03:00";
			break;
		case -12600:
			tzStr = "GMT+03:30";
			break;
		case -14400:
			tzStr = "GMT+04:00";
			break;
		case -16200:
			tzStr = "GMT+04:30";
			break;
		case -18000:
			tzStr = "GMT+05:00";
			break;
		case -19800:
			tzStr = "GMT+05:30";
			break;

		case -21600:
			tzStr = "GMT+06:00";
			break;
		case -25200:
			tzStr = "GMT+07:00";
			break;
		case -28800:
			tzStr = "GMT+08:00";
			break;
		case -32400:
			tzStr = "GMT+09:00";
			break;
		case -34200:
			tzStr = "GMT+09:30";
			break;
		case -36000:
			tzStr = "GMT+10:00";
			break;
		case -39600:
			tzStr = "GMT+11:00";
			break;
		case -43200:
			tzStr = "GMT+12:00";
			break;
		default:
			break;
		}
	}

	@Override
	public void callBackPlaybackVideoData(byte[] videobuf, int h264Data,
			int len, int width, int height,int time,int frameType,int originframeLen) {
		// TODO Auto-generated method stub
		i1++;
		if (exit == false) {
			exit = true;
			new MyThread().start();
		}
		
		this.time = time;
		videodata = videobuf;
		videoDataLen = len;
		nVideoWidth = width;
		nVideoHeight = height;
		time1 = this.time * 1000;
		timeShow = setDeviceTime(time1, tzStr);
		if (h264Data == 1) { // H264
			mHandler.sendEmptyMessage(1);
		} else { // MJPEG
			mHandler.sendEmptyMessage(2);
		}
		
		///音频数据
		if (h264Data == 0 && frameType == 6) 
		{
			// Log.e("vst ", "h264Data == 0 && FrameType == 6 ");
			/*
			 * if (!isOpen) { StartAudio(); isOpen = true; }
			 */
			if (!audioPlayer.isAudioPlaying()) {
				return;
			}

			CustomBufferHead head = new CustomBufferHead();
			CustomBufferData data = new CustomBufferData();
			head.length = len;
			// Log.e("vst ", "h264Data == 0 && FrameType == 6 " + "len" + len);
			head.startcode = AUDIO_BUFFER_START_CODE;
			data.head = head;
			data.data = videobuf;
			AudioBuffer.addData(data);

			Message msg = new Message();
			Bundle b = new Bundle();
			b.putInt("oneFramesize", originframeLen);
			msg.setData(b);
			return;
		}
	}
	

	///开启音频
	private void StartAudio() {
		synchronized (this) {
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStart();
		}
	}

	private void StopAudio() {
		synchronized (this) {
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStop();
		}
	}
}
