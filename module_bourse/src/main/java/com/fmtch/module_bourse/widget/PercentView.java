package com.fmtch.module_bourse.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.fmtch.module_bourse.R;

/**
 * Created by wtc on 2019/5/11
 */
public class PercentView extends View {
    private float iNum = 50;                     //进(左)的数量
    private int iColor = getContext().getResources().getColor(R.color.cl_03c087);              //进的颜色
    private float oNum = 50;                     //出(右)的数量
    private int oColor = getContext().getResources().getColor(R.color.cl_f15659);            //出的颜色
    private int mInclination = -20;               //两柱中间的倾斜度
    private int gap = 0;                         //左右进度条中间的距离
    private Paint mPaint;
    private Path iPath;
    private Path oPath;

    public PercentView(Context context) {
        this(context, null);
    }

    public PercentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PercentView, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.PercentView_gap) {                 //进度条中间的间隔
                gap = typedArray.getInt(attr, gap);
            } else if (attr == R.styleable.PercentView_Inclination) {  //倾斜角度
                mInclination = typedArray.getInt(attr, mInclination);
            } else if (attr == R.styleable.PercentView_iColor) {       //左边进度条的颜色
                iColor = typedArray.getColor(attr, iColor);
            } else if (attr == R.styleable.PercentView_oColor) {       //右边进度条的颜色
                oColor = typedArray.getColor(attr, oColor);
            } else if (attr == R.styleable.PercentView_iNum) {         //左边的进度值
                iNum = typedArray.getFloat(attr, iNum);
            } else if (attr == R.styleable.PercentView_oNum) {         //右边的进度值
                oNum = typedArray.getFloat(attr, oNum);
            }
        }

        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);

        iPath = new Path();
        oPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + getWidth() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingTop() + getHeight() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float iPre = (iNum / (iNum + oNum)) * getWidth();
        float oPre = (oNum / (iNum + oNum)) * getWidth();

        //如果进值或出值有一个为0，则另一个就会占满整个进度条，这时就不需要倾斜角度了
        if (iNum == 0 || oPre == 0) {
            mInclination = 0;
            gap = 0;
        }

        iPath.moveTo(0, 0);
        iPath.lineTo(iPre + mInclination, 0);
        iPath.lineTo(iPre, getHeight());
        iPath.lineTo(0, getHeight());
        iPath.close();

        mPaint.setColor(iColor);
        canvas.drawPath(iPath, mPaint);

        oPath.moveTo(iPre + mInclination + gap, 0);
        oPath.lineTo(getWidth(), 0);
        oPath.lineTo(getWidth(), getHeight());
        oPath.lineTo(iPre + gap, getHeight());
        oPath.close();

        mPaint.setColor(oColor);
        canvas.drawPath(oPath, mPaint);
    }

    /**
     * 动态设置进值
     *
     * @param iNum
     */
    public void setINum(float iNum) {
        this.iNum = iNum;
        postInvalidate();
    }

    /**
     * 动态设置出值
     *
     * @param oNum
     */
    public void setONum(float oNum) {
        this.oNum = oNum;
        postInvalidate();
    }

}
