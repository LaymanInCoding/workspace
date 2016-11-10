package com.witmoon.xmb.activity.friendship.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.friendship.UserLevel;
import com.witmoon.xmb.api.Netroid;

import java.util.List;
import java.util.Map;

/**
 * 帖子评论适配器
 * Created by zhyh on 2015/8/4.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private List<Map<String, String>> mDataList;

    public CommentAdapter(List<Map<String, String>> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_article_comment, parent, false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Map<String, String> item = mDataList.get(position);
        holder.mAuthorText.setText(item.get("author"));
        holder.mContentText.setText(item.get("content"));
        holder.mTimeText.setText(item.get("time"));
        holder.mLevelImage.setImageResource(UserLevel.getLevel(Integer
                .parseInt(item.get("level"))).getResId());
        Netroid.displayImage(item.get("avatar"), holder.mAvatarImage);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class CommentHolder extends RecyclerView.ViewHolder {

        ImageView mAvatarImage;
        TextView mAuthorText;
        ImageView mLevelImage;
        TextView mTimeText;
        TextView mContentText;

        public CommentHolder(View itemView) {
            super(itemView);

            mAvatarImage = (ImageView) itemView.findViewById(R.id.avatar_img);
            mAuthorText = (TextView) itemView.findViewById(R.id.author);
            mLevelImage = (ImageView) itemView.findViewById(R.id.level);
            mTimeText = (TextView) itemView.findViewById(R.id.time);
            mContentText = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
