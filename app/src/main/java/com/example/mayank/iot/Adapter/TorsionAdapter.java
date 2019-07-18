package com.example.mayank.iot.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mayank.iot.Model.TorsionModel;
import com.example.mayank.iot.R;
import com.example.mayank.iot.Utility.Util;

import java.util.List;

/**
 * Created by Mayank on 3/16/2017.
 */
public class TorsionAdapter extends BaseAdapter {
    Context context;
    List<TorsionModel.FeedsEntity> torsionResult;

    public TorsionAdapter(Context context, List<TorsionModel.FeedsEntity> items) {
        this.context = context;
        this.torsionResult = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtEntryId, txtDate, txtSampleName, txtCountOne, txtCountTwo, txtCountThree, txtAverage, txtClientResult, txtOverallResult;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.frag_tosion_header, null);
            holder = new ViewHolder();
            holder.txtEntryId = (TextView) convertView.findViewById(R.id.serail_no);
            holder.txtDate = (TextView) convertView.findViewById(R.id.date);
            holder.txtSampleName = (TextView) convertView.findViewById(R.id.sample_name);
            holder.txtCountOne = (TextView) convertView.findViewById(R.id.count_one);
            holder.txtCountTwo = (TextView) convertView.findViewById(R.id.count_two);
            holder.txtCountThree = (TextView) convertView.findViewById(R.id.count_three);
            holder.txtAverage = (TextView) convertView.findViewById(R.id.average);
            holder.txtClientResult = (TextView) convertView.findViewById(R.id.client_result);
            holder.txtOverallResult = (TextView) convertView.findViewById(R.id.result_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TorsionModel.FeedsEntity torsion = torsionResult.get(position);

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#0000FF"));
            holder.txtEntryId.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtDate.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtSampleName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtCountOne.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtCountTwo.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtCountThree.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtAverage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtClientResult.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.txtEntryId.setTextColor(Color.parseColor("#000000"));
            holder.txtDate.setTextColor(Color.parseColor("#000000"));
            holder.txtSampleName.setTextColor(Color.parseColor("#000000"));
            holder.txtCountOne.setTextColor(Color.parseColor("#000000"));
            holder.txtCountTwo.setTextColor(Color.parseColor("#000000"));
            holder.txtCountThree.setTextColor(Color.parseColor("#000000"));
            holder.txtAverage.setTextColor(Color.parseColor("#000000"));
            holder.txtClientResult.setTextColor(Color.parseColor("#000000"));
        }
        holder.txtEntryId.setText(torsion.getEntry_id() + "");
        holder.txtDate.setText(torsion.getCreated_at().split("T")[0] + "");
        holder.txtSampleName.setText(torsion.getField2() + "");
        holder.txtCountOne.setText(torsion.getField3() + "");
        holder.txtCountTwo.setText(torsion.getField4() + "");
        holder.txtCountThree.setText(torsion.getField5() + "");
        holder.txtAverage.setText(torsion.getField6()+ "");
        holder.txtClientResult.setText(torsion.getField7() + "");
        holder.txtOverallResult.setText("");
        float maxValue, minValue;

        if (!torsion.getField7().equalsIgnoreCase("") && !torsion.getField6().equalsIgnoreCase("")) {
            if (Float.parseFloat(torsion.getField7()) > Float.parseFloat(torsion.getField6())) {
                maxValue = Float.parseFloat(torsion.getField7());
                minValue = Float.parseFloat(torsion.getField6());
            } else {
                maxValue = Float.parseFloat(torsion.getField6());
                minValue = Float.parseFloat(torsion.getField7());
            }
            float deviation = maxValue - minValue;
            if (deviation > Util.MAX_DEVIATION) {
                holder.txtOverallResult.setText("FAIL");
                holder.txtOverallResult.setTextColor(Color.parseColor("#FFFFFF"));
                holder.txtOverallResult.setBackgroundColor(Color.parseColor("#FF0000"));
            } else {
                holder.txtOverallResult.setText("PASS");
                holder.txtOverallResult.setTextColor(Color.parseColor("#000000"));
                holder.txtOverallResult.setBackgroundColor(Color.parseColor("#008000"));
            }
        } else {
            holder.txtOverallResult.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return torsionResult.size();
    }

    @Override
    public Object getItem(int position) {
        return torsionResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return torsionResult.indexOf(getItem(position));
    }
}
