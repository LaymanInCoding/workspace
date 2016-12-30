package com.witmoon.xmb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de on 2016/11/23.
 */
public class InvoiceContentRVAdapter extends RecyclerView.Adapter<InvoiceContentRVAdapter.ViewHolder> {

    private ArrayList<String> mData;
    private Context mContext;
    private int bgFocus, bgBlur;
    private int selectedIndex = 4;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setSelectedIndex(int index){
        this.selectedIndex = index;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public InvoiceContentRVAdapter(Context context, ArrayList<String> data,int selectedIndex) {
        this.mContext = context;
        this.mData = data;
        this.selectedIndex = selectedIndex;
        bgFocus = R.mipmap.content_checked;
        bgBlur = R.mipmap.content_unchecked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_invoice_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String content = mData.get(position);
        if (position == selectedIndex) {
            holder.mImageView.setImageResource(bgFocus);
        } else {
            holder.mImageView.setImageResource(bgBlur);
        }
        holder.mTextView.setText(content);
        holder.mLayout.setOnClickListener(v -> {
            selectedIndex = position;
            notifyDataSetChanged();
            mListener.onItemClick(position);
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTextView;
        private RelativeLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            mImageView = (ImageView) itemView.findViewById(R.id.invoice_content_img);
            mTextView = (TextView) itemView.findViewById(R.id.invoice_content_txt);
        }
    }
}
