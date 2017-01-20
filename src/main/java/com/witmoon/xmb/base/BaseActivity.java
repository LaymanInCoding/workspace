package com.witmoon.xmb.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.R;
import com.witmoon.xmb.ui.dialog.DialogControl;
import com.witmoon.xmb.ui.dialog.WaitingDialog;
import com.witmoon.xmb.util.SystemBarTintManager;

import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Activity基类
 * Created by zhyh on 2015/6/15.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener, DialogControl {

    private boolean _isVisible;
    private WaitingDialog _waitDialog;

    public Toolbar mToolBar;
    private TextView mTitleText;
    private TextView mDelete;
    protected EndlessRecyclerOnScrollListener recyclerViewScrollListener;
    protected RecyclerView mRootView;
    protected LinearLayoutManager layoutManager;
    protected HeaderViewRecyclerAdapter stringAdapter;

    // 退出应用广播接收器

    private BroadcastReceiver mExistReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (!hasActionBar()) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        onBeforeSetContentLayout();

        // 设置Activity布局资源
        if (getLayoutResourceId() != Integer.MIN_VALUE) {
            setContentView(getLayoutResourceId());
        }

        mToolBar = (Toolbar) findViewById(R.id.top_toolbar);
        if (hasActionBar()) {
            initActionBar(mToolBar);
        }

        // 调用Activity初始化方法, 由子类实现初始化自身
        initialize(savedInstanceState);

        // 注册广播接收器
        IntentFilter filter = new IntentFilter(Const.INTENT_ACTION_EXIT_APP);
        registerReceiver(mExistReceiver, filter);
    }

    @Override
    protected void onPause() {
        _isVisible = false;
        hideWaitDialog();
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        _isVisible = true;
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mExistReceiver);
//        mExistReceiver = null;
        super.onDestroy();
    }

    protected void initialize(Bundle savedInstanceState) {
        if (hasActionBar()) {
            initActionBar(mToolBar);
        }
    }

    /**
     * 初始化ActionBar（Toolbar）
     *
     * @param toolbar Toolbar
     */
    private void initActionBar(Toolbar toolbar) {
        if (toolbar == null) return;
        setSupportActionBar(toolbar);

        ImageView leftBtn = (ImageView) toolbar.findViewById(R.id.toolbar_left_img);
        if (!isActionbarHasBackButton()) {
            leftBtn.setVisibility(View.GONE);
        }
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitleText = (TextView) toolbar.findViewById(R.id.toolbar_title_text);
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        mTitleText.setTypeface(tf);
        mDelete = (TextView) toolbar.findViewById(R.id.toolbar_right_text);
        if (getActionBarTitleByResId() != Integer.MIN_VALUE) {
            mTitleText.setText(getString(getActionBarTitleByResId()));
        } else {
            mTitleText.setText(getActionBarTitle());
        }

        configActionBar(toolbar);
    }

    /**
     * 配置ActionBar, 以Toolbar实现
     *
     * @param toolbar Toolbar实例
     */
    protected void configActionBar(Toolbar toolbar) {
    }

    protected String getActionBarTitle() {
        return getString(R.string.app_name);
    }

    protected int getActionBarTitleByResId() {
        return Integer.MIN_VALUE;
    }

    /**
     * 设置Actionbar标题
     *
     * @param title 标题内容
     */
    public void setToolBarTitle(int title) {
        mTitleText.setText(title);
    }

    public TextView setmDeleteText(String str) {
        mDelete.setVisibility(View.VISIBLE);
        mDelete.setText(str);
        return mDelete;
    }

    // 设置Actionbar标题
    public void setToolBarTitle(String title) {
        mTitleText.setText(title);
    }

    /**
     * 设置Toolbar背景色
     *
     * @param colorId 颜色ID
     */
    public void setToolBarBackground(int colorId) {
        mToolBar.setBackgroundColor(getResources().getColor(colorId));
    }

    protected boolean isActionbarHasBackButton() {
        return true;
    }

    protected void onBeforeSetContentLayout() {
    }

    protected int getLayoutResourceId() {
        return Integer.MIN_VALUE;
    }

    protected boolean hasActionBar() {
        return true;
    }

    public Toolbar getToolBar() {
        return mToolBar;

    }

    public void hideToolbar() {
        if (mToolBar != null) {
            mToolBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public WaitingDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    @Override
    public WaitingDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public WaitingDialog showWaitDialog(String text) {
        if (_isVisible) {
            if (_waitDialog == null) {
//                _waitDialog = new WaitingDialog(this, R.style.dialog_waiting);
                _waitDialog = new WaitingDialog(this);
            }
            _waitDialog.setMessage(text);
            _waitDialog.show();
            return _waitDialog;
        }
        return null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return BaseActivity.super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (null != v && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setTitleColor_(int mColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            if (mColor == R.color.black) {
                tintManager.setStatusBarTintColor(getResources().getColor(R.color.black));
            } else {
                tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.bg_status));
            }
        }
    }

    //获取高度
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    protected void resetStatus() {
        mRootView.removeOnScrollListener(recyclerViewScrollListener);
        recyclerViewScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                setRecRequest(currentPage);
            }
        };
        mRootView.addOnScrollListener(recyclerViewScrollListener);
    }

    public void setRecRequest(int currentPage) {

    }

    protected void createMsgHeaderView() {
        removeFooterView();
        View loadMoreView = LayoutInflater
                .from(this)
                .inflate(R.layout.view_no_message, mRootView, false);
        stringAdapter.addFooterView(loadMoreView);
    }

    protected void createLoadMoreView() {
        removeFooterView();
        View loadMoreView = LayoutInflater
                .from(this)
                .inflate(R.layout.view_load_more, mRootView, false);
        stringAdapter.addFooterView(loadMoreView);
    }

    protected void removeFooterView() {
        stringAdapter.removeFooterView();
        mRootView.removeOnScrollListener(recyclerViewScrollListener);
    }

    protected void removeHeaderView() {
        stringAdapter.removeHeaderView();
    }

}





