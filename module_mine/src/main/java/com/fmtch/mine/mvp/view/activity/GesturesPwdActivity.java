package com.fmtch.mine.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.pojo.UserGesturesPwdInfo;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.base.widget.dialog.InputLoginPwdDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.GESTURES_PWD)
public class GesturesPwdActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.switch_gestures)
    Switch switchGestures;
    @BindView(R2.id.switch_gestures_track)
    Switch switchGesturesTrack;

    private Realm mRealm;
    private UserGesturesPwdInfo mUserGesturesPwdInfo;
    private int user_id;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_gestures;
    }

    /**
     * 设置手势密码
     */
    @OnClick(R2.id.tv_set_gestures_pwd)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        new InputLoginPwdDialog(GesturesPwdActivity.this)
                .setOnConfirmSuccessListener(new InputLoginPwdDialog.OnConfirmSuccessListener() {
                    @Override
                    public void onConfirmSuccess() {
                        ARouter.getInstance().build(RouterMap.GESTURES_PWD_SETTING).navigation();
                    }
                })
                .builder()
                .show();
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
    protected void onResume() {
        super.onResume();
        refreshGestureStatus();
    }

    private void refreshGestureStatus() {
        mRealm = Realm.getDefaultInstance();
        UserLoginInfo mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            user_id = mUserLoginInfo.getId();
        }
        mUserGesturesPwdInfo = mRealm.where(UserGesturesPwdInfo.class)
                .equalTo("id", user_id)
                .findFirst();
        if (mUserGesturesPwdInfo != null) {
            switchGestures.setChecked(mUserGesturesPwdInfo.getGestures_pwd_status());
            switchGesturesTrack.setChecked(mUserGesturesPwdInfo.getGestures_pwd_track_status());
        }
    }


    @Override
    protected void initEvent() {
        super.initEvent();
        switchGestures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInputDiaolog();
            }
        });
        switchGesturesTrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //先查找后得到User对象
                        UserGesturesPwdInfo userGesturesPwdInfo = realm.where(UserGesturesPwdInfo.class)
                                .equalTo("id", user_id)
                                .findFirst();
                        if (userGesturesPwdInfo == null) {
                            userGesturesPwdInfo = realm.createObject(UserGesturesPwdInfo.class, user_id);
                        }
                        userGesturesPwdInfo.setGestures_pwd_track_status(isChecked);
                        realm.copyToRealmOrUpdate(userGesturesPwdInfo);
                    }
                });
            }
        });
    }

    //开启关闭手势密码是校验登录密码
    private void openInputDiaolog() {
        new InputLoginPwdDialog(GesturesPwdActivity.this)
                .setOnCancelListener(new InputLoginPwdDialog.OnCancelListener() {
                    @Override
                    public void OnCancel() {
                        //还原状态
                        if (switchGestures.isChecked()) {
                            switchGestures.setChecked(false);
                        } else {
                            switchGestures.setChecked(true);
                        }
                    }
                })
                .setOnConfirmSuccessListener(new InputLoginPwdDialog.OnConfirmSuccessListener() {
                    @Override
                    public void onConfirmSuccess() {
                        if (switchGestures.isChecked()) {
                            //开启手势 跳转设置手势密码
                            ARouter.getInstance().build(RouterMap.GESTURES_PWD_SETTING).navigation();
                        } else {
                            //关闭手势密码
                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    UserGesturesPwdInfo userGesturesPwdInfo = realm.where(UserGesturesPwdInfo.class)
                                            .equalTo("id", user_id)
                                            .findFirst();
                                    if (userGesturesPwdInfo != null){
                                        userGesturesPwdInfo.setGestures_pwd_status(false);
                                        realm.copyToRealmOrUpdate(userGesturesPwdInfo);
                                    }
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    ToastUtils.showLongToast(R.string.mine_modify_success);
                                }
                            });
                        }
                    }
                })
                .builder()
                .show();
    }

}
