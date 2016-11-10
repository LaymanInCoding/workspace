package com.witmoon.xmb.activity.babycenter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.fragment.PersonalDataFragment;
import com.witmoon.xmb.api.MengbaoApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.DateUtil;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class BabySettingFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener  {
    private View view,baby_rili_container,save_status;
    private Date currentDate = null;
    private TextView baby_rili,baby_nickname;
    private EmptyLayout emptyLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_baby_setting, container, false);
        baby_rili_container = view.findViewById(R.id.baby_rili_container);
        emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        baby_rili = (TextView) view.findViewById(R.id.baby_rili);
        baby_rili.setText(DateUtil.setCurrentDateTime());
        baby_nickname = (TextView) view.findViewById(R.id.baby_nickname);
        save_status = view.findViewById(R.id.save_status);
        baby_rili_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        save_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(baby_nickname.getText().toString().trim().equals("")){
                    XmbUtils.showMessage(getActivity(), "请输入昵称~");
                    return;
                }
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("gender", SharedPreferencesUtil.get(getActivity(),Const.BABY_SEX,"1").toString());
                hashMap.put("nickname",baby_nickname.getText().toString());
                hashMap.put("birthday",baby_rili.getText().toString());
                hashMap.put("baby_id",AppContext.getLoginInfo().getBaby_id());
                MengbaoApi.saveinfo(hashMap, new Listener<JSONObject>() {
                    @Override
                    public void onPreExecute() {
                        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getInt("status") == 1) {
                                AppContext.addBaby("true");
                                Intent intent1 = new Intent(Const.REFRESH_MENGBAO);
                                getActivity().sendBroadcast(intent1);
                                Intent intent2 = new Intent(Const.FINISH_CHILD_STATUS);
                                getActivity().sendBroadcast(intent2);
                                getActivity().finish();
                            } else {
                                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        return view;
    }

    private void showDatePicker(){
        Date iDate = currentDate;
        PersonalDataFragment.DatePickerDialogFragment fragment = PersonalDataFragment.DatePickerDialogFragment.newInstance(iDate);
        fragment.setOnDateSetListener(BabySettingFragment.this);
        fragment.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String currentDateTime = year + "";
        monthOfYear += 1;
        if(monthOfYear < 10){
            currentDateTime += "-0" + monthOfYear ;
        }else{
            currentDateTime += "-" + monthOfYear ;
        }
        if(dayOfMonth < 10){
            currentDateTime += "-0" + dayOfMonth ;
        }else{
            currentDateTime += "-" + dayOfMonth ;
        }
        currentDate = DateUtil.strToDate(currentDateTime);
        int diff = DateUtil.getGapCount(currentDate, DateUtil.strToDate(DateUtil.setCurrentDateTime()));
        if(diff < 0){
            baby_rili.setText(DateUtil.setCurrentDateTime());
        }else {
            baby_rili.setText(currentDateTime);
        }
    }
}
