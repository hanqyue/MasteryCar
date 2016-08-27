package com.example.p_c.masterycar.CarStatus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.p_c.masterycar.ConnectWebServer.FaultCodeInfo;
import com.example.p_c.masterycar.R;
import com.example.p_c.masterycar.ServiceMange.CarSocketService;

public class FaultcodeActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView txtTitle;
    private TextView chinese;
    private TextView english;
    private TextView fanchou;
    private TextView backgound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faultcode);

        FaultCodeInfo info;
        int position = getIntent().getIntExtra("faultposition",-1);
        if(position == -1){
            //接收类对象
            info = CarSocketService.minfo;
        }else{
           info = (FaultCodeInfo) CarStatusActivity.sList.get(position);
        }
        initView(info);
    }

    private void initView(FaultCodeInfo info) {
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("故障码"+info.getFaultCode());
        txtTitle.setVisibility(View.VISIBLE);

        chinese = (TextView) findViewById(R.id.chinese);
        english = (TextView) findViewById(R.id.english);
        fanchou = (TextView) findViewById(R.id.fanchou);
        backgound = (TextView) findViewById(R.id.background);
        chinese.setText(info.getChinese());
        english.setText(info.getEnglish());
        fanchou.setText(info.getFanChou());
        backgound.setText(info.getBackground());
    }

}
