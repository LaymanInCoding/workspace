package com.witmoon.xmb.activity.goods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.adapter.Brand_Group_Adapter;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.base.BaseFragment;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by de on 2016/1/18.
 */
public class Brand_Group_Fragment extends BaseFragment{
    private ListView mRootView;
    private ArrayList<Map<String,String>> mapList;
    public static Brand_Group_Fragment newInstance(ArrayList<Map<String,String>> list) {
        Brand_Group_Fragment fragment = new Brand_Group_Fragment();
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

            mRootView.setAdapter(new Brand_Group_Adapter(mapList));
            mRootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupBuyActivity.start(getActivity(), mapList.get(position).get("id"), "");
                }
            });
        }
        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        return mRootView;
    }
}