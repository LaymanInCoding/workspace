package com.witmoon.xmb.activity.specialoffer.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.model.Market;
import com.witmoon.xmb.util.ImageLoaders;
import com.xiaoneng.imageloader.ImageLoad;

import java.util.List;

/**
 * Created by zhyh on 2015/7/22.
 */
public class MLAdapter extends BaseAdapter {

    private List<Market> mMarketList;

    public MLAdapter(List<Market> dataList) {
        this.mMarketList = dataList;
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
        MarketHolder marketHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_market, parent, false);
            marketHolder = new MarketHolder();
            marketHolder.image = (ImageView) convertView.findViewById(R.id.goods_image);
            marketHolder.titleText = (TextView) convertView.findViewById(R.id.goods_title);
            marketHolder.favorableText = (TextView) convertView.findViewById(R.id.goods_discount);
            marketHolder.remainingTimeText = (TextView) convertView.findViewById(R.id
                    .goods_remaining_time);
            convertView.setTag(marketHolder);
        } else {
            marketHolder = (MarketHolder) convertView.getTag();
        }
        Market market = (Market) getItem(position);
        marketHolder.titleText.setText(market.getName());
        marketHolder.remainingTimeText.setText(market.getTimeDesc());
        marketHolder.favorableText.setText(market.getFavourable());
        try {
            ImageLoaders.setsendimg(market.getImage(), marketHolder.image);
//            Netroid.displayImage(market.getImage(), marketHolder.image);
//            marketHolder.image.setImageURI(Uri.parse(market.getImage()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class MarketHolder {
        ImageView image;
        TextView favorableText;
        TextView titleText;
        TextView remainingTimeText;
    }
}
