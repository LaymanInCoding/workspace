package com.witmoon.xmb.activity.friendship.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;

import java.util.List;
import java.util.Map;

/**
 * 个人主页照片RecyclerView列表适配器
 * Created by zhyh on 2015/5/31.
 */
public class PersonalHomePageAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mHeaderView;

    private List<Map<String, String>> mDataList;
    private int mResId;

    private int mHeaderColumnCount;

    public PersonalHomePageAlbumAdapter(Context context, List<Map<String, String>> dataList, int
            itemResId, View topView) {
        this(context, dataList, itemResId, topView, 1);
    }

    public PersonalHomePageAlbumAdapter(Context context, List<Map<String, String>> dataList, int
            itemResId, View topView, int gridColumnCount) {
        this.mContext = context;
        this.mHeaderView = topView;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mResId = itemResId;
        this.mHeaderColumnCount = gridColumnCount;
        this.mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = new View(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mHeaderView.getMeasuredHeight()));
            return new HeaderViewHolder(view);
        }

        return new ItemViewHolder(mLayoutInflater.inflate(mResId, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Map<String, String> dataMap = mDataList.get(position - mHeaderColumnCount);
            itemViewHolder.title.setText(dataMap.get("title"));
            itemViewHolder.mTimeText.setText(DateFormat.format("yyyy-MM-dd HH:mm", Long.parseLong
                    (dataMap.get("publish_time")) * 1000));
            itemViewHolder.mCommentNumberText.setText(dataMap.get("comment_num"));
            itemViewHolder.mPraiseNumberText.setText(dataMap.get("praise_num"));
            Netroid.displayImage(dataMap.get("url"), itemViewHolder.photo);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaderColumnCount) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDataList.size() + mHeaderColumnCount;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView photo;
        TextView mPraiseNumberText;
        TextView mCommentNumberText;
        TextView mTimeText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            mPraiseNumberText = (TextView) itemView.findViewById(R.id.praise_number);
            mCommentNumberText = (TextView) itemView.findViewById(R.id.comment_number);
            mTimeText = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
