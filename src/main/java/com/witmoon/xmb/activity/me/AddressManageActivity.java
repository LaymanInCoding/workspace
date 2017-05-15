package com.witmoon.xmb.activity.me;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.adapter.ReceiverAddressAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.ReceiverAddress;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.recyclerview.SuperRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 收货地址管理Activity
 * Created by zhyh on 2015/6/2.
 */
public class AddressManageActivity extends BaseActivity {

    public static final int NEW_ADDRESS = 0x01;

    private ReceiverAddressAdapter mAddressAdapter;
    private List<ReceiverAddress> mAddressList = new ArrayList<>();
    private SuperRecyclerView mSuperRecyclerView;

    // 启动AddressManageActivity
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddressManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reciever_address;
    }

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.text_receiver_address;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_me));
        setTitleColor_(R.color.master_me);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        mSuperRecyclerView = (SuperRecyclerView) findViewById(R.id.recycler_view);
        mSuperRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddressAdapter = new ReceiverAddressAdapter(this, mAddressList);
        // 设置为默认收货地址
        mAddressAdapter.setOnSetDefaultClickListener(new ReceiverAddressAdapter
                .OnSetDefaultClickListener() {
            @Override
            public void onSetDefaultClick(String id) {
                setDefaultAddress(id);
            }
        });
        // 删除地址
        mAddressAdapter.setOnDeleteClickListener(new ReceiverAddressAdapter
                .OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String id, int position) {
                if (null != getIntent().getStringExtra("orderConfirm")) {
                    XmbUtils.showMessage(AddressManageActivity.this, "当前页不可进行删除操作");
                } else {
                    deleteAddress(id, position);
                }
            }
        });
        // 编辑地址
        mAddressAdapter.setOnEditClickListener(new ReceiverAddressAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(String id) {
                Bundle bundle = new Bundle();
                bundle.putString("ADDR_ID", id);
                UIHelper.showSimpleBackForResult(AddressManageActivity.this, NEW_ADDRESS,
                        SimpleBackPage.NEW_ADDRESS, bundle);
            }
        });
        //若该activity是由确认订单页面启动  则添加此监听
        if (null != getIntent().getStringExtra("orderConfirm")) {
            mAddressAdapter.setOnConfirmListener(new ReceiverAddressAdapter.OnConfirmClickListener() {
                @Override
                public void onConfirmClick(int position) {
                    Intent intent = new Intent();
                    intent.putExtra("address", mAddressList.get(position));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            mAddressAdapter.setOnConfirmListener(null);
        }

        mSuperRecyclerView.setAdapter(mAddressAdapter);

        // 添加新收货地址
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBackForResult(AddressManageActivity.this, NEW_ADDRESS,
                        SimpleBackPage.NEW_ADDRESS);
            }
        });


        // 初始化视图结束, 开始加载网络数据
        mAddressList.clear();
        UserApi.addressList(mAddressListCallback);
    }

    // 收货地址列表回调接口
    private Listener<JSONObject> mAddressListCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            mSuperRecyclerView.showProgress();
        }

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            try {
                JSONArray addrArray = response.getJSONArray("data");
                for (int i = 0; i < addrArray.length(); i++) {
                    ReceiverAddress address = ReceiverAddress.parse(addrArray.getJSONObject(i), false);
                    if (address.isDefault()) {
                        mAddressList.add(0, address);
                        continue;
                    }
                    mAddressList.add(address);
                }
                if (mAddressList.size() == 0) {
                    TextView emptyText = (TextView) mSuperRecyclerView.getEmptyView();
                    emptyText.setText("您还没有收货地址, 快来添加一个吧!");
                }
                mAddressAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("data", e.getMessage());
                AppContext.showToastShort("服务器出现异常");
            }
        }

        @Override
        public void onFinish() {
            mSuperRecyclerView.showRecycler();
        }
    };

    // 设置默认收货地址
    private void setDefaultAddress(final String id) {
        UserApi.setDefaultAddr(id, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                if (!twoTuple.first) {
                    AppContext.showToastShort(twoTuple.second);
                    return;
                }
                for (ReceiverAddress address : mAddressList) {
                    if (address.isDefault()) {
                        address.setIsDefault(false);
                    }
                    if (address.getId().equals(id)) {
                        address.setIsDefault(true);
                    }
                }

                mAddressAdapter.notifyDataSetChanged();
            }
        });
    }

    // 删除地址
    private void deleteAddress(final String id, final int position) {
        new AlertDialog.Builder(AddressManageActivity.this).setMessage("确定删除吗?")
                .setCancelable(true).setNegativeButton("取消", null).setPositiveButton("确定", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserApi.deleteAddress(id, new Listener<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                mAddressList.remove(position);
                                if (mAddressList.size() == 0) {
                                    TextView emptyText = (TextView) mSuperRecyclerView.getEmptyView();
                                    emptyText.setText("您还没有收货地址, 快来添加一个吧!");
                                }
                                mAddressAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).show();
    }

    // 刷新
    private void refresh() {
        mAddressList.clear();
        UserApi.addressList(mAddressListCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("data", requestCode + "----" + resultCode);
        switch (requestCode) {
            case NEW_ADDRESS:
                if (resultCode == RESULT_OK) {
                    refresh();
                }
                break;
        }
    }
}
