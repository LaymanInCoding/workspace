package com.witmoon.xmb.activity.mbq.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;

import java.util.ArrayList;
import java.util.HashMap;

public class PostItemAdapter extends  RecyclerView.Adapter<PostItemAdapter.ViewHolder> {
    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mbq_post, parent, false);
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

    public PostItemAdapter(ArrayList<HashMap<String, Object>> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<HashMap<String,Object>> mDatas;
    private Context context;
}
