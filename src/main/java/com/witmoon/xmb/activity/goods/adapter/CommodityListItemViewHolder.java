package com.witmoon.xmb.activity.goods.adapter;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseRecyclerAdapter;

/**
 * 商品列表ViewHolder
 * Created by zhyh on 2015/7/11.
 */
public class CommodityListItemViewHolder extends BaseRecyclerAdapter.ViewHolder {

    public SimpleDraweeView mImageView;
    public TextView mTitleText;
    public TextView mPriceText;
    public TextView mMarketPriceText;
    public View view;

    public CommodityListItemViewHolder(int viewType, View v) {
        super(viewType, v);
        view = v;
        mImageView = (SimpleDraweeView) v.findViewById(R.id.image);
        mTitleText = (TextView) v.findViewById(R.id.title);
        mPriceText = (TextView) v.findViewById(R.id.price);
        mMarketPriceText = (TextView) v.findViewById(R.id.market_price);
    }
}
