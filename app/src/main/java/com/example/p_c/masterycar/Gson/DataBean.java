package com.example.p_c.masterycar.Gson;

/**
 * Created by lovecd on 2016/4/3.
 */
public class DataBean {


    public String getName() {
        return name;
    }

    public String name;
    public String address;
    public String brandname;

    public Price getPrice() {
        return price;
    }

    public Price  price;


    public String getBrandname() {
        return brandname;
    }


    public String getAddress() {
        return address;
    }


    @Override
    public String toString() {
        return "name="+getName()+"adress"+getAddress()+"brandname"+getBrandname()+"price";
    }
}
