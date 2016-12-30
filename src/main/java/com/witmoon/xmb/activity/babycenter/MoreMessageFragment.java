package com.witmoon.xmb.activity.babycenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseFragment;

/**
 * Created by de on 2016/12/1.
 */
public class MoreMessageFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.baby_guidie_layout, container, false);
        return view;
    }
}
