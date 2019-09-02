package com.fmtch.base.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollRecyclerView extends RecyclerView {
    boolean onTouchEvent;
    boolean onInterceptTouchEvent;

    public NoScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec
                .makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void setNoTouchEvent(boolean onTouchEvent, boolean onInterceptTouchEvent) {
        this.onTouchEvent = onTouchEvent;
        this.onInterceptTouchEvent = onInterceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (onTouchEvent)
            return false;
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (onInterceptTouchEvent)
            return false;
        return super.onInterceptTouchEvent(e);
    }
}
