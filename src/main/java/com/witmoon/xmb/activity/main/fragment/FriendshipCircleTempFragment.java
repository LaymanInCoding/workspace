package com.witmoon.xmb.activity.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;

/**
 * 临时替换麻包圈Fragment
 * Created by zhyh on 2015/8/30.
 */
public class FriendshipCircleTempFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setText("努力开发中, 稍候开放...");

        linearLayout.addView(textView);

        return linearLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_friendship_circle));
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_left_img).gone();
        aQuery.id(R.id.toolbar_right_img).image(R.mipmap.icon_camera).visible().clicked(this);
        aQuery.id(R.id.toolbar_title_text).gone();
        aQuery.id(R.id.toolbar_logo_img).image(R.mipmap.logo).visible();
    }
}
