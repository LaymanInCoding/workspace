package com.witmoon.xmb.activity.friendship.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.witmoon.xmb.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zhyh on 2015/8/1.
 */
public class GridPictureAdapter extends BaseAdapter {

    private List<Map<String, Object>> mDataList;

    public GridPictureAdapter(List<Map<String, Object>> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .layout_simple_image_view, parent, false);
            holder = new ImageViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ImageViewHolder) convertView.getTag();
        }

        holder.image.setImageBitmap((Bitmap) mDataList.get(position).get("bitmap"));

        return convertView;
    }

    public class ImageViewHolder {
        public ImageView image;
    }
}
