package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.BankPickerView;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.ProvinceBean;
import com.witmoon.xmb.model.Region;
import com.witmoon.xmb.util.XmbUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/12/19.
 */
public class BindBankCardFragment extends BaseFragment {

    @BindView(R.id.edit_name)
    EditText mEditName;
    @BindView(R.id.edit_bank_num)
    EditText mEditBankNum;
    @BindView(R.id.edit_phone)
    EditText mEditPhone;
    @BindView(R.id.submit_button)
    Button mSubmitButton;
    @BindView(R.id.region)
    TextView mTvRegion;
    @BindView(R.id.tv_bank)
    TextView mTvBank;

    private ArrayList<ProvinceBean> options1Items = new ArrayList<ProvinceBean>();
    private ArrayList<ArrayList<ProvinceBean>> options2Items = new ArrayList<ArrayList<ProvinceBean>>();
    private ArrayList<ArrayList<ArrayList<ProvinceBean>>> options3Items = new ArrayList<ArrayList<ArrayList<ProvinceBean>>>();

    private ArrayList<String> mBanksList;

    private Map<String, List<Region>> map;

    private String mProvinceId = "";
    private String mCityId = "";
    private String mDistrictId = "";
    private OptionsPickerView pvOptions;
    private BankPickerView pvBanks;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //选项选择器
        initRegionPickerView();
        initBankPickView();
    }

    private void initBankPickView() {
        pvBanks = new BankPickerView(getContext());
        mBanksList = new ArrayList<>();
        mBanksList.add("中国银行");
        mBanksList.add("中国工商银行");
        mBanksList.add("中国建设银行");
        mBanksList.add("中国农业银行");
        mBanksList.add("招商银行");
        mBanksList.add("交通银行");
        pvBanks.setPicker(mBanksList);
        pvBanks.setSelectOptions(0);
        pvBanks.setCyclic(false);
        pvBanks.setOnoptionsSelectListener(new BankPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int index) {
                mTvBank.setText(mBanksList.get(index));
            }
        });

    }

    private void initRegionPickerView() {
        //选项选择器
        pvOptions = new OptionsPickerView(getActivity());
        List<Region> regionListTest = ((AppContext) getActivity().getApplicationContext()).getXmbDB().loadRegions();
        Map<String, List<Region>> map = new HashMap<>();
        for (Region region : regionListTest) {
            List<Region> regionTmp = new ArrayList<>();
            if (!map.containsKey(region.getParentId())) {
                regionTmp.add(region);
            } else {
                regionTmp = map.get(region.getParentId());
                regionTmp.add(region);
            }
            map.put(region.getParentId(), regionTmp);
        }

        for (Region region : map.get("1")) {
            options1Items.add(new ProvinceBean(region.getId(), region.getName()));
        }

        for (ProvinceBean province : options1Items) {
            List<Region> regionListTmp = map.get(province.getId());
            ArrayList<ProvinceBean> options2Items_tmp = new ArrayList<ProvinceBean>();
            for (Region region : regionListTmp) {
                options2Items_tmp.add(new ProvinceBean(region.getId(), region.getName()));
            }
            options2Items.add(options2Items_tmp);
        }

        for (int i = 0; i < options2Items.size(); i++) {
            ArrayList<ArrayList<ProvinceBean>> options3Items_0 = new ArrayList<ArrayList<ProvinceBean>>();
            ArrayList<ProvinceBean> tmp = options2Items.get(i);
            for (ProvinceBean province : tmp) {
                ArrayList<ProvinceBean> options3Items_0_1 = new ArrayList<ProvinceBean>();
                List<Region> regionListTmp = map.get(province.getId());
                if (regionListTmp != null) {
                    for (Region region : regionListTmp) {
                        options3Items_0_1.add(new ProvinceBean(region.getId(), region.getName()));
                    }
                }
                options3Items_0.add(options3Items_0_1);
            }
            options3Items.add(options3Items_0);
        }

        pvOptions.setPicker(options1Items, options2Items, options3Items, true);
        pvOptions.setTitle("请选择所在地");
        pvOptions.setCyclic(false, false, false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        int pos1 = 0, pos2 = 0, pos3 = 0;
        if (!mProvinceId.equals("")) {
            pos1 = Integer.parseInt(mProvinceId);
        }

        if (!mCityId.equals("")) {
            pos2 = Integer.parseInt(mCityId);
        }

        if (!mDistrictId.equals("")) {
            pos3 = Integer.parseInt(mDistrictId);
        }

        pvOptions.setSelectOptions(pos1, pos2, pos3);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                mTvRegion.setText(options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(options2).getPickerViewText()
                        + options3Items.get(options1).get(options2).get(options3).getPickerViewText());
                mProvinceId = options1Items.get(options1).getId();
                mCityId = options2Items.get(options1).get(options2).getId();
                mDistrictId = options3Items.get(options1).get(options2).get(options3).getId();
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_card, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.region, R.id.submit_button, R.id.tv_bank})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.region:
                pvOptions.show();
                break;
            case R.id.tv_bank:
                pvBanks.show();
                break;
            case R.id.submit_button:
                break;
        }
    }

}
