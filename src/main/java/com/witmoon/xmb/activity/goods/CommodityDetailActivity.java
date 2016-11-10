package com.witmoon.xmb.activity.goods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.unicall.androidsdk.UnicallController;
import com.unicall.androidsdk.UnicallDelegate;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.fragment.EvaluateFragment;
import com.witmoon.xmb.activity.goods.fragment.IntroduceFragment;
import com.witmoon.xmb.activity.goods.fragment.SpecificationFragment;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.Goods;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.BadgeView;
import com.witmoon.xmb.ui.widget.CountDownTextView2;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.WebImagePagerAdapter;
import com.xiaoneng.menu.Ntalker;
import com.xmb.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 商品详情界面
 * Created by zhyh on 2015/6/1.
 */
public class CommodityDetailActivity extends BaseActivity implements View.OnClickListener, UnicallDelegate {
    private AQuery mAQuery;
    UnicallController unicall;
    private String Unicall_AppKey = "985DC5D9-E6A2-4B82-8673-404B47CC4A19";
    private String Unicall_TenantId = "xiaomabao.yunkefu.com";
    private JSONObject ntjson = new JSONObject();
    private JSONObject param1 = new JSONObject();
    private JSONObject ntalkerparam = new JSONObject();
    private ImageView mToolbarShoppingCartImage;
    private BadgeView mCartCountBadgeView;
    private TextView mCollectBtnText;
    private ViewPager mPhotoViewPager;
    private ViewPager mInternalViewPager;
    private PagerSlidingTabStrip mPagerIndicator;
    private Fragment[] mFragments = new Fragment[3];
    private String[] titles = {"商品介绍", "规格参数", "麻麻口碑"};
    private String mGoodsId;
    private String mActionId;   // 专场id, 可以为空
    private Goods mGoods;

    public static void start(Context context, String id) {
        start(context, id, "");
    }

    private JSONObject XN_Goods_adress;
    String erpparam = "";

    // 客服
    String sellerid = "";// 商户id,平台版企业(B2B2C企业)使用此参数，B2C企业此参数传""
    String settingid = "kf_9761_1432534158571";// 客服组id
    String groupName = "小麻包客服";// 客服组名称

//    // 商品展示（专有参数）
//    String appgoodsinfo_type = "1";
//    String clientgoodsinfo_type = "1";
//    String goods_id = "50094233";
//    String goods_name = "2015年最新潮流时尚T恤偶像同款一二线城市包邮 速度抢购有超级大礼包等你来拿";
//    String goods_price = "￥：188";
//    String goods_image = "http://img.meicicdn.com/p/51/050010/h-050010-1.jpg";
//    String goods_url = "www.baidu.com";
//    String goods_showurl = "http://pic.shopex.cn/pictures/goodsdetail/29b.jpg?rnd=111111";

    //替换页面---    加载  无连接  无数据
    private EmptyLayout mEmptyLayout;

    public static void start(Context context, String id, String actionId) {
        Intent intent = new Intent(context, CommodityDetailActivity.class);
        intent.putExtra("GOODS_ID", id);
        intent.putExtra("ACTION_ID", actionId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_commodity_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "商品详情";
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        mToolbarShoppingCartImage = (ImageView) toolbar.findViewById(R.id.toolbar_shopping_cart);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.master_shopping_cart);
        Intent intent = getIntent();
        mGoodsId = intent.getStringExtra("GOODS_ID");
        mActionId = intent.getStringExtra("ACTION_ID");
        mAQuery = new AQuery(this);
        initView();
        GoodsApi.goodsDetail(mGoodsId, mActionId, detailCallback);
        this.unicall = new UnicallController(this, Unicall_AppKey, Unicall_TenantId);
        JSONObject tmp = new JSONObject();
        try {
            if (AppContext.instance().isLogin()) {
                tmp.put("nickname", AppContext.getLoginInfo().getName());
            }else{
                tmp.put("nickname", "未登录用户");
            }
        } catch (Exception e) {

        }
        unicall.UnicallUpdateUserInfo(tmp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoodsApi.cartGoodsCount(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                if (tt.first) {
                    try {
                        String count = response.getJSONObject("data").getString("list_count");
                        if (!TextUtils.isEmpty(count) && !"0".equals(count)) {
                            if (mCartCountBadgeView == null) {
                                mCartCountBadgeView = new BadgeView(CommodityDetailActivity.this,
                                        mToolbarShoppingCartImage);
                                mCartCountBadgeView.setText(count);
                                mCartCountBadgeView.setBadgeBackgroundColor(Color.WHITE);
                                mCartCountBadgeView.setTextSize(8);
                                mCartCountBadgeView.setBadgeMargin(-2, 0);
                                mCartCountBadgeView.setTextColor(getResources().getColor(R.color
                                        .master_shopping_cart));
                                mCartCountBadgeView.show();
                                return;
                            }
                            mCartCountBadgeView.setText(count);
                        }
                    } catch (JSONException ignored) {
                    }
                }
            }
        });
    }

    // 商品详情回调
    private Listener<JSONObject> detailCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                finish();
                return;
            }
            try {
                mGoods = Goods.parse(response.getJSONObject("data"));
                onNetworkLoadSuccess(mGoods);
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            } catch (JSONException e) {
                finish();
                AppContext.showToastShort("解析商品详情出现异常");
            }
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            if (mGoods==null) {
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

    // 网络加载完成
    private void onNetworkLoadSuccess(Goods goods) {
        // 商品展示图片

        mPhotoViewPager.setAdapter(new WebImagePagerAdapter(CommodityDetailActivity.this,
                goods.getGallery()));
        CirclePageIndicator indicator = (CirclePageIndicator) mAQuery.id(R.id
                .pager_indicator).getView();
        indicator.setViewPager(mPhotoViewPager);
        // 商品名称价格等基本信息
        mAQuery.id(R.id.goods_name).text(goods.getName());
        mAQuery.id(R.id.goods_price).text(goods.getShopPriceDesc());
        mAQuery.id(R.id.market_price).text(goods.getMarketPriceDesc()).getTextView().getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mAQuery.id(R.id.tag_discount).text(goods.getDiscount() + "折");
        mAQuery.id(R.id.tag_promote).visibility(goods.isPromote() ? View.VISIBLE : View.GONE);
        mAQuery.id(R.id.tag_no_postage).visibility(goods.isNoPostage() ? View.VISIBLE : View.GONE);
        mAQuery.id(R.id.goods_preferential).text("优惠信息：" + goods.getPreferentialInfo());
        mAQuery.id(R.id.goods_freight).text("运费：" + goods.getGoodsFreight() + "元");
        mAQuery.id(R.id.goods_brief).text(goods.getBrief());
        mAQuery.id(R.id.goods_inventory).text(String.format("%d件", goods.getInventory()));
        mAQuery.id(R.id.sale_count).text(String.format("%s件", goods.getSalesSum()));
        if (goods.getActiveRemainderTime() != null) {
            ((CountDownTextView2) mAQuery.id(R.id.goods_remaining_time).visibility(View.VISIBLE)
                    .getView()).setTime(goods.getActiveRemainderTime() * 1000);
        }

        if (goods.isCollected()) {
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_heart_red_empty_48x48);
            mCollectBtnText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }

        // 初始化顶部Tab页
        mFragments[0] = IntroduceFragment.newInstance(goods.getDetailGallery());
        mFragments[1] = SpecificationFragment.newInstance(goods.getId());
        mFragments[2] = EvaluateFragment.newInstance(goods.getId());
        mInternalViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        mPagerIndicator.setViewPager(mInternalViewPager);
    }

    private void initView() {
        mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mAQuery.id(R.id.toolbar_share).clicked(this);
        mAQuery.id(R.id.toolbar_shopping_cart).clicked(this);
        mAQuery.id(R.id.buy_immediately_btn).clicked(this);
        mAQuery.id(R.id.add_to_cart_btn).clicked(this);
        mAQuery.id(R.id.specification_selection_text).clicked(this);
        mCollectBtnText = mAQuery.id(R.id.collect_button).clicked(this).getTextView();

        // 初始化商品照片PagerView
        mPhotoViewPager = (ViewPager) mAQuery.id(R.id.view_pager).getView();

        mInternalViewPager = (ViewPager) mAQuery.id(R.id.id_stickynavlayout_viewpager).getView();
        mPagerIndicator = (PagerSlidingTabStrip) findViewById(R.id.id_stickynavlayout_indicator);
        mAQuery.id(R.id.xn_customer_service).clicked(this);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsApi.goodsDetail(mGoodsId, mActionId, detailCallback);
            }
        });
    }

    // 收藏商品响应接口
    private Listener<JSONObject> mCollectCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_heart_red_empty_48x48);
            mCollectBtnText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            AppContext.showToastShort("收藏成功");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_to_cart_btn:
                SpecificationSelectionActivity.startActivity(this, SpecificationSelectionActivity
                        .ADD_TO_CART, mGoodsId);
                break;
            case R.id.specification_selection_text:
                SpecificationSelectionActivity.startActivity(this, SpecificationSelectionActivity
                        .SPECIFICATION_SELECTION, mGoodsId);
                break;
            case R.id.buy_immediately_btn:
                SpecificationSelectionActivity.startActivity(this, SpecificationSelectionActivity
                        .BUY_IMMEDIATELY, mGoodsId);
                break;
            case R.id.collect_button:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(this, LoginActivity.class));
                    return;
                }
                UserApi.collectGoods(mGoodsId, mCollectCallback);
                break;
            case R.id.toolbar_share:
                showShare();
                break;
            case R.id.toolbar_shopping_cart:
                UIHelper.showSimpleBack(this, SimpleBackPage.SHOPPING_CART);
                break;
            case R.id.xn_customer_service:
                unicall.UnicallShowView();
                break;
        }
    }

    private void showShare() {
        if (mGoods == null) {
            AppContext.showToastShort("商品沒有正确加载");
            return;
        }
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setShareFromQQAuthSupport(false);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享自小麻包母婴商城");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(ApiHelper.getGoodsShareLinkUrl(mGoodsId));
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mGoods.getName());

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        if (mGoods.getGallery().length > 0) {
            oks.setImageUrl(mGoods.getGallery()[0]);
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ApiHelper.getGoodsShareLinkUrl(mGoodsId));
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.xiaomabao.com");
        oks.setSilent(true);
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void messageArrived(JSONObject jsonObject) {

    }

    @Override
    public void messageCountUpdated(int i) {

    }

    @Override
    public void acquireValidation() {
        //JSONObject signatureData = new JSONObject(result);
        JSONObject userInfo = new JSONObject();
        try {
            if (AppContext.instance().isLogin()) {
                userInfo.put("nickname", AppContext.getLoginInfo().getName());
            }else{
                userInfo.put("nickname", "未登录用户");
            }
            unicall.UnicallUpdateUserInfo(userInfo);
            //unicall.UnicallUpdateValidation(signatureData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
