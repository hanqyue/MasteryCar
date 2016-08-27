package com.example.p_c.masterycar.camera.bean.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.camera.bean.LocalPictureAndVideoActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// 
public class LocalPictureAndVideoAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	
	private int width;
	private ViewHolder holder = null;
	private HashMap<String, Bitmap> mapBmp = null;
	private boolean isOver = false;
	private ArrayList<LocalPictureAndVideoActivity.MyItem> items;
	
	
	public LocalPictureAndVideoAdapter(Context c, ArrayList<LocalPictureAndVideoActivity.MyItem> items, int wh)
	{
		inflator = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.width = wh;
		this.items = items;
		mapBmp = new HashMap<String, Bitmap>();
		initBmp();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				isOver = true;
				break;
			default:
				break;
			}
			notifyDataSetChanged();
		}
	};
	
	public void initBmp() {
		new Thread() {
			public void run() {
				
				for (int i = 0; i < items.size(); i++) {
					LocalPictureAndVideoActivity.MyItem myItem = items.get(i);
					String data = myItem.data;
					int type = myItem.type;
					Bitmap bmp = null;
					if(type == 1){
						Bitmap btp = BitmapFactory.decodeFile(myItem.paths.get(0));
						
						if(btp == null)
						{
							items.remove(i);
							continue;
						}
					
						Matrix matrix = new Matrix();
						float scaleX = ((float) width) / btp.getWidth();
						float scaleY = ((float) width) / btp.getHeight();
						matrix.postScale(scaleX, scaleY);
						bmp = Bitmap.createBitmap(btp, 0, 0,
								btp.getWidth(), btp.getHeight(), matrix, true);
							
					}else if(type == 2){
						bmp = getBitmap(myItem.paths.get(0));
					}
					mapBmp.put(data, bmp);
					handler.sendEmptyMessage(2);
				}
//				handler.sendEmptyMessage(2);
				
			}
		}.start();
	}
	

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items==null?0:items.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		
		int type = items.get(arg0).type;
		
		if (convertView == null) {
			convertView = inflator
					.inflate(R.layout.localpicture_listitem, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.sum = (TextView) convertView.findViewById(R.id.sum);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.unit=(TextView) convertView.findViewById(R.id.zhang);
			holder.playvideo = (ImageView) convertView
					.findViewById(R.id.playvideo);
			holder.pBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.playvideo.setVisibility(View.VISIBLE);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, width);
		holder.img.setLayoutParams(lp);
		holder.playvideo.setVisibility(View.GONE);
		if(type == 1){			
			holder.pBar.setVisibility(View.GONE);
		}else if(type ==2){
			holder.pBar.setVisibility(View.GONE);
			holder.playvideo.setVisibility(View.VISIBLE);
		}
		
		String time = items.get(arg0).data;
		List<String> list = items.get(arg0).paths;
		holder.time.setText(time);
		holder.sum.setText(list.size() + "");
		if(type==2){
			holder.unit.setText(R.string.video_sum);
		}else{
			holder.unit.setText(R.string.picture_sum);
		}
		Bitmap bitmap = mapBmp.get(time);
		if (bitmap != null) {
			holder.img.setImageBitmap(bitmap);
		}
		return convertView;
	}
	
	private class ViewHolder {
		TextView unit;
		ImageView img;
		TextView time;
		TextView sum;
		ImageView playvideo;
		ProgressBar pBar;
	}
	
	//bitmap
		private Bitmap getBitmap(String path) {
			File file = new File(path);
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				//
				byte[] header = new byte[4];
				in.read(header);
				int fType = byteToInt(header);
				Log.d("tag", "fType:" + fType);
				switch (fType) {
				case 1: {// h264
					byte[] sizebyte = new byte[4];
					byte[] typebyte = new byte[4];
					byte[] timebyte = new byte[4];
					in.read(sizebyte);
					in.read(typebyte);
					in.read(timebyte);
					int length = byteToInt(sizebyte);
					int bIFrame = byteToInt(typebyte);
					int time = byteToInt(timebyte);
					byte[] h264byte = new byte[length];
					in.read(h264byte);
					byte[] yuvbuff = new byte[720 * 1280 * 3 / 2];
					int[] wAndh = new int[2];
					int result = vstc2.nativecaller.NativeCaller.DecodeH264Frame(h264byte, 1, yuvbuff,
							length, wAndh);
					if (result > 0) {
						Log.d("tag", "h264");
						int width = wAndh[0];
						int height = wAndh[1];
						Log.d("tag", "width:" + width + " height:" + height);
						byte[] rgb = new byte[width * height * 2];
						vstc2.nativecaller.NativeCaller.YUV4202RGB565(yuvbuff, rgb, width, height);
						ByteBuffer buffer = ByteBuffer.wrap(rgb);
						Bitmap bitmap = Bitmap.createBitmap(width, height,
								Bitmap.Config.RGB_565);
						bitmap.copyPixelsFromBuffer(buffer);
						Matrix matrix = new Matrix();
						float scaleX = ((float) width) / bitmap.getWidth();
						float scaleY = ((float) width) / bitmap.getHeight();
						matrix.postScale(scaleX, scaleY);
						return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
								bitmap.getHeight(), matrix, true);
					} else {
						Log.d("tag", "h264");
						return null;
					}
				}
				case 2: {// jpg
					byte[] lengthBytes = new byte[4];
					byte[] timeBytes = new byte[4];
					in.read(lengthBytes);
					in.read(timeBytes);
					int time = byteToInt(timeBytes);
					int length = byteToInt(lengthBytes);
					byte[] contentBytes = new byte[length];
					in.read(contentBytes);
					Bitmap btp = BitmapFactory.decodeByteArray(contentBytes, 0,
							contentBytes.length);
					if (btp != null) {
						Matrix matrix = new Matrix();
						float scaleX = ((float) width) / btp.getWidth();
						float scaleY = ((float) width) / btp.getHeight();
						matrix.postScale(scaleX, scaleY);
						return Bitmap.createBitmap(btp, 0, 0, btp.getWidth(),
								btp.getHeight(), matrix, true);
					} else {
						return null;
					}
				}
				default:
					return null;
				}

			} catch (Exception e) {

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

			return null;
		}
		
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


}
