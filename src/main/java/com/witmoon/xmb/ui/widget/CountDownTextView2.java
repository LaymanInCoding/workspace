package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 倒计时组件
 * Created by zhyh on 2015/6/6.
 */
public class CountDownTextView2 extends TextView {

    private int mHour, mMinute, mSecond;

    private String mContentTmpl = "剩余%02d时%02d分%02d秒";

    public CountDownTextView2(Context context) {
        this(context, null);
    }

    public CountDownTextView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTextView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private CountDownTimer mCountDownTimer;
    private boolean isTimerStarted;
    private ReentrantLock mLock = new ReentrantLock();

    public void setTime(long time) {
        setTime(time, true);
    }

    public void setTime(long time, boolean startImmediately) {
        init(time);

        mCountDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                computeTime();
                setText(String.format(mContentTmpl, mHour, mMinute, mSecond));
            }
            @Override
            public void onFinish() {

            }
        };
        if (startImmediately) {
            startTimer();
        }
    }

    // 根据给出的时间差(毫秒值)初始化时分秒
    private void init(long time) {
        long second = time / 1000;  // 首先换算成秒
        mHour = (int) (second / (60 * 60));   // 一小时(60 * 60)秒, 计算出小时数

        long sumHourInSeconds = mHour * (60 * 60);   // 秒有可能超出int值范围
        second -= sumHourInSeconds;
        mMinute = (int) (second / 60);        // 一分钟60秒, 计算出分钟数

        mSecond = (int) (second - mMinute * 60);    // 剩余秒数
    }

    public void startTimer() {
        if (mCountDownTimer == null) {
            throw new RuntimeException("启动前请先调用setTime()方法设置倒计时时间.");
        }
        if (isTimerStarted) {
            throw new RuntimeException("已经启动, 不能重复启动.");
        }
        mLock.lock();
        mCountDownTimer.start();
        isTimerStarted = true;
        mLock.unlock();
    }

    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mMinute--;
            mSecond = 59;
            if (mMinute < 0) {
                mHour--;
                mMinute = 59;
            }
        }
    }
}
