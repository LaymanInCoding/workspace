package com.witmoon.xmb.activity.goods.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
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
        mGridLayout.setColumnCount(1);
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
            Logger.json(response.toString());
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (tt.first) {
                try {
                    JSONArray propArray = response.getJSONArray("data");
                    for (int i = 0; i < propArray.length(); i++) {
                        JSONObject propAttr = propArray.getJSONObject(i);
                        if (propAttr.getString("name") != null) {
                            mGridLayout.addView(createLabel(propAttr.getString("name"),
                                    propAttr.getString("value")));
                        }
                    }
                } catch (JSONException ignored) {

                }
            }
        }
    };

    // 创建规格参数TextView
    private View createLabel(String name, String detail) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_goods_spec, null);
        TextView nameTv = (TextView) view.findViewById(R.id.name);
        nameTv.setText(name + ":");
        TextView detailTv = (TextView) view.findViewById(R.id.detail);
        detailTv.setText(detail);

        return view;
    }
}
