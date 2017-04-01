package com.witmoon.xmb.activity.me.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.adapter.InviteCouponAdapter;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.InviteCoupon;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by ming on 2017/3/30.
 */
public class InviteFriendsFragment extends BaseFragment {
    private View view;
    private View headerView;
    private View noCouponContainer;
    private EmptyLayout mEmptyLayout;
    private int page = 1;
    private InviteCouponAdapter mAdapter;
    private PopupWindow cancelWindow;
    private ArrayList<InviteCoupon> mList = new ArrayList<>();
    private HashMap<String, String> share_info = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_invite_friend, container, false);
            mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
            mRootView = (RecyclerView) view.findViewById(R.id.recyclerView);
            mAdapter = new InviteCouponAdapter(getContext(), mList);
            layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            stringAdapter = new HeaderViewRecyclerAdapter(mAdapter);
            mRootView.setLayoutManager(layoutManager);
            mRootView.setHasFixedSize(true);
//            headerView = LayoutInflater
//                    .from(this.getContext())
//                    .inflate(R.layout.header_invite_fragment, mRootView, false);
            headerView = inflater.inflate(R.layout.header_invite_fragment, mRootView, false);
            View share_container = headerView.findViewById(R.id.share_wechat);
            View share_circle_container = headerView.findViewById(R.id.share_wecircle);
            View share_qq = headerView.findViewById(R.id.share_qq);
            View share_qzone = headerView.findViewById(R.id.share_qqzone);
            share_container.setOnClickListener(this);
            share_circle_container.setOnClickListener(this);
            share_qq.setOnClickListener(this);
            share_qzone.setOnClickListener(this);
            stringAdapter.addHeaderView(headerView);
            mRootView.setAdapter(stringAdapter);
            noCouponContainer = headerView.findViewById(R.id.no_coupon_container);
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecRequest(1);
                }
            });
            setRecRequest(1);
            setListener();
        }
        return view;
    }

    private Listener<JSONObject> listener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            if (page == 1) {
                mList.clear();
            }
            Logger.json(response.toString());

            JSONArray orderArray = null;
            JSONObject shareObject = null;
            try {
                shareObject = response.getJSONObject("share");
                share_info.put("url", shareObject.getString("url"));
                share_info.put("title", shareObject.getString("title"));
                share_info.put("desc", shareObject.getString("desc"));
                orderArray = response.getJSONArray("friends");
                if (page == 1 && orderArray.length() == 0) {
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    return;
                }
                noCouponContainer.setVisibility(View.GONE);
                for (int i = 0; i < orderArray.length(); i++) {
                    InviteCoupon coupon = InviteCoupon.parse(orderArray.getJSONObject(i));
                    mList.add(coupon);
                }
                if (orderArray.length() < 20) {
                    if (page != 1) {
                        removeFooterView();
                    }
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.footer_invite_coupon, mRootView, false);
                    stringAdapter.addFooterView(view);
                } else {
                    createLoadMoreView();
                    resetStatus();
                }
                stringAdapter.notifyDataSetChanged();
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                page += 1;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setListener() {
        View ruleView = headerView.findViewById(R.id.rules_detail);
        ruleView.setOnClickListener(this);
    }

    @Override
    public void setRecRequest(int currentPage) {
        page = currentPage;
        UserApi.get_invite_coupon(page, listener);
    }

    private void showCancelWindow() {
        View rootView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
        View popView = LayoutInflater.from(getContext()).inflate(R.layout.invite_cancel_window, null);
        cancelWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        TextView textView = (TextView) popView.findViewById(R.id.bottom_text);
        TextView textView1 = (TextView) popView.findViewById(R.id.top_text);
        AssetManager mgr = getContext().getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        textView.setTypeface(tf);
        textView1.setTypeface(tf);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelWindow.dismiss();
            }
        });
        cancelWindow.setBackgroundDrawable(new BitmapDrawable());
        cancelWindow.setTouchable(true);
        cancelWindow.setOutsideTouchable(false);
        cancelWindow.showAsDropDown(popView);
        cancelWindow.setAnimationStyle(R.style.anim_pop_show);
    }

    @Override
    public boolean onBackPressed() {
        if (null != cancelWindow && cancelWindow.isShowing()) {
            cancelWindow.dismiss();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rules_detail:
                UIHelper.showSimpleBack(getContext(), SimpleBackPage.InviteRule);
                break;
            case R.id.share_wechat:
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(share_info.get("title"));
                sp.setText(share_info.get("desc"));
                sp.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp.setUrl(share_info.get("url"));
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Logger.d("cancel_wechat");
                        showCancelWindow();
                    }
                }); // 设置分享事件回调
                wechat.share(sp);
                break;
            case R.id.share_wecircle:
                Platform.ShareParams sp_circle = new Platform.ShareParams();
                sp_circle.setShareType(Platform.SHARE_WEBPAGE);
                sp_circle.setTitle(share_info.get("title"));
                sp_circle.setText(share_info.get("desc"));
                sp_circle.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp_circle.setUrl(share_info.get("url"));
                Platform wecircle = ShareSDK.getPlatform(WechatMoments.NAME);
                wecircle.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Logger.d("cancel_circle");
                        showCancelWindow();
                    }
                }); // 设置分享事件回调
                wecircle.share(sp_circle);
                break;
            case R.id.share_qq:
                Platform.ShareParams sp_qq = new Platform.ShareParams();
                sp_qq.setTitle(share_info.get("title"));
                sp_qq.setText(share_info.get("desc"));
                sp_qq.setTitleUrl(share_info.get("url"));
                sp_qq.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp_qq.setSite("小麻包");
                sp_qq.setSiteUrl("http://www.xiaomabao.com");
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Logger.d("cancel_qq");
                        showCancelWindow();
                    }
                }); // 设置分享事件回调
                qq.share(sp_qq);
                break;
            case R.id.share_qqzone:
                Platform.ShareParams sp_qzone = new Platform.ShareParams();
                sp_qzone.setTitle(share_info.get("title"));
                if (share_info.get("desc").length() > 50) {
                    sp_qzone.setText(share_info.get("desc").substring(0, 50));
                } else {
                    sp_qzone.setText(share_info.get("desc"));
                }
                sp_qzone.setTitleUrl(share_info.get("url"));
                sp_qzone.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp_qzone.setSite("小麻包");
                sp_qzone.setSiteUrl("http://www.xiaomabao.com");
                Platform qzone = ShareSDK.getPlatform(QZone.NAME);
                qzone.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Logger.d("cancel");
                        showCancelWindow();
                    }
                }); // 设置分享事件回调
                qzone.share(sp_qzone);
                break;
            default:
                break;
        }
    }
}
