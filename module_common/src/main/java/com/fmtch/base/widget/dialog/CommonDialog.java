package com.fmtch.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fmtch.base.R;

public class CommonDialog extends Dialog {
    private OnConfirmButtonClickListener listener;

    private FrameLayout rootView;
    private TextView btnCancel;
    private TextView btnConfirm;
    private View dividerLine;
    private OnCancelButtonClickListener cancelListener;
    private final TextView tvMsg;

    public CommonDialog(Context context) {
        super(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_base, null);
        tvMsg = view.findViewById(R.id.tv_message);
        Window window = getWindow();
        window.setContentView(view);

        initView();
        initEvent();
    }

    private void initView() {
        rootView = findViewById(R.id.fl_content);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);
        dividerLine = findViewById(R.id.divider_line);
    }

    private void initEvent() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (cancelListener != null) {
                    cancelListener.onCancelClick();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmClick();
                }
            }
        });
    }

    public void addContentView(View view) {
        rootView.removeAllViews();
        rootView.addView(view);
    }

    public void showMsg(@NonNull String msg) {
        tvMsg.setText(msg);
    }

    public void showMsg(@NonNull String msg, boolean onlyConfirm) {
        tvMsg.setText(msg);
        if (onlyConfirm) {
            btnCancel.setVisibility(View.GONE);
            dividerLine.setVisibility(View.GONE);
        }
    }

    public void setBtnConfirmText(String text) {
        btnConfirm.setText(text);
    }

    public void setOnConfirmClickListener(OnConfirmButtonClickListener listener) {
        this.listener = listener;
    }

    public void setOnCancelClickListener(OnCancelButtonClickListener listener) {
        this.cancelListener = listener;
    }

    public interface OnConfirmButtonClickListener {
        void onConfirmClick();
    }

    public interface OnCancelButtonClickListener {
        void onCancelClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (cancelListener != null) {
            cancelListener.onCancelClick();
        }
    }
}
