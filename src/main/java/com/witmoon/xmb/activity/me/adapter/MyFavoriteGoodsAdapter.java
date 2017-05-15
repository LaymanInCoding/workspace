package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.FavoriteGoods;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的收藏之商品适配器
 * Created by zhyh on 2015/6/23.
 */
public class MyFavoriteGoodsAdapter extends SwipeMenuAdapter<RecyclerView.ViewHolder> {

    public static final int HEADER_TYPE = 0;
    public static final int NORMAL_TYPE = 1;
    public static final int FOOTER_TYPE = 2;
    public static final int NOMORE_TYPE = 3;

    private Context context;
    private List<FavoriteGoods> mDatas;
    private List<View> mFooterViews;
    private boolean noMoreData;

    public void setNoMoreData(boolean bool) {
        noMoreData = bool;
    }

    public boolean getNoMoreData() {
        return this.noMoreData;
    }

    public int getFooterCount() {
        return mFooterViews.size();
    }

    public void removeFooterView() {
        mFooterViews.clear();
    }

    public void addFooterView(View view) {
        mFooterViews.add(view);
    }

    public MyFavoriteGoodsAdapter(Context context, List<FavoriteGoods> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        mFooterViews = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position != getItemCount() - 1) {
            return NORMAL_TYPE;
        }
        if (position == getItemCount() - 1 && !getNoMoreData()) {
            return FOOTER_TYPE;
        }
        return NOMORE_TYPE;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_TYPE) {
            return LayoutInflater.from(context).inflate(R.layout.view_load_more, parent, false);
        } else if (viewType == NORMAL_TYPE) {
            return LayoutInflater.from(context).inflate(R.layout.item_favorite_goods,
                    parent, false);
        } else return LayoutInflater.from(context).inflate(R.layout.view_no_message, parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        if (viewType == FOOTER_TYPE) {
            return new FooterHolder(realContentView);
        }
        if (viewType == NORMAL_TYPE) {
            return new MyFavoriteGoodsViewHolder(realContentView);
        } else
            return new NoMoreHolder(realContentView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == NORMAL_TYPE) {
            if (holder instanceof MyFavoriteGoodsViewHolder) {
                MyFavoriteGoodsViewHolder holder1 = (MyFavoriteGoodsViewHolder) holder;
                final FavoriteGoods goods = mDatas.get(position);
                holder1.title.setText(goods.getTitle());
                holder1.price.setText(goods.getPrice());
                Netroid.displayImage(goods.getPhoto(),holder1.photo);
//                holder1.photo.setImageURI(Uri.parse(goods.getPhoto()));
                holder1.marketPrice.setText(goods.getMarketPrice());
                holder1.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommodityDetailActivity.start(context, goods.getGoodsId());
                    }
                });
            }
        } else if (getItemViewType(position) == FOOTER_TYPE) {
            return;
        } else return;
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + getFooterCount();
    }

    // 商品列表ViewHolder
    public static class MyFavoriteGoodsViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView photo;
        public TextView title;
        public TextView price;
        public TextView discount;
        public TextView marketPrice;

        public TextView addCart;

        public MyFavoriteGoodsViewHolder(View itemView) {
            super(itemView);

            photo = (SimpleDraweeView) itemView.findViewById(R.id.goods_image);
            title = (TextView) itemView.findViewById(R.id.goods_title);
            price = (TextView) itemView.findViewById(R.id.price);
            marketPrice = (TextView) itemView.findViewById(R.id.market_price);
            marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    public static class NoMoreHolder extends RecyclerView.ViewHolder {
        public NoMoreHolder(View itemView) {
            super(itemView);
        }
    }
}
