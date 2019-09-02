package com.fmtch.mine.mvp.view.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.TimeCount;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.mine.mvp.view.activity.login.RegisterActivity;
import com.fmtch.mine.mvp.view.activity.login.SelectCountryCode;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.BIND)
public class BindActivity extends BaseActivity {


    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.et_pwd)
    EditText etPwd;
    @BindView(R2.id.tv_area)
    TextView tvArea;
    @BindView(R2.id.et_mobile)
    EditText etMobile;
    @BindView(R2.id.ll_et_mobile)
    LinearLayout llEtMobile;
    @BindView(R2.id.et_email)
    EditText etEmail;
    @BindView(R2.id.ll_et_email)
    LinearLayout llEtEmail;
    @BindView(R2.id.et_code)
    EditText etCode;
    @BindView(R2.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R2.id.tv_explain)
    TextView tvExplain;

    private boolean isBindMobile = true;//默认绑定手机号

    private TimeCount time;
    private final static int REQUESTCODE = 1;

    private String login_pwd, account, code;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_bind;
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
        initMobileOrEmail();
    }

    /**
     * 初始化绑定手机号或邮箱
     */
    private void initMobileOrEmail() {
        isBindMobile = getIntent().getBooleanExtra("bind_mobile", true);
        if (isBindMobile) {
            //绑定手机号
            tvTitle.setText(getString(R.string.mine_bind_mobile));
            tvExplain.setText(R.string.mine_modify_bind_mobile_explain);
            llEtMobile.setVisibility(View.VISIBLE);
            llEtEmail.setVisibility(View.GONE);
        } else {
            //绑定邮箱
            tvTitle.setText(getString(R.string.mine_bind_email));
            tvExplain.setText(R.string.mine_modify_bind_email_explain);
            llEtMobile.setVisibility(View.GONE);
            llEtEmail.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 确定
     */
    @OnClick(R2.id.btn_confirm)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        login_pwd = etPwd.getEditableText().toString();
        if (isBindMobile)
            account = etMobile.getEditableText().toString();
        else
            account = etEmail.getEditableText().toString();
        code = etCode.getEditableText().toString();
        if (TextUtils.isEmpty(login_pwd)) {
            ToastUtils.showLongToast(R.string.mine_input_login_pass_word);
            return;
        }
        if (isBindMobile && !StringUtils.isPhoneNum(account)) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_mobile);
            return;
        }
        if (!isBindMobile && !StringUtils.isEmail(account)) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_email);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showLongToast(R.string.mine_input_code);
            return;
        }
        RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, new SuperRequest(), new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null) {
                    if (response.getTfa_type() == 0) {
                        bind(new SuperRequest());
                    } else {
                        new SecondCheckDialog(BindActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成
                                        bind(request);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, BindActivity.this, true, getString(R.string.mine_loading_checking));
    }

    //绑定手机号或邮箱
    private void bind(SuperRequest request) {
        String api;
        if (isBindMobile) {
            api = API.BIND_MOBILE;
            request.setMobile(account);
            request.setSms_code(code);
            request.setArea("86");
        } else {
            api = API.BIND_EMAIL;
            request.setEmail(account);
            request.setEmail_code(code);
        }
        request.setLogin_password(login_pwd);
        RequestUtil.requestPost(api, request, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UserLoginInfo userLoginInfo = realm.where(UserLoginInfo.class).findFirst();
                        if (userLoginInfo != null) {
                            if (isBindMobile)
                                userLoginInfo.setMobile(account);
                            else
                                userLoginInfo.setEmail(account);
                            realm.copyToRealmOrUpdate(userLoginInfo);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        ToastUtils.showLongToast(R.string.mine_bind_success);
                        finish();
                    }
                });
            }
        }, BindActivity.this, true, getString(R.string.mine_loading_bind));
    }

    /**
     * 切换区号
     */
    @OnClick(R2.id.tv_area)
    public void onTvAreaClicked() {
//        if (ToastUtils.isFastClick()) {
//            return;
//        }
//        ARouter.getInstance().build(RouterMap.SELECT_COUNTRY_CODE).navigation(this, REQUESTCODE);
    }

    /**
     * 获取验证码
     */
    @OnClick(R2.id.tv_get_code)
    public void onTvGetCodeClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (isBindMobile)
            account = etMobile.getEditableText().toString();
        else
            account = etEmail.getEditableText().toString();
        if (isBindMobile && !StringUtils.isPhoneNum(account)) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_mobile);
            return;
        }
        if (!isBindMobile && !StringUtils.isEmail(account)) {
            ToastUtils.showLongToast(R.string.mine_please_input_true_email);
            return;
        }
        String api;
        if (isBindMobile) {
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
        }, BindActivity.this, true, getString(R.string.loading_checking));
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
