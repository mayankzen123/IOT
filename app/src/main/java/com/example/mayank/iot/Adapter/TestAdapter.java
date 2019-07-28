package com.example.mayank.iot.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mayank.iot.Model.TestCasesModel;
import com.example.mayank.iot.R;

import java.util.List;

public class TestAdapter extends BaseAdapter {
    Context context;
    List<TestCasesModel> testCasesModelList;

    private class ViewHolder {
        TextView txtCount;
        TextView txtTestId;

        private ViewHolder() {
        }
    }

    public TestAdapter(Context context2, List<TestCasesModel> items) {
        this.context = context2;
        this.testCasesModelList = items;
    }

    public void refreshItem(List<TestCasesModel> item) {
        this.testCasesModelList = item;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.frag_dht11_header, null);
            holder = new ViewHolder();
            holder.txtTestId = convertView.findViewById(R.id.serail_no);
            holder.txtCount = convertView.findViewById(R.id.date);
            convertView.findViewById(R.id.temperature).setVisibility(8);
            convertView.findViewById(R.id.humidity).setVisibility(8);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TestCasesModel testCase = this.testCasesModelList.get(position);
        if (position % 2 == 0) {
            holder.txtTestId.setTextColor(Color.parseColor("#ffffffff"));
            holder.txtCount.setTextColor(Color.parseColor("#ffffffff"));
            convertView.setBackgroundColor(Color.parseColor("#0000FF"));
        } else {
            holder.txtTestId.setTextColor(Color.parseColor("#ff000000"));
            holder.txtCount.setTextColor(Color.parseColor("#ff000000"));
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.txtTestId.setText(testCase.getTestNumber() + "");
        holder.txtCount.setText(testCase.getCount() + "");
        return convertView;
    }

    public int getCount() {
        return this.testCasesModelList.size();
    }

    public Object getItem(int position) {
        return this.testCasesModelList.get(position);
    }

    public long getItemId(int position) {
        return (long) this.testCasesModelList.indexOf(getItem(position));
    }
}
