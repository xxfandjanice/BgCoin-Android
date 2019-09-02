package com.fmtch.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fmtch.base.utils.AndroidShare;
import com.fmtch.mine.R;


public class InviteShareDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private String mInviteUrl;

    private AndroidShare androidShare;

    public InviteShareDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_invite, null);
        androidShare = new AndroidShare(context);
    }

    public InviteShareDialog setInviteUrl(String url) {
        this.mInviteUrl = url;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(true);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        dialogWindow.setWindowAnimations(R.style.BottomDialogInAndOutAnim);
        dialogWindow.setGravity(Gravity.BOTTOM);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialogWindow.setAttributes(lp);

        mDialogLayout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mDialogLayout.findViewById(R.id.tv_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
                    androidShare.shareWeChatFriend("", mInviteUrl, AndroidShare.TEXT, null);
            }
        });
        mDialogLayout.findViewById(R.id.tv_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
                    androidShare.shareQQFriend("", mInviteUrl, AndroidShare.TEXT,  null);
            }
        });

        mDialogLayout.findViewById(R.id.tv_telegram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
                    androidShare.shareTelegramMessage("", mInviteUrl);
            }
        });

        return dialog;
    }

}
