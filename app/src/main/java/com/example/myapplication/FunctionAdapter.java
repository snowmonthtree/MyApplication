package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FunctionAdapter extends ArrayAdapter<FunctionItem> {

    public FunctionAdapter(Context context, List<FunctionItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前位置的数据项
        FunctionItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.function_item_layout, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.functionTitle);
        textView.setText(item.getTitle());

        return convertView;
    }
}
