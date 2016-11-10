package com.witmoon.xmb.activity.me;

import android.util.Log;

import com.witmoon.xmb.R;

/**
 * 订单类型
 * Created by zhyh on 2015/7/29.
 */
public enum OrderType {

    TYPE_CANCELED(0, R.string.text_canceled, R.color.grey),
    TYPE_WAITING_FOR_PAYMENT(1, R.string.text_wait_for_payment, R.color.coral),
    TYPE_WAITING_FOR_SENDING(2, R.string.text_wait_for_send, R.color.master_me),
    TYPE_WAITING_FOR_RECEIVING(3, R.string.text_shipped, R.color.coral),
    TYPE_FINISHED(4, R.string.text_finished, R.color.master_me);

    private int status;
    private int title;
    private int color;

    OrderType(int status, int title, int color) {
        this.status = status;
        this.title = title;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getTitle() {
        return title;
    }

    public static OrderType getType(String type) {

        switch (type) {
            case "canceled":
                return OrderType.TYPE_CANCELED;
            case "await_pay":
                return OrderType.TYPE_WAITING_FOR_PAYMENT;
            case "await_ship":
                return OrderType.TYPE_WAITING_FOR_SENDING;
            case "shipped":
                return OrderType.TYPE_WAITING_FOR_RECEIVING;
            case "finished":
                return OrderType.TYPE_FINISHED;
        }
        return OrderType.TYPE_FINISHED;
    }
}
