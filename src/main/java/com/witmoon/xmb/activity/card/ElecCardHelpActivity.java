package com.witmoon.xmb.activity.card;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.util.SystemBarTintManager;

/**
 * Created by ming on 2017/3/23.
 */
public class ElecCardHelpActivity extends AppCompatActivity {
    private TextView title;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_help);
        title = (TextView) findViewById(R.id.toolbar_title_text);
        title.setText("帮助中心");
        back = (ImageView) findViewById(R.id.toolbar_left_img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setFont();
        setTitleColor_(R.color.main_kin);
    }

    private void setFont() {
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        title.setTypeface(tf);
    }

    public void setTitleColor_(int mColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            if (mColor == R.color.black) {
                tintManager.setStatusBarTintColor(getResources().getColor(R.color.black));
            } else {
                tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.bg_status));
            }
        }
    }

    //获取高度
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
