package com.witmoon.xmb.activity.service;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.shoppingcart.MabaoCardActivity;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceCart extends BaseActivity {
    public static final int MB_CARD_CODE = 0x01;
    private EmptyLayout emptyLayout;
    private int product_id;
    private ScrollView scrollView;
    private IncreaseReduceTextView mIncreaseReduceTextView;
    private double shop_price;
    private TextView product_name,product_price,ammout_xtotal,ammout_total,mb_card;
    private EditText phone_num;
    private ArrayList<String> mSelectedCard = new ArrayList<>();

    private Button submitBtn;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.content_service_cart;
    }

    @Override
    protected String getActionBarTitle() {
        return "提交订单";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        product_id = getIntent().getIntExtra("product_id",0);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        mIncreaseReduceTextView = (IncreaseReduceTextView) findViewById(R.id.products_count);
        product_name = (TextView) findViewById(R.id.product_name);
        product_price = (TextView) findViewById(R.id.product_price);
        ammout_xtotal = (TextView) findViewById(R.id.ammout_xtotal);
        ammout_total = (TextView) findViewById(R.id.ammout_total);
        phone_num = (EditText) findViewById(R.id.phone_num);
        mb_card = (TextView)findViewById(R.id.mb_card);
        findViewById(R.id.mb_card_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ServiceCart.this, MabaoCardActivity.class),MB_CARD_CODE);
            }
        });
        phone_num.setText(AppContext.getLoginInfo().getMobile_phone());
        ServiceApi.productCart(product_id,product_num,mSelectedCard, callback);
    }

    private int product_num = 1;
    private Listener<JSONObject> callback = new Listener<JSONObject>() {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onSuccess(JSONObject response) {
            scrollView.setVisibility(View.VISIBLE);
            try {
                shop_price = response.getDouble("product_shop_price");
                product_name.setText(response.getString("product_name"));
                product_price.setText(shop_price+"元");
                ammout_xtotal.setText("¥ " + shop_price);
                ammout_total.setText("¥ " + response.getDouble("order_amount"));
                if (response.getDouble("surplus")!=0) {
                    mb_card.setText("¥ " + response.getDouble("surplus"));
                }else{
                    mb_card.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            submitBtn = (Button)findViewById(R.id.submitButton);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (phone_num.getText().length() != 11){
                        XmbUtils.showMessage(ServiceCart.this,"请输入11位数字的手机号码");
                        return;
                    }
                    submitBtn.setClickable(false);
                    HashMap<String,String> pm = new HashMap<>();
                    pm.put("product_id",product_id + "");
                    pm.put("product_number",product_num + "");
                    String cards = "";
                    for (int i = 0; i < mSelectedCard.size();i++){
                        if (i == mSelectedCard.size() - 1){
                            cards += mSelectedCard.get(i);
                        }else{
                            cards += mSelectedCard.get(i) + ",";
                        }
                    }
                    pm.put("cards",cards);
                    pm.put("mobile_phone",phone_num.getText().toString());
                    ServiceApi.submitOrder(pm, callback2);
                }
            });

            mIncreaseReduceTextView.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
                @Override
                public void onNumberChange(int number) {
                    product_num = number;
                    ServiceApi.productCart(product_id,product_num,mSelectedCard, callback);
                }
            });
        }

        @Override
        public void onError(NetroidError error) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };


    private Listener<JSONObject> callback2 = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            try {
                Boolean status = response.getInt("status") == 1 ? true : false;
                if(status){
                    JSONObject jsonObject = response.getJSONObject("data");
                    Intent intent;
                    if (jsonObject.getInt("pay_status") == 1){
                        intent = new Intent(ServiceCart.this, ServicePaySuccessActivity.class);
                        intent.putExtra("order_id", Integer.parseInt(response.getString("order_id")));
                    }else{
                        intent = new Intent(ServiceCart.this,SubmitSuccessActivity.class);
                        intent.putExtra("ORDER_INFO", jsonObject.toString());
                    }
                    startActivity(intent);
                    finish();
                }else{
                    scrollView.setVisibility(View.GONE);
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            } catch (JSONException e) {
                scrollView.setVisibility(View.GONE);
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        }

        @Override
        public void onError(NetroidError error) {
            scrollView.setVisibility(View.GONE);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MB_CARD_CODE:
                if (resultCode == RESULT_OK) {
                    mSelectedCard  = data.getStringArrayListExtra("data");
                    ServiceApi.productCart(product_id,product_num,mSelectedCard, callback);
                }
                break;
        }
    }

}
