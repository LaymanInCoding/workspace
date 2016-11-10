package com.witmoon.xmb.ui.calendarview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * 当前日期DayViewDecorator
 * 只画一个空心圆, 使用了自定义ShapeDrawable
 * Created by zhyh on 2015/5/14.
 */
public class TodayDayViewDecorator implements DayViewDecorator {

    private CalendarDay mCalendarDay;

    private ShapeDrawable mShapeDrawable;

    public TodayDayViewDecorator() {
        mCalendarDay = new CalendarDay();

        mShapeDrawable = new CircleShapeDrawable(new OvalShape());
        mShapeDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, 0, 0, 0, 0, Shader.TileMode.REPEAT);
            }
        });
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mCalendarDay != null && mCalendarDay.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(mShapeDrawable);
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.2f));
    }

    class CircleShapeDrawable extends ShapeDrawable {

        private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public CircleShapeDrawable(Shape shape) {
            super(shape);
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setColor(Color.WHITE);
            mStrokePaint.setStrokeWidth(2);
        }

        public Paint getStrokePaint() {
            return mStrokePaint;
        }

        @Override
        protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
            shape.draw(canvas, mStrokePaint);
        }
    }
}
