package com.witmoon.xmb.activity.specialoffer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.Market;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;
import com.witmoon.xmblibrary.linearlistview.extend.LinearTag;

import java.util.List;

/**
 * Created by zhyh on 2015/7/21.
 */
public class MarketListAdapter extends LinearBaseAdapter {

    private List<Market> mMarketList;

    public MarketListAdapter(List<Market> markets) {
        this.mMarketList = markets;
    }

    @Override
    public int getCountOfIndexViewType(int mType) {
        return mMarketList.size();
    }

    @Override
    public int getCount() {
        return mMarketList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMarketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_market, parent, false);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        Market market = mMarketList.get(position);

        holder.titleText.setText(market.getName());
        holder.remainingTimeText.setText(market.getStartTime() + "");
        Netroid.displayImage(market.getImage(), holder.image);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    private class ItemHolder extends LinearTag {

        ImageView image;
        TextView discountText;
        TextView titleText;
        TextView remainingTimeText;

        public ItemHolder(View itemView) {
            image = (ImageView) itemView.findViewById(R.id.goods_image);
            discountText = (TextView) itemView.findViewById(R.id.goods_discount);
            titleText = (TextView) itemView.findViewById(R.id.goods_title);
            remainingTimeText = (TextView) itemView.findViewById(R.id.goods_remaining_time);
        }
    }
}
