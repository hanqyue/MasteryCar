package com.example.p_c.masterycar.GMap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.example.p_c.masterycar.R;

/**
 * 首页面
 */
public class IndexActivity extends Activity {


    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                startActivity(new Intent(IndexActivity.this,BaseActivity.class));
            }
        }
    };
    private String[] examples = new String[]

            {
                    "基本导航页"
            };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        initView();

        Toast.makeText(this, "demo数量:" + examples.length, Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, examples));
        setTitle("导航SDK " + AMapNavi.getVersion());
        listView.setOnItemClickListener(mItemClickListener);
    }


    /**
     * 返回键处理事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            System.exit(0);// 退出程序
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
