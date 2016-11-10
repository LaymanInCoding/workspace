package com.witmoon.xmb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.Voice;

import java.util.ArrayList;

public class MajorVoiceAdapter extends  RecyclerView.Adapter<MajorVoiceAdapter.ViewHolder> {
    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onVoiceClick(int position);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_major_voice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final Voice voice = mDatas.get(position);
        holder.abstractTextView.setText(voice.abstract_text);
        holder.authorTextView.setText("作者:"+voice.author);
        holder.clickCntTextView.setText(voice.click_cnt+"听过");
        Netroid.displayBabyImage(voice.head_img,holder.headImageView);
        if(mOnClickListener != null){
            holder.itemView.setOnClickListener((View v) -> mOnClickListener.onItemClick(position));
            holder.voiceImageView.setOnClickListener((View v) -> mOnClickListener.onVoiceClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView abstractTextView,authorTextView,clickCntTextView;
        public ImageView headImageView,voiceImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            abstractTextView = (TextView) itemView.findViewById(R.id.abstract_text);
            authorTextView = (TextView) itemView.findViewById(R.id.author_text);
            clickCntTextView = (TextView) itemView.findViewById(R.id.click_cnt);
            headImageView = (ImageView) itemView.findViewById(R.id.head_img);
            voiceImageView = (ImageView) itemView.findViewById(R.id.voice_img);
        }
    }

    public MajorVoiceAdapter(ArrayList<Voice> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<Voice> mDatas;
    private Context context;
}
