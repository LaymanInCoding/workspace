package com.witmoon.xmb.activity.shopping.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.api.Netroid;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2016/9/19.
 */
public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {
    private ArrayList<Map<String,Object>> mDatas;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public void setOnClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void OnItemClick();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_handpick_fragment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String,Object> map = mDatas.get(position);
        Log.e("MAP",map.toString());
        Netroid.displayAdImage(map.get("goods_img").toString(), holder.img);
        holder.goods_price.setText((String) map.get("goods_price"));
        holder.market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.market_price.setText("市场价:"+map.get("market_price"));
        holder.name.setText((String) map.get("goods_name"));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CommodityDetailActivity.start(mContext, (String) map.get("goods_id"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public FeatureAdapter(ArrayList<Map<String,Object>> data, Context context){
        this.mDatas = data;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView img;
        TextView name;
        TextView buy_now;
        TextView market_price;
        TextView goods_price;


        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            name = (TextView) itemView.findViewById(R.id.name);
            buy_now = (TextView) itemView.findViewById(R.id.buy_now);
            market_price = (TextView) itemView.findViewById(R.id.market_price);
            goods_price = (TextView) itemView.findViewById(R.id.goods_price);
            view = itemView;
        }
    }
}
