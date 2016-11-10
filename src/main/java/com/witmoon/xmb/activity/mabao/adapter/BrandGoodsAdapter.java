package com.witmoon.xmb.activity.mabao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
public class BrandGoodsAdapter extends  RecyclerView.Adapter<BrandGoodsAdapter.ViewHolder> {
    private OnItemClickListener mOnItemButtonClickListener;
    private int width;
    private ArrayList<Map<String, String>> mDatas;
    private Context mContext;
    public interface OnItemClickListener {
        void onItemButtonClick(Map<String, String> map);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnItemButtonClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_brand_goods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = mDatas.get(position);
        int top = 5;
        if (position % 2 == 0){
            holder.view.setPadding(DensityUtils.dp2px(mContext,5),DensityUtils.dp2px(mContext,top),DensityUtils.dp2px(mContext,2.5f),0);
        }else{
            holder.view.setPadding(DensityUtils.dp2px(mContext,2.5f),DensityUtils.dp2px(mContext,top),DensityUtils.dp2px(mContext,5),0);
        }
        holder.good_pic.setLayoutParams(new LinearLayout.LayoutParams(width/2 - DensityUtils.dp2px(mContext,15),width/2 - DensityUtils.dp2px(mContext,15)));
        Netroid.displayAdImage(map.get("goods_img").toString(), holder.good_pic);
        holder.good_price.setText( map.get("goods_price"));
        holder.market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.market_price.setText("市场价:" + map.get("market_price"));
        holder.good_desc.setText( map.get("goods_name"));
        holder.goods_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommodityDetailActivity.start(mContext, (String) map.get("goods_id"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout goods_container;
        ImageView good_pic;
        TextView good_desc;
        TextView market_price;
        TextView good_price;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            goods_container = (LinearLayout) itemView.findViewById(R.id.goods_container);
            good_pic = (ImageView) itemView.findViewById(R.id.goods_pic);
            good_desc = (TextView) itemView.findViewById(R.id.good_desc);
            market_price = (TextView) itemView.findViewById(R.id.market_price);
            good_price = (TextView) itemView.findViewById(R.id.good_price);
            view = itemView;
        }
    }

    public BrandGoodsAdapter(ArrayList<Map<String, String>> mDatas, Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.mDatas = mDatas;
        this.mContext = context;
        this.width = wm.getDefaultDisplay().getWidth();
    }


}
