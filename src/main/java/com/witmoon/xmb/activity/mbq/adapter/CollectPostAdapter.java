package com.witmoon.xmb.activity.mbq.adapter;

import android.content.Context;
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
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.circle.CirclePost;

import java.util.ArrayList;

public class CollectPostAdapter extends  RecyclerView.Adapter<CollectPostAdapter.ViewHolder> {
    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mbq_collect_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final CirclePost post = mDatas.get(position);
        holder.postTitleTextView.setText(post.getPost_title());
        holder.postDescTextView.setText(post.getPost_content());
        holder.userNameTextView.setText(post.getCircle_name());
        holder.postCommentTotalTextView.setText(post.getReply_cnt()+"");
        holder.postImgContainerLayout.removeAllViews();
        ArrayList<String> arrayList = post.getPost_imgs();
        if (arrayList.size()==0){
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
        if(mOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView postTitleTextView,postDescTextView,postCommentTotalTextView,userNameTextView;
        public LinearLayout postImgContainerLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            postTitleTextView = (TextView) itemView.findViewById(R.id.post_title);
            postDescTextView = (TextView) itemView.findViewById(R.id.post_desc);
            postCommentTotalTextView = (TextView) itemView.findViewById(R.id.post_comment_total);
            userNameTextView = (TextView) itemView.findViewById(R.id.user_name);
            postImgContainerLayout = (LinearLayout)itemView.findViewById(R.id.post_img_container);
        }
    }

    public CollectPostAdapter(ArrayList<CirclePost> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
        int img_w = (MainActivity.screen_width - 36) / 3 - 10;
        int img_h = img_w * 133 / 184;
        layoutParams = new LinearLayout.LayoutParams(img_w, img_h);
    }

    private ArrayList<CirclePost> mDatas;
    private Context context;
    private LinearLayout.LayoutParams layoutParams;
}
