package com.fmtch.mine.mvp.view.activity.login;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.FORGET_PASSWORD)
public class ForgetPasswordActivity extends BaseActivity {
    //标题栏
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.iv_status_second)
    ImageView ivStatusSecond;
    @BindView(R2.id.view_status_second)
    View viewStatusSecond;
    @BindView(R2.id.iv_status_third)
    ImageView ivStatusThird;
    @BindView(R2.id.view_status_third)
    View viewStatusThird;
    @BindView(R2.id.ll_account)
    LinearLayout llAccount;
    @BindView(R2.id.et_account)
    EditText etAccount;
    @BindView(R2.id.ll_pwd)
    LinearLayout llPwd;
    @BindView(R2.id.et_pwd)
    EditText etPwd;
    @BindView(R2.id.et_confirm_pwd)
    EditText etConfirmPwd;

    private int step = 1;//验证第一步
    private SuperRequest mSuperRequest;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_forget_password;
    }

    @Override
    protected void initView() {
        super.initView();
        //初始化标题栏
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSuperRequest = new SuperRequest();
    }

    /**
     * 点击确定
     */
    @OnClick(R2.id.btn_next)
    public void onBtnNextClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        switch (step) {
            case 1:
                //验证第一步判断用户是否已注册
                String account = etAccount.getEditableText().toString();
                if (TextUtils.isEmpty(account)) {
                    ToastUtils.showLongToast(R.string.mine_please_input_account);
                    return;
                }
                mSuperRequest.setLogin_name(account);
                checkFirst();
                break;
            case 2:
                //验证第二步
                checkSecond();
                break;
            case 3:
                //验证第三步
                break;
        }

    }

    //校验第一步
    private void checkFirst() {
        RequestUtil.requestPost(API.CHECK_ACCOUNT, mSuperRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null && response.getType() == 1) {
                    step = 2;
                    ivStatusSecond.setImageResource(R.drawable.mine_status_find_pwd_second_success);
                    viewStatusSecond.setBackgroundResource(R.color.theme);
                    llAccount.setVisibility(View.GONE);
                    llPwd.setVisibility(View.VISIBLE);
                } else {
                    ToastUtils.showLongToast(R.string.mine_account_un_register);
                }
            }
        }, ForgetPasswordActivity.this, true, getString(R.string.mine_loading_checking));
    }

    //校验第二步
    private void checkSecond() {
        String password = etPwd.getEditableText().toString();
        String confirmPassword = etConfirmPwd.getEditableText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_pwd);
        } else if (StringUtils.isEmpty(confirmPassword) || !confirmPassword.equals(password)) {
            ToastUtils.showLongToast(R.string.mine_twice_input_difference);
        } else {
            RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, mSuperRequest, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    if (response != null) {
                        step = 3;
                        ivStatusThird.setImageResource(R.drawable.mine_status_find_pwd_third_success);
                        viewStatusThird.setBackgroundResource(R.color.theme);
                        new SecondCheckDialog(ForgetPasswordActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setAccount(mSuperRequest.getLogin_name())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest data) {
                                        //二次验证输入完成
                                        checkThird(data);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }, ForgetPasswordActivity.this, true, getString(R.string.mine_loading_checking));
        }
    }

    //校验第三步
    private void checkThird(SuperRequest superRequest) {
        String api;
        if (StringUtils.isPhoneNum(mSuperRequest.getLogin_name())) {
            api = API.FIND_LOGIN_PWD_MOBILE;
            superRequest.setMobile(mSuperRequest.getLogin_name());
        } else {
            api = API.FIND_LOGIN_PWD_EMAIL;
            superRequest.setEmail(mSuperRequest.getLogin_name());
        }
        superRequest.setLogin_password(etPwd.getEditableText().toString());
        RequestUtil.requestPost(api, superRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                ToastUtils.showLongToast(R.string.mine_reset_success);
                finish();
            }
        }, ForgetPasswordActivity.this, true, getString(R.string.mine_loading_reset));
    }

}
