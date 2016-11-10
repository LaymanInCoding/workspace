package com.witmoon.xmb.activity.shoppingcart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart  extends  RecyclerView.Adapter<ShoppingCart.ViewHolder> {


    private OnClickListener mOnClickListener;

    private Context context;
    private List<Map<String, String>> mDatas;
    public ShoppingCart(Context context,List<Map<String, String>> mDatas){
        this.context = context;
        this.mDatas = mDatas;
    }

    public interface OnClickListener {
        void OnRemoveButtonClickListener(String recId);
        void OnAddFavoriteClickListener(String goodsId);
        void OnShoppingCartChangeListener(int position,boolean is_checked,int number);
    }

    public void OnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public ShoppingCart.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopping_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShoppingCart.ViewHolder holder, final int position) {
        final HashMap<String,String> hashMap = (HashMap<String, String>) mDatas.get(position);
        ImageLoader.getInstance().displayImage(hashMap.get("image"), holder.logoImage, AppContext.options_disk);
        holder.titleView.setText(hashMap.get("title"));
        holder.checkBox.setChecked(hashMap.get("checked").equals("1"));
        holder.priceText.setText(hashMap.get("price_formatted"));
        holder.irText.setNumber(Integer.parseInt(hashMap.get("count")));
        if(hashMap.get("is_group").equals("0")){
            holder.image3.setVisibility(View.GONE);
        }else{
            holder.image3.setVisibility(View.VISIBLE);
        }
        if(hashMap.get("is_cross_border").equals("0")){
            holder.image2.setVisibility(View.GONE);
        }else{
            holder.image2.setVisibility(View.VISIBLE);
        }
        if(hashMap.get("is_third").equals("0")){
            holder.image4.setVisibility(View.GONE);
        }else{
            holder.image4.setVisibility(View.VISIBLE);
        }
        if(hashMap.get("coupon_disable").equals("0")){
            holder.image1.setVisibility(View.GONE);
        }else{
            holder.image1.setVisibility(View.VISIBLE);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener != null){
                    mOnClickListener.OnShoppingCartChangeListener(position, holder.checkBox.isChecked(), holder.irText.getNumber());
                }
            }
        });
        holder.irText.setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
            @Override
            public void onNumberChange(int number) {
                if (mOnClickListener != null) {
                    mOnClickListener.OnShoppingCartChangeListener(position, holder.checkBox.isChecked(), holder.irText.getNumber());
                }
            }
        });
        holder.add_favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.OnAddFavoriteClickListener(hashMap.get("goods_id"));
                }
            }
        });
        holder.remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.OnRemoveButtonClickListener(hashMap.get("id"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image1,image2,image3,image4,logoImage;
        public CheckBox checkBox;
        public TextView titleView,priceText;
        public IncreaseReduceTextView irText;
        public View add_favorite_button,remove_button;
        public ViewHolder(View itemView) {
            super(itemView);
            image1 = (ImageView) itemView.findViewById(R.id.coupon_disable);
            image2 = (ImageView) itemView.findViewById(R.id.cross_g);
            image3 = (ImageView) itemView.findViewById(R.id.group_g);
            image4 = (ImageView) itemView.findViewById(R.id.thrid_goods);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            titleView = (TextView) itemView.findViewById(R.id.goods_title);
            logoImage = (ImageView) itemView.findViewById(R.id.goods_image);
            priceText = (TextView) itemView.findViewById(R.id.goods_price);
            irText = (IncreaseReduceTextView) itemView.findViewById(R.id.goods_number_edit);
            add_favorite_button = itemView.findViewById(R.id.add_favorite_button);
            remove_button = itemView.findViewById(R.id.remove_button);
        }
    }
}
