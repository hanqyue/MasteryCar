package com.example.p_c.masterycar.POiMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.p_c.masterycar.R;

import java.util.LinkedList;

/**
 * Created by 李思言 on 2016/4/19.
 */
public class PoiAdapter extends BaseAdapter {
    private LinkedList<PoiMessage> messages;
    private Context context;

    public PoiAdapter(LinkedList<PoiMessage>messages,Context context){

        this.messages=messages;
        this.context=context;

    }
    @Override
    public int getCount() {
        return messages.size();
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

        convertView= LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,parent,false);
        TextView txt_adress= (TextView) convertView.findViewById(R.id.poi_address);
        TextView txt_Name= (TextView) convertView.findViewById(R.id.poi_name);
        txt_adress.setText(messages.get(position).getPoi_address());
        txt_Name.setText(messages.get(position).getPoi_name());
        return convertView;
    }
}
