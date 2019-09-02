package com.fmtch.module_bourse.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by wtc on 2019/5/20
 */
public class CustomPopupWindow implements PopupWindow.OnDismissListener {
    private PopupWindow mPopupWindow;
    private View contentView;
    private Context mContext;
    private Activity mActivity;
    private OnDismissListener listener;

    private CustomPopupWindow(Builder builder) {
        mContext = builder.context;
        if (builder.contentView != null) {
            contentView = builder.contentView;
        } else {
            contentView = LayoutInflater.from(mContext).inflate(builder.contentViewId, null);
        }
        mPopupWindow = new PopupWindow(contentView, builder.width, builder.height, builder.fouse);

        //需要跟 setBackGroundDrawable 结合
        mPopupWindow.setOutsideTouchable(builder.outsideCancel);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mPopupWindow.setFocusable(true);// 这个很重要
        mPopupWindow.setAnimationStyle(builder.animStyle);

        mPopupWindow.getContentView().setFocusable(true); // 这个很重要
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mPopupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        //设置背景色
        if (builder.alpha > 0 && builder.alpha < 1) {
            mActivity = builder.activity;
            WindowManager.LayoutParams params = builder.activity.getWindow().getAttributes();
            params.alpha = builder.alpha;
            builder.activity.getWindow().setAttributes(params);
        }

        mPopupWindow.setOnDismissListener(this);
    }


    /**
     * popup 消失
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            if (mActivity != null) {
                WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
                params.alpha = 1.0f;
                mActivity.getWindow().setAttributes(params); //回复背景色
            }
        }
    }

    @Override
    public void onDismiss() {
        if (mActivity != null) {
            WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
            params.alpha = 1.0f;
            mActivity.getWindow().setAttributes(params);
        }
        if (listener != null) {
            this.listener.OnPopupDismissListener();
        }
    }


    public boolean isShowing() {
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }
        return false;
    }

    /**
     * 根据id获取view
     *
     * @param viewId
     * @return
     */
    public View getItemView(int viewId) {
        if (mPopupWindow != null) {
            return this.contentView.findViewById(viewId);
        }
        return null;
    }

    /**
     * 根据父布局，显示位置
     *
     * @param rootViewId
     * @param gravity
     * @param x
     * @param y
     * @return
     */
    public CustomPopupWindow showAtLocation(int rootViewId, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            View rootView = LayoutInflater.from(mContext).inflate(rootViewId, null);
            mPopupWindow.showAtLocation(rootView, gravity, x, y);
        }
        return this;
    }

    /**
     * 根据id获取view ，并显示在该view的位置
     *
     * @param targetViewId
     * @param gravity
     * @param offX
     * @param offY
     * @return
     */
    public CustomPopupWindow showAsLocation(int targetViewId, int gravity, int offX, int offY) {
        if (mPopupWindow != null) {
            View targetView = LayoutInflater.from(mContext).inflate(targetViewId, null);
            mPopupWindow.showAsDropDown(targetView, gravity, offX, offY);
        }
        return this;
    }

    /**
     * 显示在 targetView 的不同位置
     *
     * @param targetView
     * @param gravity
     * @param offX
     * @param offY
     * @return
     */
    public CustomPopupWindow showAsLocation(View targetView, int gravity, int offX, int offY) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(targetView, offX, offY, gravity);
        }
        return this;
    }


    /**
     * 根据id设置焦点监听
     *
     * @param viewId
     * @param listener
     */
    public void setOnFocusListener(int viewId, View.OnFocusChangeListener listener) {
        View view = getItemView(viewId);
        view.setOnFocusChangeListener(listener);
    }


    /**
     * builder 类
     */
    public static class Builder {
        private int contentViewId;
        private View contentView;
        private int width;
        private int height;
        private boolean fouse;
        private boolean outsideCancel;
        private int animStyle;
        private float alpha;
        private Activity activity;
        private Context context;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }


        public Builder setContentView(int contentViewId) {
            this.contentViewId = contentViewId;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Builder setwidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setheight(int height) {
            this.height = height;
            return this;
        }

        public Builder setFouse(boolean fouse) {
            this.fouse = fouse;
            return this;
        }

        public Builder setOutSideCancel(boolean outsideCancel) {
            this.outsideCancel = outsideCancel;
            return this;
        }

        public Builder setAnimationStyle(int animstyle) {
            this.animStyle = animstyle;
            return this;
        }

        public Builder setBackGroudAlpha(Activity activity, float alpha) {
            this.activity = activity;
            this.alpha = alpha;
            return this;
        }

        public CustomPopupWindow builder() {
            return new CustomPopupWindow(this);
        }
    }

    public interface OnDismissListener {
        void OnPopupDismissListener();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }
}
