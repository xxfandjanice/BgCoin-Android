package com.fmtch.mine.mvp.view.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.utils.ZXingUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.GOOGLE_CHECK)
public class CheckBindGoogleActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_secret_key)
    TextView tvSecretKey;
    @BindView(R2.id.iv_qr_code)
    ImageView ivQrCode;

    private String secret_key;//密钥
    private Bitmap mQrCodeBitmap;
    private UserLoginInfo mUserLoginInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_check_google;
    }

    @Override
    protected void initView() {
        super.initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mUserLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        RequestUtil.requestGet(API.GOOGLE_CHECK, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (!TextUtils.isEmpty(response.getSecret())) {
                    secret_key = response.getSecret();
                    tvSecretKey.setText(secret_key);
                    mQrCodeBitmap = ZXingUtils.Create2DCode(otpaSecretKey(), ivQrCode.getWidth(), ivQrCode.getHeight());
                    ivQrCode.setImageBitmap(mQrCodeBitmap);
                }
            }
        },this);
    }

    //处理密钥
    private String otpaSecretKey() {
        String otpa_secret_key = secret_key;
        if (!TextUtils.isEmpty(mUserLoginInfo.getMobile())) {
            otpa_secret_key = "otpauth://totp/ETF.TOP:" + mUserLoginInfo.getMobile() + "?secret=" + secret_key;
        } else if (!TextUtils.isEmpty(mUserLoginInfo.getEmail())) {
            otpa_secret_key = "otpauth://totp/ETF.TOP:" + mUserLoginInfo.getEmail() + "?secret=" + secret_key;
        }
        return otpa_secret_key;
    }

    //复制密钥
    @OnClick(R2.id.tv_copy_key)
    public void onTvCopyKeyClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (!TextUtils.isEmpty(secret_key)) {
            ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(secret_key);
            ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + secret_key);
        }
    }

    //保存二维码
    @OnClick(R2.id.tv_download_qr_code)
    public void onTvDownloadQrCodeClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (null == mQrCodeBitmap)
            return;
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "secret_key";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            OutputStream os = new FileOutputStream(file);
            mQrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
            Uri uri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            ToastUtils.showLongToast(getString(R.string.save_success_to) + storePath);
        } catch (Exception e) {

        }
    }

    //下一步
    @OnClick(R2.id.btn_confirm)
    public void onBtnConfirmClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (TextUtils.isEmpty(secret_key))
            return;
        ARouter.getInstance().build(RouterMap.SET_GOOGLE_CHECK)
                .withBoolean("is_unbind", false)
                .withString("secret_key", secret_key)
                .navigation();
        finish();
    }


}
