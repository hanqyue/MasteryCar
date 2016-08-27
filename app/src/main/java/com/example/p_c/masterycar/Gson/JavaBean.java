package com.example.p_c.masterycar.Gson;

/**
 * Created by lovecd on 2016/4/3.
 */
public class JavaBean {

    public Result result;



   public Result getResult() {
        return result;
    }



    public String toString(){
        return "Message["+getResult()+"]";
    }

}
