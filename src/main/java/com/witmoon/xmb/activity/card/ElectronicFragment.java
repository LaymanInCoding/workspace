package com.witmoon.xmb.activity.card;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.MabaoCardApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.ElecCard;
import com.witmoon.xmb.ui.FlowTagLayout;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ming on 2017/3/20.
 * 电子卡
 */
public class ElectronicFragment extends BaseFragment implements CardAdapter.OnItemClickListener {
    private View view;
    private int page = 1;
    private int custom_cardId;
    private int all_card_num;
    private int all_card_money;
    private String max_custom_money;
    private ElectronicCardAdapter mAdapter; //FlowTagLayout Adapter;
    private CardAdapter mCardAdapter;
    private FlowTagLayout mFlowTagLayout;
    private ImageView noCardContainer;
    private RecyclerView mCardRecyclerView;
    private TextView addText;
    private TextView all_moneyText;
    private TextView card_numText;
    private TextView card_amountText;
    private Button commitBtn;
    private EditText custom_edittext;

    private ArrayList<ElecCard> mDataList = new ArrayList<>();//FlowAdapter DataList
    private ArrayList<ElecCard> mCardList = new ArrayList<>();
    private Listener<JSONObject> mListener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            try {
                max_custom_money = response.getJSONObject("cards_custom").getString("card_max_money");
                custom_edittext.setHint("请输入自定义面额(1-" + max_custom_money + ")");
                custom_cardId = response.getJSONObject("cards_custom").getInt("card_id");
                JSONArray cardArray = response.getJSONArray("cards_list");
                for (int i = 0; i < cardArray.length(); i++) {
                    ElecCard cardObj = ElecCard.parse(cardArray.getJSONObject(i));
                    mDataList.add(cardObj);
                }
                mAdapter.notifyDataSetChanged();
                for (int i = 0; i < MainActivity.selec_index.size(); i++) {
                    ElecCard card = mDataList.get(MainActivity.selec_index.get(i));
                    card.setDelete_index(MainActivity.selec_index.get(i));
                    mCardList.add(card);
                    noCardContainer.setVisibility(View.GONE);
                }
                mCardAdapter.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
                calculate();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void hideSoft() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(custom_edittext.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_electronic_card, container, false);
            commitBtn = (Button) view.findViewById(R.id.pay_btn);
            commitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppContext.instance().isLogin()) {
                        if (mCardList.size() == 0) {
                            XmbUtils.showMessage(getContext(), "请先选择电子卡");
                            return;
                        }
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < mCardList.size(); i++) {
                            ElecCard card = mCardList.get(i);
                            //拼接成  12|500|1  格式
                            String tmp = card.getCard_id() + "|" + card.getCard_money() + "|" + card.getCard_num();
                            list.add(tmp);
                        }
                        Logger.d(list);
                        Intent intent = new Intent(getContext(), CardOrderConfirmActivity.class);
                        intent.putStringArrayListExtra("list", list);
                        startActivity(intent);
                    }else{
                        startActivity(new Intent(getContext(), LoginActivity.class));
                    }
                }
            });
            mFlowTagLayout = (FlowTagLayout) view.findViewById(R.id.flow_layout);
            mAdapter = new ElectronicCardAdapter(mDataList);
            mFlowTagLayout.setAdapter(mAdapter);
            mFlowTagLayout.setOnTagClickListener(((parent, view, position) -> {
                ElecCard card = mDataList.get(position);
                if (card.getDelete_index() != position) {
                    mCardList.add(card);
                    card.setDelete_index(position);
                    MainActivity.selec_index.add(position);
                }
                mCardAdapter.notifyDataSetChanged();
                noCardContainer.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
                calculate();
            }));
            mFlowTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
            all_moneyText = (TextView) view.findViewById(R.id.all_money);
            card_amountText = (TextView) view.findViewById(R.id.card_amount);
            card_numText = (TextView) view.findViewById(R.id.card_num);
            mCardAdapter = new CardAdapter(getContext(), mCardList);
            noCardContainer = (ImageView) view.findViewById(R.id.no_card_container);
            mCardRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mCardRecyclerView.setLayoutManager(manager);
            mCardRecyclerView.setAdapter(mCardAdapter);
            mCardAdapter.setOnItemClickListener(this);
            custom_edittext = (EditText) view.findViewById(R.id.custom_edittext);
            addText = (TextView) view.findViewById(R.id.add_textview);
            addText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String custom_money = custom_edittext.getText().toString().trim();
                    if (!custom_money.equals("")) {
                        if (Integer.valueOf(custom_money) <= Double.valueOf(max_custom_money) &&
                                !custom_money.equals("0")) {
                            noCardContainer.setVisibility(View.GONE);
                            for (int i = 0; i < mCardList.size(); i++) {
                                if (custom_edittext.getText().toString().trim().equals(mCardList.get(i).getCard_money())
                                        && mCardList.get(i).getCard_id() == custom_cardId) {
                                    mCardList.get(i).addCard_num();
                                    mCardAdapter.notifyDataSetChanged();
                                    custom_edittext.setText("");
                                    calculate();
                                    hideSoft();
                                    return;
                                }
                            }
                            ElecCard card = new ElecCard();
                            card.setCard_money(custom_edittext.getText().toString().trim());
                            card.setCard_id(custom_cardId);
                            card.setDelete_index(-1);
                            mCardList.add(card);
                            mCardAdapter.notifyDataSetChanged();
                            calculate();
                            custom_edittext.setText("");
                            hideSoft();
                        } else {
                            XmbUtils.showMessage(getContext(), "请输入(1-" + Double.valueOf
                                    (max_custom_money) + ")面额");
                        }
                    }
                }
            });
            setRecRequest(1);
        }
        return view;
    }

    @Override
    public void setRecRequest(int currentPage) {
        MabaoCardApi.get_electronic_card(currentPage, mListener);
    }

    @Override
    public void onShoppingCartChange(int position, int number) {
        ElecCard elecCard = mCardList.get(position);
        elecCard.setCard_num(number);
        calculate();
    }

    @Override
    public void onItemDelete(int position) {
        int index = mCardList.get(position).getDelete_index();
        int id = mCardList.get(position).getCard_id();
        if (index != -1) {
            mDataList.get(index).setDelete_index(-1);
            mAdapter.notifyDataSetChanged();
        }
        if (id != custom_cardId) {
            for (int i = 0; i < MainActivity.selec_index.size(); i++)
                if (MainActivity.selec_index.get(i) == index) {
                    MainActivity.selec_index.remove(i);
                }
        }
        mCardList.get(position).setCard_num(1);
        mCardList.remove(position);
        if (mCardList.size() == 0) {
            noCardContainer.setVisibility(View.VISIBLE);
            all_card_num = 0;
            all_card_money = 0;
        }
        mCardAdapter.notifyDataSetChanged();
        mCardAdapter.notifyItemRemoved(position);
        calculate();
    }

    private void calculate() {
        all_card_num = 0;
        all_card_money = 0;
        for (int i = 0; i < mCardList.size(); i++) {
            all_card_num += mCardList.get(i).getCard_num();
            int tmp;
            tmp = Integer.valueOf(mCardList.get(i).getCard_money());
            all_card_money += tmp * mCardList.get(i).getCard_num();
        }
        card_numText.setText(all_card_num + "");
        card_amountText.setText(all_card_money + "");
        all_moneyText.setText("¥" + all_card_money);
    }
}
