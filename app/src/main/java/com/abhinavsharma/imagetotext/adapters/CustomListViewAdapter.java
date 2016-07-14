package com.abhinavsharma.imagetotext.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abhinavsharma.imagetotext.R;
import com.abhinavsharma.imagetotext.ViewHolder;

import java.util.ArrayList;

/**
 * Created by abhinavsharma on 14/07/16.
 */
public class CustomListViewAdapter extends BaseAdapter {

    private ArrayList<String> arrayList;
    private Context mContext;
    private ViewHolder viewHolder;
    private LayoutInflater mLayoutInflater;

    public CustomListViewAdapter(Context mContext, ArrayList<String> arrayList) {
        this.arrayList = arrayList;
        this.mContext = mContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public String getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.single_text_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvString = (TextView) convertView.findViewById(R.id.tv_text);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvString.setText(getItem(position));
        return convertView;
    }

}
