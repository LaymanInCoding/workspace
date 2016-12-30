package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de on 2016/12/20.
 */
public class MabaoBeanAdapter extends RecyclerView.Adapter<MabaoBeanAdapter.BeanHolder> {

    private ArrayList<JSONObject> mArrayList;
    private Context mContext;


    public MabaoBeanAdapter(Context context, ArrayList<JSONObject> list) {
        this.mContext = context;
        this.mArrayList = list;
    }


    @Override
    public BeanHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bean_info, parent, false);
        return new BeanHolder(view);

    }

    @Override
    public void onBindViewHolder(BeanHolder holder, int position) {
        JSONObject beanObject = mArrayList.get(position);
        try {
            holder.mItemBeanTime.setText(beanObject.getString("record_time"));
            holder.mItemBeanTitle.setText(beanObject.getString("record_desc"));
            holder.mItemBeanTitle.setTextColor(Color.parseColor("#555555"));
            holder.mItemBeanTime.setTextColor(Color.parseColor("#999999"));
            holder.mItemBeanValue.setTextColor(Color.parseColor("#ff7162"));
            holder.mItemBeanValue.setText(beanObject.getString("record_val"));
            if (beanObject.getString("record_type").equals("0") || beanObject.getString("record_type").equals("2")) {
                holder.mItemBeanValue.setTextColor(Color.parseColor("#ff7162"));
            } else if (beanObject.getString("record_type").equals("1")) {
                holder.mItemBeanTitle.setTextColor(Color.parseColor("#bbbbbb"));
                holder.mItemBeanValue.setTextColor(Color.parseColor("#bbbbbb"));
                holder.mItemBeanTime.setTextColor(Color.parseColor("#bbbbbb"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_bean_title)
        TextView mItemBeanTitle;
        @BindView(R.id.item_bean_time)
        TextView mItemBeanTime;
        @BindView(R.id.item_value)
        TextView mItemBeanValue;

        public BeanHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
