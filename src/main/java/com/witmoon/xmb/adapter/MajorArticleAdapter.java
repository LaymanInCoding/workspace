package com.witmoon.xmb.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.MajorArticle;

import java.util.ArrayList;

public class MajorArticleAdapter extends  RecyclerView.Adapter<MajorArticleAdapter.ViewHolder> {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_major_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final MajorArticle article = mDatas.get(position);
        holder.major_article_title.setText(article.title);
        holder.major_article_abstract.setText(article.abstract_text);
        holder.major_article_author.setText("作者:"+article.author);
        if(article.atype.equals("短篇")){
            holder.major_article_type.setBackgroundResource(R.drawable.bg_book_short);
            holder.major_article_type.setTextColor(Color.rgb(217,93,86));
        }else{
            holder.major_article_type.setBackgroundResource(R.drawable.bg_book_long);
            holder.major_article_type.setTextColor(Color.rgb(139,194,101));
        }
        holder.major_article_type.setText(article.atype);
        int cnt = article.imgs.size();
        cnt = cnt > 3 ? 3 : cnt;
        for(int i = 0; i < cnt;i++){
            if (i == 0){
                holder.major_article_img0.setVisibility(View.VISIBLE);
                Netroid.displayImage(article.imgs.get(i),holder.major_article_img0);
            }else if(i == 1){
                holder.major_article_img1.setVisibility(View.VISIBLE);
                Netroid.displayImage(article.imgs.get(i),holder.major_article_img1);
            }else if(i == 2){
                holder.major_article_img2.setVisibility(View.VISIBLE);
                Netroid.displayImage(article.imgs.get(i),holder.major_article_img2);
            }
        }
        holder.major_article_img0.setLayoutParams(layoutParams);
        holder.major_article_img1.setLayoutParams(layoutParams);
        holder.major_article_img2.setLayoutParams(layoutParams);
        for (int i = 3; i > cnt; i--){
            if (i == 3){
                holder.major_article_img2.setVisibility(View.INVISIBLE);
            }else if(i == 2){
                holder.major_article_img1.setVisibility(View.INVISIBLE);
            }else if(i == 1){
                holder.major_article_img0.setVisibility(View.INVISIBLE);
            }
        }
        if (cnt == 0){
            holder.img_container.setVisibility(View.GONE);
        }else{
            holder.img_container.setVisibility(View.VISIBLE);
        }
        if(mOnClickListener != null){
            holder.itemView.setOnClickListener((View v) -> mOnClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView major_article_title,major_article_type,major_article_abstract,major_article_author;
        public ImageView major_article_img0,major_article_img1,major_article_img2;
        public LinearLayout img_container;
        public ViewHolder(View itemView) {
            super(itemView);
            major_article_title = (TextView) itemView.findViewById(R.id.major_article_title);
            major_article_author = (TextView) itemView.findViewById(R.id.major_article_author);
            major_article_type = (TextView) itemView.findViewById(R.id.major_article_type);
            major_article_abstract = (TextView) itemView.findViewById(R.id.major_article_abstract);
            major_article_img0 = (ImageView) itemView.findViewById(R.id.major_article_img0);
            major_article_img1 = (ImageView) itemView.findViewById(R.id.major_article_img1);
            major_article_img2 = (ImageView) itemView.findViewById(R.id.major_article_img2);
            img_container = (LinearLayout) itemView.findViewById(R.id.img_container);
        }
    }

    public MajorArticleAdapter(ArrayList<MajorArticle> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
        int img_w = (MainActivity.screen_width - 104) / 3;
        layoutParams = new LinearLayout.LayoutParams(img_w, img_w);
        layoutParams.setMargins(0,0,20,0);
    }

    private ArrayList<MajorArticle> mDatas;
    private Context context;
    private LinearLayout.LayoutParams layoutParams;
}
