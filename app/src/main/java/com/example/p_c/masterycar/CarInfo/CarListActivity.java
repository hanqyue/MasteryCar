package com.example.p_c.masterycar.CarInfo;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p_c.masterycar.R;

import java.util.List;

public class CarListActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView txtTitle;
    private TextView addmenu;
    private ListView mListView;
    private carAdapter adapter;
    private ItemMenuView mItemMenuView;
    private ImageView mImageView;
    private LinearLayout moren;
    private TextView xiangqing;
    private LinearLayout deleteitem;
    private static final int BEGIN_CARADDACTIVITY = 0;
    private static final String IS_UPDATECAR = "com.example.p_c.masterycar.CarInfo.IS_UPDATECAR";
    private boolean isadddefaultcar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        initTitbar();
        showCar();
        setitemLister();
    }


    private void initTitbar() {
        mListView = (ListView) findViewById(R.id.car_list);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isadddefaultcar) {
                    setAnswerShownResult(true);
                }
                finish();
            }
        });
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("车辆信息");
        txtTitle.setVisibility(View.VISIBLE);
        addmenu = (TextView) findViewById(R.id.bar_menu);
        addmenu.setText("添加");
        addmenu.setVisibility(View.VISIBLE);
        addmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CarListActivity.this, AddCarinfoActivity.class);
                startActivityForResult(intent,BEGIN_CARADDACTIVITY);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isadddefaultcar) {
            setAnswerShownResult(true);
        }
    }

    private void showCar() {
        adapter = new carAdapter(this, R.layout.car_item, CarInfoList.getCarinfolist().getcarList());
        mListView.setAdapter(adapter);

    }

    private void setitemLister() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                mImageView = (ImageView) view.findViewById(R.id.image_menu);
                mItemMenuView = (ItemMenuView) view.findViewById(R.id.itemMenuView);
                mItemMenuView.setContentView();

                if (mItemMenuView.isExpand()) {
                    mItemMenuView.collapse();
                    //mTextView.setText("点击向下展开");
                    mImageView.setImageResource(R.drawable.arrow);
                } else {
                    mItemMenuView.expand();
                    // mTextView.setText("点击向上收叠");
                    mImageView.setImageResource(R.drawable.arrowdown);
                    moren = (LinearLayout) mItemMenuView.getContentView().findViewById(R.id.morenbutton);
                    xiangqing = (TextView) mItemMenuView.getContentView().findViewById(R.id.xiangqingbutton);
                    deleteitem = (LinearLayout) mItemMenuView.getContentView().findViewById(R.id.deletebutton);
                    moren.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(CarInfoList.getCarinfolist().getDefaultcarposition() == position){
                                Toast.makeText(CarListActivity.this, "该车辆已为默认车辆", Toast.LENGTH_SHORT).show();
                            }else {
                                CarInfoList.getCarinfolist().setDefaultcarposition(position);
                                setAnswerShownResult(true);
                                showCar();
                            }
                        }
                    });
                    xiangqing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CarListActivity.this,CarShowinfoActivity.class);
                            intent.putExtra("car_position",position);
                            startActivity(intent);
                        }
                    });
                    deleteitem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ( position+1 == CarInfoList.getCarinfolist().listSize()){
                                CarInfoList.getCarinfolist().setDefaultcarposition(0);
                                setAnswerShownResult(true);
                            }
                            CarInfoList.getCarinfolist().deletecarinfo(position);
                            showCar();
                        }
                    });
                }
            }
        });
    }

    private void setAnswerShownResult(boolean isupdatecar){
        Intent data = new Intent();
        data.putExtra(IS_UPDATECAR,isupdatecar);
        setResult(RESULT_OK,data);
    }
    public static boolean wasAnswerShow(Intent reult){
        return reult.getBooleanExtra(IS_UPDATECAR,false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode) {
            case BEGIN_CARADDACTIVITY:
                if(data == null){
                    return;
                }
                boolean isaddcar = AddCarinfoActivity.wasAnswerShow(data);
                if (isaddcar){
                    if(CarInfoList.getCarinfolist().getcarList().size() == 1) {
                        isadddefaultcar = true;
                    }
                    showCar();
                }
                break;
        }

    }

    class carAdapter extends ArrayAdapter<CarInfo> {

        private int resourceId;
        private ImageView carsign;
        private TextView cartext;
        private ImageView moren;
        private TextView kongbai;

        public carAdapter(Context context, int textViewResourceId, List<CarInfo> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(final int position, View contvertView, ViewGroup parent) {
            CarInfo carInfo = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            carsign = (ImageView) view.findViewById(R.id.biaozhi);
            cartext = (TextView) view.findViewById(R.id.carname);
            moren = (ImageView) view.findViewById(R.id.moren);
            kongbai = (TextView) view.findViewById(R.id.kongbai);
            if (carInfo.getCarSign().equals("宝马")) {
                carsign.setImageResource(R.drawable.baoma);
            }else if(carInfo.getCarSign().equals("奥迪")){
                carsign.setImageResource(R.drawable.aodi);
            }
            cartext.setText(carInfo.getCarBrand() + " " + carInfo.getCarModel());

            if (CarInfoList.getCarinfolist().getDefaultcarposition() == position) {
                moren.setVisibility(View.VISIBLE);
            } else {
                kongbai.setVisibility(View.VISIBLE);
            }

            return view;
        }

    }

}
