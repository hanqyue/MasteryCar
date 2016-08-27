package com.example.p_c.masterycar.CarInfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.p_c.masterycar.R;

public class CarShowinfoActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtTitle;
    private CarInfo mCarInfo;
    private ImageView carsign;
    private TextView carnum;
    private TextView brand;
    private TextView carmode;
    private TextView body;
    private TextView engine;
    private TextView chejia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_showinfo);
        int position = getIntent().getIntExtra("car_position",-1);
        mCarInfo = CarInfoList.getCarinfolist().getcarinfo(position);
        initView();
    }
    private void initView(){
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("车辆详情");
        txtTitle.setVisibility(View.VISIBLE);

        carsign = (ImageView) findViewById(R.id.carsignshow);
        carnum = (TextView) findViewById(R.id.carnumshow);
        brand = (TextView) findViewById(R.id.brandshow);
        carmode = (TextView) findViewById(R.id.carmodelshow);
        body = (TextView) findViewById(R.id.bodyshow);
        engine = (TextView) findViewById(R.id.engineshow);
        chejia = (TextView) findViewById(R.id.chejiashow);
        if(mCarInfo.getCarSign().equals("宝马")){
            carsign.setImageResource(R.drawable.baoma);
        }else if(mCarInfo.getCarSign().equals("奥迪")){
            carsign.setImageResource(R.drawable.aodi);
        }
        carnum.setText(mCarInfo.getCarNum());
        brand.setText(mCarInfo.getCarBrand());
        carmode.setText(mCarInfo.getCarModel());
        body.setText(mCarInfo.getBodyLevel());
        engine.setText(mCarInfo.getEngineNum());
        chejia.setText(mCarInfo.getCarriageNum());

    }
}
