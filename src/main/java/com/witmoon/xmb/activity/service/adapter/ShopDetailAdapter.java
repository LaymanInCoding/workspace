package com.witmoon.xmb.activity.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.ServiceCommentActivity;
import com.witmoon.xmb.activity.service.ServiceShopDetailActivity;
import com.witmoon.xmb.activity.service.UserInfoActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.service.Comment;
import com.witmoon.xmb.model.service.Shop;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ZCM on 2016/1/25
 */
public class ShopDetailAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<Object> mList;
    private Context mContext;
    private int width;

    public ShopDetailAdapter(ArrayList<Object> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.width = wm.getDefaultDisplay().getWidth();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_sub_shop, parent, false);
            return new ViewHolderShop(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_d, parent, false);
            return new ViewHolderShop(view);
        } else if (viewType == 2) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_comment_d, parent, false);
            return new ViewHolderComment(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = mList.get(position);
        if (object instanceof Shop) {
            if (((Shop) object).getShop_index() == 0) {
                return 1;
            } else {
                return 0;
            }
        } else if (object instanceof Comment) {
            return 2;
        }

        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Object object = mList.get(position);
        if (object instanceof Shop) {
            ((ViewHolderShop) holder).shop_name.setText(((Shop) object).getShop_name());
            ((ViewHolderShop) holder).shop_desc.setText(((Shop) object).getShop_desc());
            ((ViewHolderShop) holder).shop_nearby_subway.setText(((Shop) object).getShop_nearby_subway());
            ((ViewHolderShop) holder).shop_city.setText(((Shop) object).getCity());
            Netroid.displayBabyImage(((Shop) object).getShop_logo(), ((ViewHolderShop) holder).shop_logo);
            ((ViewHolderShop) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ServiceShopDetailActivity.class);
                    intent.putExtra("shop_id", ((Shop) object).getShop_id());
                    intent.putExtra("shop_name",((Shop) object).getShop_name());
                    mContext.startActivity(intent);
                }
            });
        } else if (object instanceof Comment) {
            Netroid.displayBabyImage(((Comment) object).getComment_header_img(), ((ViewHolderComment) holder).comment_header_img);
            ((ViewHolderComment) holder).comment_username.setText(((Comment) object).getComment_username());
            ((ViewHolderComment) holder).comment_date.setText(((Comment) object).getComment_date());
            ((ViewHolderComment) holder).comment_content.setText(((Comment) object).getComment_content());
            ArrayList<String> comment_thumb_imgs = ((Comment) object).getComment_thumb_imgs();
            ArrayList<String> comment_imgs = ((Comment) object).getComment_imgs();
            ((ViewHolderComment) holder).container1.removeAllViews();
            ((ViewHolderComment) holder).container2.removeAllViews();
            if (comment_thumb_imgs.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    ImageView imageView = new ImageView(mContext);
                    int w = (int) (mContext.getResources().getDimension(R.dimen.dimen_16_dip));
                    imageView.setLayoutParams(new LinearLayout.LayoutParams((this.width - w) / 3, (this.width - w) / 3 - w));
                    imageView.setPadding(w, 0, 0, 0);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Netroid.displayBabyImage(comment_thumb_imgs.get(i), imageView);
                    ((ViewHolderComment) holder).container1.addView(imageView);
                }
                for (int i = 3; i < comment_thumb_imgs.size(); i++) {
                    ImageView imageView = new ImageView(mContext);
                    int w = (int) (mContext.getResources().getDimension(R.dimen.dimen_16_dip));
                    imageView.setLayoutParams(new LinearLayout.LayoutParams((this.width - w) / 3, (this.width - w) / 3 - w));
                    imageView.setPadding(w, 0, 0, 0);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Netroid.displayBabyImage(comment_thumb_imgs.get(i), imageView);
                    ((ViewHolderComment) holder).container2.addView(imageView);
                }
                ((ViewHolderComment) holder).container1.setVisibility(View.VISIBLE);
                ((ViewHolderComment) holder).container2.setVisibility(View.VISIBLE);
            } else if (comment_thumb_imgs.size() != 0) {
                for (int i = 0; i < comment_thumb_imgs.size(); i++) {
                    ImageView imageView = new ImageView(mContext);
                    int w = (int) (mContext.getResources().getDimension(R.dimen.dimen_16_dip));
                    imageView.setLayoutParams(new LinearLayout.LayoutParams((this.width - w) / 3, (this.width - w) / 3 - w));
                    imageView.setPadding(w, 0, 0, 0);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Netroid.displayBabyImage(comment_thumb_imgs.get(i), imageView);
                    ((ViewHolderComment) holder).container1.addView(imageView);
                }
                ((ViewHolderComment) holder).container1.setVisibility(View.VISIBLE);
                ((ViewHolderComment) holder).container2.setVisibility(View.GONE);
            } else {
                ((ViewHolderComment) holder).container1.setVisibility(View.GONE);
                ((ViewHolderComment) holder).container2.setVisibility(View.GONE);
            }
            ((ViewHolderComment) holder).comment_header_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserInfoActivity.class);
                    intent.putExtra("user_id", ((Comment) object).getUser_id());
                    intent.putExtra("user_name", ((Comment) object).getComment_username());
                    intent.putExtra("header_img", ((Comment) object).getComment_header_img());
                    mContext.startActivity(intent);
                }
            });
            ((ViewHolderComment) holder).comment_total_all.setText("查看其它" + ((Comment) object).getComment_cnt() + "个评价");
            ((ViewHolderComment) holder).comment_total_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ServiceCommentActivity.class);
                    intent.putExtra("shop_id", ((Comment) object).getShop_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolderShop extends RecyclerView.ViewHolder {
        TextView shop_name;
        TextView shop_desc;
        ImageView shop_logo;
        TextView shop_nearby_subway;
        TextView shop_city;
        View view;

        public ViewHolderShop(View itemView) {
            super(itemView);
            shop_name = (TextView) itemView.findViewById(R.id.shop_name);
            shop_desc = (TextView) itemView.findViewById(R.id.shop_desc);
            shop_nearby_subway = (TextView) itemView.findViewById(R.id.shop_nearby_subway);
            shop_logo = (ImageView) itemView.findViewById(R.id.shop_logo);
            shop_city = (TextView) itemView.findViewById(R.id.shop_city);
            view = itemView;
        }
    }

    public static class ViewHolderComment extends RecyclerView.ViewHolder {
        TextView comment_date;
        TextView comment_username;
        ImageView comment_header_img;
        TextView comment_content;
        LinearLayout container1, container2;
        TextView comment_total_all;
        View view;

        public ViewHolderComment(View itemView) {
            super(itemView);
            comment_username = (TextView) itemView.findViewById(R.id.comment_username);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            comment_content = (TextView) itemView.findViewById(R.id.comment_content);
            comment_header_img = (ImageView) itemView.findViewById(R.id.comment_header_img);
            container1 = (LinearLayout) itemView.findViewById(R.id.comment_img_container1);
            container2 = (LinearLayout) itemView.findViewById(R.id.comment_img_container2);
            comment_total_all = (TextView) itemView.findViewById(R.id.comment_total_all);
            view = itemView;
        }
    }

}
