package com.witmoon.xmb.activity.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.AddressManageActivity;
import com.witmoon.xmb.activity.me.OrderType;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.model.User;
import com.witmoon.xmb.ui.widget.CircleImageView;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 关于自己Fragment
 * Created by zhyh on 2015/4/28.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {

    private View mNoLoginTip;
    private View mNeedLoginLayout;

    private TextView mHotLineText;
    private CircleImageView mAvatarImage;
    private TextView mAttentionNumberText;
    private TextView mFansNumberText;
    private TextView mArticleNumberText;

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mNeedLoginLayout != null && !mNeedLoginLayout.isShown()) {
                mNeedLoginLayout.setVisibility(View.VISIBLE);
                mNoLoginTip.setVisibility(View.GONE);
                if(""!=AppContext.getLoginInfo().getAvatar())
                {
                    Netroid.displayImage(AppContext.getLoginInfo().getAvatar(), mAvatarImage);
                }else {
                    mAvatarImage.setImageResource(R.mipmap.avatar);
                }
                configToolbar();
            }
        }
    };

    private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mNeedLoginLayout != null && mNeedLoginLayout.getVisibility() != View.GONE) {
                mNeedLoginLayout.setVisibility(View.GONE);
                mNoLoginTip.setVisibility(View.VISIBLE);
                configToolbar();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter loginFilter = new IntentFilter(Const.INTENT_ACTION_LOGIN);
        getActivity().registerReceiver(mLoginReceiver, loginFilter);

        IntentFilter logoutFilter = new IntentFilter(Const.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mLogoutReceiver, logoutFilter);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        AQuery aQuery = new AQuery(getActivity(), view);
        mNeedLoginLayout = aQuery.id(R.id.need_login_layout).getView();
        mNoLoginTip = aQuery.id(R.id.no_login_tip).clicked(this).getView();

        findAndInitView(aQuery);
        configToolbar();
        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mLoginReceiver);
        getActivity().unregisterReceiver(mLogoutReceiver);
        super.onDestroy();
    }

    private void findAndInitView(AQuery aQuery) {
        if (!AppContext.instance().isLogin()) {
            mNeedLoginLayout.setVisibility(View.GONE);
            mNoLoginTip.setVisibility(View.VISIBLE);
        }

        mArticleNumberText = aQuery.id(R.id.post_number).getTextView();
        mAttentionNumberText = aQuery.id(R.id.attention_number).getTextView();
        mFansNumberText = aQuery.id(R.id.fans_number).getTextView();

        aQuery.id(R.id.me_item_all_order).clicked(this);
        aQuery.id(R.id.order_waiting_for_evaluate).clicked(this);
        aQuery.id(R.id.order_waiting_for_payment).clicked(this);
        aQuery.id(R.id.order_waiting_for_send).clicked(this);
        aQuery.id(R.id.order_waiting_for_receive).clicked(this);

        aQuery.id(R.id.me_item_certification).clicked(this);
        aQuery.id(R.id.me_item_cash_coupon).clicked(this);
        aQuery.id(R.id.me_item_my_favorite).clicked(this);
        aQuery.id(R.id.me_item_feedback).clicked(this);
        aQuery.id(R.id.me_item_browse_history).clicked(this);
        aQuery.id(R.id.me_item_receiver_address).clicked(this);
        aQuery.id(R.id.order_waiting_out_price).clicked(this);
        aQuery.id(R.id.me_item_evaluate).clicked(this);
        aQuery.id(R.id.me_item_help).clicked(this);

        aQuery.id(R.id.hot_line).clicked(this);
        mHotLineText = aQuery.id(R.id.hot_line).getTextView();
        aQuery.id(R.id.me_after_sales_service).clicked(this);
        aQuery.id(R.id.me_online_service).clicked(this);


        mAvatarImage = (CircleImageView) aQuery.id(R.id.me_avatar_img).getImageView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(AppContext.instance().isLogin()){
            Netroid.displayImage(AppContext.getLoginInfo().getAvatar(), mAvatarImage);
        }
    }

    // 配置ActionBar(Toolbar)
    private void configToolbar() {
        BaseActivity mainActivity = (BaseActivity) getActivity();
        Toolbar toolbar = mainActivity.getToolBar();
        toolbar.setBackgroundColor(mainActivity.getResources().getColor(R.color.master_me));
        ((MainActivity) getActivity()).setTitleColor_(R.color.master_me);
        AQuery aQuery = new AQuery(mainActivity);
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_title_text).visible().text("请登录体验更多功能");
        if (AppContext.instance().isLogin()) {
            User user = AppContext.getLoginInfo();
            if (!TextUtils.isEmpty(user.getName())) {
                aQuery.id(R.id.toolbar_title_text).text(user.getName());
            } else {
                aQuery.id(R.id.toolbar_title_text).text("个人中心");
            }
        }
        aQuery.id(R.id.toolbar_logo_img).gone();
        aQuery.id(R.id.toolbar_right_img).visible().image(R.mipmap.icon_gear).clicked(this);
        aQuery.id(R.id.toolbar_left_img).gone();
    }

    @Override
    public void onClick(View view) {
        Bundle args = new Bundle();
        switch (view.getId()) {
            case R.id.no_login_tip:
//                UIHelper.showSimpleBackForResult(getActivity(), LoginFragment.LOGIN_REQ_CODE,
//                        SimpleBackPage.LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.me_item_all_order:
                args.putSerializable("initType", null);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, args);
                break;
            case R.id.order_waiting_for_payment:
                args.putSerializable("initType", OrderType.TYPE_WAITING_FOR_PAYMENT);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, args);
                break;
            case R.id.order_waiting_for_send:
                args.putSerializable("initType", OrderType.TYPE_WAITING_FOR_SENDING);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, args);
                break;
            case R.id.order_waiting_for_receive:
                args.putSerializable("initType", OrderType.TYPE_WAITING_FOR_RECEIVING);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, args);
                break;
            case R.id.order_waiting_for_evaluate:
                args.putSerializable("initType", OrderType.TYPE_FINISHED);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, args);
                break;
            case R.id.me_item_my_favorite:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FAVORITE);
                break;
            case R.id.me_item_cash_coupon:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.CASH_COUPON);
                break;
            case R.id.me_item_browse_history:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BROWSE_HISTORY);
                break;
            // 收货地址管理
            case R.id.me_item_receiver_address:
                AddressManageActivity.startActivity(getActivity());
                break;
            case R.id.hot_line:
                new AlertDialog.Builder(getActivity()).setMessage("确定要拨打热线电话吗?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                        mHotLineText.getText()));
                                startActivity(intent);
                            }
                        }).setCancelable(true).show();
                break;
            case R.id.me_after_sales_service:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.AFTER_SALE_SERVICE);
                break;
            case R.id.me_online_service:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ONLINE_SERVICE);
                break;
            // 用户反馈
            case R.id.me_item_feedback:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FEEDBACK);
                break;
            case R.id.me_item_evaluate:
                TDevice.openAppInMarket(getActivity());
                break;
            case R.id.me_item_help:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.HELP);
                break;
            case R.id.me_item_certification:
                if (AppContext.getLoginInfo().getIdentity_card().length()==18)
                {
                    AppContext.showToast("您的账号已认证！");
                    break;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.CERTIFICATION);
                break;
            // Toolbar右侧设置按钮被点击
            case R.id.order_waiting_out_price:
                //跳转
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.OUT_PRICE);
                break;
            case R.id.toolbar_right_img:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SETTING);
                break;


        }
    }
}
