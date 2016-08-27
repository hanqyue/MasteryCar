package com.example.p_c.masterycar.Peccancy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.WeizhangIntentService;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;
import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.cheshouye.api.client.json.WeizhangResponseJson;
import com.example.p_c.masterycar.CarInfo.CarInfoList;
import com.example.p_c.masterycar.R;

import java.util.ArrayList;
import java.util.List;


public class PeccancyActivity extends AppCompatActivity {

    private View popLoader;
    private ImageView btnBack;
    private TextView txtTitle;
    private TextView cy_city;
    private TextView car_num;
    private ImageView qiehuan;
    private int selectposition;
    private static final int REQUEST_CDDE_CHEAT = 0;
    private static final int SELECT_CITY= 1;
    final Handler cwjHandler = new Handler();
    WeizhangResponseJson info = null;
    private CarInfoList mCarInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peccancy);
        mCarInfoList = CarInfoList.getCarinfolist();
        // ********************************************************
        Log.d("初始化服务代码","");
        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        weizhangIntent.putExtra("appId","1691");// 您的appId
        weizhangIntent.putExtra("appKey", "74af01104f4a53ab6c5e002d395ac1a5");// 您的appKey
        startService(weizhangIntent);
        // ********************************************************

        initTitbar();
        initView();
    }
    private void initTitbar() {
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("违章查询");
        txtTitle.setVisibility(View.VISIBLE);
    }
    private void initView(){
        car_num = (TextView) findViewById(R.id.car_num);
        qiehuan = (ImageView) findViewById(R.id.qiehuan);
        cy_city = (TextView) findViewById(R.id.cx_city);

        if(CarInfoList.getCarinfolist().isEmpty()) {
            car_num.setText("未添加车辆");
            qiehuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PeccancyActivity.this,"请先绑定车辆",Toast.LENGTH_SHORT).show();
                }
            });
            cy_city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PeccancyActivity.this,"请先绑定车辆",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            car_num.setText(mCarInfoList.getcarinfo(mCarInfoList.getDefaultcarposition()).getCarNum());
            qiehuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = CarselectActivity.newIntent(PeccancyActivity.this);
                    startActivityForResult(i,REQUEST_CDDE_CHEAT);
                }
            });
            cy_city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(PeccancyActivity.this, ProvinceList.class);
                    startActivityForResult(intent,SELECT_CITY);
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode) {
            case REQUEST_CDDE_CHEAT:
                if(data == null){
                    return;
                }
                selectposition = CarselectActivity.wasAnswerShow(data);
                car_num.setText(mCarInfoList.getcarinfo(selectposition).getCarNum());
                if (cy_city.getTag() != null
                        && !cy_city.getTag().equals("")) {
                    queryPeccancyActivity();
                }
                break;
            case SELECT_CITY:
                Bundle bundle1 = data.getExtras();
                String cityId = bundle1.getString("city_id");
                setQueryItem(Integer.parseInt(cityId));
                queryPeccancyActivity();
                break;
        }

    }

    // 根据城市的配置设置查询项目
    private void setQueryItem(int cityId) {
        InputConfigJson cityConfig = WeizhangClient.getInputConfig(cityId);

        // 没有初始化完成的时候;
        if (cityConfig != null) {
            CityInfoJson city = WeizhangClient.getCity(cityId);
            cy_city.setText(city.getCity_name());
            cy_city.setTag(cityId);
        }
    }

  private void queryPeccancyActivity(){

      // 获取违章信息
      CarInfo car = new CarInfo();

      String quertCityIdStr = "";
      String chepaiNum = mCarInfoList.getcarinfo(selectposition).getCarNum();
      if (cy_city.getTag() != null
              && !cy_city.getTag().equals("")) {
          quertCityIdStr = cy_city.getTag().toString().trim();

      }
      String chejiahao = mCarInfoList.getcarinfo(selectposition).getCarriageNum();
      String engine =  mCarInfoList.getcarinfo(selectposition).getEngineNum();
      char shortNameStr = chepaiNum.charAt(0);
      String chepaiNumberStr = chepaiNum.substring(chepaiNum.length()-6);
      final String chejiaNumberStr = chejiahao.substring(chejiahao.length()-6);
      final String engineNumberStr = engine.substring(engine.length()-6);

      car.setCity_id(Integer.parseInt(quertCityIdStr));
      car.setChejia_no(chejiaNumberStr);
      car.setChepai_no(shortNameStr+chepaiNumberStr);
      car.setEngine_no(engineNumberStr);
      queryLoder();
      step4(car);

  }
    private void queryLoder(){
        popLoader = (View)findViewById(R.id.popLoader);
        popLoader.setVisibility(View.VISIBLE);
    }
    public void step4(final CarInfo car) {
        // 声明一个子线程
        new Thread() {
            @Override
            public void run() {
                try {
                    // 这里写入子线程需要做的工作
                    info = WeizhangClient.getWeizhang(car);
                    cwjHandler.post(mUpdateResults); // 高速UI线程可以更新结果了
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };
    private void updateUI() {
        TextView result_null = (TextView) findViewById(R.id.result_null);
        TextView result_title = (TextView) findViewById(R.id.result_title);
        ListView result_list = (ListView) findViewById(R.id.result_list);

        popLoader.setVisibility(View.GONE);

        Log.d("返回数据", info.toJson());

        // 直接将信息限制在 Activity中
        if (info.getStatus() == 2001) {
            result_null.setVisibility(View.GONE);
            result_title.setVisibility(View.VISIBLE);
            result_list.setVisibility(View.VISIBLE);

            result_title.setText("共违章" + info.getCount() + "次, 计"
                    + info.getTotal_score() + "分, 罚款 " + info.getTotal_money()
                    + "元");

            WeizhangResponseAdapter mAdapter = new WeizhangResponseAdapter(
                    this, getData());
            result_list.setAdapter(mAdapter);

        } else {
            // 没有查到为章记录

            if (info.getStatus() == 5000) {
                result_null.setText("请求超时，请稍后重试");
            } else if (info.getStatus() == 5001) {
                result_null.setText("交管局系统连线忙碌中，请稍后再试");
            } else if (info.getStatus() == 5002) {
                result_null.setText("恭喜，当前城市交管局暂无您的违章记录");
            } else if (info.getStatus() == 5003) {
                result_null.setText("数据异常，请重新查询");
            } else if (info.getStatus() == 5004) {
                result_null.setText("系统错误，请稍后重试");
            } else if (info.getStatus() == 5005) {
                result_null.setText("车辆查询数量超过限制");
            } else if (info.getStatus() == 5006) {
                result_null.setText("你访问的速度过快, 请后再试");
            } else if (info.getStatus() == 5008) {
                result_null.setText("输入的车辆信息有误，请查证后重新输入");
            } else {
                result_null.setText("恭喜, 没有查到违章记录！");
            }

            result_title.setVisibility(View.GONE);
            result_list.setVisibility(View.GONE);
            result_null.setVisibility(View.VISIBLE);
        }
    }
    /**
     * title:填值
     *
     * @return
     */
    private List getData() {
        List<WeizhangResponseHistoryJson> list = new ArrayList();

        for (WeizhangResponseHistoryJson weizhangResponseHistoryJson : info
                .getHistorys()) {
            WeizhangResponseHistoryJson json = new WeizhangResponseHistoryJson();
            json.setFen(weizhangResponseHistoryJson.getFen());
            json.setMoney(weizhangResponseHistoryJson.getMoney());
            json.setOccur_date(weizhangResponseHistoryJson.getOccur_date());
            json.setOccur_area(weizhangResponseHistoryJson.getOccur_area());
            json.setInfo(weizhangResponseHistoryJson.getInfo());
            list.add(json);
        }

        return list;
    }


}
