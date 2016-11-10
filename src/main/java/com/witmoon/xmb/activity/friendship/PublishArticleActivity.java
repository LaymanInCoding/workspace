package com.witmoon.xmb.activity.friendship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.friendship.adapter.GridPictureAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.SDCardUtils;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.WeakAsyncTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发布帖子
 * Created by zhyh on 2015/7/31.
 */
public class PublishArticleActivity extends BaseActivity implements AdapterView
        .OnItemClickListener, AdapterView.OnItemLongClickListener, DialogInterface.OnClickListener {

    private static String[] items = {"选择本地图片", "拍照"};

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String CAMERA_OUTPUT_PIC = "camera_output.png";

    private File mCameraFile;
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private GridView mPictureGridView;
    private GridPictureAdapter mPictureAdapter;
    private List<Map<String, Object>> mPictureList;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_publish_article;
    }

    @Override
    protected String getActionBarTitle() {
        return "发布帖子";
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_friendship_circle));
        TextView rightBtn = (TextView) toolbar.findViewById(R.id.toolbar_right_text);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("发布");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2015/8/1 发布
                String content = mContentEditText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    AppContext.showToast("您不准备说点什么吗？");
                    return;
                }
                if (mPictureList.size() < 1) {
                    AppContext.showToast("您好像忘了选择照片!");
                    return;
                }
                List<File> picList = new ArrayList<>();
                for (int i = 0; i < mPictureList.size() - 1; i++) {
                    picList.add(new File((String) mPictureList.get(i).get("picPath")));
                }
                PostAsyncTask task = new PostAsyncTask(PublishArticleActivity.this);
                task.execute(picList);
            }
        });
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        mTitleEditText = (EditText) findViewById(R.id.title);
        mContentEditText = (EditText) findViewById(R.id.content);
        mPictureGridView = (GridView) findViewById(R.id.grid_view);
        mPictureGridView.setOnItemClickListener(this);
        mPictureGridView.setOnItemLongClickListener(this);

        mPictureList = new ArrayList<>();
        Map<String, Object> addIconMap = new HashMap<>();
        addIconMap.put("bitmap", BitmapFactory.decodeResource(getResources(), R.mipmap
                .icon_plus_dash_boder_focused));
        mPictureList.add(addIconMap);

        mPictureAdapter = new GridPictureAdapter(mPictureList);
        mPictureGridView.setAdapter(mPictureAdapter);
    }

    // 发布帖子任务
    private class PostAsyncTask extends WeakAsyncTask<List<File>, Void, String,
            PublishArticleActivity> {

        public PostAsyncTask(PublishArticleActivity publishArticleActivity) {
            super(publishArticleActivity);
        }

        @Override
        protected void onPreExecute(PublishArticleActivity publishArticleActivity) {
            showWaitDialog();
        }

        @Override
        protected String doInBackground(PublishArticleActivity publishArticleActivity,
                                        List<File>... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", mTitleEditText.getText());
                jsonObject.put("content", mContentEditText.getText());
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("json", jsonObject.toString());
                paramMap.put("user_id", String.valueOf(AppContext.getLoginUid()));

                List<byte[]> bitmapList = new ArrayList<>(params[0].size());
                for (int i = 0; i < params[0].size(); i++) {
                    Bitmap bitmapCompressed = BitmapUtils.getCompressedImage(params[0].get(i)
                            .getAbsolutePath(),5);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    bitmapList.add(baos.toByteArray());
                }
                String response = HttpUtility.bitmapPost(ApiHelper.getMbqzApiUrl("/mbqz/post/add"),
                        paramMap, "file", bitmapList);
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
        protected void onPostExecute(PublishArticleActivity publishArticleActivity, String s) {
            hideWaitDialog();
            if (s == null) {
                AppContext.showToast("发布成功");
                finish();
                return;
            }
            AppContext.showToast(s);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                if (resultCode != Activity.RESULT_CANCELED) {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, new
                            String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    if (null != cursor) {
                        cursor.moveToFirst();
                        String picPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images
                                .Media.DATA));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
                        Map<String, Object> addIconMap = new HashMap<>();
                        addIconMap.put("bitmap", bitmap);
                        addIconMap.put("picPath", picPath);
                        mPictureList.add(mPictureList.size() - 1, addIconMap);
                        mPictureAdapter.notifyDataSetChanged();
                        cursor.close();
                    }
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap bitmap = BitmapFactory.decodeFile(mCameraFile.getAbsolutePath(),
                            options);
                    Map<String, Object> addIconMap = new HashMap<>();
                    addIconMap.put("bitmap", bitmap);
                    addIconMap.put("picPath", mCameraFile.getAbsolutePath());
                    mPictureList.add(mPictureList.size() - 1, addIconMap);
                    mPictureAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    // GridView列表项单击响应
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mPictureList.size() - 1) {
            // TODO: 2015/8/1 添加图片
            // 弹出图像选择对话框, 可以从相册中选择或者使用相机拍摄
            AvatarDialogFragment avatarDialogFragment = new AvatarDialogFragment();
            avatarDialogFragment.setOnClickListener(this);
            avatarDialogFragment.show(getFragmentManager(), "PicDialog");
        }
    }

    // 长按图片提示是否删除
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (position != mPictureList.size() - 1) {
            new AlertDialog.Builder(this).setMessage("确定要删除该图片吗？")
                    .setNegativeButton("取消", null).setCancelable(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPictureList.remove(position);
                            mPictureAdapter.notifyDataSetChanged();
                        }
                    }).show();
            return true;
        }
        return false;
    }

    /**
     * 弹出图片选择对话框点击监听函数
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == 0) {   // 从相册中选择图片
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        } else {            // 使用相机拍摄
            if (!SDCardUtils.isSDCardEnable()) {
                AppContext.showToastShort("SD卡不可用.");
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mCameraFile = new File(Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_DCIM), System.currentTimeMillis() + ".png");
//            mCameraFile = new File(Environment.getExternalStorageDirectory(), System
//                    .currentTimeMillis() + ".png");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    // 图片选择DialogFragment
    public static class AvatarDialogFragment extends DialogFragment {
        DialogInterface.OnClickListener mOnClickListener;

        public void setOnClickListener(DialogInterface.OnClickListener listener) {
            mOnClickListener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Context context = getActivity();
            return new AlertDialog.Builder(context).setTitle("添加图片").setItems(items,
                    mOnClickListener).setNegativeButton("取消", null).setCancelable(true).create();
        }
    }
}
