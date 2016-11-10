package com.witmoon.xmb.activity.me.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.UpdataService;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.FileUtils;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * App设置界面
 * Created by zhyh on 2015/6/21.
 */
public class SettingFragment extends BaseFragment {

    private TextView mCacheSizeText;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        AQuery aQuery = new AQuery(getActivity(), view);

        // 绑定事件监听器
        aQuery.id(R.id.personal_info).clicked(this);
        if (AppContext.is_login_or()) {
            aQuery.id(R.id.change_password).visibility(View.GONE);
        }
        aQuery.id(R.id.change_password).clicked(this);
//        aQuery.id(R.id.bind_phone).clicked(this);
        aQuery.id(R.id.service_provision).clicked(this);
        aQuery.id(R.id.clear_cache).clicked(this);
        aQuery.id(R.id.check_update).clicked(this);
        aQuery.id(R.id.about).clicked(this);
        aQuery.id(R.id.submit_button).clicked(this);
        textView = (TextView) view.findViewById(R.id.version_code);
        try {
            textView.setText("当前版本：" + AppContext.geVerSion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCacheSizeText = aQuery.id(R.id.cache_size).getTextView();

        calculateCacheSize();

        return view;
    }

    // 计算缓存大小
    private void calculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();
        File externalCacheDir = getActivity().getExternalCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        fileSize += FileUtils.getDirSize(externalCacheDir);
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        mCacheSizeText.setText(cacheSize);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_info:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.PERSONAL_DATA);
                break;
//            case R.id.change_password:
//                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.CHANGE_PWD);
//                break;
            case R.id.about:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ABOUT);
                break;
            case R.id.service_provision:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SERVICE_PROVISION);
                break;
            case R.id.clear_cache:
                UIHelper.clearAppCache(getActivity());
                mCacheSizeText.setText("0KB");
                break;
            case R.id.check_update:
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setOnKeyListener((dialog, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                });
                builder.setCancelable(false);
                UserApi.Upversion("android", new Listener<JSONObject>() {
                    @Override
                    public void onPreExecute() {
                        showWaitDialog();
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.e("Response",response.toString());
                        //先取版本号，和本地比对。
                        try {
                            final float latest_version = Float.parseFloat(response.getString("latest_version"));
                            final String version_description = response.getString("version_description");
                            final String download_url = response.getString("download_url");
                            final String size = response.getString("size");
                            if (latest_version > Float.parseFloat(AppContext.geVerSion())) {
                                Log.e("respons", response.toString());
                                XmbUtils.showUpdateWindow(getActivity(), latest_version + "", version_description, download_url, size);
                            } else {
                                AppContext.showToast("已是最新版本！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        hideWaitDialog();
                    }
                });
                break;
            case R.id.submit_button:
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("确定要退出登录吗?")
                        .setCancelable(true)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppContext.instance().logout();
                                getActivity().finish();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
        }
    }
}
