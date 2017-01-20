package com.witmoon.xmb.activity.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.activity.goods.SearchResultListActivity;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.util.ArrayList;

/**
 * Created by de on 2015/10/29.
 */
public class Search_adapter extends RecyclerView.Adapter<Search_adapter.ViewHolder> {
    private ArrayList<String> mList;
    private Context mContext;
    private OnItemDeleteListener mDeleteListener;
    private boolean is_service;
    private OnItemClickListener mClickListener;


    public Search_adapter(Context mContext, ArrayList<String> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    public Search_adapter(Context mContext, ArrayList<String> mList, boolean is_service) {
        this.mList = mList;
        this.mContext = mContext;
        this.is_service = is_service;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.mDeleteListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.itme_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mList.get(position));
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteListener.onItemDelete(position);
            }
        });
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_service == true) {
                    mClickListener.onItemClick(position);
                } else {
                    SearchResultListActivity.start(mContext, mList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView deleteImg;
        View containerView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.search_tv);
            containerView = itemView.findViewById(R.id.container);
            deleteImg = (ImageView) itemView.findViewById(R.id.item_delete);
        }
    }
}
