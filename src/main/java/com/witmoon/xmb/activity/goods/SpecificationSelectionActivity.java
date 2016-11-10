package com.witmoon.xmb.activity.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;
import com.witmoon.xmb.ui.widget.LineFeedHorizontalLayout;
import com.witmoon.xmb.util.DensityUtils;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

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
 * 规格选择
 * Created by zhyh on 2015/6/4
 */
public class SpecificationSelectionActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    public static final int BUY_IMMEDIATELY = 0;    // 立即购买
    public static final int ADD_TO_CART = 1;        // 加入购物车
    public static final int SPECIFICATION_SELECTION = 2;    // 规格选择

    DecimalFormat mDecimalFormat = new DecimalFormat("##0.00");
    private int type;
    private boolean is_guige;
    private AQuery mAQuery;
    private LinearLayout mSpecificationContainer;

    private TextView mGoodsPriceText;
    private IncreaseReduceTextView mIncreaseReduceTextView;

    private View mBottomLayout;
    private Button mBottomBuyBtn;
    private Button mBottomAddBtn;
    private CountDownTimer mCountDownTimer;
    private String mGoodsId;
    private String price = "";
    private String mGoodsCount;
    private double mGoodsPrice;
    private int width;
    private int height;
    private EmptyLayout emptyLayout;
    private List<RadioGroup> mRadioGroups = new ArrayList<>();

    public static void startActivity(Context context, int type, String goodsId) {
        Intent intent = new Intent(context, SpecificationSelectionActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("GOODS_ID", goodsId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_commodity_specification;
    }

    @Override
    protected String getActionBarTitle() {
        return "商品规格";
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_shopping_cart));
        setTitleColor_(R.color.master_shopping_cart);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        Intent intent = getIntent();
        type = intent.getIntExtra("type", SPECIFICATION_SELECTION);
        if (intent.hasExtra("GOODS_ID")) {
            mGoodsId = intent.getStringExtra("GOODS_ID");
        }

        mAQuery = new AQuery(this);
        initView();
        initBottomActionLayout();
        GoodsApi.goodsSpecification(mGoodsId, mSpecificationCallback);
    }

    private void initView() {
        mGoodsPriceText = mAQuery.id(R.id.goods_price).getTextView();
        mSpecificationContainer = (LinearLayout) mAQuery.id(R.id.specification_container).getView();
        mBottomLayout = mAQuery.id(R.id.bottom_layout).getView();
        mAQuery.id(R.id.next_step_btn).clicked(this);
        mAQuery.id(R.id.submit_button).clicked(this);
        mBottomAddBtn = mAQuery.id(R.id.bottom_confirm_add).clicked(this).getButton();
        mBottomBuyBtn = mAQuery.id(R.id.bottom_confirm_buy).clicked(this).getButton();
        mIncreaseReduceTextView = (IncreaseReduceTextView) findViewById(R.id.goods_count);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mIncreaseReduceTextView.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int number) {
                if (!price.equals(""))
                    if (Integer.parseInt(mGoodsCount) < mIncreaseReduceTextView.getNumber()) {
                        AppContext.showToast("库存不足");
                        mIncreaseReduceTextView.setNumber(Integer.parseInt(mGoodsCount));
                    }
                mGoodsPriceText.setText(mDecimalFormat.format((float) number * Float.parseFloat(price.substring(1, price.length()))) + "");
            }
        });
    }

    // 商品规格回调接口
    private Listener<JSONObject> mSpecificationCallback = new Listener<JSONObject>() {
        @Override
        public void onError(NetroidError error) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
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
                mGoodsPrice = Double.parseDouble(dataObj.getString("shop_price"));
                Netroid.displayImage(dataObj.getString("goods_thumb"), mAQuery.id(R.id.goods_image)
                        .getImageView());
                mAQuery.id(R.id.goods_title).text(dataObj.getString("goods_name"));
                price = dataObj.getString("shop_price_formatted");
                mGoodsPriceText.setText(dataObj.getString("shop_price_formatted"));
                mGoodsCount = dataObj.getString("goods_number");
                mAQuery.id(R.id.inventory).text(String.format("库存量：%s件", dataObj.getString
                        ("goods_number")));
                parseResponseAndUpdateUI(dataObj.getJSONArray("goods_specs"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
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

    private void initBottomActionLayout() {
        mBottomLayout.setVisibility(View.GONE);
        mBottomBuyBtn.setVisibility(View.GONE);
        mBottomAddBtn.setVisibility(View.GONE);
        switch (type) {
            case SPECIFICATION_SELECTION:
                mBottomLayout.setVisibility(View.VISIBLE);
                break;
            case ADD_TO_CART:
                mBottomAddBtn.setVisibility(View.VISIBLE);
                break;
            case BUY_IMMEDIATELY:
                mBottomBuyBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean isImmediately;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_confirm_add:
            case R.id.submit_button:
                isImmediately = false;
                addShoppingCart();
                break;
            case R.id.bottom_confirm_buy:
            case R.id.next_step_btn:
                isImmediately = true;
                addShoppingCart();
                break;
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
            AppContext.showToastShort("商品添加成功");
            if (isImmediately) {
                UIHelper.showSimpleBack(SpecificationSelectionActivity.this, SimpleBackPage
                        .SHOPPING_CART);
            }
            finish();
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort("商品添加失败");
        }
    };

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        double price = 0F;
//        String count = "";
//        int lastCount = mIncreaseReduceTextView.getNumber();
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
        if (AppContext.isNetworkAvailable(SpecificationSelectionActivity.this)) {
            GoodsApi.getGoodsSpecificationInfo(mGoodsId, str.toString(), mIncreaseReduceTextView
                    .getNumber(), new Listener<JSONObject>() {
                @Override
                public void onPreExecute() {
                    super.onPreExecute();
                    showWaitDialog("进行中...");
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
                        mAQuery.id(R.id.inventory).text(String.format("库存量%s件", Integer.parseInt(jsonObject.getString("num"))));
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
//        price += mGoodsPrice;
//        mGoodsCount = count;
//        if (!mGoodsCount.equals("0")) {
//            if (lastCount > Integer.parseInt(count)) {
//                AppContext.showToast("库存不足");
//                mIncreaseReduceTextView.setNumber(Integer.parseInt(count));
//            } else
//                mIncreaseReduceTextView.setNumber(lastCount);
//        } else {
//            AppContext.showToast("该规格库存不足");
//            mIncreaseReduceTextView.setNumber(0);
//        }
//        mGoodsPriceText.setText(String.format("￥%s", price * mIncreaseReduceTextView.getNumber()));
//        mAQuery.id(R.id.inventory).text(String.format("库存量%s件", Integer.parseInt(count)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();

        }
    }
}
