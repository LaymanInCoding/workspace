package com.witmoon.xmb.activity.goods.adapter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.PreviewImage;
import com.witmoon.xmb.activity.goods.fragment.PublishEvaluationFragment;
import com.witmoon.xmb.activity.me.adapter.Out_imgAdapter;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.model.ImageBDInfo;
import com.witmoon.xmb.model.ImageInfo;
import com.witmoon.xmb.ui.MyGridView;
import com.witmoon.xmb.ui.popupwindow.Popup;
import com.witmoon.xmb.ui.popupwindow.PopupDialog;
import com.witmoon.xmb.ui.popupwindow.PopupUtils;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.UIutil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发表商品评价适配器
 * Created by zhyh on 2015/8/19.
 */
public class PublishEvaluationAdapter extends RecyclerView.Adapter<PublishEvaluationAdapter
        .EvaluationHolder> {
    private String localTempImgDir = "localTempImgDir";
    private String localTempImgFileName = System.currentTimeMillis() + "";
    private final int IMAGE_OPEN = 1, GET_IMAGE_VIA_CAMERA = 2;//打开图片标记
    private PopupDialog popupDialog;
    private Map<String, String> edMap = new HashMap<>();
    Out_imgAdapter imgAdapters = null;
    private String pathImage;
    private int ps = 0;
    private FragmentActivity fragmentActivity;
    private PublishEvaluationFragment publishEvaluationFragment;
    private HashMap<String, String> map = new HashMap<>();
    public HashMap<String, Out_imgAdapter> imgMap = new HashMap<>();
    private Context mContext;

    public PublishEvaluationAdapter(List<Map<String, String>> dataList, Context mContext, FragmentActivity fragmentActivity, PublishEvaluationFragment publishEvaluationFragment) {
        mDataList = dataList;
        this.mContext = mContext;
        this.publishEvaluationFragment = publishEvaluationFragment;
        this.fragmentActivity = fragmentActivity;
    }

    // 提交评价监听器
    private OnSubmitClickListener mOnSubmitClickListener;

    private OnImageClickListener mOnImageClickListener;
    private List<Map<String, String>> mDataList;

    public void setOnSubmitClickListener(
            OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
    }

    public void setOnImageClickListener(
            OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    @Override
    public EvaluationHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_publish_evaluation, parent, false);
        return new EvaluationHolder(view);
    }

    @Override
    public void onBindViewHolder(final EvaluationHolder holder, final int position) {
        final Map<String, String> dataMap = mDataList.get(position);
        holder.mTitleText.setText(dataMap.get("goods_name"));
        holder.mContentEdit.setTag("!true");
        holder.mContentEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) holder.mContentEdit.setTag("true");
            }
        });
        holder.mContentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && holder.mContentEdit.getTag().equals("true"))
                    edMap.put(position + "", s.toString());
            }
        });
        Netroid.displayImage(dataMap.get("goods_img"), holder.mImageView);
        holder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position_, long id) {
                imgAdapters = holder.imgAdapter;
                ps = position;
                if (position_ != 0) {
                    dialog(position_, holder.imgAdapter);
                } else if (holder.imgAdapter.mList.size() == 6) { //第一张为默认图片
                    AppContext.showToast("图片数量已满五张");
                } else if (position_ == 0) { //点击图片位置为+ 0对应0张图片
//                    选择图片
                    showDownUpPopupDialog(view);
                }
                if (null != mOnImageClickListener) {
                    mOnImageClickListener.onImageClick(holder.mGridView, holder.imgAdapter, position, position_);
                }
            }
        });
        holder.mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating > 0) {
                    map.put(position + "", ratingBar.getRating() + "");
                }
            }
        });
        holder.mSubmitBtnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSubmitClickListener) {
                    float rating = holder.mRatingBar.getRating();
                    String content = holder.mContentEdit.getText().toString();
                    mOnSubmitClickListener.onSubmitClick(dataMap.get("goods_id"), String.valueOf
                            (rating), content, position);
                }
            }
        });
        if (imgMap.containsKey(position + "")) {
            holder.imgAdapter = imgMap.get(position + "");
            holder.mGridView.setAdapter(holder.imgAdapter);
        } else {
            if (null == holder.mGridView.getAdapter()) {
                holder.mGridView.setAdapter(holder.imgAdapter);
            } else {
                holder.imgAdapter = new Out_imgAdapter(mContext);
                holder.imgAdapter.addItemImage(holder.bmp, "");
                holder.mGridView.setAdapter(holder.imgAdapter);
            }
        }
        if (null != map && map.containsKey(position + "")) {
            holder.mRatingBar.setRating(Float.parseFloat(map.get(position + "")));
        } else {
            holder.mRatingBar.setRating(0);
        }
        if (null != edMap && edMap.containsKey(position + "")) {
            holder.mContentEdit.setText(edMap.get(position + ""));
        } else {
            holder.mContentEdit.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    // 提交评价按钮点击事件
    public interface OnSubmitClickListener {
        void onSubmitClick(String goods_id, String rating, String content, int ps);
    }

    // 添加图片的点击事件
    public interface OnImageClickListener {
        void onImageClick(GridView mGridView, Out_imgAdapter mAdapter, int currentPs, int imagePs);
    }

    class EvaluationHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTitleText;
        RatingBar mRatingBar;
        EditText mContentEdit;
        Out_imgAdapter imgAdapter;
        TextView mSubmitBtnText;
        MyGridView mGridView;
        Bitmap bmp;//导入临时图片
        public EvaluationHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mTitleText = (TextView) itemView.findViewById(R.id.name);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.rating);
            mContentEdit = (EditText) itemView.findViewById(R.id.content);
            mSubmitBtnText = (TextView) itemView.findViewById(R.id.submit_button);
            mGridView = (MyGridView) itemView.findViewById(R.id.gridView1);
            bmp = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.up_head); //加号
            imgAdapter = new Out_imgAdapter(mContext);
            imgAdapter.addItemImage(bmp, "");
        }
    }
    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position, final Out_imgAdapter imgAdapter1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //删除集合里临时数据
                imgAdapter1.deleteItemImage(position);
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
    private void showDownUpPopupDialog(View view1) {
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
        popupDialog = PopupUtils.createPopupDialog(mContext, popup);
        popupDialog.showAtLocation(view1, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, popup.getxPos(), popup.getyPos());
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
                            publishEvaluationFragment.startActivityForResult(intent, GET_IMAGE_VIA_CAMERA);
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
                    publishEvaluationFragment.startActivityForResult(intent, IMAGE_OPEN);
                }
            }
        };
        view.findViewById(R.id.tvCancel).setOnClickListener(l);
        view.findViewById(R.id.tvTakeHeader).setOnClickListener(l);
        view.findViewById(R.id.tvHeaderFromSD).setOnClickListener(l);
    }

    //外部查询返回
    public void settingImg(Uri uri) {
        Bitmap mBitmap = null;
        Cursor cursor = fragmentActivity.managedQuery(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
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
        imgAdapters.addItemImage(mBitmap, pathImage);
        notifyItemChanged(ps);
        imgMap.put(ps + "", imgAdapters);
    }

    public int deleView(int index) {
        mDataList.remove(index);
        notifyItemRemoved(index);
        return mDataList.size();
    }
}
