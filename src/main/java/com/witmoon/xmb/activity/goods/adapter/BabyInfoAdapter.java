package com.witmoon.xmb.activity.goods.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ZCM on 2016/1/25.
 */
public class BabyInfoAdapter extends BaseAdapter {

    private List<Map<String, Object>> mList;

    public BabyInfoAdapter(List<Map<String,Object>> data)
    {
        this.mList=data;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BabyInfoHolder holder;
        if (null == convertView) {
            holder = new BabyInfoHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_of_baby, parent, false);
        }
        return convertView;
    }


    class BabyInfoHolder {

        TextView WeekDay;

        TextView Day;

        TextView DiaryInfo;

        ImageView PublishPic;

    }
}
