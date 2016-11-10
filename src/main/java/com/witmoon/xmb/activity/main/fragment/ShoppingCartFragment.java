package com.witmoon.xmb.activity.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.shoppingcart.adapter.ShoppingCartAdapter;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolderAdapter;
import com.witmoon.xmblibrary.recyclerview.SuperRecyclerView;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.linear.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车Fragment
 * Created by zhyh on 2015/4/28.
 */
public class ShoppingCartFragment extends BaseFragment implements View.OnClickListener,
        AbsViewHolderAdapter.OnItemClickListener<Map<String, String>>, ShoppingCartAdapter
                .OnRemoveButtonClickListener<Map<String, String>>, ShoppingCartAdapter
                .OnShoppingCartChangeListener, ShoppingCartAdapter
                .OnAddFavoriteClickListener<Map<String, String>> {

    private SuperRecyclerView mSuperRecyclerView;
    private ShoppingCartAdapter<Map<String, String>> mShoppingCartAdapter;
    private List<Map<String, String>> mDataList = new ArrayList<>();
    private Map<Integer, Integer> mCheckedPositionAndCountMap = new HashMap<>();

    private TextView mAccountSumText;
    private Button mSettleAccountBtn;
//    private CheckBox mCheckAll;

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GoodsApi.cartList(mCartCallback);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter loginFilter = new IntentFilter(Const.INTENT_ACTION_LOGIN);
        getActivity().registerReceiver(mLoginReceiver, loginFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        AQuery aQuery = new AQuery(getActivity(), view);

        mSuperRecyclerView = (SuperRecyclerView) aQuery.id(R.id.recycler_view).getView();
        mEmptyView = (ViewGroup) mSuperRecyclerView.getEmptyView();
        mAccountSumText = aQuery.id(R.id.account_sum).getTextView();
        mSettleAccountBtn = (Button) aQuery.id(R.id.next_step_btn).clicked(this).getView();
//        mCheckAll = aQuery.id(R.id.checkall).getCheckBox();

        // 购物车全选
//        mCheckAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mShoppingCartAdapter.checkAll(isChecked);
//                mCheckedPositionAndCountMap.clear();
//                if (isChecked) {
//                    for (int i = 0; i < mDataList.size(); i++) {
//                        mCheckedPositionAndCountMap.put(i, Integer.valueOf(mDataList.get(i).get
//                                ("count")));
//                    }
//                }
//                updateShoppingCartState();
//            }
//        });

        mShoppingCartAdapter = new ShoppingCartAdapter<>(mDataList);
        mShoppingCartAdapter.setOnItemClickListener(this);
        mShoppingCartAdapter.setOnRemoveButtonClickListener(this);
        mShoppingCartAdapter.setOnAddFavoriteClickListener(this);
        mShoppingCartAdapter.setOnShoppingCartChangeListener(this);
        mSuperRecyclerView.setAdapter(mShoppingCartAdapter);

        mSuperRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSuperRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable
                (R.drawable.divider_x1)));

        mAccountSumText.setText(String.format(mAccountSumText.getText().toString(), 0));
        mSettleAccountBtn.setText(String.format(getString(R.string.text_settle_account), 0));

        return view;
    }

    private ViewGroup mEmptyView;

    @Override
    public void onResume() {
        super.onResume();
        if (!AppContext.instance().isLogin()) {
            TextView tipView = (TextView) mEmptyView.findViewById(R.id.tv_error_tip);
            tipView.setText("登录后才可以使用购物车");
            TextView actionView = (TextView) mEmptyView.findViewById(R.id.tv_error_action);
            actionView.setText("点击登录");
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
            return;
        } else {
            TextView actionView = (TextView) mEmptyView.findViewById(R.id.tv_error_action);
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).changeToTab(0);
                }
            });
        }
        GoodsApi.cartList(mCartCallback);
    }

    // 购物车回调
    private Listener<JSONObject> mCartCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Log.e("response",response.toString());
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToastShort(tt.second);
                return;
            }
            mDataList.clear();
            try {
                JSONArray goodsArr = response.getJSONObject("data").getJSONArray("goods_list");
                for (int i = 0; i < goodsArr.length(); i++) {
                    JSONObject goods = goodsArr.getJSONObject(i);
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("id", goods.getString("rec_id"));
                    dataMap.put("goods_id", goods.getString("goods_id"));
                    dataMap.put("title", goods.getString("goods_name"));
                    dataMap.put("url", goods.getString("goods_img"));
                    dataMap.put("price_formatted", goods.getString("goods_price_formatted"));
                    dataMap.put("price", goods.getString("goods_price"));
                    dataMap.put("market_price_formatted", goods.getString
                            ("market_price_formatted"));
                    dataMap.put("count", goods.getString("goods_number"));
                    mDataList.add(dataMap);
                }

                String count = response.getJSONObject("data").getJSONObject("total").getString
                        ("real_goods_count");
                String totalPrice = response.getJSONObject("data").getJSONObject("total")
                        .getString("goods_price");
                mAccountSumText.setText(String.format(mAccountSumText.getText().toString(),
                        totalPrice));
                mSettleAccountBtn.setText(String.format("结算 (%s)", count));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mShoppingCartAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort(error.getMessage());
        }

        @Override
        public void onFinish() {
//            mCheckAll.setEnabled(mDataList.size() != 0);
            mSettleAccountBtn.setEnabled(mDataList.size() != 0);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        configToolbar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mLoginReceiver);
    }

    // 配置Toolbar
    private void configToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_shopping_cart));
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.toolbar_right_img).gone();
        aQuery.id(R.id.toolbar_right_img1).gone();
        aQuery.id(R.id.toolbar_right_img2).gone();
        aQuery.id(R.id.toolbar_logo_img).gone();
        aQuery.id(R.id.toolbar_left_img).gone();
        aQuery.id(R.id.toolbar_title_text).visible().text("购物车");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step_btn:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
//                OrderConfirmActivity.startActivity(getActivity());
                break;
        }
    }

    @Override
    public void onItemClick(AbsViewHolderAdapter<Map<String, String>> parent, View view,
                            Map<String, String> object, int position) {
    }

    @Override
    public void onRemove(AbsViewHolderAdapter<Map<String, String>> parent, View view,
                         final Map<String, String> object, int position) {
        new AlertDialog.Builder(getActivity()).setMessage("确定将该商品从购物车删除吗?")
                .setNegativeButton("取消", null).setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String recId = object.get("id");
                        GoodsApi.removeFromCart(recId, mRemoveCallback);
                    }
                }).show();
    }

    // 购物车删除商品回调接口
    private Listener<JSONObject> mRemoveCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (tt.first) {
                AppContext.showToastShort("操作成功");
                mCheckedPositionAndCountMap.clear();
                refresh();
                // TODO: 2015/7/27 刷新购物车
            }
        }
    };

    private void refresh() {
        GoodsApi.cartList(mCartCallback);
    }

    @Override
    public void onShoppingCartChange(final int position, final int count) {
        Map<String, String> dataMap = mDataList.get(position);
        GoodsApi.updateCart(dataMap.get("id"), true, count == 0 ? count + 1 : count, new
                Listener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                        if (tt.first) {
                            if (count > 0) {
                                mCheckedPositionAndCountMap.put(position, count);
                                mDataList.get(position).put("count", String.valueOf(count));
                            } else {
                                mCheckedPositionAndCountMap.remove(position);
                            }

                            updateShoppingCartState();
                        }
                    }
                });
    }

    private void updateShoppingCartState() {
        int sum = 0, amount = 0;
        for (Integer p : mCheckedPositionAndCountMap.keySet()) {
            Map<String, String> dm = mDataList.get(p);
            amount += Float.valueOf(dm.get("price")) * mCheckedPositionAndCountMap.get(p);
            sum += mCheckedPositionAndCountMap.get(p);
        }
        mAccountSumText.setText(String.format(getString(R.string.text_money_tmpl), amount));
        mSettleAccountBtn.setText(String.format(getString(R.string.text_settle_account), sum));
    }

    @Override
    public void onAddFavorite(AbsViewHolderAdapter<Map<String, String>> parent, View view,
                              Map<String, String> object, int position) {
        String goodsId = object.get("goods_id");
        UserApi.collectGoods(goodsId, mCollectCallback);
    }

    // 加入收藏回调接口
    private Listener<JSONObject> mCollectCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToastShort(tt.second);
                return;
            }
            AppContext.showToastShort("收藏成功");
        }
    };
}
