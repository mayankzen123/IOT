package com.example.mayank.iot.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mayank.iot.Model.DHT11Model;
import com.example.mayank.iot.R;

import java.util.List;

/**
 * Created by Mayank on 3/16/2017.
 */
public class Dht11Adapter extends BaseAdapter {
    Context context;
    List<DHT11Model.FeedsEntity> dht11Result;

    public Dht11Adapter(Context context, List<DHT11Model.FeedsEntity> items) {
        this.context = context;
        dht11Result = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtEntryId, txtDate, txtTemperature, txtHumidity;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.frag_dht11_header, null);
            holder = new ViewHolder();
            holder.txtEntryId = (TextView) convertView.findViewById(R.id.serail_no);
            holder.txtDate = (TextView) convertView.findViewById(R.id.date);
            holder.txtTemperature = (TextView) convertView.findViewById(R.id.temperature);
            holder.txtHumidity = (TextView) convertView.findViewById(R.id.humidity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DHT11Model.FeedsEntity dht11 = dht11Result.get(position);

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#0000FF"));
            holder.txtEntryId.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtDate.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtTemperature.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtHumidity.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.txtEntryId.setTextColor(Color.parseColor("#000000"));
            holder.txtDate.setTextColor(Color.parseColor("#000000"));
            holder.txtTemperature.setTextColor(Color.parseColor("#000000"));
            holder.txtHumidity.setTextColor(Color.parseColor("#000000"));
        }
        holder.txtEntryId.setText(dht11.getEntry_id() + "");
        holder.txtDate.setText(dht11.getCreated_at().split("T")[0] + "");
        holder.txtTemperature.setText(dht11.getField1() + "");
        holder.txtHumidity.setText(dht11.getField2() + "");
        return convertView;
    }

    @Override
    public int getCount() {
        return dht11Result.size();
    }

    @Override
    public Object getItem(int position) {
        return dht11Result.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dht11Result.indexOf(getItem(position));
    }
}
