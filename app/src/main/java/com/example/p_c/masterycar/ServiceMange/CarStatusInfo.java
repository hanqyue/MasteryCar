package com.example.p_c.masterycar.ServiceMange;

import java.io.Serializable;

/**
 * Created by p-c on 2016/7/19.
 */
public class CarStatusInfo implements Serializable{


    public int getCarSpeed() {
        return carSpeed;
    }

    public void setCarSpeed(int carSpeed) {
        this.carSpeed = carSpeed;
    }

    public int getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(int waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public float getGasNum() {
        return gasNum;
    }

    public void setGasNum(float gasNum) {
        this.gasNum = gasNum;
    }

    public int getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(int batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public float getRevSpeed() {
        return revSpeed;
    }

    public void setRevSpeed(float revSpeed) {
        this.revSpeed = revSpeed;
    }

    private int carSpeed;
    private int waterTemperature;
    private float gasNum;
    private int batteryVoltage;
    private float revSpeed;

}
