package com.witmoon.xmb.activity.mbq.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.adapter.HotPostAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.circle.CirclePost;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class HotPostFragment extends BaseFragment {
    private View view;
    private ArrayList<CirclePost> mDatas = new ArrayList<>();
    private EmptyLayout emptyLayout;
    private int page = 1;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hot_post, container, false);
            mRootView = (RecyclerView) view.findViewById(R.id.recycle_view);
            layoutManager = new LinearLayoutManager(this.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setColorSchemeResources(
                    R.color.main_green, R.color.main_gray, R.color
                            .main_black, R.color.main_purple
            );
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    setRecRequest(1);
                }
            });
            HotPostAdapter adapter = new HotPostAdapter(mDatas,this.getContext());
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            mRootView.setAdapter(stringAdapter);

            emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            emptyLayout.setOnLayoutClickListener(v -> setRecRequest(page));
            setRecRequest(page);
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    public void setRecRequest(int page0){

        CircleApi.get_circle_hot(page, new Listener<JSONObject>() {

            @Override
            public void onPreExecute() {
                if(page == 1 && !swipeRefreshLayout.isRefreshing()){
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }

            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(page == 1){
                        mDatas.clear();
                    }
                    JSONArray responseData = response.getJSONArray("data");
                    for(int i = 0; i < responseData.length(); i++){
                        CirclePost post = CirclePost.parse(responseData.getJSONObject(i));
                        mDatas.add(post);
                    }

                    if(responseData.length() < 20){
                        removeFooterView();
                    }else{
                        createLoadMoreView();
                        resetStatus();
                    }
                    if (responseData.length() == 0 && page == 1){
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    }else{
                        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    }
                    page += 1;
                    stringAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
