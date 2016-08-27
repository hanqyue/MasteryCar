package com.example.p_c.masterycar.Gson;

import java.util.List;

/**
 * Created by lovecd on 2016/4/3.
 */
public class Result {

    public List<DataBean> getData() {
        return data;
    }

    public List<DataBean>data;

    @Override
    public String toString() {
        return "Result["+getData()+"]";
    }
}
