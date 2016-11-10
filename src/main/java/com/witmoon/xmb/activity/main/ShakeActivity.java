package com.witmoon.xmb.activity.main;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.fragment.ShakeNotifyDialogFragment;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 摇一摇界面
 * 摇一摇功能主要用到加速度传感器：Sensor.TYPE_ACCELEROMETER
 * Created by zhyh on 2015/5/6.
 */
public class ShakeActivity extends BaseActivity implements ShakeNotifyDialogFragment
        .OnNotifyBtnClickListener {

    private ShakeNotifyDialogFragment mNotifyDialogFragment;
    private Timer mTimer = new Timer();
    private ImageView mAvatarImage;
    private TextView mTextView;
    private TextView mTimeText;
    private TextView mSurplusShakeCountText;
    private SensorManager mSensorManager;
    private Vibrator mVibrator;
    // 最后一次摇动时间, 用以防止频繁摇动手机产生混乱
    private long mLastShakeTime;
    // 剩余可摇动次数
    private int mSurplusShakeCount;
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ShakeActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_shake;
    }
    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        AQuery aQuery = new AQuery(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mAvatarImage = aQuery.id(R.id.avatar_img).getImageView();
        mTextView = aQuery.id(R.id.title).getTextView();
        mTimeText = aQuery.id(R.id.time).getTextView();
        mSurplusShakeCountText = aQuery.id(R.id.shake_times_text).getTextView();
        HomeApi.shakeCount(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                
                TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                if (tt.first) {
                    try {
                        mSurplusShakeCount = response.getJSONObject("data").getInt("rest_count");
                        mSurplusShakeCountText.setText(String.valueOf(mSurplusShakeCount));
                    } catch (JSONException e) {
                        mSurplusShakeCount = 0;
                    }
                }
            }
        });
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                HomeApi.newestPrize(mNewestPrizeCallback);
            }
        }, 1000, 4000);
    }
    // 最新中奖信息回调
    private Listener<JSONObject> mNewestPrizeCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (tt.first) try {
                JSONObject dataObj = response.getJSONObject("data");
                Netroid.displayImage(dataObj.getString("header_img"), mAvatarImage);
                mTextView.setText("恭喜" + dataObj.getString("nick_name") + "摇到" + dataObj
                        .getString("type_name"));
                mTimeText.setText(DateFormat.format("yyyy-MM-d HH:mm", Long.parseLong(dataObj
                        .getString("prize_time")) * 1000));
            } catch (JSONException ignored) {
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_kin));
    }

    @Override
    protected String getActionBarTitle() {
        return "摇一摇";
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, sensor, SensorManager
                .SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    // 手机摇动响应, 调用服务器接口
    private void doShakeResponse() {
        // 摇一摇请求
        HomeApi.shake(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                if (!twoTuple.first) {
                    AppContext.showToast(twoTuple.second);
                    return;
                }
                mSurplusShakeCount--;
                mSurplusShakeCountText.setText(String.valueOf(mSurplusShakeCount));
                if (null != mNotifyDialogFragment) {
                    mNotifyDialogFragment.dismiss();
                }
                try {
                    JSONObject dataObj = response.getJSONObject("data");
                    if (dataObj.isNull("type_id")) {
                        mNotifyDialogFragment = ShakeNotifyDialogFragment.newInstance
                                (ShakeNotifyDialogFragment.FAILED, null);
                    } else {
                        HashMap<String, String> paramMap = new HashMap<>();
                        paramMap.put("name", dataObj.getString("type_name"));
                        paramMap.put("endTime", dataObj.getString("use_end_date"));
                        paramMap.put("minMoney", dataObj.getString("min_goods_amount"));
                        mNotifyDialogFragment = ShakeNotifyDialogFragment.newInstance
                                (ShakeNotifyDialogFragment.SUCCESS, paramMap);
                    }
                    mNotifyDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    mNotifyDialogFragment.setOnNotifyBtnClickListener(ShakeActivity.this);
                    mNotifyDialogFragment.show(getSupportFragmentManager(), "SHAKE");
                } catch (JSONException e) {
                    AppContext.showToast("服务器异常");
                }
            }
        });
    }

    // 传感器监听器
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

            if (Math.abs(event.values[0]) > 16 || Math.abs(event.values[1]) > 16 || Math.abs
                    (event.values[2]) > 16) {
                if (System.currentTimeMillis() - mLastShakeTime > 2000) {
                    mVibrator.vibrate(500);
                    doShakeResponse();
                    mLastShakeTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onCheck() {
        UIHelper.showSimpleBack(this, SimpleBackPage.CASH_COUPON);
    }

    @Override
    public void onShare() {
//        mNotifyDialogFragment.dismiss();
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.xiaomabao.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我在小麻包母婴商城摇一摇中奖了, 亲们快来试试吧!");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.xiaomabao.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("小麻包母婴商城摇一摇");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.xiaomabao.com");
        oks.setSilent(true);
        // 启动分享GUI
        oks.show(this);
    }
}
