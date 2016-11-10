package com.witmoon.xmb.activity.friendship;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.friendship.adapter.PersonalHomePageAlbumAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmblibrary.observablescrollview.ObservableRecyclerView;
import com.witmoon.xmblibrary.observablescrollview.ObservableScrollViewCallbacks;
import com.witmoon.xmblibrary.observablescrollview.ScrollState;
import com.witmoon.xmblibrary.recyclerview.OnRcvScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人主页
 * Created by zhyh on 2015/5/29.
 */
public class PersonHomePageActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private AQuery mAQuery;

    private TextView mActionbarTitle;
    private ViewGroup mTopLayout;
    private ViewGroup mHeaderLayout;

    private ImageView mToggleLeft;
    private ImageView mToggleRight;

    private ObservableRecyclerView mRecyclerView;
    private List<Map<String, String>> mDataList = new ArrayList<>();
    private PersonalHomePageAlbumAdapter mGridAdapter;
    private PersonalHomePageAlbumAdapter mListAdapter;

    private String mUserId;
    private int page = 1;

    public static void startActivity(Context context, String userId) {
        Intent intent = new Intent(context, PersonHomePageActivity.class);
        intent.putExtra("USER_ID", userId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home_page;
    }

    @Override
    protected void onBeforeSetContentLayout() {

        Intent intent = getIntent();
        if (intent.hasExtra("USER_ID")) {
            mUserId = intent.getStringExtra("USER_ID");
        }
        if (mUserId == null && AppContext.instance().isLogin()) {
            mUserId = String.valueOf(AppContext.getLoginUid());
        }
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        mAQuery = new AQuery(this);

        configToolbar();
        initView();

        FriendshipApi.personalArticleList(mUserId, page, mCallback);
    }
    // 回调接口
    private Listener<JSONObject> mCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToast(tt.second);
                return;
            }
            try {
                JSONArray articleArray = response.getJSONArray("data");
                for (int i = 0; i < articleArray.length(); i++) {
                    JSONObject article = articleArray.getJSONObject(i);
                    Map<String, String> dm = new HashMap<>();
                    dm.put("id", article.getString("post_id"));
                    dm.put("praise_num", article.getString("praise_num"));
                    dm.put("comment_num", article.getString("comment_num"));
                    dm.put("publish_time", article.getString("publish_time"));
                    dm.put("url", article.getString("img"));
                    dm.put("title", article.getString("title"));
                    mDataList.add(dm);
                }
                mGridAdapter.notifyDataSetChanged();
                mListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void initView() {
        Netroid.displayImage("http://e.hiphotos.baidu" +
                ".com/image/pic/item/7acb0a46f21fbe09376011ee69600c338744ad0a.jpg", mAQuery.id(R
                .id.background_image).getImageView());
        Netroid.displayImage(AppContext.getLoginInfo().getAvatar(), mAQuery.id(R.id.avatar_img)
                .getImageView());

        mTopLayout = (ViewGroup) mAQuery.id(R.id.top_layout).getView();
        mHeaderLayout = (ViewGroup) mAQuery.id(R.id.header_layout).getView();
        mToggleLeft = mAQuery.id(R.id.toggle_left).clicked(this).getImageView();
        mToggleRight = mAQuery.id(R.id.toggle_right).clicked(this).getImageView();

        mRecyclerView = (ObservableRecyclerView) mAQuery.id(R.id.recycler_view).getView();
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.addOnScrollListener(mOnRcvScrollListener);
        mGridAdapter = new PersonalHomePageAlbumAdapter(this, mDataList, R.layout
                .item_home_page_grid_album, mTopLayout, 2);
        mListAdapter = new PersonalHomePageAlbumAdapter(this, mDataList, R.layout
                .item_home_page_list_album, mTopLayout);
        mRecyclerView.setAdapter(mGridAdapter);
    }

    private OnRcvScrollListener mOnRcvScrollListener = new OnRcvScrollListener() {
        @Override
        public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int
                maxLastVisiblePosition) {
            page++;
            FriendshipApi.personalArticleList(mUserId, page, mCallback);
        }
    };

    // 顶部Toolbar
    private Toolbar mToolbar;

    protected void configToolbar() {
        mToolbar = (Toolbar) mAQuery.id(R.id.top_toolbar).getView();
        mToolbar.setBackgroundColor(getResources().getColor(R.color.semitransparent_dark));
        mActionbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title_text);
        mActionbarTitle.setText(AppContext.getLoginInfo().getName());
        mToolbar.findViewById(R.id.toolbar_left_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonHomePageActivity.this.finish();
            }
        });
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    // ------------------- ObservableRecyclerView滚动回调 -----------------------
    int h;

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (h == 0) {
            h = mHeaderLayout.getHeight();
        }
        ViewPropertyAnimator.animate(mTopLayout).cancel();
        ViewHelper.setTranslationY(mTopLayout, Math.max(-scrollY, -h));
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
    // ------------------- ObservableRecyclerView滚动回调 END -----------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_left:
                mToggleRight.setBackgroundDrawable(null);
                mToggleLeft.setBackgroundResource(R.drawable.bg_rounded_light_blue);

                mOnRcvScrollListener.setLayoutManagerType(OnRcvScrollListener
                        .LAYOUT_MANAGER_TYPE.GRID);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                mRecyclerView.setAdapter(mGridAdapter);
                break;
            case R.id.toggle_right:
                mToggleLeft.setBackgroundDrawable(null);
                mToggleRight.setBackgroundResource(R.drawable.bg_rounded_light_blue);

                mOnRcvScrollListener.setLayoutManagerType(OnRcvScrollListener
                        .LAYOUT_MANAGER_TYPE.LINEAR);
                mRecyclerView .setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(mListAdapter);
                break;
        }
    }
}
