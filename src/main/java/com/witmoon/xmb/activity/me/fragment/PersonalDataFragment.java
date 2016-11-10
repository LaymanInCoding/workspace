package com.witmoon.xmb.activity.me.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.User;
import com.witmoon.xmb.ui.widget.CircleImageView;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.DateUtil;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.SDCardUtils;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import library.BitmapUtil;
import library.CropHandler;
import library.CropHelper;
import library.CropParams;

/**
 * 个人资料修改界面
 * Created by zhyh on 2015/6/21.
 */
public class PersonalDataFragment extends BaseFragment implements DatePickerDialog
        .OnDateSetListener, DialogInterface.OnClickListener, CropHandler {
    private static String[] items = {"选择本地图片", "拍照"};
    //设置头部图片的名字
    private static String dateStyle = "yyyy-MM-dd";

    private CircleImageView mAvatarImage;
    private EditText mNickNameEdit;
    private EditText mDueDayEdit;
    private EditText mBabyBirthdayEdit;
    private EditText currentEditText;

    private String mBabySex;
    private String mParentSex;

    private RadioGroup mBabySexGroup;
    private RadioGroup mParentSexGroup;

    private File mAvatar;
    private CropParams mCropParams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_data, container, false);
        mCropParams = new CropParams(getActivity());
        mAvatarImage = (CircleImageView) view.findViewById(R.id.me_avatar_img);
        mAvatarImage.setOnClickListener(this);
        mNickNameEdit = (EditText) view.findViewById(R.id.nick_name);
        mDueDayEdit = (EditText) view.findViewById(R.id.due_date);
        mDueDayEdit.setOnClickListener(this);
        mBabyBirthdayEdit = (EditText) view.findViewById(R.id.baby_birthday);
        mBabyBirthdayEdit.setOnClickListener(this);

        mBabySexGroup = (RadioGroup) view.findViewById(R.id.baby_sex);
        mBabySexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mBabySex = checkedId == R.id.baby_sex_male ? "1" : "0";
            }
        });
        mParentSexGroup = (RadioGroup) view.findViewById(R.id.parent_sex);
        mParentSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mParentSex = checkedId == R.id.parent_sex_baba ? "1" : "0";
            }
        });

        view.findViewById(R.id.submit_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        User user = AppContext.getLoginInfo();
        Netroid.displayImage(user.getAvatar(), mAvatarImage);
        mNickNameEdit.setText(user.getName());
        if (null != user.getChildSex() && !TextUtils.isEmpty(user.getChildSex())) {
            mBabySexGroup.check("1".equals(user.getChildSex()) ? R.id.baby_sex_male : R.id
                    .baby_sex_female);
        }
        if (null != user.getParentSex() && !TextUtils.isEmpty(user.getParentSex())) {
            mParentSexGroup.check("1".equals(user.getParentSex()) ? R.id.parent_sex_baba : R.id
                    .parent_sex_mama);
        }
        mBabyBirthdayEdit.setText(user.getBabyBirthday());
        mDueDayEdit.setText(user.getDueDay());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_avatar_img:
                // 弹出图像选择对话框, 可以从相册中选择或者使用相机拍摄
                AvatarDialogFragment avatarDialogFragment = new AvatarDialogFragment();
                avatarDialogFragment.setOnClickListener(this);
                avatarDialogFragment.show(getActivity().getFragmentManager(), "AvatarDialog");
                break;
            case R.id.baby_birthday:
            case R.id.due_date:
                currentEditText = v.getId() == R.id.due_date ? mDueDayEdit :
                        mBabyBirthdayEdit;

                Date iDate = null;
                String t = currentEditText.getText().toString();
                if (!TextUtils.isEmpty(t)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(dateStyle, Locale.CHINA);
                    try {
                        //设置打开默认显示的值
                        if (t.equals("0000-00-00")) {
                            t = "2015-05-05";
                        }
                        iDate = dateFormat.parse(t);
                    } catch (ParseException ignored) {
                    }
                }
                DatePickerDialogFragment fragment = DatePickerDialogFragment.newInstance(iDate);
                fragment.setOnDateSetListener(this);
                fragment.show(getActivity().getFragmentManager(), "DatePickerDialog");
                break;
            case R.id.submit_button:
                ModifyAsyncTask asyncTask = new ModifyAsyncTask(this);
                asyncTask.execute();
                break;
        }
    }

    private Map<String, String> getParamMap() {
        Map<String, String> paramMap = new HashMap<>();
        String nickName = mNickNameEdit.getText().toString();
        String babyBirthday = mBabyBirthdayEdit.getText().toString();
        String dueDay = mDueDayEdit.getText().toString();

        paramMap.put("nick_name", nickName);
        paramMap.put("child_birthday", babyBirthday);
        paramMap.put("expected_date", dueDay);
        paramMap.put("child_sex", mBabySex);
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
                JSONObject paramObj = ApiHelper.getParamObj(pm);
                pm.clear();
                pm.put("json", paramObj.toString());
                String response = HttpUtility.post(ApiHelper.getAbsoluteApiUrl("/user/modInfo"),
                        null, pm, fm);
                JSONObject respObj = new JSONObject(response);
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
            UserApi.userInfo(ApiHelper.getDefaultUserInfoListener());
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
     * 日期选择对话框设置日期时响应方法
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentEditText.setText(year + "-" + ++monthOfYear + "-" + dayOfMonth);
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
                XmbUtils.showMessage(getActivity(),"SD卡不可用.");
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
