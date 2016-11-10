package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.service.Ticket;

import java.util.ArrayList;

/**
 * Created by ZCM on 2016/1/25
 */
public class PaySuccessTicketAdapter extends  RecyclerView.Adapter<PaySuccessTicketAdapter.ViewHolder>{

    private ArrayList<Ticket> mList;
    private Context mContext;

    public PaySuccessTicketAdapter(ArrayList<Ticket> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_service_ticket_succ, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ticket ticket = mList.get(position);
        holder.ticket_code.setText(ticket.getTicket_code());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView ticket_code;

        public ViewHolder(View itemView) {
            super(itemView);
            ticket_code = (TextView) itemView.findViewById(R.id.ticket_code);
        }
    }
}
