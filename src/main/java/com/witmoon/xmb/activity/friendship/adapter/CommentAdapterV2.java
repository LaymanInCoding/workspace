package com.witmoon.xmb.activity.friendship.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * 帖子评论适配器
 * Created by zhyh on 2015/8/4.
 */
public class CommentAdapterV2 extends LinearBaseAdapter {

    private List<Map<String, String>> mDataList;

    public CommentAdapterV2(List<Map<String, String>> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getCountOfIndexViewType(int mType) {
        switch (mType) {
            case 0:
                return mDataList.size();
            default:
                break;
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
        CommentHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_article_comment, parent, false);
            holder = new CommentHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CommentHolder) convertView.getTag();
        }

        Map<String, String> item = mDataList.get(position);
        holder.mAuthorText.setText(item.get("author") + "：");
        holder.mContentText.setText(item.get("content"));

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

    class CommentHolder {

        TextView mAuthorText;
        TextView mContentText;

        public CommentHolder(View itemView) {
            mAuthorText = (TextView) itemView.findViewById(R.id.author);
            mContentText = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
