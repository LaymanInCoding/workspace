package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.ServiceShopDetailActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.service.Shop;
import com.witmoon.xmb.model.service.Ticket;

import java.util.ArrayList;

/**
 * Created by ZCM on 2016/1/25
 */
public class TicketAdapter extends  RecyclerView.Adapter<TicketAdapter.ViewHolder>{

    private ArrayList<Ticket> mList;
    private Context mContext;

    public TicketAdapter(ArrayList<Ticket> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_service_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ticket ticket = mList.get(position);
        holder.ticket_code.setText(ticket.getTicket_code());
        holder.ticket_status.setText(ticket.getTicket_status());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView ticket_code;
        TextView ticket_status;

        public ViewHolder(View itemView) {
            super(itemView);
            ticket_code = (TextView) itemView.findViewById(R.id.ticket_code);
            ticket_status = (TextView) itemView.findViewById(R.id.ticket_status);
        }
    }
}
