package com.witmoon.xmb.activity.card;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.model.ElecCardBean;

import java.util.List;

/**
 * Created by ming on 2017/3/23.
 */
public class CardOrderGoodsAdapter extends RecyclerView.Adapter<CardOrderGoodsAdapter.ViewHolder> {
    private List<ElecCardBean.CardBean> mDataList;
    private Context mContext;

    public CardOrderGoodsAdapter(Context context, List<ElecCardBean.CardBean> list) {
        this.mContext = context;
        this.mDataList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_confirm_goods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ElecCardBean.CardBean bean = mDataList.get(position);
        ImageLoader.getInstance().displayImage(bean.getCard_img(), holder.card_img, AppContext.options_memory);
        holder.card_name.setText(bean.getCard_name());
        holder.card_money.setText(bean.getCard_money());
        holder.card_num.setText("数量：" + bean.getCard_number());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView card_name;
        private TextView card_money;
        private TextView card_num;
        private ImageView card_img;

        public ViewHolder(View itemView) {
            super(itemView);
            card_name = (TextView) itemView.findViewById(R.id.card_name);
            card_money = (TextView) itemView.findViewById(R.id.card_money);
            card_num = (TextView) itemView.findViewById(R.id.card_num);
            card_img = (ImageView) itemView.findViewById(R.id.goods_img);
        }
    }
}
