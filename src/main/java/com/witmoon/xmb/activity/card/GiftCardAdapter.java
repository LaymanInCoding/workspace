package com.witmoon.xmb.activity.card;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ming on 2017/3/21.
 */
public class GiftCardAdapter extends RecyclerView.Adapter<GiftCardAdapter.ViewHolder> {
    private ArrayList<JSONObject> mDataList;
    private Context mContext;
    private OnItemClickListener mListener;

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public GiftCardAdapter(Context context, ArrayList<JSONObject> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public GiftCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gift_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GiftCardAdapter.ViewHolder holder, int position) {
        JSONObject dataObj = mDataList.get(position);
        try {
            holder.card_price.setText(dataObj.getString("goods_price"));
            holder.card_desc.setText(dataObj.getString("goods_name"));
            Netroid.displayAdImage(dataObj.getString("goods_thumb"), holder.card_pic);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView card_price;
        private TextView card_desc;
        private ImageView card_pic;

        public ViewHolder(View itemView) {
            super(itemView);
            card_desc = (TextView) itemView.findViewById(R.id.card_desc);
            card_price = (TextView) itemView.findViewById(R.id.card_price);
            card_pic = (ImageView) itemView.findViewById(R.id.card_pic);
        }
    }
}
