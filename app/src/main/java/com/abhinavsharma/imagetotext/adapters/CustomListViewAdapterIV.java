package com.abhinavsharma.imagetotext.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhinavsharma.imagetotext.R;
import com.abhinavsharma.imagetotext.ViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by abhinavsharma on 15/07/16.
 */
public class CustomListViewAdapterIV extends BaseAdapter {
    private ArrayList<String> arrayList;
    private Context mContext;
    private ViewHolder viewHolder;
    private LayoutInflater mLayoutInflater;

    public CustomListViewAdapterIV(Context mContext, ArrayList<String> arrayList) {
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
            convertView = mLayoutInflater.inflate(R.layout.single_image_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        Picasso.with(mContext).load(new File(getItem(position)))
                .placeholder(R.drawable.camera_icon).into(viewHolder.imageView);

        return convertView;
    }
}
