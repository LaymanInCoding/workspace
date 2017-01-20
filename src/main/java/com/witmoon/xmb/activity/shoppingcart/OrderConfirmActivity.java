package com.witmoon.xmb.activity.shoppingcart;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.InvoiceActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.shoppingcart.adapter.OrderConfirmAdapterV2;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.ReceiverAddress;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.dialog.WaitingDialog;
import com.witmoon.xmb.ui.popupwindow.Popup;
import com.witmoon.xmb.ui.popupwindow.PopupDialog;
import com.witmoon.xmb.ui.popupwindow.PopupUtils;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.linearlistview.LinearListView;
import com.witmoon.xmblibrary.linearlistview.util.DensityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 确认订单界面Activity
 * Created by zhyh on 2015/5/27
 */
public class OrderConfirmActivity extends BaseActivity implements View.OnClickListener {
    public static final int ADDRESS_CODE = 0x01;
    public static final int CASH_COUPON_CODE = 0x02;
    public static final int GET_IMAGE_VIA_CAMERA = 0x03;
    public static final int IMAGE_OPEN = 0x04;
    public static final int MB_CARD_CODE = 0x05;
    public static final int INVOICE_CODE = 0x06;
    public static final int MABAO_BEAN_CODE = 0x07;
    private LinearListView mGoodsListView;
    private WaitingDialog mWaitingDialog;
    private View mReceiverLayout;   // 收货地址
    private View mNoReceiverTipView;
    private String str_id_card;
    private TextView mReceiverText, name_text; // 收货人
    private TextView mReceiverAddress;  // 收货人地址
    private TextView mTotalPaymentText; // 应付金额
    private TextView mTotalPriceText;   // 商品总价格
    private TextView mShippingFeeText;  // 运费
    private TextView mDiscountText;  // 优惠金额
    private TextView mUseCashCouponText, goods_envelope, goods_dvolume, mMbCardText, mInvoiceText, mUseBeanText;
    private TextView mUseBonusText;
    private TextView id_card;
    private TextView line;
    private float is_goods_envelope = 0, is_goods_dvolume = 0;
    private LinearLayout error_prompt__information, over_sea_container;
    private EditText mName, mId, postscriptText;//姓名---身份
    private String str_name, str_id;
    private String mOrderMoney; //商品总额
    private Button mButton;
    private String mReceiverAddressId ;  // 送货地址ID
    private String mCashCouponId = "";       // 客户选择代金券ID
    private ArrayList<String> mSelectedCard = new ArrayList<>();
    private String mBonusId = "";       // 客户选择红包或兑换ID
    private String mBeanId = ""; //麻豆金额
    private String inv_type = "";  //发票类型
    private String inv_payee = ""; //发票抬头
    private String inv_content; //发票内容
    private LinearLayout k_lin;
    private EmptyLayout emptyLayout;
    private ImageView frontImageView, backendImageView;
    String name;
    private boolean is_code;
    private String localTempImgDir = "localTempImgDir";
    private String localTempImgFileName = System.currentTimeMillis() + "";
    private PopupDialog popupDialog;
    private Bitmap mBitmap;
    private String pathImage1 = "front_pic";
    private String pathImage2 = "backend_pic";
    private int current_img = 1;

    public static void startActivity(Context context, boolean is_code) {
        Intent intent = new Intent(context, OrderConfirmActivity.class);
        intent.putExtra("is_code", is_code);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_confirm;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        //设置标题栏
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_shopping_cart));
        //设置通知栏
        setTitleColor_(R.color.master_shopping_cart);
    }

    @Override
    protected String getActionBarTitle() {
        return "确认订单";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        getIntent().getBooleanExtra("is_code", false);
        mWaitingDialog = new WaitingDialog(this);
        name_text = (TextView) findViewById(R.id.receiver_name_text);
        mName = (EditText) findViewById(R.id.receiver_name);
        postscriptText = (EditText) findViewById(R.id.postscript);
        k_lin = (LinearLayout) findViewById(R.id.k_lin);
        over_sea_container = (LinearLayout) findViewById(R.id.over_sea_container);
        mId = (EditText) findViewById(R.id.id_no);
        mReceiverLayout = findViewById(R.id.address_layout);
        mReceiverLayout.setOnClickListener(this);
        mNoReceiverTipView = findViewById(R.id.submit_button);
        id_card = (TextView) findViewById(R.id.id_card);
        goods_envelope = (TextView) findViewById(R.id.goods_envelope);
        goods_dvolume = (TextView) findViewById(R.id.goods_dvolume);
        mNoReceiverTipView.setOnClickListener(this);
        mReceiverText = (TextView) findViewById(R.id.consignee_text);
        mReceiverAddress = (TextView) findViewById(R.id.address);
        mTotalPaymentText = (TextView) findViewById(R.id.total_payment);
        mTotalPriceText = (TextView) findViewById(R.id.total_price);
        mGoodsListView = (LinearListView) findViewById(R.id.goods_list);
        error_prompt__information = (LinearLayout) findViewById(R.id.error_prompt__information);
        mShippingFeeText = (TextView) findViewById(R.id.shipping_fee);
        mDiscountText = (TextView) findViewById(R.id.total_discount);
        mUseCashCouponText = (TextView) findViewById(R.id.use_cash_coupon);
        mMbCardText = (TextView) findViewById(R.id.mb_card);
        mUseBonusText = (TextView) findViewById(R.id.use_bonus);
        mUseBeanText = (TextView) findViewById(R.id.use_mabao_bean);
        mUseBeanText.setOnClickListener(this);
        mUseCashCouponText.setOnClickListener(this);
        mUseBonusText.setOnClickListener(this);
        mInvoiceText = (TextView) findViewById(R.id.invoice_message);
        mInvoiceText.setOnClickListener(this);
        mMbCardText.setOnClickListener(this);
        frontImageView = (ImageView) findViewById(R.id.post_add_front_pic);
        frontImageView.setOnClickListener(this);

        findViewById(R.id.post_add_submit).setOnClickListener(this);

        backendImageView = (ImageView) findViewById(R.id.post_add_backend_pic);
        backendImageView.setOnClickListener(this);
        mButton = (Button) findViewById(R.id.submit_name_id);
        mButton.setTextColor(Color.WHITE);
        findViewById(R.id.line).setOnClickListener(this);
        findViewById(R.id.submit_name_id).setOnClickListener(this);
        line = (TextView) findViewById(R.id.line);
        line.setText(Html.fromHtml("<u>" + "为什么需要身份认证？" + "</u>"));
        findViewById(R.id.next_step_btn).setOnClickListener(this);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsApi.checkOrder(mBonusId, mCashCouponId, mSelectedCard, mBeanId, mCheckCallback);
            }
        });
        GoodsApi.checkOrder(mBonusId, mCashCouponId, mSelectedCard, mBeanId, mCheckCallback);
    }

    private boolean is_over_sea;
    // 确认订单回调
    private Listener<JSONObject> mCheckCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                XmbUtils.showMessage(OrderConfirmActivity.this, tt.second);
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                return;
            }
            try {
                JSONObject dataObj = response.getJSONObject("data");
                List<Map<String, Object>> data = parseSupplier(dataObj.getJSONObject("s_goods_list"));
                OrderConfirmAdapterV2 orderConfirmAdapter = new OrderConfirmAdapterV2
                        (OrderConfirmActivity.this, data);
                mGoodsListView.setLinearAdapter(orderConfirmAdapter);
                is_code = Boolean.parseBoolean(dataObj.getString("real_name"));
                if (!is_code) {
                    k_lin.setVisibility(View.GONE);
                }
                Boolean over_sea = Boolean.parseBoolean(dataObj.getString("is_over_sea"));

                mOrderMoney = dataObj.getString("goods_amount");
                mTotalPaymentText.setText(dataObj.getString("order_amount_formatted"));
                mTotalPriceText.setText(dataObj.getString("goods_amount_formatted"));
                mShippingFeeText.setText(dataObj.getString("shipping_fee_formatted"));
                mDiscountText.setText(dataObj.getString("discount_formatted"));

                if (dataObj.has("surplus")) {
                    mMbCardText.setText(dataObj.getString("surplus"));
                }
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                JSONObject receiverObj = dataObj.getJSONObject("consignee");
                if (receiverObj == null || !receiverObj.has("id")) {
                    is_over_sea = true;
                    mNoReceiverTipView.setVisibility(View.VISIBLE);
                    mReceiverLayout.setVisibility(View.GONE);
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    return;
                }
                mReceiverAddressId = receiverObj.getString("id");
                mReceiverText.setText(receiverObj.getString("consignee") + "（" + receiverObj
                        .getString("mobile") + "）");
                mReceiverAddress.setText(receiverObj.getString("province_name") + receiverObj
                        .getString("city_name") + receiverObj.getString("district_name") +
                        receiverObj.getString("address"));
                String _name = receiverObj.getString("consignee");
                if (!receiverObj.has("idcard")) {
                    is_over_sea = true;
                }
                JSONObject mJSONObject = (JSONObject) receiverObj.get("idcard");
                String is_black = mJSONObject.getInt("is_black") + "";
                String identity_card = mJSONObject.getString("identity_card");
                if (!mJSONObject.getString("identity_card_front_thumb").equals("")) {
                    Netroid.displayImage(mJSONObject.getString("identity_card_front_thumb"), frontImageView);
                } else {
                    is_over_sea = false;
                }
                if (!mJSONObject.getString("identity_card_backend_thumb").equals("")) {
                    Netroid.displayImage(mJSONObject.getString("identity_card_backend_thumb"), backendImageView);
                } else {
                    is_over_sea = false;
                }

                if (!over_sea) {
                    over_sea_container.setVisibility(View.GONE);
                    is_over_sea = true;
                }
                if (!_name.equals("")) {
                    mName.setVisibility(View.GONE);
                    name_text.setText(_name);
                } else {
                    mName.setVisibility(View.VISIBLE);
                }
                if (identity_card.length() == 18) {
                    mName.setVisibility(View.GONE);
                    mId.setVisibility(View.GONE);
                    id_card.setText(identity_card.substring(0, 6) + "********" + identity_card.substring(14));
                    id_card.setVisibility(View.VISIBLE);
                    if (is_black.equals("0")) {
                        mButton.setText("已验证");
                    } else {
                        mButton.setText("已拉黑");
                        error_prompt__information.setVisibility(View.VISIBLE);
                    }
                    mButton.setEnabled(false);
                    str_id_card = identity_card;
                    return;
                }
                mId.setVisibility(View.VISIBLE);
                mButton.setEnabled(true);
                mButton.setText("验证");
                id_card.setVisibility(View.GONE);
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            } catch (JSONException e) {
                is_over_sea = true;
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }

        @Override
        public void onError(NetroidError error) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    private List<Map<String, Object>> parseSupplier(JSONObject goodsObject)
            throws JSONException {
        List<Map<String, Object>> dataList = new ArrayList<>();

        Iterator it = goodsObject.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            JSONObject value = goodsObject.getJSONObject(key);
            JSONArray goodsArray = value.getJSONArray("goods_list");
            List<Map<String, String>> tmpDataList = new ArrayList<>();
            Map<String, Object> tmpDataMap = new HashMap<>();
            for (int i = 0; i < goodsArray.length(); i++) {
                JSONObject goods = goodsArray.getJSONObject(i);
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("title", goods.getString("goods_name"));
                dataMap.put("goods_attr", goods.getString("goods_attr"));
                dataMap.put("url", goods.getString("goods_thumb"));
                dataMap.put("price_formatted", goods.getString("goods_price_formatted"));
                dataMap.put("price", goods.getString("goods_price"));
                dataMap.put("market_price_formatted", goods.getString("market_price_formatted"));
                dataMap.put("count", goods.getString("goods_number"));
                tmpDataList.add(dataMap);
            }
            tmpDataMap.put("goods_list", tmpDataList);
            tmpDataMap.put("number", value.getString("number"));
            tmpDataMap.put("total_money", value.getString("total_money"));
            tmpDataMap.put("cross_border_money", value.getString("cross_border_money"));
            tmpDataMap.put("shipping_fee", value.getString("shipping_fee"));
            tmpDataMap.put("supplier_name", key);
            dataList.add(tmpDataMap);
        }
        return dataList;
    }

    // 提交订单回调接口
    private Listener<JSONObject> submitCallback = new Listener<JSONObject>() {


        @Override
        public void onSuccess(JSONObject response) {

            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                XmbUtils.showMessage(OrderConfirmActivity.this, twoTuple.second);
                return;
            }
            try {
                JSONObject jsonObject = response.getJSONObject("data").getJSONObject("order_info");
                if (jsonObject.getDouble("order_amount") == 0.00) {
                    Intent intent = new Intent(OrderConfirmActivity.this, OrderPaySuccessActivity.class);
                    intent.putExtra("ORDER_SN", jsonObject.getString("order_sn"));
                    startActivity(intent);
                } else {
                    OrderSubmitSuccessActivity.startActivity(OrderConfirmActivity.this, jsonObject.toString());
                    AppContext.showToastShort("提交订单成功, 请尽快完成支付");
                }
                //订单成功发送广播更新购物车
                Intent intent = new Intent(Const.INTENT_ACTION_UPDATA_CAR);
                sendBroadcast(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            findViewById(R.id.next_step_btn).setClickable(true);
            XmbUtils.showMessage(OrderConfirmActivity.this, "提交订单异常");
        }
    };

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.submit_button:
            case R.id.address_layout:
                bundle.putString("selectedId", mReceiverAddressId);
                UIHelper.showSimpleBackForResult(this, ADDRESS_CODE, SimpleBackPage
                        .ADDRESS_SELECTOR, bundle);
                break;
            case R.id.mb_card:
                startActivityForResult(new Intent(OrderConfirmActivity.this, MabaoCardActivity.class), MB_CARD_CODE);
                break;
            case R.id.use_cash_coupon:
                if (is_goods_envelope > 0.00) {
                    XmbUtils.showMessage(OrderConfirmActivity.this, "您已选择红包或兑换券！");
                    break;
                }
                bundle.putString("type", "selector");
                bundle.putString("money", mOrderMoney);
                UIHelper.showSimpleBackForResult(this, 2, SimpleBackPage.CASH_COUPON, bundle);
                break;
            case R.id.invoice_message:
                Intent intent = new Intent(this, InvoiceActivity.class);
                intent.putExtra("invoice_payee", inv_payee);
                intent.putExtra("invoice_content", inv_content);
                startActivityForResult(intent, INVOICE_CODE);
                break;
            case R.id.use_bonus:
                if (is_goods_dvolume > 0.00) {
                    XmbUtils.showMessage(OrderConfirmActivity.this, "您已选择代金券！");
                    break;
                }
                doSearchBonus();
                break;
            case R.id.use_mabao_bean:
                bundle.putString("mabaobean_number", mBeanId);
                UIHelper.showSimpleBackForResult(this, MABAO_BEAN_CODE, SimpleBackPage.BeanUse,bundle);
                break;
            case R.id.submit_name_id:
                if (isCheck()) {
                    if (!mName.getText().toString().equals("")) {
                        name = mName.getText().toString();
                    } else {
                        name = name_text.getText().toString();
                    }
                    if (!mReceiverText.getText().toString().equals("")) {
                        GoodsApi.check_idcard(name, mId.getText().toString(), new Listener<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                try {
                                    if (response.getInt("status") == 0) {
                                        AppContext.showToast(response.getString("msg"));
                                        return;
                                    }
                                    String sid = mId.getText().toString();
                                    AppContext.showToast(response.getString("msg"));
                                    mButton.setEnabled(false);
                                    if (response.getString("black").equals("0")) {
                                        mButton.setText("已验证");
                                    } else {
                                        mButton.setText("已拉黑");
                                    }

                                    mId.setVisibility(View.GONE);
                                    str_id_card = sid;
                                    id_card.setText(sid.substring(0, 6) + "********" + sid.substring(14));
                                    id_card.setVisibility(View.VISIBLE);
                                    mName.setVisibility(View.GONE);
                                    name_text.setText(name);
                                } catch (JSONException e) {
                                }
//                                catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        });
                    } else {
                        XmbUtils.showMessage(OrderConfirmActivity.this, "请先选择收货地址！");
                    }
                }
                break;
            case R.id.line:
                mWaitingDialog.showInformation_prompt();
                mWaitingDialog.show();
                mWaitingDialog.mImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWaitingDialog.dismiss();
                    }
                });
                break;
            case R.id.post_add_front_pic:
                showDownUpPopupDialog(1);
                break;
            case R.id.post_add_backend_pic:
                showDownUpPopupDialog(2);
                break;
            case R.id.post_add_submit:
                ModifyAsyncTask task = new ModifyAsyncTask(OrderConfirmActivity.this);
                task.execute();
                break;
            case R.id.next_step_btn:
                if (mReceiverAddressId == null) {
                    XmbUtils.showMessage(this, "请选择收货地址");
                    return;
                }
                if (!is_over_sea) {
                    XmbUtils.showMessage(this, "海外直邮必须上传身份证照片");
                    return;
                }
                if (!mName.getText().toString().equals("")) {
                    name = mName.getText().toString();
                } else {
                    name = name_text.getText().toString();
                }
                String postscript = postscriptText.getText().toString();
                if (k_lin.getVisibility() == View.GONE) {
                    findViewById(R.id.next_step_btn).setClickable(false);
                    if (inv_payee.equals("个人")) {
                        GoodsApi.submitOrder(mReceiverAddressId, mCashCouponId, mBonusId, mBeanId, name, str_id_card, postscript, mSelectedCard, inv_type, "", inv_content, submitCallback);
                    } else {
                        GoodsApi.submitOrder(mReceiverAddressId, mCashCouponId, mBonusId, mBeanId, name, str_id_card, postscript, mSelectedCard, inv_type, inv_payee, inv_content, submitCallback);
                    }
                } else {
                    if (id_card.getVisibility() == View.VISIBLE && name_text.getVisibility() == View.VISIBLE) {
                        if (inv_payee.equals("个人")) {
                            GoodsApi.submitOrder(mReceiverAddressId, mCashCouponId, mBonusId, mBeanId, name, str_id_card, postscript, mSelectedCard, inv_type, "", inv_content, submitCallback);
                        } else {
                            GoodsApi.submitOrder(mReceiverAddressId, mCashCouponId, mBonusId, mBeanId, name, str_id_card, postscript, mSelectedCard, inv_type, inv_payee, inv_content, submitCallback);
                        }
                    } else {
                        XmbUtils.showMessage(this, "请先验证身份！");
                    }
                }
                break;
        }
    }

    // 搜索兑换券或红包
    private void doSearchBonus() {
        LinearLayout containerLayout = new LinearLayout(this);
        containerLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        containerLayout.setPadding(DensityUtil.dip2px(this, 25), DensityUtil.dip2px(this, 10), DensityUtil.dip2px(this, 25), 0);
        final EditText input = new EditText(this);
        input.setTextSize(DensityUtil.dip2px(this, 6));
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setPadding(DensityUtil.dip2px(this, 10), DensityUtil.dip2px(this, 10), DensityUtil.dip2px(this, 10), DensityUtil.dip2px(this, 10));
        input.setBackgroundResource(R.drawable.bg_input_area);
        input.setHint("请输入兑换券或红包序列号");
        input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        containerLayout.addView(input);
        new AlertDialog.Builder(this).setView(containerLayout).setNegativeButton("取消", null)
                .setTitle("使用兑换券或红包")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String no = input.getText().toString();
                        if (TextUtils.isEmpty(no)) {
                            XmbUtils.showMessage(OrderConfirmActivity.this, "请输入兑换券或红包序列号");
                            return;
                        }
                        GoodsApi.searchBouns(no, mOrderMoney, mSearchBonusCallback);
                    }
                }).show();
    }

    // 搜索红包回调接口
    private Listener<JSONObject> mSearchBonusCallback = new Listener<JSONObject>() {

        @Override
        public void onPreExecute() {
            showWaitDialog();
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToast(tt.second);
                return;
            }
            try {
                JSONObject dataObject = response.getJSONObject("data");
                mUseBonusText.setText(dataObject.getString("type_name"));
                mBonusId = dataObject.getString("bonus_id");
                is_goods_envelope = Float.parseFloat(dataObject.getString("type_money"));
                mUseCashCouponText.setText("");
                goods_envelope.setText("红包或兑换券: ￥" + is_goods_envelope);
                // 选择红包后刷新界面
                GoodsApi.checkOrder(mBonusId, mCashCouponId, mSelectedCard, mBeanId, mCheckCallback);
            } catch (JSONException e) {
                XmbUtils.showMessage(OrderConfirmActivity.this, "服务器异常");
            }
        }

        @Override
        public void onFinish() {
            hideWaitDialog();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADDRESS_CODE:
                if (resultCode == RESULT_OK) {
                    mReceiverLayout.setVisibility(View.VISIBLE);
                    mNoReceiverTipView.setVisibility(View.GONE);
                    ReceiverAddress addr = (ReceiverAddress) data.getSerializableExtra("address");
                    mReceiverAddressId = addr.getId();
                    GoodsApi.checkOrderV2(mBonusId, mCashCouponId, mReceiverAddressId, mSelectedCard, mBeanId, mCheckCallback);
                }
                break;
            case CASH_COUPON_CODE:
                if (resultCode == RESULT_OK) {
                    Map<String, String> dataMap = (Map<String, String>) data.getSerializableExtra
                            ("data");
                    if (dataMap.size() > 0) {
                        mUseCashCouponText.setText(dataMap.get("title"));
                        mCashCouponId = dataMap.get("id");
                        is_goods_dvolume = Float.valueOf(dataMap.get("type_money"));
                        goods_dvolume.setText("代金券：￥" + is_goods_dvolume);
                        // 选择红包后刷新界面
                        GoodsApi.checkOrder(mBonusId, mCashCouponId, mSelectedCard, mBeanId, mCheckCallback);
                    } else {
                        GoodsApi.checkOrder("", "", mSelectedCard, mBeanId, mCheckCallback);
                        goods_dvolume.setText("");
                        mUseCashCouponText.setText("");
                    }
                }
                break;
            case MB_CARD_CODE:
                if (resultCode == RESULT_OK) {
                    mSelectedCard = data.getStringArrayListExtra("data");
                    GoodsApi.checkOrder(mBonusId, mCashCouponId, mSelectedCard, mBeanId, mCheckCallback);
                }
                break;
            case INVOICE_CODE:
                if (resultCode == RESULT_OK) {
                    inv_type = data.getStringExtra("inv_type");
                    inv_content = data.getStringExtra("inv_content");
                    inv_payee = data.getStringExtra("inv_payee");
                    mInvoiceText.setText(inv_payee);
                }
                break;
            case IMAGE_OPEN:
                Uri uri = data.getData();
                if (!TextUtils.isEmpty(uri.getAuthority())) {
                    //查询选择图片
                    settingImg(uri, current_img);
                }
                break;
            case GET_IMAGE_VIA_CAMERA:
                File f = new File(Environment.getExternalStorageDirectory()
                        + "/" + localTempImgDir + "/" + localTempImgFileName);
                try {
                    Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                            f.getAbsolutePath(), null, null));
                    settingImg(u, current_img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case MABAO_BEAN_CODE:
                if (resultCode == RESULT_OK) {
                    mBeanId = data.getStringExtra("mabaobean_number");
                    GoodsApi.checkOrder(mBonusId, mCashCouponId, mSelectedCard, mBeanId, mCheckCallback);
                    mUseBeanText.setText("已使用" + mBeanId + "麻豆");
                }
                break;
        }
    }

    //外部查询返回
    private void settingImg(Uri uri, int type) {
        mBitmap = null;
        Cursor cursor = managedQuery(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        //返回 没找到选择图片
        if (null == cursor) {
            return;
        }
        //光标移动至开头 获取图片路径
        cursor.moveToFirst();
        if (type == 1) {
            pathImage1 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        } else if (type == 2) {
            pathImage2 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        try {
            if (type == 1) {
                mBitmap = BitmapUtils.getCompressedImage(pathImage1, 5);
            } else if (type == 2) {
                mBitmap = BitmapUtils.getCompressedImage(pathImage2, 5);
            }
        } catch (NullPointerException e) {
            XmbUtils.showMessage(OrderConfirmActivity.this, "当前相片不可用，重新选择或拍照！");
            return;
        }
        if (current_img == 1) {
            frontImageView.setImageBitmap(mBitmap);
        } else {
            backendImageView.setImageBitmap(mBitmap);
        }
    }

    private void showDownUpPopupDialog(int type) {
        current_img = type;
        Popup popup = new Popup();
        popup.setvWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setvHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setClickable(true);
        popup.setContentView(R.layout.view_userheader_modifydetail);
        //设置触摸其他位置时关闭窗口
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int height = view.findViewById(R.id.flMaskLayer).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        PopupUtils.dismissPopupDialog();
                    }
                }
                return true;
            }
        };
        popup.setTouchListener(listener);
        popupDialog = PopupUtils.createPopupDialog(this, popup);
        popupDialog.showAtLocation(findViewById(R.id.post_pic_container), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, popup.getxPos(), popup.getyPos());
        View view = popupDialog.getContentView();
        //背景透明度设置
        view.findViewById(R.id.flMaskLayer).setAlpha(0.75f);
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消按钮
                if (v.getId() == R.id.tvCancel) {
                    PopupUtils.dismissPopupDialog();
                }
                //从手机
                else if (v.getId() == R.id.tvTakeHeader) {
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                        try {
                            File dir = new File(Environment.getExternalStorageDirectory() + "/" + localTempImgDir);
                            if (!dir.exists()) dir.mkdirs();
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(dir, localTempImgFileName);//localTempImgDir和localTempImageFileName是自己定义的名字
                            Uri u = Uri.fromFile(f);
                            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                            startActivityForResult(intent, GET_IMAGE_VIA_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            // TODO Auto-generated catch block
                            XmbUtils.showMessage(OrderConfirmActivity.this, "没有找到储存目录");
                        }
                    } else {
                        XmbUtils.showMessage(OrderConfirmActivity.this, "没有找到储存卡");
                    }
                    PopupUtils.dismissPopupDialog();
                }
                //从相册
                else if (v.getId() == R.id.tvHeaderFromSD) {
                    PopupUtils.dismissPopupDialog();
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                }
            }
        };
        view.findViewById(R.id.tvCancel).setOnClickListener(l);
        view.findViewById(R.id.tvTakeHeader).setOnClickListener(l);
        view.findViewById(R.id.tvHeaderFromSD).setOnClickListener(l);
    }

    private boolean isCheck() {
        boolean is_check = false;
        str_name = name_text.getText().toString().trim();
        str_id = mId.getText().toString().trim();
        if (str_name.equals("")) {
            XmbUtils.showMessage(OrderConfirmActivity.this, "请输入姓名！");
        } else if (str_id.equals("")) {
            XmbUtils.showMessage(OrderConfirmActivity.this, "请输入身份证！");
        } else {
            is_check = true;
        }
        return is_check;
    }

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, OrderConfirmActivity> {

        public ModifyAsyncTask(OrderConfirmActivity orderConfirmActivity) {
            super(orderConfirmActivity);
        }

        @Override
        protected void onPreExecute(OrderConfirmActivity orderConfirmActivity) {
            super.onPreExecute(orderConfirmActivity);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(OrderConfirmActivity orderConfirmActivity,
                                        Void... params) {
            ArrayList<File> arrayList = new ArrayList<>();
            BitmapDrawable bmpDrawable1 = (BitmapDrawable) frontImageView.getDrawable();
            Bitmap bmp1 = bmpDrawable1.getBitmap();

            BitmapDrawable bmpDrawable2 = (BitmapDrawable) backendImageView.getDrawable();
            Bitmap bmp2 = bmpDrawable2.getBitmap();
            arrayList.add(saveAvatarBitmap(bmp1, pathImage1));
            arrayList.add(saveAvatarBitmap(bmp2, pathImage2));
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                if (!mName.getText().toString().equals("")) {
                    name = mName.getText().toString();
                } else {
                    name = name_text.getText().toString();
                }
                pm.put("real_name", name);
                pm.put("identity_card", str_id_card);
                String response = HttpUtility.post("https://api.xiaomabao.com/idcard/update", null, pm, "photo", arrayList);
                JSONObject respObj = new JSONObject(response);
                if (respObj.getString("status").equals("0")) {
                    return respObj.getString("msg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(OrderConfirmActivity orderConfirmActivity, String result) {
            hideWaitDialog();
            if (result != null) {
                AppContext.showToastShort(result);
                return;
            }
            is_over_sea = true;
            XmbUtils.showMessage(OrderConfirmActivity.this, "操作成功");
        }
    }

    private File saveAvatarBitmap(Bitmap avatar, String path) {
        File mFile = new File(path);
        OutputStream os = null;
        try {
            os = new FileOutputStream(mFile);
            avatar.compress(Bitmap.CompressFormat.JPEG, 30, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                }
            }
        }
        return mFile;
    }
}
