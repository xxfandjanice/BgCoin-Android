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
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.utils.UserInfoIntercept;
import com.fmtch.base.widget.dialog.InputPayPwdDialog;
import com.fmtch.base.widget.dialog.SecondCheckDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.CoinAddressBean;
import com.fmtch.module_bourse.bean.request.WithdrawRequest;
import com.fmtch.module_bourse.ui.property.model.BringCoinModel;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.BRING_COIN, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class BringCoinActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_coin_name)
    public TextView tvCoinName;
    @BindView(R2.id.et_address)
    EditText etAddress;
    @BindView(R2.id.et_note)
    EditText etNote;
    @BindView(R2.id.tv_bring_min_num)
    public TextView tvBringMinNum;
    @BindView(R2.id.et_num)
    public EditText etNum;
    @BindView(R2.id.tv_fee)
    public TextView tvFee;
    @BindView(R2.id.tv_explain_1)
    public TextView tvExplain1;
    @BindView(R2.id.et_tag)
    EditText etTag;
    @BindView(R2.id.ll_tag)
    public LinearLayout llTag;


    private static final int REQUEST_CODE_SCAN = 111;
    private static final int REQUEST_CODE_COIN = 222;
    private static final int REQUEST_CODE_ADDRESS = 333;

    private BringCoinModel model;

    public ArrayList<AccountBean> coinList;
    public AccountBean coinInfo;
    public CoinAddressBean coinAddressBean;

    private WithdrawRequest mRequest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bring_coin;
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
        updateView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        coinList = new ArrayList<>();
        model = new BringCoinModel(this);
        mRequest = new WithdrawRequest();
        if (getIntent().getSerializableExtra(PageConstant.COIN_INFO) != null)
            coinInfo = (AccountBean) getIntent().getSerializableExtra(PageConstant.COIN_INFO);
        else
            model.getCoinList();
    }

    //刷新币种信息
    private void updateView() {
        if (coinInfo != null && coinInfo.getCoin() != null) {
            AccountBean.Coin coin = coinInfo.getCoin();
            if (!TextUtils.isEmpty(coin.getName())) {
                tvCoinName.setText(coin.getName());
            }
            tvBringMinNum.setText(NumberUtils.getFormatMoney(coin.getWithdraw_min()));
            String etNumHint = String.format(getString(R.string.now_withdraw_num), NumberUtils.getFormatMoney(coinInfo.getAvailable()), coin.getName());
            StringUtils.setTextHintStyle(BringCoinActivity.this, etNum, etNumHint, R.color.theme, 4, etNumHint.length());
            tvFee.setText(NumberUtils.getFormatMoney(coin.getWithdraw_fee()));
            if (!TextUtils.isEmpty(coin.getWithdraw_prompt())) {
                tvExplain1.setText(coin.getWithdraw_prompt().replace("\\n", "\n"));
            }
            if (coin.getIs_tag() == 1) {
                llTag.setVisibility(View.VISIBLE);
            } else {
                llTag.setVisibility(View.GONE);
            }
        }
    }


    //提币记录
    @OnClick(R2.id.iv_records)
    public void onViewClicked() {
        ARouter.getInstance().build(RouterMap.TRANSFER_COIN_RECORDS)
                .withInt(PageConstant.TYPE, 2)
                .withSerializable(PageConstant.COIN_LIST, coinList)
                .withSerializable(PageConstant.COIN_INFO, coinInfo)
                .navigation();
    }

    //选择币种
    @OnClick(R2.id.tv_select_coin)
    public void onTvSelectCoinClicked() {
        ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                .withInt(PageConstant.TYPE, 2)
                .withSerializable(PageConstant.COIN_LIST, coinList)
                .navigation(this, REQUEST_CODE_COIN);
    }

    //扫描二维码
    @OnClick(R2.id.iv_scan)
    public void onIvScanClicked() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    //地址本选择
    @OnClick(R2.id.iv_address_book)
    public void onIvAddressBookClicked() {
        if (coinInfo != null) {
            ARouter.getInstance().build(RouterMap.COIN_ADDRESS_BOOK)
                    .withSerializable(PageConstant.COIN_INFO, coinInfo)
                    .withSerializable(PageConstant.COIN_ADDRESS_INFO, coinAddressBean)
                    .navigation(this, REQUEST_CODE_ADDRESS);
        }
    }

    //全部
    @OnClick(R2.id.tv_total)
    public void onTvTotalClicked() {
        etNum.setText(NumberUtils.getFormatMoney(coinInfo.getAvailable()));
    }

    //确定
    @OnClick(R2.id.tv_sure)
    public void onTvSureClicked() {
        if (coinInfo == null) {
            ToastUtils.showLongToast(R.string.please_select_recharge_coin);
            return;
        }
        String address = etAddress.getEditableText().toString();
        String note = etNote.getEditableText().toString();
        String number = etNum.getEditableText().toString();
        String tag = etTag.getEditableText().toString();
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showLongToast(R.string.please_input_address);
            return;
        }
        if (TextUtils.isEmpty(number)) {
            ToastUtils.showLongToast(R.string.please_input_withdraw_num);
            return;
        }
        if (new BigDecimal(number).compareTo(new BigDecimal(coinInfo.getCoin().getWithdraw_min())) == -1) {
            ToastUtils.showLongToast(String.format(getString(R.string.withdraw_explain_1), NumberUtils.getFormatMoney(coinInfo.getCoin().getWithdraw_min()), coinInfo.getCoin().getName()));
            return;
        }
        if (new BigDecimal(number).compareTo(new BigDecimal(coinInfo.getAvailable())) == 1) {
            ToastUtils.showLongToast(etNum.getHint().toString());
            return;
        }
//        UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
//        if (userLoginInfo.getPay_password() != 1) {
//            ToastUtils.showLongToast(R.string.please_set_pay_password);
//            return;
//        }
        if (UserInfoIntercept.havePayPwd(this)) {
            mRequest.setCoin_id(coinInfo.getCoin_id());
            mRequest.setAddress(address);
            mRequest.setNumber(number);
            mRequest.setNote(note);
            mRequest.setTag(tag);
            //打开输入密码框
            new InputPayPwdDialog(this)
                    .setOnConfirmSuccessListener(new InputPayPwdDialog.OnConfirmSuccessListener() {
                        @Override
                        public void onConfirmSuccess(String pay_pwd) {
                            mRequest.setPay_password(pay_pwd);
                            //开启二次验证
                            openSecondCheck();
                        }
                    })
                    .builder()
                    .show();
        }
    }

    //开启二次验证
    private void openSecondCheck() {
        RequestUtil.requestPost(API.GET_SECOND_CHECK_TYPE, new SuperRequest(), new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (response != null) {
                    if (response.getTfa_type() == 0) {
                        model.withdraw(mRequest);
                    } else {
                        new SecondCheckDialog(BringCoinActivity.this)
                                .setTyfType(response.getTfa_type())
                                .setOnConfirmListener(new SecondCheckDialog.OnConfirmListener() {
                                    @Override
                                    public void onConfirm(SuperRequest request) {
                                        //二次验证输入完成

                                        if (!TextUtils.isEmpty(mRequest.getPay_password())) {
                                            //保存支付密码校验成功时间
                                            SpUtils.put(KeyConstant.KEY_PAY_PWD_SUCCESS_TIME, System.currentTimeMillis());
                                        }

                                        mRequest.setSms_code(request.getSms_code());
                                        mRequest.setEmail_code(request.getEmail_code());
                                        mRequest.setGoogle_code(request.getGoogle_code());
                                        model.withdraw(mRequest);
                                    }
                                })
                                .builder()
                                .show();
                    }
                }
            }
        }, BringCoinActivity.this, true, getString(R.string.loading_checking));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == REQUEST_CODE_SCAN) {
            //处理扫描结果（在界面上显示）
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
        } else if (requestCode == REQUEST_CODE_COIN && data.getSerializableExtra(PageConstant.COIN_INFO) != null) {
            coinInfo = (AccountBean) data.getSerializableExtra(PageConstant.COIN_INFO);
            etAddress.setText("");
            coinAddressBean = null;
            updateView();
        } else if (requestCode == REQUEST_CODE_ADDRESS && data.getSerializableExtra(PageConstant.COIN_ADDRESS_INFO) != null) {
            coinAddressBean = (CoinAddressBean) data.getSerializableExtra(PageConstant.COIN_ADDRESS_INFO);
            if (!TextUtils.isEmpty(coinAddressBean.getAddress())) {
                etAddress.setText(coinAddressBean.getAddress());
            }
        }
    }

}
