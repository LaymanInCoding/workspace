package com.witmoon.xmb.activity.mbq.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.circle.CircleCategory;

import java.util.ArrayList;
import java.util.Map;

public class MyCircleAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener mOnItemClickListener;
    private ArrayList<Object> mDatas;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(CircleCategory circleCategory);
        void onItemButtonClick(int circle_id);
        void onItemMessageClick();
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(mDatas.get(position) instanceof CircleCategory){
            return  1;
        }else{
            Map<String,String> map = (Map<String, String>) mDatas.get(position);
            if (map.get("type").equals("no_circle")){
                return 2;
            }
        }
        return  0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.item_mbq_circle_title, parent, false);
                return new TextViewHolder(view);
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.item_mbq_circle, parent, false);
                return new CircleViewHolder(view);
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.item_mbq_no_circle, parent, false);
                return new NoCircleViewHolder(view);
        }
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = mDatas.get(position);
        if(object instanceof Map){
            final Map<String,String> map = (Map<String, String>) mDatas.get(position);
            if(map.get("type").equals("text")){
                ((TextViewHolder)holder).titleView.setText(map.get("title"));
            }else{
                ((NoCircleViewHolder)holder).conTextView.setText(map.get("title"));
                ((NoCircleViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemMessageClick();
                    }
                });
            }
        }else{
            final CircleCategory map = (CircleCategory) object;
            Netroid.displayBabyImage(map.getCircle_logo(),
                    ((CircleViewHolder) holder).circleImgView);
            ((CircleViewHolder) holder).circleNameView.setText(map.getCircle_name());
            ((CircleViewHolder) holder).circleDescView.setText(map.getCircle_desc());
            if (map.getUser_is_join()){
                ((CircleViewHolder) holder).circleJoinView.setImageResource(R.mipmap.mbq_minus);
            }else{
                ((CircleViewHolder) holder).circleJoinView.setImageResource(R.mipmap.mbq_add);
            }
            if(mOnItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(map);
                    }
                });
                ((CircleViewHolder) holder).circleJoinView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemButtonClick(map.getCircle_id());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        public TextViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView)itemView.findViewById(R.id.title);
        }
    }

    public static class NoCircleViewHolder extends RecyclerView.ViewHolder{
        public TextView conTextView;
        public NoCircleViewHolder(View itemView) {
            super(itemView);
            conTextView = (TextView) itemView.findViewById(R.id.no_circle);
        }
    }

    public static class CircleViewHolder extends RecyclerView.ViewHolder{
        public ImageView circleImgView;
        public TextView circleNameView,circleDescView;
        public ImageView circleJoinView;
        public CircleViewHolder(View itemView) {
            super(itemView);
            circleImgView = (ImageView)itemView.findViewById(R.id.circle_img);
            circleNameView = (TextView)itemView.findViewById(R.id.circle_name);
            circleDescView = (TextView)itemView.findViewById(R.id.circle_desc);
            circleJoinView = (ImageView)itemView.findViewById(R.id.circle_join);
        }
    }

    public MyCircleAdapter(ArrayList<Object> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

}
