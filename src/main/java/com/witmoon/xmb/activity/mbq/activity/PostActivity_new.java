package com.witmoon.xmb.activity.mbq.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.LocalImageHelper;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import library.BitmapUtil;

/**
 * Created by de on 2016/12/6.
 */
public class PostActivity_new extends BaseActivity {

    private ImageSpan imageSpan;
    private static final int OPEN_ALBUM = 0x100;
    private EditText richText;
    private EditText et_name;
    private TextView toolbar_right_text;
    private ImageView img_addPicture;
    private Bitmap mBitmap;
    private Uri uri;
    private int i = 0;
    private String circle_id;

    private boolean isKeyBoardUp, isEditTouch;// 判断软键盘的显示与隐藏

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.test_layout;
    }

    @Override
    protected String getActionBarTitle() {
        return "发布话题";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        LocalImageHelper.getInstance().clear();
        circle_id = getIntent().getStringExtra("circle_id");
        init();
    }

    private void init() {
        toolbar_right_text = (TextView) findViewById(R.id.toolbar_right_text);
        et_name = (EditText) findViewById(R.id.et_name);
        richText = (EditText) findViewById(R.id.richText);
        img_addPicture = (ImageView) findViewById(R.id.post_add_pic);
        initRichEdit();
    }

    private void initRichEdit() {

        img_addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, OPEN_ALBUM);
            }
        });

        toolbar_right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.equals("")) {
                    XmbUtils.showMessage(PostActivity_new.this, "请输入文章标题");
                } else if (richText.getText().equals("")) {
                    XmbUtils.showMessage(PostActivity_new.this, "请输入文章内容");
                } else {
                    CommitAsyncTask commitTask = new CommitAsyncTask(PostActivity_new.this);
                    commitTask.execute();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_ALBUM:
                    uri = data.getData();
                    ModifyAsyncTask task = new ModifyAsyncTask(this);
                    task.execute();
                    break;

            }
        }
    }


    private class ModifyAsyncTask extends WeakAsyncTask<String, Void, String, PostActivity_new> {

        public ModifyAsyncTask(PostActivity_new mUpRecordFragment) {
            super(mUpRecordFragment);
        }

        @Override
        protected void onPreExecute(PostActivity_new upRecordFragment) {
            super.onPreExecute(upRecordFragment);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(PostActivity_new mUpRecordFragment,
                                        String... params) {
            i++;
            List<File> fileList = new ArrayList<>();
            fileList.clear();
            Bitmap showBitmap = null;
            ContentResolver resolver = getContentResolver();
            if (!uri.toString().equals("")) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));
                    String path = BitmapUtils.saveMyBitmap(bitmap, "tmp" + i, PostActivity_new.this);
                    showBitmap = BitmapUtils.getCompressedImage(path, 1);
                    imageSpan = new ImageSpan(mUpRecordFragment, showBitmap);
                    File file = new File(path);
                    fileList.add(file);
                } catch (Exception e) {
                }
            }

            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                String response = HttpUtility.post(ApiHelper.BASE_URL + "userCircle/upload_img", null, pm, "photo", fileList);
                return response;
            } catch (Exception e) {
                return "{\"status\":0,\"info\":\"网络异常\"}";
            }
        }

        @Override
        protected void onPostExecute(PostActivity_new mUpRecordFragment, String result) {
            Log.e("response", result);
            hideWaitDialog();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                int index = richText.getSelectionStart(); // 获取光标所在位置
                Log.e("INDEX", index + "");
                Editable edit_text = richText.getEditableText();
                SpannableString newLine = new SpannableString("\n");
                edit_text.insert(index, newLine);
                Log.e("LENGTH", edit_text.length() + "");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String url = jsonArray.getString(i);
                    String tempUrl = "<img src=\"" + url + "\" />";
                    SpannableString spannableString = new SpannableString(tempUrl);
                    // 用ImageSpan对象替换你指定的字符串
                    spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (index < 0 || index >= edit_text.length()) {
                        edit_text.append(spannableString);
                    } else {
                        edit_text.insert(index, spannableString);
                    }
                    edit_text.insert(index, newLine);//插入图片后换行
                    Log.e("edit_text的内容为 ", edit_text.toString());
                }
                if (jsonObject.getInt("status") != 1) {
                    XmbUtils.showMessage(PostActivity_new.this, jsonObject.getString("info"));
                    return;
                }
                XmbUtils.showMessage(PostActivity_new.this, "上传成功");
                showSoftInput();
            } catch (JSONException e) {
                XmbUtils.showMessage(PostActivity_new.this, "网络异常，未知错误");
            }
        }
    }

    private void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(richText, 0);
    }

    private class CommitAsyncTask extends WeakAsyncTask<Void, Void, String, PostActivity_new> {

        public CommitAsyncTask(PostActivity_new mUpRecordFragment) {
            super(mUpRecordFragment);
        }

        @Override
        protected void onPreExecute(PostActivity_new upRecordFragment) {
            super.onPreExecute(upRecordFragment);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(PostActivity_new mUpRecordFragment,
                                        Void... params) {
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("circle_id", circle_id);
                pm.put("post_title", et_name.getText().toString());
                pm.put("post_content", richText.getText().toString());
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                Log.e("Commit_article", pm.toString());
                String response = HttpUtility.post(ApiHelper.BASE_URL + "userCircle/add_post_v2", null, pm);
                return response;
            } catch (Exception e) {
                return "{\"status\":0,\"info\":\"网络异常\"}";
            }
        }

        @Override
        protected void onPostExecute(PostActivity_new mUpRecordFragment, String result) {
            hideWaitDialog();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("POST_COMMIT", jsonObject.toString());
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    String url = jsonArray.getString(i);
//                    String tempUrl = "<img src=\"" + url + "\" />";
//                    SpannableString spannableString = new SpannableString(tempUrl);
//                    // 用ImageSpan对象替换你指定的字符串
//                    spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    // 将选择的图片追加到EditText中光标所在位置
//                }
                if (jsonObject.getInt("status") != 1) {
                    XmbUtils.showMessage(PostActivity_new.this, jsonObject.getString("info"));
                    return;
                }
                XmbUtils.showMessageRefreshCircle(PostActivity_new.this, jsonObject.getString("info"));
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 1 * 1000);
            } catch (JSONException e) {
                XmbUtils.showMessage(PostActivity_new.this, "网络异常，未知错误");
            }
        }
    }


}
