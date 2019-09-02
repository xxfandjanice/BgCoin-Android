package com.fmtch.module_bourse.ui.trade.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.TimeCount;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.ui.trade.model.AddPaymentModel;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.ADD_PAYMENT, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class AddPaymentActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.et_true_name_bank)
    EditText etTrueNameBank;
    @BindView(R2.id.et_bank_card_no)
    EditText etBankCardNo;
    @BindView(R2.id.et_create_bank)
    EditText etCreateBank;
    @BindView(R2.id.et_create_bank_branch)
    EditText etCreateBankBranch;
    @BindView(R2.id.ll_bank)
    LinearLayout llBank;
    @BindView(R2.id.et_true_name_zfb)
    EditText etTrueNameZfb;
    @BindView(R2.id.et_zfb_account)
    EditText etZfbAccount;
    @BindView(R2.id.ll_zfb)
    LinearLayout llZfb;
    @BindView(R2.id.et_wechat_nick)
    EditText etWechatNick;
    @BindView(R2.id.et_wechat_account)
    EditText etWechatAccount;
    @BindView(R2.id.ll_wechat)
    LinearLayout llWechat;
    @BindView(R2.id.et_code)
    EditText etCode;
    @BindView(R2.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R2.id.tv_receive)
    TextView tvReceive;
    @BindView(R2.id.tv_upload_receive)
    TextView tvUploadReceive;
    @BindView(R2.id.iv_receive_qr_code)
    ImageView ivReceiveQrCode;
    @BindView(R2.id.ll_receive)
    LinearLayout llReceive;
    @BindView(R2.id.tv_bind)
    TextView tvBind;

    @Autowired
    int mPayMentWay;//0:银行卡   1：支付宝  2：微信

    private TimeCount time;
    private String upload_url;//上传图片路径

    private AddPaymentModel model;

    private static final int IMAGE_PICKER_REQUEST_CODE = 0x1000;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_payment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        model = new AddPaymentModel(this);
        mPayMentWay = getIntent().getIntExtra(PageConstant.TYPE, 0);//0:银行卡   1：支付宝  2：微信
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
        //0:银行卡   1：支付宝  2：微信
        switch (mPayMentWay) {
            case 0:
                tvTitle.setText(R.string.bind_bank_card);
                llBank.setVisibility(View.VISIBLE);
                llZfb.setVisibility(View.GONE);
                llWechat.setVisibility(View.GONE);
                llReceive.setVisibility(View.GONE);
                break;
            case 1:
                tvTitle.setText(R.string.set_zfb);
                llBank.setVisibility(View.GONE);
                llZfb.setVisibility(View.VISIBLE);
                llWechat.setVisibility(View.GONE);
                llReceive.setVisibility(View.VISIBLE);
                tvReceive.setText(R.string.zfb_receive_qr_code);
                tvUploadReceive.setText(R.string.upload_zfb_receive_qr_code);
                break;
            case 2:
                tvTitle.setText(R.string.set_wechat_pay);
                llBank.setVisibility(View.GONE);
                llZfb.setVisibility(View.GONE);
                llWechat.setVisibility(View.VISIBLE);
                llReceive.setVisibility(View.VISIBLE);
                tvReceive.setText(R.string.wechat_receive_qr_code);
                tvUploadReceive.setText(R.string.upload_wechat_receive_qr_code);
                break;
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        etTrueNameBank.addTextChangedListener(textWatcher);
        etBankCardNo.addTextChangedListener(textWatcher);
        etCreateBank.addTextChangedListener(textWatcher);
        etCreateBankBranch.addTextChangedListener(textWatcher);
        etTrueNameZfb.addTextChangedListener(textWatcher);
        etZfbAccount.addTextChangedListener(textWatcher);
        etWechatNick.addTextChangedListener(textWatcher);
        etWechatAccount.addTextChangedListener(textWatcher);
        etCode.addTextChangedListener(textWatcher);
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
           refreshInputState();
        }
    };

    @OnClick({R2.id.tv_get_code, R2.id.rl_receive, R2.id.tv_bind})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_get_code) {
            //获取短信验证码
            RequestUtil.requestGet(API.SEND_SMS_CODE, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    ToastUtils.showLongToast(com.fmtch.base.R.string.send_success);
                    if (time == null)
                        time = new TimeCount(60000, 1000, tvGetCode);
                    time.start();
                }
            }, this, true, getResources().getString(com.fmtch.base.R.string.loading_checking));
        } else if (id == R.id.rl_receive) {
            ImagePicker imagePicker = ImagePicker.getInstance();
            imagePicker.setCrop(false);
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
        } else if (id == R.id.tv_bind) {
            //0:银行卡   1：支付宝  2：微信
            switch (mPayMentWay) {
                case 0:
                    model.addPaymentBank(etTrueNameBank.getEditableText().toString(), etBankCardNo.getEditableText().toString(),
                            etCreateBank.getEditableText().toString(), etCreateBankBranch.getEditableText().toString(), etCode.getEditableText().toString());
                    break;
                case 1:
                    model.addPaymentZFB(etTrueNameZfb.getEditableText().toString(), etZfbAccount.getEditableText().toString(), upload_url, etCode.getEditableText().toString());
                    break;
                case 2:
                    model.addPaymentWechat(etWechatNick.getEditableText().toString(), etWechatAccount.getEditableText().toString(), upload_url, etCode.getEditableText().toString());
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照功能或者裁剪后返回
        if (data != null && requestCode == IMAGE_PICKER_REQUEST_CODE) {
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            final String path = images.get(images.size() - 1).path;
            RequestUtil.uploadFile(this, path, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    upload_url = response.getDownload_url();
                    refreshInputState();
                    GlideLoadUtils.getInstance().glideLoad(AddPaymentActivity.this, path, ivReceiveQrCode);
                }
            });
        }
    }

    //刷新输入状态判断
    private void refreshInputState(){
        if (TextUtils.isEmpty(etCode.getEditableText().toString())) {
            tvBind.setEnabled(false);
            return;
        }
        //0:银行卡   1：支付宝  2：微信
        switch (mPayMentWay) {
            case 0:
                if (TextUtils.isEmpty(etCreateBank.getEditableText().toString())) {
                    tvBind.setEnabled(false);
                    return;
                }
                if (TextUtils.isEmpty(etBankCardNo.getEditableText().toString())) {
                    tvBind.setEnabled(false);
                    return;
                }
                if (TextUtils.isEmpty(etTrueNameBank.getEditableText().toString())) {
                    tvBind.setEnabled(false);
                    return;
                }
                break;
            case 1:
                if (TextUtils.isEmpty(upload_url)) {
                    tvBind.setEnabled(false);
                    return;
                }
                if (TextUtils.isEmpty(etZfbAccount.getEditableText().toString())) {
                    tvBind.setEnabled(false);
                    return;
                }
                if (TextUtils.isEmpty(etTrueNameZfb.getEditableText().toString())) {
                    tvBind.setEnabled(false);
                    return;
                }
                break;
            case 2:
                if (TextUtils.isEmpty(upload_url)) {
                    tvBind.setEnabled(false);
                    return;
                }
                if (TextUtils.isEmpty(etWechatAccount.getEditableText().toString())) {
                    tvBind.setEnabled(false);
                    return;
                }
                if (TextUtils.isEmpty(etWechatNick.getEditableText().toString())) {
                    tvBind.setEnabled(false);
                    return;
                }
                break;
        }
        tvBind.setEnabled(true);
    }

}
