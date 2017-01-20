package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.ProductDetailActivity;
import com.witmoon.xmb.activity.service.ServiceShopDetailActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.service.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by de on 2017/1/4.
 */
public class SubAdapter extends RecyclerView.Adapter<SubAdapter.ViewHolder> {
    private ArrayList<JSONObject> mList;
    private Context mContext;

    public SubAdapter(ArrayList<JSONObject> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sub_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject jsonObject = mList.get(position);
        try {
            Netroid.displayAdImage(jsonObject.getString("product_img"), holder.product_img);
            holder.product_name.setText(jsonObject.getString("product_name"));
            holder.product_shop_price.setText("¥" + jsonObject.getString("product_shop_price"));
            holder.product_market_price.setText("门市价：¥" + jsonObject.getString("product_market_price"));
            String product_id = jsonObject.getString("product_id");
            holder.view.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra("product_id", Integer.parseInt(product_id));
                mContext.startActivity(intent);
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
        RoundedImageView product_img;
        TextView product_market_price;
        TextView product_name;
        TextView product_shop_price;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            product_img = (RoundedImageView) itemView.findViewById(R.id.product_img);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_market_price = (TextView) itemView.findViewById(R.id.product_market_price);
            product_shop_price = (TextView) itemView.findViewById(R.id.product_shop_price);
            view = itemView;
        }
    }

}
