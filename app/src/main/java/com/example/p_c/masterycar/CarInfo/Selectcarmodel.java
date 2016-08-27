package com.example.p_c.masterycar.CarInfo;

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

import com.example.p_c.masterycar.R;

import java.util.ArrayList;
import java.util.List;

public class Selectcarmodel extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtTitle;
    private ListView carmodellist;
    private String carbrand;
    private TextView carmodel;
    private TextView body;
    private carmodelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcarmodel);
        inittitle();
    }
    private void inittitle(){
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("选择型号");
        txtTitle.setVisibility(View.VISIBLE);


        Bundle bundle = getIntent().getExtras();
        carbrand = bundle.getString("carbrand");

        carmodellist = (ListView) findViewById(R.id.carmodellist);
        adapter = new carmodelAdapter(this, R.layout.select_carmodel_item, getcarmodelList());
        carmodellist.setAdapter(adapter);

        carmodellist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                carmodel = (TextView) view.findViewById(R.id.carmodelitem);
                body = (TextView) view.findViewById(R.id.bodyitem);

                Intent intent = new Intent();
                intent.putExtra("car_model", carmodel.getText());
                intent.putExtra("car_body",body.getText());
                setResult(20, intent);
                finish();
            }
        });
    }


    private ArrayList<CarmodelInfo> getcarmodelList() {
         CarmodelInfo mselectcar = new CarmodelInfo();
         CarmodelInfo mcar = new CarmodelInfo();
        ArrayList<CarmodelInfo> list = new ArrayList();
        if(carbrand.equals("奥迪")){

            mselectcar.setCarmodel("S3·基本款");
            mselectcar.setCarbody("紧凑型车");
            list.add(mselectcar);

            mcar.setCarmodel("S3·Cabriolet");
            mcar.setCarbody("敞篷车");
            list.add(mcar);
        }else if(carbrand.equals("宝马")){

            mselectcar.setCarmodel("218i·时尚型");
            mselectcar.setCarbody("旅行车");
            list.add(mselectcar);

            mcar.setCarmodel("330i·M运动型");
            mcar.setCarbody("旅行轿车");
            list.add(mcar);
        }

        return list;
    }

    class carmodelAdapter extends ArrayAdapter<CarmodelInfo> {

        private int resourceId;
        private TextView carmodel;
        private TextView body;

        public carmodelAdapter(Context context, int textViewResourceId, List<CarmodelInfo> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(final int position, View contvertView, ViewGroup parent) {
            CarmodelInfo item = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            carmodel = (TextView) view.findViewById(R.id.carmodelitem);
            body = (TextView) view.findViewById(R.id.bodyitem);
            carmodel.setText(item.getCarmodel());
            body.setText(item.getCarbody());

            return view;
        }

    }

}
