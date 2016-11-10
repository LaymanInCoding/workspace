package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 字母索引控件
 * Created by zhyh on 2015/5/17.
 */
public class AlphabetIndexSlideBar extends View {
    private Paint paint = new Paint();
    private OnTouchLetterChangeListener mListener;

    // 准备好的A~Z的字母数组
    public static final String[] letters = {"","#", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    // 选中的项
    private int choose = -1;
    private boolean showBg;

    private float mTextSize;

    public AlphabetIndexSlideBar(Context context) {
        this(context, null);
    }

    public AlphabetIndexSlideBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AlphabetIndexSlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        float ratioWidth = (float) screenWidth / 480;
        float ratioHeight = (float) screenHeight / 800;

        mTextSize = Math.round(22 * Math.min(ratioWidth, ratioHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        int width = getWidth();
        int height = getHeight();

        // 画出背景
        if (showBg) {
            canvas.drawColor(Color.parseColor("#55000000"));
        }

        // 每个字母的高度
        int singleHeight = height / letters.length;
        // 画字母
        for (int i = 0; i < letters.length; i++) {
            // 设置字体格式
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(mTextSize);
            // 如果这一项被选中，则换一种颜色画
            if (i == choose) {
                paint.setColor(Color.parseColor("#F88701"));
                paint.setFakeBoldText(true);
            }
            // 要画的字母的x,y坐标
            float posX = width / 2 - paint.measureText(letters[i]) / 2;
            float posY = i * singleHeight + singleHeight;

            // 画出字母
            canvas.drawText(letters[i], posX, posY, paint);
            // 重新设置画笔
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        // 算出点击的字母的索引
        final int index = (int) (y / getHeight() * letters.length);

        // 保存上次点击的字母的索引到oldChoose
        final int oldChoose = choose;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showBg = true;
                if (oldChoose != index && mListener != null && index > 0
                        && index < letters.length) {
                    choose = index;
                    mListener.onTouchLetterChange(event, letters[index]);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != index && mListener != null && index > 0
                        && index < letters.length) {
                    choose = index;
                    mListener.onTouchLetterChange(event, letters[index]);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                showBg = false;
                choose = -1;
                if (mListener != null && index > 0 && index < letters.length)
                    mListener.onTouchLetterChange(event, letters[index]);
                invalidate();
                break;
        }

        return true;
    }

    public void setOnTouchLetterChangeListenner(OnTouchLetterChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnTouchLetterChangeListener {
        void onTouchLetterChange(MotionEvent event, String s);
    }
}
