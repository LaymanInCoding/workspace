package com.witmoon.xmb.activity.friendship.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.friendship.UserLevel;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Article;
import com.witmoon.xmb.model.Comment;

import java.util.List;

/**
 * 麻包圈子之育儿心经适配器
 * Created by zhyh on 2015/6/24.
 */
public class ArticleAdapter extends BaseRecyclerAdapter {

    private OnFocusOnClickListener mOnFocusOnClickListener;
    private OnPraiseClickListener mOnPraiseClickListener;
    private OnCommentClickListener mOnCommentClickListener;
    private OnShareListener mOnShareListener;

    private LayoutInflater mLayoutInflater;

    public void setOnFocusOnClickListener(
            OnFocusOnClickListener onFocusOnClickListener) {
        mOnFocusOnClickListener = onFocusOnClickListener;
    }

    public void setOnPraiseClickListener(
            OnPraiseClickListener onPraiseClickListener) {
        mOnPraiseClickListener = onPraiseClickListener;
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }

    public void setOnShareListener(OnShareListener onShareListener) {
        mOnShareListener = onShareListener;
    }

    @Override
    protected View onCreateItemView(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        return mLayoutInflater.inflate(R.layout.item_article, parent, false);
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
        return new ExperienceHolder(viewType, view);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, int position) {
        final ExperienceHolder eHolder = (ExperienceHolder) holder;
        final Article article = (Article) _data.get(position);

        eHolder.mAuthorNameText.setText(article.getNickName());
        eHolder.mLevelImage.setImageResource(UserLevel.getLevel(article.getUserRank()).getResId());
        eHolder.mTitleText.setText(article.getTitle());
        eHolder.content.setText(article.getContent());
        eHolder.mCommentNumber.setText(article.getCommentNumber());
        eHolder.mPraiseCheckbox.setText(article.getPraiseNumber() + "赞");
        Netroid.displayImage(article.getAvatar(), eHolder.mAuthorAvatarImage);
        Netroid.displayImage(article.getImageArray()[0], eHolder.mImageView);

        List<Comment> commentList = article.getComments();
        for (int i = 0; i < commentList.size(); i++) {
            Comment comment = commentList.get(i);
            ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.item_comment,
                    eHolder.mCommentContainerLayout, false);
            ImageView imageView = (ImageView) viewGroup.findViewById(R.id.avatar_img);
            Netroid.displayImage(comment.getAvatar(), imageView);
            TextView nameText = (TextView) viewGroup.findViewById(R.id.author_name);
            nameText.setText(comment.getNickName());
            TextView contentText = (TextView) viewGroup.findViewById(R.id.content);
            contentText.setText(comment.getContent());
            TextView timeText = (TextView) viewGroup.findViewById(R.id.time);
            timeText.setText(DateFormat.format("yyyy-MM-dd HH:mm", comment.getTime() * 1000));
        }

        // 绑定关注监听器
        eHolder.mFocusOnCheckbox.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != mOnFocusOnClickListener) {
                    if (isChecked) {
                        eHolder.mFocusOnCheckbox.setChecked(true);
                        eHolder.mFocusOnCheckbox.setText("已关注");
                        mOnFocusOnClickListener.onFocusOnClick(article.getUserId());
                    }
                }
            }
        });

        // 点赞监听器
        eHolder.mPraiseCheckbox.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != mOnPraiseClickListener) {
                    if (isChecked) {
                        article.setPraiseNumber(article.getPraiseNumber() + 1);
                        eHolder.mPraiseCheckbox.setText(article.getPraiseNumber() + "赞");
                        mOnPraiseClickListener.onPraiseClick(article.getId(), true);
                    } else {
                        article.setPraiseNumber(article.getPraiseNumber() - 1);
                        eHolder.mPraiseCheckbox.setText(article.getPraiseNumber() + "赞");
                        mOnPraiseClickListener.onPraiseClick(article.getId(), false);
                    }
                }
            }
        });

        // 评论点击
        eHolder.mCommentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCommentClickListener != null) {
                    mOnCommentClickListener.onCommentClick(article.getId());
                }
            }
        });

        eHolder.mShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnShareListener != null) {
                    mOnShareListener.onShare();
                }
            }
        });
    }

    // ---------------------------------- 接口 --------------------------------------

    public interface OnFocusOnClickListener {
        void onFocusOnClick(String uid);
    }

    public interface OnPraiseClickListener {
        void onPraiseClick(String id, boolean b);
    }

    public interface OnCommentClickListener {
        void onCommentClick(String id);
    }

    public interface OnShareListener {
        void onShare();
    }

    // ------------------------------------------------------------------------------

    static class ExperienceHolder extends ViewHolder {
        ImageView mAuthorAvatarImage;
        TextView mAuthorNameText;
        ImageView mLevelImage;
        ImageView mImageView;
        TextView mTitleText;
        TextView content;
        CheckBox mPraiseCheckbox;
        TextView mCommentNumber;
        ImageView mShareImage;
        CheckBox mFocusOnCheckbox;

        LinearLayout mCommentContainerLayout;

        public ExperienceHolder(int viewType, View itemView) {
            super(viewType, itemView);
            mAuthorAvatarImage = (ImageView) itemView.findViewById(R.id.author_avatar);
            mAuthorNameText = (TextView) itemView.findViewById(R.id.author_name);
            mLevelImage = (ImageView) itemView.findViewById(R.id.level);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mTitleText = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);

            mCommentContainerLayout = (LinearLayout) itemView.findViewById(R.id.comment_layout);

            mFocusOnCheckbox = (CheckBox) itemView.findViewById(R.id.focus_on);
            mPraiseCheckbox = (CheckBox) itemView.findViewById(R.id.praise_number);
            mCommentNumber = (TextView) itemView.findViewById(R.id.comment_number);
            mShareImage = (ImageView) itemView.findViewById(R.id.share);
        }
    }
}
