package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.main.fragment.MyBabyInfoFragment;
import com.witmoon.xmb.activity.service.ServiceShopDetailActivity;
import com.witmoon.xmb.activity.specialoffer.Baby_DetailsActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.RecordDetails;
import com.witmoon.xmb.model.service.Shop;
import com.witmoon.xmb.ui.widget.SortTextView;

import java.util.ArrayList;

/**
 * Created by ZCM on 2016/1/25
 */
public class ShopAdapter extends  RecyclerView.Adapter<ShopAdapter.ViewHolder>{

    private ArrayList<Shop> mList;
    private Context mContext;

    public ShopAdapter(ArrayList<Shop> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Shop shop = mList.get(position);
        holder.shop_name.setText(shop.getShop_name());
        holder.shop_desc.setText(shop.getShop_desc());
        holder.shop_nearby_subway.setText(shop.getShop_nearby_subway());
        holder.shop_city.setText(shop.getCity());
        Netroid.displayBabyImage(shop.getShop_logo(), holder.shop_logo);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ServiceShopDetailActivity.class);
                intent.putExtra("shop_id",shop.getShop_id());
                intent.putExtra("comment_num",shop.getShop_comment_cnt());
                intent.putExtra("product_num",shop.getShop_products_num());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView shop_name;
        TextView shop_desc;
        ImageView shop_logo;
        TextView shop_nearby_subway;
        TextView shop_city;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            shop_name = (TextView) itemView.findViewById(R.id.shop_name);
            shop_desc = (TextView) itemView.findViewById(R.id.shop_desc);
            shop_nearby_subway = (TextView) itemView.findViewById(R.id.shop_nearby_subway);
            shop_logo = (ImageView) itemView.findViewById(R.id.shop_logo);
            shop_city = (TextView) itemView.findViewById(R.id.shop_city);
            view = itemView;
        }
    }
}
