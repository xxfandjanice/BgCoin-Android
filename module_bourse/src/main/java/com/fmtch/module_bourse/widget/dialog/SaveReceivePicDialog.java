package com.fmtch.module_bourse.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fmtch.base.utils.FileUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;


public class SaveReceivePicDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private Bitmap mQrcodeBitmap;


    public SaveReceivePicDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_save_receive_pic, null);
    }

    public SaveReceivePicDialog setBitmap(Bitmap bitmap){
        this.mQrcodeBitmap = bitmap;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(true);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        if (mQrcodeBitmap != null){
            ((ImageView)mDialogLayout.findViewById(R.id.iv_qr_code)).setImageBitmap(mQrcodeBitmap);
        }
        mDialogLayout.findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils.savePic(mContext, mQrcodeBitmap);
                dialog.dismiss();
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        dialogWindow.setWindowAnimations(R.style.DialogInAndOutAnim);
        dialogWindow.setGravity(Gravity.CENTER);
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = display.getWidth(); //设置宽度
//        dialogWindow.setAttributes(lp);
        return dialog;
    }


}
