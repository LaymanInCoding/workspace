package com.witmoon.xmb.activity.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.calendarview.SpecialDayViewDecorator;
import com.witmoon.xmb.ui.calendarview.TodayDayViewDecorator;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 签到页面Activity
 * Created by zhyh on 2015/5/13.
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private Calendar mCalendar = Calendar.getInstance();

    private TextView mScoreText;
    private ImageView mSigninBtnImage;
    private MaterialCalendarView mCalendarView;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        AQuery aQuery = new AQuery(this);
        aQuery.id(R.id.signin_rule).clicked(this);
        mScoreText = aQuery.id(R.id.score).getTextView();
        mSigninBtnImage = aQuery.id(R.id.submit_button).clicked(this).getImageView();

        mCalendarView = (MaterialCalendarView) aQuery.id(R.id.calendarView).getView();
        mCalendarView.setTitleFormatter(new DateFormatTitleFormatter(new SimpleDateFormat
                ("yyyy年MM月", Locale.CHINA)));
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendarView.setMinimumDate(mCalendar);
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mCalendarView.setMaximumDate(mCalendar);
        mCalendarView.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(MaterialCalendarView widget, CalendarDay date) {
            }
        });
        mCalendarView.addDecorator(new TodayDayViewDecorator());

        // 调用初始化信息
        HomeApi.signinData(mSigninDataCallback);
    }

    @Override
    protected String getActionBarTitle() {
        return "签到领积分";
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_kin));
    }

    // 加载签到信息监听器
    private Listener<JSONObject> mSigninDataCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToast(twoTuple.second);
                return;
            }
            try {
                JSONObject dataObj = response.getJSONObject("data");

                mScoreText.setText(dataObj.getString("sign_score"));
                if (dataObj.getInt("signed") == 1) {
                    mSigninBtnImage.setImageResource(R.mipmap.signed_circle_btn);
                    mSigninBtnImage.setOnClickListener(null);
                }

                JSONArray signDays = dataObj.getJSONArray("days");
                ArrayList<CalendarDay> signedDays = new ArrayList<>(signDays.length());
                for (int i = 0; i < signDays.length(); i++) {
                    mCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(signDays.getString(i)));
                    CalendarDay day = new CalendarDay(mCalendar);
                    signedDays.add(day);
                }
                mCalendarView.addDecorator(new SpecialDayViewDecorator(Color.RED, signedDays));
            } catch (JSONException ignored) {
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button:
                HomeApi.signin(mSigninCallback);
                break;
            case R.id.signin_rule:
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("小麻包签到规则")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setMessage("1、每天签到能领取10个麻豆奖励，签到成功麻豆奖励直接放入您的账户中，可享受小麻包" +
                                "平台的所有麻豆优惠政策。\n\n" + "2、如因不可抗力、大面积作弊等情况导致难以继续开展本活动，小麻包可自觉取消、修改" +
                                "或暂停本活动，法律法规许可范围内，小麻包有权对活动进行解释。").show();
                TextView message = (TextView) dialog.findViewById(android.R.id.message);
                message.setLineSpacing(1.2f, 1.2f);
                message.setTextSize(13);
                break;
        }
    }

    // 签到回调接口
    private Listener<JSONObject> mSigninCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (twoTuple.first) {
                AppContext.showToastShort("签到成功");
                HomeApi.signinData(mSigninDataCallback);
                return;
            }
            AppContext.showToast(twoTuple.second);
        }
    };
}
