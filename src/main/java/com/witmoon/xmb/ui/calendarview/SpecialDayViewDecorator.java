package com.witmoon.xmb.ui.calendarview;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * 指定日期DayViewDecorator
 * 将指定日期背景设置成指定颜色
 * Created by zhyh on 2015/5/14.
 */
public class SpecialDayViewDecorator implements DayViewDecorator {

    private List<CalendarDay> mTargetCalendarDays;
    private int mColor;

    private ShapeDrawable mShapeDrawable;

    public SpecialDayViewDecorator(int color, List<CalendarDay> calendarDays) {
        this.mColor = color;
        this.mTargetCalendarDays = calendarDays;

        mShapeDrawable = new ShapeDrawable(new OvalShape());
        mShapeDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, (float) height, (float) height, mColor,
                        mColor, Shader.TileMode.REPEAT);
            }
        });
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mTargetCalendarDays.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(mShapeDrawable);
    }
}
