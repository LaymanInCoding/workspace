package com.witmoon.xmb.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.MabaoCardApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class MabaoCardAddActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText1,editText2,editText3,editText4;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_mb_card_add;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.master_shopping_cart);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title_text);
        if (toolbarTitle != null) toolbarTitle.setText("绑定新卡");
        bindEvent();
    }

    private void bindEvent(){
        editText1 = (EditText) findViewById(R.id.edit1);
        editText2 = (EditText) findViewById(R.id.edit2);
        editText3 = (EditText) findViewById(R.id.edit3);
        editText4 = (EditText) findViewById(R.id.edit4);
        editText1.addTextChangedListener(new EditChangedListener(1));
        editText2.addTextChangedListener(new EditChangedListener(2));
        editText3.addTextChangedListener(new EditChangedListener(3));
        editText4.addTextChangedListener(new EditChangedListener(4));

        final Button mb_card_bind = (Button) findViewById(R.id.mb_card_bind);
        if (mb_card_bind != null) {
            mb_card_bind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mb_card_bind.setClickable(false);
                    String card_pass = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString();
                    if (card_pass.length() != 16) {
                        XmbUtils.showMessage(MabaoCardAddActivity.this, "密码长度不正确，请检查！");
                        mb_card_bind.setClickable(true);
                        return;
                    }

                    MabaoCardApi.bind_card(card_pass, new Listener<JSONObject>() {
                        @Override
                        public void onError(NetroidError error) {
                            mb_card_bind.setClickable(true);
                            XmbUtils.showMessage(MabaoCardAddActivity.this,"绑定麻包卡异常，请重试！");
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                if (response.getString("status").equals("1")) {
                                    Intent intent = new Intent(Const.BIND_MABAO_CARD_SUCCESS);
                                    intent.putExtra("ARGS",response.toString());
                                    sendBroadcast(intent);
                                    finish();
                                }else{
                                    mb_card_bind.setClickable(true);
                                    XmbUtils.showMessage(MabaoCardAddActivity.this,response.getString("info"));
                                }
                            } catch (JSONException e) {
                                mb_card_bind.setClickable(true);
                                XmbUtils.showMessage(MabaoCardAddActivity.this,"绑定麻包卡异常，请重试！");
                            }
                        }
                    });
                }
            });
        }
    }

    class EditChangedListener implements TextWatcher {

        private int index;

        public EditChangedListener(int index){
            this.index = index;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() == 4){
                if(index == 1){
                    editText1.clearFocus();
                    editText2.requestFocus();
                }else if(index == 2){
                    editText2.clearFocus();
                    editText3.requestFocus();
                }else if(index == 3){
                    editText3.clearFocus();
                    editText4.requestFocus();
                }else if(index == 4){
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
