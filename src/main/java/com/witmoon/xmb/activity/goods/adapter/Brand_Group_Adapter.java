package com.witmoon.xmb.activity.goods.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.ui.widget.TimeView;
import com.witmoon.xmb.util.TimeUtill;

import java.util.List;
import java.util.Map;

/**
 * Created by de on 2016/1/18
 */
public class Brand_Group_Adapter extends BaseAdapter {
    private List<Map<String,String>> mMarketList;

    public Brand_Group_Adapter(List<Map<String,String>> dataList) {
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
                    .item_brand_group, parent, false);
            marketHolder = new MarketHolder();
            marketHolder.image = (ImageView) convertView.findViewById(R.id.goods_image);
            marketHolder.end_time = (TimeView) convertView.findViewById(R.id.textLeftTime);
            marketHolder.titleText = (TextView) convertView.findViewById(R.id.goods_title);
            convertView.setTag(marketHolder);
        } else {
            marketHolder = (MarketHolder) convertView.getTag();
        }

        Map<String,String> map = mMarketList.get(position);
        marketHolder.titleText.setText(map.get("title"));
//        map.get("end_time");
        try {
            Netroid.displayImage(map.get("ad_code"),marketHolder.image);
            if (TimeUtill.mlist.size()>0)
                if (TimeUtill.mGet(position, "time").equals("售罄")) {
                    marketHolder.end_time.setText(TimeUtill.mGet(position, "time"));
                }
            if (TimeUtill.mlist.size()>0)
                marketHolder.end_time.setPosition(position, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class MarketHolder {
        ImageView image;
        TextView titleText;
        TimeView end_time;
    }
}
