package com.witmoon.xmb.activity.user.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;

/**
 * 用户注册界面
 * Created by zhyh on 2015/6/19.
 */
public class RegisterFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_indicator_viewpager, container, false);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setAdapter(new RegisterPagerAdapter(getActivity().getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.indicator);
        tabs.setViewPager(mViewPager);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setToolBarTitle(R.string.text_register_success);
        baseActivity.setToolBarBackground(R.color.master_login);
    }

    public class RegisterPagerAdapter extends FragmentPagerAdapter {

        private final String[] tabLabels = {"手机注册", "邮箱注册"};
        private final Fragment[] fragments = {
                new TelephoneRegisterFragment(), new EmailRegisterFragment()
        };

        public RegisterPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabLabels[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return tabLabels.length;
        }
    }
}
