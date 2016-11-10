package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by zhyh on 2015/4/30.
 */
public class InputAreaLinearLayout extends LinearLayout {

    public InputAreaLinearLayout(Context context) {
        this(context, null);
    }

    public InputAreaLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputAreaLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int c = getChildCount();
        for (int i = 0; i < c; i++) {
            View view = getChildAt(i);
            if (view instanceof EditText) {
                view.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        setPressed(hasFocus);
                    }
                });
            }
        }
    }
}
