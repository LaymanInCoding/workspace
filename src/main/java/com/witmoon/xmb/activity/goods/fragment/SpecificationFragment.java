package com.witmoon.xmb.activity.goods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 商品规格参数Fragment
 * Created by zhyh on 2015/8/3.
 */
public class SpecificationFragment extends BaseFragment {
    private String mGoodsId;
    private GridLayout mGridLayout;
    public static SpecificationFragment newInstance(String goodsId) {
        SpecificationFragment fragment = new SpecificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GOODS_ID", goodsId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mGoodsId = bundle.getString("GOODS_ID");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.setId(R.id.id_stickynavlayout_innerscrollview);
        scrollView.setPadding(12, 12, 12, 12);

        mGridLayout = new GridLayout(container.getContext());
        mGridLayout.setColumnCount(2);
        mGridLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scrollView.addView(mGridLayout);

        GoodsApi.goodsProperties(mGoodsId, mPropertiesCallback);

        return scrollView;
    }

    // 回调接口
    private Listener<JSONObject> mPropertiesCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Log.e("response",response.toString());
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (tt.first) {
                try {
                    JSONArray propArray = response.getJSONArray("data");
                    for (int i = 0; i < propArray.length(); i++) {
                        JSONObject propAttr = propArray.getJSONObject(i);
                        mGridLayout.addView(createLabel(propAttr.getString("name"), true));
                        mGridLayout.addView(createLabel(propAttr.getString("value"), false));
                    }
                } catch (JSONException ignored) {

                }
            }
        }
    };

    // 创建规格参数TextView
    private TextView createLabel(String label, boolean isName) {
        if (null == label) label = "";
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(4, 2, 4, 2);
        if (isName) {
            textView.getPaint().setFakeBoldText(true);
            label += "：";
        }
        textView.setText(label);
        return textView;
    }
}
