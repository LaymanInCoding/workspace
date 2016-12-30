package com.witmoon.xmb.activity.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.bigkoo.pickerview.view.BasePickerView;
import com.witmoon.xmb.R;

import java.util.ArrayList;

/**
 * Created by de on 2016/12/20.
 */
public class BankPickerView<T> extends BasePickerView implements View.OnClickListener {

    WheelBanks mWheelBanks;
    private View btnSubmit, btnCancel;
    private OnOptionsSelectListener optionsSelectListener;


    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";


    public BankPickerView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.bank_picker_layout, contentContainer);
        // -----确定和取消按钮
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        // ----转轮
        final View optionspicker = findViewById(R.id.optionspicker);
        mWheelBanks = new WheelBanks(optionspicker);
    }

    public void setPicker(ArrayList<T> optionsItems) {
        mWheelBanks.setPicker(optionsItems);
    }

    public void setCyclic(boolean tag) {
        mWheelBanks.setCyclic(tag);
    }

    public void setSelectOptions(int option1) {
        mWheelBanks.setCurrentItems(option1);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
            return;
        } else {
            if (optionsSelectListener != null) {
                int optionsCurrentItems = mWheelBanks.getCurrentItems();
                optionsSelectListener.onOptionsSelect(optionsCurrentItems);
            }
            dismiss();
            return;
        }
    }

    public interface OnOptionsSelectListener {
        public void onOptionsSelect(int index);
    }

    public void setOnoptionsSelectListener(
            OnOptionsSelectListener optionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener;
    }
}
