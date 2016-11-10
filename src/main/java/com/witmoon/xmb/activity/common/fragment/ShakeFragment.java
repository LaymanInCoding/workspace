package com.witmoon.xmb.activity.common.fragment;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONObject;

/**
 * Created by zhyh on 2015/7/28.
 */
public class ShakeFragment extends BaseFragment {
    private ShakeNotifyDialogFragment mNotifyDialogFragment;
    private TextView mSurplusShakeCountText;

    private SensorManager mSensorManager;
    private Vibrator mVibrator;

    // 最后一次摇动时间, 用以防止频繁摇动手机产生混乱
    private long mLastShakeTime;
    // 剩余可摇动次数
    private int mSurplusShakeCount;

    // 传感器监听器
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

            if (Math.abs(event.values[0]) > 15 || Math.abs(event.values[1]) > 15 || Math.abs
                    (event.values[2]) > 15) {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shake, container, false);

        mSensorManager = (SensorManager) container.getContext().getSystemService(Context
                .SENSOR_SERVICE);
        mVibrator = (Vibrator) container.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        mSurplusShakeCountText = (TextView) view.findViewById(R.id.shake_times_text);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, sensor, SensorManager
                .SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    // 手机摇动响应, 调用服务器接口
    private void doShakeResponse() {

    }

    private Listener<JSONObject> mShakeCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            if (null != mNotifyDialogFragment && mNotifyDialogFragment.isVisible()) {
                mNotifyDialogFragment.dismiss();
            }
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToast(tt.second);
                return;
            }
//            mSurplusShakeCount--;
//
//            mNotifyDialogFragment = ShakeNotifyDialogFragment
//                    .newInstance(ShakeNotifyDialogFragment.SUCCESS, "");
//            mNotifyDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//            mNotifyDialogFragment.setOnNotifyBtnClickListener();
//            mNotifyDialogFragment.show(getFragmentManager(), "SHAKE");
        }
    };

    // 更新剩余可摇动次数显示
    private void updateSurplusShakeCountText() {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.format(getString(R
                .string.text_shake_times), mSurplusShakeCount));
        stringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 8, 9, Spannable
                .SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new RelativeSizeSpan(2), 8, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSurplusShakeCountText.setText(stringBuilder);
    }
}
