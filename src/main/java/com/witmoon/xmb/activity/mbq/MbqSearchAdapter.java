package com.witmoon.xmb.activity.mbq;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;

import java.util.ArrayList;

/**
 * Created by ming on 2017/5/8.
 */
public class MbqSearchAdapter extends RecyclerView.Adapter<MbqSearchAdapter.ViewHolder> {
    private ArrayList<String> mList;
    private Context mContext;
    private OnItemDeleteListener mDeleteListener;
    private boolean is_service;
    private OnItemClickListener mClickListener;


    public MbqSearchAdapter(Context mContext, ArrayList<String> mList) {
        this.mList = mList;
        this.mContext = mContext;
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
                if (mClickListener != null) {
                    mClickListener.onItemClick(position);
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
