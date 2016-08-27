package com.example.p_c.masterycar.Gson;

/**
 * Created by 李思言 on 2016/5/3.
 */
public class gas_price {

    public String getE90() {
        return E90;
    }

    public String getE93() {
        return E93;
    }

    public int getIcon() {
        return icon;
    }

    private String E90;
    private String E93;
    private int  icon;

    public gas_price(String E90,String E93,int icon){

        this.E90=E90;
        this.E93=E93;
        this.icon=icon;
    }

}
