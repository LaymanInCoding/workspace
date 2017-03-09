package com.witmoon.xmb.activity.goods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.adapter.GoodsCommentAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.EvaluateBean;
import com.witmoon.xmb.model.ImageInfo;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2017/3/2.
 */
public class GoogsCommentFragment extends BaseFragment {

    private List<EvaluateBean> mDataList;
    private String mGoodsId;
    private int mPage = 1;
    private GoodsCommentAdapter mAdapter;
    private View headerView;
    private EmptyLayout mEmptyLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mGoodsId = bundle.getString("GOODS_ID");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods_comment, null);
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        mEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecRequest(1);
            }
        });
        mRootView = (RecyclerView) view.findViewById(R.id.recycler);
        mDataList = new ArrayList<>();
        mAdapter = new GoodsCommentAdapter(getContext(), mDataList);
        headerView = inflater.inflate(R.layout.header_goods_evaluate, null);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setLayoutManager(layoutManager);
        mRootView.setHasFixedSize(true);
        stringAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        stringAdapter.addHeaderView(headerView);
        mRootView.setAdapter(stringAdapter);
        setRecRequest(1);
        return view;
    }

    @Override
    public void setRecRequest(int currentPage) {
        Logger.t("page").d(currentPage);
        GoodsApi.goodsComments(mGoodsId, currentPage, mCommentCallback);
    }

    // 网络回调接口
    private Listener<JSONObject> mCommentCallback = new Listener<JSONObject>() {

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (tt.first) {
                try {
                    if (mPage == 1) {
                        mDataList.clear();
                    }
                    JSONObject dataObj = response.getJSONObject("data");
                    JSONArray commentArray = dataObj.getJSONArray("comments_list");
                    for (int i = 0; i < commentArray.length(); i++) {
                        JSONObject commentObj = commentArray.getJSONObject(i);
                        EvaluateBean mEvaluateBean = new EvaluateBean();
                        JSONArray imgArray = commentObj.getJSONArray("img_path");
                        if (imgArray != null && imgArray.length() == 0) {
                            ArrayList<ImageInfo> imageInfos = new ArrayList<>();
                            for (int j = 0; j < imgArray.length(); j++) {
                                ImageInfo info = new ImageInfo();
                                info.url = imgArray.getString(j);
                                info.width = 0;
                                info.height = 0;
                                imageInfos.add(info);
                            }
                            mEvaluateBean.setInfo(imageInfos);
                        }
                        mEvaluateBean.setAuthor(commentObj.getString("author"));
                        mEvaluateBean.setContent(commentObj.getString("content"));
                        mEvaluateBean.setmRating(Double.parseDouble(commentObj.getString("comment_rank")));
                        mDataList.add(mEvaluateBean);
                    }
                    if (mPage == 1 && commentArray.length() != 0) {
                        stringAdapter.removeFooterView();
                        ((TextView) headerView.findViewById(R.id.comment_number))
                                .setText("评价晒单（" + dataObj.getString("comment_num") + "人评论）");
                        ((TextView) headerView.findViewById(R.id.good_reputation_rate))
                                .setText(dataObj.getString("good_comment_rate"));
                        headerView.findViewById(R.id.no_comment_tv).setVisibility(View.GONE);
                    }
                    if (commentArray.length() < 20) {
                        if (mPage != 1) {
                            removeFooterView();
                        }
                    } else {
                        createLoadMoreView();
                        resetStatus();
                    }
                    mPage++;
                    mAdapter.notifyDataSetChanged();
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
