package com.witmoon.xmb.activity.shopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.util.DensityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class SelectedActivityAdapter extends  RecyclerView.Adapter<SelectedActivityAdapter.ViewHolder> {
    private OnItemClickListener mOnItemClickListener;
    private int width;

    public interface OnItemClickListener {
        void onItemnClick(Map<String, Object> map);
    }

    public void setOnItemClickListener(
            OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, Object> map = mDatas.get(position);
        holder.img.setLayoutParams(new LinearLayout.LayoutParams(layout_width, layout_height));
        Netroid.displayAdImage(map.get("ad_img").toString(), holder.img);
        JSONArray goodsJsonArray = (JSONArray) map.get("goods");
        holder.g_container.removeAllViews();
        for(int i = 0; i < goodsJsonArray.length(); i++ ){
            try {
                final JSONObject jsonObject = goodsJsonArray.getJSONObject(i);
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_selected_good, null);
                ImageView goods_img = (ImageView) linearLayout.findViewById(R.id.g_img);
                TextView goods_name = (TextView) linearLayout.findViewById(R.id.g_name);
                TextView goods_price = (TextView) linearLayout.findViewById(R.id.g_price);
                Netroid.displayAdImage(jsonObject.getString("goods_thumb"),goods_img);
                goods_name.setText(jsonObject.getString("goods_name"));
                goods_price.setText("¥"+jsonObject.getString("goods_price"));
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UmengStatic.registStat(context,"AffordablePlane6");

                        try {
                            CommodityDetailActivity.start(context, jsonObject.getString("goods_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.g_container.addView(linearLayout);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LinearLayout relativeLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_selected_more, null);
        holder.g_container.addView(relativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener(map);
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(context,"AffordablePlane5");

                clickListener(map);
            }
        });
    }

    public void clickListener(Map<String,Object> map){
        if (null != map.get("ad_type")) {
            int type = Integer.parseInt(map.get("ad_type").toString());
            //专题  2商品 3网页 4团购 5帖子
            String id = map.get("act_id").toString();
            if (type == 1) {
                MarketPlaceActivity.start(context, id,map.get("ad_name").toString());
            } else if (type == 2) {
                CommodityDetailActivity.start(context, id,map.get("ad_name").toString());
            } else if (type == 3) {
                Intent intent = new Intent(context, InteractiveWebViewActivity.class);
                intent.putExtra("url", id);
                context.startActivity(intent);
            } else if (type == 4) {
                GroupBuyActivity.start(context, id);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        LinearLayout g_container;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            g_container = (LinearLayout) itemView.findViewById(R.id.g_container);
        }
    }

    public SelectedActivityAdapter(ArrayList<Map<String, Object>> mDatas, Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.mDatas = mDatas;
        this.context = context;
        this.width = wm.getDefaultDisplay().getWidth();
        this.layout_width =  this.width;
        this.layout_height = layout_width * 350 / 750;
    }

    private int layout_width = 0,layout_height = 0;
    private ArrayList<Map<String, Object>> mDatas;
    private Context context;
}
