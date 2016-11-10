package com.witmoon.xmb.activity.goods.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Goods;

/**
 * 商品列表适配器
 * Created by zhyh on 2015/7/11.
 */
public class CommodityListAdapter extends BaseRecyclerAdapter {
    public Activity activity;

    public CommodityListAdapter(Activity activity){
        this.activity = activity;
    }

    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_goods, parent,
                false);
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new CommodityListItemViewHolder(viewType, view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, int position) {
        final Goods goods = (Goods) _data.get(position);
        CommodityListItemViewHolder cHolder = (CommodityListItemViewHolder) holder;
        cHolder.mImageView.setImageURI(Uri.parse(goods.getThumb()));
        cHolder.mTitleText.setText(goods.getName());
        cHolder.mPriceText.setText(goods.getShopPriceDesc());
        cHolder.mMarketPriceText.setText(goods.getMarketPriceDesc());
        cHolder.mMarketPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        cHolder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                CommodityDetailActivity.start(activity, goods.getId());
            }
        });

    }
}
