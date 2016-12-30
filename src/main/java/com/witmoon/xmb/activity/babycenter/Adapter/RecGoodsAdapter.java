package com.witmoon.xmb.activity.babycenter.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.api.Netroid;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by de on 2016/12/15.
 */
public class RecGoodsAdapter extends RecyclerView.Adapter<RecGoodsAdapter.RGViewHolder> {

    private Context mContext;
    private ArrayList<JSONObject> mData;

    public RecGoodsAdapter(Context context, ArrayList<JSONObject> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RGViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_babycenter_recgoods, parent, false);
        return new RGViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RGViewHolder holder, int position) {
        JSONObject data = mData.get(position);
        try {
            holder.container.setOnClickListener(v -> {
                try {
                    CommodityDetailActivity.start(mContext, data.getString("goods_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            Netroid.displayImage(data.getString("goods_thumb"), holder.goodsImage);
            holder.goodsName.setText(data.getString("goods_name"));
            holder.shopPrice.setText("¥" + data.getString("goods_price"));
            holder.marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.marketPrice.setText("¥" + data.getString("market_price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RGViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImage;
        TextView goodsName;
        TextView shopPrice;
        TextView marketPrice;
        View container;

        public RGViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.goods_container);
            goodsImage = (ImageView) itemView.findViewById(R.id.goods_image);
            goodsName = (TextView) itemView.findViewById(R.id.goods_name);
            shopPrice = (TextView) itemView.findViewById(R.id.shop_price);
            marketPrice = (TextView) itemView.findViewById(R.id.market_price);
        }
    }

}
