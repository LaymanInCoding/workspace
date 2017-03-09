package com.witmoon.xmb.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.fragment.Group_Buying_Fragment;
import com.witmoon.xmb.activity.me.fragment.CashCouponFragment;
import com.witmoon.xmb.activity.me.fragment.ReturnofthegoodsFragment;
import com.witmoon.xmb.activity.me.fragment.WebUtilFragment;
import com.witmoon.xmb.activity.me.fragment.WebUtilFragments;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 只有后退操作的Activity
 * Created by zhyh on 2015/6/19.
 */
public class SimpleBackActivity extends BaseActivity {

    public final static String BUNDLE_KEY_PAGE = "BUNDLE_KEY_PAGE";
    public final static String BUNDLE_KEY_ARGS = "BUNDLE_KEY_ARGS";
    private static final String TAG = "FLAG_TAG";
    private WeakReference<Fragment> mFragment;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.v2_activity_simple_fragment;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        Intent data = getIntent();
        if (data == null) {
            throw new RuntimeException("you must provide a page info to display");
        }

        int pageValue = data.getIntExtra(BUNDLE_KEY_PAGE, 0);
        SimpleBackPage page = SimpleBackPage.getPageByValue(pageValue);
        if (page == null) {
            throw new IllegalArgumentException("can not find page by value:" + pageValue);
        }

        // 设置标题和标题栏背景
        setToolBarTitle(page.getTitle());
        if (page.getToolbarBgColor() != Integer.MIN_VALUE) {
            setToolBarBackground(page.getToolbarBgColor());
            //判断是否是该屏蔽的
//            if (page.getClz()!=Group_Buying_Fragment.class)
            setTitleColor_(page.getToolbarBgColor());

        }

        try {
            Fragment fragment = (Fragment) page.getClz().newInstance();
            // 如果参数不为空, 向Fragment传递参数
            Bundle args = data.getBundleExtra(BUNDLE_KEY_ARGS);
            if (args != null) {
                fragment.setArguments(args);
            }

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, fragment, TAG);
            trans.commitAllowingStateLoss();

            mFragment = new WeakReference<>(fragment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("generate fragment error. by value:" + pageValue);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null && mFragment.get() != null
                && mFragment.get() instanceof BaseFragment) {
            if(mFragment.get() instanceof WebUtilFragments){
                WebUtilFragments bf = (WebUtilFragments) mFragment.get();
                if (!bf.onBackPressed()) {
                    super.onBackPressed();
                }
                return;
            }
            if(mFragment.get() instanceof WebUtilFragment){
                WebUtilFragment bf = (WebUtilFragment) mFragment.get();
                if (!bf.onBackPressed()) {
                    super.onBackPressed();
                }
                return;
            }
            BaseFragment bf = (BaseFragment) mFragment.get();
            if (!bf.onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(mFragment.get() instanceof ReturnofthegoodsFragment){
//           return ReturnofthegoodsFragment.onKeyDown(keyCode, event, SimpleBackActivity.this);
//        }
        return super.onKeyDown(keyCode, event);
    }
}
