package com.witmoon.xmb.activity.shopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.util.DensityUtils;

import java.util.ArrayList;
import java.util.Map;


public class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {
    private OnItemClickListener mOnItemClickListener;
    private int width;

    public interface OnItemClickListener {
        void onItemnClick(Map<String, String> map);
    }

    public void setOnItemClickListener(
            OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop_cat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = mDatas.get(position);
        int top = 15;
        if (position % 2 == 0) {
            holder.itemView.setPadding(DensityUtils.dp2px(context, 10), DensityUtils.dp2px(context, top), DensityUtils.dp2px(context, 7.5f), 0);
        } else {
            holder.itemView.setPadding(DensityUtils.dp2px(context, 7.5f), DensityUtils.dp2px(context, top), DensityUtils.dp2px(context, 10), 0);
        }
        holder.img.setLayoutParams(new LinearLayout.LayoutParams(layout_width, layout_height));
        Netroid.displayAdImage(map.get("url"), holder.img);
        if (this.mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemnClick(map);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }

    public CatAdapter(ArrayList<Map<String, String>> mDatas, Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.mDatas = mDatas;
        this.context = context;
        this.width = wm.getDefaultDisplay().getWidth();
        this.layout_width = (width - DensityUtils.dp2px(context, 35)) / 2;
        this.layout_height = layout_width * 213 / 348;
    }

    private int layout_width = 0, layout_height = 0;
    private ArrayList<Map<String, String>> mDatas;
    private Context context;
}
