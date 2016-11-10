package com.witmoon.xmb.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.ProgressInfo;

/**
 * Created by ZCM on 2015/11/27
 */
public class CheckProgressFragment extends Fragment {

    private ListView mlistview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_progress, null);
        mlistview = (ListView) view.findViewById(R.id.list_progress_info);

        return view;
    }

    class MyAdapter extends ArrayAdapter<ProgressInfo> {
        public MyAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ProgressInfo progressInfo = getItem(position);
            View view;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.progress_info, null);
                viewHolder = new ViewHolder();
                viewHolder.mtextview1 = (TextView) view.findViewById(R.id.handle_time);
                viewHolder.mtextview2 = (TextView) view.findViewById(R.id.audit_type);
                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.mtextview1.setText(progressInfo.getDeal_day());
            return view;
        }

        class ViewHolder {
            TextView mtextview1;
            TextView mtextview2;
        }
    }


}
