package com.witmoon.xmb.activity.shoppingcart.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * 确认订单界面中商品列表适配器
 * Created by zhyh on 2015/5/27.
 */
public class OrderConfirmAdapterV2 extends LinearBaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Map<String, Object>> mDataList;

    public OrderConfirmAdapterV2(Context context, List<Map<String, Object>> dataList) {
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
            convertView = mLayoutInflater.inflate(R.layout.item_order_confirm, parent, false);
            holder = new ItemHolder();
            holder.supplierText = (TextView)convertView.findViewById(R.id.supplier_name);
            holder.g_container = (LinearLayout)convertView.findViewById(R.id.g_container);
            holder.supplierTax = (TextView)convertView.findViewById(R.id.supplier_tax);
            holder.supplierShipping = (TextView)convertView.findViewById(R.id.supplier_shipping);
            holder.supplierCount = (TextView)convertView.findViewById(R.id.supplier_count);
            holder.supplier_s_total = (TextView) convertView.findViewById(R.id.supplier_s_total);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        Map<String, Object> dataMap = mDataList.get(position);
        holder.supplierText.setText(dataMap.get("supplier_name").toString());
        holder.supplierTax.setText(dataMap.get("cross_border_money").toString());
        holder.supplierShipping.setText( dataMap.get("shipping_fee").toString());
        holder.supplierCount.setText("共"+dataMap.get("number").toString()+"件商品，");
        holder.supplier_s_total.setText(dataMap.get("total_money").toString());
        holder.g_container.removeAllViews();
        List<Map<String,String>> goodsList = ( List<Map<String,String>>)dataMap.get("goods_list");
        for(int i = 0; i < goodsList.size(); i++){
            LinearLayout containerView = (LinearLayout)mLayoutInflater.inflate(R.layout.item_order_confirm_goods, parent, false);
            ImageLoader.getInstance().displayImage(goodsList.get(i).get("url"), ((SimpleDraweeView) containerView.findViewById(R.id.goods_image)), AppContext.options_memory);
            ((TextView) containerView.findViewById(R.id.goods_title)).setText(goodsList.get(i).get("title"));
            ((TextView) containerView.findViewById(R.id.goods_price)).setText(goodsList.get(i).get("price_formatted"));
            ((TextView) containerView.findViewById(R.id.goods_count)).setText("x" + goodsList.get(i).get("count"));
            ((TextView) containerView.findViewById(R.id.specificationText)).setText(goodsList.get(i).get("goods_attr"));
            holder.g_container.addView(containerView);
        }

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

    class ItemHolder{
        TextView supplierText;
        LinearLayout g_container;
        TextView supplierShipping;
        TextView supplierTax;
        TextView supplierCount;
        TextView supplier_s_total;
    }
}
