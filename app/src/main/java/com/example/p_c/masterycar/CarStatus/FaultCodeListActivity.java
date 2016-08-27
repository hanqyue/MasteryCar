package com.example.p_c.masterycar.CarStatus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.p_c.masterycar.ConnectWebServer.FaultCodeInfo;
import com.example.p_c.masterycar.R;

import java.util.List;

public class FaultCodeListActivity extends AppCompatActivity {

    private ListView mListView;
    private faultAdapter adapter;
    private ImageView btnBack;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault_code_list);
        initView();
        setListView();
    }

    private void initView() {
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("故障码");
        txtTitle.setVisibility(View.VISIBLE);
    }

    private void setListView() {
        mListView = (ListView) findViewById(R.id.fault_list);
        adapter = new faultAdapter(this, R.layout.fault_item, CarStatusActivity.sList);
        mListView.setAdapter(adapter);
    }

    class faultAdapter extends ArrayAdapter<FaultCodeInfo> {

        private int resourceId;
        private TextView faultitem;

        public faultAdapter(Context context, int textViewResourceId, List<FaultCodeInfo> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(final int position, View contvertView, ViewGroup parent) {
            FaultCodeInfo info = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
             faultitem = (TextView) view.findViewById(R.id.faultitem);
             faultitem.setText(info.getFaultCode());
             faultitem.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(FaultCodeListActivity.this,FaultcodeActivity.class);
                     intent.putExtra("faultposition",position);
                     startActivity(intent);
                 }
             });
            return view;
        }

    }
}
