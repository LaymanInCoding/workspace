package com.witmoon.xmb.activity.me.adapter;

import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.FavoriteGoods;

/**
 * 我的收藏之商品适配器
 * Created by zhyh on 2015/6/23.
 */
public class MyFavoriteGoodsAdapter extends BaseRecyclerAdapter {

    private OnAddCartClickListener mOnAddCartClickListener;

    public void setOnAddCartClickListener(
            OnAddCartClickListener onAddCartClickListener) {
        mOnAddCartClickListener = onAddCartClickListener;
    }

    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_goods,
                parent, false);
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new MyFavoriteGoodsViewHolder(viewType, view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, int position) {
        MyFavoriteGoodsViewHolder fgViewHolder = (MyFavoriteGoodsViewHolder) holder;

        final FavoriteGoods goods = (FavoriteGoods) _data.get(position);

        fgViewHolder.title.setText(goods.getTitle());
        fgViewHolder.price.setText(goods.getPrice());
        fgViewHolder.photo.setImageURI(Uri.parse(goods.getPhoto()));
//        fgViewHolder.discount.setText(goods.getDiscount());
        fgViewHolder.marketPrice.setText(goods.getMarketPrice());

        if (mOnAddCartClickListener != null) {
            fgViewHolder.addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnAddCartClickListener.addToCart(goods.getGoodsId());
                }
            });
        }
    }

    public interface OnAddCartClickListener {
        void addToCart(String goodsId);
    }

    // 商品列表ViewHolder
    public static class MyFavoriteGoodsViewHolder extends BaseRecyclerAdapter.ViewHolder {

        public SimpleDraweeView photo;
        public TextView title;
        public TextView price;
        public TextView discount;
        public TextView marketPrice;

        public TextView addCart;

        public MyFavoriteGoodsViewHolder(int viewType, View itemView) {
            super(viewType, itemView);

            photo = (SimpleDraweeView) itemView.findViewById(R.id.goods_image);
            title = (TextView) itemView.findViewById(R.id.goods_title);
            price = (TextView) itemView.findViewById(R.id.price);
//            discount = (TextView) itemView.findViewById(R.id.goods_discount);
            marketPrice = (TextView) itemView.findViewById(R.id.market_price);
            marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            addCart = (TextView) itemView.findViewById(R.id.add_to_cart);
        }
    }
}
