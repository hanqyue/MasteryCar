package com.example.p_c.masterycar.Gson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.p_c.masterycar.R;

import java.util.LinkedList;

/**
 * Created by 李思言 on 2016/5/2.
 */
public class gasAdapter extends BaseAdapter {

    public LinkedList<gas_price> mprice;
    private Context context;

    public gasAdapter(LinkedList<gas_price>mprice,Context context){

        this.mprice=mprice;
        this.context=context;

    }


    public int getCount() {
        return mprice.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;

        if(convertView==null){
            convertView= LayoutInflater.from(context)
                    .inflate(R.layout.gas_money,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.imghead);
            viewHolder.text_aName= (TextView) convertView.findViewById(R.id.aName);
            viewHolder.text_aSpeak= (TextView) convertView.findViewById(R.id.aspeak);
            convertView.setTag(viewHolder);   //将Holder存储到convertView中
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.imageView.setBackgroundResource(mprice.get(position).getIcon());
        viewHolder.text_aName.setText(mprice.get(position).getE90());
        viewHolder.text_aSpeak.setText(mprice.get(position).getE93());
        return convertView;
    }
    static class ViewHolder{
        ImageView imageView;
        TextView text_aName;
        TextView text_aSpeak;

    }

}
