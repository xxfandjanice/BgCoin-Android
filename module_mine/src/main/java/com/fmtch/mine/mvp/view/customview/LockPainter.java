package com.fmtch.mine.mvp.view.customview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.wangnan.library.model.Point;
import com.wangnan.library.painter.Painter;

/**
 * @ClassName: LUcomPainter
 * @Description: (仿)陆金所绘制者
 * @Author: wangnan7
 * @Date: 2017/9/21
 */

public class LockPainter extends Painter {

    /**
     * 绘制正常状态的点
     *
     * @param point       单位点
     * @param canvas      画布
     * @param normalPaint 正常状态画笔
     */
    @Override
    public void drawNormalPoint(Point point, Canvas canvas, Paint normalPaint) {
        // 1.绘制圆形轮廓边界
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setStrokeWidth(point.radius / 30.0F);
        canvas.drawCircle(point.x, point.y, point.radius, normalPaint);
    }

    /**
     * 绘制按下状态的点
     *
     * @param point      单位点
     * @param canvas     画布
     * @param pressPaint 按下状态画笔
     */
    @Override
    public void drawPressPoint(Point point, Canvas canvas, Paint pressPaint) {
        int originColor = pressPaint.getColor();
        // 1.绘制白色底圆
        pressPaint.setStyle(Paint.Style.FILL);
        pressPaint.setColor(Color.WHITE);
        canvas.drawCircle(point.x, point.y, point.radius, pressPaint);
        // 2.绘制中心圆环
        pressPaint.setColor(originColor);
        pressPaint.setStyle(Paint.Style.STROKE);
        pressPaint.setStrokeWidth(point.radius / 10.0F);
        canvas.drawCircle(point.x, point.y, point.radius / 3.0F, pressPaint);
        // 3.绘制外部边界圆
        pressPaint.setStyle(Paint.Style.STROKE);
        pressPaint.setStrokeWidth(point.radius / 20.0F);
        canvas.drawCircle(point.x, point.y, point.radius, pressPaint);
    }

    /**
     * 绘制按下状态的点
     *
     * @param point      单位点
     * @param canvas     画布
     * @param errorPaint 按下状态画笔
     */
    @Override
    public void drawErrorPoint(Point point, Canvas canvas, Paint errorPaint) {
        int originColor = errorPaint.getColor();
        // 1.绘制白色底圆
        errorPaint.setStyle(Paint.Style.FILL);
        errorPaint.setColor(Color.WHITE);
        canvas.drawCircle(point.x, point.y, point.radius, errorPaint);
        // 2.绘制中心圆环
        errorPaint.setColor(originColor);
        errorPaint.setStyle(Paint.Style.STROKE);
        errorPaint.setStrokeWidth(point.radius / 10.0F);
        canvas.drawCircle(point.x, point.y, point.radius / 3.0F, errorPaint);
        // 3.绘制外部边界圆
        errorPaint.setStyle(Paint.Style.STROKE);
        errorPaint.setStrokeWidth(point.radius / 20.0F);
        canvas.drawCircle(point.x, point.y, point.radius, errorPaint);
    }
}
