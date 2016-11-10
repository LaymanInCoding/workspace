package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.PreviewImage;
import com.witmoon.xmb.activity.service.ServiceCommentActivity;
import com.witmoon.xmb.activity.service.UserInfoActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.ImageBDInfo;
import com.witmoon.xmb.model.ImageInfo;
import com.witmoon.xmb.model.service.Comment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZCM on 2016/1/25
 */
public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private ArrayList<Comment> mList;
    private Context mContext;
    private int width;

    public CommentAdapter(ArrayList<Comment> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.width = wm.getDefaultDisplay().getWidth();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment comment = mList.get(position);
        Netroid.displayBabyImage(comment.getComment_header_img(), holder.comment_header_img);
        holder.comment_username.setText(comment.getComment_username());
        holder.comment_date.setText(comment.getComment_date());
        holder.comment_content.setText(comment.getComment_content());
        ArrayList<String> comment_thumb_imgs = comment.getComment_thumb_imgs();
        ArrayList<String> comment_imgs = comment.getComment_imgs();
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        for(int i =0;i<comment_imgs.size();i++){
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.url = comment_imgs.get(i);
            imageInfo.width = 0;
            imageInfo.height = 0;
            imageInfos.add(imageInfo);
        }
        holder.comment_header_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("user_id",comment.getUser_id());
                intent.putExtra("user_name",comment.getComment_username());
                intent.putExtra("header_img",comment.getComment_header_img());
                mContext.startActivity(intent);
            }
        });
        holder.container1.removeAllViews();
        holder.container2.removeAllViews();
        if(comment_thumb_imgs.size() > 3){
            for(int i = 0;i < 3; i++){
                ImageView imageView = new ImageView(mContext);
                int w = (int)(mContext.getResources().getDimension(R.dimen.dimen_16_dip));
                imageView.setLayoutParams(new LinearLayout.LayoutParams((this.width - w) / 3   , (this.width - w) / 3 - w));
                imageView.setPadding(w, 0, 0, 0);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(new GridOnclick(i,imageInfos));
                Netroid.displayBabyImage(comment_thumb_imgs.get(i), imageView);
                holder.container1.addView(imageView);
            }
            for(int i = 3;i < comment_thumb_imgs.size(); i++){
                ImageView imageView = new ImageView(mContext);
                int w = (int)(mContext.getResources().getDimension(R.dimen.dimen_16_dip));
                imageView.setLayoutParams(new LinearLayout.LayoutParams((this.width - w) / 3   , (this.width - w) / 3 - w));
                imageView.setPadding(w, 0, 0, 0);
                imageView.setOnClickListener(new GridOnclick(i + 3, imageInfos));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Netroid.displayBabyImage(comment_thumb_imgs.get(i), imageView);
                holder.container2.addView(imageView);
            }
            holder.container1.setVisibility(View.VISIBLE);
            holder.container2.setVisibility(View.VISIBLE);
        }else if(comment_thumb_imgs.size() != 0){
            for(int i = 0;i < comment_thumb_imgs.size(); i++){
                ImageView imageView = new ImageView(mContext);
                int w = (int)(mContext.getResources().getDimension(R.dimen.dimen_16_dip));
                imageView.setLayoutParams(new LinearLayout.LayoutParams((this.width - w) / 3   , (this.width - w) / 3 - w));
                imageView.setPadding(w,0,0,0);
                imageView.setOnClickListener(new GridOnclick(i,imageInfos));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Netroid.displayBabyImage(comment_thumb_imgs.get(i), imageView);
                holder.container1.addView(imageView);
            }
            holder.container1.setVisibility(View.VISIBLE);
            holder.container2.setVisibility(View.GONE);
        }else{
            holder.container1.setVisibility(View.GONE);
            holder.container2.setVisibility(View.GONE);
        }
    }

    class GridOnclick implements View.OnClickListener {

        private int row;
        private ArrayList<ImageInfo> imageInfos;

        public GridOnclick(int row,ArrayList<ImageInfo> imageInfos) {
            this.row = row;
            this.imageInfos = imageInfos;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, PreviewImage.class);
            intent.putExtra("data", (Serializable) imageInfos);
            intent.putExtra("bdinfo", new ImageBDInfo());
            intent.putExtra("index", row);
            mContext.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView comment_date;
        TextView comment_username;
        ImageView comment_header_img;
        TextView comment_content;
        LinearLayout container1,container2;
        TextView comment_total_all;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            comment_username = (TextView) itemView.findViewById(R.id.comment_username);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            comment_content = (TextView) itemView.findViewById(R.id.comment_content);
            comment_header_img = (ImageView) itemView.findViewById(R.id.comment_header_img);
            container1 = (LinearLayout) itemView.findViewById(R.id.comment_img_container1);
            container2 = (LinearLayout) itemView.findViewById(R.id.comment_img_container2);
            comment_total_all = (TextView) itemView.findViewById(R.id.comment_total_all);
            view = itemView;
        }
    }
}
