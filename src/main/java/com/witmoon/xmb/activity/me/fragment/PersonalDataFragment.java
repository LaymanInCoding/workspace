package com.witmoon.xmb.activity.me.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.duowan.mobile.netroid.Listener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.User;
import com.witmoon.xmb.util.DateUtil;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.SDCardUtils;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import library.BitmapUtil;
import library.CropHandler;
import library.CropHelper;
import library.CropParams;

/**
 * 个人资料修改界面
 * Created by zhyh on 2015/6/21.
 */
public class PersonalDataFragment extends BaseFragment implements DialogInterface.OnClickListener, CropHandler {
    private static String[] items = {"选择本地图片", "拍照"};
    private RoundedImageView mAvatarImage;
    private EditText mNickNameEdit;
    private String mParentSex;
    private RadioGroup mParentSexGroup;
    private File mAvatar;
    private CropParams mCropParams;
    private String userGender;
    private String nickName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_data, container, false);
        mCropParams = new CropParams(getActivity());
        mAvatarImage = (RoundedImageView) view.findViewById(R.id.me_avatar_img);
        mAvatarImage.setOnClickListener(this);
        mNickNameEdit = (EditText) view.findViewById(R.id.nick_name);
        mParentSexGroup = (RadioGroup) view.findViewById(R.id.parent_sex);
        mParentSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mParentSex = checkedId == R.id.parent_sex_baba ? "1" : "0";
            }
        });
        view.findViewById(R.id.submit_button).setOnClickListener(this);
        requestData();
        return view;
    }

    public void requestData() {
        UserApi.userInfo(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    Log.e("Data", data.toString());
                    Netroid.displayImage(data.getString("header_img"), mAvatarImage);
                    mNickNameEdit.setText(data.getString("nick_name"));
                    mNickNameEdit.setSelection(data.getString("nick_name").length());
                    userGender = data.getString("parent_sex");
                    if (null != userGender && !TextUtils.isEmpty(userGender)) {
                        mParentSexGroup.check("1".equals(userGender) ? R.id.parent_sex_baba : R.id
                                .parent_sex_mama);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_avatar_img:
                // 弹出图像选择对话框, 可以从相册中选择或者使用相机拍摄
                AvatarDialogFragment avatarDialogFragment = new AvatarDialogFragment();
                avatarDialogFragment.setOnClickListener(this);
                avatarDialogFragment.show(getActivity().getFragmentManager(), "AvatarDialog");
                break;
            case R.id.submit_button:
                ModifyAsyncTask asyncTask = new ModifyAsyncTask(this);
                asyncTask.execute();
                break;
        }
    }

    private Map<String, String> getParamMap() {
        Map<String, String> paramMap = new HashMap<>();
        nickName = mNickNameEdit.getText().toString();
        paramMap.put("nick_name", nickName);
        paramMap.put("parent_sex", mParentSex);

        return paramMap;
    }

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, PersonalDataFragment> {

        public ModifyAsyncTask(PersonalDataFragment personalDataFragment) {
            super(personalDataFragment);
        }

        @Override
        protected String doInBackground(PersonalDataFragment personalDataFragment,
                                        Void... params) {
            Map<String, File> fm = null;
            if (mAvatar != null) {
                fm = new HashMap<>();
                fm.put("header_img", mAvatar);
            }
            Map<String, String> pm = personalDataFragment.getParamMap();
            try {
                Map<String, String> paramObj = ApiHelper.getParamMap(pm);
                String response = HttpUtility.post(ApiHelper.BASE_URL + "users/modinfo",
                        null, paramObj, fm);
                JSONObject respObj = new JSONObject(response);
                Log.e("MOD_RESPONSE", response);
                TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(respObj);
                if (!twoTuple.first) {
                    return twoTuple.second;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PersonalDataFragment personalDataFragment, String result) {
            if (result != null) {
                AppContext.showToastShort(result);
                return;
            }
            User user = AppContext.getLoginInfo();
            user.setName(nickName);
            AppContext.saveLoginInfo(user);
            AppContext.showToastShort("操作成功");
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(this, requestCode, resultCode, data);
    }

    private void saveAvatarBitmap(Bitmap avatar) {
        mAvatarImage.setImageBitmap(avatar);
        mAvatar = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "avatar.png");
        OutputStream os = null;
        try {
            os = new FileOutputStream(mAvatar);
            avatar.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * DialogInterface接口点击响应方法, 用于响应头像来源选择Dialog点击事件
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        mCropParams.refreshUri();
        if (which == 0) {   // 从相册中选择图片
            mCropParams.enable = true;
            mCropParams.compress = false;
            Intent intent = CropHelper.buildGalleryIntent(mCropParams);
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        } else {            // 使用相机拍摄
            if (!SDCardUtils.isSDCardEnable()) {
                XmbUtils.showMessage(getActivity(), "SD卡不可用.");
                return;
            }
            mCropParams.enable = true;
            mCropParams.compress = false;
            Intent intent = CropHelper.buildCameraIntent(mCropParams);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    }

    // 头像选择DialogFragment
    public static class AvatarDialogFragment extends DialogFragment {
        DialogInterface.OnClickListener mOnClickListener;

        public void setOnClickListener(DialogInterface.OnClickListener listener) {
            mOnClickListener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Context context = getActivity();
            return new AlertDialog.Builder(context).setTitle("设置头像").setItems(items,
                    mOnClickListener)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
    }

    // 日期选择对话框DialogFragment
    public static class DatePickerDialogFragment extends DialogFragment {
        private DatePickerDialog.OnDateSetListener mOnDateSetListener;

        public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
            mOnDateSetListener = listener;
        }

        /**
         * 创建DatePickerDialogFragment实例
         *
         * @param initDate 初始化日期
         * @return DatePickerDialogFragment实例
         */
        public static DatePickerDialogFragment newInstance(Date initDate) {
            DatePickerDialogFragment f = new DatePickerDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable("initDate", initDate);
            f.setArguments(args);
            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Context context = getActivity();
            Calendar calendar = Calendar.getInstance();

            Date initDate = (Date) getArguments().getSerializable("initDate");
            if (initDate != null) {
                calendar.setTime(initDate);
            }

            // 创建并返回DatePickerDialog
            DatePickerDialog dialog = new DatePickerDialog(context, mOnDateSetListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) {
                // onStop()生命周期当中，会调用tryNotifyDateSet()更新日期, 因此重写提供空实现, 表示
                // 按返回键或者空白区域不更新日期(不回调onDateSet方法)
                @Override
                protected void onStop() {
                }
            };
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMaxDate(DateUtil.dateToTimestamp(DateUtil.getDateBeforeOrAfter(DateUtil.getCurrentDate(), 365 * 2)));
            return dialog;
        }
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        // Original or Cropped uri
        if (!mCropParams.compress)
            saveAvatarBitmap(BitmapUtil.decodeUriAsBitmap(getActivity(), uri));
    }

    @Override
    public void onCompressed(Uri uri) {
        // Compressed uri
        saveAvatarBitmap(BitmapUtil.decodeUriAsBitmap(getActivity(), uri));
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onFailed(String message) {
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
