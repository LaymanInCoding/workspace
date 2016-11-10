package com.witmoon.xmb.ui.popupwindow;

import android.view.View;
import android.widget.AutoCompleteTextView;

/**
 * Created by de on 2015/12/2.
 */
public class Popup {
    private int xPos; //弹出窗口的x方向位置
    private int yPos; //弹出窗口的y方向位置
    private int vWidth; //窗口显示内容的视图宽度
    private int vHeight; //窗口显示内容的视图高度
    private int animFadeInOut; //窗口显示动画
    private int contentView; //潜入在窗口的视图
    private View customView; //潜入的窗口视图view
    private boolean isClickable; //视图外部是否可以点击
    private AutoCompleteTextView.OnDismissListener listener; //监听弹窗是否dismiss
    private View.OnTouchListener touchListener; //监听触摸位置
    private float bgAlpha; //背景遮罩的透明度

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int ypos) {
        this.yPos = ypos;
    }

    public int getvWidth() {
        return vWidth;
    }

    public void setvWidth(int vWidth) {
        this.vWidth = vWidth;
    }

    public int getvHeight() {
        return vHeight;
    }

    public void setvHeight(int vHeight) {
        this.vHeight = vHeight;
    }

    public int getAnimFadeInOut() {
        return animFadeInOut;
    }

    public void setAnimFadeInOut(int animFadeInOut) {
        this.animFadeInOut = animFadeInOut;
    }

    public int getContentView() {
        return contentView;
    }

    public void setContentView(int contentView) {
        this.contentView = contentView;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    public View getCustomView() {
        return customView;
    }

    public void setCustomView(View customView) {
        this.customView = customView;
    }

    public AutoCompleteTextView.OnDismissListener getListener() {
        return listener;
    }

    public void setListener(AutoCompleteTextView.OnDismissListener listener) {
        this.listener = listener;
    }

    public float getBgAlpha() {
        return bgAlpha;
    }

    public void setBgAlpha(float bgAlpha) {
        this.bgAlpha = bgAlpha;
    }

    public View.OnTouchListener getTouchListener() {
        return touchListener;
    }

    public void setTouchListener(View.OnTouchListener touchListener) {
        this.touchListener = touchListener;
    }
}