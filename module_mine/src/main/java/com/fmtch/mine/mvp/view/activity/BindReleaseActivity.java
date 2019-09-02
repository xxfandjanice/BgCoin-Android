package com.fmtch.mine.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
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

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.BIND_RELEASE)
public class BindReleaseActivity extends BaseActivity {


    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_explain)
    TextView tvExplain;
    @BindView(R2.id.et_pwd)
    EditText etPwd;
    @BindView(R2.id.btn_sure)
    Button btnSure;

    private boolean isUnBindMobile = true;//默认解绑定手机号

    private String login_pwd;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_bind_release;
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
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                login_pwd = etPwd.getEditableText().toString();
                if (TextUtils.isEmpty(login_pwd)) {
                    btnSure.setEnabled(false);
                }else {
                    btnSure.setEnabled(true);
                }
            }
        });
    }


    /**
     * 初始化解绑手机号或邮箱
     */
    private void initMobileOrEmail() {
        isUnBindMobile = getIntent().getBooleanExtra("bind_mobile", true);
        if (isUnBindMobile) {
            //解绑手机号
            tvTitle.setText(getString(R.string.mine_release_bind_mobile));
            tvExplain.setText(R.string.mine_modify_bind_mobile_explain);
        } else {
            //解绑邮箱
            tvTitle.setText(getString(R.string.mine_release_bind_email));
            tvExplain.setText(R.string.mine_modify_bind_email_explain);
        }
    }

    /**
     * 确定
     */
    @OnClick(R2.id.btn_sure)
    public void onBtnSureClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, new SuperRequest(), new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null) {
                    if (response.getTfa_type() == 0){
                        unBind(new SuperRequest());
                    }else {
                        new SecondCheckDialog(BindReleaseActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成
                                        unBind(request);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, BindReleaseActivity.this, true, getString(R.string.mine_loading_checking));
    }

    //解绑手机号或邮箱
    private void unBind(SuperRequest request) {
        String api;
        if (isUnBindMobile) {
            api = API.BIND_MOBILE;
        } else {
            api = API.BIND_EMAIL;
        }
        request.setLogin_password(login_pwd);
        RequestUtil.requestDelete(api, request, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                ToastUtils.showLongToast(R.string.mine_unbind_success);
                finish();
            }
        }, BindReleaseActivity.this, true, getString(R.string.mine_loading_unbind));
    }

}
