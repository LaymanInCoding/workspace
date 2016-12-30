package com.witmoon.xmb.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;

import java.util.ArrayList;

/**
 * Created by de on 2016/10/10.
 */
public class TagAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mDataList;
    private int[] mBackGround = {R.drawable.bg_rounded_radiobtn, R.drawable.bg_hotkey_one,
            R.drawable.bg_hotkey_two, R.drawable.bg_hotkey_three, R.drawable.bg_hotkey_four,
            R.drawable.bg_hotkey_five, R.drawable.bg_hotkey_six};

    public TagAdapter(Context context, ArrayList<String> data) {
        this.mContext = context;
        this.mDataList = data;
        Logger.e(mDataList.toString());

    }

    @Override
    public int getCount() {
        Logger.e(mDataList.toString());


        if (mDataList != null) {
            Logger.e("" + mDataList.size());
            return mDataList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        Logger.e(mDataList.toString());
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Logger.e(mDataList.toString());
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tag, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_tag);
        textView.setBackgroundResource(mBackGround[position % 7]);
        String t = mDataList.get(position);
        Logger.e(t);
        textView.setText(t);
        view.setTag(t);
        return view;
    }
}
