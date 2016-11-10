package com.witmoon.xmb.activity.friendship.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.activity.friendship.adapter.ArticleAdapter;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.Article;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注的人发表的帖子
 * Created by zhyh on 2015/6/13.
 */
public class AttentionArticleFragment extends BaseRecyclerViewFragmentV2 {

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mLoginReceiver, new IntentFilter(Const.INTENT_ACTION_LOGIN));
        getActivity().registerReceiver(mLoginReceiver, new IntentFilter(Const
                .INTENT_ACTION_LOGOUT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mLoginReceiver);
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        ArticleAdapter childrenExperienceAdapter = new ArticleAdapter();

        childrenExperienceAdapter.setOnPraiseClickListener(new ArticleAdapter
                .OnPraiseClickListener() {
            @Override
            public void onPraiseClick(String id, boolean b) {

            }
        });

        childrenExperienceAdapter.setOnCommentClickListener(new ArticleAdapter
                .OnCommentClickListener() {
            @Override
            public void onCommentClick(String id) {
                AppContext.showToast("评论" + id);
            }
        });

        return childrenExperienceAdapter;
    }

    @Override
    protected void requestData() {
        if (!AppContext.instance().isLogin()) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
            mErrorLayout.setErrorMessage("请登录查看关注人的帖子");
            mErrorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
            return;
        }
        FriendshipApi.attentionArticleList(mCurrentPage, "2", getDefaultListener());
    }

    @Override
    protected ListEntity parseResponse(JSONObject responseObj) throws Exception {
        JSONArray postArray = responseObj.getJSONArray("data");
        final List<Article> articles = new ArrayList<>();
        for (int i = 0; i < postArray.length(); i++) {
            Article article = Article.parse(postArray.getJSONObject(i));
            articles.add(article);
        }
        final boolean isMore = responseObj.getJSONObject("paginated").getInt("more") != 0;
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return articles;
            }

            @Override
            public boolean hasMoreData() {
                return isMore;
            }
        };
    }
}
