package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.activity.service.ProductDetailActivity;
import com.witmoon.xmb.activity.service.ServiceShopDetailActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.service.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2017/1/3.
 */
public class ShopAdapterNew extends RecyclerView.Adapter<ShopAdapterNew.ViewHolder> {
    private ArrayList<JSONObject> mList;
    private ArrayList<Product> mProducts = new ArrayList<>();
    private Context mContext;
    private int product_num;
    private RecyclerView hostRecycler;

    public ShopAdapterNew(ArrayList<JSONObject> mList, Context context, RecyclerView recyclerView) {
        this.mList = mList;
        this.mContext = context;
        this.hostRecycler = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject shop = mList.get(position);
        try {
            JSONArray productArray = shop.getJSONArray("products");
            String check = shop.getString("check");
            Log.e("check", check);
            for (int i = 0; i < productArray.length(); i++) {
                mProducts.add(Product.parse(productArray.getJSONObject(i)));
            }
            holder.product_no_more.setVisibility(View.GONE);
            holder.num_img.setImageResource(R.mipmap.down_img);
            product_num = shop.getInt("shop_products_num");
            if (productArray.length() == 0) {
                holder.product_container.setVisibility(View.GONE);
                holder.product_num_all.setVisibility(View.GONE);
            } else {
                holder.product_container.setVisibility(View.VISIBLE);
                holder.product_num_all.setVisibility(View.VISIBLE);
                setServiceProductsHeader(mProducts, holder, product_num, position, check);
            }
            mProducts.clear();
            String shop_id = shop.getString("shop_id");
            String shop_name = shop.getString("shop_name");
            holder.shop_name.setText(shop.getString("shop_name"));
            holder.shop_nearby_subway.setText(shop.getString("shop_nearby_subway"));
            holder.shop_city.setText(shop.getString("shop_city"));
            Netroid.displayBabyImage(shop.getString("shop_logo"), holder.shop_logo);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UmengStatic.registStat(mContext,"MabaoService1");

                    Intent intent = new Intent(mContext, ServiceShopDetailActivity.class);
                    intent.putExtra("shop_id", shop_id);
                    intent.putExtra("shop_name", shop_name);
                    mContext.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView shop_name;
        ImageView shop_logo;
        TextView shop_nearby_subway;
        TextView shop_city;
        View view;
        LinearLayout product_container;
        TextView product_num_all;
        View product_num_container;
        TextView product_no_more;
        ImageView num_img;

        public ViewHolder(View itemView) {
            super(itemView);
            shop_name = (TextView) itemView.findViewById(R.id.shop_name);
            shop_nearby_subway = (TextView) itemView.findViewById(R.id.shop_nearby_subway);
            shop_logo = (ImageView) itemView.findViewById(R.id.shop_logo);
            shop_city = (TextView) itemView.findViewById(R.id.shop_city);
            product_container = (LinearLayout) itemView.findViewById(R.id.product_container);
            product_num_container = itemView.findViewById(R.id.product_total_container);
            product_num_all = (TextView) itemView.findViewById(R.id.product_total_all);
            product_no_more = (TextView) itemView.findViewById(R.id.product_no_more);
            num_img = (ImageView) itemView.findViewById(R.id.num_img);
            view = itemView.findViewById(R.id.item_top);
        }
    }

    private void setServiceProductsHeader(ArrayList<Product> products, ViewHolder holder, int product_num, int position, String check) {
        final LinearLayout product_container = holder.product_container;
        product_container.removeAllViews();
        for (int i = 0; i < products.size(); i++) {
            final Product product = products.get(i);
            LinearLayout productLinearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_service_list, product_container, false);
            if (i >= 2 && !check.equals(position + "")) {
                productLinearLayout.setVisibility(View.GONE);
            }

            productLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UmengStatic.registStat(mContext,"MabaoService1");

                    Intent intent = new Intent(mContext, ProductDetailActivity.class);
                    intent.putExtra("product_id", product.getProduct_id());
                    mContext.startActivity(intent);
                }
            });
            TextView product_name = (TextView) productLinearLayout.findViewById(R.id.product_name);
            TextView product_market_price = (TextView) productLinearLayout.findViewById(R.id.product_market_price);
            TextView product_shop_price = (TextView) productLinearLayout.findViewById(R.id.product_shop_price);
            product_name.setText(product.getProduct_name());
            product_shop_price.setText("¥" + product.getProduct_shop_price());
            product_market_price.setText("门市价：¥" + product.getProduct_market_price());
            product_container.addView(productLinearLayout);
        }
        final View product_num_container = holder.product_num_container;
        final TextView product_num_all = holder.product_num_all;

        product_num_all.setText("查看其他" + (product_num - 2) + "个服务");
        product_num_container.setVisibility(View.VISIBLE);
        holder.product_no_more.setVisibility(View.GONE);
        if (product_num <= 2) {
            product_num_container.setVisibility(View.GONE);
            holder.product_no_more.setVisibility(View.VISIBLE);
        }
        if (check.equals(position + "")) {
            product_num_all.setText("收起服务");
            holder.num_img.setImageResource(R.mipmap.up_img);
            product_num_container.setVisibility(View.VISIBLE);
            holder.product_no_more.setVisibility(View.GONE);
        }
        product_num_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.e("OnClick", "OnClick");
                    Log.e("check", mList.get(position).getString("check"));
                    if (!mList.get(position).getString("check").equals(position + "")) {
                        int cnt = product_container.getChildCount();
                        for (int i = 0; i < cnt; i++) {
                            product_container.getChildAt(i).setVisibility(View.VISIBLE);
                        }
                        product_num_all.setText("收起服务");
                        holder.num_img.setImageResource(R.mipmap.up_img);
                        mList.get(position).put("check", position + "");
                    } else {
                        int cnt = product_container.getChildCount();
                        for (int i = 2; i < cnt; i++) {
                            product_container.getChildAt(i).setVisibility(View.GONE);
                        }
                        hostRecycler.smoothScrollToPosition(position + 1);
                        product_num_all.setText("查看其他" + (product_num - 2) + "个服务");
                        holder.num_img.setImageResource(R.mipmap.down_img);
                        mList.get(position).put("check", "false");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
