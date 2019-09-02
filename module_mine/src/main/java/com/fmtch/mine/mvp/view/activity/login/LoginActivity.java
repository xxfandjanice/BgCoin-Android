package com.fmtch.mine.mvp.view.activity.login;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;


import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = RouterMap.ACCOUNT_LOGIN)
public class LoginActivity extends BaseActivity {
    //输入账号
    @BindView(R2.id.et_account)
    EditText etAccount;
    //输入密码
    @BindView(R2.id.et_password)
    EditText etPassword;
    @BindView(R2.id.iv_pwd_eye)
    ImageView ivPwdEye;
    @BindView(R2.id.line_account)
    View lineAccount;
    @BindView(R2.id.line_password)
    View linePassword;
    @BindView(R2.id.btn_login)
    Button btnLogin;

    private boolean is_show = false;//密码默认隐藏
    private String account;//账号
    private String password;//密码

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        if (!TextUtils.isEmpty(SpUtils.get(KeyConstant.KEY_LOGIN_ACCOUNT, "") + "")) {
            etAccount.setText(SpUtils.get(KeyConstant.KEY_LOGIN_ACCOUNT, "") + "");
        }
    }

    /**
     * 点击注册
     */
    @OnClick(R2.id.tv_rapid_register)
    public void onRapidRegisterClick() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        ARouter.getInstance().build(RouterMap.REGISTER).navigation();
    }

    /**
     * 点击关闭
     */
    @OnClick(R2.id.iv_close)
    public void onIvCloseClick() {
        finish();
    }

    /**
     * 点击忘记密码
     */
    @OnClick(R2.id.tv_forget_password)
    public void onForgetPasswordClick() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        ARouter.getInstance().build(RouterMap.FORGET_PASSWORD).navigation();
    }


    /**
     * 密码显示隐藏
     */
    @OnClick(R2.id.iv_pwd_eye)
    public void onPwdEyeClick() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (is_show) {
            is_show = false;
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivPwdEye.setImageResource(R.drawable.mine_ic_eye_close);
        } else {
            is_show = true;
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivPwdEye.setImageResource(R.drawable.mine_ic_eye_open);
        }
    }

    /**
     * 点击登录按钮
     */
    @OnClick(R2.id.btn_login)
    public void onLoginClick() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (!StringUtils.isPhoneNum(account) && !StringUtils.isEmail(account)) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_mobile_or_email);
        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_password);
        } else {
            SuperRequest superRequest = new SuperRequest();
            String api;
            if (StringUtils.isPhoneNum(account)) {
                api = API.LOGIN_MOBILE;
                superRequest.setArea("86");
                superRequest.setMobile(account);
            } else {
                api = API.LOGIN_EMAIL;
                superRequest.setEmail(account);
            }
            superRequest.setLogin_password(password);
            RequestUtil.requestPost(api, superRequest, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    if (response != null) {
                        if (TextUtils.isEmpty(response.getToken())) {
                            //登录二次验证
                            int tfa_type = response.getTfa_type() == 1 ? 1 : 7;//如果不是手机验证码校验就为谷歌验证码校验
                            checkSecond(response.getLogin_token(), tfa_type, response.getMobile(), response.getArea());
                        } else {
                            //登录成功
                            SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, response.getToken());
                            SpUtils.put(KeyConstant.KEY_LOGIN_ACCOUNT, account);
                            EventBus.getDefault().post(new EventBean<>(EventType.USER_LOGIN));
                            ARouter.getInstance().build(RouterMap.MAIN_PAGE).navigation();
                        }
                    }
                }
            }, LoginActivity.this, true, getString(R.string.mine_loading_login));
        }
    }

    //登录二次验证
    private void checkSecond(final String login_token, final int tfa_type, String mobile, String area) {
        new SecondCheckDialog(this)
                .setMobileInfo(mobile, area)
                .setTyfType(tfa_type)
                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(SuperRequest request) {
                        //二次验证输入完成
                        request.setLogin_token(login_token);
                        request.setTfa_type(tfa_type == 1 ? 1 : 2);
                        RequestUtil.requestPost(API.LOGIN_TWICE_CHECK, request, new OnResponseListenerImpl() {
                            @Override
                            public void onNext(SuperResponse response) {
                                super.onNext(response);
                                if (response != null && !TextUtils.isEmpty(response.getToken())) {
                                    //登录成功
                                    SpUtils.put(KeyConstant.KEY_LOGIN_ACCOUNT, account);
                                    SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, response.getToken());
                                    EventBus.getDefault().post(new EventBean<>(EventType.USER_LOGIN));
                                    ARouter.getInstance().build(RouterMap.MAIN_PAGE).navigation();
                                }
                            }
                        }, LoginActivity.this, true, getString(R.string.mine_loading_login));
                    }
                })
                .builder()
                .show();
    }


    @Override
    protected void initEvent() {
        super.initEvent();
        etAccount.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            account = etAccount.getEditableText().toString();
            password = etPassword.getEditableText().toString();
            if (StringUtils.isEmpty(account)) {
                lineAccount.setBackgroundResource(R.color.cl_f2f2f2);
            } else {
                lineAccount.setBackgroundResource(R.color.theme);
            }
            if (StringUtils.isEmpty(password)) {
                linePassword.setBackgroundResource(R.color.cl_f2f2f2);
            } else {
                linePassword.setBackgroundResource(R.color.theme);
            }
            if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
                btnLogin.setEnabled(false);
            } else {
                btnLogin.setEnabled(true);
            }
        }
    };

}
