package com.witmoon.xmb.activity.specialoffer.adapter;

import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.Market;

import java.util.List;

/**
 * 即将推出适配器
 * Created by zhyh on 2015/7/31.
 */
public class ComingSoonAdapter extends BaseAdapter {

    private List<Market> mMarketList;

    public ComingSoonAdapter(List<Market> dataList) {
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
                    .item_market_masked, parent, false);
            marketHolder = new MarketHolder();
            marketHolder.image = (ImageView) convertView.findViewById(R.id.goods_image);
            marketHolder.titleText = (TextView) convertView.findViewById(R.id.goods_title);
            marketHolder.favorableText = (TextView) convertView.findViewById(R.id.goods_favorable);
            marketHolder.startTimeText = (TextView) convertView.findViewById(R.id.start_time);
            convertView.setTag(marketHolder);
        } else {
            marketHolder = (MarketHolder) convertView.getTag();
        }

        Market market = (Market) getItem(position);
        marketHolder.titleText.setText(market.getName());
        marketHolder.startTimeText.setText(String.format("%s开售", DateFormat.format("M月d日 HH:mm",
                market.getStartTime() * 1000)));
        marketHolder.favorableText.setText(market.getFavourable());
        try {
            Netroid.displayImage(market.getImage(),marketHolder.image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class MarketHolder {
        ImageView image;
        TextView favorableText;
        TextView titleText;
        TextView startTimeText;
    }
}
