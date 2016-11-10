package com.witmoon.xmb.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.witmoon.xmb.AppContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZCM on 2015/11/10
 */
public class Version_Up extends Activity{
    Handler handler = new Handler();
    ProgressDialog pbar;
    HttpClient client;
        public void dloadFile(final String url, final Context context,final String filename) {
        pbar = new ProgressDialog(context);
        pbar.setProgress(0);
        pbar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pbar.setTitle("正在下载");
        pbar.setCanceledOnTouchOutside(false);
        pbar.setMessage("请耐心等候");
        pbar.setProgressNumberFormat("%1d KB/%2d KB");
        pbar.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pbar.dismiss();
                client.getConnectionManager().shutdown();
                //删除APK  下载的文件缓存已删除
                if(FileUtils.deleteFile(filename)) {
                    AppContext.showToast("下载取消，文件已删除", Toast.LENGTH_SHORT);
                }
            }
        });
        pbar.show();
        new Thread() {
            public void run() {
                client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    pbar.setMax(length/1024);//设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        //判断SD卡
                        if (FileUtils.checkSaveLocationExists()) {
                            File file = new File(
                                    context.getExternalCacheDir(),
                                    filename);
                            fileOutputStream = new FileOutputStream(file);
                            byte[] buf = new byte[10];
                            int ch = -1;
                            int process = 0;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);
                                process += ch;
                                //这里就是关键的实时更新进度了！
                                pbar.setProgress(process/1024);
                            }
                        } else {
                            AppContext.showToast("请插入SD卡");
                        }
                        fileOutputStream.flush();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        down(context,filename);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    void down(Context context , String filename) {
        handler.post(new Runnable() {
            public void run() {
                pbar.cancel();
            }
        });
        update(context,filename);
    }
    void update(Context context,String filename) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), filename)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
