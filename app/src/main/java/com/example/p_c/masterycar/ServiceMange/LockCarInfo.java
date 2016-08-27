package com.example.p_c.masterycar.ServiceMange;

/**
 * Created by p-c on 2016/7/16.
 */
public class LockCarInfo {
    public boolean islock_car() {
        return islock_car;
    }

    public void setIslock_car(boolean islock_car) {
        this.islock_car = islock_car;
    }

    private boolean islock_car = false;//默认车门状态是开的

}
