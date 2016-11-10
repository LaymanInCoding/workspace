package com.witmoon.xmb.activity.mbq.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.activity.PostActivity;
import com.witmoon.xmb.activity.mbq.activity.PostDetailActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mbq_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final JSONObject jsonObject = mDatas.get(position);
        try {
            Netroid.displayBabyImage(jsonObject.getString("user_head"),holder.user_head_img);
            holder.user_name.setText(jsonObject.getString("user_name"));
            holder.notify_time.setText(jsonObject.getString("notify_time"));
            holder.notify_base_content.setText(jsonObject.getString("notify_base_content"));
            holder.notify_content.setText(jsonObject.getString("notify_content"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    try {
                        intent.putExtra("post_id",jsonObject.getInt("notify_post_id"));
                        intent.putExtra("comment_id",jsonObject.getInt("notify_comment_id"));
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView user_head_img;
        public TextView user_name,notify_time,notify_base_content,notify_content;
        public ViewHolder(View itemView) {
            super(itemView);
            user_head_img = (ImageView)itemView.findViewById(R.id.user_head_img);
            user_name = (TextView)itemView.findViewById(R.id.user_name);
            notify_time = (TextView)itemView.findViewById(R.id.notify_time);
            notify_base_content = (TextView)itemView.findViewById(R.id.notify_base_content);
            notify_content = (TextView)itemView.findViewById(R.id.notify_content);
        }
    }

    public MessageAdapter(ArrayList<JSONObject> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<JSONObject> mDatas;
    private Context context;
}
