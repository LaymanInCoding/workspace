package com.witmoon.xmb.activity.card;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/3/22.
 */
public class CardInfoAdapter extends LinearBaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Map<String, Object>> mDataList;
    private Context mContext;
    private String copy_content = "";

    public CardInfoAdapter(Context context, List<Map<String, Object>> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCountOfIndexViewType(int mType) {
        if (mType == 0) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_card_info_list, parent, false);
            holder = new ItemHolder();
            holder.g_container = (LinearLayout) convertView.findViewById(R.id.g_container);
            holder.copy_textView = (TextView) convertView.findViewById(R.id.copy_text);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        Map<String, Object> data = mDataList.get(position);
        List<Map<String, String>> list = (List<Map<String, String>>) data.get("virtual");
        for (int i = 0; i < list.size(); i++) {
            LinearLayout containerView = (LinearLayout) mLayoutInflater.inflate(R.layout.item_card_info, parent, false);
            ((TextView) containerView.findViewById(R.id.card_name)).setText(list.get(i).get("card_name"));
            ((TextView) containerView.findViewById(R.id.card_account)).setText(list.get(i).get("card_no"));
            ((TextView) containerView.findViewById(R.id.card_password)).setText(list.get(i).get("card_pass"));
            copy_content += "卡号：" + list.get(i).get("card_no") + "\n" + "卡密：" + list.get(i).get("card_pass") + "\n";
            holder.g_container.addView(containerView);
        }
        holder.copy_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy(copy_content);

            }
        });

        return convertView;
    }

    private void copy(String content) {
        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
        XmbUtils.showMessage(mContext,"信息复制成功");
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return mDataList.size();
    }

    class ItemHolder {
        LinearLayout g_container;
        TextView copy_textView;
    }
}