package com.witmoon.xmb.activity.mabao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.BaseBean;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmblibrary.linearlistview.util.DensityUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2016/3/18.
 */
public class SubClassAdapter extends BaseAdapter {
    public SubClassAdapter(ArrayList<Map<String, String>> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private ArrayList<Map<String, String>> mDatas;
    private Context context;
    private String red_nu;
    @Override
    public int getCount() {
        return mDatas.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder hoder = null;
        if (convertView == null) {
            hoder = new ViewHoder();
            convertView = LayoutInflater.from(context).inflate(R.layout.subclass_h, parent, false);
            hoder.subclass_img = (ImageView) convertView.findViewById(R.id.subclass_img);
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) hoder.subclass_img.getLayoutParams();
            linearParams.width = (MainActivity.screen_width - DensityUtil.dip2px(context,75)) / 4;
            linearParams.height = (MainActivity.screen_width - DensityUtil.dip2px(context,75))  * 213 / 167 / 4;
            hoder.subclass_img.setLayoutParams(linearParams);
            hoder.subclass_name = (TextView) convertView.findViewById(R.id.subclass_name);
            hoder.view = convertView;
            convertView.setTag(hoder);
        } else {
            hoder = (ViewHoder) convertView.getTag();
        }
        final Map<String, String> map = mDatas.get(position);
        Netroid.displayImage(map.get("icon"), hoder.subclass_img);
        hoder.subclass_name.setText(map.get("cat_name"));
        hoder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("cat_id", map.get("cat_id"));
                bundle.putString("is_type","0");
                bundle.putString("cat_name",map.get("cat_name"));
                UIHelper.showSimpleBack(context, SimpleBackPage.SUBCLASS_BOM, bundle);
            }
        });
        return convertView;
    }


    class ViewHoder {
        private ImageView subclass_img;
        private TextView subclass_name;
        private View view;
    }
}