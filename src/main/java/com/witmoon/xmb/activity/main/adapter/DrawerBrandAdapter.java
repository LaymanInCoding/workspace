package com.witmoon.xmb.activity.main.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.witmoon.xmb.R;
import com.witmoon.xmblibrary.recyclerview.RecyclerArrayAdapter;

import java.util.Map;

/**
 * Drawer在线品牌适配器
 * Created by zhyh on 2015/5/16.
 */
public class DrawerBrandAdapter extends RecyclerArrayAdapter<Map<String, String>,
        RecyclerView.ViewHolder> implements
        StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(24, 12,24, 12);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable
                .bg_item_pressed));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        return new RecyclerView.ViewHolder(textView) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(getItem(position).get("name"));
    }

    @Override
    public long getHeaderId(int i) {
        try {
            return getItem(i).get("alpha").charAt(0);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        LinearLayout container = new LinearLayout(parent.getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setBackgroundColor(parent.getResources().getColor(R.color.main_drawer_group_bg));

        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.WHITE);
        textView.setPadding(24, 10, 24, 10);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        textView.setBackgroundColor(parent.getContext().getResources().getColor(R.color
                .main_drawer_highlight));

        container.addView(textView);

        return new HeaderViewHolder(container);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int i) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.mTextView.setText(String.valueOf(getItem(i).get("alpha")));
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) ((LinearLayout) itemView).getChildAt(0);
        }
    }
}
