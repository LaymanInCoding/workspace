package com.witmoon.xmb.activity.shoppingcart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;

import com.alipay.sdk.app.PayTask;
import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.fragment.OrderDetailFragment;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.alipay.AlipayResult;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.wxapi.simcpux.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 订单提交成功, 选择付款方式Activity
 * Created by zhyh on 2015/5/28.
 */
public class OrderSubmitSuccessActivity extends BaseActivity {
    //判断跨境购/普通 接口标识符
    private static final String TAG = "MicroMsg.SDKSample.OrderSubmitSuccessActivity";
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
    String sign = "";
    PayReq req;
    public static final int PAYMENT_ALIPAY = 0;
    public static final int PAYMENT_TENPAY = 1;
    public static final int PAYMENT_UNIONPAY = 2;

    private CheckedTextView mAlipayText;
    private CheckedTextView mTenpayText;
    private String mOrderSerialNo;      // 订单号
    private String mOrderId;
    private String mOrderAmount;        // 订单总金额

    // 支付方式
    private int paymentType;
    private String orderInfo;
    private AQuery aQuery;
    private BroadcastReceiver wx_callback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int code = intent.getIntExtra("code", 0);
            if (code == 2) {
                CommonUtil.show(OrderSubmitSuccessActivity.this, "支付成功", 1000);
                Intent intent1 = new Intent(OrderSubmitSuccessActivity.this, OrderPaySuccessActivity.class);
                intent1.putExtra("ORDER_SN", mOrderSerialNo);
                intent1.putExtra("TYPE", "goods");
                startActivity(intent1);
                finish();
            }
        }
    };

    public static void startActivity(Context context, String orderInfo) {
        Intent intent = new Intent(context, OrderSubmitSuccessActivity.class);
        intent.putExtra("ORDER_INFO", orderInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wx_callback);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_submit_success;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_shopping_cart));
        setTitleColor_(R.color.master_shopping_cart);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgApi.registerApp(Constants.APP_ID);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        // 接收传过来的Json字符
        Intent intent = getIntent();
        orderInfo = intent.getStringExtra("ORDER_INFO");
        Log.e("Log", orderInfo.toString());
        try {
            JSONObject orderInfoObj = new JSONObject(orderInfo);
            mOrderSerialNo = orderInfoObj.getString("order_sn");
            mOrderAmount = orderInfoObj.getString("order_amount");
            Log.e("mOrderAmount", mOrderAmount);

            MainActivity.CURRENT_ORDER_TYPE = "goods";

            if (mOrderAmount.matches("￥\\d*\\.\\d*元")) {
                mOrderAmount = mOrderAmount.substring(1, mOrderAmount.length() - 1);
            }
        } catch (JSONException e) {
            AppContext.showToast("服务器返回数据异常");
            finish();
        }
        IntentFilter wx_callback_filter = new IntentFilter(Const.WX_CALLBACK);
        registerReceiver(wx_callback, wx_callback_filter);
        aQuery = new AQuery(this);
        mAlipayText = (CheckedTextView) aQuery.id(R.id.payment_alipay).clicked(this).getView();
        mTenpayText = (CheckedTextView) aQuery.id(R.id.payment_tenpay).clicked(this).getView();
//        mUnionPayText = (CheckedTextView) aQuery.id(R.id.payment_unionpay).clicked(this).getView();
        aQuery.id(R.id.order_sn).text("订单号：" + mOrderSerialNo).clicked(this);
        aQuery.id(R.id.next_step_btn).clicked(this);
        aQuery.id(R.id.total_payment).text(String.format("应付金额：%s元", mOrderAmount));
//        if (null != mIsSplitOrder) {
//            if (mIsSplitOrder.equals("1")) {
//                mAlipayText.setText("国际支付宝客户端支付");
//                mTenpayText.setVisibility(View.GONE);
//            }
//        }
        aQuery.id(R.id.next_step_btn).clickable(false);
        setRecRequest(1);
    }

    @Override
    public void setRecRequest(int currentPage) {
        GoodsApi.getSign(mOrderSerialNo, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Logger.json(response.toString());
                try {
                    sign = response.getString("data");
                    aQuery.id(R.id.next_step_btn).clickable(true);
                    Logger.d(sign);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                sign = "";
                aQuery.id(R.id.next_step_btn).clickable(true);
            }
        });
    }

    @Override
    protected String getActionBarTitle() {
        return "确认成功";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_alipay:
                resetCheckedState();
                mAlipayText.setChecked(true);
                paymentType = PAYMENT_ALIPAY;
                break;
            case R.id.payment_tenpay:
                resetCheckedState();
                mTenpayText.setChecked(true);
                paymentType = PAYMENT_TENPAY;
                break;
//            case R.id.payment_unionpay:
//                resetCheckedState();
//                mUnionPayText.setChecked(true);
//                paymentType = PAYMENT_UNIONPAY;
//                break;
            case R.id.order_sn:
                Bundle bundle = new Bundle();
                bundle.putString(OrderDetailFragment.KEY_ORDER_SN, mOrderSerialNo);
                bundle.putString(OrderDetailFragment.KEY_ORDER_TYPE, "await_pay");
                UIHelper.showSimpleBack(this, SimpleBackPage.ORDER_DETAIL, bundle);
                break;
            case R.id.next_step_btn:
                // 付款逻辑
                switch (paymentType) {
                    case PAYMENT_ALIPAY:
                        paymentWithAlipayNew();
                        break;
                    case PAYMENT_TENPAY:
                        GoodsApi.getWXSign(mOrderSerialNo, new Listener<JSONObject>() {
                            @Override
                            public void onPreExecute() {
                                showWaitDialog("处理中");
                            }

                            @Override
                            public void onSuccess(JSONObject response) {
                                Logger.json(response.toString());
                                try {
                                    req = new PayReq();
                                    String appId = response.getJSONObject("data").getString("appid");
                                    String nonceStr = response.getJSONObject("data").getString("noncestr");
                                    String packageValue = response.getJSONObject("data").getString("package");
                                    String partnerId = response.getJSONObject("data").getString("partnerid");
                                    String prepayId = response.getJSONObject("data").getString("prepayid");
                                    String timeStamp = response.getJSONObject("data").get("timestamp").toString();
                                    String sign = response.getJSONObject("data").getString("sign");
                                    Logger.d(appId + "/" + nonceStr + "/" + packageValue + "/" + partnerId + "/" + prepayId
                                            + "/" + timeStamp + "/" + sign);
                                    req.appId = appId;
                                    req.nonceStr = nonceStr;
                                    req.packageValue = packageValue;
                                    req.partnerId = partnerId;
                                    req.prepayId = prepayId;
                                    req.timeStamp = timeStamp;
                                    req.sign = sign;
                                    if (msgApi.isWXAppInstalled()) {
                                        msgApi.sendReq(req);
                                    } else {
                                        AppContext.showToast("没有检测到微信，请安装！");
                                    }
                                } catch (JSONException e) {
                                    AppContext.showToast("支付异常");
                                    e.printStackTrace();
                                }
                                hideWaitDialog();
                            }

                            @Override
                            public void onError(NetroidError error) {
                                hideWaitDialog();
                                AppContext.showToast("支付异常");
                            }
                        });
                        break;
                    default:
                        AppContext.showToast("该支付方式暂未实现");
                }
                break;
        }
    }


    // 通过支付宝付款
    private void paymentWithAlipayNew() {
        if (sign.equals("")) {
            AppContext.showToast("支付异常，请重进该页面");
            return;
        }
        PaymentAsyncTask paymentAsyncTask = new PaymentAsyncTask();
        paymentAsyncTask.execute(sign);
        Log.e("finalParam", sign);
    }

    // 重置支付方式选择状态
    private void resetCheckedState() {
        mAlipayText.setChecked(false);
        mTenpayText.setChecked(false);
//        mUnionPayText.setChecked(false);
    }

    // 支付异步工作类
    private class PaymentAsyncTask extends AsyncTask<String, Void, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(String... params) {
            Logger.t("支付参数_params").d(params[0]);
            PayTask payTask = new PayTask(OrderSubmitSuccessActivity.this);
            Map<String, String> payResult = payTask.payV2(params[0], true);
            Logger.d(payResult);
            return payResult;
        }

        @Override
        protected void onPostExecute(Map<String, String> payResult) {
            Logger.d(payResult);
            AlipayResult result = new AlipayResult(payResult);
            String resultStatus = result.getResultStatus();

            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                CommonUtil.show(OrderSubmitSuccessActivity.this, "支付成功", 1000);
                Intent intent1 = new Intent(OrderSubmitSuccessActivity.this, OrderPaySuccessActivity.class);
                intent1.putExtra("ORDER_SN", mOrderSerialNo);
                intent1.putExtra("TYPE", "goods");
                startActivity(intent1);
                finish();
            } else {    // 判断resultStatus 为非“9000”则代表可能支付失败
                // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    CommonUtil.show(OrderSubmitSuccessActivity.this, "支付结果确认中", 1000);
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    CommonUtil.show(OrderSubmitSuccessActivity.this, result.getMemo(), 1000);
                }
            }
        }
    }

//    /**
//     * 生成签名
//     */
//    private String genPackageSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(Constants.API_KEY);
//
//        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e("orion", packageSign);
//        return packageSign;
//    }
//
//    private String genAppSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(Constants.API_KEY);
//
//        this.sb.append("sign str\n" + sb.toString() + "\n\n");
//        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e("orion", appSign);
//        return appSign;
//    }
//
//    private String toXml(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<xml>");
//        for (int i = 0; i < params.size(); i++) {
//            sb.append("<" + params.get(i).getName() + ">");
//
//
//            sb.append(params.get(i).getValue());
//            sb.append("</" + params.get(i).getName() + ">");
//        }
//        sb.append("</xml>");
//
//        Log.e("orion", sb.toString());
//        return sb.toString();
//    }
//
//    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {
//
//        @Override
//        protected void onPreExecute() {
//            showWaitDialog("正在处理中...");
//        }
//
//        @Override
//        protected Map<String, String> doInBackground(Void... params) {
//
//            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
//            String entity = genProductArgs();
//
//            Log.e("orion", entity);
//
//            byte[] buf = Util.httpPost(url, entity);
//
//            String content = new String(buf);
//            Log.e("orion", content);
//            Map<String, String> xml = decodeXml(content);
//
//            return xml;
//        }
//
//        @Override
//        protected void onPostExecute(Map<String, String> result) {
//            hideWaitDialog();
//            sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
//
//            resultunifiedorder = result;
//
//            genPayReq();
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//
//    }
//
//    public Map<String, String> decodeXml(String content) {
//
//        try {
//            Map<String, String> xml = new HashMap<String, String>();
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(new StringReader(content));
//            int event = parser.getEventType();
//            while (event != XmlPullParser.END_DOCUMENT) {
//
//                String nodeName = parser.getName();
//                switch (event) {
//                    case XmlPullParser.START_DOCUMENT:
//
//                        break;
//                    case XmlPullParser.START_TAG:
//
//                        if ("xml".equals(nodeName) == false) {
//                            //实例化student对象
//                            xml.put(nodeName, parser.nextText());
//                        }
//                        break;
//                    case XmlPullParser.END_TAG:
//                        break;
//                }
//                event = parser.next();
//            }
//
//            return xml;
//        } catch (Exception e) {
//            Log.e("orion", e.toString());
//        }
//        return null;
//
//    }
//
//
//    private String genNonceStr() {
//        Random random = new Random();
//        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
//    }
//
//    private long genTimeStamp() {
//        return System.currentTimeMillis() / 1000;
//    }
//
//
//    private String genOutTradNo() {
//        Random random = new Random();
//        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
//    }
//
//
//    //
//    private String genProductArgs() {
//        StringBuffer xml = new StringBuffer();
//        try {
//            String nonceStr = genNonceStr();
//            xml.append("</xml>");
//            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
//            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
//            packageParams.add(new BasicNameValuePair("body", "xmb"));//mOrderDescription
//            packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
//            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
//            packageParams.add(new BasicNameValuePair("notify_url", "https://api.xiaomabao.com/payment/order_wechat_notify"));//回调页面
//            packageParams.add(new BasicNameValuePair("out_trade_no", mOrderSerialNo));
//            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
//            packageParams.add(new BasicNameValuePair("total_fee", (int) (Double.valueOf(mOrderAmount) * 100) + ""));//金额转换Float.parseFloat(mOrderAmount)*100+" "(int)Float.parseFloat(mOrderAmount)*100+
//            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
//            String sign = genPackageSign(packageParams);
//            packageParams.add(new BasicNameValuePair("sign", sign));
//            String xmlstring = toXml(packageParams);
//
//            return xmlstring;
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private void genPayReq() {
//        req.appId = Constants.APP_ID;
//        req.partnerId = Constants.MCH_ID;
//        req.prepayId = resultunifiedorder.get("prepay_id");
//        req.packageValue = "Sign=WXPay";
//        req.nonceStr = genNonceStr();
//        req.timeStamp = String.valueOf(genTimeStamp());
//
//        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
//        signParams.add(new BasicNameValuePair("appid", req.appId));
//        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
//        signParams.add(new BasicNameValuePair("package", req.packageValue));
//        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
//        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
//        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
//
//        req.sign = genAppSign(signParams);
//
//        sb.append("sign\n" + req.sign + "\n\n");
//
//        Log.e("orion", signParams.toString());
//        if (msgApi.isWXAppInstalled()) {
//            sendPayReq();
//        } else {
//            AppContext.showToast("没有检测到微信，请安装！");
//        }
//    }
//
//    private void sendPayReq() {
//        msgApi.registerApp(Constants.APP_ID);
//        msgApi.sendReq(req);
//    }
}