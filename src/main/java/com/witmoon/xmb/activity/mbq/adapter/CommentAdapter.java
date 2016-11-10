package com.witmoon.xmb.activity.mbq.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.activity.PostCommentActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.circle.CircleCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(CircleCategory circleCategory);
        void onItemButtonClick(int circle_id);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mbq_post_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final JSONObject jsonObject = mDatas.get(position);
        try {
            Netroid.displayBabyImage(jsonObject.getString("user_head"), holder.user_head_img);
            holder.user_name.setText(jsonObject.getString("user_name"));
            holder.comment_post_time.setText(jsonObject.getString("comment_time"));
            holder.comment_floor.setText(jsonObject.getString("comment_floor"));
            holder.comment_content.setText(jsonObject.getString("comment_content"));
            JSONArray imgsJsonArray = jsonObject.getJSONArray("comment_imgs");
            holder.comment_imgs_container.removeAllViews();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < imgsJsonArray.length();i++){
                ImageView riv = new ImageView(context);
                layoutParams.width = MainActivity.screen_width - 50;
                layoutParams.setMargins(0,0,0,20);
                riv.setMaxHeight(MainActivity.screen_width * 5);
                riv.setScaleType(ImageView.ScaleType.FIT_XY);
                riv.setAdjustViewBounds(true);
                riv.setLayoutParams(layoutParams);
                Netroid.displayImage(imgsJsonArray.getString(i), riv);
                holder.comment_imgs_container.addView(riv);
            }
            if(jsonObject.has("comment_reply")){
                holder.reply_comment_container.setVisibility(View.VISIBLE);
                Netroid.displayBabyImage(jsonObject.getJSONObject("comment_reply").getString("user_head"), holder.reply_user_head);
                holder.reply_user_name.setText(jsonObject.getJSONObject("comment_reply").getString("user_name"));
                holder.reply_content.setText(jsonObject.getJSONObject("comment_reply").getString("comment_content"));
            }else{
                holder.reply_comment_container.setVisibility(View.GONE);
            }
            holder.reply_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostCommentActivity.class);
                    try {
                        Log.e("jsonObject", jsonObject.toString());
                        intent.putExtra("post_id",jsonObject.getString("post_id"));
                        intent.putExtra("comment_reply_id",jsonObject.getString("comment_id"));
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
        public ImageView user_head_img,reply_user_head;
        public TextView user_name,comment_floor,comment_post_time,comment_content,reply_user_name,reply_content;
        public LinearLayout comment_imgs_container;
        public LinearLayout reply_comment_container;
        public View reply_btn;
        public ViewHolder(View itemView) {
            super(itemView);
            user_head_img = (ImageView)itemView.findViewById(R.id.user_head_img);
            user_name = (TextView)itemView.findViewById(R.id.user_name);
            comment_floor = (TextView)itemView.findViewById(R.id.comment_floor);
            comment_post_time = (TextView)itemView.findViewById(R.id.comment_post_time);
            comment_content = (TextView)itemView.findViewById(R.id.comment_content);
            comment_imgs_container = (LinearLayout) itemView.findViewById(R.id.comment_imgs_container);
            reply_comment_container = (LinearLayout) itemView.findViewById(R.id.reply_comment_container);
            reply_user_head = (ImageView) reply_comment_container.findViewById(R.id.reply_user_head);
            reply_user_name = (TextView) reply_comment_container.findViewById(R.id.reply_user_name);
            reply_content = (TextView) reply_comment_container.findViewById(R.id.reply_content);
            reply_btn = itemView.findViewById(R.id.reply_btn);
        }
    }

    public CommentAdapter(ArrayList<JSONObject> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<JSONObject> mDatas;
    private Context context;
}
