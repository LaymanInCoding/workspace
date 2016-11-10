package com.witmoon.xmb.activity.babycenter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.fragment.PersonalDataFragment;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.api.MengbaoApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.WheelView;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class OverdueSettingFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener  {
    private TextView last_menstruation_time,due_date,menstruation_cycle_date;
    private View view,overdue_jsq,menstruation_cycle,menstruation_cycle_container,menstruation_confirm_btn,save_status_btn;
    private int current_type = 0;
    private WheelView numberPicker;
    private ArrayList<String> numberArr;
    private int current_index = 8;
    private EmptyLayout emptyLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overdue_setting, container, false);
        emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        last_menstruation_time = (TextView) view.findViewById(R.id.last_menstruation_time);
        numberPicker = (WheelView)view.findViewById(R.id.numberPicker);
        menstruation_cycle_container = view.findViewById(R.id.menstruation_cycle_container);
        menstruation_confirm_btn = view.findViewById(R.id.menstruation_confirm_btn);
        menstruation_cycle_date = (TextView) view.findViewById(R.id.menstruation_cycle_date);
        save_status_btn = view.findViewById(R.id.save_status);
        numberArr = new ArrayList<>();
        numberArr.add("");
        for(int i = 20;i<=45;i++){
            numberArr.add(i+"");
        }
        numberArr.add("");
        numberPicker.setItems(numberArr);
        numberPicker.setOffset(2);
        menstruation_cycle = view.findViewById(R.id.menstruation_cycle);
        due_date = (TextView) view.findViewById(R.id.due_date);
        overdue_jsq= view.findViewById(R.id.overdue_jsq);
        due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_type = 0;
                showDatePicker();
            }
        });

        save_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("overdue_date",due_date.getText().toString());
                hashMap.put("last_period_data",last_menstruation_time.getText().toString());
                hashMap.put("period_circle", menstruation_cycle_date.getText().toString());
                MengbaoApi.saveinfo(hashMap, new Listener<JSONObject>() {
                    @Override
                    public void onPreExecute() {
                        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if(response.getInt("status") == 1){
                                AppContext.addBaby("true");
                                Intent intent = new Intent(Const.REFRESH_MENGBAO);
                                getActivity().sendBroadcast(intent);
                                getActivity().finish();
                            }else{
                                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                getActivity().finish();
            }
        });

        menstruation_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_index = numberPicker.getSeletedIndex();
                menstruation_cycle_date.setText(numberArr.get(numberPicker.getSeletedIndex()+1));
                due_date.setText(DateUtil.getDateBeforeOrAfter(DateUtil.strToDate(due_date.getText().toString()), Integer.parseInt(menstruation_cycle_date.getText().toString()) - 28));
                menstruation_cycle_container.setVisibility(View.GONE);
            }
        });

        menstruation_cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menstruation_cycle_container.setVisibility(View.VISIBLE);
                numberPicker.setSeletion(current_index);
            }
        });

        overdue_jsq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view.findViewById(R.id.overdue_container).getVisibility() == View.GONE) {
                    view.findViewById(R.id.overdue_container).setVisibility(View.VISIBLE);
                    due_date.setClickable(false);
                }else{
                    view.findViewById(R.id.overdue_container).setVisibility(View.GONE);
                    due_date.setClickable(true);
                }
            }
        });

        last_menstruation_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_type = 1;
                showDatePicker();
            }
        });

        last_menstruation_time.setText(DateUtil.setCurrentDateTime());
        due_date.setText(DateUtil.getDateBeforeOrAfter(DateUtil.getCurrentDate(), 279));

        bindEvent();
        return view;
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

        int diff = DateUtil.getGapCount(DateUtil.strToDate(currentDateTime), DateUtil.strToDate(DateUtil.setCurrentDateTime()));

        if(current_type == 0 ){
            if(diff > 0){
                due_date.setText(DateUtil.setCurrentDateTime());
            }else{
                due_date.setText(currentDateTime);
            }
            return;
        }

        if(diff >= -279 && diff < 0){
            last_menstruation_time.setText(DateUtil.setCurrentDateTime());
            due_date.setText(DateUtil.getDateBeforeOrAfter(DateUtil.getCurrentDate(), 279 + Integer.parseInt(menstruation_cycle_date.getText().toString()) - 28));
        }else{
            last_menstruation_time.setText(currentDateTime);
            due_date.setText(DateUtil.getDateBeforeOrAfter(DateUtil.strToDate(currentDateTime), 279));
        }
    }

    private void showDatePicker(){
        Date iDate = DateUtil.strToDate(last_menstruation_time.getText().toString());
        PersonalDataFragment.DatePickerDialogFragment fragment = PersonalDataFragment.DatePickerDialogFragment.newInstance(iDate);
        fragment.setOnDateSetListener(OverdueSettingFragment.this);
        fragment.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }

    private void bindEvent(){
        view.findViewById(R.id.cal_overdue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                intent.putExtra("url", "file:///android_asset/duedate.html");
                startActivity(intent);
            }
        });
    }
}
