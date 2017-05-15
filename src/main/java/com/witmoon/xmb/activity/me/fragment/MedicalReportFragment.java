package com.witmoon.xmb.activity.me.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.util.Constants;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.NumberProgressBar;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.XmbUtils;
import com.yxp.permission.util.lib.PermissionUtil;
import com.yxp.permission.util.lib.callback.PermissionResultCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ming on 2017/5/3.
 */
public class MedicalReportFragment extends BaseFragment implements OnLoadCompleteListener, OnRenderListener {

    private EmptyLayout mEmptyLayout;
    private EditText mIdentityEdit;
    private EditText mNameEdit;
    private Button mButton;
    private LinearLayout mQueryLayout;
    private PDFView mPdfView;
    private NumberProgressBar mProgressbar;
    private ImageView mNoRecordImg;


    private String url = "";
    private String name;
    private String idcard;
    private String fname;

    public static final int PERMISSION_CODE = 42042;
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_report, container, false);
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
        mQueryLayout = (LinearLayout) view.findViewById(R.id.query_layout);
        mIdentityEdit = (EditText) view.findViewById(R.id.identity_edit);
        mNameEdit = (EditText) view.findViewById(R.id.name_edit);
        mButton = (Button) view.findViewById(R.id.query_button);
        mPdfView = (PDFView) view.findViewById(R.id.pdfView);
        mProgressbar = (NumberProgressBar) view.findViewById(R.id.progress);
        mNoRecordImg = (ImageView) view.findViewById(R.id.no_record_img);
        mButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_button:
                name = mNameEdit.getText().toString().trim();
                idcard = mIdentityEdit.getText().toString().trim();
                mButton.setClickable(false);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mButton.setClickable(true);
                    }
                }, 2000);
                if (name.equals("")) {
                    XmbUtils.showWarning(getContext(), "姓名不能为空");
                    return;
                }
                if (idcard.equals("")) {
                    XmbUtils.showWarning(getContext(), "身份证号不能为空");
                    return;
                }
                showWaitDialog();
                setRecRequest(1);
        }
    }

    @Override
    public void setRecRequest(int currentPage) {
        Logger.d("request");
        UserApi.getPdfUrl(idcard, name, new Listener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                hideWaitDialog();
                try {
                    int status = response.getInt("status");
                    if (status == 200) {
                        XmbUtils.showWarning(getContext(), response.getString("info"));
                        return;
                    } else if (status == 100) {
                        XmbUtils.showWarning(getContext(), response.getString("info"));
                        return;
                    } else if (status == 300) {
                        mQueryLayout.setVisibility(View.GONE);
                        mNoRecordImg.setVisibility(View.VISIBLE);
                        return;
                    } else if (status == 0) {
                        Constants.THUMBNAIL_RATIO = 1.0f;
                         url = "http://api.xiaomabao.com/meinian/pdf/1493710489622203.pdf";
                        url = response.getString("data");
                        String[] strings = url.split("pdf/");
                        String s =strings[1];
                        String[] strings2 = s.split("\\.");
                        //fname = 1493710489622203;
                        fname = strings2[0];
                        Logger.d(fname);
                        PermissionUtil.getInstance().request(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionResultCallBack() {
                            @Override
                            public void onPermissionGranted() {
                                String dir = Environment.getExternalStorageDirectory() + "/xmb/" + fname;
                                String destFileName = "medical.pdf";
                                File file = new File(dir, destFileName);
                                if (file.exists()) {
                                    Logger.d("exist");
                                    mQueryLayout.setVisibility(View.GONE);
                                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                                    mPdfView.fromFile(file)
                                            .defaultPage(0)
                                            .onRender(MedicalReportFragment.this)
                                            .enableSwipe(true)
                                            .enableDoubletap(true)
                                            .swipeHorizontal(false)
                                            .onLoad(MedicalReportFragment.this)
                                            .load();
                                    return;
                                }
                                OkGo.get(url)
                                        .tag(this)
                                        .execute(new DownloadFileCallBack(dir, destFileName));
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                CommonUtil.showShort(getContext(), "网络状况不太好哦！");
            }
        });
    }

    @Override
    public void loadComplete(int nbPages) {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mPdfView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
        mPdfView.fitToWidth();
    }

    private class DownloadFileCallBack extends FileCallback {

        public DownloadFileCallBack(String destFileDir, String destFileName) {
            super(destFileDir, destFileName);
        }

        @Override
        public void onBefore(BaseRequest request) {
            super.onBefore(request);
            Logger.d("onBefore");
//            mProgressbar.setVisibility(View.VISIBLE);
            mQueryLayout.setVisibility(View.GONE);
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
            Logger.d("downloadProgress");
//            mProgressbar.setMax(100);
//            mProgressbar.setProgress((int) (progress * 100));
        }

        @Override
        public void onAfter(File file, Exception e) {
            super.onAfter(file, e);
            Logger.d("onAfter");
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//            mProgressbar.setVisibility(View.GONE);
            mPdfView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(Call call, Response response, Exception e) {
            super.onError(call, response, e);
            Logger.d("onError");
        }

        @Override
        public void onSuccess(File file, Call call, Response response) {
            Logger.d("onSuccess");
            mPdfView.fromFile(file)
                    .onRender(MedicalReportFragment.this)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .swipeHorizontal(false)
                    .load();
        }
    }
}
