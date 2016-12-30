package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.me.OrderType;
import com.witmoon.xmb.activity.me.Out_ServiceActivity;
import com.witmoon.xmb.activity.me.fragment.Fill_info_Fragent;
import com.witmoon.xmb.activity.me.fragment.OrderDetailFragment;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Order;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单适配器
 * Created by zhyh on 2015/6/13.
 */
public class OrderAdapter extends BaseRecyclerAdapter {
    private Context mContext;
    private OnItemButtonClickListener mOnItemButtonClickListener;
    private OnItemButtonWlClickListener mOnItemButtonWlClickListener;
    private boolean is_is_comment;

    public void setOnItemButtonClickListener(
            OnItemButtonClickListener onItemButtonClickListener) {
        mOnItemButtonClickListener = onItemButtonClickListener;
    }

    public void setOnItemButtonWlClickListener(
            OnItemButtonWlClickListener onItemButtonWlClickListener) {
        mOnItemButtonWlClickListener = onItemButtonWlClickListener;
    }

    public OrderAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new OrderViewHolder(viewType, view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, int position) {
        OrderViewHolder oHolder = (OrderViewHolder) holder;
        final Order order = (Order) _data.get(position);
        String type = order.getOrderType();
        // 没有子订单
        if (order.getIsSplitOrder() == 0) {
            oHolder.orderBottomView.setVisibility(View.VISIBLE);
            oHolder.splitParentView.setVisibility(View.GONE);
            oHolder.noSplitParentView.setVisibility(View.VISIBLE);
            oHolder.topView.setVisibility(View.VISIBLE);
            oHolder.title.setText(OrderType.getType(type).getTitle());
            if (OrderType.getType(type) == OrderType.TYPE_WAITING_FOR_PAYMENT || OrderType.getType
                    (type) == OrderType.TYPE_FINISHED || OrderType.getType
                    (type) == OrderType.TYPE_WAITING_FOR_RECEIVING) {
                oHolder.itemButton.setVisibility(View.VISIBLE);
                if (OrderType.getType(type) == OrderType.TYPE_FINISHED) {
                    if (order.getOrderRefundType().equals("1") || order.getOrderRefundType().equals("3")) {
                        if (order.getOrderRefundType().equals("3")) {
                            oHolder.refund_button.setText("重新申请");
                        } else {
                            oHolder.refund_button.setText("申请售后");
                        }
                    } else if (order.getOrderRefundType().equals("5")) {
                        oHolder.refund_button.setText("填写运单号");
                    } else if (order.getOrderRefundType().equals("4")) {
                        oHolder.refund_button.setText("已完成退换货");
                    } else {
                        oHolder.refund_button.setText("进度查询");
                    }
                    oHolder.refund_button.setVisibility(View.VISIBLE);
                    oHolder.itemButton.setText("发表评价");
                } else if (OrderType.getType(type) == OrderType.TYPE_WAITING_FOR_PAYMENT) {
                    oHolder.itemButton.setText("去付款");
                } else if (OrderType.getType(type) == OrderType.TYPE_WAITING_FOR_RECEIVING) {
                    oHolder.itemButton.setText("确认收货");
                }
//----------------------退换货-------------------
                oHolder.refund_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Out_ orderContentNew = new Out_();
                        orderContentNew.setOrder_id(order.getId());
                        orderContentNew.setmGoodsList(order.getGoodsList());
                        orderContentNew.setOrder_sn(order.getSerialNo());
                        if (order.getOrderRefundType().equals("3") || order.getOrderRefundType().equals("1")) {
                            Intent mIntent = new Intent(mContext, Out_ServiceActivity.class);
                            mIntent.putExtra("order", orderContentNew);
                            mContext.startActivity(mIntent);
                        } else if (order.getOrderRefundType().equals("5")) {
                            Intent mIntent = new Intent(mContext, Fill_info_Fragent.class);
                            mIntent.putExtra("order", orderContentNew);
                            mContext.startActivity(mIntent);
                        } else {
                            Bundle mb = new Bundle();
                            mb.putSerializable("order", orderContentNew);
                            UIHelper.showSimpleBack(mContext, SimpleBackPage.JINDU, mb);
                        }
                    }
                });
//----------------------------------------------
                oHolder.itemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemButtonClickListener != null)
                            mOnItemButtonClickListener.onItemButtonClick(order);
                    }
                });
            } else {
                oHolder.refund_button.setVisibility(View.GONE);
                oHolder.itemButton.setVisibility(View.GONE);
            }
            //添加订单详情按钮点击事件
            oHolder.detail_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle argument = new Bundle();
                    argument.putString(OrderDetailFragment.KEY_ORDER_TYPE, order.getOrderType());
                    argument.putString(OrderDetailFragment.KEY_ORDER_SN, order.getSerialNo());
                    UIHelper.showSimpleBack(v.getContext(), SimpleBackPage.ORDER_DETAIL, argument);

                }
            });
//            oHolder.serialNoText.setVisibility(View.GONE);
            oHolder.totalPrice.setText(order.getTotalFee());
            oHolder.time.setText(order.getTime());
            oHolder.container.removeAllViews();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            List<Map<String, String>> goodsList = order.getGoodsList();
            for (Map<String, String> goodsMap : goodsList) {
                View view = inflater.inflate(R.layout.item_order_goods, oHolder.container, false);
                view.setOnClickListener(v -> CommodityDetailActivity.start(mContext, goodsMap.get("goods_id")));
                ImageView imageView = (ImageView) view.findViewById(R.id.goods_image);
                Netroid.displayBabyImage(goodsMap.get("goods_img"), imageView);
                TextView title = (TextView) view.findViewById(R.id.goods_title);
                title.setText(goodsMap.get("goods_name"));
                TextView price = (TextView) view.findViewById(R.id.goods_price);
                price.setText(goodsMap.get("goods_price"));
                TextView count = (TextView) view.findViewById(R.id.goods_count);
                count.setText("x" + goodsMap.get("count"));
                if (goodsMap.get("is_comment").equals("0")) {
                    is_is_comment = true;
                }
                oHolder.container.addView(view);
            }
            if (OrderType.getType(type) == OrderType.TYPE_FINISHED) {
                if (is_is_comment) {
                    oHolder.itemButton.setVisibility(View.VISIBLE);
                    oHolder.itemButton.setVisibility(View.VISIBLE);
                    is_is_comment = false;
                } else {
                    oHolder.itemButton.setVisibility(View.GONE);
                }
            }
        }
        //  拆单
        else {
            oHolder.noSplitParentView.setVisibility(View.GONE);
            oHolder.splitParentView.setVisibility(View.VISIBLE);
            oHolder.splitTitle.setVisibility(View.GONE);
            oHolder.splitTitle.setText(OrderType.getType(type).getTitle());
            if (OrderType.getType(type) == OrderType.TYPE_WAITING_FOR_PAYMENT || OrderType.getType
                    (type) == OrderType.TYPE_FINISHED || OrderType.getType
                    (type) == OrderType.TYPE_WAITING_FOR_RECEIVING) {
                oHolder.splitOrderBottomView.setVisibility(View.VISIBLE);
                oHolder.splitItemButton.setVisibility(View.VISIBLE);
                if (OrderType.getType(type) == OrderType.TYPE_FINISHED) {
                    oHolder.splitTitle.setVisibility(View.GONE);
                    oHolder.splitItemButton.setText("发表评价");
                } else if (OrderType.getType(type) == OrderType.TYPE_WAITING_FOR_PAYMENT) {
                    oHolder.splitItemButton.setText("去付款");
                    oHolder.splitTitle.setVisibility(View.VISIBLE);
                } else if (OrderType.getType(type) == OrderType.TYPE_WAITING_FOR_RECEIVING) {
                    oHolder.splitTitle.setVisibility(View.GONE);
                    oHolder.splitItemButton.setText("确认收货");
                }
                oHolder.splitItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemButtonClickListener != null)
                            mOnItemButtonClickListener.onItemButtonClick(order);
                    }
                });
            } else {
                oHolder.splitOrderBottomView.setVisibility(View.GONE);
                oHolder.splitTitle.setVisibility(View.GONE);
            }
//             添加子订单详情按钮点击事件
            oHolder.split_detail_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle argument = new Bundle();
                    argument.putString(OrderDetailFragment.KEY_ORDER_TYPE, order.getOrderType());
                    argument.putString(OrderDetailFragment.KEY_ORDER_SN, order.getSerialNo());
                    UIHelper.showSimpleBack(v.getContext(), SimpleBackPage.ORDER_DETAIL, argument);
                }
            });

            oHolder.splitTotalPrice.setText(order.getTotalFee());
            oHolder.splitTime.setText(order.getTime());
            oHolder.splitContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            JSONArray child_orders = order.getChild_orders();
            Out_ orderContentNew1 = new Out_();
            List<Map<String, String>> goodList = new ArrayList<>();
            for (int i = 0; i < child_orders.length(); i++) {
                try {
                    JSONObject child_order = child_orders.getJSONObject(i);
                    orderContentNew1.setOrder_sn(child_order.getString("order_sn"));
                    orderContentNew1.setOrder_id(child_order.getString("order_id"));
                    JSONArray child_order_goodsList = child_order.getJSONArray("goods_list");
                    for (int j = 0; j < child_order_goodsList.length(); j++) {
                        JSONObject goodsObj = child_order_goodsList.getJSONObject(j);
                        Map<String, String> tmap = new HashMap<>();
                        tmap.put("goods_id", goodsObj.getString("goods_id"));
                        tmap.put("goods_name", goodsObj.getString("name"));
                        tmap.put("count", goodsObj.getString("goods_number"));
                        tmap.put("is_comment", goodsObj.getString("is_comment"));
                        tmap.put("goods_img", goodsObj.getString("img"));
                        tmap.put("goods_price", goodsObj.getString("shop_price_formatted"));
                        goodList.add(tmap);
                    }
                    orderContentNew1.setmGoodsList(goodList);
                    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.item_split_order, oHolder.container, false);
                    TextView refund_button = (TextView) view.findViewById(R.id.split_refund_button);
                    TextView detail_text = (TextView) view.findViewById(R.id.detail_button);
                    refund_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mIntent = new Intent(mContext, Out_ServiceActivity.class);
                            mIntent.putExtra("order", orderContentNew1);
                            mContext.startActivity(mIntent);
                        }
                    });
                    detail_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle argument = new Bundle();
                            try {
                                argument.putString(OrderDetailFragment.KEY_ORDER_TYPE, order.getOrderType());
                                argument.putString(OrderDetailFragment.KEY_ORDER_ID, child_order.getString("order_id"));
                                argument.putString(OrderDetailFragment.KEY_ORDER_SN, child_order.getString("order_sn"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            UIHelper.showSimpleBack(v.getContext(), SimpleBackPage.ORDER_DETAIL, argument);
                        }
                    });
                    if (OrderType.getType(type) == OrderType.TYPE_WAITING_FOR_PAYMENT) {
//                        refund_button.setVisibility(View.GONE);
                        view.findViewById(R.id.order_type).setVisibility(View.GONE);
                        view.findViewById(R.id.split_order_bottom).setVisibility(View.GONE);
                    } else {
//                        if (OrderType.getType(type) == OrderType.TYPE_FINISHED) {
//                            refund_button.setVisibility(View.VISIBLE);
//                            refund_button.setText(child_order.getString("refund_status"));
//                        }
                        view.findViewById(R.id.order_type).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.split_order_bottom).setVisibility(View.VISIBLE);
                    }
                    ((TextView) view.findViewById(R.id.total_price)).setText(child_order.getString("total_fee"));
                    ((TextView) view.findViewById(R.id.child_serial_no)).setText("订单号：" + child_order.getString("order_sn"));
                    ((TextView) view.findViewById(R.id.order_type)).setText(OrderType.getType(type).getTitle());
                    oHolder.splitContainer.addView(view);
                    LinearLayout childGoodsContainerView = (LinearLayout) view.findViewById(R.id.child_container);
                    JSONArray goodsList = child_order.getJSONArray("goods_list");
                    for (int j = 0; j < goodsList.length(); j++) {
                        JSONObject goods = goodsList.getJSONObject(j);

                        View goodsContainerView = inflater.inflate(R.layout.item_order_goods, childGoodsContainerView, false);
                        String id = goods.getString("goods_id");
                        goodsContainerView.setOnClickListener(v -> CommodityDetailActivity.start(mContext, id));
                        ImageView imageView = (ImageView) goodsContainerView.findViewById(R.id.goods_image);
                        Netroid.displayBabyImage(goods.getString("goods_img"), imageView);
                        TextView title = (TextView) goodsContainerView.findViewById(R.id.goods_title);
                        title.setText(goods.getString("goods_name"));
                        TextView price = (TextView) goodsContainerView.findViewById(R.id.goods_price);
                        price.setText("¥" + goods.getString("goods_price"));
                        TextView count = (TextView) goodsContainerView.findViewById(R.id.goods_count);
                        count.setText("x" + goods.getInt("goods_number"));
//
                        if (goods.getString("is_comment").equals("0")) {
                            is_is_comment = true;
                        }
                        childGoodsContainerView.addView(goodsContainerView);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            if (OrderType.getType(type) == OrderType.TYPE_FINISHED) {
                if (is_is_comment) {
                    oHolder.splitItemButton.setVisibility(View.VISIBLE);
                    is_is_comment = false;
                } else {
                    oHolder.splitItemButton.setVisibility(View.GONE);
                }
            }
        }
    }

    public interface OnItemButtonClickListener {
        void onItemButtonClick(Order order);
    }

    public interface OnItemButtonWlClickListener {
        void onItemButtonWlClick(Order order);
    }

    // ViewHolder
    static class OrderViewHolder extends ViewHolder {
        public View topView, splitTopView;
        public TextView title, splitTitle;
        public TextView itemButton, splitItemButton, detail_button, split_detail_button, refund_button;
        public LinearLayout container, splitContainer;
        public TextView totalPrice, splitTotalPrice;
        public TextView time, splitTime;
        public LinearLayout noSplitParentView, splitParentView, orderBottomView, splitOrderBottomView;

        public OrderViewHolder(int viewType, View v) {
            super(viewType, v);
            noSplitParentView = (LinearLayout) v.findViewById(R.id.no_split);
            splitParentView = (LinearLayout) v.findViewById(R.id.split);
            orderBottomView = (LinearLayout) v.findViewById(R.id.order_bottom);
            splitOrderBottomView = (LinearLayout) v.findViewById(R.id.split_order_bottom);
            topView = v.findViewById(R.id.top_layout);
            title = (TextView) v.findViewById(R.id.title);
            detail_button = (TextView) v.findViewById(R.id.detail_button);
            split_detail_button = (TextView) v.findViewById(R.id.split_detail_button);
            itemButton = (TextView) v.findViewById(R.id.submit_button);
            refund_button = (TextView) v.findViewById(R.id.refund_button);
            container = (LinearLayout) v.findViewById(R.id.container);
            totalPrice = (TextView) v.findViewById(R.id.total_price);
            time = (TextView) v.findViewById(R.id.time);
            splitTopView = v.findViewById(R.id.split_top_layout);
            splitTitle = (TextView) v.findViewById(R.id.split_title);
            splitItemButton = (TextView) v.findViewById(R.id.split_submit_button);
            splitContainer = (LinearLayout) v.findViewById(R.id.split_order_container);
            splitTotalPrice = (TextView) v.findViewById(R.id.split_total_price);
            splitTime = (TextView) v.findViewById(R.id.split_time);
        }
    }
}
