package com.witmoon.xmb.ui;

import android.widget.GridView;

/**
 * Created by de on 2015/12/29.
 */
public class MyGridView extends GridView {
    public MyGridView(android.content.Context context,
                      android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
