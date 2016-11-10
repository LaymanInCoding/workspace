package com.witmoon.xmb.activity.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.witmoon.xmb.activity.friendship.fragment.CommentFragment;
import com.witmoon.xmb.activity.me.Out_ServiceActivity;
import com.witmoon.xmb.activity.me.adapter.OrderAdapter;
import com.witmoon.xmb.activity.me.adapter.Out_PriceAdapter;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.model.Order;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de on 2015/12/3.
 */
public class AppliedFragment extends BaseRecyclerViewFragmentV2{
    Out_PriceAdapter mOut_PriceAdapter;
    private Out_ out;
    private Out_ mOut_;
    private boolean is_ = false;
    private BroadcastReceiver mOut = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            out = (Out_) intent.getSerializableExtra("order");
            for (int i=0;i<mAdapter.getData().size();i++)
            {
                mOut_ = (Out_) mAdapter.getData().get(i);
                if (mOut_.getOrder_sn().equals(out.getOrder_sn()))
                {
                    ((Out_) mAdapter.getData().get(i)).setRefund_status(out.getRefund_status());
                    mAdapter.notifyItemChanged(i);
                    is_ = true;
                }
            }
            if(is_)
            {
                mAdapter.getData().add(out);
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        is_dow = true;
        getActivity().registerReceiver(mOut, new IntentFilter(Const.INTENT_ACTION_TUI));
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        mOut_PriceAdapter = new Out_PriceAdapter(getActivity());
        mOut_PriceAdapter.setOnItemButtonClickListener(new Out_PriceAdapter.OnItemButtonClickListener() {
            @Override
            public void onItemButtonClick(Out_ order, int position) {
                order.setPosion(position);
                order.setIs_(1);
                if (order.getRefund_status().equals("1")){
                    Intent mIntent = new Intent(getActivity(), Out_ServiceActivity.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                    return;
                }
                Bundle mb = new Bundle();
                mb.putSerializable("order", order);
                UIHelper.showSimpleBack(getActivity(),SimpleBackPage.JINDU,mb);
            }
        });
        mOut_PriceAdapter.setOnItemTypeClickListener(new Out_PriceAdapter.OnItemTypeClickListener() {
            @Override
            public void OnItemTypeClick(Out_ order, int position) {
                order.setPosion(position);
                order.setIs_(1);
                if (order.getRefund_status().equals("2")) {
                    Bundle mb = new Bundle();
                    mb.putSerializable("order", order);
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.JINDU, mb);
                } else if (order.getRefund_status().equals("3")) {
                    Intent mIntent = new Intent(getActivity(), Out_ServiceActivity.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                } else if (order.getRefund_status().equals("5")) {
                    Intent mIntent = new Intent(getActivity(), Fill_info_Fragent.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                }
            }
        });
        mOut_PriceAdapter.setmOnItemAllClickListener(new Out_PriceAdapter.OnItemAllClickListener() {
            @Override
            public void OnItemAllClickListener(Out_ order) {
                Bundle argument = new Bundle();
                argument.putString(OrderDetailFragment.KEY_ORDER_TYPE, "");
                argument.putString(OrderDetailFragment.KEY_ORDER_SN, order.getOrder_sn());
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER_DETAIL, argument);
            }
        });
        return mOut_PriceAdapter;
    }

    @Override
    protected void requestData() {
        UserApi.applyList(mCurrentPage + "", getDefaultListener());
    }

    @Override
    protected ListEntity parseResponse(JSONObject json) throws Exception {
        JSONArray orderArray = json.getJSONArray("data");
        final List<Out_> outs = new ArrayList<Out_>();
        Log.e("parseResponse", orderArray.toString());
        for (int i = 0; i < orderArray.length(); i++) {
            outs.add(Out_.parse(orderArray.getJSONObject(i)));
        }
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return outs;
            }
            @Override
            public boolean hasMoreData() {
                    return true;
            }
        };
    }

    @Override
    protected void onItemClick(View view, int position) {
//        Out_ order = (Out_) mAdapter.getData().get(position);
//        Bundle argument = new Bundle();
//        argument.putString(OrderDetailFragment.KEY_ORDER_TYPE, "");
//        argument.putString(OrderDetailFragment.KEY_ORDER_ID, order.getOrder_id());
//        UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER_DETAIL, argument);
    }
}
