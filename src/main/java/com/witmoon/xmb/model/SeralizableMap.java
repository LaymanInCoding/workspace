package com.witmoon.xmb.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by de on 2015/11/18.
 */
public class SeralizableMap implements Serializable {
    public String getmUseCashCouponText() {
        return mUseCashCouponText;
    }

    public void setmUseCashCouponText(String mUseCashCouponText) {
        this.mUseCashCouponText = mUseCashCouponText;
    }

    public String getmCashCouponId() {
        return mCashCouponId;
    }

    public void setmCashCouponId(String mCashCouponId) {
        this.mCashCouponId = mCashCouponId;
    }

    public String mUseCashCouponText;
    public String mCashCouponId;
}
