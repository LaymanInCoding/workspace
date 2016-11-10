package com.witmoon.xmb.activity.babycenter.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.specialoffer.Baby_DetailsActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.ImageInfo;
import com.witmoon.xmb.model.RecordDetails;

import java.util.ArrayList;

public class DiaryAdapter extends  RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    public DiaryAdapter(ArrayList<RecordDetails> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
        margin = (int)context.getResources().getDimension(R.dimen.dimen_25_dip);
    }

    private ArrayList<RecordDetails> mDatas;
    private Context context;
    private int margin;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mengbao_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RecordDetails hashMap = mDatas.get(position);
        holder.diary_day_zh.setText(hashMap.getDay());
        holder.diary_day_en.setText(hashMap.getYear()+"."+hashMap.getMonth());
        holder.diary_day_hint.setText(hashMap.getWeek() + " " + hashMap.getAddtime());
        if (hashMap.getPosition().equals("")){
            holder.place_container.setVisibility(View.GONE);
        }else{
            holder.place_container.setVisibility(View.VISIBLE);
            holder.diary_place.setText(hashMap.getPosition());
        }
        holder.diary_container.removeAllViews();
        ArrayList<ImageInfo> imgList = hashMap.getPhoto_thumb();
        if (imgList.size() == 0){
            TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.item_mengbao_diary_word, null);
            textView.setText(hashMap.getContent());
            holder.diary_container.addView(textView);
        }else if(imgList.size() == 1){
            ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.item_mengbao_diary_img, null);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(MainActivity.screen_width * 333 / 750, MainActivity.screen_width * 322 / 690));
            ImageLoader.getInstance().displayImage(imgList.get(0).url, imageView, AppContext.options_disk);
            holder.diary_container.addView(imageView);
        }else if(imgList.size() >= 2){
            LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_mengbao_diary_imgs, null);
            ImageView imageView1 = (ImageView) layout.findViewById(R.id.img1);
            ImageView imageView2 = (ImageView) layout.findViewById(R.id.img2);
            imageView1.setLayoutParams(new LinearLayout.LayoutParams(MainActivity.screen_width * 333 / 750 ,MainActivity.screen_width * 322 / 750));
            LinearLayout.LayoutParams layout2 = new LinearLayout.LayoutParams(MainActivity.screen_width * 333 / 750 ,MainActivity.screen_width * 322 / 750);
            layout2.setMargins(margin,0,0,0);
            imageView2.setLayoutParams(layout2);
            ImageLoader.getInstance().displayImage(imgList.get(0).url, imageView1, AppContext.options_disk);
            ImageLoader.getInstance().displayImage(imgList.get(1).url, imageView2, AppContext.options_disk);
            holder.diary_container.addView(layout);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Baby_DetailsActivity.class);
                intent.putExtra("index", position);
                intent.putExtra("ZKD", hashMap);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView diary_day_zh,diary_day_en,diary_day_hint,diary_place;
        public LinearLayout diary_container,place_container;

        public ViewHolder(View itemView) {
            super(itemView);
            diary_day_zh = (TextView) itemView.findViewById(R.id.diary_day_zh);
            diary_day_en = (TextView) itemView.findViewById(R.id.diary_day_en);
            diary_day_hint = (TextView) itemView.findViewById(R.id.diary_day_hint);
            diary_place = (TextView) itemView.findViewById(R.id.diary_place);
            diary_container = (LinearLayout) itemView.findViewById(R.id.diary_container);
            place_container = (LinearLayout) itemView.findViewById(R.id.place_container);
        }
    }
}