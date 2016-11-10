package com.witmoon.xmb.activity.specialoffer.adapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.ui.widget.TimeView;
import com.witmoon.xmb.util.DensityUtils;
import com.witmoon.xmb.util.TimeUtill;
import com.witmoon.xmb.wxapi.simcpux.Util;

import java.util.List;
import java.util.Map;

/**
 * Created by ZCM on 2016/1/19
 */
public class GroupBuyAdapter extends  RecyclerView.Adapter<ViewHolder>{

    public GroupBuyAdapter(List<Map<String, Object>> data,Context context,Activity activity) {
        this.data = data;
        this.context = context;
        this.activity = activity;
    }

    private List<Map<String, Object>> data;
    private Context context;
    private Activity activity;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view1 = LayoutInflater.from(context).inflate(R.layout.item_list_hotsale, parent, false);
                return new ViewHolderHot(view1);
            case 1:
                View view2 = LayoutInflater.from(context).inflate(R.layout.item_grid_hotsale, parent, false);
                return new ViewHolderMid(view2);
            case 2:
                View view3 = LayoutInflater.from(context).inflate(R.layout.header_group_bottom, parent, false);
                return new ViewHolderBanner(view3);
            case 3:
                View view4 = LayoutInflater.from(context).inflate(R.layout.item_grid_bottom, parent, false);
                return new ViewHolderBottom(view4);
            default:
                View view5 = LayoutInflater.from(context).inflate(R.layout.item_list_hotsale, parent, false);
                return new ViewHolderHot(view5);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String,Object> map = data.get(position);
        if(map.get("type").toString().equals("hot")) {
            Netroid.displayBabyImage(map.get("goods_thumb").toString(), ((ViewHolderHot) holder).goods_image);
            Netroid.displayBabyImage(map.get("origin_pic").toString(), ((ViewHolderHot) holder).country_img);
            ((ViewHolderHot) holder).goods_name.setText(map.get("goods_name").toString());
            ((ViewHolderHot) holder).goods_brief.setText(map.get("goods_brief").toString());
            ((ViewHolderHot) holder).shop_price.setText(map.get("org_price").toString());
            ((ViewHolderHot) holder).market_price.setText(map.get("market_price").toString());
            ((ViewHolderHot) holder).sale_sum.setText(map.get("salesnum").toString());
            if (TimeUtill.byeLit.size() > 0) {
                if (TimeUtill.byeGet(position, "time").equals("售罄")) {
                    ((ViewHolderHot) holder).end_time.setText(TimeUtill.byeGet(position, "time"));
                }
            }
            if (TimeUtill.byeLit.size() > 0)
                if (TimeUtill.byeGet(position, "time").equals("售罄")) {
                    ((ViewHolderHot) holder).end_time.setText(TimeUtill.byeGet(position, "time"));
                }
            if (TimeUtill.byeLit.size() > 0)
                ((ViewHolderHot) holder).end_time.setPosition(position, 3);
            ((ViewHolderHot) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommodityDetailActivity.start(context, map.get("goods_id").toString(), "");
                }
            });
        }else if(map.get("type").toString().equals("mid")){
            if (position % 2 == 0){
                ((ViewHolderMid) holder).view.setPadding(DensityUtils.dp2px(context, 8),0,DensityUtils.dp2px(context, 4),0);
            }else{
                ((ViewHolderMid) holder).view.setPadding(DensityUtils.dp2px(context, 4),0,DensityUtils.dp2px(context,8),0);
            }
            Netroid.displayBabyImage(map.get("goods_thumb").toString(), ((ViewHolderMid) holder).goods_image);
            ((ViewHolderMid) holder).goods_brief.setText(map.get("goods_name").toString());
            ((ViewHolderMid) holder).shop_price.setText(map.get("org_price").toString());
            ((ViewHolderMid) holder).shop_discount.setText(map.get("discount").toString() + "折");
            ((ViewHolderMid) holder).market_price.setText(map.get("market_price").toString());
            ((ViewHolderMid) holder).market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            ((ViewHolderMid) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommodityDetailActivity.start(context, map.get("goods_id").toString(), "");
                }
            });
        }else if(map.get("type").toString().equals("banner")){

        }else if(map.get("type").toString().equals("bot")){
            if(Integer.parseInt(map.get("index").toString()) % 2 == 0){
                ((ViewHolderBottom) holder).view.setPadding(DensityUtils.dp2px(context, 8),0,DensityUtils.dp2px(context, 4),0);
            }else{
                ((ViewHolderBottom) holder).view.setPadding(DensityUtils.dp2px(context,4),0,DensityUtils.dp2px(context, 8),0);
            }
            ((ViewHolderBottom) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupBuyActivity.start(context, map.get("goods_id").toString(), "");
                }
            });
            Netroid.displayBabyImage(map.get("ad_code").toString(), ((ViewHolderBottom)holder).goods_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Map<String,Object> hashMap = data.get(position);
        if(hashMap.get("type").toString().equals("hot")){
            return 0;
        }else if(hashMap.get("type").toString().equals("mid")){
            return 1;
        }else if(hashMap.get("type").toString().equals("banner")){
            return 2;
        }else if(hashMap.get("type").toString().equals("bot")){
            return 3;
        }
        return  0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolderMid extends RecyclerView.ViewHolder{
        SimpleDraweeView goods_image;
        TextView goods_name,shop_discount,goods_brief,shop_price,market_price;
        View view;

        public ViewHolderMid(View itemView) {
            super(itemView);
            goods_image = (SimpleDraweeView) itemView.findViewById(R.id.goods_image);
            goods_name = (TextView) itemView.findViewById(R.id.goods_name);
            shop_discount = (TextView) itemView.findViewById(R.id.shop_discount);
            goods_brief = (TextView) itemView.findViewById(R.id.goods_brief);
            shop_price = (TextView) itemView.findViewById(R.id.shop_price);
            market_price = (TextView) itemView.findViewById(R.id.market_price);
            view = itemView;
        }
    }

    public static class ViewHolderBanner extends RecyclerView.ViewHolder{

        public ViewHolderBanner(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolderHot extends ViewHolder{
        ImageView goods_image,country_img;
        TextView goods_name,sale_sum,goods_brief,shop_price,market_price;
        View view;
        TimeView end_time;

        public ViewHolderHot(View itemView) {
            super(itemView);
            goods_image = (ImageView) itemView.findViewById(R.id.goods_image);
            country_img = (ImageView) itemView.findViewById(R.id.country_img);
            goods_name = (TextView) itemView.findViewById(R.id.goods_name);
            sale_sum = (TextView) itemView.findViewById(R.id.sale_sum);
            goods_brief = (TextView) itemView.findViewById(R.id.goods_brief);
            end_time = (TimeView) itemView.findViewById(R.id.end_time);
            shop_price = (TextView) itemView.findViewById(R.id.goods_price);
            market_price = (TextView) itemView.findViewById(R.id.market_price);
            view = itemView;
        }
    }

    public static class ViewHolderBottom extends ViewHolder{
        ImageView goods_image;
        View view;

        public ViewHolderBottom(View itemView) {
            super(itemView);
            goods_image = (ImageView) itemView.findViewById(R.id.goods_image);
            view = itemView;
        }
    }
}

