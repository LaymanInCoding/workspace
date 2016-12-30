package com.witmoon.xmb.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.UpdataService;
import com.witmoon.xmb.activity.mbq.activity.PostDetailActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.views.MajorArticleDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class XmbUtils {

    public static PopupWindow popupWindow;
    public static PopupWindow shareWindow;

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static void showUpdateWindow(Activity activity, String latest_version, String version_description, String download_url, String size) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_update, null);
        PopupWindow updateWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        updateWindow.setTouchable(true);
        updateWindow.showAsDropDown(contentView);
        ((TextView) contentView.findViewById(R.id.latest_version)).setText(latest_version);
        ((TextView) contentView.findViewById(R.id.new_version_size)).setText(size);
        ((TextView) contentView.findViewById(R.id.version_description)).setText(version_description);
        final String app_name = download_url.substring(download_url.lastIndexOf("/") + 1);
        contentView.findViewById(R.id.update_right_now).setOnClickListener(v -> {
//            updateWindow.dismiss();
            AppContext.showToast("正在后台下载");
            Intent intent = new Intent(activity, UpdataService.class);
            intent.putExtra("Key_App_Name", app_name);
            intent.putExtra("Key_Down_Url", download_url);
            activity.startService(intent);
        });
    }

    public static void showInvoiceInfo(Activity activity) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_invoice_info, null);
        PopupWindow infoWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        infoWindow.setTouchable(true);
        infoWindow.showAsDropDown(contentView);
        Button button = (Button) contentView.findViewById(R.id.ok_btn);
        button.setOnClickListener(v -> infoWindow.dismiss());
    }

    //弹出mbq提示文字
    public static void showMessage(Context context, String message) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_mbq_message, null);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.message);
        messageTextView.setText(message);
        XmbUtils.popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        XmbUtils.popupWindow.setTouchable(true);
        XmbUtils.popupWindow.showAsDropDown(contentView);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                XmbUtils.popupWindow.dismiss();
            }
        }, 1 * 1000);
    }

    public static class AvatarDialogFragment extends DialogFragment {
        DialogInterface.OnClickListener mOnClickListener;
        private static String[] items = {"选择本地图片", "拍照"};

        public void setOnClickListener(DialogInterface.OnClickListener listener) {
            mOnClickListener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Context context = getActivity();
            return new AlertDialog.Builder(context).setTitle("设置照片").setItems(items,
                    mOnClickListener)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
    }

    //弹出mbq提示文字刷新circleActivity  发表帖子
    public static void showMessageRefreshCircle(final Context context, String message) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_mbq_message, null);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.message);
        messageTextView.setText(message);
        XmbUtils.popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        XmbUtils.popupWindow.setTouchable(true);
        XmbUtils.popupWindow.showAsDropDown(contentView);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                XmbUtils.popupWindow.dismiss();
                Intent intent = new Intent(Const.INTENT_ACTION_REFRESH_CIRCLE);
                context.sendBroadcast(intent);
            }
        }, 1 * 1000);
    }

    //弹出mbq提示文字刷新  发表评论
    public static void showMessageRefreshPost(final Context context, String message) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_mbq_message, null);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.message);
        messageTextView.setText(message);
        XmbUtils.popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        XmbUtils.popupWindow.setTouchable(true);
        XmbUtils.popupWindow.showAsDropDown(contentView);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                XmbUtils.popupWindow.dismiss();
                Intent intent = new Intent(Const.INTENT_ACTION_REFRESH_POST);
                context.sendBroadcast(intent);
            }
        }, 1 * 1000);
    }

    //弹出mbq提示文字
    public static void showMessageConfirm(Context context, String message, View.OnClickListener listener) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_mbq_message_confirm, null);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.message);
        messageTextView.setText(message);
        XmbUtils.popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        XmbUtils.popupWindow.setTouchable(true);
        XmbUtils.popupWindow.showAsDropDown(contentView);
        View confirmView = contentView.findViewById(R.id.confirm);
        View cancelView = contentView.findViewById(R.id.cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmbUtils.popupWindow.dismiss();
            }
        });
        confirmView.setOnClickListener(listener);
    }

    public static void joinCircle(final Context context, int circle_id, Listener<JSONObject> listener) {
        if (!AppContext.instance().isLogin()) {
            context.startActivity(new Intent(context, LoginActivity.class));
        } else {
            CircleApi.join_circle(circle_id, listener);
        }
    }

    public static void loginCallback(Activity activity) {
        Intent intent = new Intent(Const.INTENT_ACTION_LOGIN);
        activity.sendBroadcast(intent);

        Intent intent2 = new Intent(Const.INTENT_ACTION_REFRESH);
        activity.sendBroadcast(intent2);

        Intent intent3 = new Intent(Const.INTENT_WEB_REFRESH);
        activity.sendBroadcast(intent3);
    }

    public static void showMbqShare(Context context, View parentView, final HashMap<String, String> params) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_mbq_share, null);
        XmbUtils.popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // Closes the popup window when touch outside.
        XmbUtils.popupWindow.setOutsideTouchable(true);
        XmbUtils.popupWindow.setFocusable(true);
        // Removes default background.
        XmbUtils.popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.anim_pop_bottom);
        XmbUtils.popupWindow.showAtLocation(parentView,
                Gravity.BOTTOM, 0, 0);
        ShareSDK.initSDK(context);
        contentView.findViewById(R.id.share_weixin_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(params.get("title"));
                if (params.get("desc").length() > 50) {
                    sp.setText(params.get("desc").substring(0, 50));
                } else {
                    sp.setText(params.get("desc"));
                }
                sp.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp.setUrl(params.get("url"));
                Platform qzone = ShareSDK.getPlatform(Wechat.NAME);
                qzone.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                    }
                }); // 设置分享事件回调
                qzone.share(sp);
            }
        });

        contentView.findViewById(R.id.share_weixin_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(params.get("title"));
                if (params.get("desc").length() > 50) {
                    sp.setText(params.get("desc").substring(0, 50));
                } else {
                    sp.setText(params.get("desc"));
                }
                sp.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp.setUrl(params.get("url"));
                Platform qzone = ShareSDK.getPlatform(WechatMoments.NAME);
                qzone.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                    }
                }); // 设置分享事件回调
                qzone.share(sp);
            }
        });

        contentView.findViewById(R.id.share_qzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setTitle(params.get("title"));
                if (params.get("desc").length() > 50) {
                    sp.setText(params.get("desc").substring(0, 50));
                } else {
                    sp.setText(params.get("desc"));
                }
                sp.setTitleUrl(params.get("url"));
                sp.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp.setSite("小麻包");
                sp.setSiteUrl("http://www.xiaomabao.com");
                Platform qzone = ShareSDK.getPlatform(QZone.NAME);
                qzone.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                    }
                }); // 设置分享事件回调
                qzone.share(sp);
            }
        });

        contentView.findViewById(R.id.share_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setText(params.get("title"));
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitleUrl(params.get("url"));
                sp.setUrl(params.get("url"));
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                weibo.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                weibo.share(sp);
            }
        });

        contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmbUtils.popupWindow.dismiss();
            }
        });

    }

    public static void showMbqArticleShare(MajorArticleDetailActivity activity, View parentView, final HashMap<String, String> params, int is_collect) {
        View contentView = LayoutInflater.from(activity).inflate(
                R.layout.pop_mbq_article_share, null);
        XmbUtils.shareWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // Closes the popup window when touch outside.
        XmbUtils.shareWindow.setOutsideTouchable(true);
        XmbUtils.shareWindow.setFocusable(true);
        // Removes default background.
        XmbUtils.shareWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        XmbUtils.shareWindow.setAnimationStyle(R.style.anim_pop_bottom);
        XmbUtils.shareWindow.showAtLocation(parentView,
                Gravity.BOTTOM, 0, 0);
        ShareSDK.initSDK(activity);
        contentView.findViewById(R.id.share_weixin_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(params.get("title"));
                sp.setText(params.get("desc").substring(0, 50));
                sp.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp.setUrl(params.get("url"));
                Platform qzone = ShareSDK.getPlatform(Wechat.NAME);
                qzone.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                    }
                }); // 设置分享事件回调
                qzone.share(sp);
            }
        });

        contentView.findViewById(R.id.share_weixin_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitle(params.get("title"));
                sp.setText(params.get("desc").substring(0, 50));
                sp.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp.setUrl(params.get("url"));
                Platform qzone = ShareSDK.getPlatform(WechatMoments.NAME);
                qzone.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                    }
                }); // 设置分享事件回调
                qzone.share(sp);
            }
        });

        contentView.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setTitle(params.get("title"));
                sp.setText(params.get("desc").substring(0, 50));
                sp.setTitleUrl(params.get("url"));
                sp.setImageUrl("http://www.xiaomabao.com/static1/images/app_icon.png");
                sp.setSite("小麻包");
                sp.setSiteUrl("http://www.xiaomabao.com");
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                    }
                }); // 设置分享事件回调
                qq.share(sp);
            }
        });

        contentView.findViewById(R.id.share_qqkj).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setText(params.get("title"));
                sp.setShareType(Platform.SHARE_WEBPAGE);
                sp.setTitleUrl(params.get("url"));
                sp.setUrl(params.get("url"));
                Platform weibo = ShareSDK.getPlatform(QZone.NAME);
                weibo.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                weibo.share(sp);
            }
        });
        final ImageView imageView = (ImageView) contentView.findViewById(R.id.collect_img);
        if (is_collect == 1) {
            imageView.setImageResource(R.mipmap.yshouc);
        } else {
            imageView.setImageResource(R.mipmap.shouc);
        }
        contentView.findViewById(R.id.collect_article).setOnClickListener((View v) -> {
            if (!AppContext.instance().isLogin()) {
                activity.startActivity(new Intent(activity, LoginActivity.class));
                return;
            }
            activity.collectArticle();
            if (activity.is_collect == 1) {
                imageView.setImageResource(R.mipmap.shouc);
            } else {
                imageView.setImageResource(R.mipmap.yshouc);
            }
        });

        contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmbUtils.shareWindow.dismiss();
            }
        });

    }

    public static Boolean check_is_join(Context context, int circle_id) {
        String shared = SharedPreferencesUtil.get(context, Const.MY_JOIN_CIRCLE_KEY, "").toString();
        try {
            JSONArray jsonArray = new JSONArray(shared);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (circle_id == jsonArray.getInt(i)) {
                    return true;
                }
            }
            return false;
        } catch (JSONException e) {
            return false;
        }
    }

}
