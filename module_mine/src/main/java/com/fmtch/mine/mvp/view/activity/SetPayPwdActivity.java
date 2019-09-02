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
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.SET_ASSETS_PWD)
public class SetPayPwdActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_tile)
    TextView tvTitle;
    @BindView(R2.id.tv_old_pwd)
    TextView tvOldPwd;
    @BindView(R2.id.et_old_pwd)
    EditText etOldPwd;
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

    private String old_pwd, new_pwd, confirm_pwd;

    private boolean is_modify;//是否是修改资金密码

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_set_pay_pwd;
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
        is_modify = getIntent().getBooleanExtra("is_modify", false);
        if (is_modify) {
            //修改资金密码
            tvTitle.setText(R.string.mine_modify_assets_pwd);
            tvOldPwd.setText(R.string.mine_assets_password);
            etOldPwd.setHint(R.string.mine_please_input_assets_password);
        } else {
            //设置资金密码
            tvTitle.setText(R.string.mine_set_assets_pwd);
            tvOldPwd.setText(R.string.mine_login_pass_word);
            etOldPwd.setHint(R.string.mine_input_login_pass_word);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        etOldPwd.addTextChangedListener(textWatcher);
        etNewPwd.addTextChangedListener(textWatcher);
        etConfirmPwd.addTextChangedListener(textWatcher);
        cbOldPwdEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置为明文显示
                    etOldPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置为密文显示
                    etOldPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                //光标在最后显示
                etOldPwd.setSelection(etOldPwd.length());
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
            old_pwd = etOldPwd.getEditableText().toString();
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
            if (TextUtils.isEmpty(old_pwd)) {
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
                        setAssetsPwd(new SuperRequest());
                    } else {
                        new SecondCheckDialog(SetPayPwdActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成
                                        setAssetsPwd(request);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, SetPayPwdActivity.this, true, getString(R.string.mine_loading_checking));
    }

    private void setAssetsPwd(SuperRequest superRequest) {
        String api;
        if (is_modify) {
            api = API.MODIFY_PAY_PWD;
            superRequest.setPay_password(old_pwd);
            superRequest.setNew_pay_password(new_pwd);
        } else {
            api = API.SET_PAY_PWD;
            superRequest.setPay_password_type("0");//交易密码验证类型: 0:每次都验证；1:不验证；2:每两小时验证一次
            superRequest.setLogin_password(old_pwd);
            superRequest.setPay_password(new_pwd);
        }
        RequestUtil.requestPost(api, superRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UserLoginInfo userLoginInfo = realm.where(UserLoginInfo.class).findFirst();
                        if (userLoginInfo != null) {
                            userLoginInfo.setPay_password(1);
                            realm.copyToRealmOrUpdate(userLoginInfo);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        ToastUtils.showLongToast(R.string.mine_set_success);
                        finish();
                    }
                });
            }
        }, SetPayPwdActivity.this, true, getString(R.string.mine_loading_set));
    }

}
