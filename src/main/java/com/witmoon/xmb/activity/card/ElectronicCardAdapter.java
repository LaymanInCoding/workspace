package com.witmoon.xmb.activity.card;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.ElecCard;

import java.util.List;

/**
 * Created by ming on 2017/3/20.
 */
public class ElectronicCardAdapter extends BaseAdapter {
    private List<ElecCard> mDataList;

    public ElectronicCardAdapter(List<ElecCard> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_electronic_card, parent, false);
            holder = new ImageViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.card_textview);
            holder.mTextView.setTextColor(Color.parseColor("#555555"));
            holder.mTextView.setBackgroundResource(R.drawable.eleccard_unselected);
//            for (int i = 0; i < index.size(); i++) {
//                int ind = index.get(i);
                if (mDataList.get(position).getDelete_index() == position) {
                    holder.mTextView.setTextColor(Color.parseColor("#e8465e"));
                    holder.mTextView.setBackgroundResource(R.drawable.eleccard_selected);
                }
//            }
            convertView.setTag(holder);
        } else {
            holder = (ImageViewHolder) convertView.getTag();
        }
        holder.mTextView.setText(mDataList.get(position).getCard_money_format());
        return convertView;
    }

    public class ImageViewHolder {
        public TextView mTextView;
    }

}
