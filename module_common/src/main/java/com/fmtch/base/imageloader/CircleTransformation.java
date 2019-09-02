package com.fmtch.base.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by wtc on 2019/6/18
 */
public class CircleTransformation extends BitmapTransformation {

    public CircleTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        //获取资源的长宽,获取最小值 子位图的像素个数
        int size = Math.min(source.getWidth(), source.getHeight());
        // 子位图第一个像素在源位图的X坐标
        int x = (source.getWidth() - size) / 2;
        //子位图第一个像素在源位图的y坐标
        int y = (source.getHeight() - size) / 2;
        //创建新位图  source 源位图
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        //返回一个正好匹配给定宽、高和配置的只包含透明像素的Bitmap
        // 如果BitmapPool中找不到这样的Bitmap，就返回null
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        //当返回null 时,创建给定宽、高和配置的新位图
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        //画图
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        // 设置shader
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        //抗锯齿
        paint.setAntiAlias(true);
        float r = size / 2f;
        // 用设置好的画笔绘制一个圆
        canvas.drawCircle(r, r, r, paint);
        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
