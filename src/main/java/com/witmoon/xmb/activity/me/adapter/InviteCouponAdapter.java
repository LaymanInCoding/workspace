package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.InviteCoupon;

import java.util.ArrayList;

/**
 * Created by ming on 2017/3/30.
 */
public class InviteCouponAdapter extends RecyclerView.Adapter<InviteCouponAdapter.ViewHolder> {
    private ArrayList<InviteCoupon> mData;
    private Context mContext;

    public InviteCouponAdapter(Context context, ArrayList<InviteCoupon> data) {
        this.mContext = context;
        this.mData = data;
    }


    @Override
    public InviteCouponAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_invite_coupon, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InviteCouponAdapter.ViewHolder holder, int position) {
        InviteCoupon coupon = mData.get(position);
        holder.userName.setText(coupon.getUser_name());
        holder.couponStatus.setText(coupon.getStatus());
        holder.couponType.setText(coupon.getCoupon_type());
        Netroid.displayImage(coupon.getHeader_img(), holder.headerImg);
        if (coupon.getCoupon_type().equals("已入账")) {
            holder.couponStatus.setTextColor(Color.parseColor("#aaaaaa"));
        } else {
            holder.couponStatus.setTextColor(Color.parseColor("#555555"));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView headerImg;
        private TextView userName;
        private TextView couponType;
        private TextView couponStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            headerImg = (ImageView) itemView.findViewById(R.id.header_img);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            couponType = (TextView) itemView.findViewById(R.id.coupon_type);
            couponStatus = (TextView) itemView.findViewById(R.id.coupon_status);
        }
    }
}
