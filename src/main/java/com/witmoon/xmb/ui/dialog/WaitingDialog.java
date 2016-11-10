package com.witmoon.xmb.ui.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmblibrary.percent.PercentRelativeLayout;

public class WaitingDialog extends Dialog {

	private TextView _messageTv,discoloration;
	private ProgressBar waiting_pb;
	private LinearLayout information_prompt;
	private ImageView hidden_img;
	private Context mContext;

	public WaitingDialog(Context context) {
		super(context);
		mContext = context;
		init(context);
	}

	public WaitingDialog(Context context, int defStyle) {
		super(context, defStyle);
		init(context);
	}

	protected WaitingDialog(Context context, boolean cancelable, OnCancelListener listener) {
		super(context, cancelable, listener);
		init(context);
	}

	public static boolean dismiss(WaitingDialog dialog) {
		if (dialog != null) {
			dialog.dismiss();
			return false;
		} else {
			return true;
		}
	}

	public static void hide(Context context) {
		if (context instanceof DialogControl)
			((DialogControl) context).hideWaitDialog();
	}

	public static boolean hide(WaitingDialog dialog) {
		if (dialog != null) {
			dialog.hide();
			return false;
		} else {
			return true;
		}
	}

	private void init(Context context) {
		setCancelable(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(R.layout.v2_dialog_waiting, null);
		_messageTv = (TextView) view.findViewById(R.id.waiting_tv);
		waiting_pb = (ProgressBar) view.findViewById(R.id.waiting_pb);
		information_prompt = (LinearLayout) view.findViewById(R.id.information_prompt);
		hidden_img = (ImageView) view.findViewById(R.id.hidden_img);
		discoloration = (TextView) view.findViewById(R.id.discoloration);
		setContentView(view);
	}

	public static void show(Context context) {
		if (context instanceof DialogControl)
			((DialogControl) context).showWaitDialog();
	}

	public static boolean show(WaitingDialog waitdialog) {
		boolean flag;
		if (waitdialog != null) {
			waitdialog.show();
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (TDevice.isTablet()) {
			int i = (int) TDevice.dpToPixel(360F);
			if (i < TDevice.getScreenWidth()) {
				WindowManager.LayoutParams params = getWindow()
						.getAttributes();
				params.width = i;
				getWindow().setAttributes(params);
			}
		}
	}

	public void setMessage(int message) {
		_messageTv.setText(message);
	}

	public void setMessage(String message) {
		_messageTv.setText(message);
	}
	//显示错误提示
	public void showError_information()
	{
		_messageTv.setVisibility(View.GONE);
		waiting_pb.setVisibility(View.GONE);
		show();
	}
	//显示信息提示
	public void showInformation_prompt()
	{
		_messageTv.setVisibility(View.GONE);
		waiting_pb.setVisibility(View.GONE);
		information_prompt.setVisibility(View.VISIBLE);
	}
	public ImageView mImageView()
	{
		return hidden_img;
	}


}
