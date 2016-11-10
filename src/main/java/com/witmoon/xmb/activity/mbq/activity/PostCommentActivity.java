package com.witmoon.xmb.activity.mbq.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostCommentActivity extends BaseActivity {

    private int size;
    private int padding;
    private List<LocalImageHelper.LocalFile> pictures = new ArrayList<>();//图片路径数组
    private LinearLayout picContainer;
    private View add;
    DisplayImageOptions options;
    private HorizontalScrollView scrollView;
    View pagerContainer;//图片显示部分
    private TextView toolbar_right_text;

    AlbumViewPager viewpager;//大图显示pager
    ImageView mBackView;//返回/关闭大图
    TextView mCountView;//大图数量提示
    View mHeaderBar;//大图顶部栏
    ImageView delete;//删除按钮
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
    public void onDestroy(){
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
    protected void  initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        post_id = getIntent().getStringExtra("post_id");
        comment_reply_id = getIntent().getStringExtra("comment_reply_id");
        size = (int) getResources().getDimension(R.dimen.size_100);
        padding = (int) getResources().getDimension(R.dimen.padding_10);
        picContainer = (LinearLayout) findViewById(R.id.post_pic_container);
        scrollView = (HorizontalScrollView)findViewById(R.id.post_scrollview);
        add = findViewById(R.id.post_add_pic);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        content_edit = (EditText) findViewById(R.id.post_content);
        toolbar_right_text = (TextView) findViewById(R.id.toolbar_right_text);
        viewpager = (AlbumViewPager) findViewById(R.id.albumviewpager);
        viewpager.setOnPageChangeListener(pageChangeListener);
        mBackView = (ImageView) findViewById(R.id.header_bar_photo_back);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        mHeaderBar = findViewById(R.id.album_item_header_bar);
        delete = (ImageView) findViewById(R.id.header_bar_photo_delete);
        pagerContainer = findViewById(R.id.pagerview);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        toolbar_right_text.setOnClickListener(this);
        LocalImageHelper.getInstance().clear();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostCommentActivity.this, LocalAlbum.class);
                startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
            }
        });
        IntentFilter finishActivity = new IntentFilter(Const.INTENT_ACTION_REFRESH_POST);
        registerReceiver(finishThisActivity, finishActivity);
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
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                        params.rightMargin = padding;
                        FilterImageView imageView = new FilterImageView(this);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right_text:
                String content = content_edit.getText().toString();
                if (StringUtils.isEmpty(content)) {
                    XmbUtils.showMessage(PostCommentActivity.this,"内容不能为空哦~");
                    return;
                } else {
                    //设置为不可点击，防止重复提交
                    view.setEnabled(false);
                    ModifyAsyncTask task = new ModifyAsyncTask(this);
                    task.execute();
                }
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
                                mCountView.setText((viewpager.getCurrentItem() + 1) + "/" + pictures.size());
                                viewpager.getAdapter().notifyDataSetChanged();
                                LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                            }
                        })
                        .show();

                break;
            default:
                if (view instanceof FilterImageView) {
                    for (int i = 0; i < picContainer.getChildCount(); i++) {
                        if (view == picContainer.getChildAt(i)) {
                            showViewPager(i);
                        }
                    }
                }
        }
    }

    //显示大图pager
    private void showViewPager(int index) {
        pagerContainer.setVisibility(View.VISIBLE);
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
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

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

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, PostCommentActivity> {

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
                                        Void... params) {
            List<File> fileList = new ArrayList<>();
            for (int i = 0; i < pictures.size(); i++) {
                Uri uri = Uri.parse(pictures.get(i).getOriginalUri());
                String img_path = FileUtils.getPath(PostCommentActivity.this, uri);
                Bitmap bmp = BitmapUtils.getCompressedImage(img_path, 2);
                try {
                    String f = BitmapUtils.saveMyBitmap(bmp, "tmp" + i, PostCommentActivity.this);
                    File file = new File(f);
                    fileList.add(file);
                } catch (Exception e) {
                }
            }
            Map<String, String> pm = new HashMap<>();
            try {
                pm.put("session[uid]", AppContext.getLoginUid() + "");
                pm.put("session[sid]", ApiHelper.mSessionID);
                pm.put("comment_content",content_edit.getText().toString());
                pm.put("comment_reply_id",comment_reply_id);
                pm.put("post_id",post_id);
                Log.e("data", pm.toString());
                String response = HttpUtility.post("http://api.xiaomabao.com/UserCircle/add_comment", null, pm, "photo", fileList);
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
                if (jsonObject.getInt("status") != 1) {
                    toolbar_right_text.setEnabled(true);
                    XmbUtils.showMessage(PostCommentActivity.this, jsonObject.getString("info"));
                    return;
                }
                XmbUtils.showMessageRefreshPost(PostCommentActivity.this, jsonObject.getString("info"));
            } catch (JSONException e) {
                XmbUtils.showMessage(PostCommentActivity.this, "网络异常，未知错误");
            }
        }
    }
}
