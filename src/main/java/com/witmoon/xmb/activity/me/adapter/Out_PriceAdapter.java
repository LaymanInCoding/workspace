package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.OrderType;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Order;
import com.witmoon.xmb.model.Out_;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单适配器 ----   we
 */
public class Out_PriceAdapter extends RecyclerView.Adapter<Out_PriceAdapter.OrderViewHolder> {
    private Context mContext;
    private OnItemButtonClickListener mOnItemButtonClickListener;
    private OnItemTypeClickListener mOnItemTypeClickListener;
    private OnItemAllClickListener mOnItemAllClickListener;
    private List<Out_> _data = new ArrayList<>();

    public void setOnItemButtonClickListener(
            OnItemButtonClickListener onItemButtonClickListener) {
        mOnItemButtonClickListener = onItemButtonClickListener;
    }

    public void setmOnItemAllClickListener(
            OnItemAllClickListener onItemAllClickListener) {
        mOnItemAllClickListener = onItemAllClickListener;
    }

    public void setOnItemTypeClickListener(
            OnItemTypeClickListener onItemTypeClickListener) {
        mOnItemTypeClickListener = onItemTypeClickListener;
    }

    public Out_PriceAdapter(Context context, List<Out_> _data) {
        this.mContext = context;
        this._data = _data;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_out_price, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        OrderViewHolder oHolder = holder;
        final Out_ order = _data.get(position);
        switch (Integer.valueOf(order.getRefund_status())) {
            case 1:
                oHolder.goods_od.setVisibility(View.GONE);
                oHolder.type1.setVisibility(View.GONE);
                oHolder.type2.setVisibility(View.VISIBLE);
                oHolder.type2.setText("申请售后");
                break;
            case 2:
                oHolder.goods_od.setVisibility(View.GONE);
                oHolder.type1.setVisibility(View.GONE);
                oHolder.type2.setVisibility(View.VISIBLE);
                oHolder.type2.setText("进度查询");
                break;
            case 3:
                oHolder.goods_od.setVisibility(View.GONE);
                oHolder.type1.setVisibility(View.VISIBLE);
                oHolder.type2.setVisibility(View.GONE);
                oHolder.type1.setText("重新申请");
                break;
            case 4:
                oHolder.goods_od.setVisibility(View.VISIBLE);
                oHolder.type1.setVisibility(View.GONE);
                oHolder.type2.setVisibility(View.VISIBLE);
                oHolder.type2.setText("已完成退换货");
                break;
            case 5:
                oHolder.type1.setVisibility(View.VISIBLE);
                oHolder.goods_od.setVisibility(View.GONE);
                oHolder.type2.setVisibility(View.VISIBLE);
                oHolder.type2.setText("进度查询");
                oHolder.type1.setText("填写运单号");
                break;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        oHolder.serialNoText.setText("订单号：" + order.getOrder_sn());
        oHolder.splitTime.setText(order.getAdd_time());
        oHolder.total_price.setText("¥" + order.getOrder_total_money());
        oHolder.type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClickListener != null)
                    mOnItemButtonClickListener.onItemButtonClick(order, position);
            }
        });

        oHolder.type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemTypeClickListener != null)
                    mOnItemTypeClickListener.OnItemTypeClick(order, position);
            }
        });
        oHolder.splitContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemAllClickListener != null)
                    mOnItemAllClickListener.OnItemAllClickListener(order);
            }
        });
        List<Map<String, String>> goodsList = order.getmGoodsList();
        oHolder.splitContainer.removeAllViews();
        for (int i = 0; i < goodsList.size(); i++) {
            Map<String, String> map = goodsList.get(i);
            View goodsContainerView = inflater.inflate(R.layout.item_order_goods, oHolder.container, false);
            ImageView imageView = (ImageView) goodsContainerView.findViewById(R.id.goods_image);
            Netroid.displayBabyImage(map.get("goods_img"), imageView);
            TextView title = (TextView) goodsContainerView.findViewById(R.id.goods_title);
            title.setText(map.get("goods_name"));
            TextView price = (TextView) goodsContainerView.findViewById(R.id.goods_price);
            price.setText("¥" + map.get("goods_price"));
            TextView count = (TextView) goodsContainerView.findViewById(R.id.goods_count);
            count.setText("x" + map.get("goods_number"));
            oHolder.splitContainer.addView(goodsContainerView);
        }
    }


    @Override
    public int getItemCount() {
        return _data.size();
    }

    // ViewHolder
    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView serialNoText;
        private LinearLayout container, splitContainer;
        private TextView type1, splitTime, type2, goods_od, total_price;

        public OrderViewHolder(View v) {
            super(v);
            serialNoText = (TextView) v.findViewById(R.id.serial_no);
            splitContainer = (LinearLayout) v.findViewById(R.id.split_order_container);
            splitTime = (TextView) v.findViewById(R.id.split_time);
            type1 = (TextView) v.findViewById(R.id.type1);
            total_price = (TextView) v.findViewById(R.id.total_price);
            type2 = (TextView) v.findViewById(R.id.type2);
            goods_od = (TextView) v.findViewById(R.id.goods_od);
        }
    }

    //申请回调接口 --- 申请售后
    public interface OnItemButtonClickListener {
        void onItemButtonClick(Out_ order, int position);
    }

    //申请回调接口 --- 重新申请售后
    public interface OnItemTypeClickListener {
        void OnItemTypeClick(Out_ order, int position);
    }

    public interface OnItemAllClickListener {
        void OnItemAllClickListener(Out_ order);
    }
}
