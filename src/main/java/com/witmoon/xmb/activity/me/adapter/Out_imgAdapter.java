package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.util.BitmapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
//上传图片的处理
/**
 * Created by de on 2015/12/1
 */
public class Out_imgAdapter extends BaseAdapter{
    public ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
    private Context mContext;
    Bitmap mBitMap =null;
    public Out_imgAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public int getCount() {

        return mList.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHodler mViewHodler = null;
        if (view==null)
        {
            mViewHodler = new ViewHodler();
            view = LayoutInflater.from(mContext).inflate(R.layout.griditem_addpic,parent,false);
            mViewHodler.mImageView = (SimpleDraweeView) view.findViewById(R.id.imageView1);
            view.setTag(mViewHodler);
        }else{
            mViewHodler = (ViewHodler) view.getTag();
        }
        mViewHodler.mImageView.setImageBitmap((Bitmap) mList.get(position).get("itemImage"));
        final ViewHodler finalMViewHodler = mViewHodler;
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==2&&null!=mBitMap)
                {
                    finalMViewHodler.mImageView.setImageBitmap(mBitMap);
                    mList.get(position).put("itemImage", mBitMap);
                    try {
                        mList.get(position).put("imgPath", BitmapUtils.saveMyBitmap(mBitMap, "img_" + position,mContext));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    //返回图片--！
                    AppContext.showToast("加载图片失败！");
                }
            }
        };
        if (""!=mList.get(position).get("imgPath"))
        {
            Log.e("mList",mList.get(position).get("imgPath").toString().substring(0,4));
            if (mList.get(position).get("imgPath").toString().length()>5)
                if (mList.get(position).get("imgPath").toString().substring(0, 4).equals("http")) {
                    Log.e("imgPath", (String) mList.get(position).get("imgPath"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mBitMap = BitmapUtils.the_values_bitmap((String) mList.get(position).get("imgPath"));
                            Message ms = new Message();
                            ms.what = 2;
                            mHandler.sendMessage(ms);
                        }
                    }).start();
                }
        }
        return view;
    }
    //添加图片
    public void addItemImage(Bitmap addbmp,String imgPath)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage",addbmp);
        map.put("imgPath",imgPath);
        mList.add(map);
        notifyDataSetChanged();
    }
    //删除图片
    public void deleteItemImage(int position){
        mList.remove(position);
        notifyDataSetChanged();
    }

    class ViewHodler {
        public SimpleDraweeView mImageView;
    }
}
