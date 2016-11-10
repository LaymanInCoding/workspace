package com.witmoon.xmb.activity.mbq.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;

import java.util.ArrayList;

public class CircleCategoryAdapter extends  RecyclerView.Adapter<CircleCategoryAdapter.ViewHolder> {
    private OnItemClickListener mOnClickListener;
    private int current_selected = 0;

    public interface OnItemClickListener {
        void onItemButtonClick(int position);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_circle_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        if(current_selected == position){
            holder.circleCategoryTextView.setTextColor(cs_selected);
        }else{
            holder.circleCategoryTextView.setTextColor(cs_normal);
        }
        holder.circleCategoryTextView.setText(mDatas.get(position));
        if(mOnClickListener != null){
            holder.circleCategoryTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    current_selected = position;
                    mOnClickListener.onItemButtonClick(position);
                    }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView circleCategoryTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            circleCategoryTextView = (TextView)itemView.findViewById(R.id.circle_category_name);
        }
    }

    public CircleCategoryAdapter(ArrayList<String> mDatas, Context context) {
        Resources resource = context.getResources();
        cs_selected =  resource.getColorStateList(R.color.mbq_circle_category_name_selected);
        cs_normal =  resource.getColorStateList(R.color.mbq_circle_category_name_normal);
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<String> mDatas;
    private Context context;
    private ColorStateList cs_selected,cs_normal;
}
