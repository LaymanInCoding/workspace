package com.witmoon.xmb.activity.goods.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.EvaluateBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de on 2017/3/2.
 */
public class GoodsCommentAdapter extends RecyclerView.Adapter<GoodsCommentAdapter.ViewHolder> {

    private List<EvaluateBean> mDataList;
    private Context mContext;

    public GoodsCommentAdapter(Context context, List<EvaluateBean> list) {
        this.mContext = context;
        this.mDataList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_good_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EvaluateBean evaluateBean = mDataList.get(position);
        holder.content.setText(evaluateBean.getContent());
        holder.nameText.setText(evaluateBean.getAuthor());
        holder.rating.setRating(Float.valueOf(evaluateBean.getmRating() + ""));

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView content;
        private TextView nameText;
        private RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content_e);
            nameText = (TextView) itemView.findViewById(R.id.name_e);
            rating = (RatingBar) itemView.findViewById(R.id.rating_e);
        }
    }
}
