package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.TicketsDetailActivity;
import com.witmoon.xmb.activity.service.ServiceOrderDetailActivity;
import com.witmoon.xmb.activity.service.ServicePostCommentActivity;
import com.witmoon.xmb.activity.service.ServiceShopDetailActivity;
import com.witmoon.xmb.activity.service.SubmitSuccessActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.service.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/6.
 */
public class OrderAdapter extends  RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    private ArrayList<Order> orderList = new ArrayList<>();
    private Context context;

    public OrderAdapter(ArrayList<Order> orderList,Context context){
        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Order order = orderList.get(position);
        Netroid.displayBabyImage(order.getShop_logo(), holder.shop_logo);
        holder.shop_name.setText(order.getShop_name());
        holder.order_status.setText(order.getOrder_status());
        Netroid.displayBabyImage(order.getProduct_img(), holder.product_img);
        holder.product_num.setText("数量：" + order.getProduct_number());
        holder.order_amount.setText(order.getOrder_amount() + "");
        if (order.getOrder_status().equals("待付款")){
            holder.order_btn.setText("付款");
            holder.btn_container.setVisibility(View.VISIBLE);
        }else if(order.getOrder_status().equals("待使用")){
            holder.order_btn.setText("查看券码");
            holder.btn_container.setVisibility(View.VISIBLE);
        }else if(order.getOrder_status().equals("待评价")){
            holder.order_btn.setText("评价");
            holder.btn_container.setVisibility(View.VISIBLE);
        }else if(order.getOrder_status().equals("")){
            holder.order_btn.setText("申请退款");
            holder.btn_container.setVisibility(View.VISIBLE);
        }else if(order.getOrder_status().equals("退款中")){
            holder.btn_container.setVisibility(View.GONE);
        }else if(order.getOrder_status().equals("已退款")){
            holder.btn_container.setVisibility(View.GONE);
        }
        holder.shop_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ServiceShopDetailActivity.class);
                intent.putExtra("shop_id",order.getShop_id());
                context.startActivity(intent);
            }
        });

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(order.getOrder_status().equals("待付款")) {
                       return;
                    }
                    Intent intent = new Intent(context, ServiceOrderDetailActivity.class);
                    intent.putExtra("order_id", order.getOrder_id());
                    context.startActivity(intent);
                }
            });
        holder.order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getOrder_status().equals("待付款")){
                    JSONObject order_info = new JSONObject();
                    try {
                        order_info.put("order_id",order.getOrder_id());
                        order_info.put("product_sn",order.getProduct_sn());
                        order_info.put("order_amount",order.getOrder_amount());
                        order_info.put("subject",order.getProduct_name()+"-麻包服务");
                        order_info.put("desc",order.getProduct_name()+"-麻包服务");
                        Intent intent = new Intent(context, SubmitSuccessActivity.class);
                        intent.putExtra("ORDER_INFO",order_info.toString());
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(order.getOrder_status().equals("待使用")){
                    Intent intent = new Intent(context, TicketsDetailActivity.class);
                    intent.putExtra("order_id",order.getOrder_id());
                    context.startActivity(intent);
                }else if(order.getOrder_status().equals("待评价")){
                    Intent intent = new Intent(context, ServicePostCommentActivity.class);
                    intent.putExtra("index",position);
                    intent.putExtra("shop_id",order.getShop_id());
                    intent.putExtra("order_id",order.getOrder_id());
                    intent.putExtra("shop_name",order.getShop_name());
                    context.startActivity(intent);
                }else if(order.getOrder_status().equals("")){
                }else if(order.getOrder_status().equals("退款中")){
                }else if(order.getOrder_status().equals("已退款")){
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView shop_logo;
        private TextView shop_name;
        private TextView order_status;
        private ImageView product_img;
        private TextView product_num;
        private TextView order_amount;
        private Button order_btn;
        private View container,btn_container;

        public ViewHolder(View itemView) {
            super(itemView);
            shop_logo = (ImageView) itemView.findViewById(R.id.shop_logo);
            shop_name = (TextView) itemView.findViewById(R.id.shop_name);
            order_status = (TextView) itemView.findViewById(R.id.order_status);
            product_img = (ImageView) itemView.findViewById(R.id.product_img);
            product_num = (TextView) itemView.findViewById(R.id.product_num);
            order_amount = (TextView) itemView.findViewById(R.id.order_amount);
            order_btn = (Button) itemView.findViewById(R.id.order_btn);
            container = itemView.findViewById(R.id.container);
            btn_container = itemView.findViewById(R.id.btn_container);
        }
    }
}
