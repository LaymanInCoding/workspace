package com.witmoon.xmb.ui.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by de on 2015/12/2.
 */
public class PopupUtils {

    private static PopupDialog popupDialog= null;
    public static PopupDialog createPopupDialog(Context context,Popup dialog) {
        dismissPopupDialog();
        View view =null;
        if(null == dialog.getCustomView()){
            LayoutInflater inflater = LayoutInflater.from(context);
            view= inflater.inflate(dialog.getContentView(),null);
        }else{
            view= dialog.getCustomView();
        }
        view.setOnTouchListener(dialog.getTouchListener());
        if(0!= dialog.getBgAlpha()){
            view.setAlpha(dialog.getBgAlpha());
        }
        popupDialog= new PopupDialog(view,dialog.getvWidth(),dialog.getvHeight());
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT); //follow two lines is used for back key -00000
        popupDialog.setBackgroundDrawable(dw);
        popupDialog.setAnimationStyle(dialog.getAnimFadeInOut());
        popupDialog.setOutsideTouchable(dialog.isClickable());
        popupDialog.setFocusable(true); //not allow user click popupwindowbackground event or not permit
        popupDialog.setOnDismissListener((PopupWindow.OnDismissListener) dialog.getListener());
        popupDialog.update();
        return popupDialog;
    }
    public static void dismissPopupDialog() {
        if(popupDialog!=null&&
                popupDialog.isShowing()){
            popupDialog.dismiss();
            popupDialog= null;
        }
    }
    public static boolean isPopupShowing() {
        if(popupDialog!=null&&
                popupDialog.isShowing()){
            return true;
        }else{
            return false;
        }
    }
}
