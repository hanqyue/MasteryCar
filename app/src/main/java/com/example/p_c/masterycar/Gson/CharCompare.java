package com.example.p_c.masterycar.Gson;


import java.util.List;

/**
 * Created by 李思言 on 2016/4/16.
 */
class CharCompare {
    private String S1;
    private String S2;
    private String result;
    private List list;
    private int position;
    char ts1[]=new char[50];
    char ts2[]=new char[50];
    int array[]=new int [50];


    CharCompare(String s1, List s2){

        S1=s1;
        list=s2;
        ts1=S1.toCharArray();
    }

    String ResultName(){

        for(int i=0,size=list.size();i<size;i++){
            S2=list.get(i).toString();

            array[i]=compare(ts1,toCharArray(S2));

        }

        MaxPosition(array);
        result=list.get(position).toString();



        return result;
    }
    int ResultPosition(){

        return position;

    }
    char[] toCharArray(String string){

        return ts2=string.toCharArray();

    }
    void MaxPosition(int []array){

        int max=array[0];
        position=0;
        for (int i=0;i<array.length;i++){
            if (max<array[i]){
                max=array[i];
                position=i;
            }
        }

    }

    int compare(char []s1,char []s2){

        int po=0;

        for(int i=0;i<s1.length;i++){
            for(int j=0;j<s2.length;j++){
                if (s1[i]==s2[j]) {
                    po++;

                }

            }

        }

        return po;
    }
}






