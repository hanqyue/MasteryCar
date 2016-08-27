package com.example.p_c.masterycar.POiMessage;

/**
 * Created by 李思言 on 2016/4/19.
 */
public class PoiMessage {

    private String poi_name;
    private String poi_address;

    public  PoiMessage(String name,String adress){

        poi_address=adress;

        poi_name=name;

    }


    public String getPoi_address() {
        return poi_address;
    }

    public void setPoi_address(String poi_address) {
        this.poi_address = poi_address;
    }

    public String getPoi_name() {
        return poi_name;
    }

    public void setPoi_name(String poi_name) {
        this.poi_name = poi_name;
    }


}
