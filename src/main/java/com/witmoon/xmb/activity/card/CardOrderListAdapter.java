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
 * Created by ming on 2017/3/22.
 */
public class CardOrderListAdapter extends LinearBaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Map<String, Object>> mDataList;

    public CardOrderListAdapter(Context context, List<Map<String, Object>> dataList) {
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
            convertView = mLayoutInflater.inflate(R.layout.item_card_list, parent, false);
            holder = new ItemHolder();
            holder.g_container = (LinearLayout) convertView.findViewById(R.id.g_container);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        Map<String,Object> data = mDataList.get(position);
        List<Map<String,String>> list = (List<Map<String, String>>) data.get("cards");
        for (int i = 0; i < list.size(); i++) {
            RelativeLayout containerView = (RelativeLayout) mLayoutInflater.inflate(R.layout.item_card_confirm_goods, parent, false);
            ImageLoader.getInstance().displayImage(list.get(i).get("card_img"), (ImageView) containerView.findViewById(R.id.goods_img), AppContext.options_memory);
            ((TextView) containerView.findViewById(R.id.card_name)).setText(list.get(i).get("card_name"));
            ((TextView) containerView.findViewById(R.id.card_money)).setText(list.get(i).get("card_money"));
            ((TextView) containerView.findViewById(R.id.card_num)).setText("数量：" + list.get(i).get("card_number"));
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
    }
}