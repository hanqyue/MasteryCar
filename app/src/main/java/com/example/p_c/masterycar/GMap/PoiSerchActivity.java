package com.example.p_c.masterycar.GMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.p_c.masterycar.CarVideo.CarVideo;
import com.example.p_c.masterycar.Gson.MainTestActivity;
import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.ServiceMange.CarSocketService;
import com.example.p_c.masterycar.Util.AMapUtil;
import com.example.p_c.masterycar.Util.ToastUtil;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by 李思言 on 2016/4/10.
 */
public class PoiSerchActivity extends AppCompatActivity implements LocationSource, AMapLocationListener
        ,PoiSearch.OnPoiSearchListener, View.OnClickListener, AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.InfoWindowAdapter,TextWatcher, Inputtips.InputtipsListener {
    private AMap aMap;
    private MapView view;
    private OnLocationChangedListener mListener;
    //    定位服务类
    private AMapLocationClient mlocationClient;
    //    定位参数设置
    private AMapLocationClientOption mLocationOption;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private int currentPage = 0;// 当前页面，从0开始计数
    private EditText editCity;// 要输入的城市名字或者城市区号
    private connectReceiver mConnectReceiver;
    private AutoCompleteTextView searchText;// 输入搜索关键字
    private PoiResult poiResults; // poi返回的结果
    private String key=null;
    private TextView searchButton;
    //private EditText mSearchText;
    private List<PoiItem> poiItems;// poi数据
    private Marker locationMarker; // 选择的点
    private Marker mlastMarker;
    private LatLonPoint lp = new LatLonPoint(39.993167, 116.473274);//
    private TextView mPoiName, mPoiAddress;
    private LinearLayout mPoiDetail;
    private myPoiOverlay poiOverlay;// poi图层
    private Marker detailMarker;
    private String City;
    private String Adress;
    private String routeAdress;
    private String destination=null;
    private String Lat;//目的地坐标
    private String Lon;//目的地坐标
    private String PoiAdress;//目的地地址
    private double StartLat;
    private double StartLon;
    private UiSettings mUiSettings;
    private Button gas_station;
    //private Button navigation;
    private Button Serch;
    private Button gasMessage;
    private Button toroute;
    //--------------------------------------//
    private MyBcReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poiaroundsearch_activity);
        view= (MapView) findViewById(R.id.mapView);
        view.onCreate(savedInstanceState);
        init();

    }
    private void init(){
        if(aMap==null){
            aMap=view.getMap();
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);
            aMap.setOnMapClickListener(this);
            aMap.setOnMarkerClickListener(this);
            aMap.setInfoWindowAdapter(this);
            searchButton = (TextView) findViewById(R.id.btn_search);//搜索按钮
            searchButton.setOnClickListener(this);
            locationMarker=aMap.addMarker(new MarkerOptions().anchor(0.5f,0.5f)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
                    .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
            searchText= (AutoCompleteTextView) findViewById(R.id.input_edittext);//查询框
            searchText.addTextChangedListener(this);
           // searchText.setOnClickListener(this);
            setUpMap();
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 14));
        }

        gas_station= (Button) findViewById(R.id.gas_station);
        gas_station.setOnClickListener(this);
      // navigation= (Button) findViewById(R.id.navigation);
       // navigation.setOnClickListener(this);
        Serch= (Button) findViewById(R.id.Serch);
        Serch.setOnClickListener(this);
        gasMessage= (Button) findViewById(R.id.gas_message);
        gasMessage.setOnClickListener(this);
        toroute= (Button) findViewById(R.id.to_route);
        toroute.setOnClickListener(this);
      /*    navigation= (Button) findViewById(R.id.navigation);
     navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POi","into");
                Intent intent = new Intent("com.jay.mybcreceiver.LOGIN_OTHER");
                localBroadcastManager.sendBroadcast(intent);
            }
        });*/

        //-----------------广播---------------
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //初始化广播接收者，设置过滤器
        localReceiver = new MyBcReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.jay.mybcreceiver.LOGIN_OTHER");
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

    }
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mPoiDetail = (LinearLayout) findViewById(R.id.poi_detail);

        mPoiName=(TextView)findViewById(R.id.poi_name);
        mPoiAddress = (TextView) findViewById(R.id.poi_address);
        searchText = (AutoCompleteTextView) findViewById(R.id.input_edittext);
    }
    protected void doSearchQuery(){
       currentPage=0;
        query=new PoiSearch.Query(destination,key,City);
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);
        if(lp!=null){
            poiSearch = new PoiSearch(this, query);//初始化poiSearch对象
            poiSearch.setOnPoiSearchListener(this);//设置回调数据的监听器
            poiSearch.searchPOIAsyn();//开始搜索

        }
    }
    protected void doSearchQuery(String key){
        currentPage=0;
        query=new PoiSearch.Query(Adress,key,City);
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);
        if(lp!=null){
            poiSearch = new PoiSearch(this, query);//初始化poiSearch对象
            poiSearch.setOnPoiSearchListener(this);//设置回调数据的监听器
            poiSearch.searchPOIAsyn();//开始搜索

        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
        whetherToShowDetailInfo(false);
        registerReceiver();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
        unregisterReceiver(mConnectReceiver);
    }


    private void registerReceiver(){
        mConnectReceiver = new connectReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CarSocketService.ACTION_LOGIN_OTHER);
        registerReceiver(mConnectReceiver, intentFilter);
    }
    class connectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("QWERT","没车辆数据");
            if(CarSocketService.ACTION_LOGIN_OTHER.equals(action)){
                //Intent t = new Intent("com.jay.mybcreceiver.LOGIN_OTHER");
                // localBroadcastManager.sendBroadcast(t);
                Intent it = new Intent(PoiSerchActivity.this, CarVideo.class);
                startActivity(it);

            }
        }
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
         view.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.onDestroy();
    }


    //    激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener=onLocationChangedListener;
        if (mlocationClient==null){
            mlocationClient=new AMapLocationClient(this);
            mLocationOption=new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }


    }
    //    停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet());
        PoiAdress=mCurrentPoi.getTitle();
        Lat= String.valueOf(mCurrentPoi.getLatLonPoint().getLatitude());
        Lon= String.valueOf(mCurrentPoi.getLatLonPoint().getLongitude());

    }

    private int[] markers = {
            R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };
//获取你的位置信息
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                boolean test=false;
                City=aMapLocation.getCity();
                Adress=aMapLocation.getDistrict()+"|"+aMapLocation.getStreet()+"|"+aMapLocation.getStreetNum()
                        +aMapLocation.getAoiName();
                routeAdress=aMapLocation.getAoiName();
                StartLat=aMapLocation.getLatitude();
                StartLon=aMapLocation.getLongitude();
                Log.d("POI","lat="+StartLat+"lon="+StartLon);
             /*   startVideo video=new startVideo(StartLon,StartLat);
                test=video.move();
                Log.d("POI", String.valueOf(test));
                if (test){
                    Intent intent = new Intent("com.jay.mybcreceiver.LOGIN_OTHER");
                  localBroadcastManager.sendBroadcast(intent);
               }
*/
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }




    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode==1000){
            if (poiResult!=null&&poiResult.getQuery()!=null){
                if (poiResult.getQuery().equals(query)){
                    poiResults=poiResult;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    if (poiItems != null && poiItems.size() > 0) {
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                       if (mlastMarker != null) {
                           resetlastmarker();
                       }
                         //清理之前搜索结果的marker
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                       }
                        aMap.clear();// 清理之前的图标
                        setUpMap();
                        poiOverlay = new myPoiOverlay(aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.point4)))
                                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));

                        aMap.addCircle(new CircleOptions()
                                .center(new LatLng(lp.getLatitude(),
                                        lp.getLongitude())).radius(5000)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));
                    } else {
                        ToastUtil.show(PoiSerchActivity.this,
                                "无数据");
                    }
                }else {
                    ToastUtil.show(PoiSerchActivity.this,
                            "无数据");

                }

            }
        }else {
            ToastUtil.showerror(PoiSerchActivity.this, rCode);
        }

    }
    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        }else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    //button点击
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                destination=searchText.getText().toString().trim();
                if ("".equals(destination)){
                    ToastUtil.show(PoiSerchActivity.this, "请输入搜索关键字");
                    return;
                }else {
                    doSearchQuery();
                }

                break;
            case R.id.gas_station:
                key="加油站";
                doSearchQuery(key);
                break;
            case R.id.Serch:
                Intent aimintent=new Intent(PoiSerchActivity.this,ItoAim.class);
                Bundle serchbundle=new Bundle();
                serchbundle.putString("city",City);
                serchbundle.putString("name",routeAdress);
                aimintent.putExtras(serchbundle);
                startActivity(aimintent);
                break;
            case R.id.gas_message:
                Bundle bundlegas=new Bundle();
                bundlegas.putDouble("Lat_data", Double.parseDouble(Lat));
                bundlegas.putDouble("Lon_data", Double.parseDouble(Lon));
                bundlegas.putString("PoiAdress",PoiAdress);
                Intent intentGs=new Intent(PoiSerchActivity.this,MainTestActivity.class);
                intentGs.putExtras(bundlegas);
                startActivity(intentGs);
                break;
            case R.id.to_route:
                Bundle bundle=new Bundle();
                bundle.putDouble("MyLat",StartLat);
                bundle.putDouble("MyLon",StartLon);
                bundle.putString("PoiAdress",PoiAdress);
                bundle.putDouble("Lat_data", Double.parseDouble(Lat));
                bundle.putDouble("Lon_data", Double.parseDouble(Lon));
                Intent intentRoute=new Intent(PoiSerchActivity.this,RouteActivity.class);
                intentRoute.putExtras(bundle);
                startActivity(intentRoute);
            default:
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.poi_marker_pressed)));
                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }
        return true;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText,City);
            Inputtips inputTips = new Inputtips(PoiSerchActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode){
        if (rCode == 1000) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            searchText.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(PoiSerchActivity.this, rCode);
        }
    }

    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        public myPoiOverlay(AMap amap , List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {

            if (mPois != null && mPois.size() > 0) {

                if (mamap == null) {

                    return;
                }

                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
       public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类
         * <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html"
         * title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {

                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {

                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }




    }

}
