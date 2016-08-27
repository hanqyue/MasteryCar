package com.example.p_c.masterycar.GMap;

import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.example.p_c.masterycar.R;

/**
 * 创建时间：11/10/15 15:08
 * 项目名称：newNaviDemo
 *
 * @author lingxiang.wang
 * @email lingxiang.wang@alibaba-inc.com
 * 类说明：
 * <p/>
 * 最普通的导航页面，如果你想处理一些诸如菜单点击，停止导航按钮点击的事件处理
 * 请implement AMapNaviViewListener
 */

public class BasicNaviActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }


}
