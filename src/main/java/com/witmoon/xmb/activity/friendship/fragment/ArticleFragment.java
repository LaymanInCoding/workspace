package com.witmoon.xmb.activity.friendship.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.friendship.adapter.ArticleAdapter;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.model.Article;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 育儿心经
 * Created by zhyh on 2015/6/13.
 */
public class ArticleFragment extends BaseRecyclerViewFragmentV2 {

    private String mType;

    public static ArticleFragment newInstance(String type) {
        ArticleFragment articleFragment = new ArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", type);
        articleFragment.setArguments(bundle);
        return articleFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mType = bundle.getString("TYPE");
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        ArticleAdapter articleAdapter = new ArticleAdapter();

        // 设置关注作者监听器
        articleAdapter.setOnFocusOnClickListener(new ArticleAdapter.OnFocusOnClickListener() {
            @Override
            public void onFocusOnClick(String uid) {
                FriendshipApi.focusOnOrNot(uid, mFocusOnCallback);
            }
        });

        // 设置点赞监听器
        articleAdapter.setOnPraiseClickListener(new ArticleAdapter
                .OnPraiseClickListener() {
            @Override
            public void onPraiseClick(String id, boolean b) {
                FriendshipApi.praiseOrNot(id, b, mPraiseCallback);
            }
        });

        // 评论监听器
        articleAdapter.setOnCommentClickListener(new ArticleAdapter
                .OnCommentClickListener() {
            @Override
            public void onCommentClick(String id) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(CommentFragment.KEY_ARTICLE_ID, id);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ARTICLE_COMMENT, bundle);
            }
        });

        // 分享监听器
        articleAdapter.setOnShareListener(new ArticleAdapter.OnShareListener() {
            @Override
            public void onShare() {
                share();
            }
        });

        return articleAdapter;
    }

    private void share() {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.xiaomabao.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("分享自小麻包母婴商城的文本信息");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.xiaomabao.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.xiaomabao.com");
        oks.setSilent(true);

        // 启动分享GUI
        oks.show(getActivity());
    }

    @Override
    protected void requestData() {
        FriendshipApi.articleList(mCurrentPage, mType, getDefaultListener());
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

    // ---------------------------- 网络请求回调接口 --------------------------

    // 点赞或取消点赞回调接口
    private Listener<JSONObject> mPraiseCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
            }
        }
    };

    // 关注回调接口
    private Listener<JSONObject> mFocusOnCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToast(tt.second);
                return;
            }
        }
    };
}
