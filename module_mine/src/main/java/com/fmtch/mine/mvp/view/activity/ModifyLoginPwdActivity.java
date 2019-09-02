package com.fmtch.mine.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.MODIFY_LOGIN_PWD)
public class ModifyLoginPwdActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.et_login_pwd)
    EditText etLoginPwd;
    @BindView(R2.id.et_new_pwd)
    EditText etNewPwd;
    @BindView(R2.id.et_confirm_pwd)
    EditText etConfirmPwd;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;
    @BindView(R2.id.cb_old_pwd_eye)
    CheckBox cbOldPwdEye;
    @BindView(R2.id.cb_new_pwd_eye)
    CheckBox cbNewPwdEye;
    @BindView(R2.id.cb_confirm_pwd_eye)
    CheckBox cbConfirmPwdEye;

    private String login_pwd, new_pwd, confirm_pwd;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_modify_login_pwd;
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
    protected void initEvent() {
        super.initEvent();
        etLoginPwd.addTextChangedListener(textWatcher);
        etNewPwd.addTextChangedListener(textWatcher);
        etConfirmPwd.addTextChangedListener(textWatcher);
        cbOldPwdEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置为明文显示
                    etLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置为密文显示
                    etLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                //光标在最后显示
                etLoginPwd.setSelection(etLoginPwd.length());
            }
        });
        cbNewPwdEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etNewPwd.setSelection(etNewPwd.length());
            }
        });
        cbConfirmPwdEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etConfirmPwd.setSelection(etConfirmPwd.length());
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            login_pwd = etLoginPwd.getEditableText().toString();
            new_pwd = etNewPwd.getEditableText().toString();
            confirm_pwd = etConfirmPwd.getEditableText().toString();
            if (TextUtils.isEmpty(confirm_pwd)) {
                btnConfirm.setEnabled(false);
                return;
            }
            if (TextUtils.isEmpty(new_pwd)) {
                btnConfirm.setEnabled(false);
                return;
            }
            if (TextUtils.isEmpty(login_pwd)) {
                btnConfirm.setEnabled(false);
                return;
            }
            btnConfirm.setEnabled(true);
        }
    };

    @OnClick(R2.id.btn_confirm)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (new_pwd.length() < 6) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_pwd);
            return;
        }
        if (!TextUtils.equals(new_pwd, confirm_pwd)) {
            ToastUtils.showLongToast(R.string.mine_twice_input_difference);
            return;
        }
        RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, new SuperRequest(), new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null) {
                    if (response.getTfa_type() == 0) {
                        modifyLoginPwd(new SuperRequest());
                    } else {
                        new SecondCheckDialog(ModifyLoginPwdActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成
                                        modifyLoginPwd(request);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, ModifyLoginPwdActivity.this, true, getString(R.string.mine_loading_checking));
    }

    private void modifyLoginPwd(SuperRequest superRequest) {
        superRequest.setLogin_password(login_pwd);
        superRequest.setNew_login_password(new_pwd);
        RequestUtil.requestPost(API.MODIFY_LOGIN_PWD, superRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                ToastUtils.showLongToast(R.string.mine_modify_success);
                EventBus.getDefault().post(new EventBean<>(EventType.USER_EXIT));
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
                finish();
            }
        }, ModifyLoginPwdActivity.this, true, getString(R.string.mine_loading_set));
    }

}
