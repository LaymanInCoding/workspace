package com.witmoon.xmb.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.witmoon.xmb.R;

/**
 * 排序用TextView
 * Created by zhyh on 2015-07-13.
 */
public class SortTextView extends TextView {

    public static final String SORT_TYPE_NONE = "none";
    public static final String SORT_TYPE_ASC = "asc";
    public static final String SORT_TYPE_DESC = "desc";

    private String mSortType = SORT_TYPE_NONE;
    private String mSortColumn;
    private Drawable mAscLeftDrawable;
    private Drawable mDescLeftDrawable;
    private int mSortEnableTextColor = 0xFFFF0000;

    private int mAscLeftResId = R.mipmap.icon_order_asc;
    private int mDescLeftResId = R.mipmap.icon_order_desc;

    public SortTextView(Context context) {
        this(context, null);
    }

    public SortTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SortTextView);
        mSortColumn = ta.getString(R.styleable.SortTextView_sortColumn);
        mSortEnableTextColor = ta.getColor(R.styleable.SortTextView_sortEnableTextColor,
                mSortEnableTextColor);
//        mAscLeftDrawable = ta.getDrawable(R.styleable.SortTextView_ascLeftDrawable);
//        mDescLeftDrawable = ta.getDrawable(R.styleable.SortTextView_descLeftDrawable);
        mAscLeftResId = ta.getResourceId(R.styleable.SortTextView_ascLeftDrawable, mAscLeftResId);
        mDescLeftResId = ta.getResourceId(R.styleable.SortTextView_descLeftDrawable,
                mDescLeftResId);

        ta.recycle();

        mAscLeftDrawable = getResources().getDrawable(mAscLeftResId);
        mDescLeftDrawable = getResources().getDrawable(mDescLeftResId);
        setCompoundDrawablePadding(10);
    }

    public String getSortType() {
        return mSortType;
    }

    public String getSortColumn() {
        return mSortColumn;
    }

    public void setSortType(String sortType) {
        mSortType = sortType;
        refresh();
    }

    private void refresh() {
        switch (mSortType) {
            case SORT_TYPE_NONE:
                setCompoundDrawables(null, null, null, null);
                setTextColor(0xFF000000);
                break;
            case SORT_TYPE_ASC:
                if (mAscLeftDrawable != null) {
                    mAscLeftDrawable.setBounds(0, 0, mAscLeftDrawable.getMinimumWidth(),
                            mAscLeftDrawable.getMinimumHeight());
                    setCompoundDrawables(mAscLeftDrawable, null, null, null);
                }
                setTextColor(mSortEnableTextColor);
                break;
            case SORT_TYPE_DESC:
                if (mDescLeftDrawable != null) {
                    mDescLeftDrawable.setBounds(0, 0, mDescLeftDrawable.getMinimumWidth(),
                            mDescLeftDrawable.getMinimumHeight());
                    setCompoundDrawables(mDescLeftDrawable, null, null, null);
                }
                setTextColor(mSortEnableTextColor);
                break;
        }
    }

    public void toggle() {
        switch (mSortType) {
            case SORT_TYPE_NONE:
            case SORT_TYPE_DESC:
                mSortType = SORT_TYPE_ASC;
                break;
            case SORT_TYPE_ASC:
                mSortType = SORT_TYPE_DESC;
                break;
        }
        refresh();
    }
}
