package com.witmoon.xmb.activity.goods.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.PreviewImage;
import com.witmoon.xmb.activity.goods.fragment.EvaluateFragment;
import com.witmoon.xmb.model.EvaluateBean;
import com.witmoon.xmb.model.ImageBDInfo;
import com.witmoon.xmb.util.ImageLoaders;
import com.witmoon.xmblibrary.linearlistview.adapter.LinearBaseAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品评价列表适配器
 * Created by zhyh on 2015/8/30.
 */
public class GoodsEvaluationAdapter extends LinearBaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<EvaluateBean> mDataList;
    private ImageBDInfo bdInfo;
    private EvaluateFragment evaluateFragment;
    private int mCommentCount = 3;
    private int ImagaId[] = {R.id.img_0, R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4};

    public GoodsEvaluationAdapter(Context context, List<EvaluateBean> dataList, EvaluateFragment evaluateFragment) {
        mContext = context;
        this.mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(context);
        this.evaluateFragment = evaluateFragment;
        bdInfo = new ImageBDInfo();
    }

    @Override
    public int getCountOfIndexViewType(int mType) {
        if (mType == 0) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public int getCount() {
        if (mDataList.size() > 3) {
            return mCommentCount;
        } else {
            return mDataList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        EvaluationHolder evaluationHolder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_evaluate, parent, false);
            evaluationHolder = new EvaluationHolder();
            evaluationHolder.content = (TextView) view.findViewById(R.id.content_e);
            evaluationHolder.nameText = (TextView) view.findViewById(R.id.name_e);
            evaluationHolder.rating = (RatingBar) view.findViewById(R.id.rating_e);
            evaluationHolder.showimage = (ImageView) view.findViewById(R.id.showimage);
            evaluationHolder.gridview = (GridLayout) view.findViewById(R.id.gridview);
            for (int i = 0; i < 5; i++) {
                evaluationHolder.imgview[i] = (ImageView) view.findViewById(ImagaId[i]);
            }
            view.setTag(evaluationHolder);
        } else {
            evaluationHolder = (EvaluationHolder) view.getTag();
        }
        EvaluateBean mEvaluateBean = mDataList.get(position);
        evaluationHolder.nameText.setText(mEvaluateBean.getAuthor());
        evaluationHolder.content.setText(mEvaluateBean.getContent());
        evaluationHolder.rating.setRating(Float.valueOf(mEvaluateBean.getmRating() + ""));
        if (mEvaluateBean.getInfo().size() == 1) {
            evaluationHolder.showimage.setVisibility(View.VISIBLE);
            evaluationHolder.gridview.setVisibility(View.GONE);
            com.witmoon.xmb.model.ImageInfo imageInfo = mEvaluateBean.getInfo().get(0);
            ImageLoaders.setsendimg(imageInfo.url, evaluationHolder.showimage);
            evaluationHolder.showimage.setOnClickListener(new SingleOnclick(position));
        } else if (null != mEvaluateBean.getInfo())
            if (mEvaluateBean.getInfo().size() > 1) {
                evaluationHolder.showimage.setVisibility(View.GONE);
                evaluationHolder.gridview.setVisibility(View.VISIBLE);
                int a = mEvaluateBean.getInfo().size() / 3;
                int b = mEvaluateBean.getInfo().size() % 3;
                if (b > 0) {
                    a++;
                }
                float width = (evaluateFragment.getView().getWidth() - dip2px(80) - dip2px(2)) / 3;
                evaluationHolder.gridview.getLayoutParams().height = (int) (a * width);
                for (int i = 0; i < 5; i++) {
                    //设置全部隐藏
                    evaluationHolder.imgview[i].setVisibility(View.GONE);
                }

                for (int i = 0; i < mEvaluateBean.getInfo().size(); i++) {
                    //动态设置图片的现实条目
                    com.witmoon.xmb.model.ImageInfo imageInfo = (com.witmoon.xmb.model.ImageInfo) mEvaluateBean.getInfo().get(i);
                    evaluationHolder.imgview[i].setVisibility(View.VISIBLE);
                    evaluationHolder.imgview[i].getLayoutParams().width = (int) width;
                    evaluationHolder.imgview[i].getLayoutParams().height = (int) width;
                    ImageLoaders.setsendimg(imageInfo.url, evaluationHolder.imgview[i]);
                    evaluationHolder.imgview[i].setOnClickListener(new GridOnclick(position,i));
                }
            }
        return view;
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    @Override
    public int getViewTypeCount() {
        return mDataList.size();
    }
    class SingleOnclick implements View.OnClickListener {
        private int index;

        public SingleOnclick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, PreviewImage.class);
            ArrayList<com.witmoon.xmb.model.ImageInfo> info = mDataList.get(index).getInfo();
            intent.putExtra("data", (Serializable) info);
            intent.putExtra("bdinfo", bdInfo);
            intent.putExtra("index", 0);
            mContext.startActivity(intent);
        }
    }

    class GridOnclick implements View.OnClickListener {

        private int index;
        private int row;
        public GridOnclick(int index, int row) {
            this.index = index;
            this.row = row;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, PreviewImage.class);
            ArrayList<com.witmoon.xmb.model.ImageInfo> info = mDataList.get(index).getInfo();
            intent.putExtra("data", (Serializable) info);
            intent.putExtra("bdinfo", bdInfo);
            intent.putExtra("index", row);
            mContext.startActivity(intent);
        }
    }

    // ViewHolder
    class EvaluationHolder {
        TextView nameText;
        TextView content;
        RatingBar rating;
        GridLayout gridview;
        ImageView showimage;
        ImageView imgview[] = new ImageView[5];
    }

    public void showAllComments() {
        mCommentCount = mDataList.size();
        notifyDataSetInvalidated();
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
