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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.activity.service.ServiceOrderActivity;
import com.witmoon.xmb.activity.shoppingcart.MabaoCardActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

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
            Log.d("createView", "true");
            view = inflater.inflate(R.layout.fragment_usercenter, container, false);
            AssetManager mgr = getActivity().getAssets();//得到AssetManager
            Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
            TextView titleView = (TextView) view.findViewById(R.id.toolbar_title_text);
            titleView.setTypeface(tf);
            bindEvent();
        }
        ButterKnife.bind(this, view);

        return view;
    }

    private void requestData() {
        login_text_view.setText(AppContext.getLoginInfo().getName());
        UserApi.userInfo(new Listener<JSONObject>() {
            @Override
            public void onPreExecute() {
                super.onPreExecute();
//                login_text_view.setText(AppContext.getLoginInfo().getName());
                if (!AppContext.getLoginInfo().getAvatar().equals("")) {
                    ImageLoader.getInstance().displayImage(AppContext.getLoginInfo().getAvatar(), me_avatar_img, AppContext.options_disk);
                } else {
                    me_avatar_img.setImageResource(R.mipmap.touxiang);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                Logger.json(response.toString());
                try {
                    JSONObject data = response.getJSONObject("data");
                    Log.e("Data", data.toString());
                    if (!data.getString("header_img").equals("")) {
                        ImageLoader.getInstance().displayImage(data.getString("header_img"), me_avatar_img, AppContext.options_disk);
                    } else {
                        me_avatar_img.setImageResource(R.mipmap.touxiang);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindEvent() {

        login_text_view = (TextView) view.findViewById(R.id.login_text_view);
        login_text_view.setText(AppContext.getLoginInfo().getName());

        me_avatar_img = (ImageView) view.findViewById(R.id.me_avatar_img);

        login_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        me_setting = view.findViewById(R.id.me_setting);
        me_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter0");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SETTING);
                }
            }
        });

        View my_medical_report = view.findViewById(R.id.my_medical_report);
        my_medical_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.MedicalReport);
                }

            }
        });

        View my_order_view = view.findViewById(R.id.my_order);
        my_order_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter1");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
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
                UmengStatic.registStat(getActivity(), "PersonalCenter2");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), ServiceOrderActivity.class));
                }

            }
        });

        View my_shopping_cart_view = view.findViewById(R.id.my_shopping_cart);
        my_shopping_cart_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter3");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBackForResult(getActivity(), 3, SimpleBackPage.SHOPPING_CART);
                }
            }
        });

        View my_mbcard_view = view.findViewById(R.id.my_mbcard);
        my_mbcard_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Intent intent = new Intent(getActivity(), MabaoCardActivity.class);
                    intent.putExtra("from","userCenter");
                    startActivity(intent);
                }
            }
        });

        View my_collect_view = view.findViewById(R.id.my_collect);
        my_collect_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter4");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FAVORITE);
                }
            }
        });

        View me_history_view = view.findViewById(R.id.me_history);
        me_history_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter10");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BROWSE_HISTORY);
                }
            }
        });

        View hot_line_view = view.findViewById(R.id.hot_line);
        hot_line_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter8");

                new AlertDialog.Builder(getActivity()).setMessage("确定要拨打热线电话吗?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-8517-0751"));
                                startActivity(intent);
                            }
                        }).setCancelable(true).show();
            }
        });

        View me_address_view = view.findViewById(R.id.me_address);
        me_address_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter9");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    AddressManageActivity.startActivity(getActivity());
                }
            }
        });
        View my_invite_view = view.findViewById(R.id.me_invite);
        my_invite_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.InviteFriend);
                }
            }
        });


        View after_sale_view = view.findViewById(R.id.after_sales);
        after_sale_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter12");

                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.AFTER_SALE_SERVICE);
            }
        });

        View contact_us_view = view.findViewById(R.id.contact_us);
        contact_us_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter11");

                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ONLINE_SERVICE);
            }
        });

        View me_mb_help_view = view.findViewById(R.id.me_mb_help);
        me_mb_help_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter13");

                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.HELP);
            }
        });

        View order_sel_after_view = view.findViewById(R.id.order_sel_after);
        order_sel_after_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter5");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.OUT_PRICE);
                }
            }
        });

        View my_elec_order_view = view.findViewById(R.id.my_elec_order);
        my_elec_order_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ElecOrder);
                }
            }
        });

        View me_item_cash_coupon_view = view.findViewById(R.id.me_item_cash_coupon);
        me_item_cash_coupon_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatic.registStat(getActivity(), "PersonalCenter6");

                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
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
        if (AppContext.instance().isLogin()) {
            requestData();
        } else {
            login_text_view.setText("登录 / 注册");
            me_avatar_img.setImageResource(R.mipmap.touxiang);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }


    @OnClick(R.id.me_mabao_bean)
    public void onClick() {
        UmengStatic.registStat(getActivity(), "PersonalCenter7");

        if (!AppContext.instance().isLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.MyMabaoBean);
        }
    }

}