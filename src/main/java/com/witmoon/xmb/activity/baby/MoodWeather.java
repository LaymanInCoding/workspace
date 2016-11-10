package com.witmoon.xmb.activity.baby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.ImageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoodWeather extends BaseActivity {
    private MyAdapter myadapter;
    private View backView,confirmView;
    private GridView gview;
    private TextView post_title,mood_desc;
    private String type = "mood";
    private int clickTemp = -1;
    private ArrayList<Map<String,String>> maps;
    private ArrayList<String> mood_pic = new ArrayList<>();
    private String mood_txt_arr[];
    private int mood_img_arr[];
    private int mood_active_img_arr[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babay_mood);
        initView();
    }

    private void initView(){
        type = getIntent().getStringExtra("type");

        backView = findViewById(R.id.post_back);
        confirmView = findViewById(R.id.post_confirm);
        post_title = (TextView)findViewById(R.id.post_title);
        mood_desc = (TextView)findViewById(R.id.mood_desc);
        gview = (GridView)findViewById(R.id.gview);
        if(type.equals("mood")){
            post_title.setText("心情");
            mood_desc.setText("今天心情如何？");
            mood_txt_arr = Const.mood_desc_text;
            mood_img_arr = Const.mood_img;
            mood_active_img_arr = Const.mood_active_img;
            myadapter = new MyAdapter(this);
            gview.setAdapter(myadapter);
        }else if (type.equals("weather")){
            post_title.setText("天气");
            mood_desc.setText("今天天气如何？");
            mood_txt_arr = Const.weather_desc_text;
            mood_img_arr = Const.weather_img;
            mood_active_img_arr = Const.weather_active_img;
            myadapter = new MyAdapter(this);
            gview.setAdapter(myadapter);
        }else{
            post_title.setText("分享");
            mood_desc.setText("分享到哪个圈子？");
            mood_txt_arr = Const.weather_desc_text;
            mood_img_arr = Const.weather_img;
            mood_active_img_arr = Const.weather_active_img;
            gview.setNumColumns(3);
            FriendshipApi.categoryinfo(listener);
        }
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myadapter.setSeclection(position);
                myadapter.notifyDataSetChanged();
            }
        });

        setTitleColor_(R.color.master_me);
        backView.setOnClickListener(this);
        confirmView.setOnClickListener(this);
    }
    Listener<JSONObject> listener = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showWaitDialog();
        }

        @Override
        public void onSuccess(JSONObject response) {
            hideWaitDialog();
          Log.e("response",response.toString());
            try {
                if (response.getString("status").equals("1")) {
                    JSONArray data = response.getJSONArray("data");
                    maps = new ArrayList<>();
                    for (int i=0;i<data.length();i++)
                    {
                        Map<String,String> map = new HashMap<>();
                        JSONObject object = data.getJSONObject(i);
                        map.put("cat_id", object.getString("cat_id"));
                        map.put("cat_icon", object.getString("cat_icon"));
                        map.put("cat_name", object.getString("cat_name"));
                        maps.add(map);
                    }
                    myadapter = new MyAdapter(MoodWeather.this);
                    gview.setAdapter(myadapter);
                }
                else{
                    AppContext.showToast("数据解析异常");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            hideWaitDialog();
        }
    };
    private class MyAdapter extends BaseAdapter{

            private Context context=null;
        private LayoutInflater inflater=null;

        public void setSeclection(int position) {
            clickTemp = position;
        }

        public MyAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (type.equals("weather")||type.equals("mood"))
            return mood_txt_arr.length;
            else
             return maps.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        private class MoodHolder{
            TextView mood_txt=null;
            ImageView mood_img=null;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //        获得holder以及holder对象中tv和img对象的实例
            MoodHolder holder;
            if(convertView==null){
                convertView= inflater.inflate(R.layout.item_mood, null);
                holder=new MoodHolder();
                holder.mood_img = (ImageView) convertView.findViewById(R.id.mood_img);
                holder.mood_txt =(TextView) convertView.findViewById(R.id.mood_txt);

                convertView.setTag(holder);

            }else{
                holder=(MoodHolder) convertView.getTag();
            }
            if (type.equals("weather")||type.equals("mood")) {
                if (clickTemp != position) {
                    holder.mood_txt.setTextColor(getResources().getColor(R.color.mood_gray));
                    holder.mood_img.setImageResource(mood_img_arr[position]);
                } else {
                    holder.mood_txt.setTextColor(getResources().getColor(R.color.mood_selected));
                    holder.mood_img.setImageResource(mood_active_img_arr[position]);
                }
                holder.mood_txt.setText(mood_txt_arr[position]);
            }else{
                if (clickTemp != position) {
                    holder.mood_txt.setTextColor(getResources().getColor(R.color.mood_gray));
                } else {
                    holder.mood_txt.setTextColor(getResources().getColor(R.color.mood_selected));
                }
                holder.mood_txt.setText(maps.get(position).get("cat_name"));
                Netroid.displayImage(maps.get(position).get("cat_icon"),holder.mood_img);
            }
            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_back:
                finish();
                break;
            case R.id.post_confirm:
                Intent mIntent = new Intent();

                if ((type.equals("weather")||type.equals("mood"))&&clickTemp!=-1)
                {
                    mIntent.putExtra("type", type);
                    mIntent.putExtra("index", clickTemp);
                }else if (clickTemp!=-1)
                {
                    mIntent.putExtra("type", maps.get(clickTemp).get("cat_id"));
                    mIntent.putExtra("index",maps.get(clickTemp).get("cat_name"));
                }
                this.setResult(ImageUtils.REQUEST_CODE_MOOD, mIntent);
                finish();
                break;
        }
    }

}
