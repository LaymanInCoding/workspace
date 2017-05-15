package com.witmoon.xmb.activity.service;

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
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.api.alipay.AlipayResult;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.wxapi.simcpux.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 订单提交成功, 选择付款方式Activity
 * Created by zhyh on 2015/5/28.
 */
public class SubmitSuccessActivity extends BaseActivity {
    //判断跨境购/普通 接口标识符
    private static final String TAG = "MicroMsg.SDKSample.OrderSubmitSuccessActivity";
    PayReq req;
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    Map<String, String> resultunifiedorder;
    StringBuffer sb;
    //商户PID
    public static final String PARTNER = "2088911663943262";
    private int fee;
    String sign;

    public static final int PAYMENT_ALIPAY = 0;
    public static final int PAYMENT_TENPAY = 1;
    public static final int PAYMENT_UNIONPAY = 2;

    private CheckedTextView mAlipayText;
    private CheckedTextView mTenpayText;
    //    private CheckedTextView mUnionPayText;
//    private String mIsSplitOrder;
    private String mOrderSerialNo;      // 订单号
    private String mOrderId;
    private String mOrderSubject;       // 订单主题(用于subject)
    private String mOrderDescription;   // 订单描述(用于body)
    private String mOrderAmount;        // 订单总金额
    private AQuery aQuery;
    // 支付方式
    private int paymentType;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_submit_success;
    }

    private BroadcastReceiver wx_callback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int code = intent.getIntExtra("code", 0);
            if (code == 1) {
                Intent intent1 = new Intent(SubmitSuccessActivity.this, ServicePaySuccessActivity.class);
                intent1.putExtra("order_id", Integer.parseInt(mOrderId));
                startActivity(intent1);
                finish();
            }
        }
    };

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_shopping_cart));
        setTitleColor_(R.color.master_shopping_cart);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        req = new PayReq();
        sb = new StringBuffer();
        msgApi.registerApp(Constants.APP_ID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wx_callback);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        // 接收传过来的Json字符
        Intent intent = getIntent();
        String orderInfo = intent.getStringExtra("ORDER_INFO");
        try {
            JSONObject orderInfoObj = new JSONObject(orderInfo);
            mOrderId = orderInfoObj.getString("order_id");
            mOrderSerialNo = orderInfoObj.getString("product_sn");
            mOrderAmount = orderInfoObj.getString("order_amount");
            mOrderSubject = orderInfoObj.getString("subject");
            mOrderDescription = orderInfoObj.getString("desc");

            MainActivity.CURRENT_ORDER_TYPE = "service";

        } catch (JSONException e) {
            AppContext.showToast("服务器返回数据异常");
            finish();
        }

        IntentFilter wx_callback_filter = new IntentFilter(Const.WX_CALLBACK);
        registerReceiver(wx_callback, wx_callback_filter);
        aQuery = new AQuery(this);
        mAlipayText = (CheckedTextView) aQuery.id(R.id.payment_alipay).clicked(this).getView();
        mTenpayText = (CheckedTextView) aQuery.id(R.id.payment_tenpay).clicked(this).getView();
        aQuery.id(R.id.order_sn).text("订单号：" + mOrderSerialNo).clicked(this);
        aQuery.id(R.id.next_step_btn).clicked(this);
        aQuery.id(R.id.total_payment).text(String.format("应付金额：%s元", mOrderAmount));
        aQuery.id(R.id.next_step_btn).clickable(false);
        setRecRequest(1);
    }

    @Override
    public void setRecRequest(int currentPage) {
        ServiceApi.getSign(mOrderSerialNo, new Listener<JSONObject>() {
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
            case R.id.order_sn:

                break;
            case R.id.next_step_btn:
                // 付款逻辑
                switch (paymentType) {
                    case PAYMENT_ALIPAY:
                        paymentWithAlipayNew();
                        break;
                    case PAYMENT_TENPAY:
                        ServiceApi.getWXSign(mOrderSerialNo, new Listener<JSONObject>() {
                            @Override
                            public void onPreExecute() {
                                showWaitDialog("处理中");
                            }

                            @Override
                            public void onSuccess(JSONObject response) {
                                Logger.json(response.toString());
                                try {
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
//                        GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
//                        getPrepayId.execute();
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
            Log.e("支付参数_params", params[0]);
            PayTask payTask = new PayTask(SubmitSuccessActivity.this);
            Map<String, String> payResult = payTask.payV2(params[0], true);

            return payResult;
        }

        @Override
        protected void onPostExecute(Map<String, String> payResult) {
            AlipayResult result = new AlipayResult(payResult);
            String resultStatus = result.getResultStatus();

            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                CommonUtil.show(SubmitSuccessActivity.this, "支付成功", 1000);
                Intent intent1 = new Intent(SubmitSuccessActivity.this, ServicePaySuccessActivity.class);
                intent1.putExtra("order_id", Integer.parseInt(mOrderId));
                startActivity(intent1);
                finish();
            } else {    // 判断resultStatus 为非“9000”则代表可能支付失败
                // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    CommonUtil.show(SubmitSuccessActivity.this, "支付结果确认中", 1000);
                } else {
                    CommonUtil.show(SubmitSuccessActivity.this, result.getMemo(), 1000);
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
//        private ProgressDialog dialog;
//
//        @Override
//        protected void onPreExecute() {
//            dialog = ProgressDialog.show(SubmitSuccessActivity.this, "提交申请", "正在处理中...");
//        }
//
//        @Override
//        protected void onPostExecute(Map<String, String> result) {
//            if (dialog != null) {
//                dialog.dismiss();
//            }
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
//
//        try {
//            String nonceStr = genNonceStr();
//            xml.append("</xml>");
//            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
//            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
//            packageParams.add(new BasicNameValuePair("body", "xmb"));//mOrderDescription
//            packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
//            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
//            packageParams.add(new BasicNameValuePair("notify_url", "https://api.xiaomabao.com/payment/wechat_notify"));//回调页面
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
//        if (msgApi.isWXAppInstalled()) {
//            sendPayReq();
//        } else {
//            AppContext.showToast("没有检测到微信，请安装！");
//        }
//    }
//
//    private void sendPayReq() {
//
//        msgApi.registerApp(Constants.APP_ID);
//        msgApi.sendReq(req);
//    }
}