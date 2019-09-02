package com.fmtch.base.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.fmtch.base.BuildConfig;
import com.fmtch.base.R;
import com.fmtch.base.app.BaseApplication;
import com.fmtch.base.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class CheckVersionDialog {

    private Activity mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗
    private boolean mCancelable = true;//是否弹窗可关闭（默认可关闭）

    private CircleProgressBar mProgressBar;
    private Dialog mDownloadDialog;

    private int mProgress;
    private boolean mIsCancel = false;//下载

    private static final int DOWNLOADING = 1;
    private static final int DOWNLOAD_FINISH = 2;

    private String mDownApkUrl;
    private String mSavePath;
    private String mVersion_code;

    private Thread mThread;

    public CheckVersionDialog(Activity context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_check_version, null);
    }

    public CheckVersionDialog setTitle(String title) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_title)).setText(title);
        return this;
    }

    public CheckVersionDialog setContent(String content) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_content)).setText(content);
        return this;
    }

    public CheckVersionDialog setVersionCode(String versionCode) {
        this.mVersion_code = versionCode;
        return this;
    }

    public CheckVersionDialog setDownApkUrl(String url) {
        this.mDownApkUrl = url;
        return this;
    }

    //设置不可关闭时为强制更新不可点击取消
    public CheckVersionDialog setCancelable(boolean cancel) {
        this.mCancelable = cancel;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(mCancelable);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
        mDialogLayout.findViewById(R.id.tv_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!TextUtils.isEmpty(mDownApkUrl)) {
                    openDownloadDialog();
                }
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.7); //设置宽度
//        lp.height = (int) (lp.width * 1.23); //设置高度
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    protected void openDownloadDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.dialog_progress_layout, null);
        mDownloadDialog = new Dialog(mContext, R.style.MyDialogTheme);
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.addContentView(layout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        mDownloadDialog.getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
        mProgressBar = layout.findViewById(R.id.id_progress);
        layout.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCancelable) {
                    return;
                }
                mIsCancel = true;
                mDownloadDialog.dismiss();
                if (mThread != null && mThread.isAlive()) {
                    mThread.interrupt();
                }
            }
        });
        Window dialogWindow = mDownloadDialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.7); //设置宽度
//        lp.height = (int) (lp.width * 1.23); //设置高度
        mDownloadDialog.getWindow().setAttributes(lp);
        mDownloadDialog.show();
        downloadAPK();
    }

    private Handler mUpdateProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    mProgressBar.setProgress(mProgress);
//                    mProgressBarView.setText(String.format(mContext.getResources().getString(R.string.down_loading), String.valueOf(mProgress)));
                    break;
                case DOWNLOAD_FINISH:
                    mDownloadDialog.dismiss();
                    installAPK();
            }
        }
    };

    private void downloadAPK() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Environment.getExternalStorageState()
                            .equals(Environment.MEDIA_MOUNTED)) {
                        String sdPath = Environment.getExternalStorageDirectory() + "/";
                        mSavePath = sdPath + "apkDownload";
                        File dir = new File(mSavePath);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        // 下载文件
                        HttpURLConnection conn = (HttpURLConnection) new URL(mDownApkUrl)
                                .openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        int length = conn.getContentLength();
                        File apkFile = new File(mSavePath,
                                "bourse_" + mVersion_code + ".apk");
                        FileOutputStream fos = new FileOutputStream(apkFile);
                        int count = 0;
                        byte[] buffer = new byte[1024];
                        while (!mIsCancel) {
                            int numread = is.read(buffer);
                            count += numread;
                            // 计算进度条的当前位置
                            mProgress = (int) (((float) count / length) * 100);
                            // 更新进度条
                            mUpdateProgressHandler.sendEmptyMessage(DOWNLOADING);
                            // 下载完成
                            if (numread < 0) {
                                mUpdateProgressHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                                break;
                            }
                            fos.write(buffer, 0, numread);
                        }
                        fos.close();
                        is.close();
                    }
                } catch (Exception e) {
                    mDownloadDialog.dismiss();
                    e.printStackTrace();
                    return;
                }
            }
        });
        mThread.start();
    }

    protected void installAPK() {
        File apkFile = new File(mSavePath,
                "bourse_" + mVersion_code + ".apk");
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, BaseApplication.getApplication().getPackageName() + ".fileProvider", apkFile);
            mContext.grantUriPermission(mContext.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);

    }

}
