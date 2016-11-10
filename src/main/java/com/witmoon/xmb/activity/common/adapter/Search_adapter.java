package com.witmoon.xmb.activity.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.util.ArrayList;

/**
 * Created by de on 2015/10/29.
 */
public class Search_adapter extends LinearBaseAdapter {
    private ArrayList<String> mList;
    private Context mContext;

    public Search_adapter(Context mContext, ArrayList<String> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (null == view){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_search, parent, false);
            holder.textView = (TextView) view.findViewById(R.id.search_tv);
            view.setTag(holder);
         }else{
            holder = (ViewHolder) view.getTag();
         }
        //设置holder
        holder.textView.setText(mList.get(position));
        return view;
    }

    @Override
    public int getCountOfIndexViewType(int mType) {
        if (mType == 0)
        {
            return mList.size();
        }
        return 0;
    }

    class ViewHolder{
        TextView textView;
    }
}
