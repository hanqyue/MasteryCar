package com.example.p_c.masterycar.GMap;

import com.example.p_c.masterycar.ActivityLife.ActivityLife;

/**
 * Created by 李思言 on 2016/7/19.
 */
public class startVideo {

    private double startlon;

    private double startlat;

    private static double slon;

    private static double slat;

    private static int number=0;

    private static int stop=0;


public startVideo(double lon,double lat){

    startlat=lat;

    startlon=lon;


}

    public boolean move(){

        if (number==0){

            slat=startlat;
            slon=startlon;
            number=1;
        }

            if (slat != startlat || slon != startlon) {

                slat = startlat;
                slon = startlon;
                if (number==1) {
                    number++;
                    return true;
                }
            }

        if (slat==startlat&&slon==startlon&&number>0){

            stop++;

            if (stop>10){
                number=0;
                stop=0;
               ActivityLife.destoryActivity("MyVideo");
                return false;
            }
        }



        return false;

    }




}
