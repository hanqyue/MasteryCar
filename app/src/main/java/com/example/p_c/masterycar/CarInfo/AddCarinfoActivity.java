package com.example.p_c.masterycar.CarInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p_c.masterycar.R;

public class AddCarinfoActivity extends AppCompatActivity {
    private static final String IS_CARADD = "com.example.p_c.masterycar.CarInfo.IS_CARADD";
    private ImageView btnBack;
    private TextView txtTitle;
    private TextView addmenu;
    private RelativeLayout mlisteradd;
    private TextView addtext;
    private EditText carnumshow;
    private EditText chejiashow;
    private EditText engineshow;
    private RelativeLayout bodylodel;
    private ImageView carsign;
    private TextView carName;
    private TextView bodyshow;

    private View popXSZ;
    private CarInfo mCarInfo = new CarInfo();
    private static final int SELECT_CAR_BRAND = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carinfo);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("填写车辆信息");
        txtTitle.setVisibility(View.VISIBLE);
        addmenu = (TextView) findViewById(R.id.bar_menu);
        addmenu.setText("完成");
        addmenu.setVisibility(View.VISIBLE);

        carnumshow = (EditText) findViewById(R.id.carnumshow);
        chejiashow = (EditText) findViewById(R.id.chejiashow);
        engineshow = (EditText) findViewById(R.id.engineshow);
        addmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCarInfo.setCarNum(carnumshow.getText().toString().trim());
                mCarInfo.setCarriageNum(chejiashow.getText().toString().trim());
                mCarInfo.setEngineNum(chejiashow.getText().toString().trim());
                boolean result = checkQueryItem(mCarInfo);
              if(result){
                  CarInfoList.getCarinfolist().addcarinfo(mCarInfo);
                  setAnswerShownResult(true);
                  finish();
              }
            }
        });

        initView();
    }

    private void initView(){

        carsign = (ImageView) findViewById(R.id.carsignitem);
        carName = (TextView) findViewById(R.id.carnameitem);
        bodylodel = (RelativeLayout) findViewById(R.id.bodylayout);
        bodyshow = (TextView) findViewById(R.id.bodyshow);


        addtext = (TextView) findViewById(R.id.addtext);
        addtext.setVisibility(View.VISIBLE);
        mlisteradd = (RelativeLayout)findViewById(R.id.listeradd);
        mlisteradd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddCarinfoActivity.this, Selectcarbrand.class);
                startActivityForResult(intent,SELECT_CAR_BRAND);
            }
        });
        // 显示隐藏行驶证图示
        popXSZ = (View) findViewById(R.id.popXSZ);
        popXSZ.setOnTouchListener(new popOnTouchListener());
        hideShowXSZ();

    }


    // 避免穿透导致表单元素取得焦点
    private class popOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            popXSZ.setVisibility(View.GONE);
            return true;
        }
    }

    // 显示隐藏行驶证图示
    private void hideShowXSZ() {
        View btn_help1 = (View) findViewById(R.id.ico_chejia);
        View btn_help2 = (View) findViewById(R.id.ico_engine);
        Button btn_closeXSZ = (Button) findViewById(R.id.btn_closeXSZ);

        btn_help1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.VISIBLE);
            }
        });
        btn_help2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.VISIBLE);
            }
        });
        btn_closeXSZ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode) {
            case SELECT_CAR_BRAND:
                if(data == null){
                    return;
                }
                Bundle bundle = data.getExtras();
                String carname = bundle.getString("car_brand");
                String carmodel = bundle.getString("car_model");
                String carbody = bundle.getString("car_body");
                mCarInfo.setCarSign(carname);
                mCarInfo.setCarBrand(carname);
                mCarInfo.setCarModel(carmodel);
                mCarInfo.setBodyLevel(carbody);

                addtext.setVisibility(View.GONE);
                carsign.setVisibility(View.VISIBLE);
                carName.setVisibility(View.VISIBLE);
                bodylodel.setVisibility(View.VISIBLE);
                if (carname.equals("宝马")) {
                    carsign.setImageResource(R.drawable.baoma);
                }else if(carname.equals("奥迪")){
                    carsign.setImageResource(R.drawable.aodi);
                }
                carName.setText(carname+" "+carmodel);
                bodyshow.setText(carbody);

                break;
        }

    }

    private void setAnswerShownResult(boolean isaddcar){
        Intent data = new Intent();
        data.putExtra(IS_CARADD,isaddcar);
        setResult(RESULT_OK,data);
    }
    public static boolean wasAnswerShow(Intent reult){
        return reult.getBooleanExtra(IS_CARADD,false);
    }

    // 提交表单检测
    private boolean checkQueryItem(CarInfo car) {

        if (car.getCarModel().equals("")) {
            Toast.makeText(this, "请选择车辆品牌型号", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (car.getCarNum().length() != 7) {
            Toast.makeText(this, "您输入的车牌号有误", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 车架号
        if (car.getCarriageNum().length() < 6) {
            Toast.makeText(this, "您输入的车架号有误", Toast.LENGTH_SHORT).show();
            return false;
        }

        //发动机
        if (car.getEngineNum().length() < 6) {
            Toast.makeText(this, "您输入的发动机号有误", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
