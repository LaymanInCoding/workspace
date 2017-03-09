package com.witmoon.xmb.activity.goods.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.EvaluateBean;

import java.util.List;

/**
 * Created by de on 2017/3/1.
 */
public class GoodsEvaluateAdapter extends RecyclerView.Adapter<GoodsEvaluateAdapter.ViewHolder> {
    private List<EvaluateBean> mDataList;
    private Context mContext;

    public GoodsEvaluateAdapter(Context context, List<EvaluateBean> beanList) {
        this.mContext = context;
        this.mDataList = beanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_evaluate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EvaluateBean mEvaluateBean = mDataList.get(position);
        holder.nameText.setText(mEvaluateBean.getAuthor());
        holder.content.setText(mEvaluateBean.getContent());
        holder.rating.setRating(Float.valueOf(mEvaluateBean.getmRating() + ""));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView content;
        private RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content_e);
            nameText = (TextView) itemView.findViewById(R.id.name_e);
            rating = (RatingBar) itemView.findViewById(R.id.rating_e);
        }
    }
}
