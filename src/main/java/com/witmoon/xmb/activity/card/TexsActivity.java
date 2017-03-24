package com.witmoon.xmb.activity.card;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.witmoon.xmb.model.ElecCardBean;

/**
 * Created by ming on 2017/3/24.
 */
public class TexsActivity extends AppCompatActivity {

    static volatile TexsActivity defaultInstance;

    public static TexsActivity getDefaultInstance() {
        if (defaultInstance == null) {
            synchronized (TexsActivity.class) {
                if (defaultInstance == null) {
                    defaultInstance = new TexsActivity();
                }
            }
        }
        return defaultInstance;
    }

    private final ThreadLocal<ElecCardBean> mCurrentThreadLocal = new ThreadLocal<ElecCardBean>() {
        @Override
        protected ElecCardBean initialValue() {
            return super.initialValue();
        }
    };

    protected void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        ElecCardBean bean1 = new ElecCardBean();
        bean1.setOrder_sn("bean1");
        ElecCardBean bean2 = new ElecCardBean();
        bean2.setOrder_sn("bean2");
        mCurrentThreadLocal.set(bean1);
        mCurrentThreadLocal.set(bean2);
        ElecCardBean beans = mCurrentThreadLocal.get();
    }

    private Handler mHandler = new Handler();
}
