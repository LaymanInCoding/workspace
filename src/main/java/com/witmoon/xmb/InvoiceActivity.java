package com.witmoon.xmb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaExtractor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.XmbUtils;

import org.apache.cordova.LOG;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de on 2016/11/22.
 */
public class InvoiceActivity extends BaseActivity {

    private ImageView backImg;
    private TextView toolbarTitle, toolbarRightText;
    private Button typeInvoice, personalInvoice, companyInvoice, finishBtn;
    private EditText companyInfoEt;
    private String invoiceHead = "单位", invoiceContent;
    private RecyclerView contentRv;
    private InvoiceContentRVAdapter mContentRVAdapter;
    private ArrayList<String> mData = new ArrayList<>();
    private String[] stringContent = {"服装", "化妆品", "电子产品", "文具用品", "商品明细"};
    private Intent resultIntent, requestIntent;
    private String invoicePayeeOld, invoiceContentOld = "";
    int selectIndex = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        resultIntent = new Intent();
        requestIntent = getIntent();
        invoicePayeeOld = requestIntent.getStringExtra("invoice_payee");
        invoiceContentOld = requestIntent.getStringExtra("invoice_content");
        if (invoiceContentOld != null) {
            resultIntent.putExtra("inv_content", invoiceContentOld); //设置默认发票内容
        } else {
            resultIntent.putExtra("inv_content", "商品明细");//设置默认发票内容
        }
        resultIntent.putExtra("inv_payee", "个人");  //设置默认发票抬头
        setData();
        findViewAndSet();
        Log.e("ERROR", invoicePayeeOld + "  " + invoiceContentOld);
    }

    private void setData() {
        for (int i = 0; i < stringContent.length; i++) {
            if (stringContent[i].equals(invoiceContentOld)) {
                selectIndex = i;
            }
            mData.add(stringContent[i]);
        }
    }

    private void findViewAndSet() {
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title_text);
        toolbarTitle.setText("发票信息");
        personalInvoice = (Button) findViewById(R.id.personal_invoice);
        personalInvoice.setOnClickListener(this);
        companyInvoice = (Button) findViewById(R.id.company_invoice);
        companyInvoice.setOnClickListener(this);
        companyInfoEt = (EditText) findViewById(R.id.company_info_et);
        finishBtn = (Button) findViewById(R.id.next_step);
        finishBtn.setOnClickListener(this);
        toolbarRightText = (TextView) findViewById(R.id.some_message);
        toolbarRightText.setOnClickListener(this);
        backImg = (ImageView) findViewById(R.id.toolbar_left_img);
        backImg.setOnClickListener(v -> finish());
        typeInvoice = (Button) findViewById(R.id.paper_invoice);
        typeInvoice.setOnClickListener(this);
        resultIntent.putExtra("inv_type", typeInvoice.getText().toString()); //设置发票类型
        contentRv = (RecyclerView) findViewById(R.id.invoice_content_rv);
        mContentRVAdapter = new InvoiceContentRVAdapter(this, mData, selectIndex);
        for (int i = 0; i < mData.size(); i++) {
            if (invoiceContentOld == mData.get(i)) {
                mContentRVAdapter.setSelectedIndex(i);
                mContentRVAdapter.notifyDataSetChanged();
            }
        }
        mContentRVAdapter.setOnItemClickListener(position -> {
            invoiceContent = mData.get(position);
            resultIntent.removeExtra("inv_content");
            resultIntent.putExtra("inv_content", invoiceContent);
        });
        if (invoicePayeeOld != null) {
            if (!invoicePayeeOld.equals("个人")) {
                companyInfoEt.setText(invoicePayeeOld);
                invoiceHead = "单位";
                editTextSetFocus(true);
                companyInfoEt.requestFocus();
                if (invoicePayeeOld != null) {
                    if (invoicePayeeOld.length() != 0) {
                        companyInfoEt.setSelection(invoicePayeeOld.length());
                    }
                }
            } else {
                setBackgroundAndColor(personalInvoice, companyInvoice);
                invoiceHead = "个人";
                companyInfoEt.setText("");
                editTextSetFocus(false);
            }
        }

        CustomLinearLayoutManager manager = new CustomLinearLayoutManager(this);
        manager.setScrollEnabled(false);
        contentRv.setHasFixedSize(true);
        contentRv.setLayoutManager(manager);
        contentRv.setAdapter(mContentRVAdapter);
    }

    private void setBackgroundAndColor(Button check, Button uncheck) {
        check.setBackground(getResources().getDrawable(R.drawable.bg_invoice_text));
        check.setTextColor(Color.parseColor("#fb5151"));
        uncheck.setBackground(getResources().getDrawable(R.drawable.bg_invoice_text_uncheck));
        uncheck.setTextColor(Color.parseColor("#555555"));
    }

    private void editTextSetFocus(boolean flag) {
        companyInfoEt.setFocusableInTouchMode(flag);
        companyInfoEt.setFocusable(flag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paper_invoice:
                typeInvoice.setBackground(getResources().getDrawable(R.drawable.bg_invoice_text));
                typeInvoice.setTextColor(Color.parseColor("#fb5151"));
                break;
            case R.id.personal_invoice:
                setBackgroundAndColor(personalInvoice, companyInvoice);
                invoiceHead = "个人";
                editTextSetFocus(false);
                break;
            case R.id.company_invoice:
                setBackgroundAndColor(companyInvoice, personalInvoice);
                invoiceHead = "单位";
                editTextSetFocus(true);
                companyInfoEt.requestFocus();
                break;
            case R.id.some_message:
                XmbUtils.showInvoiceInfo(this);
                break;
            case R.id.next_step:
                if (invoiceHead.equals("单位")) {
                    if (companyInfoEt.getText().toString().trim().equals("")) {
                        XmbUtils.showMessage(this, "单位公司名称不能为空");
                    } else {
                        resultIntent.removeExtra("inv_payee");
                        resultIntent.putExtra("inv_payee", companyInfoEt.getText().toString());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        break;
                    }
                } else {
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    break;
                }
        }
    }

    class CustomLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomLinearLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
