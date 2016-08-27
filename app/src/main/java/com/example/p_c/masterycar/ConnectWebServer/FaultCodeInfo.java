package com.example.p_c.masterycar.ConnectWebServer;

/**
 * Created by p-c on 2016/8/7.
 */
public class FaultCodeInfo {
    private String faultCode;
    private String chinese;

    public String getEnglish() {
        return English;
    }

    public void setEnglish(String english) {
        English = english;
    }

    private String English;
    private String FanChou;
    private String background;

    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getFanChou() {
        return FanChou;
    }

    public void setFanChou(String fanChou) {
        FanChou = fanChou;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

}
