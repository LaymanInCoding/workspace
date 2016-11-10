package com.witmoon.xmb.activity.specialoffer.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.adapter.Out_imgAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by de on 2016/1/25
 */
public class UpRecordFragment extends BaseFragment implements View.OnClickListener {
    private EditText content;
    private GridView mGridview;
    private Out_imgAdapter imgAdapter;
    private String pathImage;//选择图片路径
    private Bitmap bmp;//导入临时图片
    private View topView;
    private final int IMAGE_OPEN = 1, GET_IMAGE_VIA_CAMERA = 2;//打开图片标记
    //图片名称
    private String[] str = new String[]{"null", "refund_pic1", "refund_pic2", "refund_pic3"};
    //ppw
    PopupDialog popupDialog;
    private String localTempImgFileName = System.currentTimeMillis() + "";
    private String localTempImgDir = "localTempImgDir";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseActivity activity = (BaseActivity) getActivity();
        Toolbar toolbar = activity.getToolBar();
        TextView empty = (TextView) toolbar.findViewById(R.id.toolbar_right_text);
        empty.setVisibility(View.VISIBLE);
        empty.setText(R.string.text_sub);
        empty.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uprecord, container, false);
        topView = view;
        content = (EditText) view.findViewById(R.id.content);
        mGridview = (GridView) view.findViewById(R.id.grid_view);
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.up_head); //加号
        imgAdapter = new Out_imgAdapter(getContext());
        imgAdapter.addItemImage(bmp, "");
        mGridview.setAdapter(imgAdapter);
        init();
        return view;
    }

    private void init() {
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position != 0) {
                    dialog(position);
                } else if (imgAdapter.mList.size() == 7) { //第一张为默认图片
                    AppContext.showToast("图片数6张已满");
                } else if (position == 0) { //点击图片位置为+ 0对应0张图片
                    //选择图片
                    showDownUpPopupDialog();
                }
            }
        });
    }

    private boolean isCheck() {
        boolean is_code = false;
        if (content.getText().toString().length() <= 0) {
            AppContext.showToast("请输入要发表的记录...");
        } else if (imgAdapter.mList.size() <= 1) {
            AppContext.showToast("请上传你和宝宝的美好记录吧！");
        } else {
            is_code = true;
        }
        return is_code;
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //删除集合里临时数据
                imgAdapter.deleteItemImage(position);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
        popupDialog = PopupUtils.createPopupDialog(getContext(), popup);
        popupDialog.showAtLocation(topView.findViewById(R.id.grid_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, popup.getxPos(), popup.getyPos());
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

    //外部查询返回
    private void settingImg(Uri uri) {
        Bitmap mBitmap = null;
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
        imgAdapter.addItemImage(mBitmap, pathImage);
    }

    //防止部分手机返回错误
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
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

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, UpRecordFragment> {

        public ModifyAsyncTask(UpRecordFragment mUpRecordFragment) {
            super(mUpRecordFragment);
        }

        @Override
        protected void onPreExecute(UpRecordFragment upRecordFragment) {
            super.onPreExecute(upRecordFragment);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(UpRecordFragment mUpRecordFragment,
                                        Void... params) {
            List<HashMap<String, Object>> mList = imgAdapter.mList;
            List<File> fielList = new ArrayList<>();
            for (int i = 1; i < mList.size(); i++) {
                fielList.add(saveAvatarBitmap((Bitmap) mList.get(i).get("itemImage"), (String) mList.get(i).get("imgPath")));
            }
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("content", content.getText().toString().trim());
                String response = HttpUtility.post(ApiHelper.getAbsoluteUrl("/athena/diaryadd"), null, pm, "photo", fielList);
                JSONObject respObj = new JSONObject(response);
                Log.e("response", response.toString());
                Log.e("respObj", respObj.toString());
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
        protected void onPostExecute(UpRecordFragment mUpRecordFragment, String result) {
            hideWaitDialog();
            if (result != null) {
                AppContext.showToastShort(result);
                return;
            }
            AppContext.showToastShort("操作成功");
            Intent intent = new Intent(Const.INTENT_ACTION_BABY);
            getActivity().sendBroadcast(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.toolbar_right_text:
                if (isCheck()) {
                    ModifyAsyncTask task = new ModifyAsyncTask(UpRecordFragment.this);
                    task.execute();
                }
                break;
        }
    }
}
