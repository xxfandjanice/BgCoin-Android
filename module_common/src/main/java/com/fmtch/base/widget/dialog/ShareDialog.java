package com.fmtch.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.fmtch.base.R;
import com.fmtch.base.utils.AndroidShare;
import com.fmtch.base.utils.FileUtils;
import com.fmtch.base.utils.ToastUtils;


public class ShareDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗


    private Bitmap mShareBigmap;
    private AndroidShare androidShare;

    public ShareDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_share, null);
        androidShare = new AndroidShare(context);
    }

    public ShareDialog setShareBitmap(Bitmap bitmap) {
        this.mShareBigmap = bitmap;
        return this;
    }

    public Dialog builder() {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(mDialogLayout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

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
                if (mShareBigmap != null) {
                    androidShare.shareWeChatFriend("", "", AndroidShare.DRAWABLE, mShareBigmap);
                }
            }
        });
        mDialogLayout.findViewById(R.id.tv_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mShareBigmap != null) {
                    androidShare.shareQQFriend("", "", AndroidShare.DRAWABLE, mShareBigmap);
                }
            }
        });

        mDialogLayout.findViewById(R.id.tv_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mShareBigmap != null) {
                    FileUtils.savePic(mContext, mShareBigmap);
                }
            }
        });
        return dialog;
    }

}
