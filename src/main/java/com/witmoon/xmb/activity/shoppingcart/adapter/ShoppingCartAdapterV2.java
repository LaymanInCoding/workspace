package com.witmoon.xmb.activity.shoppingcart.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolder;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolderAdapter;

import java.util.List;
import java.util.Map;

/**
 * 购物车列表数据适配器
 * Created by zhyh on 2015/5/25.
 */
public class ShoppingCartAdapterV2<T> extends AbsViewHolderAdapter<T> {

    private OnRemoveButtonClickListener<T> mOnRemoveButtonClickListener;
    private OnAddFavoriteClickListener<T> mOnAddFavoriteClickListener;
    private OnShoppingCartChangeListener mOnShoppingCartChangeListener;

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

    public ShoppingCartAdapterV2(List<T> dataList) {
        super(dataList);
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
        final IncreaseReduceTextView irText = (IncreaseReduceTextView) viewHolder
                .findViewByIdEfficient(R.id.goods_number_edit);

//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                setOnShoppingCartChangeListener(position, isChecked, irText.getNumber());
//            }
//        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnShoppingCartChangeListener(position, checkBox.isChecked(), irText.getNumber());
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
        viewHolder.findViewByIdEfficient(R.id.check_lin).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnShoppingCartChangeListener(position, !checkBox.isChecked(), irText.getNumber());
                checkBox.setChecked(checkBox.isChecked());
            }
        });
        irText.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int number) {
                setOnShoppingCartChangeListener(position, checkBox.isChecked(), irText.getNumber());
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

    private void setOnShoppingCartChangeListener(int position, boolean checked, int count) {
        if (mOnShoppingCartChangeListener != null) {
            mOnShoppingCartChangeListener.onShoppingCartChange(position, checked, count);
        }
    }

    /**
     * 更新全选状态
     *
     * @param checked 是否全选
     */
    public void checkAll(boolean checked) {
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
     * 定义全部按钮点击时回调接口
     *
     * @param <T> Adapter内部数据类型
     */
//    public interface OnAllButtonClickListener<T> {
//        void onShoppingCartChange(int position, boolean checked, int count);
//    }

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
        void onShoppingCartChange(int position, boolean checked, int count);
    }


    // ViewHolder内部类
    static class ShoppingCartViewHolder extends AbsViewHolder<Map<String, String>> {
        public ShoppingCartViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, Map<String, String> object) {
            ImageView image1 = findViewByIdEfficient(R.id.coupon_disable);
            ImageView image2 = findViewByIdEfficient(R.id.cross_g);
            ImageView image3 = findViewByIdEfficient(R.id.group_g);
            ImageView image4 = findViewByIdEfficient(R.id.thrid_goods);
            if (!object.get("is_group").equals("0"))
            {
                image3.setVisibility(View.VISIBLE);
            }else{
                image3.setVisibility(View.GONE);
            }
            if (!object.get("is_third").equals("0"))
            {
                image4.setVisibility(View.VISIBLE);
            }else{
                image4.setVisibility(View.GONE);
            }
            if (!object.get("coupon_disable").equals("0"))
            {
                image1.setVisibility(View.VISIBLE);
            }else{
                image1.setVisibility(View.GONE);
            }
            if (!object.get("is_cross_border").equals("0"))
            {
                image2.setVisibility(View.VISIBLE);
            }else{
                image2.setVisibility(View.GONE);
            }
            CheckBox checkBox = findViewByIdEfficient(R.id.checkbox);
            checkBox.setChecked(object.get("checked").equals("1"));

            TextView titleView = findViewByIdEfficient(R.id.goods_title);
            titleView.setText(object.get("title"));

            ImageView logoImage = findViewByIdEfficient(R.id.goods_image);
            Netroid.displayImage(object.get("image"), logoImage);

            TextView priceText = findViewByIdEfficient(R.id.goods_price);
            priceText.setText(object.get("price_formatted"));

            IncreaseReduceTextView irText = findViewByIdEfficient(R.id.goods_number_edit);
            irText.setNumber(Integer.valueOf(object.get("count")));
        }
    }
}
