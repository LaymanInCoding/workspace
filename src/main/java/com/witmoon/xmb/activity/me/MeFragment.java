package com.witmoon.xmb.activity.me;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.ServiceOrderActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;

public class MeFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private TextView login_text_view;
    private ImageView me_avatar_img;
    private View me_setting;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configToolbar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
       if (view == null) {
           view = inflater.inflate(R.layout.fragment_usercenter, container, false);
           AssetManager mgr = getActivity().getAssets();//得到AssetManager
           Typeface tf=Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
           TextView titleView = (TextView) view.findViewById(R.id.toolbar_title_text);
           titleView.setTypeface(tf);
           bindEvent();
        }
        return view;
    }

    private void bindEvent(){
        login_text_view = (TextView) view.findViewById(R.id.login_text_view);
        me_avatar_img = (ImageView) view.findViewById(R.id.me_avatar_img);

        login_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppContext.instance().isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        me_setting = view.findViewById(R.id.me_setting);
        me_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SETTING);
                }
            }
        });

        View my_order_view = view.findViewById(R.id.my_order);
        my_order_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    Bundle args = new Bundle();
                    args.putSerializable("initType", null);
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, args);
                }
            }
        });

        View my_service_view = view.findViewById(R.id.my_service);
        my_service_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    startActivity(new Intent(getActivity(), ServiceOrderActivity.class));
                }

            }
        });

        View my_shopping_cart_view = view.findViewById(R.id.my_shopping_cart);
        my_shopping_cart_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    UIHelper.showSimpleBackForResult(getActivity(), 3, SimpleBackPage.SHOPPING_CART);
                }
            }
        });

        View my_collect_view = view.findViewById(R.id.my_collect);
        my_collect_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FAVORITE);
                }
            }
        });

        View me_history_view = view.findViewById(R.id.me_history);
        me_history_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BROWSE_HISTORY);
                }
            }
        });

        View hot_line_view = view.findViewById(R.id.hot_line);
        hot_line_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setMessage("确定要拨打热线电话吗?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:400-0056-830"));
                                startActivity(intent);
                            }
                        }).setCancelable(true).show();
            }
        });

        View me_address_view = view.findViewById(R.id.me_address);
        me_address_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    AddressManageActivity.startActivity(getActivity());
                }
            }
        });

        View after_sale_view = view.findViewById(R.id.after_sales);
        after_sale_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.AFTER_SALE_SERVICE);
            }
        });

        View contact_us_view = view.findViewById(R.id.contact_us);
        contact_us_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ONLINE_SERVICE);
            }
        });

        View me_mb_help_view = view.findViewById(R.id.me_mb_help);
        me_mb_help_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.HELP);
            }
        });

        View order_sel_after_view = view.findViewById(R.id.order_sel_after);
        order_sel_after_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.OUT_PRICE);
                }
            }
        });

        View me_item_cash_coupon_view = view.findViewById(R.id.me_item_cash_coupon);
        me_item_cash_coupon_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else{
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.CASH_COUPON);
                }
            }
        });

    }

    private void configToolbar() {
        Toolbar mToolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), mToolbar);
        aQuery.id(R.id.top_toolbar).gone();
    }

    @Override
    public void onResume() {
        super.onResume();
        configToolbar();
        MainActivity.current_tab_index = 4;
        if(AppContext.instance().isLogin()){
            login_text_view.setText(AppContext.getLoginInfo().getName());
            if (!AppContext.getLoginInfo().getAvatar().equals("")){
                ImageLoader.getInstance().displayImage(AppContext.getLoginInfo().getAvatar(), me_avatar_img, AppContext.options_disk);
            }else{
                me_avatar_img.setImageResource(R.mipmap.touxiang);
            }
        }else{
            login_text_view.setText("登录 / 注册");
            me_avatar_img.setImageResource(R.mipmap.touxiang);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}