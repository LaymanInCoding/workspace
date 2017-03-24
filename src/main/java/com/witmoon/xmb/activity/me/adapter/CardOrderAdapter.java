package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.card.CardOrderGoodsAdapter;
import com.witmoon.xmb.model.ElecCardBean;

import java.util.List;

/**
 * Created by ming on 2017/3/22.
 */
public class CardOrderAdapter extends RecyclerView.Adapter<CardOrderAdapter.ViewHolder> {

    private List<ElecCardBean> mDataList;
    private Context mContext;
    private OnPayTextClickListener mListener;
    private LayoutInflater mLayoutInflater;

    public void setOnPayTextClickListener(OnPayTextClickListener listener) {
        this.mListener = listener;
    }

    public interface OnPayTextClickListener {
        void onPayTextClick(int position);

        void onDetailTextClick(int position);
    }

    public CardOrderAdapter(Context context, List<ElecCardBean> dataList) {
        this.mDataList = dataList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ElecCardBean bean = mDataList.get(position);
        holder.addTimeText.setText(bean.getAdd_time());
        holder.orderTypeText.setText(bean.getOrder_status_code());
        holder.orderAmountText.setText(bean.getCard_amount());
        if (!bean.getOrder_status_code().equals("待付款")) {
            holder.orderPayText.setVisibility(View.GONE);
        } else {
            holder.orderPayText.setVisibility(View.VISIBLE);
        }
        holder.orderDetailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onDetailTextClick(position);
            }
        });
        holder.orderPayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onPayTextClick(position);
            }
        });
        List<ElecCardBean.CardBean> cardBeanList = bean.getOrder_cards_list();
        CardOrderGoodsAdapter adapter = new CardOrderGoodsAdapter(mContext, cardBeanList);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setAutoMeasureEnabled(true);
        holder.cardOrderListContainer.setLayoutManager(manager);
        holder.cardOrderListContainer.setNestedScrollingEnabled(false);
        holder.cardOrderListContainer.setAdapter(adapter);
//        for (int i = 0; i < cardBeanList.size(); i++) {
//            RelativeLayout containerView = (RelativeLayout) mLayoutInflater.inflate(R.layout.item_card_confirm_goods, holder.cardOrderListContainer, false);
//            ImageLoader.getInstance().displayImage(cardBeanList.get(i).getCard_img(), (ImageView) containerView.findViewById(R.id.goods_img), AppContext.options_memory);
//            ((TextView) containerView.findViewById(R.id.card_name)).setText(cardBeanList.get(i).getCard_name());
//            ((TextView) containerView.findViewById(R.id.card_money)).setText(cardBeanList.get(i).getCard_money());
//            ((TextView) containerView.findViewById(R.id.card_num)).setText("数量：" + cardBeanList.get(i).getCard_number());
//            holder.cardOrderListContainer.addView(containerView);
//        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView addTimeText;
        private TextView orderTypeText;
        private TextView orderAmountText;
        private TextView orderDetailText;
        private TextView orderPayText;
        private RecyclerView cardOrderListContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            addTimeText = (TextView) itemView.findViewById(R.id.add_time);
            orderTypeText = (TextView) itemView.findViewById(R.id.order_status);
            orderAmountText = (TextView) itemView.findViewById(R.id.order_amount);
            orderDetailText = (TextView) itemView.findViewById(R.id.detail_button);
            orderPayText = (TextView) itemView.findViewById(R.id.pay_button);
            cardOrderListContainer = (RecyclerView) itemView.findViewById(R.id.card_order_list);
        }
    }
}
