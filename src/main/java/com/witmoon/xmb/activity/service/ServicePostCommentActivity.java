package com.witmoon.xmb.activity.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.baby.LocalAlbum;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.dialog.DialogControl;
import com.witmoon.xmb.ui.widget.AlbumViewPager;
import com.witmoon.xmb.ui.widget.FilterImageView;
import com.witmoon.xmb.ui.widget.MatrixImageView;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.FileUtils;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.ImageUtils;
import com.witmoon.xmb.util.LocalImageHelper;
import com.witmoon.xmb.util.StringUtils;
import com.witmoon.xmb.util.WeakAsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author linjizong
 * @Description:发布动态界面
 * @date 2015-5-14
 */
public class ServicePostCommentActivity extends BaseActivity implements OnClickListener, DialogControl, MatrixImageView.OnSingleTapListener {

    private ImageView mBack;//返回键
    private View mSend;//发送
    private EditText mContent;//动态内容编辑框
    private InputMethodManager imm;//软键盘管理
    private TextView textRemain;//字数提示
    private TextView picRemain;//图片数量提示
    private ImageView add;//添加图片按钮
    private LinearLayout picContainer;//图片容器
    private List<LocalImageHelper.LocalFile> pictures = new ArrayList<>();//图片路径数组
    HorizontalScrollView scrollView;//滚动的图片容器
    View editContainer;//动态编辑部分
    View pagerContainer;//图片显示部分

    //显示大图的viewpager 集成到了Actvity中 下面是和viewpager相关的控件
    AlbumViewPager viewpager;//大图显示pager
    ImageView mBackView;//返回/关闭大图
    TextView mCountView;//大图数量提示
    View mHeaderBar;//大图顶部栏
    ImageView delete;//删除按钮
    int index;

    int size;//小图大小
    int padding;//小图间距
    DisplayImageOptions options;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_post_comment);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //设置ImageLoader参数
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        initViews();
        initData();
        index = getIntent().getIntExtra("index",0);
        LocalImageHelper.getInstance().clear();
        activity = this;
    }


    /**
     * @Description： 初始化Views
     */
    private void initViews() {
        // TODO Auto-generated method stub
        setTitleColor_(R.color.main_kin);
        mBack = (ImageView) findViewById(R.id.post_back);
        mSend = findViewById(R.id.post_send);
        mContent = (EditText) findViewById(R.id.post_content);
        textRemain = (TextView) findViewById(R.id.post_text_remain);
        picRemain = (TextView) findViewById(R.id.post_pic_remain);
        add = (ImageView) findViewById(R.id.post_add_pic);
        picContainer = (LinearLayout) findViewById(R.id.post_pic_container);
        scrollView = (HorizontalScrollView) findViewById(R.id.post_scrollview);
        viewpager = (AlbumViewPager) findViewById(R.id.albumviewpager);
        mBackView = (ImageView) findViewById(R.id.header_bar_photo_back);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        mHeaderBar = findViewById(R.id.album_item_header_bar);
        delete = (ImageView) findViewById(R.id.header_bar_photo_delete);
        editContainer = findViewById(R.id.post_edit_container);
        pagerContainer = findViewById(R.id.pagerview);
        delete.setVisibility(View.VISIBLE);

        TextView shop_name = (TextView) findViewById(R.id.shop_name);
        shop_name.setText(getIntent().getStringExtra("shop_name"));

        viewpager.setOnPageChangeListener(pageChangeListener);
        viewpager.setOnSingleTapListener(this);
        mBackView.setOnClickListener(this);
        mCountView.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mSend.setOnClickListener(this);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);

        mContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable content) {
                textRemain.setText(content.toString().length() + "/140");
            }
        });
    }

    private void initData() {
        size = (int) getResources().getDimension(R.dimen.dimen_146_dip);
        padding = (int) getResources().getDimension(R.dimen.padding_10);
    }

    @Override
    public void onBackPressed() {
        if (pagerContainer.getVisibility() != View.VISIBLE) {
            //showSaveDialog();
        } else {
            hideViewPager();
        }
    }


    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, ServicePostCommentActivity> {

        public ModifyAsyncTask(ServicePostCommentActivity mUpRecordFragment) {
            super(mUpRecordFragment);
        }

        @Override
        protected void onPreExecute(ServicePostCommentActivity upRecordFragment) {
            super.onPreExecute(upRecordFragment);
            showWaitDialog();
        }

        @Override
        protected String doInBackground(ServicePostCommentActivity mUpRecordFragment,
                                        Void... params) {
            List<File> fielList = new ArrayList<>();
            for (int i = 0; i < pictures.size(); i++) {
                Uri uri = Uri.parse(pictures.get(i).getOriginalUri());
                String img_path = FileUtils.getPath(activity, uri);
                Bitmap bmp = BitmapUtils.getCompressedImage(img_path, 2);
                try {
                    String f = BitmapUtils.saveMyBitmap(bmp, "tmp" + i, activity);
                    File file = new File(f);
                    fielList.add(file);
                } catch (Exception e) {

                }
            }
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("content", mContent.getText().toString().trim());
                pm.put("order_id", getIntent().getIntExtra("order_id",0)+"");

                String response = HttpUtility.post("https://api.xiaomabao.com/service/post_comment", null, pm, "photo", fielList);
                JSONObject respObj = new JSONObject(response);
                if (respObj.getString("status").equals("0")) {
                    return respObj.getString("info");
                }
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ServicePostCommentActivity mUpRecordFragment, String result) {
            hideWaitDialog();
            if (result != null) {
                AppContext.showToastShort(result);
                mSend.setEnabled(true);
                return;
            }
            AppContext.showToastShort("操作成功");
            Intent intent = new Intent(ServicePostCommentActivity.this,CommentSuccessActivity.class);
            startActivity(intent);
            Intent intent2 = new Intent(Const.INTENT_ACTION_REFRESH_SERVICE_ORDER);
            intent2.putExtra("index",index);
            sendBroadcast(intent2);
            finish();
        }
    }


    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.post_back:
                finish();
                break;
            case R.id.header_bar_photo_back:
            case R.id.header_bar_photo_count:
                hideViewPager();
                break;
            case R.id.header_bar_photo_delete:
                final int index = viewpager.getCurrentItem();
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("要删除这张照片吗?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pictures.remove(index);
                                if (pictures.size() == 6) {
                                    add.setVisibility(View.GONE);
                                } else {
                                    add.setVisibility(View.VISIBLE);
                                }
                                if (pictures.size() == 0) {
                                    hideViewPager();
                                }
                                picContainer.removeView(picContainer.getChildAt(index));
                                picRemain.setText(pictures.size() + "/6");
                                mCountView.setText((viewpager.getCurrentItem() + 1) + "/" + pictures.size());
                                viewpager.getAdapter().notifyDataSetChanged();
                                LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                            }
                        })
                        .show();

                break;
            case R.id.post_send:
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String content = mContent.getText().toString();
                if (StringUtils.isEmpty(content)) {
                    Toast.makeText(this, "请填写这一刻的想法", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //设置为不可点击，防止重复提交
                    view.setEnabled(false);
                    ModifyAsyncTask task = new ModifyAsyncTask(this);
                    task.execute();
                }
                break;
            case R.id.post_add_pic:
                Intent intent = new Intent(ServicePostCommentActivity.this, LocalAlbum.class);
                startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
                break;
            default:
                if (view instanceof FilterImageView) {
                    for (int i = 0; i < picContainer.getChildCount(); i++) {
                        if (view == picContainer.getChildAt(i)) {
                            showViewPager(i);
                        }
                    }
                }
                break;
        }
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (viewpager.getAdapter() != null) {
                String text = (position + 1) + "/" + viewpager.getAdapter().getCount();
                mCountView.setText(text);
            } else {
                mCountView.setText("0/0");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    //显示大图pager
    private void showViewPager(int index) {
        pagerContainer.setVisibility(View.VISIBLE);
        editContainer.setVisibility(View.GONE);
        viewpager.setAdapter(viewpager.new LocalViewPagerAdapter(pictures));
        viewpager.setCurrentItem(index);
        mCountView.setText((index + 1) + "/" + pictures.size());
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    //关闭大图显示
    private void hideViewPager() {
        pagerContainer.setVisibility(View.GONE);
        editContainer.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    @Override
    public void onSingleTap() {
        hideViewPager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                if (LocalImageHelper.getInstance().isResultOk()) {
                    LocalImageHelper.getInstance().setResultOk(false);
                    //获取选中的图片
                    List<LocalImageHelper.LocalFile> files = LocalImageHelper.getInstance().getCheckedItems();
                    for (int i = 0; i < files.size(); i++) {
                        LayoutParams params = new LayoutParams(size, size);
                        params.rightMargin = padding;
                        FilterImageView imageView = new FilterImageView(this);
                        imageView.setScaleType(ScaleType.CENTER_CROP);
                        imageView.setLayoutParams(params);
                        ImageLoader.getInstance().displayImage(files.get(i).getOriginalUri(), new ImageViewAware(imageView), options);
                        imageView.setOnClickListener(this);
                        pictures.add(files.get(i));
                        if (pictures.size() == 6) {
                            add.setVisibility(View.GONE);
                        } else {
                            add.setVisibility(View.VISIBLE);
                        }
                        picContainer.addView(imageView, picContainer.getChildCount() - 1);
                        picRemain.setText(pictures.size() + "/6");
                        LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                    }
                    //清空选中的图片
                    files.clear();
                    //设置当前选中的图片数量
                    LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                    //延迟滑动至最右边
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    }, 50L);
                }
                //清空选中的图片
                LocalImageHelper.getInstance().getCheckedItems().clear();
                break;
            default:
                break;
        }
    }
}
