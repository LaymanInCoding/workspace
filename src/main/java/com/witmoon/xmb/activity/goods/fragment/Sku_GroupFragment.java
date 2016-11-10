package com.witmoon.xmb.activity.goods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.goods.adapter.SkuGroupAdapter;
import com.witmoon.xmb.base.BaseFragment;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2016/1/18
 */
public class Sku_GroupFragment extends BaseFragment implements SkuGroupAdapter.OnItemButtonClickListener{
    private ListView mRootView;
    private SkuGroupAdapter mAdapter;
    private ArrayList<Map<String,String>> mapList;
    public static Sku_GroupFragment newInstance(ArrayList<Map<String,String>> list) {
        Sku_GroupFragment fragment = new Sku_GroupFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("maplist", (ArrayList) list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapList = (ArrayList)getArguments().getParcelableArrayList("maplist");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = new ListView(getActivity());
            mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mRootView.setId(R.id.id_stickynavlayout_innerscrollview);
            mRootView.setSelector(R.color.list_item_background_normal);
            mAdapter = new SkuGroupAdapter(getContext(),mapList);
            mAdapter.setOnItemButtonClickListener(this);
            mRootView.setAdapter(mAdapter);
        }

        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        return mRootView;
    }

    @Override
    public void onItemButtonClick(String id){
        CommodityDetailActivity.start(getContext(), id);
    }
}
