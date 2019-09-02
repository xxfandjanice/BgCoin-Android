package com.fmtch.module_bourse.ui.property.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
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
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.request.AddCoinAddressRequest;
import com.fmtch.module_bourse.ui.property.model.AddCoinAddressModel;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.ADD_COIN_ADDRESS)
public class AddCoinAddressActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.et_address)
    EditText etAddress;
    @BindView(R2.id.et_note)
    EditText etNote;
    @BindView(R2.id.et_tag)
    EditText etTag;
    @BindView(R2.id.ll_tag)
    LinearLayout llTag;

    public AccountBean coinInfo;
    private AddCoinAddressModel model;
    private AddCoinAddressRequest mRequest;

    private static final int REQUEST_CODE_SCAN = 666;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_coin_address;
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
        if (coinInfo != null && coinInfo.getCoin() != null) {
            AccountBean.Coin coin = coinInfo.getCoin();
            if (!TextUtils.isEmpty(coin.getName())) {
                tvTitle.setText(String.format(getString(R.string.add_coin_address), coin.getName()));
            }
            if (coin.getIs_tag() == 1) {
                llTag.setVisibility(View.VISIBLE);
            } else {
                llTag.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mRequest = new AddCoinAddressRequest();
        model = new AddCoinAddressModel(this);
        if (getIntent().getSerializableExtra(PageConstant.COIN_INFO) != null) {
            coinInfo = (AccountBean) getIntent().getSerializableExtra(PageConstant.COIN_INFO);
            mRequest.setCoin_id(coinInfo.getCoin_id());
        }
    }

    //扫描二维码
    @OnClick(R2.id.iv_scan)
    public void onIvScanClicked() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    //确定
    @OnClick(R2.id.tv_sure)
    public void onTvSureClicked() {
        String address = etAddress.getEditableText().toString();
        String note = etNote.getEditableText().toString();
        String tag = etTag.getEditableText().toString();
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showLongToast(R.string.please_input_address);
            return;
        }
        if (TextUtils.isEmpty(note)) {
            ToastUtils.showLongToast(R.string.please_input_note);
            return;
        }
        mRequest.setAddress(address);
        mRequest.setNote(note);
        if (!TextUtils.isEmpty(tag)) {
            mRequest.setTag(tag);
        }
        RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, new SuperRequest(), new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null) {
                    if (response.getTfa_type() == 0) {
                        model.addCoinAddress(mRequest);
                    } else {
                        new SecondCheckDialog(AddCoinAddressActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成
                                        mRequest.setSms_code(request.getSms_code());
                                        mRequest.setEmail_code(request.getEmail_code());
                                        mRequest.setGoogle_code(request.getGoogle_code());
                                        model.addCoinAddress(mRequest);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, AddCoinAddressActivity.this, true, getString(R.string.loading_checking));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    etAddress.setText(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtils.showLongToast(R.string.qr_code_error);
                }
            }
        }
    }

}
