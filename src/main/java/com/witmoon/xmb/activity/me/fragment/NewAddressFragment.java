package com.witmoon.xmb.activity.me.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bigkoo.pickerview.OptionsPickerView;
import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.AreaChooserActivity;
import com.witmoon.xmb.activity.me.AddressManageActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.ProvinceBean;
import com.witmoon.xmb.model.ReceiverAddress;
import com.witmoon.xmb.model.Region;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增收货地址界面
 * Created by zhyh on 2015/6/22.
 */
public class NewAddressFragment extends BaseFragment {

    public static final int AREA_CHOOSER_CODE = 1;

    private ArrayList<ProvinceBean> options1Items = new ArrayList<ProvinceBean>();
    private ArrayList<ArrayList<ProvinceBean>> options2Items = new ArrayList<ArrayList<ProvinceBean>>();
    private ArrayList<ArrayList<ArrayList<ProvinceBean>>> options3Items = new ArrayList<ArrayList<ArrayList<ProvinceBean>>>();
    private EditText mNameEdit;
    private EditText mPhoneEdit;
    private TextView mRegionText;
    private EditText mAddressEdit;

    private String mProvinceId = "";
    private String mCityId = "";
    private String mDistrictId = "";

    private String mAddressId;
    OptionsPickerView pvOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("ADDR_ID")) {
            mAddressId = arguments.getString("ADDR_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_address, container, false);

        AQuery aQuery = new AQuery(getActivity(), view);

        mNameEdit = aQuery.id(R.id.name).getEditText();
        mPhoneEdit = aQuery.id(R.id.phone).getEditText();
        mRegionText = aQuery.id(R.id.region).clicked(this).getTextView();
        mAddressEdit = aQuery.id(R.id.address).getEditText();

        //选项选择器
        pvOptions = new OptionsPickerView(getActivity());
        List<Region> regionListTest = ((AppContext)getActivity().getApplicationContext()).getXmbDB().loadRegions();
        Map<String,List<Region>> map = new HashMap<>();
        for(Region region : regionListTest){
            List<Region> regionTmp = new ArrayList<>();
            if(!map.containsKey(region.getParentId())){
                regionTmp.add(region);
            }else{
                regionTmp = map.get(region.getParentId());
                regionTmp.add(region);
            }
            map.put(region.getParentId(),regionTmp);
        }

        for (Region region : map.get("1")) {
            options1Items.add(new ProvinceBean(region.getId(),region.getName()));
        }

        for(ProvinceBean province : options1Items ){
            List<Region> regionListTmp = map.get(province.getId());
            ArrayList<ProvinceBean> options2Items_tmp=new ArrayList<ProvinceBean>();
            for (Region region : regionListTmp) {
                options2Items_tmp.add(new ProvinceBean(region.getId(),region.getName()));
            }
            options2Items.add(options2Items_tmp);
        }

        for(int i = 0; i < options2Items.size();i++){
            ArrayList<ArrayList<ProvinceBean>> options3Items_0 = new ArrayList<ArrayList<ProvinceBean>>();
            ArrayList<ProvinceBean> tmp = options2Items.get(i);
            for (ProvinceBean province : tmp){
                ArrayList<ProvinceBean> options3Items_0_1=new ArrayList<ProvinceBean>();
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
        pvOptions.setTitle("选择配送区域");
        pvOptions.setCyclic(false, false, false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        int pos1=0,pos2=0,pos3=0;
        if(!mProvinceId.equals("")){
            pos1 = Integer.parseInt(mProvinceId);
        }

        if(!mCityId.equals("")){
            pos2 = Integer.parseInt(mCityId);
        }

        if(!mDistrictId.equals("")){
            pos3 = Integer.parseInt(mDistrictId);
        }

        pvOptions.setSelectOptions(pos1, pos2, pos3);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                mRegionText.setText(options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(options2).getPickerViewText()
                        + options3Items.get(options1).get(options2).get(options3).getPickerViewText());
                mProvinceId = options1Items.get(options1).getId();
                mCityId = options2Items.get(options1).get(options2).getId();
                mDistrictId = options3Items.get(options1).get(options2).get(options3).getId();
            }
        });

        aQuery.id(R.id.submit_button).clicked(this);    // 保存按钮
        aQuery.id(R.id.next_step_btn).clicked(this);    // 设为默认并保存按钮

        return view;
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(mAddressId)) {
            showWaitDialog();
            UserApi.fetchAddress(mAddressId, new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                    if (!twoTuple.first) {
                        getActivity().finish();
                        AppContext.showToastShort(twoTuple.second);
                        return;
                    }
                    try {
                        ReceiverAddress ad = ReceiverAddress.parse(response.getJSONObject("data"),false);
                        mAddressEdit.setText(ad.getAddress());
                        mPhoneEdit.setText(ad.getTelephone());
                        mNameEdit.setText(ad.getName());
                        mRegionText.setText(ad.getProvinceName() + ad.getCityName() + ad
                                .getDistrictName());
                        mProvinceId = ad.getProvinceId();
                        mCityId = ad.getCityId();
                        mDistrictId = ad.getDistrictId();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    hideWaitDialog();
                }
            });
        }
    }

    public void onClick(View v) {
        TwoTuple<Boolean, ReceiverAddress> twoTuple;
        switch (v.getId()) {
            case R.id.region:
                pvOptions.show();
//                startActivityForResult(new Intent(getActivity(), AreaChooserActivity.class),
//                        AREA_CHOOSER_CODE);
                break;
            case R.id.submit_button:    // 保存
                twoTuple = checkInput();
                if (twoTuple.first) {
                    if (TextUtils.isEmpty(mAddressId)) {
                        UserApi.newAddress(twoTuple.second, newCallback);
                    } else {
                        UserApi.updateAddress(mAddressId, twoTuple.second, newCallback);
                    }
                }
                break;
            case R.id.next_step_btn:    // 设为默认并保存
                twoTuple = checkInput();
                if (twoTuple.first) {
                    ReceiverAddress address = twoTuple.second;
                    address.setIsDefault(true);
                    if (TextUtils.isEmpty(mAddressId)) {
                        UserApi.newAddress(twoTuple.second, newCallback);
                    } else {
                        UserApi.updateAddress(mAddressId, twoTuple.second, newCallback);
                    }
                }
                break;
        }
    }

    // 新建或更新地址回调接口
    private Listener<JSONObject> newCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            AppContext.showToastShort("操作成功");
            getActivity().setResult(AddressManageActivity.RESULT_OK);
            getActivity().finish();
        }
    };

    // 检查输入是否正确
    private TwoTuple<Boolean, ReceiverAddress> checkInput() {
        String name = mNameEdit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            AppContext.showToastShort("请输入收货人姓名");
            mNameEdit.requestFocus();
            return new TwoTuple<>(false, null);
        }
        String phone = mPhoneEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            AppContext.showToastShort("请输入收货人联系方式");
            mPhoneEdit.requestFocus();
            return new TwoTuple<>(false, null);
        }
        String region = (String) mRegionText.getText();
        if (TextUtils.isEmpty(region)) {
            AppContext.showToastShort("请选择收货人所在地区");
            return new TwoTuple<>(false, null);
        }
        String address = mAddressEdit.getText().toString();
        if (TextUtils.isEmpty(address)) {
            AppContext.showToastShort("请输入收货人详细地址");
            mAddressEdit.requestFocus();
            return new TwoTuple<>(false, null);
        }
        ReceiverAddress receiverAddress = new ReceiverAddress();
        receiverAddress.setName(name);
        receiverAddress.setAddress(address);
        receiverAddress.setTelephone(phone);
        receiverAddress.setProvinceId(mProvinceId);
        receiverAddress.setCityId(mCityId);
        receiverAddress.setDistrictId(mDistrictId);
        return new TwoTuple<>(true, receiverAddress);
    }
}
