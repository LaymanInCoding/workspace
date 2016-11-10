package com.witmoon.xmb.activity.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.activity.specialoffer.fragment.HotSaleFragment;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页免税Fragment
 * Created by zhyh on 2015/4/28.
 */
public class TaxExemptionFragment extends BaseFragment implements View.OnClickListener {
    private View mRootView;
    private MainActivity mMainActivity;

    private PagerSlidingTabStrip mIndicator;
    private ViewPager mViewPager;

    private View mGridLayout;
    private GridView mGridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();
        if (mRootView == null) {

            mRootView = inflater.inflate(R.layout.fragment_special_offer, container, false);

            mRootView = inflater.inflate(R.layout.fragment_special_offer, container, false);


            mGridLayout = mRootView.findViewById(R.id.grid_layout);
            mGridLayout.setOnClickListener(this);
            mGridView = (GridView) mRootView.findViewById(R.id.grid);
            mRootView.findViewById(R.id.slider).setOnClickListener(this);

            mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
            mIndicator = (PagerSlidingTabStrip) mRootView.findViewById(R.id.indicator);

            // 发送分类请求

        }

        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        return mRootView;
    }

    private Listener<JSONObject> mCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            showWaitDialog();
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort("服务器异常: " + twoTuple.second);
                return;
            }
            try {
                List<Map<String, String>> menuList = parseResponse(response);
                for (int i = 0;i < 5 ;i++)
                {
                    Map<String, String> mMap = new HashMap<>();
                    mMap.put("id",i+"");
                    mMap.put("name","name"+i);
                    menuList.add(mMap);
                }
                PagerAdapter pagerAdapter = new TaxExemptionPagerAdapter
                        (getChildFragmentManager(), menuList);
                mViewPager.setAdapter(pagerAdapter);
                mIndicator.setViewPager(mViewPager);

                SimpleAdapter gridAdapter = new SimpleAdapter(getActivity(), menuList,
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

        @Override
        public void onError(NetroidError error) {
            AppContext.showToast(error.getMessage());
        }

        @Override
        public void onFinish() {
            hideWaitDialog();
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        configToolbar();
    }

    // 解析网络响应数据
    private List<Map<String, String>> parseResponse(JSONObject respObj) throws JSONException {
        Log.e("免税区",respObj.toString());
        List<Map<String, String>> menuList = new ArrayList<>();
        JSONArray menuArray = respObj.getJSONArray("data");
        for (int i = 0; i < menuArray.length(); i++) {
            JSONObject menuObj = menuArray.getJSONObject(i);
            Map<String, String> tmap = new HashMap<>();
            tmap.put("id", menuObj.getString("menu_id"));
            tmap.put("name", menuObj.getString("menu_name"));
            menuList.add(tmap);
        }
        return menuList;
    }

    private void configToolbar() {
        Toolbar toolbar = mMainActivity.getToolBar();
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_special_offer));
        AQuery aQuery = new AQuery(mMainActivity, toolbar).gone();
        aQuery.id(R.id.toolbar_logo_img).image(R.mipmap.logo).gone();
        aQuery.id(R.id.toolbar_right_img).image(R.mipmap.icon_drawer_menu).gone().clicked(this);
        aQuery.id(R.id.toolbar_left_img).image(R.mipmap.search).gone().clicked(this);
        aQuery.id(R.id.toolbar_title_text).gone();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_left_img:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.toolbar_right_img:
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
    public class TaxExemptionPagerAdapter extends FragmentStatePagerAdapter {
        private List<Map<String, String>> mMenuList;

        public TaxExemptionPagerAdapter(FragmentManager fm, List<Map<String, String>> menuList) {
            super(fm);
            mMenuList = menuList;
        }

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public Fragment getItem(int position) {

            //请求数据ID
            String menuId = mMenuList.get(position).get("id");
            return HotSaleFragment.newInstance(menuId);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            //子视图的 title
            return mMenuList.get(position).get("name");
        }
    }
}
