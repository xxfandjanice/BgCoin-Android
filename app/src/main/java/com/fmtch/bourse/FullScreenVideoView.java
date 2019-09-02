package com.fmtch.bourse;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.VideoView;


/**
 * Created by wtc on 2018/12/26
 */

public class FullScreenVideoView extends VideoView {


    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mVideoWidth = 750;
        int mVideoHeight = 1400;
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        float fWidth = mVideoWidth / (float) width;
        float fHeight = mVideoHeight / (float) height;

        if (fWidth > fHeight) {
            width = (int) (mVideoWidth / fHeight);
            height = (int) (mVideoHeight / fHeight);
        } else if (fWidth < fHeight) {
            width = (int) (mVideoWidth / fWidth);
            height = (int) (mVideoHeight / fWidth);
        } else {
            width = (int) (mVideoWidth / fWidth);
            height = (int) (mVideoHeight / fHeight);
        }
        setMeasuredDimension(width, height);
    }
}


