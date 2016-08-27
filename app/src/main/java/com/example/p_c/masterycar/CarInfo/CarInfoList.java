package com.example.p_c.masterycar.CarInfo;

import java.util.ArrayList;

/**
 * Created by p-c on 2016/7/24.
 */
public class CarInfoList {
    private static final CarInfoList mCarInfoList = new CarInfoList();
    private static ArrayList<CarInfo> carlist = new ArrayList();
    private int defaultcarposition = 0;

    private CarInfoList(){
    }

    public static CarInfoList getCarinfolist(){
        return mCarInfoList;
    }
    public void addcarinfo(CarInfo mcarinfo){
        carlist.add(mcarinfo);
    }
    public void deletecarinfo(int position){
        if(carlist.size() == 1){
            carlist.clear();
        }else {
            carlist.remove(position);
        }
    }
    public boolean isEmpty(){
        return carlist.isEmpty();
    }
    public int getDefaultcarposition() {
        return defaultcarposition;
    }

    public void setDefaultcarposition(int defaultcarposition) {
        this.defaultcarposition = defaultcarposition;
    }
    public CarInfo getcarinfo(int position){
        return carlist.get(position);
    }
    public ArrayList getcarList(){
        return carlist;
    }
    public int listSize(){
        return carlist.size();
    }
}
