package com.witmoon.xmb.activity.specialoffer.fragment;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.fragment.PersonalDataFragment;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.popupwindow.Popup;
import com.witmoon.xmb.ui.popupwindow.PopupDialog;
import com.witmoon.xmb.ui.popupwindow.PopupUtils;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.WeakAsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by de on 2016/1/25
 */
public class Add_babyFragment extends BaseFragment implements DatePickerDialog
        .OnDateSetListener {
    private EditText add_baby_birthday, add_baby_name, add_baby_gender;
    private int add_baby_gender_num;
    private static String dateStyle = "yyyy-MM-dd";
    private ImageView imageView;
    PopupDialog popupDialog;
    private View topView;
    private String localTempImgFileName = System.currentTimeMillis() + "";
    private final int IMAGE_OPEN = 1, GET_IMAGE_VIA_CAMERA = 2;//打开图片标记
    private String pathImage = "";//选择图片路径
    private String localTempImgDir = "localTempImgDir";
    private Button mButton;
    private boolean is_img;
    Bitmap mBitmap = null;
    private String babyImgUrl = "";


    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(AppContext.getLoginInfo().getIs_baby_add().equals("true")){
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_baby, container, false);
        topView = view;
        add_baby_birthday = (EditText) view.findViewById(R.id.add_baby_birthday);
        add_baby_name = (EditText) view.findViewById(R.id.add_baby_name);
        add_baby_gender = (EditText) view.findViewById(R.id.add_baby_gender);
        imageView = (ImageView) view.findViewById(R.id.add_baby_picture);
        mButton = (Button) view.findViewById(R.id.next_step_btn);

        getActivity().registerReceiver(mLoginReceiver, new IntentFilter(Const.INTENT_ACTION_LOGIN));

        try {
            add_baby_birthday.setText(AppContext.getLoginInfo().getBaby_birthday());
            add_baby_name.setText(AppContext.getLoginInfo().getBaby_nickname());
            add_baby_name.setSelection(AppContext.getLoginInfo().getBaby_nickname().length());
            add_baby_gender.setText((Integer.parseInt(AppContext.getLoginInfo().getBaby_gender()) == 0) ? "男" : "女");
            babyImgUrl = AppContext.getLoginInfo().getBaby_photo();
            if (!babyImgUrl.equals(""))
            {
                is_img = true;
                if (babyImgUrl.substring(0, 4).equals("http"))
                {
                    Netroid.displayImage(AppContext.getLoginInfo().getBaby_photo(), imageView);
                }else
                {
                    imageView.setImageBitmap(BitmapUtils.getCompressedImage(AppContext.getLoginInfo().getBaby_photo(), 2));
                }
            }
        } catch (Exception e) {

        }
        init();
        return view;
    }

    private void init() {

        add_baby_name.setFocusable(false);
        add_baby_name.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        add_baby_name.setClickable(false);
        add_baby_gender.setFocusable(false);
        add_baby_gender.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        add_baby_gender.setClickable(false);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppContext.instance().isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                showDownUpPopupDialog();
            }
        });
        add_baby_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppContext.instance().isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }else{
                    add_baby_gender.setFocusable(true);
                    add_baby_gender.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                    add_baby_gender.setClickable(true);
                }
            }
        });
        add_baby_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppContext.instance().isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }else{
                    add_baby_name.setFocusable(true);
                    add_baby_name.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                    add_baby_name.setClickable(true);
                }
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppContext.instance().isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                if (is_check()) {
                    if (add_baby_gender.getText().toString().trim().equals("男")) {
                        add_baby_gender_num = 0;
                    } else if (add_baby_gender.getText().toString().trim().equals("女")) {
                        add_baby_gender_num = 1;
                    } else {
                        AppContext.showToast("请输入正确的性别。");
                        return;
                    }
                    ModifyAsyncTask task = new ModifyAsyncTask(Add_babyFragment.this);
                    task.execute();
                }
            }
        });
        add_baby_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppContext.instance().isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Date iDate = null;
                String t = add_baby_birthday.getText().toString();
                if (!TextUtils.isEmpty(t)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(dateStyle, Locale.CHINA);
                    try {
                        //设置打开默认显示的值
                        if (t.equals("0000-00-00")) {
                            t = AppContext.getLoginInfo().getBaby_birthday();
                        }
                        iDate = dateFormat.parse(t);
                    } catch (ParseException ignored) {
                    }
                }
                PersonalDataFragment.DatePickerDialogFragment fragment = PersonalDataFragment.DatePickerDialogFragment.newInstance(iDate);
                fragment.setOnDateSetListener(Add_babyFragment.this);
                fragment.show(getActivity().getFragmentManager(), "DatePickerDialog");
            }
        });
    }

    private boolean is_check() {
        boolean is_ = false;
        if (add_baby_name.getText().length() <= 0) {
            AppContext.showToast("请输入宝宝姓名。");
        } else if (add_baby_birthday.getText().length() <= 0) {
            AppContext.showToast("请输入宝宝出生日。");
        } else if (add_baby_gender.getText().length() <= 0) {
            AppContext.showToast("请输入宝宝性别。");
        } else if (!is_img) {
            AppContext.showToast("请上传宝宝相片。");
        } else {
            is_ = true;
        }

        return is_;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        add_baby_birthday.setText(year + "-" + ++monthOfYear + "-" + dayOfMonth);
    }

    private void showDownUpPopupDialog() {
        Popup popup = new Popup();
        popup.setvWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setvHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setClickable(true);
        popup.setContentView(R.layout.view_userheader_modifydetail);
        //设置触摸其他位置时关闭窗口
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int height = view.findViewById(R.id.flMaskLayer).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        PopupUtils.dismissPopupDialog();
                    }
                }
                return true;
            }
        };
        popup.setTouchListener(listener);
        popupDialog = PopupUtils.createPopupDialog(getActivity(), popup);
        popupDialog.showAtLocation(topView.findViewById(R.id.add_baby_picture), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, popup.getxPos(), popup.getyPos());
        View view = popupDialog.getContentView();
        //背景透明度设置
        view.findViewById(R.id.flMaskLayer).setAlpha(0.75f);
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消按钮
                if (v.getId() == R.id.tvCancel) {
                    PopupUtils.dismissPopupDialog();
                }
                //从手机
                else if (v.getId() == R.id.tvTakeHeader) {
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                        try {
                            File dir = new File(Environment.getExternalStorageDirectory() + "/" + localTempImgDir);
                            if (!dir.exists()) dir.mkdirs();
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(dir, localTempImgFileName);//localTempImgDir和localTempImageFileName是自己定义的名字
                            Uri u = Uri.fromFile(f);
                            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                            startActivityForResult(intent, GET_IMAGE_VIA_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            // TODO Auto-generated catch block
                            AppContext.showToast("没有找到储存目录");
                        }
                    } else {
                        AppContext.showToast("没有找到储存卡");
                    }
                    PopupUtils.dismissPopupDialog();
                }
                //从相册
                else if (v.getId() == R.id.tvHeaderFromSD) {
                    PopupUtils.dismissPopupDialog();
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                }
            }
        };
        view.findViewById(R.id.tvCancel).setOnClickListener(l);
        view.findViewById(R.id.tvTakeHeader).setOnClickListener(l);
        view.findViewById(R.id.tvHeaderFromSD).setOnClickListener(l);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                settingImg(uri);
            }
        } else if (requestCode == GET_IMAGE_VIA_CAMERA) {
            File f = new File(Environment.getExternalStorageDirectory()
                    + "/" + localTempImgDir + "/" + localTempImgFileName);
            try {
                Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                        f.getAbsolutePath(), null, null));
                settingImg(u);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //外部查询返回
    private void settingImg(Uri uri) {
        mBitmap = null;
        Cursor cursor = getActivity().managedQuery(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        //返回 没找到选择图片
        if (null == cursor) {
            return;
        }
        //光标移动至开头 获取图片路径
        cursor.moveToFirst();
        pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        try {
            mBitmap = BitmapUtils.getCompressedImage(pathImage, 5);
        } catch (NullPointerException e) {
            AppContext.showToast("当前相片不可用，重新选择或拍照！");
            return;
        }
        imageView.setImageBitmap(mBitmap);
        is_img = true;
    }

    //防止部分手机返回错误
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, Add_babyFragment> {

        public ModifyAsyncTask(Add_babyFragment mAdd_babyFragment) {
            super(mAdd_babyFragment);
        }

        @Override
        protected void onPreExecute(Add_babyFragment add_babyFragment) {
            super.onPreExecute(add_babyFragment);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(Add_babyFragment mAdd_babyFragment,
                                        Void... params) {
            Map<String, File> fm = new HashMap<>();
            if (pathImage.equals("")) {
                pathImage = "baby_img";
            }
            fm.put("children", saveAvatarBitmap(mBitmap, pathImage));
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("nickname", add_baby_name.getText().toString());
                pm.put("birthday", add_baby_birthday.getText().toString());
                pm.put("gender", add_baby_gender_num + "");
                if (!AppContext.getLoginInfo().getBaby_id().equals("")) {
                    pm.put("id", AppContext.getLoginInfo().getBaby_id());
                    pm.put("act", "modify");
                } else {
                    pm.put("act", "insert");
                }
                Log.e("params", pm.toString());
                Log.e("Baby_id", AppContext.getLoginInfo().getBaby_id());
                String response = HttpUtility.post(ApiHelper.getAbsoluteUrl("athena/babyadd"), null, pm, fm);
                JSONObject respObj = new JSONObject(response);
                if (respObj.getString("status").equals("0")) {
                    return respObj.getString("msg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Add_babyFragment mAdd_babyFragment, String result) {
            hideWaitDialog();
            if (result != null) {
                AppContext.showToastShort(result);
                return;
            }
            AppContext.showToastShort("操作成功");
            AppContext.addBaby("true");
            if (pathImage.equals("baby_img"))
            {
                pathImage = babyImgUrl;
            }
            AppContext.addBaby_name(add_baby_name.getText().toString(), add_baby_gender_num + "", pathImage);
            Intent intent = new Intent(Const.INTENT_ACTION_BABY);
            getActivity().sendBroadcast(intent);
            getActivity().finish();
        }
    }

    private File saveAvatarBitmap(Bitmap avatar, String path) {
        File mFile = new File(path);
        OutputStream os = null;
        try {
            os = new FileOutputStream(mFile);
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
        return mFile;
    }
}
