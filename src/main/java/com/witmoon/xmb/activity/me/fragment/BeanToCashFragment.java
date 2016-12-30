package com.witmoon.xmb.activity.me.fragment;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/12/20.
 */
public class BeanToCashFragment extends BaseFragment {

    @BindView(R.id.toolbar_title_text)
    TextView mToolbarTitleText;
    @BindView(R.id.toolbar_right_text)
    TextView mToolbarRightText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideConfigToolbar();
    }

    private void hideConfigToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.top_toolbar).gone();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bean_to_cash, container, false);
        view.findViewById(R.id.bean_use_container).setVisibility(View.GONE);
        ButterKnife.bind(this, view);
        setFont();
        return view;
    }

    private void setFont() {
        AssetManager mgr = getActivity().getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        mToolbarTitleText.setTypeface(tf);
        mToolbarRightText.setTypeface(tf);
        mToolbarTitleText.setText("兑换现金");
        mToolbarRightText.setText("绑定银行卡");
        mToolbarRightText.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.toolbar_right_text, R.id.bean_help_tv, R.id.toolbar_left_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bean_help_tv:
                UIHelper.showSimpleBack(getContext(), SimpleBackPage.BeanHelp);
                break;
            case R.id.toolbar_right_text:
                UIHelper.showSimpleBack(getContext(), SimpleBackPage.BindBankCard);
                break;
            case R.id.toolbar_left_img:
                getActivity().onBackPressed();
                break;
        }
    }
}
