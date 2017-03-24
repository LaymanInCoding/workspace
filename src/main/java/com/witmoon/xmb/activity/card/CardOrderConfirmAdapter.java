package com.witmoon.xmb.activity.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/3/21.
 */
public class CardOrderConfirmAdapter extends LinearBaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Map<String, Object>> mDataList;

    public CardOrderConfirmAdapter(Context context, List<Map<String, Object>> dataList) {
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
            convertView = mLayoutInflater.inflate(R.layout.item_card_order_confirm, parent, false);
            holder = new ItemHolder();
            holder.g_container = (LinearLayout) convertView.findViewById(R.id.g_container);
            holder.supplierCount = (TextView) convertView.findViewById(R.id.card_amount);
            holder.supplier_s_total = (TextView) convertView.findViewById(R.id.supplier_s_total);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        Map<String, Object> dataMap = mDataList.get(position);
        holder.supplierCount.setText("共 " + dataMap.get("card_numbers").toString() + " 件商品");
        holder.supplier_s_total.setText(dataMap.get("card_amount").toString());
        holder.g_container.removeAllViews();
        List<Map<String, String>> goodsList = (List<Map<String, String>>) dataMap.get("cards");
        for (int i = 0; i < goodsList.size(); i++) {
            RelativeLayout containerView = (RelativeLayout) mLayoutInflater.inflate(R.layout.item_card_confirm_goods, parent, false);
            ImageLoader.getInstance().displayImage(goodsList.get(i).get("card_img"), (ImageView) containerView.findViewById(R.id.goods_img), AppContext.options_memory);
            ((TextView) containerView.findViewById(R.id.card_name)).setText(goodsList.get(i).get("card_name"));
            ((TextView) containerView.findViewById(R.id.card_money)).setText(goodsList.get(i).get("card_money"));
            ((TextView) containerView.findViewById(R.id.card_num)).setText("数量：" + goodsList.get(i).get("card_cnt"));
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

    class ItemHolder {
        LinearLayout g_container;
        TextView supplierCount;
        TextView supplier_s_total;
    }
}
