package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by zhyh on 2015/5/19.
 */
public class EdgeObserverScrollView extends ScrollView {

    public EdgeObserverScrollView(Context context) {
        super(context);
    }

    public EdgeObserverScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EdgeObserverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private OnEdgeObserverListener mObserverListener;

    private OnScrollListener onScrollListener;
    /**
     * 主要是用在用户手指离开MyScrollView，MyScrollView还在继续滑动，我们用来保存Y的距离，然后做比较
     */
    private int lastScrollY;

    public interface OnEdgeObserverListener {
        void onBottom();
    }

    public void setObserverListener(OnEdgeObserverListener l) {
        this.mObserverListener = l;
    }

    /**
     * 用于用户手指离开MyScrollView的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中
     */
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            int scrollY = EdgeObserverScrollView.this.getScrollY();

            //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
            if (lastScrollY != scrollY) {
                lastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(), 5);
            }
            if (onScrollListener != null) {
                onScrollListener.onScroll(scrollY);
            }

        }
    };

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t + getHeight() + 20 >= computeVerticalScrollRange() && mObserverListener != null) {
            mObserverListener.onBottom();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 重写onTouchEvent， 当用户的手在MyScrollView上面的时候，
     * 直接将MyScrollView滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候，
     * MyScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，在handler处理
     * MyScrollView滑动的距离
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(lastScrollY = this.getScrollY());
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                handler.sendMessageDelayed(handler.obtainMessage(), 5);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         *
         * @param scrollY
         */
        void onScroll(int scrollY);
    }
}
