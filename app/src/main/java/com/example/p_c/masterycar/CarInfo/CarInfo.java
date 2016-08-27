package com.example.p_c.masterycar.CarInfo;

import java.io.Serializable;

/**
 * Created by p-c on 2016/7/24.
 */
public class CarInfo implements Serializable {
    public String getCarSign() {
        return carSign;
    }

    public void setCarSign(String carSign) {
        this.carSign = carSign;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getBodyLevel() {
        return bodyLevel;
    }

    public void setBodyLevel(String bodyLevel) {
        this.bodyLevel = bodyLevel;
    }

    public String getEngineNum() {
        return engineNum;
    }

    public void setEngineNum(String engineNum) {
        this.engineNum = engineNum;
    }

    public String getCarriageNum() {
        return carriageNum;
    }

    public void setCarriageNum(String carriageNum) {
        this.carriageNum = carriageNum;
    }

    private String carSign="";//标志
    private String carNum="";//车牌号
    private String carBrand="";// 车品牌
    private String carModel="";// 型号
    private String bodyLevel="";//车身级别
    private String engineNum="";//发动机号
    private String carriageNum="";//车架号

}
