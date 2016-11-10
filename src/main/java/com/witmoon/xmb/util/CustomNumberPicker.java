package com.witmoon.xmb.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.witmoon.xmb.R;

/**
 * Created by Administrator on 2016/6/6.
 */
public class CustomNumberPicker extends NumberPicker
{

    public CustomNumberPicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void addView(View child)
    {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params)
    {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params)
    {
        super.addView(child, params);
        updateView(child);
    }

    public void updateView(View view)
    {
        if (view instanceof EditText)
        {
            //这里修改字体的属性
            ((EditText) view).setTextSize(15.0f);
            ((EditText) view).setTextColor(getResources().getColor(R.color.number_text_color));
        }
    }

}
