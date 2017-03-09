package com.witmoon.xmb.activity.goods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.adapter.GoodsEvaluationAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.EvaluateBean;
import com.witmoon.xmb.model.ImageInfo;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价Fragment（麻妈口碑）
 * Created by zhyh on 2015/6/9.
 */
public class EvaluateFragment extends BaseFragment {
    private int mPageNo = 1;
    private TextView mCommentNumberText;
    private TextView mGoodReputationRateText;
    private TextView mSeeAllCommentsBtn;
    private GoodsEvaluationAdapter adapter;
    private ScrollView mScrollView;
    private boolean is_show;
    private boolean hasMore;
    private String mGoodsId;
    private List<EvaluateBean> mDataList;
    public ListView mListView;
    private boolean isLoad = false;

    public static EvaluateFragment newInstance(String id) {
        EvaluateFragment fragment = new EvaluateFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GOODS_ID", id);
        fragment.setArguments(bundle);
        Logger.e("eee");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mGoodsId = bundle.getString("GOODS_ID");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_goods_evaluate, container, false);
        mCommentNumberText = (TextView) view.findViewById(R.id.comment_number);
        mGoodReputationRateText = (TextView) view.findViewById(R.id.good_reputation_rate);
        mSeeAllCommentsBtn = (TextView) view.findViewById(R.id.submit_button);
        mSeeAllCommentsBtn.setOnClickListener(this);
        mListView = (ListView) view.findViewById(R.id.linearlistview);
        mScrollView = (ScrollView) view.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mScrollView.setOnTouchListener(new TouchListenerImpl());
        return view;
    }

    private class TouchListenerImpl implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:
                    int scrollY = view.getScrollY();
                    int height = view.getHeight();
                    int scrollViewMeasuredHeight = mScrollView.getChildAt(0).getMeasuredHeight();
                    if ((scrollY + height) == scrollViewMeasuredHeight && adapter.getCount() > 0 && is_show) {
                        if (!hasMore) {
                            AppContext.showToastShort("没有更多评论");
                        } else {
                            if (!isLoad){
                                isLoad = true;
                                mPageNo++;
                                GoodsApi.goodsComments(mGoodsId, mPageNo, mCommentCallback);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        GoodsApi.goodsComments(mGoodsId, mPageNo, mCommentCallback);
    }

    // 网络回调接口
    private Listener<JSONObject> mCommentCallback = new Listener<JSONObject>() {

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (tt.first) {
                try {
                    JSONObject dataObj = response.getJSONObject("data");
                    parseAndUpdateUI(dataObj);
                    hasMore = response.getJSONObject("paginated").getInt("more") != 0;
                    if (hasMore) {
                        mSeeAllCommentsBtn.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isLoad = false;
        }
    };

    private void parseAndUpdateUI(JSONObject dataObj) throws JSONException {
        String number = dataObj.getString("comment_num");
        if ("0".equals(number)) {
            mSeeAllCommentsBtn.setText("暂时还没有评价呦");
            return;
        }

        String rate = dataObj.getString("good_comment_rate");
        mGoodReputationRateText.setText(rate);
        mCommentNumberText.setText("评价晒单（"+number+"人评论）");
        JSONArray commentArray = dataObj.getJSONArray("comments_list");
        if (mPageNo == 1){
            mDataList = new ArrayList<>();
        }
        for (int i = 0; i < commentArray.length(); i++) {
            JSONObject commentObj = commentArray.getJSONObject(i);
            EvaluateBean mEvaluateBean = new EvaluateBean();
            JSONArray imgArray = commentObj.getJSONArray("img_path");
            if (imgArray != null && imgArray.length() == 0) {
                ArrayList<ImageInfo> imageInfos = new ArrayList<>();
                for (int j = 0; j < imgArray.length()   ; j++) {
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
//            }
            mDataList.add(mEvaluateBean);
        }
        if (mDataList.size() <= 3 && mPageNo == 1) {
            mSeeAllCommentsBtn.setVisibility(View.GONE);
        }
        adapter = new GoodsEvaluationAdapter(getActivity(), mDataList,this);
        mListView.setAdapter(adapter);
        if (mPageNo > 1){
            adapter.showAllComments();
        }
        UIutil.setListViewHeightBasedOnChildren(mListView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_button) {
            if (null != adapter) {
                if (adapter.getCount() > 0) {
                    adapter.showAllComments();
                    is_show = true;
                    UIutil.setListViewHeightBasedOnChildren(mListView);
                    mSeeAllCommentsBtn.setVisibility(View.GONE);
                }
            }
        }
    }
}
