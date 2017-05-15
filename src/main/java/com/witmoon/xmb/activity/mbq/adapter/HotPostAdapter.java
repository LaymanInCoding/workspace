package com.witmoon.xmb.activity.mbq.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.activity.mbq.activity.PostDetailActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.circle.CirclePost;

import java.util.ArrayList;

public class HotPostAdapter extends  RecyclerView.Adapter<HotPostAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_circle_hot_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CirclePost post = mDatas.get(position);
        Netroid.displayBabyImage(post.getAuthor_userhead(),
                holder.userHeadImgView);
        holder.userNameTextView.setText(post.getAuthor_name());
        holder.circleNameView.setText(post.getCircle_name());
        holder.circleTitleTextView.setText(post.getPost_title());
        holder.postImgContainerLayout.removeAllViews();
        ArrayList<String> arrayList = post.getPost_imgs();
        if(arrayList.size()==0){
            holder.postImgContainerLayout.setVisibility(View.GONE);
        }else{
            holder.postImgContainerLayout.setVisibility(View.VISIBLE);
        }
        for(int i = 0;i < arrayList.size();i++){
            RoundedImageView riv = new RoundedImageView(context);
            riv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            riv.setCornerRadius((float) 10);
            riv.mutateBackground(true);
            riv.setLayoutParams(layoutParams);
            riv.setPadding(0, 0, 20, 0);
            Netroid.displayImage(arrayList.get(i), riv);
            holder.postImgContainerLayout.addView(riv);
        }
        if(!post.getPost_content().equals("")) {
            holder.circleDescTextView.setVisibility(View.VISIBLE);
            holder.circleDescTextView.setText(post.getPost_content());
        }else {
            holder.circleDescTextView.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(context,"TopPost0");

                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post_id",post.getPost_id());
                intent.putExtra("post_content",post.getPost_content());
                intent.putExtra("post_title",post.getPost_title());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView userHeadImgView;
        public TextView userNameTextView,circleTitleTextView,circleDescTextView,circleNameView;
        public LinearLayout postImgContainerLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            userHeadImgView = (ImageView)itemView.findViewById(R.id.user_head_img);
            circleNameView = (TextView)itemView.findViewById(R.id.circle_name);
            userNameTextView = (TextView)itemView.findViewById(R.id.user_name);
            circleTitleTextView = (TextView)itemView.findViewById(R.id.circle_post_title);
            circleDescTextView = (TextView)itemView.findViewById(R.id.circle_post_desc);
            postImgContainerLayout = (LinearLayout)itemView.findViewById(R.id.post_img_container);
        }
    }

    public  HotPostAdapter(ArrayList<CirclePost> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
        int img_w = (MainActivity.screen_width - 20) / 3 - 10;
        int img_h = img_w * 133 / 184;
        layoutParams = new LinearLayout.LayoutParams(img_w, img_h);
    }

    private ArrayList<CirclePost> mDatas;
    private Context context;
    private LinearLayout.LayoutParams layoutParams;
}
