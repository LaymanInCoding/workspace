package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2017/1/4.
 */
public class SubServiceHeaderAdapter extends RecyclerView.Adapter<SubServiceHeaderAdapter.ViewHolder> {

    public SubServiceHeaderAdapter(ArrayList<Map<String, String>> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<Map<String, String>> mDatas;
    private Context context;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sub_service_header_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubServiceHeaderAdapter.ViewHolder holder, int position) {
        final Map<String, String> map = mDatas.get(position);
        Netroid.displayImage(map.get("cat_icon"), holder.subclass_img);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(context,"MabaoService3");

                Bundle bundle = new Bundle();
                bundle.putString("cat_id", map.get("cat_id"));
                bundle.putString("cat_name", map.get("cat_name"));
                UIHelper.showSimpleBack(context, SimpleBackPage.SubSubService, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView subclass_img;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            subclass_img = (ImageView) itemView.findViewById(R.id.cat_img);
            view = itemView;
        }
    }
}
