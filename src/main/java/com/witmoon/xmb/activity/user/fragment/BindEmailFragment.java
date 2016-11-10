package com.witmoon.xmb.activity.user.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseFragment;

/**
 * 绑定邮箱界面
 * Created by zhyh on 2015/6/20.
 */
public class BindEmailFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_email, container, false);

        return view;
    }


}
