package com.example.p_c.masterycar.GMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.Util.AMapUtil;
import com.example.p_c.masterycar.Util.ToastUtil;


/**
 * Created by 李思言 on 2016/4/17.
 */
public class RouteActivity extends Activity implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener, View.OnClickListener {
    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint = new LatLonPoint(39.917636, 116.397743);//起点，
    private LatLonPoint mEndPoint = new LatLonPoint(39.984947, 116.494689);//终点，
    private final int ROUTE_TYPE_DRIVE = 2;
  //  private LinearLayout mBusResultLayout;
    private RelativeLayout mBottomLayout;
    private TextView mRotueTimeDes, mRouteDetailDes;
    private ImageView mDrive;
    private TextView route_nv;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private double endLat;
    private double endLon;
    private double startLat;
    private double startLon;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.route_activity);

        startLat= (double) getIntent().getExtras().get("MyLat");
        startLon=(double)getIntent().getExtras().get("MyLon");
        endLat= (double) getIntent().getExtras().get("Lat_data");
        endLon=(double) getIntent().getExtras().get("Lon_data");
        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写

        init();
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        setfromandtoMarker();
    }

    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        mStartPoint.setLatitude(startLat);
        mStartPoint.setLongitude(startLon);
        mEndPoint.setLongitude(endLon);
        mEndPoint.setLatitude(endLat);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        mDrive = (ImageView)findViewById(R.id.route_drive);
        route_nv= (TextView) findViewById(R.id.route_navi);
        route_nv.setOnClickListener(this);
        mStartPoint.setLatitude(startLat);
        mStartPoint.setLongitude(startLon);
        mEndPoint.setLatitude(endLat);
        mEndPoint.setLongitude(endLon);

    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(RouteActivity.this);
        aMap.setOnMarkerClickListener(RouteActivity.this);
        aMap.setOnInfoWindowClickListener(RouteActivity.this);
        aMap.setInfoWindowAdapter(RouteActivity.this);

    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }



//    public void onDriveClick(View view) {
//        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
//        mDrive.setImageResource(R.drawable.route_drive_select);
//        mapView.setVisibility(View.VISIBLE);
//    }


    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
           // 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
            // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，
            // 第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

    }
//驾驶导航
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约"+taxiCost+"元");
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    DriveRouteDetailActivity.class);
                            intent.putExtra("drive_path", drivePath);
                            intent.putExtra("drive_result",
                                    mDriveRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }


    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }


    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.route_navi:
                Bundle nvbundle=new Bundle();
                Intent nvintent=new Intent(RouteActivity.this,BaseActivity.class);
                nvbundle.putDouble("startLat",startLat);
                nvbundle.putDouble("startlon",startLon);
                nvbundle.putDouble("endlat",endLat);
                nvbundle.putDouble("endlon",endLon);
                nvintent.putExtras(nvbundle);
                startActivity(nvintent);

        }
    }
}

