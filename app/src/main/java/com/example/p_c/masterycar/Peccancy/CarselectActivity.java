package com.example.p_c.masterycar.Peccancy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.p_c.masterycar.CarInfo.CarInfo;
import com.example.p_c.masterycar.CarInfo.CarInfoList;
import com.example.p_c.masterycar.R;

import java.util.List;

public class CarselectActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtTitle;
    private ListView mListView;
    private carselectAdapter adapter;
    private static final String CAR_SELECT = "com.example.p_c.masterycar.Peccancy.CAR_SELECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carselect);
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
        txtTitle.setText("选择查询车辆");
        txtTitle.setVisibility(View.VISIBLE);

        mListView = (ListView) findViewById(R.id.carselect_list);
        adapter = new carselectAdapter(this, R.layout.select_car_item, CarInfoList.getCarinfolist().getcarList());
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
              setAnswerShownResult(position);
            }
        });
    }

    class carselectAdapter extends ArrayAdapter<CarInfo> {

        private int resourceId;
        private ImageView carsign;
        private TextView cartext;
        private TextView carnum;

        public carselectAdapter(Context context, int textViewResourceId, List<CarInfo> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(final int position, View contvertView, ViewGroup parent) {
            CarInfo carInfo = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            carsign = (ImageView) view.findViewById(R.id.carsignitem);
            cartext = (TextView) view.findViewById(R.id.carnameitem);
            carnum = (TextView) view.findViewById(R.id.carnumitem);
            if (carInfo.getCarSign().equals("宝马")) {
                carsign.setImageResource(R.drawable.baoma);
            }else if (carInfo.getCarSign().equals("奥迪")) {
                carsign.setImageResource(R.drawable.aodi);
            }
            cartext.setText(carInfo.getCarBrand() + " " + carInfo.getCarModel());
            carnum.setText(carInfo.getCarNum());
            return view;
        }

    }

    public static Intent newIntent(Context mcontent){
        Intent i = new Intent(mcontent,CarselectActivity.class);
        return i;
    }
    private void setAnswerShownResult(int position){
        Intent data = new Intent();
        data.putExtra(CAR_SELECT,position);
        setResult(RESULT_OK,data);
        finish();
    }
    public static int wasAnswerShow(Intent reult){
        return reult.getIntExtra(CAR_SELECT,0);
    }
}
