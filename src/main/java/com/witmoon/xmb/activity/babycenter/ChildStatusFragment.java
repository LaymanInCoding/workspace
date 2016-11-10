package com.witmoon.xmb.activity.babycenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.UIHelper;

public class ChildStatusFragment extends BaseFragment {
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_child_status, container, false);
        bindEvent();
        return view;
    }

    private void bindEvent(){
        view.findViewById(R.id.boy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.remove(getActivity(), Const.BABY_SEX);
                SharedPreferencesUtil.put(getActivity(), Const.BABY_SEX,"1");
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BabySetting);
            }
        });
        view.findViewById(R.id.girl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.remove(getActivity(), Const.BABY_SEX);
                SharedPreferencesUtil.put(getActivity(), Const.BABY_SEX,"0");
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BabySetting);
            }
        });
        IntentFilter intentFilter = new IntentFilter(Const.FINISH_CHILD_STATUS);
        getActivity().registerReceiver(finish_child_status, intentFilter);
    }

    private BroadcastReceiver finish_child_status = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().finish();
        }
    };

}
