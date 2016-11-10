package com.witmoon.xmb.activity.shoppingcart.fragment;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseFragment;

public class MabaoCardFragment extends BaseFragment {
    private View rootView;
    private View no_mabao_card_view;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mb_card, container, false);
        }
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        setFont();
        return rootView;
    }

    private void setFont(){
        AssetManager mgr = getActivity().getAssets();
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/font.otf");
        TextView mb_card_sorry_title = (TextView) rootView.findViewById(R.id.mb_card_sorry_title);
        TextView mb_card_sorry_desc = (TextView) rootView.findViewById(R.id.mb_card_sorry_desc);

        mb_card_sorry_title.setTypeface(tf);
        mb_card_sorry_desc.setTypeface(tf);
    }

}
