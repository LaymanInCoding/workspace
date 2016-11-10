package com.witmoon.xmb.activity.shoppingcart.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SimpleBackActivity;
import com.witmoon.xmb.activity.me.AddressManageActivity;
import com.witmoon.xmb.activity.shoppingcart.adapter.AddressChooseAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.ReceiverAddress;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolder;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolderAdapter;
import com.witmoon.xmblibrary.efficientadapter.SimpleAdapter;
import com.witmoon.xmblibrary.recyclerview.SuperRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 确认订单时用于选择收货地址
 * Created by zhyh on 2015/8/6.
 */
public class AddressSelectorFragment extends BaseFragment  {

//    implements AbsViewHolderAdapter
//            .OnItemClickListener<ReceiverAddress>
    private SuperRecyclerView mSuperRecyclerView;
//    private SimpleAdapter<ReceiverAddress> mAddressAdapter;
    private ArrayList<ReceiverAddress> mAddressList = new ArrayList<>();
    private AddressChooseAdapter mAddressChooseAdapter;
    private String mSelectedId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mSelectedId = bundle.getString("selectedId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reciever_address, container, false);

        view.findViewById(R.id.top_toolbar).setVisibility(View.GONE);
        view.findViewById(R.id.submit_button).setOnClickListener(this);//.setVisibility(View.GONE);
        mSuperRecyclerView = (SuperRecyclerView) view.findViewById(R.id.recycler_view);
        mSuperRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mAddressAdapter = new SimpleAdapter<>(R.layout.item_address_selector,
//                AddressViewHolder.class);
        mAddressChooseAdapter = new AddressChooseAdapter(getContext(),mAddressList);
        mAddressChooseAdapter.setOnItemClickListener(position -> {
            mAddressList.get(position).setIsDefault(true);
            Intent intent = new Intent();
            intent.putExtra("address", mAddressList.get(position));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        });
//        mAddressAdapter.setOnItemClickListener(this);
        mSuperRecyclerView.setAdapter(mAddressChooseAdapter);
        TextView emptyText = (TextView) mSuperRecyclerView.getEmptyView();
        emptyText.setText("您还没有收货地址, 快来添加一个吧!");

        // 初始化视图结束, 开始加载网络数据
        UserApi.addressList(mAddressListCallback);

        return view;
    }

    // 收货地址列表回调接口
    private Listener<JSONObject> mAddressListCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            mSuperRecyclerView.showProgress();
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            try {
                mAddressList.clear();
                JSONArray addrArray = response.getJSONArray("data");
                List<ReceiverAddress> dataList = new ArrayList<>();
                for (int i = 0; i < addrArray.length(); i++) {
                    ReceiverAddress address = ReceiverAddress.parse(addrArray.getJSONObject(i),true);
                    address.setIsDefault(false);
                    if (address.getId().equals(mSelectedId)) {
                        address.setIsDefault(true);
                    }
                    dataList.add(address);
                }
                mAddressList.addAll(dataList);
                mAddressChooseAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                AppContext.showToastShort("服务器出现异常");
            }
        }

        @Override
        public void onFinish() {
            mSuperRecyclerView.showRecycler();
        }
    };

//    @Override
//    public void onItemClick(AbsViewHolderAdapter<ReceiverAddress> parent, View view,
//                            ReceiverAddress object, int position) {
//        // TODO: 2015/8/6
//        Intent intent = new Intent();
//        intent.putExtra("address", object);
//        getActivity().setResult(Activity.RESULT_OK, intent);
//        getActivity().finish();
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_button) {
//            UIHelper.showSimpleBackForResult(getActivity(), AddressManageActivity.NEW_ADDRESS,
//                    SimpleBackPage.NEW_ADDRESS);
            Intent intent = new Intent(getActivity(), SimpleBackActivity.class);
            intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, SimpleBackPage.NEW_ADDRESS.getValue());
            startActivityForResult(intent, AddressManageActivity.NEW_ADDRESS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AddressManageActivity.NEW_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    UserApi.addressList(mAddressListCallback);
                }
                break;
        }
    }

//    // ViewHolder
//    static class AddressViewHolder extends AbsViewHolder<ReceiverAddress> {
//
//        public AddressViewHolder(View itemView) {
//            super(itemView);
//        }
//
//        @Override
//        protected void updateView(Context context, ReceiverAddress address) {
//            ((TextView) findViewByIdEfficient(R.id.name)).setText(address.getName());
//            ((TextView) findViewByIdEfficient(R.id.telephone)).setText(address.getTelephone());
//            ((TextView) findViewByIdEfficient(R.id.address)).setText(address.getProvinceName() +
//                    address.getCityName() + address.getDistrictName() + address.getAddress());
//            if (address.isDefault()) {
//                findViewByIdEfficient(R.id.checkbox).setVisibility(View.VISIBLE);
//            }else {
//                findViewByIdEfficient(R.id.checkbox).setVisibility(View.INVISIBLE);
//            }
//        }
//    }
}
