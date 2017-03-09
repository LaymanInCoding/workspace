package com.witmoon.xmb.activity.me.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.Out_ServiceActivity;
import com.witmoon.xmb.activity.me.adapter.Out_PriceAdapter;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.model.event.SearchEvent;
import com.witmoon.xmb.rx.RetrofitHelper;
import com.witmoon.xmb.rx.RxBus;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2015/11/24.
 */
public class ReturnofthegoodsFragment extends BaseFragment {
    private static final String[] TITLES = {"全部订单", "已申请"};
    List<Fragment> fragments = new ArrayList<>();
    private ArrayList<Out_> data = new ArrayList<>();
    public static EditText mEditText;
    private Out_PriceAdapter mOut_PriceAdapter;
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private LinearLayout layout_tab_vp;
    private LinearLayoutManager mLinearLayoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragments.add(new Out_PriceFragment());
        fragments.add(new AppliedFragment());
        View view = inflater.inflate(R.layout.layout_out_service, container, false);
        layout_tab_vp = (LinearLayout) view.findViewById(R.id.layout_tab_vp);
        mEditText = (EditText) view.findViewById(R.id.edit_text);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mRootView = (RecyclerView) view.findViewById(R.id.search_recycler);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setLayoutManager(mLinearLayoutManager);
        mRootView.setHasFixedSize(true);
        viewPager.setAdapter(new MyFavoriteViewPagerAdapter(getFragmentManager(), fragments));
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(TITLES[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(TITLES[1]));
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.getTabAt(0).setText(TITLES[0]);
        mTabLayout.getTabAt(1).setText(TITLES[1]);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (mEditText.getText().toString() != "") {
                        UserApi.search_(mEditText.getText().toString().trim(), mListener);
                    } else {
                        XmbUtils.showMessage(getActivity(), "请输入搜索条件");
                    }
                    layout_tab_vp.setVisibility(View.GONE);
                    mRootView.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
        mOut_PriceAdapter = new Out_PriceAdapter(getContext(), data);
        initEvent();
        mOut_PriceAdapter.setOnItemButtonClickListener(new Out_PriceAdapter.OnItemButtonClickListener() {
            @Override
            public void onItemButtonClick(Out_ order, int position) {
                order.setPosion(position);
                if (order.getRefund_status().equals("1")) {
                    Intent mIntent = new Intent(getActivity(), Out_ServiceActivity.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                    return;
                }
                Bundle mb = new Bundle();
                mb.putSerializable("order", order);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.JINDU, mb);
            }
        });
        mOut_PriceAdapter.setOnItemTypeClickListener(new Out_PriceAdapter.OnItemTypeClickListener() {
            @Override
            public void OnItemTypeClick(Out_ order, int position) {
                if (order.getRefund_status().equals("2")) {
                    Bundle mb = new Bundle();
                    mb.putSerializable("order", order);
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.JINDU, mb);
                } else if (order.getRefund_status().equals("3")) {
                    Intent mIntent = new Intent(getActivity(), Out_ServiceActivity.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                } else if (order.getRefund_status().equals("5")) {
                    Intent mIntent = new Intent(getActivity(), Fill_info_Fragent.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                }
            }
        });
        mOut_PriceAdapter.setmOnItemAllClickListener(new Out_PriceAdapter.OnItemAllClickListener() {
            @Override
            public void OnItemAllClickListener(Out_ order) {
                Bundle argument = new Bundle();
                argument.putString(OrderDetailFragment.KEY_ORDER_TYPE, "");
                argument.putString(OrderDetailFragment.KEY_ORDER_SN, order.getOrder_sn());
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER_DETAIL, argument);
            }
        });
        mRootView.setAdapter(mOut_PriceAdapter);
        return view;
    }

    private void initEvent() {
        Subscription rxSubscription = RxBus.getDefault().toObservable(SearchEvent.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchEvent>() {
                    @Override
                    public void call(SearchEvent event) {
//                        UserApi.search_(event.getSearchKey(), mListener);
                    }
                });
    }

    private Listener<JSONObject> mListener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Log.e("response", response.toString());
            data.clear();
            JSONArray orderArray = null;
            try {
                if (!response.getJSONObject("status").getString("succeed").equals("1")) {
                    AppContext.showToast("搜索异常！");
                    return;
                }
                orderArray = response.getJSONArray("data");
                if (orderArray.length() == 0) {
                    AppContext.showToast("没有相对应信息，请重新搜索！");
                    return;
                }
                for (int i = 0; i < orderArray.length(); i++) {
                    Out_ out = Out_.parse(orderArray.getJSONObject(i));
                    data.add(out);
                }
                mOut_PriceAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    public class MyFavoriteViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public MyFavoriteViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        }

    }

    @Override
    public boolean onBackPressed() {
        if (mRootView.getVisibility() == View.VISIBLE) {
            mRootView.setVisibility(View.GONE);
            layout_tab_vp.setVisibility(View.VISIBLE);
            data.clear();
            return true;
        }
        return super.onBackPressed();
    }
}
