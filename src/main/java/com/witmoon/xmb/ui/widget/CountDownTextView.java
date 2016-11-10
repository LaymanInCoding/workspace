package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.util.DensityUtils;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 倒计时组件
 * Created by zhyh on 2015/6/6.
 */
public class CountDownTextView extends LinearLayout {

    private int mDay, mHour, mMinute, mSecond;
    private TextView mDayTextView;
    private TextView mHourTextView;
    private TextView mMinuteTextView;
    private TextView mSecondTextView;

    private int mDecimalBackground = -1;
    private int mTextSize = 12;
    private int mTextColor = 0x000000;
    private int mDecimalTextColor = 0x000000;
    private int mHorizontalPadding = 2;
    private int mVerticalPadding = 1;
    private int mDecimalHorizontalPadding = 4;
    private int mDecimalVerticalPadding = 1;

    public CountDownTextView(Context context) {
        this(context, null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getChildCount() > 0) {
            throw new RuntimeException("CountDownTextView不允许含有子组件.");
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .CountDownTextView);
        mDecimalBackground = typedArray.getResourceId(R.styleable
                .CountDownTextView_decimalBackground, android.R.color.transparent);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable
                .CountDownTextView_android_textSize, DensityUtils.sp2px(context, mTextSize));
        mTextColor = typedArray.getColor(R.styleable.CountDownTextView_android_textColor,
                mTextColor);
        mHorizontalPadding = typedArray.getDimensionPixelSize(R.styleable
                .CountDownTextView_cdHorizontalPadding, DensityUtils.dp2px(context,
                mHorizontalPadding));
        mDecimalTextColor = typedArray.getColor(R.styleable.CountDownTextView_decimalTextColor,
                mDecimalTextColor);
        mVerticalPadding = typedArray.getDimensionPixelSize(R.styleable
                .CountDownTextView_cdVerticalPadding, DensityUtils.dp2px(context,
                mVerticalPadding));
        mDecimalHorizontalPadding = typedArray.getDimensionPixelSize(R.styleable
                .CountDownTextView_decimalHorizontalPadding, DensityUtils.dp2px(context,
                mDecimalHorizontalPadding));
        mDecimalVerticalPadding = typedArray.getDimensionPixelSize(R.styleable
                .CountDownTextView_decimalVerticalPadding, DensityUtils.dp2px(context,
                mDecimalVerticalPadding));
        typedArray.recycle();

        initView(context);
    }

    private void initView(Context context) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDayTextView = createTextView(context, "00", true);
        addView(mDayTextView, layoutParams);

        addView(createTextView(context, "天", false), layoutParams);

        mHourTextView = createTextView(context, "00", true);
        addView(mHourTextView, layoutParams);

        addView(createTextView(context, "时", false), layoutParams);

        mMinuteTextView = createTextView(context, "00", true);
        addView(mMinuteTextView, layoutParams);

        addView(createTextView(context, "分", false), layoutParams);

        mSecondTextView = createTextView(context, "00", true);
        addView(mSecondTextView, layoutParams);

        TextView secondText = createTextView(context, "秒", false);
        secondText.setPadding(secondText.getPaddingLeft(), secondText.getPaddingTop(), 0,
                secondText.getPaddingBottom());
        addView(secondText, layoutParams);
    }

    private TextView createTextView(Context context, String data, boolean isDecimal) {
        TextView textView = new TextView(context);
        textView.setText(data);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        if (isDecimal) {
            textView.setPadding(mDecimalHorizontalPadding, mDecimalVerticalPadding,
                    mDecimalHorizontalPadding, mDecimalVerticalPadding);
            textView.setBackgroundResource(mDecimalBackground);
            textView.setTextColor(mDecimalTextColor);
            textView.getPaint().setFakeBoldText(true);
        } else {
            textView.setPadding(mHorizontalPadding, mVerticalPadding, mHorizontalPadding,
                    mVerticalPadding);
            textView.setTextColor(mTextColor);
        }
        return textView;
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
                mSecondTextView.setText(String.format("%02d", mSecond));
                mMinuteTextView.setText(String.format("%02d", mMinute));
                mHourTextView.setText(String.format("%02d", mHour));
                mDayTextView.setText(String.format("%02d", mDay));
            }

            @Override
            public void onFinish() {

            }
        };
        if (startImmediately) {
            startTimer();
        }
    }

    // 根据给出的时间差(毫秒值)初始化天时分秒
    private void init(long time) {
        long second = time / 1000;  // 首先换算成秒
        mDay = (int) (second / (60 * 60 * 24));   // 一天(60 * 60 * 24)秒, 计算出天数

        long sumDayInSeconds = mDay * (60 * 60 * 24);   // 天数多了秒有可能超出int值范围
        second -= sumDayInSeconds;
        mHour = (int) (second / (60 * 60));   // 一小时(60 * 60)秒, 计算出小时数

        second = second - mHour * (60 * 60);
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
                if (mHour < 0) {
                    mDay--;
                    mHour = 23;
                }
            }
        }
    }
}
