package com.fmtch.base.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fmtch.base.R;
import com.fmtch.base.utils.UIUtils;

import java.lang.ref.WeakReference;

public class MyProgressView extends LinearLayout {
    private static final int NUM = 3;
    private Context context;
    private ImageView mOldIM;
    private UpdateHandler handler;

    public MyProgressView(Context context) {
        this(context,null);
    }

    public MyProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        handler = new UpdateHandler(context);
        LayoutParams layoutParams = new LayoutParams(UIUtils.dp2px(5), UIUtils.dp2px(5));
        layoutParams.leftMargin = 10;
        layoutParams.rightMargin = 10;

        //添加3个小点省略号
        for (int i = 0; i < NUM; i++) {
            ImageView vDot = new ImageView(context);
            vDot.setLayoutParams(layoutParams);
            if (i == 0) {
                vDot.setBackgroundResource(R.drawable.point_white);
            } else {
                vDot.setBackgroundResource(R.drawable.point_black);
            }
            this.addView(vDot);
        }
        mOldIM = (ImageView) this.getChildAt(0);
        handler.sendEmptyMessage(0);
    }

    //提供给外部消除message
    public void setDestroyCallBack() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    class UpdateHandler extends Handler {
        WeakReference<Context> reference;

        public UpdateHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int cPosition = msg.what;
            if (mOldIM != null)
                mOldIM.setBackgroundResource(R.drawable.point_black);
            ImageView currentIM = (ImageView) MyProgressView.this.getChildAt(cPosition);
            currentIM.setBackgroundResource(R.drawable.point_white);
            mOldIM = currentIM;
            if (++cPosition == NUM)
                cPosition = 0;
            this.sendEmptyMessageDelayed(cPosition, 200);
        }
    }
}
