package com.witmoon.xmb.activity.goods.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.adapter.PublishEvaluationAdapter;
import com.witmoon.xmb.activity.me.adapter.Out_imgAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.Order;
import com.witmoon.xmb.ui.popupwindow.PopupDialog;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmblibrary.recyclerview.SuperRecyclerView;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.linear.DividerItemDecoration;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发表商品评价
 * Created by zhyh on 2015/8/19.
 */
public class PublishEvaluationFragment extends BaseFragment implements PublishEvaluationAdapter
        .OnSubmitClickListener, PublishEvaluationAdapter.OnImageClickListener {

    private String[] str = new String[]{"null", "file_name_0", "file_name_1", "file_name_2", "file_name_3", "file_name_4"};
    private View rootView;
    private Order mOrder;   // 发表评价对应的订单对象
    private PopupDialog popupDialog;
    private String localTempImgDir = "localTempImgDir";
    private String localTempImgFileName = System.currentTimeMillis() + "";
    private final int IMAGE_OPEN = 1, GET_IMAGE_VIA_CAMERA = 2;//打开图片标记
    private String pathImage;
    private Out_imgAdapter imgAdapter = new Out_imgAdapter(getContext());
    private SuperRecyclerView recyclerView;
    private String goods_id;
    private String rating;
    private int index = 0;
    private String content;
    PublishEvaluationAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOrder = (Order) bundle.getSerializable("ORDER");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.sticky_recycler_view, container, false);

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id
                .id_stickynavlayout_innerscrollview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R
                .drawable.divider_x2)));
        List<Map<String, String>> goodsList = mOrder.getGoodsList();
        for (int i = 0; i < goodsList.size(); i++) {
            if (!goodsList.get(i).get("is_comment").equals("0")) {
                goodsList.remove(i);
            }
        }
        adapter = new PublishEvaluationAdapter(goodsList, getContext(), getActivity(), this);
        adapter.setOnSubmitClickListener(this);
        adapter.setOnImageClickListener(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onImageClick(GridView mGridView, Out_imgAdapter mAdapter_, int currentPs, int imagePs) {
        imgAdapter = null;
        imgAdapter = mAdapter_;
    }

    @Override
    public void onSubmitClick(String goods_id_, String rating_, String content_, int ps) {
//        GoodsApi.commentGoods(goods_id, mOrder.getId(), rating, content,
//                new Listener<JSONObject>() {
//                    @Override
//                    public void onSuccess(JSONObject response) {
//                        TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
//                        if (tt.first) {
//                            AppContext.showToast("评价成功");
//                        }
//                    }
//                });
        index = ps;
        if (content_.length() == 0) {
            AppContext.showToast("请输入评价内容");
        } else {
            goods_id = goods_id_;
            content = content_;
            rating = rating_;
            ModifyAsyncTask asyncTask = new ModifyAsyncTask(this);
            asyncTask.execute();
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

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, PublishEvaluationFragment> {
        public ModifyAsyncTask(PublishEvaluationFragment mPublishEvaluationFragment) {
            super(mPublishEvaluationFragment);
        }

        @Override
        protected String doInBackground(PublishEvaluationFragment mPublishEvaluationFragment,
                                        Void... params) {
            List<HashMap<String, Object>> mList = imgAdapter.mList;
            String fileName = "";
            Map<String, File> fm = new HashMap<>();
            for (int i = 1; i < mList.size(); i++) {
                fm.put(str[i], saveAvatarBitmap((Bitmap) mList.get(i).get("itemImage"), (String) mList.get(i).get("imgPath")));
                fileName = fileName + str[i] + ";";
            }
//            showWaitDialog();
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("order_id", mOrder.getId());
                pm.put("goods_id", goods_id);
                pm.put("fileNames", fileName);
                pm.put("xinde", content);
                pm.put("comment_rank", rating);
                Log.e("fm", fm.toString());
                Log.e("pm", pm.toString());
                String response = HttpUtility.post(ApiHelper.BASE_URL+"order/order_comment", null, pm, fm);
                JSONObject respObj = new JSONObject(response);
                JSONObject js = respObj.getJSONObject("status");
                if (js.getString("succeed").equals("0")) {
                    return js.getString("error_desc");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PublishEvaluationFragment mPublishEvaluationFragment, String result) {
            if (result != null) {
                AppContext.showToastShort(result);
//                hideWaitDialog();
                return;
            }
            hideWaitDialog();
            AppContext.showToast("评价成功");
            //评价成功删除当前的View&&判断当前的适配器是否可以有值否则关闭页面
            if (adapter.deleView(index)==0)
            {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                adapter.settingImg(uri);
            }
        } else if (requestCode == GET_IMAGE_VIA_CAMERA) {
            File f = new File(Environment.getExternalStorageDirectory()
                    + "/" + localTempImgDir + "/" + localTempImgFileName);
            try {
                Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                        f.getAbsolutePath(), null, null));
                adapter.settingImg(u);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
}
