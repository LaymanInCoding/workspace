package com.witmoon.xmb.activity.shoppingcart.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolder;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolderAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车列表数据适配器
 */
public class ShoppingCartAdapter<T> extends AbsViewHolderAdapter<T> {

    private OnRemoveButtonClickListener<T> mOnRemoveButtonClickListener;
    private OnAddFavoriteClickListener<T> mOnAddFavoriteClickListener;
    private OnShoppingCartChangeListener mOnShoppingCartChangeListener;

    private Map<Integer, Boolean> mCheckedStateMap = new HashMap<>();

    public ShoppingCartAdapter(List<T> dataList) {
        super(dataList);
    }

    /**
     * 注册删除按钮点击监听器
     *
     * @param listener 监听器
     */
    public void setOnRemoveButtonClickListener(OnRemoveButtonClickListener<T> listener) {
        this.mOnRemoveButtonClickListener = listener;
    }

    /**
     * 注册添加收藏按钮点击监听器
     *
     * @param listener 单击监听器
     */
    public void setOnAddFavoriteClickListener(OnAddFavoriteClickListener<T> listener) {
        this.mOnAddFavoriteClickListener = listener;
    }

    public void setOnShoppingCartChangeListener(OnShoppingCartChangeListener listener) {
        mOnShoppingCartChangeListener = listener;
    }

    @Override
    public void onBindViewHolder(final AbsViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);

        viewHolder.findViewByIdEfficient(R.id.remove_button).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnRemoveClickListener(viewHolder, position);
            }
        });
        viewHolder.findViewByIdEfficient(R.id.add_favorite_button).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnAddFavoriteClickListener(viewHolder, position);
            }
        });
        // CheckBox状态及事件绑定
        final CheckBox checkBox = (CheckBox) viewHolder.findViewByIdEfficient(R.id.checkbox);
        if (!mCheckedStateMap.containsKey(position)) {
            mCheckedStateMap.put(position, false);
        }
        checkBox.setChecked(mCheckedStateMap.get(position));

        final IncreaseReduceTextView irText = (IncreaseReduceTextView) viewHolder
                .findViewByIdEfficient(R.id.goods_number_edit);
        irText.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int number) {
                if (!checkBox.isChecked()) {
                    checkBox.setChecked(true);
                } else {
                    setOnShoppingCartChangeListener(position, irText.getNumber());
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckedStateMap.put(position, isChecked);
                setOnShoppingCartChangeListener(position, isChecked ? irText.getNumber() : 0);
            }
        });
    }

    private void setOnAddFavoriteClickListener(AbsViewHolder viewHolder, int position) {
        if (mOnAddFavoriteClickListener != null) {
            T t = get(position);
            mOnAddFavoriteClickListener.onAddFavorite(this, viewHolder.getView(), t, position);
        }
    }

    private void setOnRemoveClickListener(AbsViewHolder viewHolder, int position) {
        if (mOnRemoveButtonClickListener != null) {
            T t = get(position);
            mOnRemoveButtonClickListener.onRemove(this, viewHolder.getView(), t, position);
        }
    }

    private void setOnShoppingCartChangeListener(int position, int count) {
        if (mOnShoppingCartChangeListener != null) {
            mOnShoppingCartChangeListener.onShoppingCartChange(position, count);
        }
    }

    /**
     * 更新全选状态
     * @param checked 是否全选
     */
    public void checkAll(boolean checked) {
        mCheckedStateMap.clear();
        for (int i = 0; i < getObjects().size(); i++) {
            mCheckedStateMap.put(i, checked);
        }
        notifyDataSetChanged();
    }

    @Override
    protected Class<? extends AbsViewHolder> getViewHolderClass(int viewType) {
        return ShoppingCartViewHolder.class;
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_shopping_cart;
    }

    /**
     * 定义删除按钮点击时回调接口
     *
     * @param <T> Adapter内部数据类型
     */
    public interface OnRemoveButtonClickListener<T> {
        /**
         * 当删除按钮被点击时调用
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param object   The object of the view.
         * @param position The position of the view in the adapter.
         */
        void onRemove(AbsViewHolderAdapter<T> parent, View view, T object, int position);
    }

    /**
     * 定义添加到收藏回调接口
     *
     * @param <T>
     */
    public interface OnAddFavoriteClickListener<T> {
        /**
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param object   The object of the view.
         * @param position The position of the view in the adapter.
         */
        void onAddFavorite(AbsViewHolderAdapter<T> parent, View view, T object, int position);
    }

    public interface OnShoppingCartChangeListener {
        void onShoppingCartChange(int position, int count);
    }


    // ViewHolder内部类
    static class ShoppingCartViewHolder extends AbsViewHolder<Map<String, String>> {
        public ShoppingCartViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, Map<String, String> object) {
            TextView titleView = findViewByIdEfficient(R.id.goods_title);
            titleView.setText(object.get("title"));
            ImageView logoImage = findViewByIdEfficient(R.id.goods_image);
            Netroid.displayImage(object.get("url"), logoImage);
            TextView priceText = findViewByIdEfficient(R.id.goods_price);
            priceText.setText(object.get("price_formatted"));
            TextView marketPriceText = findViewByIdEfficient(R.id.market_price);
            marketPriceText.setText(object.get("market_price_formatted"));
            marketPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            TextView discountText = findViewByIdEfficient(R.id.goods_discount);
//            discountText.setText(object.get("discount"));
            IncreaseReduceTextView irText = findViewByIdEfficient(R.id.goods_number_edit);
            irText.setNumber(Integer.valueOf(object.get("count")));
        }
    }
}
