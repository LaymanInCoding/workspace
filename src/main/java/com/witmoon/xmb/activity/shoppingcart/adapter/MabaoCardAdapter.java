package com.witmoon.xmb.activity.shoppingcart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.MabaoCard;

import java.util.ArrayList;

public class MabaoCardAdapter extends  RecyclerView.Adapter<MabaoCardAdapter.ViewHolder> {

    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    public MabaoCardAdapter(ArrayList<MabaoCard> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<MabaoCard> mDatas;
    private Context context;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mabao_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MabaoCard mabaoCard = mDatas.get(position);
        holder.mb_card_no.setText(mabaoCard.getCard_no());
        holder.mb_card_surplus_money.setText("抵用金额："+mabaoCard.getCard_surplus_money()+"元");
        holder.mb_card_use_end_time.setText("有效期："+mabaoCard.getCard_use_end_time());
        if(mabaoCard.getIs_checked() == 1){
            holder.mb_card_checked.setImageResource(R.mipmap.mb_card_selected);
        }else{
            holder.mb_card_checked.setImageResource(R.mipmap.mb_card_default);
        }
        if (mabaoCard.getOver_date()){
            holder.mb_over_date.setVisibility(View.VISIBLE);
        }else{
            holder.mb_over_date.setVisibility(View.GONE);
        }
        if(mOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mabaoCard.getOver_date()){
                        mOnClickListener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mb_card_no,mb_card_surplus_money,mb_card_use_end_time;
        public ImageView mb_card_checked,mb_over_date;

        public ViewHolder(View itemView) {
            super(itemView);
            mb_card_no = (TextView) itemView.findViewById(R.id.mb_card_no);
            mb_card_surplus_money = (TextView) itemView.findViewById(R.id.mb_card_surplus_money);
            mb_card_use_end_time = (TextView) itemView.findViewById(R.id.mb_card_use_end_time);
            mb_card_checked = (ImageView)itemView.findViewById(R.id.mb_card_checked);
            mb_over_date = (ImageView)itemView.findViewById(R.id.mb_over_date);
        }
    }
}