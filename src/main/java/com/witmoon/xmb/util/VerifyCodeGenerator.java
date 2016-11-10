package com.witmoon.xmb.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

/**
 * 验证码生成
 * Created by zhyh on 2015/4/30.
 */
public class VerifyCodeGenerator {
    // 用于生成验证码的字符
    private static final char[] CHARS = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    //default settings
    private int width = 80, height = 24;
    private int codeLength = 4, lineNumber = 3, fontSize = 24;
    private int paddingLeft = 10, paddingTop = 2;

    private static Random random = new Random();
    private String code;

    private static VerifyCodeGenerator instance;

    private VerifyCodeGenerator() {
    }

    public static VerifyCodeGenerator getInstance() {
        if (null == instance) {
            instance = new VerifyCodeGenerator();
        }
        return instance;
    }

    public Bitmap generateBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        code = createCode();

        Paint paint = new Paint();

        int avgW = width / codeLength;
        Rect rect = new Rect();

        int charW, charH;
        while (true) {
            paint.setTextSize(fontSize);
            paint.getTextBounds(code.charAt(0) + "", 0, 1, rect);
            charW = rect.width();
            charH = rect.height();

            if (charW < avgW && charH < height) break;

            fontSize -= 2;
        }

        int pl = paddingLeft;
        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);

            pl += random.nextInt(avgW - charW);
            int pt = charH + random.nextInt(height - charH);
            canvas.drawText(code.charAt(i) + "", pl, pt, paint);
            pl += charW;
        }

        for (int i = 0; i < lineNumber; i++) {
            drawLine(canvas, paint);
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();//

        return bitmap;
    }

    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        int stopX = random.nextInt(width);
        int stopY = random.nextInt(height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private int randomColor() {
        return randomColor(1);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
//      paint.setUnderlineText(true); //true为下划线，false为非下划线
//      paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    public VerifyCodeGenerator width(int width) {
        this.width = width;
        return this;
    }

    public VerifyCodeGenerator height(int height) {
        this.height = height;
        return this;
    }

    public VerifyCodeGenerator fontSize(int fs) {
        this.fontSize = fs;
        return this;
    }

    public String getCode() {
        return code;
    }
}
