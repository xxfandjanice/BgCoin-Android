package com.fmtch.mine.mvp.view.activity;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.imageloader.CircleTransformation;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserGesturesPwdInfo;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.base.widget.dialog.InputLoginPwdDialog;
import com.fmtch.base.utils.DialogUtils;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.SAFE_CENTER, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class SafeCenterActivity extends BaseActivity {

    private static final int IMAGE_PICKER_REQUEST_CODE = 0x1000;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.civ_user_icon)
    ImageView civUserIcon;
    @BindView(R2.id.tv_user_name)
    TextView tvUserName;
    @BindView(R2.id.tv_mobile)
    TextView tvMobile;
    @BindView(R2.id.tv_email)
    TextView tvEmail;
    @BindView(R2.id.tv_gestures_pwd)
    TextView tvGesturesPwd;
    @BindView(R2.id.tv_auth)
    TextView tvAuth;
    @BindView(R2.id.tv_login_pwd)
    TextView tvLoginPwd;
    @BindView(R2.id.tv_assets_pwd)
    TextView tvAssetsPwd;
    @BindView(R2.id.tv_assets_pwd_times)
    TextView tvAssetsPwdTimes;
    @BindView(R2.id.tv_finger_pwd)
    TextView tvFingerPwd;
    @BindView(R2.id.tv_google_check)
    TextView tvGoogleCheck;

    private Realm mRealm;
    private UserLoginInfo mUserLoginInfo;
    private UserGesturesPwdInfo mUserGesturesPwdInfo;
    private int user_id;


    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_safe_center;
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
        mRealm = Realm.getDefaultInstance();
        refreshUserView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upDateUserInfo();
    }

    private void upDateUserInfo() {
        RequestUtil.requestGet(API.GET_USER_INFO, new OnResponseListenerImpl() {
            @Override
            public void onNext(final SuperResponse response) {
                super.onNext(response);
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
//                        UserLoginInfo userLoginInfo = CombineBeans.combineSydwCore(UserLoginInfo.objectFromData(new Gson().toJson(response)), mUserLoginInfo);
                        UserLoginInfo userLoginInfo = UserLoginInfo.objectFromData(new Gson().toJson(response));
                        realm.copyToRealmOrUpdate(userLoginInfo);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        refreshUserView();
                    }
                });
            }
        }, this);
    }

    private void refreshUserView() {
        if (this.isDestroyed() || this.isFinishing()) {
            return;
        }
        mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            user_id = mUserLoginInfo.getId();
            GlideLoadUtils.getInstance().glideLoad(this, mUserLoginInfo.getAvatar(), civUserIcon, new CircleTransformation(this), R.drawable.icon_default_avatar);
            if (!TextUtils.isEmpty(mUserLoginInfo.getUsername())) {
                tvUserName.setText(mUserLoginInfo.getUsername());
            } else if (!TextUtils.isEmpty(mUserLoginInfo.getMobile())) {
                tvUserName.setText(mUserLoginInfo.getMobile());
            } else if (!TextUtils.isEmpty(mUserLoginInfo.getEmail())) {
                tvUserName.setText(mUserLoginInfo.getEmail());
            }
            tvMobile.setText(StringUtils.isEmpty(mUserLoginInfo.getMobile()) ? getString(R.string.mine_unbind) : mUserLoginInfo.getMobile());
            tvEmail.setText(StringUtils.isEmpty(mUserLoginInfo.getEmail()) ? getString(R.string.mine_unbind) : mUserLoginInfo.getEmail());
            tvAuth.setText(mUserLoginInfo.getKyc_status() == 1 ? getString(R.string.mine_already_auth) : getString(R.string.mine_un_auth));
            tvAssetsPwd.setText(mUserLoginInfo.getPay_password() == 1 ? getString(R.string.mine_modify) : getString(R.string.mine_unset));
            switch (mUserLoginInfo.getPay_password_type()) {
                // 0:每次都验证；1:不验证；2:每两小时验证一次
                case 0:
                    tvAssetsPwdTimes.setText(R.string.mine_pay_pwd_type_every_times);
                    break;
                case 1:
                    tvAssetsPwdTimes.setText(R.string.mine_pay_pwd_type_never);
                    break;
                case 2:
                    tvAssetsPwdTimes.setText(R.string.mine_pay_pwd_type_two_hour_once);
                    break;
                default:
                    tvAssetsPwdTimes.setText(R.string.mine_pay_pwd_type_every_times);
                    break;
            }
            tvGoogleCheck.setText(mUserLoginInfo.getGa_secret() == 1 ? getString(R.string.mine_already_bind) : getString(R.string.mine_unbind));
            mUserGesturesPwdInfo = mRealm.where(UserGesturesPwdInfo.class)
                    .equalTo("id", mUserLoginInfo.getId())
                    .findFirst();
            if (null != mUserGesturesPwdInfo) {
                //手势密码
                if (mUserGesturesPwdInfo.getGestures_pwd_status()) {
                    tvGesturesPwd.setText(R.string.mine_already_open);
                } else {
                    tvGesturesPwd.setText(R.string.mine_un_open);
                }
                //指纹密码
                if (mUserGesturesPwdInfo.getFinger_pwd_status()) {
                    tvFingerPwd.setText(R.string.mine_already_open);
                } else {
                    tvFingerPwd.setText(R.string.mine_un_open);
                }
            }
        }
    }

    @OnClick({R2.id.tv_avatar, R2.id.tv_user_name, R2.id.tv_mobile, R2.id.tv_email, R2.id.tv_auth,
            R2.id.tv_login_pwd, R2.id.tv_assets_pwd, R2.id.tv_assets_pwd_times, R2.id.tv_gestures_pwd, R2.id.tv_finger_pwd, R2.id.tv_google_check})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick() || mUserLoginInfo == null || !mUserLoginInfo.isValid()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_avatar) {
            //点击重新上传头像
            openSelectPhotoDialog();
        } else if (id == R.id.tv_user_name) {
            //点击用户名
            if (TextUtils.isEmpty(mUserLoginInfo.getUsername())) {
                ARouter.getInstance().build(RouterMap.SET_USER_NAME).navigation();
            }
        } else if (id == R.id.tv_mobile) {
            //点击绑定手机号
            if (StringUtils.isEmpty(mUserLoginInfo.getMobile())) {
                //手机为空进入绑定手机号
                ARouter.getInstance().build(RouterMap.BIND).withBoolean("bind_mobile", true).navigation();
            } else if (!StringUtils.isEmpty(mUserLoginInfo.getEmail())) {
                //手机和邮箱都不为空进入解绑手机号
                ARouter.getInstance().build(RouterMap.BIND_RELEASE).withBoolean("bind_mobile", true).navigation();
            }
        } else if (id == R.id.tv_email) {
            //点击绑定邮箱
            if (StringUtils.isEmpty(mUserLoginInfo.getEmail())) {
                //邮箱为空进入绑定邮箱
                ARouter.getInstance().build(RouterMap.BIND).withBoolean("bind_mobile", false).navigation();
            } else if (!StringUtils.isEmpty(mUserLoginInfo.getMobile())) {
                //手机和邮箱都不为空进入解绑手机号
                ARouter.getInstance().build(RouterMap.BIND_RELEASE).withBoolean("bind_mobile", false).navigation();
            }
        } else if (id == R.id.tv_auth) {
            //点击实名认证
            if (mUserLoginInfo.getKyc_status() == 0) {
                //未实名认证
                ARouter.getInstance().build(RouterMap.AUTH).navigation();
            } else {
                //进入实名认证状态
                ARouter.getInstance().build(RouterMap.AUTH_STATUS).navigation();
            }
        } else if (id == R.id.tv_login_pwd) {
            //点击登录密码
            ARouter.getInstance().build(RouterMap.MODIFY_LOGIN_PWD).navigation();
        } else if (id == R.id.tv_assets_pwd) {
            //点击资金密码
            ARouter.getInstance().build(RouterMap.SET_ASSETS_PWD)
                    .withBoolean("is_modify", mUserLoginInfo.getPay_password() == 1)
                    .navigation();
        } else if (id == R.id.tv_assets_pwd_times) {
            //点击资金密码时效
            openSelectPwdTimesDialog();
        } else if (id == R.id.tv_gestures_pwd) {
            //点击手势密码
            if (mUserGesturesPwdInfo != null && mUserGesturesPwdInfo.getFinger_pwd_status()) {
                //已开启手势密码
                ToastUtils.showLongToast(R.string.mine_open_gesture_pwd_explain);
                return;
            }
            ARouter.getInstance().build(RouterMap.GESTURES_PWD).navigation();
        } else if (id == R.id.tv_finger_pwd) {
            if (mUserGesturesPwdInfo != null && mUserGesturesPwdInfo.getGestures_pwd_status()) {
                //已开启手势密码
                ToastUtils.showLongToast(R.string.mine_open_finger_pwd_explain);
                return;
            }
            //点击指纹密码
            FingerprintManagerCompat fingerprint = FingerprintManagerCompat.from(this);   //v4包下的API，包装内部已经判断Android系统版本是否大于6.0，这也是官方推荐的方式
            // 判断设备是否支持指纹解锁
            if (!fingerprint.isHardwareDetected()) {
                ToastUtils.showLongToast(R.string.mine_nonsupport_finger_lock);
                return;
            }
            if (!fingerprint.hasEnrolledFingerprints()) {
                ToastUtils.showLongToast(R.string.mine_please_open_system_finger_lock);
                return;
            }
            //验证密码后开启指纹解锁
            new InputLoginPwdDialog(SafeCenterActivity.this)
                    .setOnConfirmSuccessListener(new InputLoginPwdDialog.OnConfirmSuccessListener() {
                        @Override
                        public void onConfirmSuccess() {
                            //密码验证成功
                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    UserGesturesPwdInfo userGesturesPwdInfo = realm.where(UserGesturesPwdInfo.class)
                                            .equalTo("id", user_id)
                                            .findFirst();
                                    if (userGesturesPwdInfo != null) {
                                        userGesturesPwdInfo.setFinger_pwd_status(!userGesturesPwdInfo.getFinger_pwd_status());
                                        realm.copyToRealmOrUpdate(userGesturesPwdInfo);
                                    }
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    ToastUtils.showLongToast(R.string.mine_set_success);
                                    refreshUserView();
                                }
                            });
                        }
                    })
                    .builder()
                    .show();
        } else if (id == R.id.tv_google_check) {
            //点击谷歌验证
            if (mUserLoginInfo.getGa_secret() == 1) {
                ARouter.getInstance().build(RouterMap.SET_GOOGLE_CHECK)
                        .withBoolean("is_unbind", true)
                        .navigation();
            } else {
                ARouter.getInstance().build(RouterMap.GOOGLE_CHECK).navigation();
            }
        }
    }

    //打开资金密码时效选择弹窗
    public void openSelectPwdTimesDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_pay_pwd_check_time, null);
        final TextView tvfirst = view.findViewById(R.id.tv_first);
        TextView tvSecond = view.findViewById(R.id.tv_second);
        TextView tvThird = view.findViewById(R.id.tv_third);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        switch (mUserLoginInfo.getPay_password_type()) {
            case 0:
                tvfirst.setTextColor(getResources().getColor(R.color.theme));
                tvSecond.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvThird.setTextColor(getResources().getColor(R.color.color_common_text99));
                break;
            case 1:
                tvfirst.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvSecond.setTextColor(getResources().getColor(R.color.theme));
                tvThird.setTextColor(getResources().getColor(R.color.color_common_text99));
                break;
            case 2:
                tvfirst.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvSecond.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvThird.setTextColor(getResources().getColor(R.color.theme));
                break;
        }
        final BottomSheetDialog mDialog = DialogUtils.showDetailBottomDialog(this, view);
        tvfirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                checkSecond(0);
            }
        });
        tvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                checkSecond(1);
            }
        });
        tvThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                checkSecond(2);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    //修改交易密码验证时效二次验证
    private void checkSecond(final int type) {
        RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, new SuperRequest(), new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null) {
                    if (response.getTfa_type() == 0) {
                        modifyPayPwdCheckTimes(new SuperRequest());
                    } else {
                        new SecondCheckDialog(SafeCenterActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成
                                        request.setPay_password_type(String.valueOf(type));
                                        modifyPayPwdCheckTimes(request);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, SafeCenterActivity.this, true, getString(R.string.mine_loading_checking));
    }

    //修改交易密码验证时效
    private void modifyPayPwdCheckTimes(SuperRequest superRequest) {
        RequestUtil.requestPost(API.MODIFY_PAY_PWD_TYPE, superRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                //刷新支付密码校验成功时间
                SpUtils.put(KeyConstant.KEY_PAY_PWD_SUCCESS_TIME, 0L);

                upDateUserInfo();
                ToastUtils.showLongToast(R.string.mine_set_success);
            }
        }, SafeCenterActivity.this, true, getString(R.string.loading_set));
    }

    /**
     * 打开选择图片对话框
     */

    public void openSelectPhotoDialog() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setCrop(true);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照功能或者裁剪后返回
        if (data != null && requestCode == IMAGE_PICKER_REQUEST_CODE) {
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            String filePath = images.get(images.size() - 1).path;
            RequestUtil.uploadFile(this, filePath, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    uploadService(response.getDownload_url());
                }
            });
        }
    }

    //上传服务器
    private void uploadService(final String path) {
        SuperRequest superRequest = new SuperRequest();
        superRequest.setAvatar(path);
        RequestUtil.requestPost(API.UPLOAD_AVATAR, superRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                GlideLoadUtils.getInstance().glideLoad(SafeCenterActivity.this, path, civUserIcon, new CircleTransformation(SafeCenterActivity.this), R.drawable.icon_default_avatar);
                ToastUtils.showLongToast(R.string.mine_set_success);
                EventBus.getDefault().post(new EventBean<>(EventType.USER_LOGIN));
            }
        }, SafeCenterActivity.this, true, getString(R.string.loading_set));
    }
}
