package com.witmoon.xmb.ui.dialog;

public interface DialogControl {

	void hideWaitDialog();

	WaitingDialog showWaitDialog();

	WaitingDialog showWaitDialog(int resid);

	WaitingDialog showWaitDialog(String text);
}
