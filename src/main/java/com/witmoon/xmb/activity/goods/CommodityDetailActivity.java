package com.witmoon.xmb.activity.goods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.unicall.androidsdk.UnicallController;
import com.unicall.androidsdk.UnicallDelegate;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.fragment.IntroduceFragment;
import com.witmoon.xmb.activity.goods.fragment.SpecificationFragment;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.Goods;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.BadgeView;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;
import com.witmoon.xmb.ui.widget.LineFeedHorizontalLayout;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.DensityUtils;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.WebImagePagerAdapter;
import com.witmoon.xmb.util.XmbUtils;
import com.xmb.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品详情界面
 * Created by zhyh on 2015/6/1.
 */
public class CommodityDetailActivity extends BaseActivity implements View.OnClickListener, UnicallDelegate, RadioGroup.OnCheckedChangeListener {
    private AQuery mAQuery;
    UnicallController unicall;
    private String Unicall_AppKey = "985DC5D9-E6A2-4B82-8673-404B47CC4A19";
    private String Unicall_TenantId = "xiaomabao.yunkefu.com";
    private JSONObject ntjson = new JSONObject();
    private JSONObject param1 = new JSONObject();
    private JSONObject ntalkerparam = new JSONObject();
    private ImageView mToolbarShoppingCartImage;
    private BadgeView mCartCountBadgeView;
    private ImageView mCollectImg;
    private ViewPager mPhotoViewPager;
    private ViewPager mInternalViewPager;
    private PagerSlidingTabStrip mPagerIndicator;
    private Fragment[] mFragments = new Fragment[2];
    private String[] titles = {"商品介绍", "规格参数"};
    private String mGoodsId;
    private String mActionId;   // 专场id, 可以为空
    private Goods mGoods;
    private Context mContext;
    private boolean is_guige;//是否有规格属性
    private String mGoodsCount = "";//库存量
    private String mGoodsTitle = "";
    private String mGoodsThumbUrl = "";//规格商品图片url；
    private String mGoodsPrice = "";//规格商品价格
    private LinearLayout mSpecificationContainer;
    private List<RadioGroup> mRadioGroups = new ArrayList<>();
    private CountDownTimer mCountDownTimer;
    private IncreaseReduceTextView mIncreaseReduceTextView;
    private TextView mGoodsPriceText;
    private TextView mGoodsTitleText;//规格标题
    private TextView mGoodsInventoryText;//规格库存
    private ImageView mGoodImageView;//规格商品图片
    private Button mGoodConfirmBtn;//规格确定按钮
    private String price = "";//商品售价
    private PopupWindow specificationWindow;
    private DecimalFormat mDecimalFormat = new DecimalFormat("##0.00");
    private View popView;
    private int width;
    private int height;
    private boolean is_collect = false;

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
    private HashMap<String, String> share_info = new HashMap<>();


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
        mCollectImg = (ImageView) toolbar.findViewById(R.id.collect_button);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        setTitleColor_(R.color.master_shopping_cart);
        mContext = this;
        Intent intent = getIntent();
        mGoodsId = intent.getStringExtra("GOODS_ID");
        mActionId = intent.getStringExtra("ACTION_ID");
        mAQuery = new AQuery(this);
        initView();
        initPopView();
        GoodsApi.goodsDetail(mGoodsId, mActionId, detailCallback);
        GoodsApi.goodsSpecification(mGoodsId, mSpecificationCallback);

        this.unicall = new UnicallController(this, Unicall_AppKey, Unicall_TenantId);
        JSONObject tmp = new JSONObject();
        try {
            if (AppContext.instance().isLogin()) {
                tmp.put("nickname", AppContext.getLoginInfo().getName());
            } else {
                tmp.put("nickname", "未登录用户");
            }
        } catch (Exception e) {

        }
        unicall.UnicallUpdateUserInfo(tmp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("onResume");
//        Subscription subscription = RxBus.getDefault().toObservable(CartRefreshCd.class)
//                .compose(RxUtil.rxSchedulerHelper())
//                .subscribe(new Action1<CartRefreshCd>() {
//                    @Override
//                    public void call(CartRefreshCd cd) {
//                        if (cd.getIsRefresh()) {
//                            GoodsApi.cartGoodsCount(mCartNumCallback);
//                        }
//                    }
//                });
//        addSubscribe(subscription);
        GoodsApi.cartGoodsCount(mCartNumCallback);
    }

    //商品规格回调接口
    private Listener mCartNumCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (tt.first) {
                try {
                    String count = response.getJSONObject("data").getString("list_count");
                    if (!TextUtils.isEmpty(count) && !"0".equals(count)) {
                        if (mCartCountBadgeView == null) {
                            mCartCountBadgeView = new BadgeView(CommodityDetailActivity.this,
                                    mToolbarShoppingCartImage);
                            mCartCountBadgeView.setText(count);
                            mCartCountBadgeView.setTextColor(Color.WHITE);
                            mCartCountBadgeView.setBadgeBackgroundColor(Color.parseColor("#D66263"));
                            mCartCountBadgeView.setTextSize(8);
                            mCartCountBadgeView.setBadgeMargin(0, 0);
                            mCartCountBadgeView.show();
                            return;
                        }
                        mCartCountBadgeView.setVisibility(View.VISIBLE);
                        mCartCountBadgeView.setText(count);
                    } else {
                        if (mCartCountBadgeView != null) {
                            mCartCountBadgeView.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException ignored) {
                }
            }
        }
    };
    // 商品规格回调接口
    private Listener<JSONObject> mSpecificationCallback = new Listener<JSONObject>() {

        @Override
        public void onError(NetroidError error) {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToast(twoTuple.second);
                return;
            }
            try {
                JSONObject dataObj = response.getJSONObject("data");
                mGoodsTitle = dataObj.getString("goods_name");
                mGoodsCount = dataObj.getString("goods_number");
                mGoodsThumbUrl = dataObj.getString("goods_thumb");
                mGoodsPrice = dataObj.getString("shop_price_formatted");
                setPopView();
                parseResponseAndUpdateUI(dataObj.getJSONArray("goods_specs"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void parseResponseAndUpdateUI(JSONArray specArray) throws JSONException {
        Log.e("specArray", specArray.toString());
        for (int i = 0; i < specArray.length(); i++) {
            JSONObject specObj = specArray.getJSONObject(i);
            Map<String, Float> innerMap = new HashMap<>();
            TextView specNameText = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = DensityUtils.dp2px(this, 10);
            layoutParams.topMargin = DensityUtils.dp2px(this, 12);
            specNameText.setLayoutParams(layoutParams);
            specNameText.setText(specObj.getString("attr_name"));
            mSpecificationContainer.addView(specNameText);

            LineFeedHorizontalLayout specTagGroup = new LineFeedHorizontalLayout(this);
            specTagGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            specTagGroup.setTag(specObj.getString("attr_id"));
            specTagGroup.setHorizontalSpace(DensityUtils.dp2px(this, 12));
            specTagGroup.setVerticalSpace(DensityUtils.dp2px(this, 6));
            specTagGroup.setOnCheckedChangeListener(this);
            mSpecificationContainer.addView(specTagGroup);
            mRadioGroups.add(specTagGroup);
            createSpecificationAttrTag(specObj.getJSONArray("goods_attr_list"), innerMap,
                    specTagGroup);
        }
    }

    private void createSpecificationAttrTag(JSONArray specAttrArray, Map<String, Float>
            attrPriceMap, RadioGroup tagGroup) throws JSONException {
        for (int i = 0; i < specAttrArray.length(); i++) {
            is_guige = true;
            JSONObject to = specAttrArray.getJSONObject(i);
            attrPriceMap.put(to.getString("goods_attr_id"), Float.parseFloat(to.getString
                    ("goods_attr_price")));

            final RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //动态设置边距
            radioButton.setPadding(width / 30, width / 60, width / 30, width / 60);
            radioButton.setText(to.getString("goods_attr_name"));
            radioButton.setTag(to.getString("goods_attr_id") + ":" + to.getString
                    ("goods_attr_price"));
            radioButton.setButtonDrawable(android.R.color.transparent);
            radioButton.setTextColor(getResources().getColorStateList(R.color
                    .checked_inverse_white));
            radioButton.setBackgroundDrawable(getResources().getDrawable(R.drawable
                    .bg_checked_rounded2_red));
            tagGroup.addView(radioButton);
        }
    }

    // 商品详情回调
    private Listener<JSONObject> detailCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            com.orhanobut.logger.Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                finish();
                return;
            }
            try {
                mGoods = Goods.parse(response.getJSONObject("data"));
                JSONObject comments = response.getJSONObject("data").getJSONObject("comments");
                if (comments.getInt("total") != 0) {
                    Logger.d("11111");
                    mAQuery.id(R.id.comments_container).visibility(View.VISIBLE);
                    JSONObject comment = comments.getJSONObject("comment");
                    ((TextView) mAQuery.id(R.id.comments_rate).getView()).setText(
                            comments.getString("rate"));
                    ((TextView) mAQuery.id(R.id.name_e).getView()).setText(
                            comment.getString("user_name"));
                    ((RatingBar) mAQuery.id(R.id.rating_e).getView()).setRating(
                            Float.valueOf(comment.getString("rank")));
                    ((TextView) mAQuery.id(R.id.content_e).getView()).setText(
                            comment.getString("content"));
                }
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
            if (mGoods == null) {
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
        price = goods.getShopPriceDesc();
        mAQuery.id(R.id.goods_name).text(goods.getName());
        mAQuery.id(R.id.goods_price).text(goods.getShopPriceDesc());
        mAQuery.id(R.id.market_price).text(goods.getMarketPriceDesc()).getTextView().getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mAQuery.id(R.id.tag_discount).text(goods.getDiscount() + "折");
        mAQuery.id(R.id.tag_promote).visibility(goods.isPromote() ? View.VISIBLE : View.GONE);
        mAQuery.id(R.id.tag_no_postage).visibility(goods.isNoPostage() ? View.VISIBLE : View.GONE);
//        mAQuery.id(R.id.goods_preferential).text("优惠信息：" + goods.getPreferentialInfo());
        mAQuery.id(R.id.goods_freight).text("运费：" + goods.getGoodsFreight() + "元");
        if (!goods.getBrief().trim().equals("")) {
            mAQuery.id(R.id.brief_container).visibility(View.VISIBLE);
            mAQuery.id(R.id.goods_brief).text(goods.getBrief());
        }
        mAQuery.id(R.id.goods_inventory).text(String.format("%d件", goods.getInventory()));
        mAQuery.id(R.id.sale_count).text(String.format("%s件", goods.getSalesSum()));

        if (goods.isCollected()) {
            is_collect = true;
            mCollectImg.setImageResource(R.mipmap.btn_like_press);
        }

        // 初始化顶部Tab页
        mFragments[0] = IntroduceFragment.newInstance(goods.getDetailGallery());
        mFragments[1] = SpecificationFragment.newInstance(goods.getId());
//        mFragments[2] = EvaluateFragment.newInstance(goods.getId());
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
        mToolbarShoppingCartImage = (ImageView) findViewById(R.id.toolbar_shopping_cart);

        mAQuery.id(R.id.comments_container).clicked(this);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mAQuery.id(R.id.toolbar_share).clicked(this);
        mAQuery.id(R.id.collect_textview).clicked(this);
        mAQuery.id(R.id.buy_immediately_btn).clicked(this);
        mAQuery.id(R.id.add_to_cart_btn).clicked(this);
        mAQuery.id(R.id.collect_button).clicked(this);
        mAQuery.id(R.id.specification_selection_text).clicked(this);
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
            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            mCollectImg.setImageResource(R.mipmap.btn_like_press);
            AppContext.showToastShort("收藏成功");
        }
    };
    // 收藏商品响应接口
    private Listener<JSONObject> mCancelCollectCallback = new Listener<JSONObject>() {

        @Override
        public void onError(NetroidError error) {
            Logger.d("error");
            error.printStackTrace();
            super.onError(error);
        }

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            mCollectImg.setImageResource(R.mipmap.btn_like_unpress);
            AppContext.showToastShort("取消收藏");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_to_cart_btn:
                showPop();
//                SpecificationSelectionActivity.startActivity(this, SpecificationSelectionActivity
//                        .ADD_TO_CART, mGoodsId);
                break;
            case R.id.specification_selection_text:
//                SpecificationSelectionActivity.startActivity(this, SpecificationSelectionActivity
//                        .SPECIFICATION_SELECTION, mGoodsId);
                showPop();
                break;
            case R.id.buy_immediately_btn:
                showPop();
//                SpecificationSelectionActivity.startActivity(this, SpecificationSelectionActivity
//                        .BUY_IMMEDIATELY, mGoodsId);
                break;
            case R.id.collect_button:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(this, LoginActivity.class));
                    return;
                }
                if (is_collect) {
                    Logger.d("cancel");
                    UserApi.cancelCollect(mGoodsId, mCancelCollectCallback);
                    is_collect = false;
                } else {
                    Logger.d("collect");
                    UserApi.collectGoods(mGoodsId, mCollectCallback);
                    is_collect = true;
                }
                break;
            case R.id.toolbar_share:
                showShare();
                break;
            case R.id.comments_container:
                Bundle bundle = new Bundle();
                bundle.putString("GOODS_ID", mGoodsId);
                UIHelper.showSimpleBack(this, SimpleBackPage.GoodsComment, bundle);
                break;
            case R.id.collect_textview:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(this, LoginActivity.class));
                    return;
                }
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
//        ShareSDK.initSDK(this);
//        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        oks.setShareFromQQAuthSupport(false);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle("分享自小麻包母婴商城");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(ApiHelper.getGoodsShareLinkUrl(mGoodsId));
        // text是分享文本，所有平台都需要这个字段
//        oks.setText(mGoods.getName());

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        if (mGoods.getGallery().length > 0) {
//            oks.setImageUrl(mGoods.getGallery()[0]);
//        }
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(ApiHelper.getGoodsShareLinkUrl(mGoodsId));
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://www.xiaomabao.com");
//        oks.setSilent(true);
        // 启动分享GUI
//        oks.show(this);
        share_info.put("title", mGoods.getName());
        share_info.put("desc", mGoods.getBrief());
        share_info.put("url", ApiHelper.getGoodsShareLinkUrl(mGoodsId));
        XmbUtils.showMbqShare(CommodityDetailActivity.this, findViewById(R.id.commodity_detail_root), share_info);
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
            } else {
                userInfo.put("nickname", "未登录用户");
            }
            unicall.UnicallUpdateUserInfo(userInfo);
            //unicall.UnicallUpdateValidation(signatureData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //设置背景颜色
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.alpha = bgAlpha;
        this.getWindow().setAttributes(layoutParams);
    }

    public void showPop() {
        View rootView = findViewById(R.id.commodity_detail_root);
        specificationWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundAlpha(0.5f);
        specificationWindow.setBackgroundDrawable(new BitmapDrawable());
        specificationWindow.setFocusable(true);
        specificationWindow.setAnimationStyle(R.style.PopAnimation);
        specificationWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        specificationWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });

    }

    private void setPopView() {
        Netroid.displayAdImage(mGoodsThumbUrl, mGoodImageView);
        mGoodsTitleText.setText(mGoodsTitle);
        mGoodsInventoryText.setText(String.format("库存量%s件", mGoodsCount));
        mGoodsPriceText.setText(mGoodsPrice);
        mIncreaseReduceTextView.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int number) {
                if (!price.equals("") && !mGoodsCount.equals(""))
                    Log.d("COUNT", mGoodsCount);
                if (Integer.parseInt(mGoodsCount) < mIncreaseReduceTextView.getNumber()) {
                    AppContext.showToast("库存不足");
                    mIncreaseReduceTextView.setNumber(Integer.parseInt(mGoodsCount));
                }
                mGoodsPriceText.setText(mDecimalFormat.format((float) number * Float.parseFloat(price.substring(1, price.length()))) + "");
            }
        });
        mGoodConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShoppingCart();
            }
        });
    }

    private void initPopView() {
        popView = LayoutInflater.from(mContext).inflate(R.layout.commodity_specification_pop, null);
        mGoodsTitleText = (TextView) popView.findViewById(R.id.goods_title);
        mGoodsInventoryText = (TextView) popView.findViewById(R.id.inventory);
        mGoodImageView = (ImageView) popView.findViewById(R.id.goods_image);
        mGoodsPriceText = (TextView) popView.findViewById(R.id.goods_price);
        mSpecificationContainer = (LinearLayout) popView.findViewById(R.id.specification_container);
        popView.findViewById(R.id.submit_button).setOnClickListener(this);
        mIncreaseReduceTextView = (IncreaseReduceTextView) popView.findViewById(R.id.goods_count);
        mGoodConfirmBtn = (Button) popView.findViewById(R.id.submit_button);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String str = new String();
        for (RadioGroup tagGroup : mRadioGroups) {
            int id = tagGroup.getCheckedRadioButtonId();
            if (id != -1) {
                String tag = (String) tagGroup.findViewById(id).getTag();
                str = str + tag.split(":")[0] + ",";
//                price += Double.parseDouble(tag.split(":")[1]);
//                count = tag.split(":")[2];
            }
        }
        if (str.length() > 0)
            str = str.substring(0, str.length() - 1);
        String[] tagStr = str.split(",");
        Arrays.sort(tagStr);
        str = "";
        for (int i = 0; i < tagStr.length; i++) {
            str = str + tagStr[i] + ",";
        }
        str = str.substring(0, str.length() - 1);
        if (AppContext.isNetworkAvailable(CommodityDetailActivity.this)) {
            GoodsApi.getGoodsSpecificationInfo(mGoodsId, str.toString(), mIncreaseReduceTextView
                    .getNumber(), new Listener<JSONObject>() {
                @Override
                public void onPreExecute() {
                    super.onPreExecute();
//                    showWaitDialog("进行中...");
                }

                @Override
                public void onSuccess(JSONObject response) {
                    Log.e("response", response.toString());
                    TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                    if (!twoTuple.first) {
                        AppContext.showToastShort(twoTuple.second);
                        return;
                    }
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");
                        mGoodsPriceText.setText(jsonObject.getString("result"));
                        ((TextView) popView.findViewById(R.id.inventory)).setText(String.
                                format("库存量%s件", Integer.parseInt(jsonObject.getString("num"))));
                        mIncreaseReduceTextView.setNumber(Integer.parseInt(jsonObject.getString("qty")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    String message = error.toString();
                    if (TextUtils.isEmpty(error.toString())) {
                        if (TDevice.hasInternet()) {
                            message = getString(R.string.tip_load_data_error);
                        } else {
                            message = getString(R.string.tip_network_error);
                        }
                    }
                    AppContext.showToastShort(message);
                    hideWaitDialog();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mCountDownTimer = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            hideWaitDialog();
                        }
                    }.start();
                }
            });
        } else {
            AppContext.showToast("请检查当前网络");
        }
    }

    private void addShoppingCart() {
        if (!AppContext.instance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        String str = new String();
        boolean is_ = true;
        for (RadioGroup tagGroup : mRadioGroups) {
            int id = tagGroup.getCheckedRadioButtonId();
            View checkedRadio = tagGroup.findViewById(id);
            // 如果用户没有勾选任何规格直接略过
            if (checkedRadio == null) {
                is_ = false;
                continue;
            }
            String radioTag = (String) checkedRadio.getTag();
            str = str + radioTag.split(":")[0] + ",";
        }
        if (is_guige) {
            if (str.length() > 0) {
                if (is_) {
                    str = str.substring(0, str.length() - 1);
                    //选择规格接口，返回价格和库存
                    GoodsApi.addToCart(mGoodsId, str.toString(), mIncreaseReduceTextView
                            .getNumber(), addCartCallback);
                } else {
                    AppContext.showToast("请选择商品规格和属性");
                }
            } else {
                AppContext.showToast("请选择商品规格和属性");
            }
        } else {
            GoodsApi.addToCart(mGoodsId, str.toString(), mIncreaseReduceTextView
                    .getNumber(), addCartCallback);
        }
    }


    // 添加购物车回调
    private Listener<JSONObject> addCartCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            specificationWindow.dismiss();
            GoodsApi.cartGoodsCount(mCartNumCallback);

            AppContext.showToastShort("商品添加成功");

        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort("商品添加失败");
        }
    };

}
