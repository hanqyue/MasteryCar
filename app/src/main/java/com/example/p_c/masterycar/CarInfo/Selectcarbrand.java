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

public class Selectcarbrand extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtTitle;
    private ListView carbrandlist;
    private carbrandAdapter adapter;
    private String selectBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcarbrand);
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
        txtTitle.setText("选择品牌");//选择型号
        txtTitle.setVisibility(View.VISIBLE);


        carbrandlist = (ListView) findViewById(R.id.carbrandlist);
        adapter = new carbrandAdapter(this, R.layout.select_carbrand_item, getcarbrandList());
        carbrandlist.setAdapter(adapter);

        carbrandlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView carbrand = (TextView) view.findViewById(R.id.carnameitem);
                selectBrand = carbrand.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("carbrand", selectBrand);
                intent.setClass(Selectcarbrand.this, Selectcarmodel.class);
                startActivityForResult(intent, 20);
            }
        });
    }


    private ArrayList<CarbrandInfo> getcarbrandList() {

        ArrayList<CarbrandInfo> list = new ArrayList();
        CarbrandInfo mselectcar = new CarbrandInfo();
        mselectcar.setCarsign("奥迪");
        mselectcar.setCarbrand("奥迪");
        list.add(mselectcar);
        CarbrandInfo mcar = new CarbrandInfo();
        mcar.setCarsign("宝马");
        mcar.setCarbrand("宝马");
        list.add(mcar);
        return list;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        Bundle bundle = data.getExtras();
        // 获取城市name
        String car_body = bundle.getString("car_body");
        String car_model = bundle.getString("car_model");

        Intent intent = new Intent();
        intent.putExtra("car_brand", selectBrand);
        intent.putExtra("car_model", car_model);
        intent.putExtra("car_body",car_body);
        setResult(RESULT_OK, intent);
        finish();
    }

    class carbrandAdapter extends ArrayAdapter<CarbrandInfo> {

        private int resourceId;
        private ImageView carsign;
        private TextView carname;

        public carbrandAdapter(Context context, int textViewResourceId, List<CarbrandInfo> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(final int position, View contvertView, ViewGroup parent) {
            CarbrandInfo item = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            carsign = (ImageView) view.findViewById(R.id.carsignitem);
            carname = (TextView) view.findViewById(R.id.carnameitem);
            if (item.getCarsign().equals("宝马")) {
                carsign.setImageResource(R.drawable.baoma);
            }else if(item.getCarsign().equals("奥迪")){
                carsign.setImageResource(R.drawable.aodi);
            }
            carname.setText(item.getCarbrand());
            return view;
        }

    }

}
