package com.fmtch.mine.mvp.view.activity.login;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.TimeCount;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.utils.UrlConstant;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;


import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.REGISTER)
public class RegisterActivity extends BaseActivity {
    @BindView(R2.id.et_code)
    EditText etCode;
    @BindView(R2.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R2.id.et_pwd)
    EditText etPwd;
    @BindView(R2.id.et_confirm_pwd)
    EditText etConfirmPwd;
    @BindView(R2.id.et_invite_code)
    EditText etInviteCode;
    @BindView(R2.id.tv_select)
    TextView tvSelect;
    @BindView(R2.id.tv_un_select)
    TextView tvUnSelect;
    @BindView(R2.id.et_mobile)
    EditText etMobile;
    @BindView(R2.id.tv_area)
    TextView tvArea;
    @BindView(R2.id.et_email)
    EditText etEmail;
    @BindView(R2.id.iv_checked)
    ImageView ivChecked;
    @BindView(R2.id.ll_et_mobile)
    LinearLayout llEtMobile;
    @BindView(R2.id.ll_et_email)
    LinearLayout llEtEmail;

    private Boolean is_mobile = true;//默认手机号注册
    private Boolean is_checked = false;//默认未同意协议

    private TimeCount time;
    private final static int REQUESTCODE = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_register;
    }


    @OnClick({R2.id.tv_login, R2.id.iv_close, R2.id.tv_un_select, R2.id.tv_get_code, R2.id.tv_use_protocol, R2.id.tv_privacy_clause, R2.id.btn_regist, R2.id.iv_checked, R2.id.tv_area})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_login) {
            //跳转登录
            finish();
        } else if (id == R.id.iv_close) {
            //关闭页面
            finish();
        } else if (id == R.id.tv_un_select) {
            //切换注册方式
            if (is_mobile) {
                is_mobile = false;
                tvSelect.setText(R.string.mine_email_register);
                tvUnSelect.setText(R.string.mine_mobile_register);
                llEtEmail.setVisibility(View.VISIBLE);
                llEtMobile.setVisibility(View.GONE);
            } else {
                is_mobile = true;
                tvSelect.setText(R.string.mine_mobile_register);
                tvUnSelect.setText(R.string.mine_email_register);
                llEtMobile.setVisibility(View.VISIBLE);
                llEtEmail.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_area) {
            //点击区号
//            ARouter.getInstance().build(RouterMap.SELECT_COUNTRY_CODE).navigation(this, REQUESTCODE);
        } else if (id == R.id.tv_get_code) {
            //获取验证码
            getCode();
        } else if (id == R.id.tv_use_protocol) {
            //服务协议
            ARouter.getInstance().build(RouterMap.WEB_VIEW).withString(PageConstant.TARGET_URL, UrlConstant.USE_AGREEMENT).navigation();
        } else if (id == R.id.tv_privacy_clause) {
            //隐私条款
            ARouter.getInstance().build(RouterMap.WEB_VIEW).withString(PageConstant.TARGET_URL, UrlConstant.PRIVACY_POLICY).navigation();
        } else if (id == R.id.iv_checked) {
            //同意协议
            if (is_checked) {
                is_checked = false;
                ivChecked.setImageResource(R.drawable.mine_icon_circle_normal);
            } else {
                is_checked = true;
                ivChecked.setImageResource(R.drawable.mine_icon_circle_checked);
            }
        } else if (id == R.id.btn_regist) {
            //注册
            String account;
            if (is_mobile)
                account = etMobile.getEditableText().toString().trim();
            else
                account = etEmail.getEditableText().toString().trim();
            String code = etCode.getEditableText().toString().trim();
            String password = etPwd.getEditableText().toString().trim();
            String confirmPassword = etConfirmPwd.getEditableText().toString().trim();
            String inviteCode = etInviteCode.getEditableText().toString().trim();
            if (is_mobile && !StringUtils.isPhoneNum(account)) {
                ToastUtils.showLongToast(R.string.mine_please_input_true_mobile);
            } else if (!is_mobile && !StringUtils.isEmail(account)) {
                ToastUtils.showLongToast(R.string.mine_please_input_true_email);
            } else if (StringUtils.isEmpty(code) || code.length() != 6) {
                ToastUtils.showLongToast(R.string.mine_please_input_true_code);
            } else if (TextUtils.isEmpty(password) || password.length() < 6) {
                ToastUtils.showLongToast(R.string.mine_please_input_true_pwd);
            } else if (StringUtils.isEmpty(confirmPassword) || !confirmPassword.equals(password)) {
                ToastUtils.showLongToast(R.string.mine_twice_input_difference);
            } else if (!is_checked) {
                ToastUtils.showLongToast(R.string.mine_please_agree_protocol);
            } else {
                SuperRequest superRequest = new SuperRequest();
                String api;
                if (is_mobile) {
                    api = API.REGISTER_MOBILE;
                    superRequest.setArea("86");
                    superRequest.setMobile(account);
                    superRequest.setSms_code(code);
                } else {
                    api = API.REGISTER_EMAIL;
                    superRequest.setEmail(account);
                    superRequest.setEmail_code(code);
                }
                superRequest.setLogin_password(password);
                superRequest.setParent_id(inviteCode);
                RequestUtil.requestPost(api, superRequest, new OnResponseListenerImpl() {
                    @Override
                    public void onNext(SuperResponse response) {
                        super.onNext(response);
                        ToastUtils.showLongToast(R.string.mine_register_success);
                        finish();
                    }
                }, RegisterActivity.this, true, getString(R.string.mine_loading_register));
            }
        }
    }

    //获取验证码
    private void getCode() {
        String account;
        if (is_mobile) {
            account = etMobile.getEditableText().toString().trim();
        } else {
            account = etEmail.getEditableText().toString().trim();
        }
        if (is_mobile && !StringUtils.isPhoneNum(account)) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_mobile);
            return;
        }
        if (!is_mobile && !StringUtils.isEmail(account)) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_email);
            return;
        }
        String api;
        if (is_mobile) {
            api = API.SEND_SMS_CODE + "?area=86&mobile=" + account;
        } else {
            api = API.SEND_EMAIL_CODE + "?email=" + account;
        }
        RequestUtil.requestGet(api, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                ToastUtils.showLongToast(R.string.send_success);
                if (time == null)
                    time = new TimeCount(60000, 1000, tvGetCode);
                time.start();
            }
        }, RegisterActivity.this, true, getString(R.string.loading_checking));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == SelectCountryCode.RESULTCODE
                && data != null && !TextUtils.isEmpty(data.getStringExtra("area"))) {
            tvArea.setText(data.getStringExtra("area"));
        }
    }

}
