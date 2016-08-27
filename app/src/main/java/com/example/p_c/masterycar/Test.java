package com.example.p_c.masterycar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.p_c.masterycar.CarVideo.CarVideo;

/**
 * Created by 李思言 on 2016/7/30.
 */
public class Test extends Activity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button= (Button) findViewById(R.id.buttonlsy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Test.this, CarVideo.class);
                startActivity(intent);
            }
        });
    }
}
