package com.classic.wages.ui.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.widget
 *
 * 文件描述: 圆形Drawable
 * 创 建 人: 续写经典
 * 创建时间: 2016/10/17 20:01
 */
public class CircularDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int   mColor;
    private float mRadius;

    public CircularDrawable(int color, float radius) {
        this.mColor = color;
        this.mRadius = radius;
    }

    @Override public void draw(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true); //设置画笔的锯齿效果
        /**
         * 参数1: 要绘制的圆弧中心的x坐标
         * 参数2：要绘制的圆中心的y坐标
         * 参数3：要绘制圆的半径
         * 参数4：画笔
         */
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);//画圆
    }

    public void invalidateSelf() {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override public void setAlpha(int alpha) {
        final int oldAlpha = mPaint.getAlpha();
        if (alpha != oldAlpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
