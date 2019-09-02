package com.fmtch.base.utils;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.fmtch.base.R;


public class DialogUtils {
    /**
     * 创建对话框
     * @param context           上下文
     * @param view              对话框内容视图
     * @param marginWidth       外边距
     * @return
     */
    public static AlertDialog showDialog(Context context, View view, int marginWidth) {
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        builder.setView(view);
        // 创建对话框
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = UIUtils.getScreenWidth() - UIUtils.dp2px(marginWidth);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(params);
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_shape);
        return alertDialog;
    }

    /**
     * 显示
     * @param context
     * @param resId
     */
    public static void showToastDialogWithConfirm(Context context,int resId) {
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        // 获取布局
        View view = View.inflate(context, R.layout.dialog_toast, null);
        //标题
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(resId);
        //确认
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);

        // 设置参数
        builder.setView(view);
        // 创建对话框
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = UIUtils.getScreenWidth() - UIUtils.dp2px(55);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(params);
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_shape);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }



    /**
     * 从底部弹出对话框
     * @param context
     * @param view
     */
    public static BottomSheetDialog showDetailBottomDialog(final Context context, View view ) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(true);

        view.post(new Runnable() {
            @Override
            public void run() {
                View mView = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(mView);
                mView.setBackgroundResource(android.R.color.transparent);
                behavior.setHideable(false);
                behavior.setPeekHeight(UIUtils.getDisplayScreenHeight(context));
            }
        });
        return bottomSheetDialog;
    }

    /**
     * 从底部弹出对话框
     * @param context
     * @param view
     */
    public static BottomSheetDialog showInfoBottomDialog(final Context context, View view ) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(true);

        view.post(new Runnable() {
            @Override
            public void run() {
                View mView = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(mView);
                behavior.setHideable(false);
                behavior.setPeekHeight(UIUtils.getDisplayScreenHeight(context));
            }
        });
        return bottomSheetDialog;
    }
}
