package com.example.p_c.masterycar.offlinemap;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.example.p_c.masterycar.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * AMapV2地图中简单介绍离线地图下载
 */
public class OfflineMapActivity_Old extends Activity implements
		OfflineMapDownloadListener {
	private OfflineMapManager amapManager = null;// 离线地图下载控制器
	private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
	private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<Object, List<OfflineMapCity>>();// 保存二级目录的市
	private int groupPosition = -1;// 记录一级目录的position
	private int childPosition = -1;// 记录二级目录的position
	private boolean isStart = false;// 判断是否开始下载,true表示开始下载，false表示下载失败
	private boolean[] isOpen;// 记录一级目录是否打开

	ExpandableListView expandableListView;

	// 刚进入该页面时初始化弹出的dialog
	private ProgressDialog initDialog;
	// 长按弹出的dialog
	private Dialog todoDialog;

	private Context context;

	private final static int UPDATE_LIST = 0;
	private final static int DISMISS_INIT_DIALOG = 1;
	private final static int SHOW_INIT_DIALOG = 2;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_LIST:
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				break;
			case DISMISS_INIT_DIALOG:
				initDialog.dismiss();
				initData();
				handler.sendEmptyMessage(UPDATE_LIST);
				break;
			case SHOW_INIT_DIALOG:
				if (initDialog != null) {
					initDialog.show();
				}

				break;
			default:
				break;
			}
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
		// Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		// MapsInitialihenger.sdcardDir =OffLineMapUtils.getSdCacheDir(this);

		setContentView(R.layout.offlinemap_activity_old);
		context = OfflineMapActivity_Old.this;
		initDialog();

		expandableListView = (ExpandableListView) findViewById(R.id.list);
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(adapter);

	}

	/**
	 * 初始化如果已下载的城市多的话，会比较耗时
	 */
	private void initDialog() {

		initDialog = new ProgressDialog(this);
		initDialog.setMessage("正在获取离线城市列表");
		initDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		initDialog.setCancelable(false);
		initDialog.show();

		handler.sendEmptyMessage(SHOW_INIT_DIALOG);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();

				final Handler handler1 = new Handler();
				handler1.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Do Work
						init();
						handler.sendEmptyMessage(DISMISS_INIT_DIALOG);

						handler.removeCallbacks(this);
						Looper.myLooper().quit();
					}
				}, 10);

				Looper.loop();

			}
		}).start();
	}

	/**
	 * 初始化UI布局文件 以及初始化OfflineMapManager
	 */
	private void init() {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		amapManager = new OfflineMapManager(context, this);
		provinceList = amapManager.getOfflineMapProvinceList();

		List<OfflineMapProvince> bigCityList = new ArrayList<OfflineMapProvince>();// 以省格式保存直辖市、港澳、全国概要图
		List<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
		List<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
		List<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图
		for (int i = 0; i < provinceList.size(); i++) {
			OfflineMapProvince offlineMapProvince = provinceList.get(i);
			List<OfflineMapCity> city = new ArrayList<OfflineMapCity>();
			OfflineMapCity aMapCity = getCicy(offlineMapProvince);
			if (offlineMapProvince.getCityList().size() != 1) {
				city.add(aMapCity);
				city.addAll(offlineMapProvince.getCityList());
			} else {
				cityList.add(aMapCity);
				bigCityList.add(offlineMapProvince);
			}
			cityMap.put(i + 3, city);
		}
		OfflineMapProvince title = new OfflineMapProvince();

		title.setProvinceName("概要图");
		provinceList.add(0, title);
		title = new OfflineMapProvince();
		title.setProvinceName("直辖市");
		provinceList.add(1, title);
		title = new OfflineMapProvince();
		title.setProvinceName("港澳");
		provinceList.add(2, title);
		provinceList.removeAll(bigCityList);

		for (OfflineMapProvince aMapProvince : bigCityList) {
			if (aMapProvince.getProvinceName().contains("香港")
					|| aMapProvince.getProvinceName().contains("澳门")) {
				gangaoList.add(getCicy(aMapProvince));
			} else if (aMapProvince.getProvinceName().contains("全国概要图")) {
				gaiyaotuList.add(getCicy(aMapProvince));
			}
		}
		try {
			cityList.remove(4);// 从List集合体中删除香港
			cityList.remove(4);// 从List集合体中删除澳门
			cityList.remove(4);// 从List集合体中删除澳门
		} catch (Throwable e) {
			e.printStackTrace();
		}
		cityMap.put(0, gaiyaotuList);// 在HashMap中第0位置添加全国概要图
		cityMap.put(1, cityList);// 在HashMap中第1位置添加直辖市
		cityMap.put(2, gangaoList);// 在HashMap中第2位置添加港澳
		isOpen = new boolean[provinceList.size()];
	}

	/**
	 * 为列表绑定数据源
	 */
	private void initData() {
		expandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {

					@Override
					public void onGroupCollapse(int groupPosition) {
						;
						isOpen[groupPosition] = false;
					}
				});

		expandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					@Override
					public void onGroupExpand(int groupPosition) {
						isOpen[groupPosition] = true;
					}
				});
		// 设置二级item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				try {

					String name = cityMap.get(groupPosition).get(childPosition)
							.getCity();
					String url = amapManager.getItemByCityName(name).getUrl();
					Log.d("amap-onclick", name + " : " + url);

					// 下载全国概要图、直辖市、港澳离线地图数据
					if (groupPosition == 0 || groupPosition == 1
							|| groupPosition == 2) {
						amapManager.downloadByProvinceName(cityMap
								.get(groupPosition).get(childPosition)
								.getCity());
					}
					// 下载各省的离线地图数据
					else {
						// 下载各省列表中的省份离线地图数据
						if (childPosition == 0) {
							amapManager.downloadByProvinceName(provinceList
									.get(groupPosition).getProvinceName());
						}
						// 下载各省列表中的城市离线地图数据
						else if (childPosition > 0) {
							amapManager.downloadByCityName(cityMap
									.get(groupPosition).get(childPosition)
									.getCity());
						}
					}
				} catch (AMapException e) {
					e.printStackTrace();
					Log.e("离线地图下载", "离线地图下载抛出异常" + e.getErrorMessage());
				}
				// 保存当前正在正在下载省份或者城市的position位置
				if (isStart) {
					OfflineMapActivity_Old.this.groupPosition = groupPosition;
					OfflineMapActivity_Old.this.childPosition = childPosition;
				}

				handler.sendEmptyMessage(UPDATE_LIST);
				return false;
			}
		});

		expandableListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
												   View view, int position, long id) {
						if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
							groupPosition = ExpandableListView
									.getPackedPositionGroup(id);
							childPosition = ExpandableListView
									.getPackedPositionChild(id);

							OfflineMapCity mapCity = cityMap.get(groupPosition)
									.get(childPosition);

							showDialog(mapCity.getCity());

						}
						return false;
					}
				});

	}

	/**
	 * 长按弹出提示框
	 */
	public void showDialog(final String name) {
		Builder builder = new Builder(OfflineMapActivity_Old.this);

		builder.setTitle(name);
		builder.setSingleChoiceItems(new String[] { "暂停(暂停正在下载的)", "继续", "删除",
				"检查更新", "停止" }, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				todoDialog.dismiss();
				if (amapManager == null) {
					return;
				}
				switch (arg1) {
				case 0:
					amapManager.pause();
					break;
				case 1:
					try {
						amapManager.downloadByCityName(name);
					} catch (AMapException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 2:
					amapManager.remove(name);
					break;
				case 3:
					try {
						amapManager.updateOfflineCityByName(name);
					} catch (AMapException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 4:
					amapManager.stop();
					break;

				default:
					break;
				}
			}
		});
		builder.setNegativeButton("取消", null);
		todoDialog = builder.create();
		todoDialog.show();
	}

	/**
	 * 把一个省的对象转化为一个市的对象
	 */
	public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
		OfflineMapCity aMapCity = new OfflineMapCity();
		aMapCity.setCity(aMapProvince.getProvinceName());
		aMapCity.setSize(aMapProvince.getSize());
		aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
		aMapCity.setState(aMapProvince.getState());
		aMapCity.setUrl(aMapProvince.getUrl());
		return aMapCity;
	}

	// 一些可能会用到的方法
	/**
	 * 暂停所有下载和等待
	 */
	private void stopAll() {
		if (amapManager != null) {
			amapManager.stop();
		}
	}

	/**
	 * 继续下载所有暂停中
	 */
	private void startAllInPause() {
		if (amapManager == null) {
			return;
		}
		for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
			if (mapCity.getState() == OfflineMapStatus.PAUSE) {
				try {
					amapManager.downloadByCityName(mapCity.getCity());
				} catch (AMapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 取消所有<br>
	 * 即：删除下载列表中除了已完成的所有<br>
	 * 会在OfflineMapDownloadListener.onRemove接口中回调是否取消（删除）成功
	 */
	private void cancelAll() {
		if (amapManager == null) {
			return;
		}
		for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
			if (mapCity.getState() == OfflineMapStatus.PAUSE) {
				amapManager.remove(mapCity.getCity());
			}
		}
	}

	final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {

		@Override
		public int getGroupCount() {
			return provinceList.size();
		}

		/**
		 * 获取一级标签内容
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return provinceList.get(groupPosition).getProvinceName();
		}

		/**
		 * 获取一级标签的ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 获取一级标签下二级标签的总数
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return cityMap.get(groupPosition).size();
		}

		/**
		 * 获取一级标签下二级标签的内容
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return cityMap.get(groupPosition).get(childPosition).getCity();
		}

		/**
		 * 获取二级标签的ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * 指定位置相应的组视图
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * 对一级标签进行设置
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			TextView group_text;
			ImageView group_image;
			if (convertView == null) {
				convertView = (RelativeLayout) RelativeLayout.inflate(
						getBaseContext(), R.layout.offlinemap_group_old, null);
			}
			group_text = (TextView) convertView.findViewById(R.id.group_text);
			group_image = (ImageView) convertView
					.findViewById(R.id.group_image);
			group_text.setText(provinceList.get(groupPosition)
					.getProvinceName());
			if (isOpen[groupPosition]) {
				group_image.setImageDrawable(getResources().getDrawable(
						R.drawable.downarrow));
			} else {
				group_image.setImageDrawable(getResources().getDrawable(
						R.drawable.rightarrow));
			}
			return convertView;
		}

		/**
		 * 对一级标签下的二级标签进行设置
		 */
		@Override
		public View getChildView(final int groupPosition,
								 final int childPosition, boolean isLastChild, View convertView,
								 ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = (RelativeLayout) RelativeLayout.inflate(
						getBaseContext(), R.layout.offlinemap_child_old, null);

				holder.cityName = (TextView) convertView
						.findViewById(R.id.name);
				holder.citySize = (TextView) convertView
						.findViewById(R.id.name_size);
				holder.cityDown = (TextView) convertView
						.findViewById(R.id.download_progress_status);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.cityName.setText(cityMap.get(groupPosition)
					.get(childPosition).getCity());
			holder.citySize.setText((cityMap.get(groupPosition).get(
					childPosition).getSize())
					/ (1024 * 1024f) + "MB");

			OfflineMapCity mapCity = cityMap.get(groupPosition).get(
					childPosition);
			// 通过getItem方法获取最新的状态
			if (groupPosition == 0 || groupPosition == 1 || groupPosition == 2) {
				// 全国，直辖市，港澳，按照城市处理
				mapCity = amapManager.getItemByCityName(mapCity.getCity());
			} else {
				if (childPosition == 0) {
					// 省份
					mapCity = getCicy(amapManager.getItemByProvinceName(mapCity
							.getCity()));
				} else {
					// 城市
					mapCity = amapManager.getItemByCityName(mapCity.getCity());
				}
			}
			int state = mapCity.getState();
			int completeCode = mapCity.getcompleteCode();
			if (state == OfflineMapStatus.SUCCESS) {
				holder.cityDown.setText("安装完成");
			} else if (state == OfflineMapStatus.LOADING) {
				holder.cityDown.setText("正在下载" + completeCode + "%");
			} else if (state == OfflineMapStatus.WAITING) {
				holder.cityDown.setText("等待中");
			} else if (state == OfflineMapStatus.UNZIP) {
				holder.cityDown.setText("正在解压" + completeCode + "%");
			} else if (state == OfflineMapStatus.LOADING) {
				holder.cityDown.setText("下载");
			} else if (state == OfflineMapStatus.PAUSE) {
				holder.cityDown.setText("暂停中");
			} else {
				holder.cityDown.setText("未下载");
			}
			return convertView;
		}

		class ViewHolder {
			TextView cityName;
			TextView citySize;
			TextView cityDown;
		}

		/**
		 * 当选择子节点的时候，调用该方法
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (amapManager != null) {
			amapManager.destroy();
		}
	}

	private void logList() {
		ArrayList<OfflineMapCity> list = amapManager.getDownloadingCityList();

		for (OfflineMapCity offlineMapCity : list) {
			Log.i("amap-city-loading: ", offlineMapCity.getCity() + ","
					+ offlineMapCity.getState());
		}

		ArrayList<OfflineMapCity> list1 = amapManager
				.getDownloadOfflineMapCityList();

		for (OfflineMapCity offlineMapCity : list1) {
			Log.i("amap-city-loaded: ", offlineMapCity.getCity() + ","
					+ offlineMapCity.getState());
		}
	}

	/**
	 * 离线地图下载回调方法
	 */
	@Override
	public void onDownload(int status, int completeCode, String downName) {

		switch (status) {
		case OfflineMapStatus.SUCCESS:
			break;
		case OfflineMapStatus.LOADING:
			Log.d("amap-download", "download: " + completeCode + "%" + ","
					+ downName);
			break;
		case OfflineMapStatus.UNZIP:
			Log.d("amap-unzip", "unzip: " + completeCode + "%" + "," + downName);
			break;
		case OfflineMapStatus.WAITING:
			break;
		case OfflineMapStatus.PAUSE:
			Log.d("amap-unzip", "pause: " + completeCode + "%" + "," + downName);
			break;
		case OfflineMapStatus.STOP:
			break;
		case OfflineMapStatus.ERROR:
			Log.e("amap-download", "download: " + " ERROR " + downName);
			break;
		case OfflineMapStatus.EXCEPTION_AMAP:
			Log.e("amap-download", "download: " + " EXCEPTION_AMAP " + downName);
			break;
		case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
			Log.e("amap-download", "download: " + " EXCEPTION_NETWORK_LOADING "
					+ downName);
			Toast.makeText(OfflineMapActivity_Old.this, "网络异常", Toast.LENGTH_SHORT).show();
			amapManager.pause();
			break;
		case OfflineMapStatus.EXCEPTION_SDCARD:
			Log.e("amap-download", "download: " + " EXCEPTION_SDCARD "
					+ downName);
			break;
		default:
			break;
		}
		handler.sendEmptyMessage(UPDATE_LIST);

	}

	@Override
	public void onCheckUpdate(boolean hasNew, String name) {
		Log.i("amap-demo", "onCheckUpdate " + name + " : " + hasNew);

	}

	@Override
	public void onRemove(boolean success, String name, String describe) {
		Log.i("amap-demo", "onRemove " + name + " : " + success + " , "
				+ describe);
		handler.sendEmptyMessage(UPDATE_LIST);
	}
}