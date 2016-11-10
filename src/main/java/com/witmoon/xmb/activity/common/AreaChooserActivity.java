package com.witmoon.xmb.activity.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.Region;
import com.witmoon.xmblibrary.efficientadapter.AbsViewHolder;
import com.witmoon.xmblibrary.efficientadapter.SimpleAdapter;
import com.witmoon.xmblibrary.recyclerview.ItemClickSupport;
import com.witmoon.xmblibrary.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地区选择界面
 * Created by zhyh on 2015/1/24.
 */
public class AreaChooserActivity extends BaseActivity implements ItemClickSupport
        .OnItemClickListener {

    private int type = 0;

    private StringBuffer mRegionIdSB = new StringBuffer();
    private StringBuilder mAddressSB = new StringBuilder();

        private SimpleAdapter mAdapter;
        private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

        private AppContext applicationContext;

        @Override
        protected int getLayoutResourceId() {
            return R.layout.recycler_view_with_toolbar;
        }

        @Override
        protected String getActionBarTitle() {
            return "地区选择";
        }

        @Override
        protected void configActionBar(Toolbar toolbar) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.master_me));
            setTitleColor_(R.color.master_me);
        }

        @Override
        protected void initialize(Bundle savedInstanceState) {
        SuperRecyclerView recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        applicationContext = (AppContext) getApplicationContext();
        mAdapter = new SimpleAdapter<>(android.R.layout.simple_list_item_1, AreaHolder.class,
                dataList);
        recyclerView.setAdapter(mAdapter);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView.getRecyclerView());
        itemClickSupport.setOnItemClickListener(this);

        initRegionData("1");
    }

    /**
     * 初始化行政区划数据
     * @param pid 父行政区划ID
     */
    private void initRegionData(String pid) {
        dataList.clear();
        List<Region> regionList = applicationContext.getXmbDB().loadRegions(pid);
        for (Region region : regionList) {
            Map<String, String> map = new HashMap<>();
            map.put("name", region.getName());
            map.put("id", region.getId());
            dataList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        Map<String, String> tm = dataList.get(position);
        String code = tm.get("id");
        mAddressSB.append(tm.get("name"));
        if (type == 0) {
            type = 1;
            mRegionIdSB.append(code).append(",");
            initRegionData(code);
        } else if (type == 1) {
            type = 2;
            mRegionIdSB.append(code).append(",");
            initRegionData(code);
        } else {
            Intent intent = new Intent();
            intent.putExtra("address", mAddressSB.toString());
            intent.putExtra("regionId", mRegionIdSB.append(code).toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    static class AreaHolder extends AbsViewHolder<Map<String, String>> {

        public AreaHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, Map<String, String> object) {
            TextView textView = findViewByIdEfficient(android.R.id.text1);
            textView.setText(object.get("name"));
        }
    }
}
