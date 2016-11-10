package com.witmoon.xmb.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by ZCM on 2016/1/25.
 */
public class MyListView extends ListView {
    public MyListView(Context context, AttributeSet attrs) {
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
