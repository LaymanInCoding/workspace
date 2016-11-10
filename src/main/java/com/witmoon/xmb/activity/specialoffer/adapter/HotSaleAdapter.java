package com.witmoon.xmb.activity.specialoffer.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Market;

/**
 * 热卖(最后疯抢)适配器
 * Created by zhyh on 2015/5/11.
 */
public class HotSaleAdapter extends BaseRecyclerAdapter {

    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market, parent,
                false);
    }

    @Override
    protected BaseRecyclerAdapter.ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new HotSaleViewHolder(viewType, view);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindItemViewHolder(BaseRecyclerAdapter.ViewHolder holder, int position) {
        super.onBindItemViewHolder(holder, position);

        HotSaleViewHolder viewHolder = (HotSaleViewHolder) holder;

        Market market = (Market) _data.get(position);
        viewHolder.goodsTitleText.setText(market.getName());
        viewHolder.goodsRemainingTimeText.setText(market.getTimeDesc());
        //加载
        Netroid.displayImage(market.getImage(),viewHolder.goodsImage);
    }

    // ViewHolder
    public class HotSaleViewHolder extends BaseRecyclerAdapter.ViewHolder {

        ImageView goodsImage;
        TextView goodsDiscountText;
        TextView goodsTitleText;
        TextView goodsRemainingTimeText;

        public HotSaleViewHolder(int viewType, View v) {
            super(viewType, v);
            goodsImage = (ImageView) itemView.findViewById(R.id.goods_image);
            goodsDiscountText = (TextView) itemView.findViewById(R.id.goods_discount);
            goodsTitleText = (TextView) itemView.findViewById(R.id.goods_title);
            goodsRemainingTimeText = (TextView) itemView.findViewById(R.id.goods_remaining_time);
        }
    }
}
