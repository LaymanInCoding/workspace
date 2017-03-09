package com.witmoon.xmb.activity.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SimpleBackActivity;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.shoppingcart.OrderConfirmActivity;
import com.witmoon.xmb.activity.shoppingcart.adapter.ShoppingCart;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.event.CartRefreshCd;
import com.witmoon.xmb.rx.RxBus;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.linear.DividerItemDecoration;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

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
public class ShoppingCartFragmentV2 extends BaseFragment implements ShoppingCart.OnClickListener {
    /**
     * 小能 ----
     */
//    private JSONObject ntjson = new JSONObject();
//    private JSONObject param1 = new JSONObject();
//    private JSONArray ntalkerparam = new JSONArray();
    private View rootView;
    private SwipeMenuRecyclerView scrollRecycler;
    private ViewGroup mEmptyView;
    private TextView mActionTipText;    // 空布局提示
    private ImageView imageView;
    private TextView mActionView;   // 空布局按钮
    private ShoppingCart mShoppingCartAdapter;
    private List<Map<String, String>> mDataList = new ArrayList<>();

    private TextView mAccountSumText;
    private Button mSettleAccountBtn;
    private CheckBox mCheckAllCheckbox;
    private CountDownTimer mCountDownTimer;
    private boolean is_code = false;
    List<Map<String, String>> goodsList;
    private BroadcastReceiver mLoginOrLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private BroadcastReceiver mUpdata_car = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private EmptyLayout emptyLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter loginFilter = new IntentFilter(Const.INTENT_ACTION_LOGIN);
        getActivity().registerReceiver(mLoginOrLogoutReceiver, loginFilter);
        getActivity().registerReceiver(mLoginOrLogoutReceiver, new IntentFilter(Const
                .INTENT_ACTION_LOGOUT));
        //优化界面---  即时更新广播。
        IntentFilter updata_car = new IntentFilter(Const.INTENT_ACTION_UPDATA_CAR);
        getActivity().registerReceiver(mUpdata_car, updata_car);
        getActivity().registerReceiver(mUpdata_car, new IntentFilter(Const
                .INTENT_ACTION_UPDATA_CAR));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setTitleColor_(R.color.main_kin);
        }
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

            scrollRecycler = (SwipeMenuRecyclerView) rootView.findViewById(R.id.recycler_view);
            scrollRecycler.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
            scrollRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            scrollRecycler.addItemDecoration(new DividerItemDecoration(getResources()
                    .getDrawable(R.drawable.divider_x1)));
            // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
            // 设置菜单创建器。
            scrollRecycler.setSwipeMenuCreator(swipeMenuCreator);
            // 设置菜单Item点击监听。
            scrollRecycler.setSwipeMenuItemClickListener(menuItemClickListener);
            mEmptyView = (ViewGroup) rootView.findViewById(R.id.cart_empty);
            mActionView = (TextView) mEmptyView.findViewById(R.id.tv_error_action);
            mActionTipText = (TextView) mEmptyView.findViewById(R.id.tv_error_tip);
            imageView = (ImageView) mEmptyView.findViewById(R.id.car_img);
            mActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).changeToTab(0);
                }
            });
            mAccountSumText = (TextView) rootView.findViewById(R.id.account_sum);
            mSettleAccountBtn = (Button) rootView.findViewById(R.id.next_step_btn);
            mSettleAccountBtn.setOnClickListener(this);
            mCheckAllCheckbox = (CheckBox) rootView.findViewById(R.id.checkall);
            mCheckAllCheckbox.setOnClickListener(this);

            mShoppingCartAdapter = new ShoppingCart(getContext(), mDataList);
            mShoppingCartAdapter.OnClickListener(this);
            scrollRecycler.setAdapter(mShoppingCartAdapter);
            emptyLayout = (EmptyLayout) rootView.findViewById(R.id.error_layout);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });
        }

        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        return rootView;
    }

    // 刷新购物车
    public void refresh() {
        if (null != scrollRecycler) {
            if (!AppContext.instance().isLogin()) {
                scrollRecycler.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mActionTipText.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                mActionView.setVisibility(View.VISIBLE);
                mActionTipText.setText("登录后才可以使用购物车");
                mActionView.setText("点击登录");
                mActionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                });
                mAccountSumText.setText("￥0");
                mSettleAccountBtn.setText("结算(0)");
            } else {
                GoodsApi.cartList(mCartCallback);
            }
        }
    }

    // 购物车回调
    private Listener<JSONObject> mCartCallback = new Listener<JSONObject>() {
        private String count = "0", totalPrice = "￥0";

        @Override
        public void onPreExecute() {
            count = "0";
            totalPrice = "￥0";
//            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            mDataList.clear();
            try {
                //error_linked:防止在fragment attach 到Activity 前调用getResource();
                // http://stackoverflow.com/questions/10919240/fragment-myfragment-not-attached-to-activity
                if (isAdded()) {
                    Logger.json(response.toString());
                    JSONObject dataObj = response.getJSONObject("data");
                    TwoTuple<Integer, List<Map<String, String>>> parseResult = parseGoodsList(dataObj
                            .getJSONArray("goods_list"));
                    if (parseResult.second.size() < 1) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        scrollRecycler.setVisibility(View.GONE);
//                        mSuperRecyclerView.hideRecycler();
                        mCheckAllCheckbox.setEnabled(false);
                        mActionTipText.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        mActionTipText.setText(getResources().getString(R.string.tip_empty_cart));
                        mActionView.setText("今日特卖，去看看！");
                        mActionView.setVisibility(View.VISIBLE);
                        mActionView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.current_tab_index = 2;
                                getActivity().finish();
                            }
                        });
                        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        return;
                    }
                    mEmptyView.setVisibility(View.GONE);
                    scrollRecycler.setVisibility(View.VISIBLE);
                    mCheckAllCheckbox.setEnabled(true);
                    mCheckAllCheckbox.setChecked(parseResult.first == parseResult.second.size());
                    mSettleAccountBtn.setClickable(parseResult.first > 0);
//                    mSuperRecyclerView.showRecycler();
                    mDataList.addAll(parseResult.second);
                    totalPrice = dataObj.getJSONObject("total").getString("goods_price");
                    count = dataObj.getJSONObject("total").getString("real_goods_count");
                    mShoppingCartAdapter.notifyDataSetChanged();
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

                }
            } catch (JSONException e) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                XmbUtils.showMessage(getActivity(), "服务器返回数据错误");
            }
        }

        @Override
        public void onError(NetroidError error) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);

        }

        @Override
        public void onFinish() {
            mSettleAccountBtn.setEnabled(mDataList.size() != 0);
            mAccountSumText.setText(totalPrice);
            mSettleAccountBtn.setText(String.format("结算(%s)", count));
        }
    };

    // 解析购物车中商品数据
    private TwoTuple<Integer, List<Map<String, String>>> parseGoodsList(
            JSONArray goodsArray) throws JSONException {
        goodsList = new ArrayList<>();
        int checkedCount = 0;
        for (int i = 0; i < goodsArray.length(); i++) {
            JSONObject goods = goodsArray.getJSONObject(i);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("id", goods.getString("rec_id"));
            dataMap.put("goods_id", goods.getString("goods_id"));
            dataMap.put("title", goods.getString("goods_name"));
            dataMap.put("image", goods.getString("goods_img"));
            dataMap.put("checked", goods.getString("flow_order"));
            dataMap.put("price_formatted", goods.getString("goods_price_formatted"));
            dataMap.put("price", goods.getString("goods_price"));
            dataMap.put("market_price_formatted", goods.getString("market_price_formatted"));
            dataMap.put("count", goods.getString("goods_number"));
            dataMap.put("is_third", goods.getString("is_third"));
            dataMap.put("is_group", goods.getString("is_group"));
            dataMap.put("goods_attr",goods.getString("goods_attr"));
            dataMap.put("coupon_disable", goods.getString("coupon_disable"));
            dataMap.put("is_cross_border", goods.getString("is_cross_border"));
            goodsList.add(dataMap);
            if ("1".equals(goods.getString("flow_order"))) {
                checkedCount++;
            }
        }
        return new TwoTuple<>(checkedCount, goodsList);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        configToolbar();
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mLoginOrLogoutReceiver);
        getActivity().unregisterReceiver(mUpdata_car);
    }

    // 配置Toolbar
    private void configToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_kin));
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_right_img2).gone();
        aQuery.id(R.id.toolbar_right_img1).gone();
        aQuery.id(R.id.toolbar_logo_img).gone();
        if (!(getActivity() instanceof SimpleBackActivity)) {
            aQuery.id(R.id.toolbar_left_img).gone();
        }
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
                OrderConfirmActivity.startActivity(getActivity(), is_code);
                break;
            case R.id.checkall:
                GoodsApi.selectAllInCart(new Listener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                        if (tt.first) {
                            refresh();
                        }
                    }
                });
                break;
        }
    }
    /*
    @Override
    public void onItemClick(AbsViewHolderAdapter<Map<String, String>> parent, final View view,
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



    @Override
    public void onShoppingCartChange(final int position, boolean checked, final int count) {
        Map<String, String> dataMap = mDataList.get(position);
        GoodsApi.updateCart(dataMap.get("id"), checked, count, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                if (!tt.first) {
                    CommonUtil.show(getActivity(), tt.second, 1000);
                }
            }
            @Override
            public void onFinish() {
                refresh();
            }
        });
    }

    @Override
    public void onAddFavorite(AbsViewHolderAdapter<Map<String, String>> parent, View view,
                              Map<String, String> object, int position) {
        String goodsId = object.get("goods_id");
        UserApi.collectGoods(goodsId, mCollectCallback);
    }*/

    // 购物车删除商品回调接口
    private Listener<JSONObject> mRemoveCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                CommonUtil.show(getActivity(), tt.second, 1000);
            }
//            RxBus.getDefault().post(new CartRefreshCd(true));
        }

        @Override
        public void onFinish() {
            refresh();
        }
    };

    // 加入收藏回调接口
    private Listener<JSONObject> mCollectCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                CommonUtil.show(getActivity(), tt.second, 1000);
                return;
            }
            CommonUtil.show(getActivity(), "收藏成功", 1000);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRemoveButtonClick(int position) {
        Map<String, String> dataMap = mDataList.get(position);

        GoodsApi.removeFromCart(dataMap.get("id"), mRemoveCallback);
//                    }
//                }).show();
    }

    @Override
    public void onAddFavoriteClick(int position) {
        Map<String, String> dataMap = mDataList.get(position);

        UserApi.collectGoods(dataMap.get("goods_id"), mCollectCallback);
    }

    @Override
    public void onShoppingCartChange(int position, boolean is_checked, int number) {
        Map<String, String> dataMap = mDataList.get(position);
        GoodsApi.updateCart(dataMap.get("id"), is_checked, number, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                if (!tt.first) {
                    CommonUtil.show(getActivity(), tt.second, 1000);
                }
            }

            @Override
            public void onFinish() {
                refresh();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Map<String, String> dataMap = mDataList.get(position);
        CommodityDetailActivity.start(getActivity(), dataMap.get("goods_id"));
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dimen_100_dip);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(getContext())
                        .setText("收藏") // 文字，还可以设置文字颜色，大小等。。
                        .setBackgroundDrawable(R.color.master_me)
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem);// 添加一个按钮到右侧侧菜单。

                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                        .setText("删除")
                        .setBackgroundDrawable(R.color.grey)
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem); // 添加一个按钮到右侧菜单。
            }
        }
    };
    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if (menuPosition == 1) {// 删除按钮被点击。
                Map<String, String> dataMap = mDataList.get(adapterPosition);
                GoodsApi.removeFromCart(dataMap.get("id"), mRemoveCallback);
                mDataList.remove(adapterPosition);
                mShoppingCartAdapter.notifyItemRemoved(adapterPosition);
            }
            if (menuPosition == 0) { //收藏被点击
                Map<String, String> dataMap = mDataList.get(adapterPosition);
                UserApi.collectGoods(dataMap.get("goods_id"), mCollectCallback);
            }
        }
    };
}
