package com.witmoon.xmb.activity.me.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.observablescrollview.ObservableRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de on 2015/11/24.
 */
public class ReturnofthegoodsFragment extends BaseFragment{
    private static final String[] TITLES = {"全部订单", "已申请"};
    public static EditText mEditText;
    public static ObservableRecyclerView mObservableRecyclerView;
    private Out_PriceAdapter mOut_PriceAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_out_service, container, false);
        mEditText = (EditText) view.findViewById(R.id.edit_text);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        mObservableRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.recycleView);
        viewPager.setAdapter(new MyFavoriteViewPagerAdapter(getFragmentManager()));
        PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) view.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        indicator.setIndicatorColorResource(R.color.master_me);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (mEditText.getText().toString()!="")
                    {
                        UserApi.search_(mEditText.getText().toString().trim(), mListener);
                    }else {
                        XmbUtils.showMessage(getActivity(),"请输入搜索条件");
                    }

                    mObservableRecyclerView.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
        intis();
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
        return view;
    }
    private void intis(){
        mObservableRecyclerView.setLayoutManager(getLayoutManager());
        mObservableRecyclerView.setHasFixedSize(true);
        if (mOut_PriceAdapter != null) {
            mObservableRecyclerView.setAdapter(mOut_PriceAdapter);
        } else {
            mOut_PriceAdapter = new Out_PriceAdapter(getContext());
            mObservableRecyclerView.setAdapter(mOut_PriceAdapter);
        }
    }

    private Listener<JSONObject> mListener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Log.e("response",response.toString());

            JSONArray orderArray = null;
            try {
                if(!response.getJSONObject("status").getString("succeed").equals("1")){
                    AppContext.showToast("搜索异常！");
                    return;
                }
                orderArray = response.getJSONArray("data");
                if (orderArray.length()==0)
                {
                    AppContext.showToast("没有相对应信息，请重新搜索！");
                    return;
                }
                final List<Out_> outs = new ArrayList<Out_>(orderArray.length());
                for (int i = 0; i < orderArray.length(); i++) {
                    Out_ out = Out_.parse(orderArray.getJSONObject(i));
                    outs.add(out);
                }
                mOut_PriceAdapter.setData((ArrayList) outs);
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

        public MyFavoriteViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new Out_PriceFragment();
            }
            return new AppliedFragment();
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
    public static boolean onKeyDown(int keyCode, KeyEvent event,Activity activity) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            if (mObservableRecyclerView.getVisibility()==View.VISIBLE)
            {
                mObservableRecyclerView.setVisibility(View.GONE);
                mEditText.setText("");
                return false;
            } else{
                activity.finish();
                return false;
            }
        }
        return true;
    }
}
