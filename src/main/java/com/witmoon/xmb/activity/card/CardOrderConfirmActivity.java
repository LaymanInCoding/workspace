package com.witmoon.xmb.activity.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.InvoiceActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.shoppingcart.MabaoCardActivity;
import com.witmoon.xmb.activity.shoppingcart.OrderPaySuccessActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.MabaoCardApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.MoneyTextView;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/3/21.
 */
public class CardOrderConfirmActivity extends BaseActivity implements View.OnClickListener {
    public static final int MB_CARD_CODE = 0x05;
    public static final int INVOICE_CODE = 0x06;
    private LinearListView mGoodsListView;
    private TextView mMbCardText, mInvoiceText;
    private String mOrderMoney; //商品总额
    private MoneyTextView total_paymentText;
    private Button mButton;
    private ArrayList<String> mSelectedCard = new ArrayList<>();
    private String inv_type = "";  //发票类型
    private String inv_payee = ""; //发票抬头
    private String inv_content = ""; //发票内容
    private String remarks = "";
    private EditText postscriptEt;
    private EmptyLayout emptyLayout;
    private ImageView frontImageView, backendImageView;
    private ArrayList<String> mCardList = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_card_order_confirm;
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
        mCardList = getIntent().getStringArrayListExtra("list");
        mGoodsListView = (LinearListView) findViewById(R.id.goods_list);
        mMbCardText = (TextView) findViewById(R.id.mb_card);
        mInvoiceText = (TextView) findViewById(R.id.invoice_message);
        postscriptEt = (EditText) findViewById(R.id.postscript);
        remarks = postscriptEt.getText().toString().trim();
        mInvoiceText.setOnClickListener(this);
        mMbCardText.setOnClickListener(this);
        total_paymentText = (MoneyTextView) findViewById(R.id.total_payment);
        findViewById(R.id.next_step_btn).setOnClickListener(this);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MabaoCardApi.checkOrder(inv_payee, inv_type, inv_content, remarks, mSelectedCard, mCardList, mCheckCallback);
            }
        });
        MabaoCardApi.checkOrder(inv_payee, inv_type, inv_content, remarks, mSelectedCard, mCardList, mCheckCallback);
    }

    // 确认订单回调
    private Listener<JSONObject> mCheckCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
//            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
//            if (!tt.first) {
//                XmbUtils.showMessage(CardOrderConfirmActivity.this, tt.second);
//                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
//                return;
//            }
            try {
                List<Map<String, Object>> data = parseSupplier(response);
                CardOrderConfirmAdapter orderConfirmAdapter = new CardOrderConfirmAdapter
                        (CardOrderConfirmActivity.this, data);
                mGoodsListView.setLinearAdapter(orderConfirmAdapter);
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mOrderMoney = response.getJSONObject("total").getString("be_paid");
                total_paymentText.setText(mOrderMoney);
                if (response.getJSONObject("total").has("mabao_card_amount")) {
                    mMbCardText.setText(response.getJSONObject("total").getString("mabao_card_amount"));
                } else {
                    mMbCardText.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        JSONArray goodsArray = goodsObject.getJSONArray("cards");
        List<Map<String, String>> tmpDataList = new ArrayList<>();
        Map<String, Object> tmpDataMap = new HashMap<>();
        for (int i = 0; i < goodsArray.length(); i++) {
            JSONObject goods = goodsArray.getJSONObject(i);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("card_id", goods.getInt("card_id") + "");
            dataMap.put("card_no", goods.getString("card_no"));
            dataMap.put("card_name", goods.getString("card_name"));
            dataMap.put("card_money", goods.getString("card_money"));
            dataMap.put("card_cnt", goods.getInt("card_cnt") + "");
            dataMap.put("subtotal", goods.getString("subtotal"));
            dataMap.put("card_img", goods.getString("card_img"));
            tmpDataList.add(dataMap);
        }
        tmpDataMap.put("cards", tmpDataList);
        JSONObject totalObj = goodsObject.getJSONObject("total");
        tmpDataMap.put("card_numbers", totalObj.getString("card_numbers"));
        tmpDataMap.put("card_amount", totalObj.getString("card_amount"));
        dataList.add(tmpDataMap);
        return dataList;
    }

    // 提交订单回调接口
    private Listener<JSONObject> submitCallback = new Listener<JSONObject>() {


        @Override
        public void onSuccess(JSONObject response) {

            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                XmbUtils.showMessage(CardOrderConfirmActivity.this, twoTuple.second);
                return;
            }
            try {
                JSONObject jsonObject = response.getJSONObject("data");
                jsonObject.put("desc", "小麻包商城");
                jsonObject.put("subject", "小麻包商城");
                if (jsonObject.getDouble("order_amount") == 0.00) {
                    Intent intent = new Intent(CardOrderConfirmActivity.this, OrderPaySuccessActivity.class);
                    intent.putExtra("ORDER_SN", jsonObject.getString("order_sn"));
                    intent.putExtra("TYPE", "card");
                    startActivity(intent);
                    finish();
                } else {
                    CardOrderSubmitSuccessActivity.startActivity(CardOrderConfirmActivity.this, jsonObject.toString());
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
            XmbUtils.showMessage(CardOrderConfirmActivity.this, "提交订单异常");
        }
    };

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.mb_card:
                startActivityForResult(new Intent(CardOrderConfirmActivity.this, MabaoCardActivity.class), MB_CARD_CODE);
                break;
            case R.id.invoice_message:
                Intent intent = new Intent(this, InvoiceActivity.class);
                intent.putExtra("invoice_payee", inv_payee);
                intent.putExtra("invoice_content", inv_content);
                startActivityForResult(intent, INVOICE_CODE);
                break;
            case R.id.next_step_btn:
                MabaoCardApi.submitOrder(inv_payee, inv_type, inv_content, remarks, mSelectedCard, mCardList, submitCallback);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MB_CARD_CODE:
                if (resultCode == RESULT_OK) {
                    mSelectedCard = data.getStringArrayListExtra("data");
                    MabaoCardApi.checkOrder(inv_payee, inv_type, inv_content, remarks, mSelectedCard, mCardList, mCheckCallback);
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
        }
    }
}
