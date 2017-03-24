package com.witmoon.xmb.activity.card;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ming on 2017/3/20.
 */
public class EntityCardAdapter extends RecyclerView.Adapter<EntityCardAdapter.ViewHolder> {
    private ArrayList<JSONObject> mDataList;
    private Context mContext;
    private OnItemClickListener mListener;

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public EntityCardAdapter(Context context, ArrayList<JSONObject> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public EntityCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_entity_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntityCardAdapter.ViewHolder holder, int position) {
//        int top = 5;
//        WindowManager wm = (WindowManager) mContext
//                .getSystemService(Context.WINDOW_SERVICE);
//        int width = wm.getDefaultDisplay().getWidth();
//        if (position % 2 == 0) {
//            holder.itemView.setPadding(DensityUtils.dp2px(mContext, 5), DensityUtils.dp2px(mContext, top), DensityUtils.dp2px(mContext, 2.5f), 0);
//        } else {
//            holder.itemView.setPadding(DensityUtils.dp2px(mContext, 2.5f), DensityUtils.dp2px(mContext, top), DensityUtils.dp2px(mContext, 5), 0);
//        }
//        holder.card_pic.setLayoutParams(new RelativeLayout.LayoutParams(width / 2 - DensityUtils.dp2px(mContext, 8), width / 2 - DensityUtils.dp2px(mContext, 15)));
        JSONObject dataObj = mDataList.get(position);
        try {

            holder.card_price.setText(dataObj.getString("goods_price"));
            holder.card_desc.setText(dataObj.getString("goods_name"));
            Netroid.displayAdImage(dataObj.getString("goods_thumb"), holder.card_pic);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView card_price;
        private TextView card_desc;
        private ImageView card_pic;

        public ViewHolder(View itemView) {
            super(itemView);
            card_desc = (TextView) itemView.findViewById(R.id.card_desc);
            card_price = (TextView) itemView.findViewById(R.id.card_price);
            card_pic = (ImageView) itemView.findViewById(R.id.card_pic);
        }
    }
}
