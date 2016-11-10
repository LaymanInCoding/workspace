package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONObject;

/**
 * 评价小麻包界面
 * Created by zhyh on 2015/6/21.
 */
public class EvaluateFragment extends BaseFragment {

    private EditText mEvaluateEdit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluate, container, false);

        mEvaluateEdit = (EditText) view.findViewById(R.id.edit_text);
        view.findViewById(R.id.submit_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_button) {
            String evaluate = mEvaluateEdit.getText().toString();
            if (TextUtils.isEmpty(evaluate)) {
                AppContext.showToastShort("请输入评价内容");
                return;
            }
            UserApi.evaluate(evaluate, mEvaluateCallback);
        }
    }

    // 回调
    private Listener<JSONObject> mEvaluateCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            AppContext.showToastShort("发表评价成功");
            getActivity().finish();
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort("服务器异常");
        }
    };
}
