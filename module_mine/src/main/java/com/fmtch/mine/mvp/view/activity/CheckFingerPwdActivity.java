package com.fmtch.mine.mvp.view.activity;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.pojo.UserGesturesPwdInfo;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.ArouterNavCallback;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.base.widget.dialog.InputLoginPwdDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

@Route(path = RouterMap.CHECK_FINGER_PWD)
public class CheckFingerPwdActivity extends BaseActivity {

    @BindView(R2.id.tv_account)
    TextView tvAccount;
    @BindView(R2.id.tv_error)
    TextView tvError;
    @BindView(R2.id.iv_finger)
    ImageView ivFinger;

    private int times = 5;

    private CancellationSignal cancellationSignal;
    private boolean IsFinish = false;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_finger_check;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Realm mRealm = Realm.getDefaultInstance();
        UserLoginInfo mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            if (!StringUtils.isEmpty(mUserLoginInfo.getMobile())) {
                tvAccount.setText(StringUtils.hiddenMobile(mUserLoginInfo.getMobile()));
            } else if (!StringUtils.isEmpty(mUserLoginInfo.getEmail())) {
                tvAccount.setText(StringUtils.hiddenEmail(mUserLoginInfo.getEmail()));
            }
        }
        FingerprintManagerCompat fingerprint = FingerprintManagerCompat.from(this);
        // 判断设备是否支持指纹解锁
        if (!fingerprint.isHardwareDetected() || !fingerprint.hasEnrolledFingerprints()) {
            //设备不支持或未开启指纹跳转登录页
            onViewClicked();
        }
        cancellationSignal = new CancellationSignal();
        fingerprint.authenticate(null, 0, cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                super.onAuthenticationError(errMsgId, errString);
                //验证错误时，回调该方法。当连续验证5次错误时，将会走
                if (!IsFinish)
                    handler.obtainMessage(1, errMsgId, 0).sendToTarget();
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //验证成功时，回调该方法。fingerprint对象不能再验证
                if (!IsFinish)
                    handler.obtainMessage(2).sendToTarget();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //验证失败时，回调该方法。fingerprint对象不能再验证并且需要等待一段时间才能重新创建指纹管理对象进行验证
                if (!IsFinish)
                    handler.obtainMessage(3).sendToTarget();
            }
        }, handler);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:   //验证失败
//                    handleErrorCode(msg.arg1);
                    ToastUtils.showLongToast(R.string.mine_finger_error_to_login);
                    SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, "");
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<UserLoginInfo> userList = realm.where(UserLoginInfo.class).findAll();
                            userList.deleteAllFromRealm();
                            RealmResults<UserGesturesPwdInfo> userGesturesPwdInfos = realm.where(UserGesturesPwdInfo.class).findAll();
                            userGesturesPwdInfos.deleteAllFromRealm();
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
                            finish();
                        }
                    });
                    break;
                case 2:   //验证成功
                    ivFinger.setImageResource(R.drawable.mine_icon_finger_success);
                    ARouter.getInstance().build(getIntent().getStringExtra("router")).navigation(CheckFingerPwdActivity.this, new ArouterNavCallback(CheckFingerPwdActivity.this));
                    break;
                case 3:
                    //验证错误
                    times--;
                    tvError.setText(String.format(getString(R.string.mine_finger_error_times), String.valueOf(times)));
                    break;
            }
            return false;
        }
    });

    //对应不同的错误，可以有不同的操作
    private void handleErrorCode(int code) {
        switch (code) {
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                //指纹传感器不可用，该操作被取消
                tvError.setText("指纹传感器不可用，该操作被取消");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                //当前设备不可用，请稍后再试
                tvError.setText("当前设备不可用，请稍后再试");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
                //由于太多次尝试失败导致被锁，该操作被取消
                tvError.setText("由于太多次尝试失败导致被锁，该操作被取消");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                //没有足够的存储空间保存这次操作，该操作不能完成
                tvError.setText("没有足够的存储空间保存这次操作，该操作不能完成");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                //操作时间太长，一般为30秒
                tvError.setText("操作时间太长，一般为30秒");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                //传感器不能处理当前指纹图片
                tvError.setText("传感器不能处理当前指纹图片");
                break;
        }
    }

    @OnClick(R2.id.tv_pwd_login)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        //验证密码
        new InputLoginPwdDialog(CheckFingerPwdActivity.this)
                .setOnConfirmSuccessListener(new InputLoginPwdDialog.OnConfirmSuccessListener() {
                    @Override
                    public void onConfirmSuccess() {
                        //密码验证成功
                        ARouter.getInstance().build(getIntent().getStringExtra("router")).navigation();
                        finish();
                    }
                })
                .setOnConfirmFailListener(new InputLoginPwdDialog.OnConfirmFailListener() {
                    @Override
                    public void onConfirmFail() {
                        //密码验证失败
                        SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, "");
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<UserLoginInfo> userList = realm.where(UserLoginInfo.class).findAll();
                                userList.deleteAllFromRealm();
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
                                finish();
                            }
                        });
                    }
                })
                .builder()
                .show();
    }

    @Override
    protected void onDestroy() {
        IsFinish = true;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
       if (event.getTag() == EventType.USER_EXIT) {
            //退出登录清空账号信息
            SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, "");
            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<UserLoginInfo> userList = realm.where(UserLoginInfo.class).findAll();
                    userList.deleteAllFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
                    finish();
                }
            });
        }
    }

}
