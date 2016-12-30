package com.witmoon.xmb.activity.service;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.CheckedTextView;

import com.alipay.sdk.app.PayTask;
import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.OrderType;
import com.witmoon.xmb.activity.me.fragment.OrderDetailFragment;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.alipay.AlipayResult;
import com.witmoon.xmb.api.alipay.SignUtils;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.wxapi.simcpux.Constants;
import com.witmoon.xmb.wxapi.simcpux.MD5;
import com.witmoon.xmb.wxapi.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    //商户收款账号
    public static final String SELLER = "liulianqi@xiaomabao.com";
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATEOR = "MIICXQIBAAKBgQCyRddjMMtmtp6XIsrjwbf9DuwmdqBx4ODqmiRyON935ZAYeAbau1kPTF0N1NQmDzp62uTDeBfsk7AMViVgCLlFrrvMlLkxmqtmEuMU/" +
            "LHBQu1F9lu15n9KaZrmK7LSzlr/Xw6Ljfhp8AL1T0EolzyGvHF30sgZ9e8pOFPjGeHL0wIDAQABAoGABSx9ucU6wfpe0+" +
            "gQl1eR7Wg3dk5PDb8HCAf9MstvwN0Kt4sUN9jkFsuSj8ozdf9PJS2hIgMkPptyqoj9gLIAHncyjcich6Uc8t0Blebj502qdzQZPBKUffTFY8uVuL41QcOIqG9DL28wdPLzDCgXrK+wuaRi2iWEV" +
            "+mscjokXhECQQDab/KaNbjveOlhsNlPq4FbwugB1F3wsbDhg4WKa+" +
            "Qk5lXjeM6fjHbM/CLTZ7rNCL4Zy0sGai0ZcPqCAcB9fLB1AkEA0O3FoehiGtTP4MhRr3kq9s9Df6QfuW2yIIqDw0KUwwdf4yqawT+" +
            "1cRtDFdOgg22OCY8dTgzTxuRISHFf6DoCJwJBAMSV/YtFkBONCNTPmTO3USspJS4IVyb4dAzxFez2npOK7H9UyPgULRLcU+dYsmg2VwfVysaSJdaAtCLuurL01cUCQEI5K6yfQQFqNR31wNaS3Ihwt99sgVXALHbeENfCce7DlqQlq/nXHMbmMkRn2PfvsNbc0xgbPsFLOJIlxZWRVX8CQQCLWnk" +
            "+mf+NhHRk0XOYoP9Gl7Swa0jJunj4WdEBBNbGl3Q98N1rJDyON0QhA3C4t5i85BURJJWGeniMEHKkQ0Pg";
    public static final String RSA_PRIVATE = "MIICWwIBAAKBgQDd93uBF65oHar1x8Md24n1vr+exqJRKx5lrgxe7xp9pB/F" +
            "/DOeoYHfSAHpS7Ej1DGqa3Yrd13G3SHdCa2kM68krM4iIYXwpIrv5YlFJ7ySO2uqNj4kaaaTmXO3XdY3I0znMgfWrtk/0ParrCT4zCIuiIVQMcCExcXPBvSGB5o8PQIDAQABAoGAJFWmufMOcaypl6IjpneRldCrvWn2mCLezvY2wDOTuo1rRhWKvaTihZJb/byOOrc5ihQdO/BGQY9hQ7WZ3w821BQBtu8WcrO93mM/ymwSPQRDar3o/m0PBm0iwgR1ylukDmFRsdQbkSguoRZ6qQpO+CMy/UJUevvO/pTNqpkBSQECQQD1lne+op0H1KdhSCdsqP4RADtwUs50YGlRwyophbDt+aG3H8jooyRh3C9ZIxigGfYky+zIXu+VzC8cwoRS0/ZVAkEA52Cjr4CWYC6xGrqV6DWIjnEu+uamkLq5KqTtPzNYKqwvBCnXCal6ORVNDKp0MZFrsMYq9/GWU2+LlqtvUNcGSQJAOzFNI1Gmb6C9xyDQ/8urkQVxTh1nTX1/ZQmZO/DROEqWw8CReCD6P+wGLYHcCZq4TR4psBHRxVU1dcUch6o3EQJAI5AF+J6h/kyO6hXNnrBDuekJmITFCy+38CcDVBh3L6/hVhl8NL74yzdL7WtxioDYdYYv6oYWXeKiKNlWta0nCQJAOb2bhcBCRzEJX96dfBipxsndkESRNDw0j/cAe9+ku1YeTxLGkANltjfUGBz4yVOr+DcGr93QkHm9+dMZJjxpkA==";

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
        ;
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
        AQuery aQuery = new AQuery(this);
        mAlipayText = (CheckedTextView) aQuery.id(R.id.payment_alipay).clicked(this).getView();
        mTenpayText = (CheckedTextView) aQuery.id(R.id.payment_tenpay).clicked(this).getView();
        aQuery.id(R.id.order_sn).text("订单号：" + mOrderSerialNo).clicked(this);
        aQuery.id(R.id.next_step_btn).clicked(this);
        aQuery.id(R.id.total_payment).text(String.format("应付金额：%s元", mOrderAmount));
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
                Bundle bundle = new Bundle();
                bundle.putString(OrderDetailFragment.KEY_ORDER_SN,mOrderSerialNo);
                bundle.putString(OrderDetailFragment.KEY_ORDER_TYPE, OrderType
                        .TYPE_WAITING_FOR_PAYMENT.name());
                UIHelper.showSimpleBack(this, SimpleBackPage.ORDER_DETAIL, bundle);
                break;
            case R.id.next_step_btn:
                // 付款逻辑
                switch (paymentType) {
                    case PAYMENT_ALIPAY:
                        paymentWithAlipay(buildPaymentParam());
                        break;
                    case PAYMENT_TENPAY:
                        GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
                        getPrepayId.execute();
                        break;
                    default:
                        AppContext.showToast("该支付方式暂未实现");
                }
                break;
        }
    }

    // 构建付款请求参数
    public String buildPaymentParam() {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + "zfb@xiaomabao.com" + "\"";

        // 商户网站唯一订单号

        orderInfo += "&out_trade_no=" + "\"" + mOrderSerialNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + mOrderSubject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + mOrderDescription + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + mOrderAmount + "\"";

        // 服务器异步通知页面路径

        orderInfo += "&notify_url=" + "\"" + "https://api.xiaomabao.com/payment/alipay_notify" + "\"";


        // 服务接口名称, 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型, 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码, 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间, 默认30分钟, 一旦超时, 该笔交易就会自动被关闭
        // 取值范围：1m～15d, 该参数数值不接受小数点，如1.5h, 可转换为90m
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）
        orderInfo += "&it_b_pay=\"30m\"";

        // 支付宝处理完请求后,当前页面跳转到商户指定页面的路径,可空
//        orderInfo += "&return_url=m.alipay.com";

        orderInfo += "&return_url=\"m.alipay.com\"";

        return orderInfo;
    }

    //    构建付款请求参数
    public String buildPaymentParamOR() {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + "2088021773439893" + "\"";

//        orderInfo += "&currency="+"USD";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + mOrderSerialNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + mOrderSubject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + mOrderDescription + "\"";

        // 商品金额
        orderInfo += "&rmb_fee=" + "\"" + mOrderAmount + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" +
                "https://api.xiaomabao.com/interalipay/notify_url.php" + "\"";

        // 服务接口名称, 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型, 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码, 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间, 默认30分钟, 一旦超时, 该笔交易就会自动被关闭
        // 取值范围：1m～15d, 该参数数值不接受小数点，如1.5h, 可转换为90m
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）
        orderInfo += "&it_b_pay=\"30m\"";

//        // 支付宝处理完请求后,当前页面跳转到商户指定页面的路径,可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        orderInfo += "&forex_biz=\"FP\"";

        orderInfo += "&product_code=\"NEW_WAP_OVERSEAS_SELLER\"";

        orderInfo += "&currency=\"USD\"";

        orderInfo += "&split_fund_info=" + "\"[{transIn:\"2088911663943262\",amount:\"0.0\",currency:\"CNY\",desc:\"\"}]\"";

        return orderInfo;
    }

    // 通过支付宝付款
    private void paymentWithAlipay(String param) {
        sign = SignUtils.sign(param, RSA_PRIVATE);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
            AppContext.showToast("程序异常, 无法完成支付");
            return;
        }
        String finalParam = param + "&sign=" + "\"" + sign + "\"" + "&sign_type=\"RSA\"";
        PaymentAsyncTask paymentAsyncTask = new PaymentAsyncTask();
        paymentAsyncTask.execute(finalParam);
        Log.e("finalParam", finalParam);
    }

    // 重置支付方式选择状态
    private void resetCheckedState() {
        mAlipayText.setChecked(false);
        mTenpayText.setChecked(false);
//        mUnionPayText.setChecked(false);
    }

    // 支付异步工作类
    private class PaymentAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.e("支付参数_params", params[0]);
            PayTask payTask = new PayTask(SubmitSuccessActivity.this);
            String payResult = payTask.pay(params[0]);
            hideWaitDialog();

            return payResult;
        }

        @Override
        protected void onPostExecute(String payResult) {
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
            hideWaitDialog();
        }
    }

    /**
     * 生成签名
     */
    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);

        this.sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");


            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(SubmitSuccessActivity.this, "提交申请", "正在处理中...");
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");

            resultunifiedorder = result;

            genPayReq();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = genProductArgs();

            Log.e("orion", entity);

            byte[] buf = Util.httpPost(url, entity);

            String content = new String(buf);
            Log.e("orion", content);
            Map<String, String> xml = decodeXml(content);

            return xml;
        }
    }

    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }


    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }


    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }


    //
    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            String nonceStr = genNonceStr();
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
            packageParams.add(new BasicNameValuePair("body", "xmb"));//mOrderDescription
            packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", "https://api.xiaomabao.com/payment/wechat_notify"));//回调页面
            packageParams.add(new BasicNameValuePair("out_trade_no", mOrderSerialNo));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
            packageParams.add(new BasicNameValuePair("total_fee", (int) (Double.valueOf(mOrderAmount) * 100) + ""));//金额转换Float.parseFloat(mOrderAmount)*100+" "(int)Float.parseFloat(mOrderAmount)*100+
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));
            String xmlstring = toXml(packageParams);

            return xmlstring;

        } catch (Exception e) {
            return null;
        }
    }

    private void genPayReq() {
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = resultunifiedorder.get("prepay_id");
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);

        sb.append("sign\n" + req.sign + "\n\n");

        if (msgApi.isWXAppInstalled()) {
            sendPayReq();
        } else {
            AppContext.showToast("没有检测到微信，请安装！");
        }
    }

    private void sendPayReq() {

        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
    }
}