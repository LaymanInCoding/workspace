package com.witmoon.xmb.activity.mabao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.util.DensityUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2016/3/17.
 */
public class AddordableAdapter extends  RecyclerView.Adapter<AddordableAdapter.ViewHolder> {
    private OnItemClickListener mOnItemButtonClickListener;
    private int width;

    public interface OnItemClickListener {
        void onItemButtonClick(Map<String, String> map);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnItemButtonClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_affordable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = mDatas.get(position);
        int top = 5;
        if (position % 2 == 0){
            holder.view.setPadding(DensityUtils.dp2px(context,5),DensityUtils.dp2px(context,top),DensityUtils.dp2px(context,2.5f),0);
        }else{
            holder.view.setPadding(DensityUtils.dp2px(context,2.5f),DensityUtils.dp2px(context,top),DensityUtils.dp2px(context,5),0);
        }
        holder.goods_img.setLayoutParams(new LinearLayout.LayoutParams(width/2 - DensityUtils.dp2px(context,15),width/2 - DensityUtils.dp2px(context,15)));
        holder.goods_price.setText("¥"+map.get("goods_price"));
        holder.goods_img.setImageResource(R.mipmap.pic_goods_placeholder);
        Netroid.displayImage(map.get("goods_thumb"), holder.goods_img);
        holder.goods_name.setText(map.get("goods_name"));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommodityDetailActivity.start(context, map.get("goods_id"));
            }
        });
        if (red_nu.equals("1"))
        {
            holder.goods_price.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView goods_img;
        TextView goods_name;
        TextView goods_price;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            goods_img = (ImageView) itemView.findViewById(R.id.h_goods_img);
            goods_name = (TextView) itemView.findViewById(R.id.goods_name);
            goods_price = (TextView) itemView.findViewById(R.id.goods_price);
            view = itemView;
        }
    }

    public AddordableAdapter(ArrayList<Map<String, String>> mDatas, Context context,String red_nu) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.mDatas = mDatas;
        this.context = context;
        this.width = wm.getDefaultDisplay().getWidth();
        this.red_nu = red_nu;
    }

    private ArrayList<Map<String, String>> mDatas;
    private Context context;
    private String red_nu;
}
