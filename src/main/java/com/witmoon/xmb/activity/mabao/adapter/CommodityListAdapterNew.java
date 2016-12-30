package com.witmoon.xmb.activity.mabao.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.goods.adapter.CommodityListItemViewHolder;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Goods;
import com.witmoon.xmb.util.DensityUtils;

import java.util.Map;

/**
 * Created by de on 2016/12/2.
 */
public class CommodityListAdapterNew extends BaseRecyclerAdapter {
    public Activity activity;

    public CommodityListAdapterNew(Activity activity) {
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
        final Map<String, String> map = (Map<String, String>) _data.get(position);
        CommodityListItemViewHolder cHolder = (CommodityListItemViewHolder) holder;
        int top = 5;
        if (position % 2 == 0) {
            cHolder.view.setPadding(DensityUtils.dp2px(activity, 5), DensityUtils.dp2px(activity, top), DensityUtils.dp2px(activity, 2.5f), 0);
        } else {
            cHolder.view.setPadding(DensityUtils.dp2px(activity, 2.5f), DensityUtils.dp2px(activity, top), DensityUtils.dp2px(activity, 5), 0);
        }
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        cHolder.mImageView.setLayoutParams(new LinearLayout.LayoutParams(width / 2 - DensityUtils.dp2px(activity, 15), width / 2 - DensityUtils.dp2px(activity, 15)));
        cHolder.mImageView.setImageResource(R.mipmap.pic_goods_placeholder);
        Netroid.displayImage(map.get("goods_thumb"), cHolder.mImageView);
        cHolder.mTitleText.setText(map.get("goods_name"));
        cHolder.mPriceText.setText("¥" + map.get("goods_price"));
        cHolder.view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CommodityDetailActivity.start(activity, map.get("goods_id"));
            }
        });

    }
}
