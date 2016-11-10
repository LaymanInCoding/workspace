package com.witmoon.xmb.activity.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.AreaChooserActivity;
import com.witmoon.xmb.activity.me.adapter.Out_imgAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.popupwindow.Popup;
import com.witmoon.xmb.ui.popupwindow.PopupDialog;
import com.witmoon.xmb.ui.popupwindow.PopupUtils;
import com.witmoon.xmb.ui.widget.IncreaseReduceTextView;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by de on 2015/11/25
 */
public class Out_ServiceActivity extends BaseActivity implements View.OnClickListener {
    private GridView Image_view;//网格显示缩略图
    private final int IMAGE_OPEN = 1, GET_IMAGE_VIA_CAMERA = 2;//打开图片标记
    private String pathImage;//选择图片路径
    private Bitmap bmp;//导入临时图片
    private Out_imgAdapter imgAdapter;
    public static final int AREA_CHOOSER_CODE = 5;
    //图片名称
    private String[] str = new String[]{"null", "refund_pic1", "refund_pic2", "refund_pic3"};
    //ppw
    PopupDialog popupDialog;
    private String localTempImgFileName = System.currentTimeMillis() + "";
    private String localTempImgDir = "localTempImgDir";
    private EditText region;
    private Out_ out;
    JSONObject ssm;
    private boolean is_or = false;
    private IncreaseReduceTextView mIncreaseReduceTextView;
    private JSONArray mJSONArray;
    DecimalFormat mDecimalFormat = new DecimalFormat("##0.00");
    private String order_id;//订单编号
    //问题描述，收货人。 电话 address
    private EditText edit_text, name, phone, detailed_address;
    private LinearLayout setting_out_t;
    private List<String> numlist;
    private List<IncreaseReduceTextView> onlist;
    private float price_;
    private RadioButton trade, quit;
    private TextView title, serial_no, serial_count;
    int serial_counts = 0;
    private String mProvinceId;
    private String mCityId;
    AppContext applicationContext;
    private String mDistrictId;
    private LinearLayout lin_is, lin_is_s;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_out_service;
    }

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.text_receiver_out_service;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        setTitleColor_(R.color.master_me);
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_me));
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        applicationContext = (AppContext) getApplicationContext();
        trade = (RadioButton) findViewById(R.id.trade);
        quit = (RadioButton) findViewById(R.id.quit);
        out = (Out_) getIntent().getSerializableExtra("order");
        title = (TextView) findViewById(R.id.title);
        serial_count = (TextView) findViewById(R.id.serial_count);
        serial_no = (TextView) findViewById(R.id.serial_no);
        edit_text = (EditText) findViewById(R.id.edit_text1);
        Log.e("data",edit_text.getText().toString());
        lin_is = (LinearLayout) findViewById(R.id.lin_is);
        lin_is_s = (LinearLayout) findViewById(R.id.lin_is_s);
        findViewById(R.id.submit_button).setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        detailed_address = (EditText) findViewById(R.id.detailed_address);
        setting_out_t = (LinearLayout) findViewById(R.id.setting_out_t);
        Image_view = (GridView) findViewById(R.id.gridView1);
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.mbq_post_add); //加号
        region = (EditText) findViewById(R.id.region);
        imgAdapter = new Out_imgAdapter(Out_ServiceActivity.this);
        imgAdapter.addItemImage(bmp, "");
        Image_view.setAdapter(imgAdapter);
        UserApi.out_addss(out.getOrder_id(), new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.e("response", response.toString());
                    JSONObject jsonObject = response.getJSONObject("status");
                    if (jsonObject.getString("succeed").equals("0")) {
                        AppContext.showToast("获取信息失败！");
                        return;
                    }
                    ssm = response;
                    parsJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        registered_click();
    }

    //注册点击事件
    public void registered_click() {
        trade.setOnClickListener(this);
        quit.setOnClickListener(this);
        region.setOnClickListener(this);
        findViewById(R.id.all_out).setOnClickListener(this);
        /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        Image_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position != 0) {
                    dialog(position);
                } else if (imgAdapter.mList.size() == 4) { //第一张为默认图片
                    AppContext.showToast("图片数3张已满");
                } else if (position == 0) { //点击图片位置为+ 0对应0张图片
                    //选择图片
                    showDownUpPopupDialog();
                }
            }
        });
    }

    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                settingImg(uri);
            }
        } else if (requestCode == GET_IMAGE_VIA_CAMERA) {
            File f = new File(Environment.getExternalStorageDirectory()
                    + "/" + localTempImgDir + "/" + localTempImgFileName);
            try {
                Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                        f.getAbsolutePath(), null, null));
                settingImg(u);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == AREA_CHOOSER_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                region.setText(data.getStringExtra("address"));
                String[] regionIdArray = data.getStringExtra("regionId").split(",");
                mProvinceId = regionIdArray[0];
                mCityId = regionIdArray[1];
                mDistrictId = regionIdArray[2];
            }
        }
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        popupDialog = PopupUtils.createPopupDialog(this, popup);
        popupDialog.showAtLocation(this.findViewById(R.id.gridView1), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, popup.getxPos(), popup.getyPos());
        View view = popupDialog.getContentView();
        //背景透明度设置
        view.findViewById(R.id.flMaskLayer).setAlpha(0.75f);
        View.OnClickListener l =    new View.OnClickListener() {
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

    public PopupDialog getPopupDialog() {
        return popupDialog;
    }

    //外部查询返回
    private void settingImg(Uri uri) {
        Bitmap mBitmap = null;
        Cursor cursor = managedQuery(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        //返回 没找到选择图片
        if (null == cursor) {
            return;
        }
        //光标移动至开头 获取图片路径
        cursor.moveToFirst();
        pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        try {
            mBitmap = BitmapUtils.getCompressedImage(pathImage,5);
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

    private void parsJson(JSONObject object) throws JSONException {
        JSONObject js = object.getJSONObject("data");
        order_id = js.getString("order_id");
        title.setText(js.getString("order_sn"));
        serial_no.setText("¥" + js.getString("refund_total_money"));
        serial_count.setText("数量" + js.getString("refund_total_goods_num"));
        JSONArray array = js.getJSONArray("refund_goods_detail");
        onlist = new ArrayList<>();
        final List<String> price_List = new ArrayList<>();
        numlist = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jb = array.getJSONObject(i);
            View goodsContainerView = LayoutInflater.from(this).inflate(R.layout.item_out_service_, setting_out_t, false);
            mIncreaseReduceTextView = (IncreaseReduceTextView) goodsContainerView.findViewById(R.id.goods_number_edit);
            mIncreaseReduceTextView.setting_num();
            onlist.add(mIncreaseReduceTextView);
            mIncreaseReduceTextView.setNumber(Integer.valueOf(jb.getString("goods_refund_number")));
            numlist.add(jb.getString("goods_max_refund_number"));
            ImageView imageView = (ImageView) goodsContainerView.findViewById(R.id.goods_image);
            Netroid.displayBabyImage(jb.getString("goods_img"),imageView);
            TextView title = (TextView) goodsContainerView.findViewById(R.id.goods_title);
            title.setText(jb.getString("goods_name"));
            TextView price = (TextView) goodsContainerView.findViewById(R.id.goods_price);
            price.setText("¥" + jb.getString("goods_price"));
            price_List.add(jb.getString("goods_price"));
            TextView count = (TextView) goodsContainerView.findViewById(R.id.goods_count);
            count.setText("退货数量(最多" + jb.getString("goods_max_refund_number") + "件)");
            jb.getString("goods_id");
            jb.getString("goods_name");
            jb.getString("goods_max_refund_number");
            setting_out_t.addView(goodsContainerView);
        }
        if (js.getString("refund_type").equals("1")) {
            quit.setChecked(true);
            quit.setTextColor(Color.RED);
        } else {
            trade.setChecked(true);
            trade.setTextColor(Color.RED);
        }

        if (quit.isChecked()) {
            lin_is.setVisibility(View.VISIBLE);
            lin_is_s.setVisibility(View.GONE);
        }
        edit_text.setText(js.getString("refund_reason"));
        phone.setText(js.getString("mobile"));
        name.setText(js.getString("consignee"));
        mProvinceId = js.getString("province");
        mCityId = js.getString("city");
        mDistrictId = js.getString("district");
        region.setText(applicationContext.getXmbDB().loadCao(mProvinceId) + applicationContext.getXmbDB().loadCao(mCityId) +
                applicationContext.getXmbDB().loadCao(mDistrictId));
//        region.setText(js.getString("province") + "—" + js.getString("city") + "—" + js.getString("district"));
        detailed_address.setText(js.getString("address"));
        if (js.getString("refund_pic1") + "" != "") {
            imgAdapter.addItemImage(null, js.getString("refund_pic1"));
        }
        if (js.getString("refund_pic2") + "" != "") {
            imgAdapter.addItemImage(null, js.getString("refund_pic2"));
        }
        if (js.getString("refund_pic3") + "" != "") {
            imgAdapter.addItemImage(null, js.getString("refund_pic3"));
        }
        for (int i = 0; i < onlist.size(); i++) {
            final int finalI = i;
            onlist.get(finalI).setOnNumberChangeListener(new IncreaseReduceTextView.OnNumberChangeListener() {
                @Override
                public void onNumberChange(int number) {
                    if (number > Integer.valueOf(numlist.get(finalI))) {
                        onlist.get(finalI).setNumber(Integer.valueOf(numlist.get(finalI)));
                        AppContext.showToast("已是最大数量！");
                    } else {
                        price_ = 0;
                        serial_counts = 0;
                        for (int i = 0; i < onlist.size(); i++) {
                            serial_counts = serial_counts + onlist.get(i).getNumber();
                            serial_count.setText("数量" + serial_counts + "");
                            price_ = price_ + (Float.parseFloat(price_List.get(i)) * onlist.get(i).getNumber());
                            serial_no.setText("￥：" + mDecimalFormat.format(price_) + "");
                        }
                    }
                }
            });
        }
    }

    private boolean Checkis_() {
        boolean is_ = false;
        Log.e("data",edit_text.getText().toString());
        if (edit_text.getText().toString().trim().equals("")) {
            AppContext.showToast("请输入问题描述！");
        } else if (name.getText().toString().trim().equals("")) {
            AppContext.showToast("请输入收货人！");
        } else if (phone.getText().toString().trim().equals("")) {
            AppContext.showToast("请输入联系电话！");
        } else if (region.getText().toString().trim().equals("")) {
            AppContext.showToast("请选择收货地址！");
        } else if (detailed_address.getText().toString().trim().equals("")) {
            AppContext.showToast("请输入详细地址！");
        } else if (imgAdapter.mList.size() <= 1) {
            AppContext.showToast("请上传图片！");
        } else if (!is_or) {
            AppContext.showToast("请选择商品数量！");
        } else {
            is_ = true;
        }
        return is_;
    }

    private boolean is_checks() {
        boolean is_s = false;
        if (edit_text.getText().toString().trim().equals("")) {
            AppContext.showToast("请输入问题描述！");
        } else if (imgAdapter.mList.size() <= 1) {
            AppContext.showToast("请上传图片！");
        } else if (!is_or) {
            AppContext.showToast("请选择商品数量！");
        } else {
            is_s = true;
        }
        return is_s;
    }

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, Out_ServiceActivity> {

        public ModifyAsyncTask(Out_ServiceActivity mOut_ServiceActivity) {
            super(mOut_ServiceActivity);
        }

        @Override
        protected String doInBackground(Out_ServiceActivity mOut_ServiceActivity,
                                        Void... params) {
            List<HashMap<String, Object>> mList = imgAdapter.mList;
            Map<String, File> fm = new HashMap<>();
            for (int i = 1; i < mList.size(); i++) {
                fm.put(str[i], saveAvatarBitmap((Bitmap) mList.get(i).get("itemImage"), (String) mList.get(i).get("imgPath")));
            }
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("order_id", order_id);
                pm.put("refund_goods", mJSONArray.toString());
                if (quit.isChecked()) {
                    pm.put("refund_type", "1");
                } else {
                    pm.put("refund_type", "2");
                }
                pm.put("refund_way", "0");
                pm.put("refund_reason", edit_text.getText().toString());
                pm.put("consignee", name.getText().toString());
                pm.put("mobile", phone.getText().toString());
                pm.put("province", mProvinceId);
                pm.put("city", mCityId);
                pm.put("district", mDistrictId);
                pm.put("address", detailed_address.getText().toString());
                String response = HttpUtility.post(ApiHelper.getAbsoluteApiUrl("/refund/apply"), null, pm, fm);
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
        protected void onPostExecute(Out_ServiceActivity mOut_ServiceActivity, String result) {
            if (result != null) {
                AppContext.showToastShort(result);
                return;
            }
            Intent intent = new Intent(Const.INTENT_ACTION_TUI);
            out.setRefund_status("2");
            intent.putExtra("order", out);
            sendBroadcast(intent);
            AppContext.showToastShort("操作成功");
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", out);
            UIHelper.showSimpleBack(Out_ServiceActivity.this, SimpleBackPage.JINDU, bundle);
            finish();
        }
    }

    private class ModifyAsyncTask1 extends WeakAsyncTask<Void, Void, String, Out_ServiceActivity> {

        public ModifyAsyncTask1(Out_ServiceActivity mOut_ServiceActivity) {
            super(mOut_ServiceActivity);
        }

        @Override
        protected String doInBackground(Out_ServiceActivity mOut_ServiceActivity,
                                        Void... params) {
            List<HashMap<String, Object>> mList = imgAdapter.mList;
            Map<String, File> fm = new HashMap<>();
            for (int i = 1; i < mList.size(); i++) {
                fm.put(str[i], saveAvatarBitmap((Bitmap) mList.get(i).get("itemImage"), (String) mList.get(i).get("imgPath")));
            }
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("order_id", order_id);
                pm.put("refund_goods", mJSONArray.toString());
                if (quit.isChecked()) {
                    pm.put("refund_type", "1");
                } else {
                    pm.put("refund_type", "2");
                }
                pm.put("refund_way", "1");
                pm.put("refund_reason", edit_text.getText().toString());
                pm.put("consignee", "");
                pm.put("mobile", "");
                pm.put("province", "");
                pm.put("city", "");
                pm.put("district", "");
                pm.put("address", "");
                Log.e("pm", pm.toString());
                Log.e("mas", fm.toString());
                String response = HttpUtility.post(ApiHelper.getAbsoluteApiUrl("/refund/apply"), null, pm, fm);
                JSONObject respObj = new JSONObject(response);
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
        protected void onPostExecute(Out_ServiceActivity mOut_ServiceActivity, String result) {
            if (result != null) {
                AppContext.showToastShort(result);
                return;
            }
            Intent intent = new Intent(Const.INTENT_ACTION_TUI);
            out.setRefund_status("2");
            intent.putExtra("order", out);
            sendBroadcast(intent);
            AppContext.showToastShort("操作成功");
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", out);
            UIHelper.showSimpleBack(Out_ServiceActivity.this, SimpleBackPage.JINDU, bundle);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.region:
                startActivityForResult(new Intent(Out_ServiceActivity.this, AreaChooserActivity.class),
                        AREA_CHOOSER_CODE);
                break;
            case R.id.trade:
                lin_is.setVisibility(View.GONE);
                lin_is_s.setVisibility(View.VISIBLE);
                trade.setTextColor(Color.RED);
                trade.setChecked(true);
                quit.setTextColor(Color.BLACK);
                break;
            case R.id.quit:
                trade.setTextColor(Color.BLACK);
                lin_is.setVisibility(View.VISIBLE);
                lin_is_s.setVisibility(View.GONE);
                quit.setTextColor(Color.RED);
                quit.setChecked(true);
                break;
            case R.id.submit_button:
                Log.e("out.getRefund_status()", out.getRefund_status());
                try {
                    parsJson1(ssm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (AppContext.isNetworkAvailable(Out_ServiceActivity.this)) {
                    if (quit.isChecked()) {
                        if (is_checks()) {
                            ModifyAsyncTask1 asyncTask = new ModifyAsyncTask1(this);
                            asyncTask.execute();
                        }
                    }

                    if (trade.isChecked()) {
                        if (Checkis_()) {
                            ModifyAsyncTask asyncTask = new ModifyAsyncTask(this);
                            asyncTask.execute();
                        }
                    }
                } else {
                    AppContext.showToast("请检查当前网络");
                }
                break;
            default:
                break;
        }
    }

    private void parsJson1(JSONObject object) throws JSONException {
        JSONObject js = object.getJSONObject("data");
        JSONArray array = js.getJSONArray("refund_goods_detail");
        Log.e("data",array.toString());
        mJSONArray = null;
        mJSONArray = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jb = array.getJSONObject(i);
            JSONObject up_js = new JSONObject();
            up_js.put(jb.getString("goods_id"), onlist.get(i).getNumber() + "");
            if (onlist.get(i).getNumber() >= 1) {
                is_or = true;
            }
            mJSONArray.put(i, up_js);
        }
    }
}

