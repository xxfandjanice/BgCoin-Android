package com.fmtch.mine.mvp.view.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
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
import com.fmtch.mine.mvp.view.customview.LockPainter;
import com.fmtch.base.utils.DialogUtils;
import com.wangnan.library.GestureLockView;
import com.wangnan.library.listener.OnGestureLockListener;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

@Route(path = RouterMap.CHECK_GESTURES_PWD)
public class CheckGesturesPwdActivity extends BaseActivity implements OnGestureLockListener {

    @BindView(R2.id.gl_view)
    GestureLockView glView;
    @BindView(R2.id.tv_account)
    TextView tvAccount;
    @BindView(R2.id.tv_error)
    TextView tvError;

    private int times;

    private String gestures_pwd;//手势密码

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_gestures_check;
    }

    @Override
    protected void initView() {
        super.initView();
        glView.setPainter(new LockPainter());
        glView.setGestureLockListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        times = (int) SpUtils.get(KeyConstant.KEY_GESTURE_PWD_TIMES, 5);//最多可输入错误5次
        Realm mRealm = Realm.getDefaultInstance();
        UserLoginInfo mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            if (!StringUtils.isEmpty(mUserLoginInfo.getMobile())) {
                tvAccount.setText(StringUtils.hiddenMobile(mUserLoginInfo.getMobile()));
            } else if (!StringUtils.isEmpty(mUserLoginInfo.getEmail())) {
                tvAccount.setText(StringUtils.hiddenEmail(mUserLoginInfo.getEmail()));
            }
            UserGesturesPwdInfo mUserGesturesPwdInfo = mRealm.where(UserGesturesPwdInfo.class)
                    .equalTo("id", mUserLoginInfo.getId())
                    .findFirst();
            if (mUserGesturesPwdInfo != null && mUserGesturesPwdInfo.getGestures_pwd_status() && !StringUtils.isEmpty(mUserGesturesPwdInfo.getGestures_pwd())) {
                gestures_pwd = mUserGesturesPwdInfo.getGestures_pwd();
            }
        }
    }

    /**
     * 解锁开始监听方法
     */
    @Override
    public void onStarted() {

    }

    /**
     * 解锁密码改变监听方法
     */
    @Override
    public void onProgress(String progress) {

    }

    /**
     * 解锁完成监听方法
     */
    @Override
    public void onComplete(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        } else if (TextUtils.equals(gestures_pwd, result)) {
            //验证成功
            glView.clearView();
            //重新刷新次数
            SpUtils.put(KeyConstant.KEY_GESTURE_PWD_TIMES, 5);
            ARouter.getInstance().build(getIntent().getStringExtra("router")).navigation(CheckGesturesPwdActivity.this, new ArouterNavCallback(CheckGesturesPwdActivity.this));
        } else {
            if (times > 1) {
                times--;
                SpUtils.put(KeyConstant.KEY_GESTURE_PWD_TIMES, times);
                tvError.setText(String.format(getString(R.string.mine_gestures_error_times), String.valueOf(times)));
                glView.showErrorStatus(400);
            } else {
                ToastUtils.showLongToast(R.string.mine_gestures_error_to_login);
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
            }
        }
    }

    @OnClick(R2.id.tv_pwd_login)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        //验证密码
        new InputLoginPwdDialog(CheckGesturesPwdActivity.this)
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

    public void openSelectDialog() {
        View view = LayoutInflater.from(CheckGesturesPwdActivity.this).inflate(R.layout.mine_dialog_gestures_select, null);
        TextView tvPwdLogin = view.findViewById(R.id.tv_pwd_login);
        TextView tvForgetGesturePwd = view.findViewById(R.id.tv_forget_gesture_pwd);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        final BottomSheetDialog dialog = DialogUtils.showDetailBottomDialog(CheckGesturesPwdActivity.this, view);
        dialog.show();

        tvPwdLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, "");
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<UserLoginInfo> userList = realm.where(UserLoginInfo.class).findAll();
                        userList.deleteAllFromRealm();
                    }
                });
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
                finish();
            }
        });
        tvForgetGesturePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, "");
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<UserLoginInfo> userList = realm.where(UserLoginInfo.class).findAll();
                        userList.deleteAllFromRealm();
                        RealmResults<UserGesturesPwdInfo> userGesturesPwdInfos = realm.where(UserGesturesPwdInfo.class).findAll();
                        userGesturesPwdInfos.deleteAllFromRealm();
                    }
                });
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
                finish();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
