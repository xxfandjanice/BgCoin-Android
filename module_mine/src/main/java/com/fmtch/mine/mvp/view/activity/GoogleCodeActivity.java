package com.fmtch.mine.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

@Route(path = RouterMap.SET_GOOGLE_CHECK)
public class GoogleCodeActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.et_login_pwd)
    EditText etLoginPwd;
    @BindView(R2.id.et_google_code)
    EditText etGoogleCode;
    @BindView(R2.id.ll_google_code)
    LinearLayout llGoogleCode;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;

    private boolean is_unbind;//是否是解绑谷歌验证码
    private String secret_key;//密钥
    private String login_pwd, google_code;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_google_code;
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
        is_unbind = getIntent().getBooleanExtra("is_unbind", false);
        secret_key = getIntent().getStringExtra("secret_key");
        if (is_unbind) {
            tvTitle.setText(R.string.mine_unbind_google_check);
            llGoogleCode.setVisibility(View.GONE);
        } else {
            tvTitle.setText(R.string.mine_bind_google_check);
            llGoogleCode.setVisibility(View.VISIBLE);
        }
        etLoginPwd.addTextChangedListener(textWatcher);
        etGoogleCode.addTextChangedListener(textWatcher);
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
            google_code = etGoogleCode.getEditableText().toString();
            if (TextUtils.isEmpty(login_pwd)) {
                btnConfirm.setEnabled(false);
                return;
            }
            if (!is_unbind && TextUtils.isEmpty(google_code)) {
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
        RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, new SuperRequest(), new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null) {
                    if (response.getTfa_type() == 0) {
                        setGoogleCheck(new SuperRequest());
                    } else {
                        new SecondCheckDialog(GoogleCodeActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成
                                        setGoogleCheck(request);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, GoogleCodeActivity.this, true, getString(R.string.mine_loading_checking));
    }

    //绑定或解绑谷歌验证
    private void setGoogleCheck(SuperRequest superRequest){
        superRequest.setLogin_password(login_pwd);
        if (is_unbind){
            //解绑
            RequestUtil.requestDelete(API.GOOGLE_CHECK,superRequest,new OnResponseListenerImpl(){
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    ToastUtils.showLongToast(R.string.mine_unbind_success);
                    finish();
                }
            }, GoogleCodeActivity.this, true, getString(R.string.mine_loading_unbind));
        }else {
            //绑定
            superRequest.setGoogle_secret(secret_key);
            superRequest.setGoogle_code(google_code);
            RequestUtil.requestPost(API.GOOGLE_CHECK,superRequest,new OnResponseListenerImpl(){
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    ToastUtils.showLongToast(R.string.mine_bind_success);
                    finish();
                }
            },GoogleCodeActivity.this, true, getString(R.string.mine_loading_bind));
        }

    }

}
