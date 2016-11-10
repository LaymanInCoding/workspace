package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import com.witmoon.xmb.R;

/**
 * 可以自动换行的RadioGroup
 * Created by zhyh on 2015/6/4.
 */
public class LineFeedHorizontalLayout extends RadioGroup {

    private int hSpace;
    private int vSpace;

    public LineFeedHorizontalLayout(Context context) {
        this(context, null);
    }

    public LineFeedHorizontalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineFeedHorizontalLayout);
        hSpace = ta.getDimensionPixelSize(R.styleable.LineFeedHorizontalLayout_horizontalSpace, 8);
        vSpace = ta.getDimensionPixelSize(R.styleable.LineFeedHorizontalLayout_verticalSpace, 4);
        ta.recycle();
    }

    public void setHorizontalSpace(int size) {
        this.hSpace = size;
    }

    public void setVerticalSpace(int size) {
        this.vSpace = size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int paddingLeft = getPaddingLeft(), paddingRight = getPaddingRight();
        int contentWidth = w - paddingLeft - paddingRight;
        int widthSum = 0, heightSum = 0;
        int maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();
            widthSum = widthSum + cWidth + hSpace;
            // 取本行最高的子View高度
            if (cHeight > maxHeight) {
                maxHeight = cHeight;
            }
            // 需要换行显示
            if (widthSum > contentWidth) {
                widthSum = cWidth + hSpace;
                heightSum = heightSum + maxHeight + vSpace;
            }
        }
        heightSum = heightSum + maxHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(wMode == MeasureSpec.EXACTLY ? w : widthSum, hMode == MeasureSpec
                .EXACTLY ? h : heightSum);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft(), paddingRight = getPaddingRight(), paddingTop =
                getPaddingTop();
        int childCount = getChildCount();

        int leftSpace = paddingLeft;
        int topSpace = paddingTop;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();

            // 需要换行显示, 重置
            if (leftSpace + w + paddingLeft + paddingRight > r) {
                leftSpace = paddingLeft;
                topSpace = topSpace + h + vSpace;
            }
            child.layout(leftSpace, topSpace, leftSpace + w, topSpace + h);
            leftSpace = leftSpace + w + hSpace;
        }
    }
}
