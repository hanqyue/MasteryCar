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
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.bean.utils.DatabaseUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * */
public class ShowLocalVideoActivity extends BaseActivity implements
		OnClickListener, OnGestureListener, OnDoubleTapListener,
		OnTouchListener {
	private DatabaseUtil mDbUtil;
	private TextView mTv_Prompt;
	private String filePath = null;
	private String videoTime;
	private ImageView img;
	private ImageView imgPause;
	private String strCameraName;
	private ImageView btnBack;
	private TextView tvBack;
	private TextView tvTitle;
	private TextView tvTime;
	private TextView showVideoTime;
	private GestureDetector gt = new GestureDetector(this);
	private boolean isPlaying = false;// ־
	private boolean flag = true;//־
	private RelativeLayout topLayout;
	private RelativeLayout bottomLayout;
	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	private boolean isShowing = false;
	private boolean isStart = true;
	private int videoSumTime;//
	private int progress = 0;// 
	private int frameCout = 0;//֡
	private int sum;// ֡
	private ProgressBar seekBar;
	private TextView tvSumTime;
	private TextView tvCurrentTime;
	private Button btnPlay;
	private Button btnLeft;
	private Button btnRight;
	private boolean isPause = false;
	private int sumTime;
	//private ArrayList<Map<String, Object>> arrayList;
	//private ArrayList<String> timeList;
	private boolean isView = false;
	private int sumFrame;
	private int middleFrame;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				isShowing = false;
				topLayout.setVisibility(View.GONE);
				bottomLayout.setVisibility(View.GONE);
				break;
			case 2:
				seekBar.setMax(videoSumTime);
				tvSumTime.setText(getTime(videoSumTime / 1000));
				startVideo();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();

		setContentView(R.layout.showlocalvideo_activity);
		findView();
		setListener();
		tvTime.setText(getResources().getString(R.string.local_video_date)
				+ getContent(filePath));
		firstPicture();
		mHandler.sendEmptyMessageDelayed(1, 3000);
	}

	private void setListener() {
		btnPlay.setOnClickListener(this);
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		img.setOnTouchListener(this);
		topLayout.setOnTouchListener(this);
		bottomLayout.setOnTouchListener(this);
	}

	private String mess;

	private void getDataFromOther() {
		Intent intent = getIntent();
		filePath = intent.getStringExtra("filepath");
		strCameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		//arrayList = (ArrayList<Map<String, Object>>) intent
		//		.getSerializableExtra("arrayList");
		position = intent.getIntExtra("position", 0);
		mess = intent.getStringExtra("videotime");
		//timeList = (ArrayList<String>) intent.getSerializableExtra("timeList");
		videoTime = mess(mess);
		Log.d("tag", "strDID:" + filePath);
	}

	private void findView() {
		wh = getWindowManager().getDefaultDisplay().getWidth();
		ht = getWindowManager().getDefaultDisplay().getHeight();
		btnBack = (ImageView) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		tvBack = (TextView) findViewById(R.id.tv_back);
		tvBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.takevideo_title);
		showVideoTime = (TextView) findViewById(R.id.showvideotime);
		showVideoTime.setText(videoTime.substring(0, 19));
		tvTitle.setText("录像");
		tvTime = (TextView) findViewById(R.id.takevideo_time);
		img = (ImageView) findViewById(R.id.img_playvideo);
		imgPause = (ImageView) findViewById(R.id.img_pause);
		topLayout = (RelativeLayout) findViewById(R.id.top);
		bottomLayout = (RelativeLayout) findViewById(R.id.bottom);
		seekBar = (ProgressBar) findViewById(R.id.progressBar1);
		tvSumTime = (TextView) findViewById(R.id.sumtime);
		tvCurrentTime = (TextView) findViewById(R.id.currenttime);
		btnPlay = (Button) findViewById(R.id.btn_play);
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnRight = (Button) findViewById(R.id.btn_right);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.top_bg);
		// BitmapDrawable drawable = new BitmapDrawable(bitmap);
		// drawable.setTileModeXY(TileMode.REPEAT , TileMode.REPEAT );
		// drawable.setDither(true);
		// layout.setBackgroundDrawable(drawable);

		RelativeLayout showbottom = (RelativeLayout) findViewById(R.id.showbottom);
		// showbottom.setBackgroundDrawable(drawable);
		// bottomLayout.setBackgroundDrawable(drawable);

		myGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
		myRender = new MyRender(myGLSurfaceView);
		myGLSurfaceView.setRenderer(myRender);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(wh,
					wh * 3 / 4);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			myGLSurfaceView.setLayoutParams(lp);
			img.setLayoutParams(lp);
			tvTime.setVisibility(View.GONE);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			tvTime.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(wh, ht);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			img.setLayoutParams(lp);
			myGLSurfaceView.setLayoutParams(lp);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		wh = getWindowManager().getDefaultDisplay().getWidth();
		ht = getWindowManager().getDefaultDisplay().getHeight();
		if (getResources().getConfiguration().orientation == newConfig.ORIENTATION_PORTRAIT) {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(wh,
					wh * 3 / 4);
			lp.gravity = Gravity.CENTER;
			myGLSurfaceView.setLayoutParams(lp);
			img.setLayoutParams(lp);
			// tvTime.setVisibility(View.GONE);
		} else if (getResources().getConfiguration().orientation == newConfig.ORIENTATION_LANDSCAPE) {
			// tvTime.setVisibility(View.VISIBLE);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(wh, ht);
			lp.gravity = Gravity.CENTER;
			img.setLayoutParams(lp);
			myGLSurfaceView.setLayoutParams(lp);
		}
	}

	private String getContent(String filePath) {
		Log.d("tag", "filePath:" + filePath);
		String s = filePath.substring(filePath.lastIndexOf("/") + 1);
		String date = s.substring(0, 10);
		String time = s.substring(11, 16).replace("_", ":");
		String result = time;
		Log.d("tag", "result:" + result);
		Log.d("tag", "sss:" + s.substring(0, 16));
		return result;
	}

	private String getTime(int time) {
		int second = time % 60;
		int minute = time / 60;
		int hour = minute / 60;
		String strSecond = "";
		String strMinute = "";
		String strHour = "";
		if (second < 10) {
			strSecond = "0" + second;
		} else {
			strSecond = String.valueOf(second);
		}
		if (minute < 10) {
			strMinute = "0" + minute;
		} else {
			strMinute = String.valueOf(minute);
		}
		if (hour < 10) {
			strHour = "0" + hour;
		} else {
			strHour = String.valueOf(hour);
		}

		return strHour + ":" + strMinute + ":" + strSecond;
	}

	/**
	 *
	 * */
	private class PlayThread extends Thread {
		public void run() {
			if (filePath != null) {
				File file = new File(filePath);
				FileInputStream in = null;
				try {
					in = new FileInputStream(file);
					Log.d("tag", "" + in.available());
					byte[] header = new byte[4];
					in.read(header);
					int fType = byteToInt(header);
					Log.d("tag", "fType:" + fType);
					frameCout = 0;
					sumTime = 0;
					int pici = 0;
					flag = true;
					mProgressHandler.postDelayed(myProRunnable, 0);
					while (in.available() != 0 && flag) {

						synchronized (ShowLocalVideoActivity.this) {
							Log.d("tag", "flag=" + flag);

							if (isPlaying) {
								frameCout++;

								switch (fType) {
								case 1: {// yuv
									long startTiem = (new Date()).getTime();
									byte[] sizebyte = new byte[4];
									byte[] typebyte = new byte[4];
									byte[] timebyte = new byte[4];
									in.read(sizebyte);
									in.read(typebyte);
									in.read(timebyte);
									int length = byteToInt(sizebyte);
									pici = pici + 1;
									middleFrame = pici;

									if (length == pici - 1) {

										flag = false;
										isStart = true;
										isPlaying = false;
										mProgressHandler.sendEmptyMessage(2);
										return;

									}
									int bIFrame = byteToInt(typebyte);
									int time = byteToInt(timebyte);
									byte[] h264byte = new byte[length];
									in.read(h264byte);
									byte[] yuvbuff = new byte[720 * 1280 * 3 / 2];
									int[] wAndh = new int[2];
									int result = vstc2.nativecaller.NativeCaller
											.DecodeH264Frame(h264byte, 1,
													yuvbuff, length, wAndh);

									if (result > 0) {
										int width = wAndh[0];
										int height = wAndh[1];
										myRender.writeSample(yuvbuff, width,
												height);
										int comsumeTime = (int) ((new Date()
												.getTime() - startTiem));// 解码播放完一帧的时间
										int sleepTime = time - comsumeTime;// 解码之后还需要延时的时间

										if (sleepTime > 0) {
											sumTime += comsumeTime;
											int count = sleepTime / 10;
											int remainTime = sleepTime % 10;
											for (int i = 0; i < count; i++) {
												sumTime += 10;
												Thread.sleep(10);
											}
											sumTime += remainTime;
											Thread.sleep(remainTime);
										} else {
											sumTime += time;
										}
									} else {

									}
								}
									break;
								case 2: {
									long startTiem = (new Date()).getTime();
									byte[] lengthBytes = new byte[4];
									in.read(lengthBytes);
									int length = byteToInt(lengthBytes);
									if (length == 0) {

										flag = false;
										isStart = true;
										isPlaying = false;
										mProgressHandler.sendEmptyMessage(2);
										return;
									}
									pici = pici + 1;
									middleFrame = pici;
									if (sumFrame == pici - 1) {

										flag = false;
										isStart = true;
										isPlaying = false;
										mProgressHandler.sendEmptyMessage(2);
										return;

									}

									byte[] timeBytes = new byte[4];
									in.read(timeBytes);//
									int time = byteToInt(timeBytes);
									Log.d("vst", "time:" + time);
									Log.e("vst","length"+length);
									byte[] contentBytes = new byte[length];
									in.read(contentBytes);
									Bitmap bmp = BitmapFactory.decodeByteArray(
											contentBytes, 0,
											contentBytes.length);

									if (bmp != null) {
										Log.e("vst","bmp != null");
										Message message = mPlayHandler
												.obtainMessage();
										message.obj = bmp;
										mPlayHandler.sendMessage(message);
									}
									int comsumeTime = (int) ((new Date()
											.getTime() - startTiem));
									int sleepTime = time - comsumeTime;
									if (sleepTime > 0) {
										sumTime += comsumeTime;
										int count = sleepTime / 10;
										int remainTime = sleepTime % 10;
										for (int i = 0; i < count; i++) {
											sumTime += 10;
											Thread.sleep(10);
										}
										sumTime += remainTime;
										Thread.sleep(remainTime);
									} else {
										sumTime += time;
									}

								}
								default:
									break;
								}
							} else {
								Log.d("tag", "wait 1");
								isPause = true;
								ShowLocalVideoActivity.this.wait();
								Log.d("tag", "wait 2");
								isPause = false;
								mProgressHandler.postDelayed(myProRunnable, 0);
							}
						}
					}
					Log.d("tag", "");
				} catch (Exception e) {
					Log.d("tag", "" + e.getMessage());
				} finally {
					if (in != null) {
						try {
							in.close();
							in = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	};

	/**
	 *
	 * */
	private void pVideo() {
		synchronized (ShowLocalVideoActivity.this) {
			ShowLocalVideoActivity.this.notifyAll();
		}
	}

	/** ֡
	 * **/
	private void firstPicture() {
		if (filePath != null) {
			new Thread() {
				public void run() {
					File file = new File(filePath);
					FileInputStream in = null;
					try {

						in = new FileInputStream(file);
						int fileSumLength = in.available();
						byte[] header = new byte[4];
						int read = in.read(header);
						int fType = byteToInt(header);
						Log.d("tag", "fType:" + fType);
						switch (fType) {
						case 1: {// h264
							in.skip(fileSumLength - 16);
							in.skip(4);
							byte[] sumF = new byte[4];
							in.read(sumF);
							int SumFrame = byteToInt(sumF);

							sumFrame = SumFrame;
							// in.skip(4);

							byte[] sumB = new byte[4];
							in.read(sumB);
							videoSumTime = byteToInt(sumB);
							Log.d("tag", "  videoSumTime:"
									+ videoSumTime);
							mHandler.sendEmptyMessage(2);
						}
							isView = true;
							break;
						case 2: {// jpg

							isView = false;
							byte[] lengthBytes = new byte[4];
							byte[] timeBytes = new byte[4];
							in.read(lengthBytes);
							in.read(timeBytes);
							int length = byteToInt(lengthBytes);
							int time = byteToInt(timeBytes);

							byte[] contentBytes = new byte[length];
							in.read(contentBytes);
							Bitmap bmp = BitmapFactory.decodeByteArray(
									contentBytes, 0, contentBytes.length);
							Message message = mPlayHandler.obtainMessage();
							message.obj = bmp;
							mPlayHandler.sendMessage(message);

							//
							int need = fileSumLength - (length + 16+4);


							in.skip(need);

							byte[] sumF = new byte[4];
							in.read(sumF);
							int SumFrame = byteToInt(sumF);
							Log.e("vst","SumFrame:" + SumFrame);
							sumFrame = SumFrame;


							byte[] sumB = new byte[4];
							in.read(sumB);

							// test
							// sum = byteToInt(sumB);
							// int remaider=sum%5;
							// if(remaider>0){
							// videoSumTime=sum/5+1;
							// }else{
							// videoSumTime = sum/5;
							// }
							// test
							videoSumTime = byteToInt(sumB);
							Log.d("tag", "videoSumTime:"
									+ videoSumTime);
							mHandler.sendEmptyMessage(2);
						}
						default:
							break;
						}
					} catch (Exception e) {
						Log.d("tag", " " + e.getMessage());
					} finally {
						if (in != null) {
							try {
								in.close();
								in = null;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}.start();
		}
	}

	/**
	 * handler
	 * */
	private Handler mPlayHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (!isView) {
				myGLSurfaceView.setVisibility(View.GONE);
			}
			Bitmap bmp = (Bitmap) msg.obj;
			if (bmp == null) {
				Log.d("tag", "play this picture failed");
				return;
			}
			if(img.getVisibility() == View.GONE)
			{
				img.setVisibility(View.VISIBLE);
			}
			img.setImageBitmap(bmp);
		}
	};

	private String mess(String mess) {
		String d = mess.substring(0, 10);
		String dd = mess.substring(10, 11);
		String de = mess.substring(11, 19);
		String dee = de.replace(dd, ":");
		String ddee = d + " " + dee;
		return ddee;
	}

	
	public static boolean isLeapYear(String date) {

		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 3.能被4整除同时能被100整除则不是闰年
		 */
		Date d = strToDate(date);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(d);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) {
			if ((year % 100) == 0)
				return false;
			else
				return true;
		} else
			return false;
	}
	
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
	private int old_secs, old_mins, old_hours, old_days, old_months, old_years;

	private String showTime(int time) {
		String year = mess.substring(0, 4);
		String month = mess.substring(5, 7);
		String day = mess.substring(8, 10);
		String hour = mess.substring(11, 13);
		String min = mess.substring(14, 16);
		String sec = mess.substring(17, 19);
	
		old_secs = Integer.parseInt(sec);
		old_mins = Integer.parseInt(min);
		old_hours = Integer.parseInt(hour);

		old_days = Integer.parseInt(day);
		old_months = Integer.parseInt(month);
		old_years = Integer.parseInt(year);

		int new_second = time % 60;
		int new_minute = time / 60;
		int new_hour = new_minute / 60;

		String strSecond = "";
		String strMinute = "";
		String strHour = "";

		if ((old_secs + new_second) < 10) {
			strSecond = "0" + (old_secs + new_second);
		} else if ((old_secs + new_second) >= 60) {
			if ((old_secs + new_second) - 60 >= 10) {
				strSecond = "" + ((old_secs + new_second) - 60);
			} else {
				strSecond = "0" + ((old_secs + new_second) - 60);
			}
			old_mins = old_mins + 1;
		} else {
			strSecond = "" + (old_secs + new_second);
		}

		if ((old_mins + new_minute) < 10) {
			strMinute = "0" + (old_mins + new_minute);
		} else if ((old_mins + new_minute) >= 60) {
			if ((old_mins + new_minute) - 60 >= 10) {
				strMinute = "" + ((old_mins + new_minute) - 60);
			} else {
				strMinute = "0" + ((old_mins + new_minute) - 60);
			}
			old_hours = old_hours + 1;
		} else {
			strMinute = "" + (old_mins + new_minute);
		}

		if ((old_hours + new_hour) < 10) {
			strHour = "0" + (old_hours + new_hour);
		} else if ((old_hours + new_hour) >= 24) {
			strHour = "00";
			if (old_months == 1 || old_months == 3 || old_months == 5
					|| old_months == 7 || old_months == 8 || old_months == 10
					|| old_months == 12) {
				if (old_days == 31) {
					old_days = 1;
					old_months = old_months + 1;
				} else {
					old_days = old_days + 1;
				}
			} else if (old_months == 4 || old_months == 6 || old_months == 9
					|| old_months == 11) {
				if (old_days == 30) {
					old_days = 1;
					old_months = old_months + 1;
				} else {
					old_days = old_days + 1;
				}

			} else {
				if (isLeapYear(year + "-" + month + "-" + day)) {
					if (old_days == 29) {
						old_days = 1;
						old_months = old_months + 1;
					} else {
						old_days = old_days + 1;
					}
				} else {
					if (old_days == 28) {
						old_days = 1;
						old_months = old_months + 1;
					} else {
						old_days = old_days + 1;
					}
				}
			}
		} else {
			strHour = "" + (old_hours + new_hour);
		}

		return year + "-"
				+ ((old_months < 10) ? ("0" + old_months) : (old_months)) + "-"
				+ ((old_days < 10) ? ("0" + old_days) : (old_days)) + " "
				+ strHour + ":" + strMinute + ":" + strSecond;
	}

	/***
	 * handler
	 * **/
	private Handler mProgressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				seekBar.setProgress(progress);
				System.out.println(">>>>>>>>>>>>>>>>>>>" + progress);
				tvCurrentTime.setText(getTime(progress));
				showVideoTime.setText(showTime(progress));
				System.out.println(">>>>>>>>>>>>>>>>" + getTime(progress));
				break;
			case 2:
				imgPause.setVisibility(View.GONE);
				btnPlay.setBackgroundResource(R.drawable.video_play_pause_selector);
				finish();
				break;
			default:
				break;
			}

		}
	};
	private int position;
	private PlayThread playThread;

	private Runnable myProRunnable = new Runnable() {

		@Override
		public void run() {
			if (!flag) {
				return;
			}
			if (seekBar.getProgress() != seekBar.getMax()) {	
				//seekBar.setProgress(sumTime);
				if(sumFrame == 0)
				{
					mProgressHandler.postDelayed(myProRunnable, 300);
					return;
				}
				int time = (int)(middleFrame*(videoSumTime)/1000)/sumFrame;
				seekBar.setProgress(time*1000);
				tvCurrentTime.setText(getTime(time ));
				showVideoTime.setText(showTime(time ));
				mProgressHandler.postDelayed(myProRunnable, 300);
				// mProgressHandler.sendEmptyMessage(1);
			}
		}

	};
	private GLSurfaceView myGLSurfaceView;
	private MyRender myRender;
	private int ht;
	private int wh;

	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();//
			temp = temp >> 8;//
		}
		return b;
	}

	public static int byteToInt(byte[] b) {
		int s = 0;
		int s0 = b[0] & 0xff;// 
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}

	public static byte[] longToByte(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();//
			temp = temp >> 8;// 
		}
		return b;

	}

	public static long byteToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;//
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;//
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff; //
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			flag = false;
			finish();
			break;
		case R.id.tv_back:
			flag = false;
			finish();
			break;
		case R.id.btn_left:
			//if (position < arrayList.size() - 1) {
			//	playNextFile(position++);
			//} else {
			//	showToast(R.string.playvideo_no_next);
			//}
			break;
		case R.id.btn_right:
			if (position > 0) {
				playNextFile(position--);
			} else {
				
			}
			break;
		case R.id.btn_play:
			if (isPlaying) {
				btnPlay.setBackgroundResource(R.drawable.video_play_pause_selector);
				imgPause.setVisibility(View.GONE);
				Log.d("tag", "pause");
				isPlaying = false;
			} else {
				btnPlay.setBackgroundResource(R.drawable.video_play_play_selector);
				imgPause.setVisibility(View.GONE);
				if (isStart) {//
					startVideo();
				} else {
					isPlaying = true;
					pVideo();
				}
			}
			break;
		default:
			break;
		}

	}

	private void playNextFile(int pos) {
		flag = false;
		isStart = true;
		//Map<String, Object> map = arrayList.get(position);
		//filePath = (String) map.get("path");

		//mess = timeList.get(position);
		videoTime = mess(mess);

		tvTime.setText(getResources().getString(R.string.local_video_date)
				+ getContent(filePath));
		firstPicture();
		startVideo();

	}

	private Toast mToast;

	public void showToast(int rid) {
		String text = getResources().getString(rid);

		if (mToast == null) {
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}

		mToast.show();
	}

	private void startVideo() {
		Log.d("tag", "");
		if (playThread != null) {
			playThread.interrupt();
			playThread = null;
		}
		imgPause.setVisibility(View.GONE);
		isStart = false;
		isPlaying = true;
		progress = 0;
		seekBar.setProgress(progress);
		tvCurrentTime.setText(getTime(progress));
		playThread = new PlayThread();
		playThread.start();
	}

	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		if (arg0 == KeyEvent.KEYCODE_BACK) {
			flag = false;
			finish();
			return true;
		}
		return super.onKeyDown(arg0, arg1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return gt.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		if (isShowing) {
			isShowing = false;
			topLayout.setVisibility(View.GONE);
			bottomLayout.setVisibility(View.GONE);
		} else {
			isShowing = true;
			topLayout.setVisibility(View.VISIBLE);
			bottomLayout.setVisibility(View.VISIBLE);
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
						   float arg3) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
							float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		Log.d("tag", "onDoubleTap");

		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		Log.d("tag", "onSingleTapConfirmed");

		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.top:
			return true;
		case R.id.bottom:
			return true;
		case R.id.img_playvideo:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (isShowing) {
					isShowing = false;
					topLayout.setVisibility(View.GONE);
					bottomLayout.setVisibility(View.GONE);
				} else {
					isShowing = true;
					topLayout.setVisibility(View.VISIBLE);
					bottomLayout.setVisibility(View.VISIBLE);
				}
				break;

			default:
				break;
			}

			return true;
		default:
			break;
		}

		return false;
	}
}