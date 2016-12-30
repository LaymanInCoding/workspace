package com.witmoon.xmb.activity.mbq.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.baby.LocalAlbum;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.widget.AlbumViewPager;
import com.witmoon.xmb.ui.widget.FilterImageView;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.FileUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.ImageUtils;
import com.witmoon.xmb.util.LocalImageHelper;
import com.witmoon.xmb.util.StringUtils;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmb.util.XmbUtils;
import com.yxp.permission.util.lib.PermissionInfo;
import com.yxp.permission.util.lib.PermissionUtil;
import com.yxp.permission.util.lib.callback.PermissionOriginResultCallBack;
import com.yxp.permission.util.lib.callback.PermissionResultCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostCommentActivity extends BaseActivity {
    private static final int OPEN_ALBUM = 0x100;
    private int i = 0;
    private View add;
    private ImageSpan imageSpan;
    private TextView toolbar_right_text;
    private ImageView toolbar_left_img;
    private Uri uri;
    EditText content_edit;
    private String post_id;
    private String comment_reply_id;

    private BroadcastReceiver finishThisActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishThisActivity);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mbq_post_comment;
    }

    @Override
    protected String getActionBarTitle() {
        return "发表回复";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        post_id = getIntent().getStringExtra("post_id");
        comment_reply_id = getIntent().getStringExtra("comment_reply_id");
        add = findViewById(R.id.post_add_pic);
        content_edit = (EditText) findViewById(R.id.post_content);
        toolbar_right_text = (TextView) findViewById(R.id.toolbar_right_text);
        toolbar_left_img = (ImageView) findViewById(R.id.toolbar_left_img);
        toolbar_left_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar_right_text.setOnClickListener(this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.getInstance().request(new String[]{Manifest.permission.CAMERA}, new PermissionResultCallBack() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, OPEN_ALBUM);
                    }

                    @Override
                    public void onPermissionGranted(String... strings) {

                    }

                    @Override
                    public void onPermissionDenied(String... strings) {

                    }

                    @Override
                    public void onRationalShow(String... strings) {

                    }
                });
            }
        });
        IntentFilter finishActivity = new IntentFilter(Const.INTENT_ACTION_REFRESH_POST);
        registerReceiver(finishThisActivity, finishActivity);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right_text:
                String content = content_edit.getText().toString();
                if (StringUtils.isEmpty(content)) {
                    XmbUtils.showMessage(PostCommentActivity.this, "内容不能为空哦~");
                    return;
                } else {
                    //设置为不可点击，防止重复提交
                    view.setEnabled(false);
                    CommitAsyncTask task = new CommitAsyncTask(this);
                    task.execute();
                }
                break;
        }
    }

    private class ModifyAsyncTask extends WeakAsyncTask<String, Void, String, PostCommentActivity> {

        public ModifyAsyncTask(PostCommentActivity mUpRecordFragment) {
            super(mUpRecordFragment);
        }

        @Override
        protected void onPreExecute(PostCommentActivity upRecordFragment) {
            super.onPreExecute(upRecordFragment);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(PostCommentActivity mUpRecordFragment,
                                        String... params) {
            i++;
            List<File> fileList = new ArrayList<>();
            fileList.clear();
            Bitmap showBitmap = null;
            ContentResolver resolver = getContentResolver();
            if (!uri.toString().equals("")) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));
                    String path = BitmapUtils.saveMyBitmap(bitmap, "tmp" + i, PostCommentActivity.this);
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
        protected void onPostExecute(PostCommentActivity mUpRecordFragment, String result) {
            Log.e("response", result);
            hideWaitDialog();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                int index = content_edit.getSelectionStart(); // 获取光标所在位置
                Log.e("INDEX", index + "");
                Editable edit_text = content_edit.getEditableText();
                SpannableString newLine = new SpannableString("\n");
                edit_text.insert(index, newLine);
                Log.e("LENGTH", edit_text.length() + "");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String url = jsonArray.getString(i);
                    String tempUrl = "<br><img src=\"" + url + "\" /><br>";
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
                    XmbUtils.showMessage(PostCommentActivity.this, jsonObject.getString("info"));
                    return;
                }
                XmbUtils.showMessage(PostCommentActivity.this, "上传成功");
                showSoftInput();
            } catch (JSONException e) {
                XmbUtils.showMessage(PostCommentActivity.this, "网络异常，未知错误");
            }
        }
    }

    private void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(content_edit, 0);
    }

    private class CommitAsyncTask extends WeakAsyncTask<Void, Void, String, PostCommentActivity> {

        public CommitAsyncTask(PostCommentActivity mUpRecordFragment) {
            super(mUpRecordFragment);
        }

        @Override
        protected void onPreExecute(PostCommentActivity upRecordFragment) {
            super.onPreExecute(upRecordFragment);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(PostCommentActivity mUpRecordFragment,
                                        Void... params) {
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("comment_content", content_edit.getText().toString());
                pm.put("comment_reply_id", comment_reply_id);
                pm.put("post_id", post_id);
                Log.e("Commit_article", pm.toString());
                String response = HttpUtility.post(ApiHelper.BASE_URL + "UserCircle/add_comment", null, pm);
                return response;
            } catch (Exception e) {
                return "{\"status\":0,\"info\":\"网络异常\"}";
            }
        }

        @Override
        protected void onPostExecute(PostCommentActivity mUpRecordFragment, String result) {
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
                    XmbUtils.showMessage(PostCommentActivity.this, jsonObject.getString("info"));
                    return;
                }
                XmbUtils.showMessageRefreshPost(PostCommentActivity.this, jsonObject.getString("info"));
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 1 * 1000);
            } catch (JSONException e) {
                XmbUtils.showMessage(PostCommentActivity.this, "网络异常，未知错误");
            }
        }
    }

}
