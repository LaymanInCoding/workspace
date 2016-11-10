package com.witmoon.xmb.activity.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.main.fragment.MyBabyInfoFragment;
import com.witmoon.xmb.activity.specialoffer.Baby_DetailsActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.RecordDetails;
import com.witmoon.xmb.ui.widget.SortTextView;

import java.util.ArrayList;

/**
 * Created by ZCM on 2016/1/25
 */
public class BabyInfoAdapter extends  RecyclerView.Adapter<BabyInfoAdapter.ViewHolder>{

    private ArrayList<Object> mList;
    private Context mContext;

    public BabyInfoAdapter(ArrayList<Object> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RecordDetails recordDetails = (RecordDetails)mList.get(position);
        if(recordDetails.getIs_first() == 1){
            ((TextView)(holder.headView.findViewById(R.id.publish_date_month))).setText(recordDetails.getGroup());
            ((TextView)(holder.headView.findViewById(R.id.publish_count))).setText(MyBabyInfoFragment.diaryHashMap.get(recordDetails.getGroup()) + "篇");
            holder.headView.setVisibility(View.VISIBLE);
        }else{
            holder.headView.setVisibility(View.GONE);
        }

        if(!recordDetails.getMood().equals("")){
            holder.mood_img.setVisibility(View.VISIBLE);
            holder.mood_img.setImageResource(Const.mood_active_img[Integer.parseInt(recordDetails.getMood())]);
        }else{
            holder.mood_img.setVisibility(View.GONE);
        }
        if(!recordDetails.getWeather().equals("")){
            holder.weather_img.setVisibility(View.VISIBLE);
            holder.weather_img.setImageResource(Const.weather_icon_img[Integer.parseInt(recordDetails.getWeather())]);
        }else{
            holder.weather_img.setVisibility(View.GONE);
        }
        if(recordDetails.getWeather().equals("") && recordDetails.getMood().equals("")){
            holder.diary_mood.setVisibility(View.GONE);
        }else{
            holder.diary_mood.setVisibility(View.VISIBLE);
        }
        holder.publish_weekday.setText(recordDetails.getWeek());
        holder.publish_day.setText(recordDetails.getDay());
        final int index = position;
        holder.item.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Baby_DetailsActivity.class);
                intent.putExtra("index", index);
                intent.putExtra("ZKD", recordDetails);
                mContext.startActivity(intent);
            }
        });

        if(recordDetails.getPosition().equals("")){
            holder.publish_place.setVisibility(View.GONE);
            holder.publish_location.setVisibility(View.GONE);
        }else{
            holder.publish_place.setText(recordDetails.getPosition());
            holder.publish_place.setVisibility(View.VISIBLE);
            holder.publish_location.setVisibility(View.VISIBLE);
        }

        holder.diary_info.setText(recordDetails.getContent());
        if (recordDetails.getPhoto().size() > 0) {
            Netroid.displayImage(recordDetails.getPhoto().get(0).url, holder.img);
        }

        holder.publish_time.setText(recordDetails.getMonth() + "月" + recordDetails.getDay() + "日 " + recordDetails.getAddtime());
        if (recordDetails.getPhoto().size() > 0) {
            holder.img.setVisibility(View.VISIBLE);
            Netroid.displayBabyImage(recordDetails.getPhoto_thumb().get(0).url,  holder.img);
        }else {
            holder.img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView publish_weekday;
        TextView publish_day;
        TextView publish_place;
        SortTextView diary_info;
        TextView publish_time;
        ImageView img;
        ImageView mood_img;
        ImageView weather_img;
        View diary_mood,publish_location;
        View headView,item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            publish_weekday = (TextView) itemView.findViewById(R.id.publish_weekday);
            publish_day = (TextView) itemView.findViewById(R.id.publish_day);
            publish_place = (TextView) itemView.findViewById(R.id.publish_place);
            diary_info = (SortTextView) itemView.findViewById(R.id.diary_info);
            publish_time = (TextView) itemView.findViewById(R.id.publish_time);
            img = (ImageView) itemView.findViewById(R.id.diary_pic);
            mood_img = (ImageView) itemView.findViewById(R.id.mood_icon);
            weather_img = (ImageView) itemView.findViewById(R.id.weather_icon);
            diary_mood = itemView.findViewById(R.id.diary_mood);
            publish_location = itemView.findViewById(R.id.publish_location);
            headView = itemView.findViewById(R.id.header);
        }
    }
}
