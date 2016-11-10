package com.witmoon.xmb.activity.babycenter.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmb.util.XmbUtils;

import java.util.ArrayList;

public class NumberPickerAdapter extends  RecyclerView.Adapter<NumberPickerAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mbq_circle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
       public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public NumberPickerAdapter(ArrayList<String> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<String> mDatas;
    private Context context;
}
