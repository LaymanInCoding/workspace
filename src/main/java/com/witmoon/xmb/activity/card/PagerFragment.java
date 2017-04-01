package com.witmoon.xmb.activity.card;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.witmoon.xmb.R;

/**
 * Created by ming on 2017/3/28.
 */
public class PagerFragment extends Fragment {
    private int img_resource;

    public static PagerFragment createInstance(int img_resource) {
        PagerFragment fragment = new PagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("img", img_resource);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img_resource = getArguments().getInt("img");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_help_fragment, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        img.setImageResource(img_resource);
        return view;
    }
}
