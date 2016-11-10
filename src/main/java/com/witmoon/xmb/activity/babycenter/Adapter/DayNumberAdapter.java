package com.witmoon.xmb.activity.babycenter.Adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmblibrary.linearlistview.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DayNumberAdapter extends  RecyclerView.Adapter<DayNumberAdapter.ViewHolder> {
    private int width;
    private int padding;
    private float font_size1,font_size2,font_size11,font_size22;
    private Typeface tf;

    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(
            OnItemClickListener onItemButtonClickListener) {
        mOnClickListener = onItemButtonClickListener;
    }

    public DayNumberAdapter(ArrayList<HashMap<String, String>> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
        AssetManager mgr = context.getAssets();//得到AssetManager
        tf=Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.width = wm.getDefaultDisplay().getWidth();
        padding = (int)context.getResources().getDimension(R.dimen.dimen_80_dip)*2;
        font_size1 = DensityUtil.px2dip(context,context.getResources().getDimension(R.dimen.dimen_35_dip));
        font_size2 = DensityUtil.px2dip(context, context.getResources().getDimension(R.dimen.dimen_28_dip));
        font_size11 = DensityUtil.px2dip(context, context.getResources().getDimension(R.dimen.dimen_24_dip));
        font_size22 = DensityUtil.px2dip(context, context.getResources().getDimension(R.dimen.dimen_20_dip));

    }

    private ArrayList<HashMap<String, String>> mDatas;
    private Context context;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daynum_checked, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setLayoutParams(new LinearLayout.LayoutParams((width - padding) / 3, LinearLayout.LayoutParams.MATCH_PARENT));
        final Map<String, String> map = mDatas.get(position);
        holder.date_textview.setText(map.get("date"));
        holder.daynum_textview.setText(map.get("daynum"));
        if (map.get("is_checked").equals("1")){
            holder.date_textview.setTextSize(font_size1);
            holder.daynum_textview.setTextSize(font_size2);
        }else{
            holder.date_textview.setTextSize(font_size11);
            holder.daynum_textview.setTextSize(font_size22);
        }
        holder.date_textview.setTypeface(tf);
        holder.daynum_textview.setTypeface(tf);
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
        TextView date_textview,daynum_textview;

        public ViewHolder(View itemView) {
            super(itemView);
            date_textview = (TextView) itemView.findViewById(R.id.date_textView);
            daynum_textview = (TextView) itemView.findViewById(R.id.daynum_textview);
        }
    }
}