package com.witmoon.xmb.activity.mbq.adapter;

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
import java.util.HashMap;

public class CircleAdapter extends  RecyclerView.Adapter<CircleAdapter.ViewHolder> {
    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(CircleCategory circleCategory);
        void onItemButtonClick(int circle_id);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mbq_circle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final CircleCategory map = mDatas.get(position);
        Netroid.displayBabyImage(map.getCircle_logo(),
                holder.circleImgView);
        holder.circleNameView.setText(map.getCircle_name());
        holder.circleDescView.setText(map.getCircle_desc());
        if(mOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(map);
                }
            });
            holder.circleJoinView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemButtonClick(map.getCircle_id());
                }
            });
        }
        if(XmbUtils.check_is_join(context,map.getCircle_id())){
             holder.circleJoinView.setImageResource(R.mipmap.mbq_minus);
        }else{
            holder.circleJoinView.setImageResource(R.mipmap.mbq_add);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView circleImgView;
        public TextView circleNameView,circleDescView;
        public ImageView circleJoinView;
        public ViewHolder(View itemView) {
            super(itemView);
            circleImgView = (ImageView)itemView.findViewById(R.id.circle_img);
            circleNameView = (TextView)itemView.findViewById(R.id.circle_name);
            circleDescView = (TextView)itemView.findViewById(R.id.circle_desc);
            circleJoinView = (ImageView)itemView.findViewById(R.id.circle_join);
        }
    }

    public CircleAdapter(ArrayList<CircleCategory> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<CircleCategory> mDatas;
    private Context context;
}
