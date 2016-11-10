package com.witmoon.xmb.activity.shoppingcart.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * 确认订单界面中商品列表适配器
 * Created by zhyh on 2015/5/27.
 */
public class OrderConfirmAdapter extends LinearBaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Map<String, String>> mDataList;
    public OrderConfirmAdapter(Context context, List<Map<String, String>> dataList) {
        this.mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCountOfIndexViewType(int mType) {
        if (mType == 0) {
            return mDataList.size();
        }
        return 0;
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
            convertView = mLayoutInflater.inflate(R.layout.item_order_confirm_goods, parent, false);
            holder = new ItemHolder();
            holder.mImage = (SimpleDraweeView) convertView.findViewById(R.id.goods_image);
            holder.titleText = (TextView) convertView.findViewById(R.id.goods_title);
            holder.countText = (TextView) convertView.findViewById(R.id.goods_count);
            holder.priceText = (TextView) convertView.findViewById(R.id.goods_price);
            holder.specificationText = (TextView) convertView.findViewById(R.id.specificationText);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        Map<String, String> dataMap = mDataList.get(position);
        holder.mImage.setImageURI(Uri.parse(dataMap.get("url")));
        holder.titleText.setText(dataMap.get("title"));
        holder.priceText.setText(dataMap.get("price_formatted"));
        holder.countText.setText("x" + dataMap.get("count"));
        holder.specificationText.setText(dataMap.get("goods_attr"));
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

    class ItemHolder {
        SimpleDraweeView mImage;
        TextView titleText;
        TextView priceText;
        TextView countText;
        TextView specificationText;
    }
}
