package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.util.TimeUtill;

/**
 * Created by de on 2016/1/14
 */
public class TimeView extends TextView implements Runnable {

    private int position;
    private int isType;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        只有创建该View才会走Run（）方法，所以放到setPosition（）该方法里。
//        postDelayed(this, 1000);
    }

    public void setPosition(int position, int isType) {
        this.position = position;
        this.isType = isType;
        postDelayed(this, 1000);
    }

    @Override
    public void run() {
        //        判断根据不同的集合取值
        if (null != TimeUtill.list)
            if (isType == 1) {
                //单品团
                if (TimeUtill.list.size() > 0) {
                    String time = TimeUtill.get(position, "time");
                    if (!"售完".equals(time))
                        setText(init(Long.parseLong(time) * 1000));
                    if (!"售完".equals(time)) {
                        postDelayed(this, 1000);
                    } else {
                        setText("已结束");
                    }
                }
            } else if (isType == 2) {
                //品牌团
                if (TimeUtill.mlist.size() > 0) {
                    String time = TimeUtill.mGet(position, "time");
                    if (!"售完".equals(time))
                        setText(init(Long.parseLong(time) * 1000));
                    if (!"售完".equals(time)) {
                        postDelayed(this, 1000);
                    } else {
                        setText("已结束");
                    }
                }
            } else {
                if (TimeUtill.byeLit.size() > 0) {
                    String time = TimeUtill.byeGet(position, "time");
                    if (!"售完".equals(time))
                        setText(init(Long.parseLong(time) * 1000));
                    if (!"售完".equals(time)) {
                        postDelayed(this, 1000);
                    } else {
                        setText("已结束");
                    }
                }
            }
    }

    private String init(long time) {
        long second = time / 1000;  // 首先换算成秒
        int mDay = (int) (second / (60 * 60 * 24));   // 一天(60 * 60 * 24)秒, 计算出天数

        long sumDayInSeconds = mDay * (60 * 60 * 24);   // 天数多了秒有可能超出int值范围
        second -= sumDayInSeconds;
        int mHour = (int) (second / (60 * 60));   // 一小时(60 * 60)秒, 计算出小时数

        second = second - mHour * (60 * 60);
        int mMinute = (int) (second / 60);        // 一分钟60秒, 计算出分钟数

        int mSecond = (int) (second - mMinute * 60);    // 剩余秒数
        return "剩余时间:" + mDay + "天" + mHour + "时" + mMinute + "分" + mSecond + "秒";
    }

    public TimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context) {
        this(context, null);
    }
}