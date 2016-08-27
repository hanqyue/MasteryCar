package com.example.p_c.masterycar.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.adapter.ShowLocPicGridViewAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * */
public class LocalVideoGridActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener, OnItemLongClickListener {
	private String strDID;
	private String strDate;
	private ArrayList<String> aList;
	private ArrayList<String> videoTime;
	private ArrayList<Map<String, Object>> arrayList;
	private Button btnBack;
	private Button btnEdit;
	private TextView tvTakePicTime;
	private TextView tvSelectSum;
	private GridView gridView;
	private String strCameraName;
	private LinearLayout layoutDel;
	private TextView tvNoVideo;
	private Button btnSelectAll;
	private Button btnSelectReverse;
	private Button btnDel;
	private boolean isEditing = false;
	private int position = -1;
	private int seletNum;
	private ShowLocPicGridViewAdapter mAdapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDataFromOther();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showlocalpicgrid);
		findView();
		setListener();
		tvTakePicTime.setText(strDate + "/" + arrayList.size());
		mAdapter = new ShowLocPicGridViewAdapter(this, strDID);
		mAdapter.setMode(2);// 
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);
	
		initBmp();
	}

	private void initBmp() {
		Log.d("info", "LocalVideoGridActivity  initBmp:");
		new Thread() {
			public void run() {
				for (int i = 0; i < arrayList.size(); i++) {
					Map<String, Object> map = arrayList.get(i);
					String path = (String) map.get("path");
					File file = new File(path);
					FileInputStream in = null;
					try {
						in = new FileInputStream(file);
						byte[] header = new byte[4];
						in.read(header);
						int fType = byteToInt(header);
						Log.d("tag", "fType:" + fType);
						switch (fType) {
						case 1: {// h264
							Log.d("tag", "h264");
							byte[] sizebyte = new byte[4];
							byte[] typebyte = new byte[4];
							byte[] timebyte = new byte[4];
							in.read(sizebyte);
							in.read(typebyte);
							in.read(timebyte);
							int length = byteToInt(sizebyte);
							int bIFrame = byteToInt(typebyte);
							Log.d("tag", "bIFrame:" + bIFrame);
							byte[] h264byte = new byte[length];
							in.read(h264byte);
							byte[] yuvbuff = new byte[720 * 1280 * 3 / 2];
							int[] wAndh = new int[2];
							int result = vstc2.nativecaller.NativeCaller.DecodeH264Frame(h264byte,
									1, yuvbuff, length, wAndh);
							if (result > 0) {
								Log.d("tag", "h264");
								int width = wAndh[0];
								int height = wAndh[1];
								Log.d("tag", "width:" + width + " height:"
										+ height);
								byte[] rgb = new byte[width * height * 2];
								vstc2.nativecaller.NativeCaller.YUV4202RGB565(yuvbuff, rgb, width,
										height);
								ByteBuffer buffer = ByteBuffer.wrap(rgb);
								Bitmap bitmap = Bitmap.createBitmap(width,
										height, Bitmap.Config.RGB_565);
								bitmap.copyPixelsFromBuffer(buffer);
								Matrix matrix = new Matrix();
								float scaleX = ((float) 140)
										/ bitmap.getWidth();
								float scaleY = ((float) 120)
										/ bitmap.getHeight();
								matrix.postScale(scaleX, scaleY);
								bitmap = Bitmap.createBitmap(bitmap, 0, 0,
										bitmap.getWidth(), bitmap.getHeight(),
										matrix, true);
								mAdapter.addBitmap(bitmap, path, 0);
								handler.sendEmptyMessage(1);
							} else {
								Log.d("tag", "h264");
							}
						}
							break;
						case 2: {// jpg
							byte[] lengthBytes = new byte[4];
							byte[] timeBytes = new byte[4];
							in.read(lengthBytes);
							in.read(timeBytes);
							int time = byteToInt(timeBytes);
							int length = byteToInt(lengthBytes);
							byte[] contentBytes = new byte[length];
							in.read(contentBytes);
							Bitmap btp = BitmapFactory.decodeByteArray(
									contentBytes, 0, contentBytes.length);
							if (btp != null) {
								Matrix matrix = new Matrix();
								float scaleX = ((float) 140) / btp.getWidth();
								float scaleY = ((float) 120) / btp.getHeight();
								matrix.postScale(scaleX, scaleY);
								Bitmap bitmap = Bitmap.createBitmap(btp, 0, 0,
										btp.getWidth(), btp.getHeight(),
										matrix, true);
								mAdapter.addBitmap(bitmap, path, 0);
								handler.sendEmptyMessage(1);
							} else {
								Bitmap bmp = BitmapFactory.decodeResource(
										getResources(), R.drawable.bad_video);
								Matrix matrix = new Matrix();
								float scaleX = ((float) 140) / bmp.getWidth();
								float scaleY = ((float) 120) / bmp.getHeight();
								matrix.postScale(scaleX, scaleY);
								Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0,
										bmp.getWidth(), bmp.getHeight(),
										matrix, true);
								mAdapter.addBitmap(bitmap, path, 1);
								handler.sendEmptyMessage(1);
							}
						}
						default:
							break;
						}

					} catch (Exception e) {
						Log.d("tag",
								"LocalViewGrid  initBmp:" + e.getMessage());
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
		}.start();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (arrayList.size() == 0)
		{
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isEditing) {
				seletNum = 0;
				tvSelectSum.setVisibility(View.GONE);
				isEditing = false;
				layoutDel.setVisibility(View.GONE);
				ArrayList<Map<String, Object>> arrayPics = mAdapter
						.getArrayPics();
				for (int i = 0; i < arrayPics.size(); i++) {
					Map<String, Object> map = arrayPics.get(i);
					map.put("status", 0);
				}
				mAdapter.notifyDataSetChanged();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		btnBack.setOnClickListener(this);
		btnSelectAll.setOnClickListener(this);
		btnSelectReverse.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		btnEdit.setOnClickListener(this);

	}

	private void findView() {
		btnBack = (Button) findViewById(R.id.back);
		tvTakePicTime = (TextView) findViewById(R.id.tv_time);
		tvSelectSum = (TextView) findViewById(R.id.tv_select_sum);
		gridView = (GridView) findViewById(R.id.gridView1);
		layoutDel = (LinearLayout) findViewById(R.id.del_bottom_layout);
		tvNoVideo = (TextView) findViewById(R.id.localpic_tv_nopic);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		layout.setBackgroundDrawable(drawable);
		tvNoVideo.setText(getResources().getString(R.string.no_video));
		btnSelectAll = (Button) findViewById(R.id.selectall);
		btnSelectReverse = (Button) findViewById(R.id.selectreverse);
		btnDel = (Button) findViewById(R.id.delete);
		//btnDel.setText(R.string.del_localvid);
		btnEdit = (Button) findViewById(R.id.edit);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra("did");
		strDate = intent.getStringExtra("date");
		strCameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		videoTime = (ArrayList<String>) intent.getSerializableExtra("videotime");
		aList = (ArrayList<String>) intent.getSerializableExtra("list");
		Log.i("info", "videoTime:"+videoTime);
		arrayList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < aList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			String path = aList.get(i);
			map.put("path", path);
			map.put("status", 0);
			arrayList.add(map);
		}
		aList.clear();
		aList = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
							long arg3) {

		if (!isEditing) {
			if (this.position != position) {
				this.position = -1;
				Map<String, Object> map = arrayList.get(position);
				String path = (String) map.get("path");
				Intent intent = new Intent(this, ShowLocalVideoActivity.class);
				intent.putExtra("did", strDID);
				intent.putExtra("filepath", path);
				intent.putExtra("arrayList", arrayList);
				intent.putExtra("position", position);
				intent.putExtra(ContentCommon.STR_CAMERA_NAME, strCameraName);
				intent.putExtra("videotime", videoTime.get(position));
				intent.putExtra("timeList", videoTime);
				startActivityForResult(intent, 2);
				overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			} else {
				this.position = -1;
			}
		} else {
			if (this.position != position) {
				this.position = -1;
				ArrayList<Map<String, Object>> arrayPics = mAdapter
						.getArrayPics();
				Map<String, Object> map = arrayPics.get(position);
				Map<String, Object> map2 = arrayList.get(position);
				int status = (Integer) map.get("status");
				if (status == 0) {
					seletNum++;
					map2.put("status", 1);
					map.put("status", 1);
				} else {
					seletNum--;
					map2.put("status", 0);
					map.put("status", 0);
				}
				tvSelectSum.setText(String.valueOf(seletNum));
				mAdapter.notifyDataSetChanged();
				checkSelect();
			} else {
				this.position = -1;
			}
		}

	}
	private void checkSelect() {
		for (int i = 0; i < arrayList.size(); i++) {
			Map<String, Object> map = arrayList.get(i);
			int status = (Integer) map.get("status");
			if (status == 1) {
				return;
			}
		}
		tvSelectSum.setVisibility(View.GONE);
		layoutDel.setVisibility(View.GONE);
		isEditing = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (isEditing) {
				seletNum = 0;
				tvSelectSum.setVisibility(View.GONE);
				isEditing = false;
				layoutDel.setVisibility(View.GONE);
				ArrayList<Map<String, Object>> arrayPics = mAdapter
						.getArrayPics();
				for (int i = 0; i < arrayPics.size(); i++) {
					Map<String, Object> map = arrayPics.get(i);
					map.put("status", 0);
				}
				mAdapter.notifyDataSetChanged();
			} else {
				finish();
			}
			break;
		case R.id.selectall: {
			ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();

			for (int i = 0; i < arrayPics.size(); i++) {
				Map<String, Object> map = arrayPics.get(i);
				Map<String, Object> map2 = arrayList.get(i);
				int status = (Integer) map.get("status");
				if (status != 1) {
					map2.put("status", 1);
					map.put("status", 1);
				}
			}
			seletNum = arrayPics.size();
			tvSelectSum.setText(String.valueOf(seletNum));
			mAdapter.notifyDataSetChanged();
		}
			break;
		case R.id.selectreverse: {
			ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
			for (int i = 0; i < arrayPics.size(); i++) {
				Map<String, Object> map = arrayPics.get(i);
				Map<String, Object> map2 = arrayList.get(i);
				int status = (Integer) map.get("status");
				switch (status) {
				case 0:
					seletNum++;
					map2.put("status", 1);
					map.put("status", 1);
					break;
				case 1:
					seletNum--;
					map2.put("status", 0);
					map.put("status", 0);
					break;

				default:
					break;
				}
			}
			tvSelectSum.setText(String.valueOf(seletNum));
			mAdapter.notifyDataSetChanged();
		}
			break;
		case R.id.delete: {
			Log.d("tag", "delete");
			seletNum = 0;
			tvSelectSum.setVisibility(View.GONE);
			ArrayList<Map<String, Object>> delPics = mAdapter.DelPics();
			Log.d("tag", "delPics.size:" + delPics.size());
			if (delPics.size() == 0) {
				tvNoVideo.setVisibility(View.VISIBLE);
				isEditing = false;
				layoutDel.setVisibility(View.GONE);
			} else {
				boolean flag = true;
				for (int i = 0; i < delPics.size() && flag; i++) {
					Map<String, Object> map = delPics.get(i);
					int status = (Integer) map.get("status");
					if (status == 1) {
						flag = false;
					}
				}
				if (!flag) {
					isEditing = false;
					layoutDel.setVisibility(View.GONE);
					btnEdit.setText(getResources().getString(R.string.main_edit));
				}
			}
			mAdapter.notifyDataSetChanged();
		}
			break;
		case R.id.edit:
			if(isEditing){
				btnEdit.setText(getResources().getString(R.string.main_edit));
				layoutDel.setVisibility(View.GONE);
				isEditing=false;
			}else{
				btnEdit.setText(getResources().getString(R.string.done));
				layoutDel.setVisibility(View.VISIBLE);
				isEditing=true;
				//layoutDel.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
								   int position, long arg3) {
		this.position = position;
		layoutDel.setVisibility(View.VISIBLE);
		isEditing = true;
		Log.d("tag", "onItemLongClick");
		ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
		Map<String, Object> map = arrayPics.get(position);
		Map<String, Object> map2 = arrayList.get(position);
		int status = (Integer) map.get("status");
		if (status == 0) {
			seletNum++;
			map2.put("status", 1);
			map.put("status", 1);
		} else {
			seletNum--;
			map.put("status", 0);
			map2.put("status", 0);
		}
		tvSelectSum.setText(String.valueOf(seletNum));
		tvSelectSum.setVisibility(View.VISIBLE);
		mAdapter.notifyDataSetChanged();
		checkSelect();
		return false;
	}

	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static int byteToInt(byte[] b) {
		int s = 0;
		int s0 = b[0] & 0xff;
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		arrayList.clear();
		arrayList = null;
	}
}