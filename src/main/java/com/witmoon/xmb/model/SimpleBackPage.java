package com.witmoon.xmb.model;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.babycenter.BabyRecordFragment;
import com.witmoon.xmb.activity.babycenter.BabySettingFragment;
import com.witmoon.xmb.activity.babycenter.CalOverdueFragment;
import com.witmoon.xmb.activity.babycenter.ChildStatusFragment;
import com.witmoon.xmb.activity.babycenter.MoreMessageFragment;
import com.witmoon.xmb.activity.babycenter.OverdueSettingFragment;
import com.witmoon.xmb.activity.friendship.fragment.CommentFragment;
import com.witmoon.xmb.activity.goods.fragment.GoogsCommentFragment;
import com.witmoon.xmb.activity.goods.fragment.Group_Buying_Fragment;
import com.witmoon.xmb.activity.goods.fragment.PublishEvaluationFragment;
import com.witmoon.xmb.activity.mabao.SubclassFragment;
import com.witmoon.xmb.activity.mabao.Subclass_bomFragment;
import com.witmoon.xmb.activity.main.fragment.ShoppingCartFragmentV2;
import com.witmoon.xmb.activity.me.fragment.AboutFragment;
import com.witmoon.xmb.activity.me.fragment.AfterSaleServiceFragment;
import com.witmoon.xmb.activity.me.fragment.BeanHelpFragment;
import com.witmoon.xmb.activity.me.fragment.BeanSendFragment;
import com.witmoon.xmb.activity.me.fragment.BeanToCashFragment;
import com.witmoon.xmb.activity.me.fragment.BeanUseFragment;
import com.witmoon.xmb.activity.me.fragment.BindBankCardFragment;
import com.witmoon.xmb.activity.me.fragment.BrowseHistoryFragment;
import com.witmoon.xmb.activity.me.fragment.CashCouponFragment;
import com.witmoon.xmb.activity.me.fragment.CertificationFragment;
import com.witmoon.xmb.activity.me.fragment.ChangePasswordFragment;
import com.witmoon.xmb.activity.me.fragment.CheckProgressFragment;
import com.witmoon.xmb.activity.me.fragment.EvaluateFragment;
import com.witmoon.xmb.activity.me.fragment.HelpFragment;
import com.witmoon.xmb.activity.me.fragment.LogisticsFragment;
import com.witmoon.xmb.activity.me.fragment.MyFavoriteGoodsFragment;
import com.witmoon.xmb.activity.me.fragment.MyMabaoBeanFragment;
import com.witmoon.xmb.activity.me.fragment.MyOrderFragment;
import com.witmoon.xmb.activity.me.fragment.NewAddressFragment;
import com.witmoon.xmb.activity.me.fragment.OnlineServiceFragment;
import com.witmoon.xmb.activity.me.fragment.OrderDetailFragment;
import com.witmoon.xmb.activity.me.fragment.PersonalDataFragment;
import com.witmoon.xmb.activity.me.fragment.ReturnofthegoodsFragment;
import com.witmoon.xmb.activity.me.fragment.ServiceProvisionFragment;
import com.witmoon.xmb.activity.me.fragment.SettingFragment;
import com.witmoon.xmb.activity.me.fragment.WebUtilFragment;
import com.witmoon.xmb.activity.me.fragment.WebUtilFragments;
import com.witmoon.xmb.activity.service.SubServiceFragment;
import com.witmoon.xmb.activity.service.Sub_SubServiceFragment;
import com.witmoon.xmb.activity.shopping.FeatureBrandFragment;
import com.witmoon.xmb.activity.shoppingcart.fragment.AddressSelectorFragment;
import com.witmoon.xmb.activity.shoppingcart.fragment.MabaoCardFragment;
import com.witmoon.xmb.activity.specialoffer.fragment.Add_babyFragment;
import com.witmoon.xmb.activity.specialoffer.fragment.UpRecordFragment;
import com.witmoon.xmb.activity.user.fragment.BindEmailFragment;
import com.witmoon.xmb.activity.user.fragment.LoginFragment;
import com.witmoon.xmb.activity.user.fragment.RegisterSuccessFragment;
import com.witmoon.xmb.activity.user.fragment.RetrievePasswordFragment;
import com.witmoon.xmb.activity.user.fragment.RetrieveTypeFragment;
import com.witmoon.xmb.activity.user.fragment.TelephoneRegisterFragment;
import com.witmoon.xmb.activity.user.fragment.WriteCheckCodeFragment;
import com.witmoon.xmb.activity.user.fragment.WritePasswordFragment;

/**
 * 定义简单页面的枚举
 * Created by zhyh on 2015/6/19.
 */
public enum SimpleBackPage {

    LOGIN(0, R.string.text_login, LoginFragment.class),
    REGISTER(1, R.string.text_register, R.color.master_login, TelephoneRegisterFragment.class),
    RETRIEVE_PWD(2, R.string.text_retrieve_pwd, R.color.master_login, RetrievePasswordFragment
            .class),
    RETRIEVE_TYPE(3, R.string.text_security_check, R.color.master_login, RetrieveTypeFragment
            .class),
    WRITE_CHECK_CODE(4, R.string.text_write_check_code, R.color.master_login,
            WriteCheckCodeFragment.class),
    REGISTER_SUCCESS(5, R.string.text_register_success, R.color.master_login,
            RegisterSuccessFragment.class),
    WRITE_PASSWORD(6, R.string.text_set_pwd, R.color.master_login, WritePasswordFragment.class),
    BIND_EMAIL(7, R.string.text_bind_email, R.color.master_login, BindEmailFragment.class),
    ABOUT(8, R.string.text_mb_about, R.color.master_me, AboutFragment.class),
    FEEDBACK(9, R.string.text_evaluate, R.color.master_me, EvaluateFragment.class),
    CERTIFICATION(10, R.string.text_real_name_certification, R.color.master_me,
            CertificationFragment.class),
    SETTING(11, R.string.text_setting, R.color.master_me, SettingFragment.class),
    PERSONAL_DATA(12, R.string.text_personal_data, R.color.master_me, PersonalDataFragment.class),
    CHANGE_PWD(13, R.string.text_change_pwd, R.color.master_me, ChangePasswordFragment.class),
    NEW_ADDRESS(14, R.string.text_new_receiver_address, R.color.master_me, NewAddressFragment
            .class),
    BROWSE_HISTORY(15, R.string.text_browsing_history, R.color.master_me, BrowseHistoryFragment
            .class),
    CASH_COUPON(16, R.string.text_cash_coupon, R.color.master_me, CashCouponFragment.class),
    ORDER(17, R.string.text_my_order, R.color.master_me, MyOrderFragment.class),
    ORDER_DETAIL(19, R.string.text_order_detail, R.color.master_me, OrderDetailFragment.class),
    FAVORITE(18, R.string.text_my_favorite, R.color.master_me, MyFavoriteGoodsFragment.class),

    SHOPPING_CART(20, R.string.text_shopping_cart, R.color.main_kin,
            ShoppingCartFragmentV2.class),
    ADDRESS_SELECTOR(21, R.string.text_receiver_address, R.color.master_shopping_cart,
            AddressSelectorFragment.class),

    ARTICLE_COMMENT(22, R.string.text_comment, R.color.master_friendship_circle, CommentFragment
            .class),
    GOODS_EVALUATE(23, R.string.text_goods_evaluation, R.color.master_me, PublishEvaluationFragment
            .class),
    SERVICE_PROVISION(24, R.string.text_service_item, R.color.master_me, ServiceProvisionFragment
            .class),
    HELP(25, R.string.text_mb_help, R.color.master_me, HelpFragment.class),
    ONLINE_SERVICE(26, R.string.text_online_service, R.color.master_me, OnlineServiceFragment
            .class),
    AFTER_SALE_SERVICE(27, R.string.text_after_sales_service, R.color.master_me,
            AfterSaleServiceFragment.class),
    OUT_PRICE(28, R.string.text_wait_for_out_price, R.color.master_me,
            ReturnofthegoodsFragment.class),
    JINDU(30, R.string.text_wait_for_out_price2, R.color.master_me,
            CheckProgressFragment.class),
    GROUP_BUYING(31, R.string.text_group_buying, R.color.main_kin,
            Group_Buying_Fragment.class),
    LOGISTICS(29, R.string.text_logistics, R.color.master_me,
            LogisticsFragment.class),
    VACCINE_(33, R.string.text_vaccine_cj, R.color.master_me, WebUtilFragments.class),
    VACCINE(32, R.string.text_vaccine, R.color.master_me, WebUtilFragment.class),
    UPRECORD(34, R.string.text_uprecord, R.color.master_me, UpRecordFragment.class),
    ADDBABY(35, R.string.text_add_baby, R.color.master_me, Add_babyFragment.class),
    UPDATEBABY(36, R.string.text_edit_baby, R.color.master_me, Add_babyFragment.class),
    WEIGHT_HEIGHT(37, R.string.text_baby_weight_height, R.color.master_me, WebUtilFragments.class),
    BEDTIME_STORY(38, R.string.text_bedtime_story, R.color.master_me, WebUtilFragments.class),
    VACCINE_INDEX(42, R.string.text_vaccine_cj_, R.color.master_me, WebUtilFragments.class),
    SUBCLASS(45, R.string.app_name, R.color.master_me, SubclassFragment.class),
    SUBCLASS_BOM(46, R.string.app_name, R.color.master_me, Subclass_bomFragment.class),
    //    SUBCLASS_BOM(46, R.string.app_name, R.color.master_me, SubClass_bomFragment_New.class),
    ChildStatus(47, R.string.baby_sex, R.color.master_me, ChildStatusFragment.class),
    OverdueSetting(48, R.string.overdue_setting, R.color.master_me, OverdueSettingFragment.class),
    CalOverdue(49, R.string.overdue_setting, R.color.master_me, CalOverdueFragment.class),
    BabySetting(50, R.string.baby_setting, R.color.master_me, BabySettingFragment.class),
    BabyRecord(51, R.string.baby_record, R.color.master_me, BabyRecordFragment.class),
    MbCard(52, R.string.my_mb_card, R.color.master_me, MabaoCardFragment.class),
    FEATURE_BRAND(53, R.string.feature_brand, R.color.master_me, FeatureBrandFragment.class),
    BABYMOREMESSAGE(54, R.string.mengbao_introduce, R.color.master_me, MoreMessageFragment.class),

    BindBankCard(55, R.string.bind_bank_card, R.color.master_me, BindBankCardFragment.class),
    MyMabaoBean(56, R.string.my_mabao_bean, R.color.master_me, MyMabaoBeanFragment.class),
    BeanHelp(57, R.string.madou_use_help, R.color.master_me, BeanHelpFragment.class),
    BeanToCash(58, R.string.bean_to_cash, R.color.master_me, BeanToCashFragment.class),
    BeanUse(59, R.string.madou_use, R.color.master_me, BeanUseFragment.class),
    BeanSend(60, R.string.send_bean, R.color.master_me, BeanSendFragment.class),

    SubService(61, R.string.send_bean, R.color.master_me, SubServiceFragment.class),
    SubSubService(62, R.string.send_bean, R.color.master_me, Sub_SubServiceFragment.class),

    GoodsComment(63, R.string.text_mama_comment, R.color.master_me, GoogsCommentFragment.class);


    private int value;
    private int title;
    private int toolbarBgColor;
    private Class<?> clz;

    SimpleBackPage(int value, int title, Class<?> clz) {
        this(value, title, Integer.MIN_VALUE, clz);
    }

    SimpleBackPage(int value, int title, int color, Class<?> clz) {
        this.value = value;
        this.title = title;
        this.clz = clz;
        setClz(clz);
        this.toolbarBgColor = color;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getToolbarBgColor() {
        return toolbarBgColor;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static SimpleBackPage getPageByValue(int val) {
        for (SimpleBackPage p : values()) {
            if (p.getValue() == val)
                return p;
        }
        return null;
    }
}
