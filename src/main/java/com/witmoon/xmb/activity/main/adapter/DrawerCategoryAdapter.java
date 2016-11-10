package com.witmoon.xmb.activity.main.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.witmoon.xmb.R;
import com.witmoon.xmb.model.Category;
import com.witmoon.xmblibrary.recyclerview.RecyclerArrayAdapter;

/**
 * 商品分类适配器
 * Created by zhyh on 2015-07-11.
 */
public class DrawerCategoryAdapter extends RecyclerArrayAdapter<Category, RecyclerView
        .ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(24, 12, 24, 12);
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
        textView.setText(getItem(position).getName());
    }

    @Override
    public long getHeaderId(int i) {
        try {
            return Long.parseLong(getItem(i).getParent().getId());
        } catch (Exception e) {
            return -1L;
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
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setBackgroundColor(parent.getContext().getResources().getColor(R.color
                .main_drawer_highlight));

        container.addView(textView);

        return new HeaderViewHolder(container);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        headerViewHolder.mTextView.setText(getItem(i).getParent().getName());
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) ((LinearLayout) itemView).getChildAt(0);
        }
    }
}
