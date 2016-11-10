package com.witmoon.xmb.activity.mabao.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.api.Netroid;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2016/3/17.
 */
public class Duty_freeAdapter extends  RecyclerView.Adapter<Duty_freeAdapter.ViewHolder> {
    public Duty_freeAdapter(ArrayList<Map<String, String>> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView goods_img;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            goods_img = (ImageView) itemView.findViewById(R.id.goods_image);
            goods_img.setMaxWidth(MainActivity.screen_width);
            goods_img.setMaxHeight(MainActivity.screen_width);
            view = itemView;
        }
    }

    private ArrayList<Map<String, String>> mDatas;
    private Context context;



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_duty_free, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = mDatas.get(position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketPlaceActivity.start(context, map.get("act_id"));
            }
        });
        Netroid.displayAdImage(map.get("act_img"), holder.goods_img);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
