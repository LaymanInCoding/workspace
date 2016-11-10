package com.witmoon.xmb.activity.me.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseRecyclerAdapter;

import java.util.Map;

/**
 * 我的收藏之品牌适配器
 * Created by zhyh on 2015/6/23.
 */
public class MyFavoriteBrandAdapter extends BaseRecyclerAdapter {
    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_brand,
                parent, false);
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new BrandViewHolder(viewType, view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, int position) {
        BrandViewHolder vHolder = (BrandViewHolder) holder;
        Map<String, String> data = (Map<String, String>) _data.get(position);
        vHolder.brandNameView.setText(data.get("brandName"));
        vHolder.brandLogoView.setImageURI(Uri.parse(data.get("brandLogo")));
    }

    // 收藏品牌ViewHolder
    public static class BrandViewHolder extends BaseRecyclerAdapter.ViewHolder {

        public SimpleDraweeView brandLogoView;
        public TextView brandNameView;

        public BrandViewHolder(int viewType, View itemView) {
            super(viewType, itemView);
            brandLogoView = (SimpleDraweeView) itemView.findViewById(R.id.image);
            brandNameView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
