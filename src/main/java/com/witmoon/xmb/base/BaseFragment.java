package com.witmoon.xmb.base;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.ui.dialog.DialogControl;
import com.witmoon.xmb.ui.dialog.WaitingDialog;

import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * 项目Fragment基类
 * Created by zhyh on 2015/6/15.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {
    protected static final int STATE_NONE = 0;
    protected static final int STATE_REFRESH = 1;
    protected static final int STATE_LOAD_MORE = 2;
    protected int mState = STATE_NONE;
    protected EndlessRecyclerOnScrollListener recyclerViewScrollListener;
    protected RecyclerView mRootView;
    protected LinearLayoutManager layoutManager;
    protected HeaderViewRecyclerAdapter stringAdapter;
    public MainActivity mainActivity;

    // 配置Toolbar
    public void hideToolBar() {
        mainActivity = (MainActivity) getActivity();
        Toolbar toolbar = mainActivity.getToolBar();
        AQuery aQuery = new AQuery(mainActivity, toolbar);
        aQuery.id(R.id.top_toolbar).gone();
    }
    //显示加载中的对话框
    // 隐藏等待对话框
    protected void hideWaitDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            ((DialogControl) activity).hideWaitDialog();
        }
    }

    public void setRecRequest(int currentPage){

    }

    public void showProgressing(int res_id,View view){
        view.findViewById(res_id).setVisibility(View.VISIBLE);
    }

    public void hideProgressing(int res_id,View view){

        view.findViewById(res_id).setVisibility(View.GONE);
    }

    protected void resetStatus(){
        mRootView.removeOnScrollListener(recyclerViewScrollListener);
        recyclerViewScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                setRecRequest(currentPage);
            }
        };
        mRootView.addOnScrollListener(recyclerViewScrollListener);
    }

    protected void createMsgHeaderView(){
        removeFooterView();
        View loadMoreView = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.view_no_message, mRootView, false);
        stringAdapter.addHeaderView(loadMoreView);
    }

    protected void createLoadMoreView() {
        removeFooterView();
        View loadMoreView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_load_more, mRootView, false);
        stringAdapter.addFooterView(loadMoreView);
    }

    protected void removeFooterView(){
        stringAdapter.removeFooterView();
        mRootView.removeOnScrollListener(recyclerViewScrollListener);
    }

    protected void removeHeaderView(HeaderViewRecyclerAdapter stringAdapter){
        stringAdapter.removeHeaderView();
    }

    // 显示等待对话框
    protected WaitingDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    // 显示等待对话框
    protected WaitingDialog showWaitDialog(int resid) {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            return ((DialogControl) activity).showWaitDialog(resid);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
    }

    public boolean onBackPressed() {
        return false;
    }

    public void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getActivity().getWindow().setAttributes(lp);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getActivity().getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().setAttributes(attr);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
