package com.witmoon.xmb.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.witmoon.xmb.AppContext;

import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * Created by de on 2016/12/19.
 */
public class URLImageGetter implements Html.ImageGetter {

    private HtmlTextView mTextView;
    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        ImageLoader.getInstance().loadImage(source, AppContext.options_memory,new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                urlDrawable.bitmap = loadedImage;
                urlDrawable.setBounds(0,0,loadedImage.getWidth(),loadedImage.getHeight());
                mTextView.setText(mTextView.getText());
            }
        });
        return urlDrawable;
    }

    public URLImageGetter(HtmlTextView textView){
        this.mTextView = textView;
    }

    public class URLDrawable extends BitmapDrawable {

        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }
}
