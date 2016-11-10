package com.witmoon.xmb.activity.mbq.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.activity.MessageActivity;
import com.witmoon.xmb.activity.mbq.activity.SearchPost;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.widget.BadgeView;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.linearlistview.util.DensityUtil;

import org.json.JSONException;
import org.json.JSONObject;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MbqFragment extends BaseFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    public static ViewPager mViewPager;
    private String[] str_title = {"我的圈","热帖","更多圈"};
    private View view;
    private ImageView search_imags;
    private Fragment[] fragments = {new MyCircleFragment(),new HotPostFragment(),new MoreCircleFragment()};
    private ImageView mbq_message;
    private BadgeView badgeView;
    private RadioButton my_circle_btn,hot_post_btn,more_circle_btn;
    private SegmentedGroup segmented;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_mbq, container, false); 
            segmented = (SegmentedGroup) view.findViewById(R.id.segmented2);
            mViewPager = (ViewPager) view.findViewById(R.id.mbq_viewpager);
            search_imags = (ImageView) view.findViewById(R.id.search_imags);
            search_imags.setOnClickListener(this);
            mbq_message = (ImageView)view.findViewById(R.id.mbq_message);
            mbq_message.setOnClickListener(this);
            badgeView = new BadgeView(getActivity(), mbq_message);
            badgeView.setTextSize(8);
            badgeView.setBadgeMargin(0, 0);
            badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            my_circle_btn = (RadioButton) view.findViewById(R.id.my_circle_btn);
            hot_post_btn = (RadioButton) view.findViewById(R.id.hot_post_btn);
            more_circle_btn = (RadioButton) view.findViewById(R.id.more_circle_btn);
            AssetManager mgr = getActivity().getAssets();//得到AssetManager
            Typeface tf=Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
            more_circle_btn.setTypeface(tf);
            my_circle_btn.performClick();
            hot_post_btn.setTypeface(tf);
            my_circle_btn.setTypeface(tf);
            segmented.setOnCheckedChangeListener(this);
            init();
        }

        //返回是否存在父视图
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideToolBar();
    }

    private void init() {
        mViewPager.setOnPageChangeListener(new mOnPageChangeListener());
        mViewPager.setAdapter(new CanulacirclePagerAdapter(getChildFragmentManager()));
        mViewPager.setCurrentItem(0);

        IntentFilter refresh_message = new IntentFilter(Const.MBQ_MESSAGE_PUSH);
        getActivity().registerReceiver(refresh_my_message, refresh_message);

        IntentFilter logout = new IntentFilter(Const.INTENT_ACTION_REFRESH);
        getActivity().registerReceiver(loginout, logout);

        getMessage();
    }

    private void getMessage(){
        if (AppContext.instance().isLogin()){
            CircleApi.get_message_number(new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        int number = Integer.parseInt(response.getString("number"));
                        if(number > 0){
                            badgeView.setText(number + "");
                            badgeView.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.my_circle_btn:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.hot_post_btn:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.more_circle_btn:
                mViewPager.setCurrentItem(2);
                break;
            default:
                // Nothing to do
        }
    }

    public class mOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0){
                my_circle_btn.performClick();
            }else if(position == 1){
                hot_post_btn.performClick();
            }else if(position == 2){
                more_circle_btn.performClick();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideToolBar();
        MainActivity.current_tab_index = 3;

    }

    private BroadcastReceiver loginout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getMessage();
        }
    };

    private BroadcastReceiver refresh_my_message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message_num = SharedPreferencesUtil.get(getActivity(), Const.MBQ_MESSAGE_TIP,"").toString();
            if(message_num != null && !message_num.equals("") && !message_num.equals("0")){
                badgeView.setText(message_num);
                badgeView.show();
            }else{
                badgeView.hide();
            }
        }
    };

    public class CanulacirclePagerAdapter extends FragmentPagerAdapter {

        public CanulacirclePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return str_title.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return str_title[position];
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.search_imags:
                startActivity(new Intent(getActivity(), SearchPost.class));
                this.getActivity().overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
                break;
            case R.id.mbq_message:
                badgeView.setText("0");
                badgeView.hide();
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
        }
    }

}
