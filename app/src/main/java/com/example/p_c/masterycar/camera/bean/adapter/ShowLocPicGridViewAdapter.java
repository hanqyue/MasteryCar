package com.example.p_c.masterycar.camera.bean.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.bean.utils.DatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowLocPicGridViewAdapter extends BaseAdapter {
	private Context context;
	private String did;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private int mode = 1;// 连接模式
	private ArrayList<Map<String, Object>> arrayList;

	public ShowLocPicGridViewAdapter(Context context, String did) {
		this.context = context;
		this.did = did;
		arrayList = new ArrayList<Map<String, Object>>();
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		String path = arrayList.get(position).get("path").toString();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.showlocalpicgrid_griditem,
					null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.playvideo = (ImageView) convertView
					.findViewById(R.id.playvideo);
			holder.img_delHook = (ImageView) convertView
					.findViewById(R.id.del_hook);
			holder.baFlag = (TextView) convertView
					.findViewById(R.id.tvbadfileflag);
			holder.textView_timeshow = (TextView) convertView
					.findViewById(R.id.locVidTimeShow);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, Object> map = arrayList.get(position);
		Bitmap bmp = (Bitmap) map.get("bmp");
		int status = (Integer) map.get("status");
		int type = (Integer) map.get("type");
		if (type == 1) {
			holder.baFlag.setVisibility(View.VISIBLE);
		} else {
			holder.baFlag.setVisibility(View.GONE);
		}
		Log.d("tag", "adapter  status:" + status + " position:" + position);
		switch (status) {
		case 0:
		holder.img_delHook.setVisibility(View.GONE);
			holder.img.setPadding(2, 2, 2, 2);
			holder.img.setBackgroundColor(0x00ff0000);
			break;
		case 1:
			holder.img_delHook.setVisibility(View.VISIBLE);
			holder.img.setPadding(2, 2, 2, 2);
			holder.img.setBackgroundColor(0xffff0000);
			break;
		default:
			break;
		}
		switch (mode) {
		case 1:
			holder.playvideo.setVisibility(View.GONE);
			break;
		case 2:
			holder.playvideo.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		holder.img.setImageBitmap(bmp);
		holder.textView_timeshow.setText(getContent(path));
		return convertView;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public ArrayList<Map<String, Object>> getArrayPics() {
		return arrayList;
	}

	public void clearAll() {
		arrayList.clear();
	}
	private String getContent(String filePath) {
		Log.d("tag", "filePath:" + filePath);
		String s = filePath.substring(filePath.lastIndexOf("/") + 1);
		//String date = s.substring(0, 10);

		String time = s.substring(11, 16).replace("_", ":");
		String result = time;
		Log.d("tag", "result:" + result);
		Log.d("tag", "sss:" + s.substring(0, 16));
		return result;
	}

	public ArrayList<Map<String, Object>> DelPics() {
		DatabaseUtil dbUtil = new DatabaseUtil(context);
		dbUtil.open();
		ArrayList<String> delArray = new ArrayList<String>();
		for (int i = 0; i < arrayList.size(); i++) {
			Map<String, Object> map = arrayList.get(i);
			String path = (String) map.get("path");
			int status = (Integer) map.get("status");
			if (status == 1) {
				Log.d("tag", "path:" + path);
				delArray.add(path);
			}
		}

		int size = delArray.size();
		Log.d("tag", "delArray.size():" + size);

		for (int i = 0; i < size; i++) {
			String path = delArray.get(i);
			Log.d("tag", "");
			boolean flag = true;
			for (int j = 0; j < arrayList.size() && flag; j++) {
				Map<String, Object> map = arrayList.get(j);
				String path2 = (String) map.get("path");
				if (path.equals(path2)) {
					Log.d("tag", "");
					String type = "";
					switch (mode) {
					case 1:
						type = DatabaseUtil.TYPE_PICTURE;
						break;
					case 2:
						type = DatabaseUtil.TYPE_VIDEO;
						break;
					}
					if (dbUtil.deleteVideoOrPicture(did, path2, type)) {

						File file = new File(path2);
						if (file != null && file.exists()) {
							Log.d("tag", "");
							file.delete();

						}
						map.clear();
						arrayList.remove(j);
					}
					flag = false;
				}
			}
		}
		dbUtil.close();
		delArray.clear();
		delArray = null;
		Log.d("tag", "DelPics end");
		return arrayList;
	}

	public void addBitmap(Bitmap bitmap, String path, int type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bmp", bitmap);
		map.put("path", path);
		map.put("status", 0);
		map.put("type", type);
		arrayList.add(map);
	}

	// private Bitmap getBitmap(String path) {
	//
	// switch (mode) {
	// case 1: {// ͼƬ
	// holder.playvideo.setVisibility(View.GONE);
	// Bitmap btp = BitmapFactory.decodeFile(path);
	// int btmWidth = btp.getWidth();
	// int btmHeight = btp.getHeight();
	// float scaleW = ((float) 140) / btmWidth;
	// float scaleH = ((float) 120) / btmHeight;
	// Matrix matrix = new Matrix();
	// matrix.postScale(scaleW, scaleH);
	// Bitmap bt = Bitmap.createBitmap(btp, 0, 0, btmWidth, btmHeight,
	// matrix, true);
	//
	// return bt;
	// }
	// case 2://
	// holder.playvideo.setVisibility(View.VISIBLE);
	// File file = new File(path);
	// FileInputStream in = null;
	// try {
	// in = new FileInputStream(file);
	// Log.d("tag", ":" + in.available());
	// byte[] lengthBytes = new byte[4];
	// byte[] timeBytes = new byte[4];
	// in.read(lengthBytes);
	// in.read(timeBytes);
	// int time = byteToInt(timeBytes);
	// int length = byteToInt(lengthBytes);
	// Log.d("tag", "time:" + time);
	// Log.d("tag", "length:" + length);
	// byte[] contentBytes = new byte[length];
	// in.read(contentBytes);
	// Bitmap btp = BitmapFactory.decodeByteArray(contentBytes, 0,
	// contentBytes.length);
	// if (btp != null) {
	// Matrix matrix = new Matrix();
	// float scaleX = ((float) 140) / btp.getWidth();
	// float scaleY = ((float) 120) / btp.getHeight();
	// Log.d("tag", "sx:" + scaleX + " sy:" + scaleY);
	// matrix.postScale(scaleX, scaleY);
	// return Bitmap.createBitmap(btp, 0, 0, btp.getWidth(),
	// btp.getHeight(), matrix, true);
	// } else {
	// return null;
	// }
	//
	// } catch (Exception e) {
	//
	// } finally {
	// if (in != null) {
	// try {
	// in.close();
	// in = null;
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// break;
	// }
	// return null;
	//
	// }

	// private class MyTouch implements OnTouchListener{
	// private int position;
	// public MyTouch(int position){
	// this.position=position;
	// }
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	//
	// return false;
	// }
	//
	// }
	private class ViewHolder {
		ImageView img;
		ImageView playvideo;
		ImageView img_delHook;
		TextView baFlag;
		TextView textView_timeshow;
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
}