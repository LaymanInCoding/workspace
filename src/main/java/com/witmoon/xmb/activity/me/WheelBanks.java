package com.witmoon.xmb.activity.me;

import android.view.View;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import java.util.ArrayList;

/**
 * Created by de on 2016/12/20.
 */
public class WheelBanks<T> {

    private View view;
    private WheelView wv_bank;

    private ArrayList<T> mBankItems;
    private OnItemSelectedListener wheelListener_option1;

    public interface OnItemSelectedListener {
        void onItemSelected(int index);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public WheelBanks(View view) {
        super();
        this.view = view;
        setView(view);
    }

    public void setPicker(ArrayList<T> bankItems){
        this.mBankItems = bankItems;
        int len = 8;
        wv_bank = (WheelView) view.findViewById(com.bigkoo.pickerview.R.id.options1);
        wv_bank.setAdapter(new ArrayWheelAdapter(mBankItems, len));// 设置显示数据
        wv_bank.setCurrentItem(0);// 初始化时显示的数据
    }
    public void setCyclic(boolean cyclic) {
        wv_bank.setCyclic(cyclic);

    }
    /**
     * 返回当前选中的结果对应的位置数组
     *
     * @return
     */
    public int getCurrentItems() {
        return wv_bank.getCurrentItem();
    }
    public void setCurrentItems(int option1) {
        wv_bank.setCurrentItem(option1);
    }
}
