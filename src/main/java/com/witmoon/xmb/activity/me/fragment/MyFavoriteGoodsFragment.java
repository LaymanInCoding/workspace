package com.witmoon.xmb.activity.me.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.adapter.MyFavoriteGoodsAdapter;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.FavoriteGoods;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;
import com.witmoon.xmb.ui.widget.LineFeedHorizontalLayout;
import com.witmoon.xmb.util.DensityUtils;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;

/**
 * 我的收藏之商品
 * Created by zhyh on 2015/6/23.
 */
public class MyFavoriteGoodsFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private int page = 1;
    private EmptyLayout mEmptyLayout;
    private SwipeMenuRecyclerView mRecyclerView;
    private View rootView;
    private ArrayList<FavoriteGoods> mDataList = new ArrayList<>();
    private MyFavoriteGoodsAdapter mAdapter;
    private String mGoodsId;
    private boolean is_guige;//是否有规格属性
    private String mGoodsCount = "";//库存量
    private String mGoodsTitle = "";
    private String mGoodsThumbUrl = "";//规格商品图片url；
    private String mGoodsPrice = "";//规格商品价格
    private LinearLayout mSpecificationContainer;
    private List<RadioGroup> mRadioGroups = new ArrayList<>();
    private CountDownTimer mCountDownTimer;
    private IncreaseReduceTextView mIncreaseReduceTextView;
    private TextView mGoodsPriceText;
    private TextView mGoodsTitleText;//规格标题
    private TextView mGoodsInventoryText;//规格库存
    private ImageView mGoodImageView;//规格商品图片
    private Button mGoodConfirmBtn;//规格确定按钮
    private PopupWindow specificationWindow;
    private DecimalFormat mDecimalFormat = new DecimalFormat("##0.00");
    private View popView;
    private int width;
    private int height;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        WindowManager wm = getActivity().getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_me_favorite, container, false);
            mEmptyLayout = (EmptyLayout) rootView.findViewById(R.id.empty_layout);
            mRecyclerView = (SwipeMenuRecyclerView) rootView.findViewById(R.id.swipe_recycler);
            mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
            layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources()
                    .getDrawable(R.drawable.divider_x1)));
            // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
            // 设置菜单创建器。
            mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
            // 设置菜单Item点击监听。
            mRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
            mAdapter = new MyFavoriteGoodsAdapter(getContext(), mDataList);
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mRecyclerView.setAdapter(mAdapter);
            setRecRequest(1);
            initPopView();
        }
        return rootView;
    }

    @Override
    public void setRecRequest(int currentPage) {
        currentPage = page;
        UserApi.collectionList(currentPage, mListener);
    }

    private Listener<JSONObject> mListener = new Listener<JSONObject>() {

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            try {
                JSONArray goodsArray = response.getJSONArray("data");
                if (page == 1 && goodsArray.length() == 0) {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                    return;
                }
                for (int i = 0; i < goodsArray.length(); i++) {
                    JSONObject goodsObj = goodsArray.getJSONObject(i);
                    mDataList.add(FavoriteGoods.parse(goodsObj));
                }
                if (goodsArray.length() < 10) {
                    if (page != 1) {
                        mAdapter.removeFooterView();
                    }
                    View nomoreView = LayoutInflater.from(getContext()).inflate(R.layout.view_no_message, null);
                    mAdapter.addFooterView(nomoreView);
                    mAdapter.setNoMoreData(true);
                } else {
                    mAdapter.removeFooterView();
                    mRecyclerView.removeOnScrollListener(recyclerViewScrollListener);
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.view_load_more, null);
                    mAdapter.addFooterView(view);
                    resetStatus();
                    mAdapter.setNoMoreData(false);
                }
                mAdapter.notifyDataSetChanged();
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                page += 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void resetStatus() {
        mRecyclerView.removeOnScrollListener(recyclerViewScrollListener);
        recyclerViewScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                setRecRequest(currentPage);
            }
        };
        mRecyclerView.addOnScrollListener(recyclerViewScrollListener);
    }

    // 取消收藏回调接口
    private Listener<JSONObject> mCancelCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToastShort(tt.second);
                return;
            }
            AppContext.showToastShort("删除成功");
        }
    };

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dimen_100_dip);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (viewType == mAdapter.FOOTER_TYPE) {

            } else {
                // 添加右侧的，如果不添加，则右侧不会出现菜单。
                SwipeMenuItem addItem = new SwipeMenuItem(getContext())
                        .setText("购买") // 文字，还可以设置文字颜色，大小等。。
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
                FavoriteGoods goods = mDataList.get(adapterPosition);
                UserApi.cancelCollect(goods.getGoodsId(), mCancelCallback);
//                GoodsApi.removeFromCart(dataMap.get("id"), mRemoveCallback);
                mDataList.remove(adapterPosition);
                mAdapter.notifyItemRemoved(adapterPosition);
                Logger.d(mDataList.size());
                if (mDataList.size() == 0) {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                }
//                mShoppingCartAdapter.notifyItemRemoved(adapterPosition);
            }
            if (menuPosition == 0) { //购买被点击
                FavoriteGoods goods = mDataList.get(adapterPosition);
                mGoodsId = goods.getGoodsId();
                GoodsApi.goodsSpecification(goods.getGoodsId(), mSpecificationCallback);

//                Map<String, String> dataMap = mDataList.get(adapterPosition);
//                UserApi.collectGoods(dataMap.get("goods_id"), mCollectCallback);
            }
        }
    };
    // 商品规格回调接口
    private Listener<JSONObject> mSpecificationCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showWaitDialog();
        }

        @Override
        public void onError(NetroidError error) {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToast(twoTuple.second);
                return;
            }
            hideWaitDialog();
            try {
                JSONObject dataObj = response.getJSONObject("data");
                mGoodsTitle = dataObj.getString("goods_name");
                mGoodsCount = dataObj.getString("goods_number");
                mGoodsThumbUrl = dataObj.getString("goods_thumb");
                mGoodsPrice = dataObj.getString("shop_price_formatted");
                setPopView();
                parseResponseAndUpdateUI(dataObj.getJSONArray("goods_specs"));
                showPop();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void parseResponseAndUpdateUI(JSONArray specArray) throws JSONException {
        is_guige = false;
        mSpecificationContainer.removeAllViews();
        if (mRadioGroups.size() != 0) {
            mRadioGroups.clear();
        }
        Log.e("specArray", specArray.toString());
        for (int i = 0; i < specArray.length(); i++) {
            JSONObject specObj = specArray.getJSONObject(i);
            Map<String, Float> innerMap = new HashMap<>();
            TextView specNameText = new TextView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = DensityUtils.dp2px(getContext(), 10);
            layoutParams.topMargin = DensityUtils.dp2px(getContext(), 12);
            specNameText.setLayoutParams(layoutParams);
            specNameText.setText(specObj.getString("attr_name"));
            mSpecificationContainer.addView(specNameText);

            LineFeedHorizontalLayout specTagGroup = new LineFeedHorizontalLayout(getContext());
            specTagGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            specTagGroup.setTag(specObj.getString("attr_id"));
            specTagGroup.setHorizontalSpace(DensityUtils.dp2px(getContext(), 12));
            specTagGroup.setVerticalSpace(DensityUtils.dp2px(getContext(), 6));
            specTagGroup.setOnCheckedChangeListener(this);
            mSpecificationContainer.addView(specTagGroup);
            mRadioGroups.add(specTagGroup);
            createSpecificationAttrTag(specObj.getJSONArray("goods_attr_list"), innerMap,
                    specTagGroup);
        }
    }

    private void createSpecificationAttrTag(JSONArray specAttrArray, Map<String, Float>
            attrPriceMap, RadioGroup tagGroup) throws JSONException {
        for (int i = 0; i < specAttrArray.length(); i++) {
            is_guige = true;
            JSONObject to = specAttrArray.getJSONObject(i);
            attrPriceMap.put(to.getString("goods_attr_id"), Float.parseFloat(to.getString
                    ("goods_attr_price")));

            final RadioButton radioButton = new RadioButton(getContext());
            radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //动态设置边距
            radioButton.setPadding(width / 30, width / 60, width / 30, width / 60);
            radioButton.setText(to.getString("goods_attr_name"));
            radioButton.setTag(to.getString("goods_attr_id") + ":" + to.getString
                    ("goods_attr_price"));
            radioButton.setButtonDrawable(android.R.color.transparent);
            radioButton.setTextColor(getResources().getColorStateList(R.color
                    .checked_inverse_white));
            radioButton.setBackgroundDrawable(getResources().getDrawable(R.drawable
                    .bg_checked_rounded2_red));
            tagGroup.addView(radioButton);
        }
    }

    private void setPopView() {
        Netroid.displayAdImage(mGoodsThumbUrl, mGoodImageView);
        mGoodsTitleText.setText(mGoodsTitle);
        mGoodsInventoryText.setText(String.format("库存量%s件", mGoodsCount));
        mGoodsPriceText.setText(mGoodsPrice);
        mIncreaseReduceTextView.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int number) {
                if (!mGoodsPrice.equals("") && !mGoodsCount.equals(""))
                    Log.d("COUNT", mGoodsCount);
                if (Integer.parseInt(mGoodsCount) < mIncreaseReduceTextView.getNumber()) {
                    AppContext.showToast("库存不足");
                    mIncreaseReduceTextView.setNumber(Integer.parseInt(mGoodsCount));
                }
                mGoodsPriceText.setText(mDecimalFormat.format((float) number * Float.parseFloat(mGoodsPrice.substring(1, mGoodsPrice.length()))) + "");
            }
        });
        mGoodConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShoppingCart();
            }
        });
    }

    private void addShoppingCart() {
        if (!AppContext.instance().isLogin()) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
        String str = new String();
        boolean is_ = true;
        for (RadioGroup tagGroup : mRadioGroups) {
            int id = tagGroup.getCheckedRadioButtonId();
            View checkedRadio = tagGroup.findViewById(id);
            // 如果用户没有勾选任何规格直接略过
            if (checkedRadio == null) {
                is_ = false;
                continue;
            }
            String radioTag = (String) checkedRadio.getTag();
            str = str + radioTag.split(":")[0] + ",";
        }
        if (is_guige) {
            if (str.length() > 0) {
                if (is_) {
                    str = str.substring(0, str.length() - 1);
                    //选择规格接口，返回价格和库存
                    GoodsApi.addToCart(mGoodsId, str.toString(), mIncreaseReduceTextView
                            .getNumber(), addCartCallback);
                } else {
                    AppContext.showToast("请选择商品规格和属性");
                }
            } else {
                AppContext.showToast("请选择商品规格和属性");
            }
        } else {
            GoodsApi.addToCart(mGoodsId, str.toString(), mIncreaseReduceTextView
                    .getNumber(), addCartCallback);
        }
    }

    // 添加购物车回调
    private Listener<JSONObject> addCartCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            specificationWindow.dismiss();
            AppContext.showToastShort("商品添加成功");
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort("商品添加失败");
        }
    };

    private void initPopView() {
        popView = LayoutInflater.from(getContext()).inflate(R.layout.commodity_specification_pop, null);
        mGoodsTitleText = (TextView) popView.findViewById(R.id.goods_title);
        mGoodsInventoryText = (TextView) popView.findViewById(R.id.inventory);
        mGoodImageView = (ImageView) popView.findViewById(R.id.goods_image);
        mGoodsPriceText = (TextView) popView.findViewById(R.id.goods_price);
        mSpecificationContainer = (LinearLayout) popView.findViewById(R.id.specification_container);
        popView.findViewById(R.id.submit_button).setOnClickListener(this);
        mIncreaseReduceTextView = (IncreaseReduceTextView) popView.findViewById(R.id.goods_count);
        mGoodConfirmBtn = (Button) popView.findViewById(R.id.submit_button);

    }

    public void showPop() {
        specificationWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundAlpha(0.5f);
        specificationWindow.setBackgroundDrawable(new BitmapDrawable());
        specificationWindow.setFocusable(true);
        specificationWindow.setAnimationStyle(R.style.PopAnimation);
        specificationWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        specificationWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });

    }

    //设置背景颜色
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String str = new String();
        for (RadioGroup tagGroup : mRadioGroups) {
            int id = tagGroup.getCheckedRadioButtonId();
            if (id != -1) {
                String tag = (String) tagGroup.findViewById(id).getTag();
                str = str + tag.split(":")[0] + ",";
//                price += Double.parseDouble(tag.split(":")[1]);
//                count = tag.split(":")[2];
            }
        }
        if (str.length() > 0)
            str = str.substring(0, str.length() - 1);
        String[] tagStr = str.split(",");
        Arrays.sort(tagStr);
        str = "";
        for (int i = 0; i < tagStr.length; i++) {
            str = str + tagStr[i] + ",";
        }
        str = str.substring(0, str.length() - 1);
        if (AppContext.isNetworkAvailable(getContext())) {
            GoodsApi.getGoodsSpecificationInfo(mGoodsId, str.toString(), mIncreaseReduceTextView
                    .getNumber(), new Listener<JSONObject>() {
                @Override
                public void onPreExecute() {
                    super.onPreExecute();
//                    showWaitDialog("进行中...");
                }

                @Override
                public void onSuccess(JSONObject response) {
                    Log.e("response", response.toString());
                    TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                    if (!twoTuple.first) {
                        AppContext.showToastShort(twoTuple.second);
                        return;
                    }
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");
                        mGoodsPriceText.setText(jsonObject.getString("result"));
                        ((TextView) popView.findViewById(R.id.inventory)).setText(String.format
                                ("库存量%s件", Integer.parseInt(jsonObject.getString("num"))));
                        mIncreaseReduceTextView.setNumber(Integer.parseInt(jsonObject.getString("qty")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    String message = error.toString();
                    if (TextUtils.isEmpty(error.toString())) {
                        if (TDevice.hasInternet()) {
                            message = getString(R.string.tip_load_data_error);
                        } else {
                            message = getString(R.string.tip_network_error);
                        }
                    }
                    AppContext.showToastShort(message);
                    hideWaitDialog();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mCountDownTimer = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            hideWaitDialog();
                        }
                    }.start();
                }
            });
        } else {
            AppContext.showToast("请检查当前网络");
        }
    }
}
