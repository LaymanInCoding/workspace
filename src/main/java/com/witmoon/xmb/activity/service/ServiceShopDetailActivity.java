package com.witmoon.xmb.activity.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.ShopDetailAdapter;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.service.Comment;
import com.witmoon.xmb.model.service.Product;
import com.witmoon.xmb.model.service.Shop;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class ServiceShopDetailActivity extends BaseActivity {

    private ArrayList<Object> shopArrayList = new ArrayList<>();
    private ShopDetailAdapter adapter;
    private EmptyLayout emptyLayout;
    public Shop shop_detail;
    private RelativeLayout headerView;
    private int shop_id;
    private int comment_num;
    private LinearLayout productHeaderView;
    private int product_num;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_shop_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);

        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);

        shop_id = getIntent().getIntExtra("shop_id", 0);
        product_num = getIntent().getIntExtra("product_num", 0);
        headerView = (RelativeLayout) getLayoutInflater().inflate(R.layout.header_service_shop, mRootView, false);
        adapter = new ShopDetailAdapter(shopArrayList, this);
        final Activity activity = this;
        headerView.findViewById(R.id.shop_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity).setMessage("确定要拨打热线电话吗?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                        shop_detail.getShop_phone()));
                                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                activity.startActivity(intent);
                            }
                        }).setCancelable(true).show();
            }
        });

        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        stringAdapter.addHeaderView(headerView);
        mRootView.setAdapter(stringAdapter);
        ServiceApi.shopDetail(shop_id, shop_detail_listener);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecRequest(shop_id);
            }
        });
    }

    private void setShopHeader(Shop shop){
        RoundedImageView shop_logo = (RoundedImageView) headerView.findViewById(R.id.shop_logo);
        TextView shop_name = (TextView) headerView.findViewById(R.id.shop_name);
        TextView shop_nearby_subway = (TextView) headerView.findViewById(R.id.shop_nearby_subway);
        TextView shop_phone = (TextView) headerView.findViewById(R.id.shop_phone);
        TextView shop_address = (TextView) headerView.findViewById(R.id.shop_address);
        Netroid.displayBabyImage(shop.getShop_logo(),shop_logo);
        shop_name.setText(shop.getShop_name());
        shop_address.setText(shop.getShop_address());
        shop_nearby_subway.setText(shop.getShop_nearby_subway());
        shop_phone.setText(shop.getShop_phone());
    }

    private void setServiceProductsHeader(ArrayList<Product> products){
        productHeaderView = (LinearLayout)getLayoutInflater().inflate(R.layout.header_products, mRootView, false);
        TextView product_num_view = (TextView) productHeaderView.findViewById(R.id.products_num);
        final LinearLayout product_container = (LinearLayout) productHeaderView.findViewById(R.id.product_container);
        for(int i = 0; i < products.size(); i++){
            final Product product = products.get(i);
            LinearLayout productLinearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.item_product, mRootView, false);
            if(i >= 2){
                productLinearLayout.setVisibility(View.GONE);
            }
            if(i == products.size() - 1){
                productLinearLayout.findViewById(R.id.split_line).setVisibility(View.GONE);
            }
            productLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ServiceShopDetailActivity.this,ProductDetailActivity.class);
                    intent.putExtra("product_id",product.getProduct_id());
                    startActivity(intent);
                }
            });
            ImageView product_img = (ImageView) productLinearLayout.findViewById(R.id.product_img);
            TextView  product_name = (TextView) productLinearLayout.findViewById(R.id.product_name);
            TextView  product_market_price = (TextView) productLinearLayout.findViewById(R.id.product_market_price);
            TextView  product_shop_price = (TextView) productLinearLayout.findViewById(R.id.product_shop_price);
            Netroid.displayBabyImage(product.getProduct_img(), product_img);
            product_name.setText(product.getProduct_name());
            product_shop_price.setText("¥"+product.getProduct_shop_price());
            product_market_price.setText("市场价：¥" + product.getProduct_market_price());
            product_container.addView(productLinearLayout);
        }
        product_num_view.setText("服务" + shop_detail.getShop_products_num());

        final TextView product_num_all = (TextView) productHeaderView.findViewById(R.id.product_total_all);
        product_num_all.setText("查看其他" + (shop_detail.getShop_products_num() - 2) + "个服务");

        if(shop_detail.getShop_products_num() <= 2){
            product_num_all.setVisibility(View.GONE);
        }

        product_num_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = product_container.getChildCount();
                for (int i = 0; i < cnt; i++) {
                    product_container.getChildAt(i).setVisibility(View.VISIBLE);
                }
                product_num_all.setVisibility(View.GONE);
            }
        });
        stringAdapter.addHeaderView(productHeaderView);
    }

    private Listener<JSONObject> shop_detail_listener = new Listener<JSONObject>() {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            //设置店铺头部信息
            try {
                JSONObject shopinfoObject = response.getJSONObject("shop_info");
                shopinfoObject.put("index",0);
                shop_detail = Shop.parse(shopinfoObject);
                setShopHeader(shop_detail);
            }catch (JSONException e){
                e.printStackTrace();
            }

            try {
                ArrayList<Product> products = new ArrayList<>();
                JSONArray productsArray = response.getJSONArray("products");
                for(int i = 0; i < productsArray.length(); i++){
                    JSONObject jsonObject = productsArray.getJSONObject(i);
                    products.add(Product.parse(jsonObject));
                }
                setServiceProductsHeader(products);
            }catch (JSONException e){
                e.printStackTrace();
            }

            try {
                JSONObject commentObject = response.getJSONArray("comment").getJSONObject(0);
                commentObject.put("comment_total",shop_detail.getShop_comment_cnt());
                Comment comment = Comment.parse(commentObject);
                shopArrayList.add(comment);
            }catch (JSONException e){
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = response.getJSONArray("other_shop");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    jsonObject.put("index",i);
                    shopArrayList.add(Shop.parse(jsonObject));
                }
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {

            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };
}
