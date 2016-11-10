package com.witmoon.xmb.activity.specialoffer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;
import com.witmoon.xmblibrary.linearlistview.extend.LinearTag;

import java.util.List;
import java.util.Map;

public class LinearListAdapter extends LinearBaseAdapter {

    private List<Map<String, String>> mDataList;
    private LayoutInflater mLayoutInflater;

    public LinearListAdapter(Context mContext, List<Map<String, String>> dataList) {
        mLayoutInflater = LayoutInflater.from(mContext);
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_market, parent, false);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        Map<String, String> dataMap = mDataList.get(position);

        holder.goodsTitleText.setText(dataMap.get("title"));
        holder.goodsDiscountText.setText(dataMap.get("discount"));
        holder.goodsRemainingTimeText.setText(dataMap.get("time"));
        Netroid.displayImage(dataMap.get("url"), holder.goodsAdvertisementImage);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return mDataList.size();
    }

    @Override
    public int getCountOfIndexViewType(int mType) {
        switch (mType) {
            case 0:
                return mDataList.size();
            default:
                break;
        }
        return 0;
    }

    private class ItemHolder extends LinearTag {

        ImageView goodsAdvertisementImage;
        TextView goodsDiscountText;
        TextView goodsTitleText;
        TextView goodsRemainingTimeText;

        public ItemHolder(View itemView) {
            goodsAdvertisementImage = (ImageView) itemView.findViewById(R.id
                    .goods_image);
            goodsDiscountText = (TextView) itemView.findViewById(R.id.goods_discount);
            goodsTitleText = (TextView) itemView.findViewById(R.id.goods_title);
            goodsRemainingTimeText = (TextView) itemView.findViewById(R.id.goods_remaining_time);
        }
    }
}
