package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.service.ReturnTicket;
import com.witmoon.xmb.model.service.Ticket;

import java.util.ArrayList;

/**
 * Created by ZCM on 2016/1/25
 */
public class ReturnTicketAdapter extends  RecyclerView.Adapter<ReturnTicketAdapter.ViewHolder>{

    private ArrayList<ReturnTicket> mList;
    private Context mContext;

    private OnSelectBtnClickListener mOnSelectBtnClickListener;

    public interface OnSelectBtnClickListener {
        void changeStatus(int position,int ticket_sel_status);
    }

    public void setOnSelectBtnClickListener(
            OnSelectBtnClickListener mOnSelectBtnClickListener) {
        this.mOnSelectBtnClickListener = mOnSelectBtnClickListener;
    }

    public ReturnTicketAdapter(ArrayList<ReturnTicket> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_service_return_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        final ReturnTicket ticket = mList.get(position);
        holder.ticket_code.setText(ticket.getTicket_code());
        if (ticket.getTicket_status() == 0){
            holder.select_btn.setImageResource(R.mipmap.flight_butn_check_unselect);
        }else{
            holder.select_btn.setImageResource(R.mipmap.flight_butn_check_select);
        }
        if (mOnSelectBtnClickListener != null) {
            holder.select_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnSelectBtnClickListener.changeStatus(position,ticket.getTicket_status());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView ticket_code;
        ImageView select_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            ticket_code = (TextView) itemView.findViewById(R.id.ticket_code);
            select_btn = (ImageView) itemView.findViewById(R.id.select_btn);
        }
    }
}
