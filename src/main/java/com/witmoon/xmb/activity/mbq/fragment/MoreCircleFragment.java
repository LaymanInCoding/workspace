package com.witmoon.xmb.activity.mbq.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.R;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.activity.mbq.activity.CircleActivity;
import com.witmoon.xmb.activity.mbq.activity.SearchCircle;
import com.witmoon.xmb.activity.mbq.adapter.CircleAdapter;
import com.witmoon.xmb.activity.mbq.adapter.CircleCategoryAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoreCircleFragment extends BaseFragment implements  CircleCategoryAdapter.OnItemClickListener{
    private View view,search_circleView;
    private RecyclerView circleCategoryRecycleView,circleRecycleView;
    private CircleCategoryAdapter circleCategoryAdapter;
    private CircleAdapter circleAdapter;
    private ArrayList<String> circleCategoryList = new ArrayList<>();
    private ArrayList<ArrayList<CircleCategory>> circleList = new ArrayList<>();
    private ArrayList<CircleCategory> circleCurrent = new ArrayList<>();
    private EmptyLayout emptyLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            IntentFilter refresh = new IntentFilter(Const.INTENT_ACTION_REFRESH_MY_MBQ_SEARCH);
            getActivity().registerReceiver(refreshSearch, refresh);
            view = inflater.inflate(R.layout.fragment_more_circle, container, false);
            search_circleView = view.findViewById(R.id.search_circle);
            search_circleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UmengStatic.registStat(getActivity(),"MoreCircle0");

                    startActivity(new Intent(getActivity(), SearchCircle.class));
                    getActivity().overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
                }
            });
            circleCategoryRecycleView = (RecyclerView) view.findViewById(R.id.circle_category_recycle);
            circleRecycleView = (RecyclerView) view.findViewById(R.id.circle_recycle);
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(this.getContext());
            layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(this.getContext());
            layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
            circleCategoryRecycleView.setHasFixedSize(true);
            circleCategoryRecycleView.setLayoutManager(layoutManager1);
            circleRecycleView.setHasFixedSize(true);
            circleRecycleView.setLayoutManager(layoutManager2);
            circleCategoryAdapter = new CircleCategoryAdapter(circleCategoryList,this.getContext());
            circleCategoryAdapter.setOnItemClickListener(this);
            circleCategoryRecycleView.setAdapter(circleCategoryAdapter);
            circleAdapter = new CircleAdapter(circleCurrent,this.getContext());
            circleAdapter.setOnItemClickListener(new CircleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(CircleCategory circleCategory) {
                    UmengStatic.registStat(getActivity(),"MoreCircle2");

                    Intent intent = new Intent(getActivity(), CircleActivity.class);
                    intent.putExtra("circle_id", circleCategory.getCircle_id());
                    intent.putExtra("circle_logo",circleCategory.getCircle_logo());
                    intent.putExtra("circle_name",circleCategory.getCircle_name());
                    intent.putExtra("circle_post_cnt",circleCategory.getCircle_post_cnt() + "个话题");
                    intent.putExtra("circle_is_join",circleCategory.getUser_is_join());
                    startActivity(intent);
                }

                @Override
                public void onItemButtonClick(int circle_id) {
                    UmengStatic.registStat(getActivity(),"MoreCircle3");

                    XmbUtils.joinCircle(getActivity(), circle_id, new Listener<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                if (response.getInt("status") == 1) {

                                    if(XmbUtils.popupWindow != null && XmbUtils.popupWindow.isShowing()) {
                                        XmbUtils.popupWindow.dismiss();
                                    }
                                    XmbUtils.showMessage(getActivity(), response.getString("info"));
                                    Intent intent = new Intent(Const.INTENT_ACTION_REFRESH_MY_MBQ);
                                    getActivity().sendBroadcast(intent);
                                } else {
                                    XmbUtils.showMessage(getActivity(), response.getString("info"));
                                }
                            } catch (JSONException e) {
                                XmbUtils.showMessage(getActivity(), "网络异常，请稍后重试！");
                            }
                        }
                    });
                }
            });
            circleRecycleView.setAdapter(circleAdapter);
            emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestData();
                }
            });
            requestData();
        }else{
            circleAdapter.notifyDataSetChanged();
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }


    private BroadcastReceiver refreshSearch = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            circleAdapter.notifyDataSetChanged();
        }
    };

    public void requestData(){
        CircleApi.get_all_cat(new Listener<JSONObject>() {
            @Override
            public void onPreExecute() {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        ArrayList<CircleCategory> arrayList = new ArrayList<>();
                        circleCategoryList.add(jsonArray.getJSONObject(i).getString("cat_name"));
                        JSONArray catJsonArray = jsonArray.getJSONObject(i).getJSONArray("child_cats");
                        for(int j = 0;j<catJsonArray.length();j++){
                            arrayList.add(CircleCategory.parse(catJsonArray.getJSONObject(j)));
                        }
                        circleList.add(arrayList);
                    }
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    circleCategoryAdapter.notifyDataSetChanged();
                    circleCurrent.addAll(circleList.get(0));
                    circleAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }

    @Override
    public void onItemButtonClick(int position) {
        UmengStatic.registStat(getActivity(),"MoreCircle1");

        circleCategoryAdapter.notifyDataSetChanged();
        circleCurrent.clear();
        circleCurrent.addAll(circleList.get(position));
        circleAdapter.notifyDataSetChanged();
        circleRecycleView.scrollToPosition(0);
    }
}
