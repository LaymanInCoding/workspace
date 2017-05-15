package com.witmoon.xmb.activity.card;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.ElecCard;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;

import java.util.ArrayList;

/**
 * Created by ming on 2017/3/21.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private ArrayList<ElecCard> mDataList;
    private Context mContext;
    private OnItemClickListener mListener;

    interface OnItemClickListener {
        void onShoppingCartChange(int position, int number);

        void onItemDelete(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CardAdapter(Context context, ArrayList<ElecCard> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_buy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.priceText.setText(mDataList.get(position).getCard_money());
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemDelete(position);
                }
            }
        });
        holder.irText.setNumber(mDataList.get(position).getCard_num());
        holder.irText.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int number) {
                if (mListener != null) {
                    mListener.onShoppingCartChange(position, holder.irText.getNumber());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImg;
        private IncreaseReduceTextView irText;
        private TextView priceText;

        public ViewHolder(View itemView) {
            super(itemView);
            deleteImg = (ImageView) itemView.findViewById(R.id.item_delete);
            priceText = (TextView) itemView.findViewById(R.id.price_text);
            irText = (IncreaseReduceTextView) itemView.findViewById(R.id.cards_number_edit);
        }
    }
}
