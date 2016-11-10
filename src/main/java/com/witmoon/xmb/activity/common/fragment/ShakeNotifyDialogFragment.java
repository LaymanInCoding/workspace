package com.witmoon.xmb.activity.common.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.witmoon.xmb.R;

import java.util.HashMap;

/**
 * 摇一摇弹出对话框
 * Created by zhyh on 2015/5/13.
 */
public class ShakeNotifyDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    private int mType = FAILED;
    private HashMap<String, String> mParamMap;

    private OnNotifyBtnClickListener mOnNotifyBtnClickListener;

    public interface OnNotifyBtnClickListener {
        void onCheck();

        void onShare();
    }

    public static ShakeNotifyDialogFragment newInstance(int type,
                                                        HashMap<String, String> paramMap) {
        ShakeNotifyDialogFragment fragment = new ShakeNotifyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putSerializable("content", paramMap);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnNotifyBtnClickListener(OnNotifyBtnClickListener listener) {
        mOnNotifyBtnClickListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mType = args.getInt("type");
        if (args.getSerializable("content") != null) {
            mParamMap = (HashMap<String, String>) args.getSerializable("content");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_shake_result, container, false);
        AQuery aQuery = new AQuery(getActivity(), view);

        if (mType == FAILED) {
            aQuery.id(R.id.shake_result_again_btn).clicked(this);
        } else {
            aQuery.id(R.id.shake_result_again_btn).gone();
            aQuery.id(R.id.shake_result_button_layout).visible();
            aQuery.id(R.id.shake_result_share_btn).clicked(this);
            aQuery.id(R.id.shake_result_check_btn).clicked(this);
            aQuery.id(R.id.shake_result_title).text("哇！真棒呢~");
            aQuery.id(R.id.shake_result_name).text("摇到" + mParamMap.get("name"));
            aQuery.id(R.id.shake_result_content).text(String.format("满%1$s可用, 有效期至%2$s",
                    mParamMap.get("minMoney"), DateFormat.format("yyyy年MM月dd", Long
                            .parseLong(mParamMap.get("endTime")) * 1000)));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shake_result_again_btn:
                this.dismiss();
                break;
            case R.id.shake_result_check_btn:
                if (mOnNotifyBtnClickListener != null) {
                    mOnNotifyBtnClickListener.onCheck();
                }
                break;
            case R.id.shake_result_share_btn:
                if (mOnNotifyBtnClickListener != null) {
                    mOnNotifyBtnClickListener.onShare();
                }
                break;
        }
    }
}
