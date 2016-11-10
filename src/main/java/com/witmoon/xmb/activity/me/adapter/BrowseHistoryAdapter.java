package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.BrowseHistory;

/**
 * 浏览记录适配器
 * Created by zhyh on 2015/5/3.
 */
public class BrowseHistoryAdapter extends BaseRecyclerAdapter {

    private LayoutInflater mLayoutInflater;

    public BrowseHistoryAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        return mLayoutInflater.inflate(R.layout.item_browse_history, parent, false);
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new BrowseHistoryViewHolder(viewType, view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, int position) {
        BrowseHistoryViewHolder bhHolder = (BrowseHistoryViewHolder) holder;
        BrowseHistory browseHistory = (BrowseHistory) _data.get(position);
        bhHolder.mBrowseTimeTextView.setText(browseHistory.getBrowseTime());
        bhHolder.mGoodsNameTextView.setText(browseHistory.getGoodsName());
        Netroid.displayImage(browseHistory.getPhoto(), bhHolder.mGoodsPhotoImageView);
    }

    public static class BrowseHistoryViewHolder extends BaseRecyclerAdapter.ViewHolder {

        public ImageView mGoodsPhotoImageView;
        public TextView mGoodsNameTextView;
        public TextView mBrowseTimeTextView;

        public BrowseHistoryViewHolder(int viewType, View itemView) {
            super(viewType, itemView);

            mGoodsPhotoImageView = (ImageView) itemView.findViewById(R.id
                    .item_browse_history_photo);
            mGoodsNameTextView = (TextView) itemView.findViewById(R.id.item_browse_history_name);
            mBrowseTimeTextView = (TextView) itemView.findViewById(R.id.item_browse_history_time);
        }
    }
}
