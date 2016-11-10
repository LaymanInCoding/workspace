package com.witmoon.xmb.activity.common.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.witmoon.xmb.R;
import com.witmoon.xmb.util.DensityUtils;

/**
 * 数量增减对话框
 * Created by zhyh on 2015/6/6.
 */
public class PurchaseQuantityDialogFragment extends DialogFragment {

    private OnInputCompleteListener mOnInputCompleteListener;

    public void setOnInputCompleteListener(OnInputCompleteListener listener) {
        mOnInputCompleteListener = listener;
    }

    private EditText mEditText;
    private String mInitInput;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_purchase_quantity, null);
//        View view = createView(getActivity());
        mEditText = (EditText) view.findViewById(R.id.edit);
        mEditText.setText(mInitInput);
        mEditText.selectAll();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view).setTitle("修改购买数量").setPositiveButton(R.string.text_confirm, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String data = mEditText.getText().toString();
                        if (isInputValid(data)) {
                            if (mOnInputCompleteListener != null) {
                                mOnInputCompleteListener.onInputComplete(data);
                            }
                            dialog.dismiss();
                        }
                    }
                }).setNegativeButton(R.string.text_cancel, null);

        return builder.create();
    }

    // 创建对话框View
    private View createView(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);

        AppCompatEditText editText = new AppCompatEditText(context);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setId(R.id.edit);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setPadding(12, 0, 12, 0);
        editText.setMinWidth(DensityUtils.dp2px(context, 40));
        layout.addView(editText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    // 设置初始值
    public void setInitNumber(int number) {
        this.mInitInput = String.valueOf(number);
    }

    private boolean isInputValid(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        if (!input.matches("\\d+")) {
            return false;
        }
        try {
            return Integer.parseInt(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public interface OnInputCompleteListener {
        void onInputComplete(String data);
    }
}
