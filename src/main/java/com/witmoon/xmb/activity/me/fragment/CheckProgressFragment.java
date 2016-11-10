package com.witmoon.xmb.activity.me.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.util.TDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZCM on 2015/11/27
 */
public class CheckProgressFragment extends BaseFragment {

    private ListView mlistview;
    private Out_ out;
    private LinearLayout linearLayout;
    private TextView tv0, tv1, tv2, tv3;
    private ArrayList<Map<String, String>> mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundler = getArguments();
        out = (Out_) mBundler.getSerializable("order");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_progress, null);
        tv0 = (TextView) view.findViewById(R.id.order_num);
        linearLayout = (LinearLayout) view.findViewById(R.id._gson);
        tv1 = (TextView) view.findViewById(R.id.return_money);
        tv2 = (TextView) view.findViewById(R.id.handle_type);
        tv3 = (TextView) view.findViewById(R.id.question_desc);
        mlistview = (ListView) view.findViewById(R.id.list_progress_info);
        if (AppContext.isNetworkAvailable(getContext())) {
            UserApi.check_proo(out.getOrder_id(), new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONObject js = response.getJSONObject("status");
                        if (js.getString("succeed").equals("0")) {
                            AppContext.showToast("信息错误！");
                            return;
                        }
                        JSONObject js_data = response.getJSONObject("data");
                        tv0.setText("：" + out.getOrder_sn());
                        tv1.setText(js_data.getString("refund_money"));
                        if (js_data.getString("refund_type").equals("换货")) {
                            linearLayout.setVisibility(View.GONE);
                        }
                        tv2.setText(js_data.getString("refund_type"));
                        tv3.setText(js_data.getString("refund_reason"));
                        mList = new ArrayList<Map<String, String>>();
                        JSONArray jsonArray = js_data.getJSONArray("process");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Map<String, String> map = new HashMap<String, String>();
                            JSONObject jss = jsonArray.getJSONObject(i);
                            map.put("time", jss.getString("log_time"));
                            map.put("content", jss.getString("log_content"));
                            mList.add(map);
                        }
                        mlistview.setAdapter(new MyAdapter());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
//                executeOnLoadDataError(error.getMessage());
                }
            });
        } else {
            AppContext.showToast("当前无网络连接！");
        }
        return view;
    }

    class MyAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.progress_info, null);
                viewHolder = new ViewHolder();
                viewHolder.mtextview1 = (TextView) convertView.findViewById(R.id.handle_time);
                viewHolder.mtextview2 = (TextView) convertView.findViewById(R.id.audit_type);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position % 2 == 1) {
                convertView.setBackgroundColor(Color.parseColor("#C4C7AC"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#A9C3C0"));
            }
            Map<String, String> map = mList.get(position);
            viewHolder.mtextview1.setText(map.get("time"));
            viewHolder.mtextview2.setText(map.get("content"));
            return convertView;
        }

        class ViewHolder {
            TextView mtextview1;
            TextView mtextview2;
        }
    }

    private void executeOnLoadDataError(String error) {

        String message = error;
        if (TextUtils.isEmpty(error)) {
            if (TDevice.hasInternet()) {
                message = getString(R.string.tip_load_data_error);
            } else {
                message = getString(R.string.tip_network_error);
            }
        }
        AppContext.showToastShort(message);
    }
}
