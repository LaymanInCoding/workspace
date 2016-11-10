package com.witmoon.xmb.activity.friendship.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseRecyclerAdapter;

import java.util.Map;

/**
 * 麻包圈子之活动适配器
 * Created by zhyh on 2015/6/24.
 */
public class ActivityAdapter extends BaseRecyclerAdapter {

    private OnParticipateInClickListener mOnParticipateInClickListener;

    public void setOnParticipateInClickListener(
            OnParticipateInClickListener onParticipateInClickListener) {
        mOnParticipateInClickListener = onParticipateInClickListener;
    }

    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent,
                false);
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new ActivityHolder(viewType, view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, int position) {
        ActivityHolder activityHolder = (ActivityHolder) holder;
        final Map<String, String> dataMap = (Map<String, String>) _data.get(position);
        Netroid.displayImage(dataMap.get("avatar"), activityHolder.mAvatarImage);
        activityHolder.mNameText.setText(dataMap.get("name"));
        activityHolder.mTitleText.setText(dataMap.get("title"));
        activityHolder.mContentTe.setText(dataMap.get("content"));
        Netroid.displayImage(dataMap.get("image"), activityHolder.mImageView);

        if (null != mOnParticipateInClickListener) {
            activityHolder.mButtonText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnParticipateInClickListener.onParticipateInClick(dataMap.get("link"));
                }
            });
        }
    }

    public interface OnParticipateInClickListener {
        void onParticipateInClick(String link);
    }

    static class ActivityHolder extends BaseRecyclerAdapter.ViewHolder {
        ImageView mAvatarImage;
        TextView mNameText;
        ImageView mImageView;
        TextView mTitleText;
        TextView mContentTe;
        TextView mButtonText;

        public ActivityHolder(int viewType, View itemView) {
            super(viewType, itemView);
            mAvatarImage = (ImageView) itemView.findViewById(R.id.avatar_img);
            mNameText = (TextView) itemView.findViewById(R.id.name);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mTitleText = (TextView) itemView.findViewById(R.id.title);
            mContentTe = (TextView) itemView.findViewById(R.id.content);
            mButtonText = (TextView) itemView.findViewById(R.id.submit_button);
        }
    }
}
