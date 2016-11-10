package com.witmoon.xmb.activity.friendship.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.friendship.adapter.CommentAdapterV2;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.WebImagePagerAdapter;
import com.witmoon.xmblibrary.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子评论Fragment
 * Created by zhyh on 2015/8/4.
 */
public class CommentFragment extends BaseFragment {
    public static final String KEY_ARTICLE_ID = "article_id";
    private String mArticleId;
    private ScrollView mScrollView;
    private ViewPager mViewPager;
    private TextView mTitleText;
    private TextView mContentText;
    private EditText mMessageEdit;
    private TextView mAuthorText;
    private TextView mPraizeNumText;
    private TextView mCommentNumText;
    private TextView mTimeText;

    private Handler mHandler = new Handler();

    List<Map<String, String>> commentList = new ArrayList<>();
    private CommentAdapterV2 mRecyclerArrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mArticleId = bundle.getString(KEY_ARTICLE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_comment, container, false);

        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mTitleText = (TextView) view.findViewById(R.id.title);
        mContentText = (TextView) view.findViewById(R.id.content);
        mMessageEdit = (EditText) view.findViewById(R.id.comment);
        mAuthorText = (TextView) view.findViewById(R.id.author);
        mPraizeNumText = (TextView) view.findViewById(R.id.praise_number);
        mCommentNumText = (TextView) view.findViewById(R.id.comment_number);
        mTimeText = (TextView) view.findViewById(R.id.time);
        TextView sendBtn = (TextView) view.findViewById(R.id.submit_button);
        sendBtn.setOnClickListener(this);

        LinearListView linearListView = (LinearListView) view.findViewById(R.id.recycle_view);
        mRecyclerArrayAdapter = new CommentAdapterV2(commentList);
        linearListView.setLinearAdapter(mRecyclerArrayAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadData();
    }

    private void loadData() {
        FriendshipApi.articleDetail(mArticleId, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                if (!twoTuple.first) {
                    AppContext.showToast(twoTuple.second);
                    return;
                }
                try {
                    JSONObject dataObj = response.getJSONObject("data");
                    mViewPager.setAdapter(new WebImagePagerAdapter(getActivity(), dataObj
                            .getString("imgs").split(",")));
                    mTitleText.setText(dataObj.getString("title"));
                    mContentText.setText(dataObj.getString("content"));
                    mAuthorText.setText(dataObj.getString("nick_name"));
                    mPraizeNumText.setText(dataObj.getString("praise_num"));
                    mCommentNumText.setText(dataObj.getString("comment_num"));
                    mTimeText.setText(DateFormat.format("yyyy.MM.dd HH:mm", Long.parseLong(dataObj
                            .getString("publish_time")) * 1000));
                    parseComments(dataObj.getJSONArray("comments"));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 解析评论
    private void parseComments(JSONArray comments) throws JSONException {
        for (int i = 0; i < comments.length(); i++) {
            JSONObject commentObj = comments.getJSONObject(i);
            Map<String, String> tmap = new HashMap<>();
            tmap.put("user_id", commentObj.getString("user_id"));
            tmap.put("author", commentObj.getString("nick_name"));
            tmap.put("content", commentObj.getString("comment_content"));
            tmap.put("level", commentObj.getString("user_rank"));
            tmap.put("avatar", commentObj.getString("header_img"));

            commentList.add(tmap);
        }
        mRecyclerArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_button) {
            String content = mMessageEdit.getText().toString();
            if (TextUtils.isEmpty(content)) {
                AppContext.showToast("评论内容不能为空");
                return;
            }
            FriendshipApi.comment(mArticleId, content, mCommentCallback);
        }
    }

    // 发表评论回调接口
    private Listener<JSONObject> mCommentCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToast(twoTuple.second);
                return;
            }
            AppContext.showToast("发表成功");
        }
    };
}
