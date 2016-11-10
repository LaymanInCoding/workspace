package com.witmoon.xmb.activity.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.activity.specialoffer.Baby_DetailsActivity;
import com.witmoon.xmb.activity.specialoffer.fragment.HotSaleFragment;
import com.witmoon.xmb.activity.specialoffer.fragment.NewArrivalFragmentV2;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页特卖Fragment
 * Created by zhyh on 2015/4/28.
 */
public class SpecialOfferFragment extends BaseFragment {

    private JSONObject ntjson = new JSONObject();
    private View mRootView;
    private MainActivity mMainActivity;
    private View mGridLayout;
    private GridView mGridView;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mIndicator;
    private SimpleAdapter gridAdapter;
    //替换页面 --!
    private EmptyLayout mEmptyLayout;
    private int mStoreEmptyState = -1;
    private String mStoreEmptyMessage;
    private List<Map<String, String>> menuList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
                ((MainActivity) getActivity()).setTitleColor_(R.color.main_kin);
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_special_offer, container, false);
            mEmptyLayout = (EmptyLayout) mRootView.findViewById(R.id.error_layout);
            mMainActivity = (MainActivity) getActivity();
            mGridLayout = mRootView.findViewById(R.id.grid_layout);
            mGridLayout.setVisibility(View.GONE);
            mGridLayout.setOnClickListener(this);
            mGridView = (GridView) mRootView.findViewById(R.id.grid);
            mRootView.findViewById(R.id.slider).setOnClickListener(this);
            mRootView.findViewById(R.id.toolbar_left_img1).setOnClickListener(this);
            mRootView.findViewById(R.id.toolbar_right_img1).setOnClickListener(this);
            mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
            mIndicator = (PagerSlidingTabStrip) mRootView.findViewById(R.id.indicator);
            //显示加载等待页面加载
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            HomeApi.functionClass("1", mFunCallback);
            //首页--- 小能轨迹
//            try {
//                ntjson.put("ttl", "首页");// 当前访问页面标题
//                ntjson.put("url", "www.xiaomabao.com"); // 当前页url
//                ntjson.put("isvip", "0"); // 0 非 1 是
//                ntjson.put("userlevel", "1"); // 用户等级，分为1-5级
//                ntjson.put("orderid", ""); // 订单号
//                ntjson.put("orderprice", ""); // 订单价格
//                ntjson.put("ref", ""); // 当前访问页面前一页URL地址
//                ntjson.put("ntalkerparam", ""); // 商品信息、购物车商品JSON字符串数据
//            } catch (Exception e) {
//                Log.e("轨迹", "首页轨迹异常" + e.toString());
//            } // 必传
//            int trackback1 = Ntalker.getInstance().startAction(getActivity(),
//                    ntjson);
//            if (0 != trackback1) {
//                Log.e("轨迹log", trackback1 + "");
//            }
        }
        if (null == mEmptyLayout) {
            mEmptyLayout = (EmptyLayout) mRootView.findViewById(R.id.error_layout);
        }
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeApi.functionClass("1", mFunCallback);
            }
        });
        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        if (mStoreEmptyState != -1) {
            mEmptyLayout.setErrorType(mStoreEmptyState);
        }
        if (!TextUtils.isEmpty(mStoreEmptyMessage)) {
            mEmptyLayout.setErrorMessage(mStoreEmptyMessage);
        }
        return mRootView;
    }

    //
    private Listener<JSONObject> mFunCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (twoTuple.first) {
                try {
                    menuList = parseResponse(response);
                    PagerAdapter pagerAdapter = new SpecialOfferPagerAdapter
                            (getChildFragmentManager(), menuList);
                    mViewPager.setAdapter(pagerAdapter);
                    mIndicator.setViewPager(mViewPager);

                    gridAdapter = new SimpleAdapter(getActivity(), menuList,
                            R.layout.layout_grid_text_view, new String[]{"name"}, new
                            int[]{R.id.text});
                    mGridView.setAdapter(gridAdapter);
                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {
                            mViewPager.setCurrentItem(position, true);
                            mGridLayout.setVisibility(View.GONE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hideWaitDialog();
        }

        @Override
        public void onFinish() {
            if (menuList.size() > 0) {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
            super.onFinish();
        }

        @Override
        public void onError(NetroidError error) {
            if (menuList.size() == 0) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            } else {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                String message = error.toString();
                if (TextUtils.isEmpty(error.toString())) {
                    if (TDevice.hasInternet()) {
                        message = getString(R.string.tip_load_data_error);
                    } else {
                        message = getString(R.string.tip_network_error);
                    }
                }
                AppContext.showToastShort(message);
            }
        }
    };

    // 解析网络响应数据
    private List<Map<String, String>> parseResponse(JSONObject respObj) throws JSONException {
        Log.e("全部信息", respObj.toString());
        List<Map<String, String>> menuList = new ArrayList<>();
        JSONArray menuArray = respObj.getJSONArray("data");
        for (int i = 0; i < menuArray.length(); i++) {
            JSONObject menuObj = menuArray.getJSONObject(i);
            Map<String, String> tmap = new HashMap<>();
            tmap.put("id", menuObj.getString("menu_id"));
            tmap.put("name", menuObj.getString("menu_name"));
            menuList.add(tmap);
        }
        Map<String, String> homeMap = new HashMap<>();
        homeMap.put("name", "首页");
        menuList.add(0, homeMap);
        return menuList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configToolbar();
    }

    private void configToolbar() {
        Toolbar toolbar = mMainActivity.getToolBar();

        AQuery aQuery = new AQuery(mMainActivity, toolbar);
        aQuery.id(R.id.top_toolbar).gone();
        aQuery.id(R.id.toolbar_logo_img1).image(R.mipmap.logo).visible();
        aQuery.id(R.id.toolbar_right_img1).image(R.mipmap.icon_drawer_menu).visible().clicked(this);
        aQuery.id(R.id.toolbar_left_img1).image(R.mipmap.search).visible().clicked(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_left_img1:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.toolbar_right_img1:
                mMainActivity.openRightDrawer();
                break;
            case R.id.slider:
                mGridLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.grid_layout:
                mGridLayout.setVisibility(View.GONE);
                break;
        }
    }

    // 适配器定义
    public class SpecialOfferPagerAdapter extends FragmentPagerAdapter {
        List<Map<String, String>> mMenuList;

        public SpecialOfferPagerAdapter(FragmentManager fm, List<Map<String, String>> menuList) {
            super(fm);
            mMenuList = menuList;
        }

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new NewArrivalFragmentV2();
            }
            Map<String, String> menu = mMenuList.get(position);
            return HotSaleFragment.newInstance(menu.get("id"));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mMenuList.get(position).get("name");
        }
    }

    @Override
    public void onDestroy() {
        if (null != mEmptyLayout) {
            mStoreEmptyState = mEmptyLayout.getErrorState();
            mStoreEmptyMessage = mEmptyLayout.getMessage();
        }
        super.onDestroy();
    }
}
