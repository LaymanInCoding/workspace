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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.AddressManageActivity;
import com.witmoon.xmb.activity.me.fragment.New_BabyWebActivity;
import com.witmoon.xmb.activity.service.ServiceOrderActivity;
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
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by de on 2016/1/21
 */

public class MeFragment1 extends BaseFragment implements View.OnClickListener {

    private View mNoLoginTip;
    private View mNeedLoginLayout;
    private CircleImageView mAvatarImage;
    private TextView mAttentionNumberText;
    private LinearLayout me_is_login;
    private TextView mFansNumberText;
    private View view;
    private Boolean headimg_isload = false;
    private TextView mHotLineText;
    private TextView nameTextView;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setTitleColor_(R.color.main_kin);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_me2, container, false);
            AQuery aQuery = new AQuery(getActivity(), view);
            mNeedLoginLayout = aQuery.id(R.id.need_login_layout).getView();
            mNoLoginTip = aQuery.id(R.id.no_login_tip).clicked(this).getView();
            me_is_login = (LinearLayout) aQuery.id(R.id.me_is_login).getView();
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            findAndInitView(aQuery);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (AppContext.instance().isLogin() && !headimg_isload) {
            headimg_isload = true;
            Netroid.displayImage(AppContext.getLoginInfo().getAvatar(), mAvatarImage);
        }
    }

    private void findAndInitView(AQuery aQuery) {
        if (!AppContext.instance().isLogin()) {
            mNeedLoginLayout.setVisibility(View.GONE);
            mNoLoginTip.setVisibility(View.VISIBLE);
            me_is_login.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(AppContext.getLoginInfo().getName())) {
                aQuery.id(R.id.name).text(AppContext.getLoginInfo().getName());
            }
        }

//        mAttentionNumberText = aQuery.id(R.id.follow_number).getTextView();
        mFansNumberText = aQuery.id(R.id.fans_number).getTextView();

        aQuery.id(R.id.my_order).clicked(this);
        aQuery.id(R.id.bedtime_story).clicked(this);
        aQuery.id(R.id.ma_bu_dai).clicked(this);
        aQuery.id(R.id.my_favorite).clicked(this);
        aQuery.id(R.id.cash_coupon).clicked(this);
        aQuery.id(R.id.new_born).clicked(this);
        aQuery.id(R.id.me_browse_history).clicked(this);
        aQuery.id(R.id.me_receiver_address).clicked(this);
        aQuery.id(R.id.me_mb_help).clicked(this);
        aQuery.id(R.id.vaccine_record).clicked(this);
        aQuery.id(R.id.new_born).clicked(this);
        aQuery.id(R.id.antenatal_care).clicked(this);
        aQuery.id(R.id.baby_weight_height).clicked(this);
        aQuery.id(R.id.order_ser).clicked(this);
        aQuery.id(R.id.product_order).clicked(this);
//        aQuery.id(R.id.baby_weight_height).clicked(this);
        aQuery.id(R.id.hot_line).clicked(this);
        mHotLineText = aQuery.id(R.id.hot_line).getTextView();
        aQuery.id(R.id.me_after_sales_service).clicked(this);
        aQuery.id(R.id.me_online_service).clicked(this);

        mNeedLoginLayout.setOnClickListener(this);
        mAvatarImage = (CircleImageView) aQuery.id(R.id.me_avatar_img).getImageView();
    }

    // 配置ActionBar(Toolbar)
    private void configToolbar() {
        BaseActivity mainActivity = (BaseActivity) getActivity();
        ((MainActivity) getActivity()).setTitleColor_(R.color.main_kin);
        AQuery aQuery = new AQuery(mainActivity);
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_title_text).visible().text("请登录体验更多功能");
        if (AppContext.instance().isLogin()) {
            User user = AppContext.getLoginInfo();
            if (!TextUtils.isEmpty(user.getName())) {
                aQuery.id(R.id.toolbar_title_text).text(user.getName());
                aQuery.id(R.id.name).text(user.getName());
            } else {
                aQuery.id(R.id.toolbar_title_text).text("个人中心");
            }
        }
        aQuery.id(R.id.toolbar_logo_img).gone();
        aQuery.id(R.id.toolbar_right_img).gone();
        aQuery.id(R.id.toolbar_right_img2).gone();
        aQuery.id(R.id.toolbar_right_img1).visible().image(R.mipmap.icon_gear).clicked(this);
        aQuery.id(R.id.toolbar_left_img).gone();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.current_tab_index = 4;
        if (AppContext.instance().isLogin()) {
            headimg_isload = true;
            nameTextView.setText(AppContext.getLoginInfo().getName());
            mNoLoginTip.setVisibility(View.GONE);
            me_is_login.setVisibility(View.VISIBLE);
            mNeedLoginLayout.setVisibility(View.VISIBLE);
            Netroid.displayImage(AppContext.getLoginInfo().getAvatar(), mAvatarImage);
        }else{
            me_is_login.setVisibility(View.GONE);
            mNoLoginTip.setVisibility(View.VISIBLE);
            mNeedLoginLayout.setVisibility(View.GONE);
        }
        configToolbar();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.my_order:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                args.putSerializable("initType", null);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, args);
                break;
            case R.id.ma_bu_dai:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                UIHelper.showSimpleBackForResult(getActivity(),3, SimpleBackPage.SHOPPING_CART);
                break;
            case R.id.my_favorite:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FAVORITE);
                break;
            case R.id.cash_coupon:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.CASH_COUPON);
                break;
            case R.id.me_browse_history:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BROWSE_HISTORY);
                break;
            case R.id.me_receiver_address:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                AddressManageActivity.startActivity(getActivity());
                break;
            case R.id.me_mb_help:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.HELP);
                break;
            case R.id.no_login_tip:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.toolbar_right_img1:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SETTING);
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
            case R.id.order_ser:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.OUT_PRICE);
                break;
            case R.id.me_after_sales_service:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SERVICE_PROVISION);
                break;
            case R.id.me_online_service:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ONLINE_SERVICE);
                break;
            //疫苗记录
            case R.id.vaccine_record:
                args.putSerializable("URL", ApiHelper.BASE_URL + "/discovery/vaccine_record");
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.VACCINE, args);
                break;
            //产检
            case R.id.antenatal_care:
                args.putSerializable("URL", ApiHelper.BASE_URL + "/discovery/pregnancy_record");
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.VACCINE_, args);
                break;
            //身高体重
            case R.id.baby_weight_height:
                args.putSerializable("URL", ApiHelper.BASE_URL + "/babyInfo/inforecord");
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.WEIGHT_HEIGHT, args);
                break;
            //睡前故事
            case R.id.bedtime_story:
                args.putSerializable("URL", ApiHelper.BASE_URL + "/video/videolist");
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BEDTIME_STORY, args);
                break;
            //知识库
            case R.id.new_born:
                startActivity(new Intent(getActivity(), New_BabyWebActivity.class));
                break;
            case R.id.product_order:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
                startActivity(new Intent(this.getActivity(), ServiceOrderActivity.class));
                break;
        }
    }
}