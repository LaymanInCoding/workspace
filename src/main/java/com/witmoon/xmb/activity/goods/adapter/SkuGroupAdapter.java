package com.witmoon.xmb.activity.goods.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.ui.widget.TimeView;
import com.witmoon.xmb.util.TimeUtill;

import java.sql.Time;
import java.util.List;
import java.util.Map;


/**
 * Created by de on 2016/1/13
 */
public class SkuGroupAdapter extends BaseAdapter {
    private Context mContext;
    private  List<Map<String, String>> mapList;
    private OnItemButtonClickListener mOnItemButtonClickListener;
    public void setOnItemButtonClickListener(
            OnItemButtonClickListener onItemButtonClickListener) {
        mOnItemButtonClickListener = onItemButtonClickListener;
    }
    public SkuGroupAdapter(Context mContext, List<Map<String, String>> mapList) {
        this.mContext = mContext;
        this.mapList = mapList;
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == view) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_skugroup, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.price = (TextView) view.findViewById(R.id.price);
            viewHolder.market_price = (TextView) view.findViewById(R.id.market_price);
            viewHolder.image = (SimpleDraweeView) view.findViewById(R.id.image);
            viewHolder.count = (TextView) view.findViewById(R.id.count);
            viewHolder.end_time = (TimeView) view.findViewById(R.id.end_time);
            viewHolder.goods_brand = (TextView) view.findViewById(R.id.goods_brand);
            viewHolder.context = (TextView) view.findViewById(R.id.context);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Map<String,String> map = mapList.get(position);
        viewHolder.title.setText(map.get("name"));
        viewHolder.image.setImageURI(Uri.parse(map.get("goods_img")));
        viewHolder.price.setText("￥" + map.get("shop_price"));
        viewHolder.market_price.setText(map.get("market_price"));
        viewHolder.market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.count.setText(map.get("salesnum") + "件已付款");
//        if(map.get("short_name").equals("")){
//            viewHolder.context.setVisibility(View.GONE);
//        }else{
//            viewHolder.context.setVisibility(View.VISIBLE);
//        }
        viewHolder.context.setText(map.get("short_name"));
        if (TimeUtill.list.size()>0)
        if (TimeUtill.get(position, "time").equals("售罄")) {
            viewHolder.end_time.setText(TimeUtill.get(position, "time"));
        }
        if (TimeUtill.list.size()>0)
            viewHolder.end_time.setPosition(position, 1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClickListener != null)
                    mOnItemButtonClickListener.onItemButtonClick(map.get("id"));
            }
        });
        return view;
    }

    class ViewHolder {
        TextView title, price, market_price,count,goods_brand,context;
        TimeView end_time;
        SimpleDraweeView image;
    }

    public interface OnItemButtonClickListener {
        void onItemButtonClick(String id);
    }
}
