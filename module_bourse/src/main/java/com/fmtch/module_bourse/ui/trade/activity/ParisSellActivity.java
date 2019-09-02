package com.fmtch.module_bourse.ui.trade.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.InputPwdDialogUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.CommonDialog;
import com.fmtch.base.widget.dialog.InputPayPwdDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.OrderBookBean;
import com.fmtch.module_bourse.bean.ParisBuySellBean;
import com.fmtch.module_bourse.ui.trade.model.ParisSellModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.PARIS_SELL, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class ParisSellActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    public TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_name)
    public TextView tvName;
    @BindView(R2.id.tv_user_deal_num)
    public TextView tvUserDealNum;
    @BindView(R2.id.iv_pay_bank_card)
    public ImageView ivPayBankCard;
    @BindView(R2.id.iv_pay_wechat)
    public ImageView ivPayWechat;
    @BindView(R2.id.iv_pay_zfb)
    public ImageView ivPayZfb;
    @BindView(R2.id.tv_register_time)
    public TextView tvRegisterTime;
    @BindView(R2.id.tv_auth_level)
    public TextView tvAuthLevel;
    @BindView(R2.id.tv_release_coin_time)
    public TextView tvReleaseCoinTime;
    @BindView(R2.id.tv_price_unit)
    public TextView tvPriceUnit;
    @BindView(R2.id.tv_money_unit)
    public TextView tvMoneyUnit;
    @BindView(R2.id.tv_price)
    public TextView tvPrice;
    @BindView(R2.id.tv_num_unit)
    public TextView tvNumUnit;
    @BindView(R2.id.et_num)
    public EditText etNum;
    @BindView(R2.id.et_money)
    public EditText etMoney;
    @BindView(R2.id.tv_buy)
    TextView tvBuy;
    @BindView(R2.id.tv_useful_money)
    public TextView tvUsefulMoney;

    public int mId;//订单id
    public int mCoinScale;//币种保留位数

    private ParisSellModel model;
    public OrderBookBean orderInfo;
    public ParisBuySellBean buyInfo;

    public BigDecimal price;//价格
    public BigDecimal canBuyNum;//最大能购买数量
    public BigDecimal canBuyMoney;//账户可用金额
    public BigDecimal minMoney;//最小可输入金额
    public BigDecimal maxMoney;//最大可输入金额


    @Override
    protected int getLayoutId() {
        return R.layout.activity_paris_sell;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mId = getIntent().getIntExtra(PageConstant.ID, 0);
        mCoinScale = getIntent().getIntExtra(PageConstant.SCALE, 8);//默认保留八位
        model = new ParisSellModel(this);
        model.uploadParisSellInfo(mId);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etNum.addTextChangedListener(numTextWatcher);
        etMoney.addTextChangedListener(moneyTextWatcher);
    }

    private TextWatcher numTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().endsWith("."))
                return;
            if (canBuyNum == null)
                return;
            etNum.removeTextChangedListener(numTextWatcher);
            etMoney.removeTextChangedListener(moneyTextWatcher);
            if (!TextUtils.isEmpty(s.toString()) && new BigDecimal(s.toString()).compareTo(BigDecimal.ZERO) == 1) {
                BigDecimal numBigDecimal = new BigDecimal(s.toString()).setScale(mCoinScale, BigDecimal.ROUND_DOWN);
//                //判断是否超过订单设置上限或账户余额
//                if (numBigDecimal.compareTo(canBuyNum) > 0) {
//                    numBigDecimal = canBuyNum;
//                    ToastUtils.showMessage(String.format(getString(R.string.can_sell_num), numBigDecimal.stripTrailingZeros().toPlainString()));
//                }
//                numBigDecimal = numBigDecimal.setScale(mCoinScale, BigDecimal.ROUND_DOWN);
                etNum.setText(numBigDecimal.stripTrailingZeros().toPlainString());
                etNum.setSelection(etNum.getEditableText().length());
                etMoney.setText(numBigDecimal.multiply(price).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                etMoney.setSelection(etMoney.getEditableText().length());
                tvBuy.setEnabled(true);
            } else {
                etMoney.setText("");
                tvBuy.setEnabled(false);
            }
            etNum.addTextChangedListener(numTextWatcher);
            etMoney.addTextChangedListener(moneyTextWatcher);
        }
    };

    private TextWatcher moneyTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().endsWith("."))
                return;
            if (maxMoney == null)
                return;
            etNum.removeTextChangedListener(numTextWatcher);
            etMoney.removeTextChangedListener(moneyTextWatcher);
            if (!TextUtils.isEmpty(s.toString()) && new BigDecimal(s.toString()).compareTo(BigDecimal.ZERO) == 1) {
                BigDecimal moneyBigDecimal = new BigDecimal(s.toString()).setScale(2, BigDecimal.ROUND_DOWN);
//                //判断是否超过当前账户余额
//                if (canBuyMoney != null && moneyBigDecimal.compareTo(canBuyMoney) > 0) {
//                    moneyBigDecimal = canBuyMoney;
//                    ToastUtils.showMessage(tvUsefulMoney.getText().toString());
//                }
//                //判断是否超过订单设置上限
//                if (moneyBigDecimal.compareTo(maxMoney) > 0) {
//                    moneyBigDecimal = maxMoney;
//                    ToastUtils.showMessage(String.format(getString(R.string.please_input_money), etMoney.getHint().toString()));
//                }
//                moneyBigDecimal = moneyBigDecimal.setScale(2, BigDecimal.ROUND_DOWN);
                etMoney.setText(moneyBigDecimal.stripTrailingZeros().toPlainString());
                etMoney.setSelection(etMoney.getEditableText().length());
                etNum.setText(moneyBigDecimal.divide(price, mCoinScale, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                etNum.setSelection(etNum.getEditableText().length());
                tvBuy.setEnabled(true);
            } else {
                etNum.setText("");
                tvBuy.setEnabled(false);
            }
            etNum.addTextChangedListener(numTextWatcher);
            etMoney.addTextChangedListener(moneyTextWatcher);
        }
    };

    @OnClick({R2.id.tv_total_num, R2.id.tv_total_money, R2.id.tv_buy})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_total_num) {
            if (canBuyNum != null) {
                etNum.setText(canBuyNum.setScale(mCoinScale, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString());
            }
        } else if (id == R.id.tv_total_money) {
            if (maxMoney != null && canBuyMoney != null) {
                //判断可用法币及订单最大值
                if (maxMoney.compareTo(canBuyMoney) > 0) {
                    etMoney.setText(canBuyMoney.stripTrailingZeros().toPlainString());
                } else {
                    etMoney.setText(maxMoney.stripTrailingZeros().toPlainString());
                }
            }
        } else if (id == R.id.tv_buy) {
            BigDecimal numBigDecimal = new BigDecimal(etNum.getEditableText().toString());
            //判断是否超过可出售数量
            if (numBigDecimal.compareTo(canBuyNum) > 0) {
                ToastUtils.showMessage(String.format(getString(R.string.can_sell_num), canBuyNum.stripTrailingZeros().toPlainString()));
                return;
            }
            BigDecimal moneyBigDecimal = new BigDecimal(etMoney.getEditableText().toString());
            //判断是否超过订单设置下限
            if (moneyBigDecimal.compareTo(minMoney) < 0) {
                ToastUtils.showMessage(String.format(getString(R.string.please_input_money), etMoney.getHint().toString()));
                return;
            }
            //判断是否超过订单设置上限
            if (moneyBigDecimal.compareTo(maxMoney) > 0) {
                ToastUtils.showMessage(String.format(getString(R.string.please_input_money), etMoney.getHint().toString()));
                return;
            }
            //判断是否超过当前账户余额
            if (canBuyMoney != null && moneyBigDecimal.compareTo(canBuyMoney) > 0) {
                ToastUtils.showMessage(tvUsefulMoney.getText().toString());
                return;
            }
            //输入资金密码的弹窗
            showInputPayPwdDialog();
        }
    }

    /**
     * 输入资金密码的弹窗
     */
    private void showInputPayPwdDialog() {
        int show = InputPwdDialogUtils.showPwdDialog();
        if (show == InputPwdDialogUtils.STATUS_UN_SET_PAY_PWD) {
            CommonDialog dialog = new CommonDialog(this);
            dialog.showMsg(getString(com.fmtch.base.R.string.no_set_pay_pwd), false);
            dialog.setBtnConfirmText(getString(R.string.go_set));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.SET_ASSETS_PWD).navigation();
                }
            });
        } else if (show == InputPwdDialogUtils.STATUS_UN_SHOW) {
            model.sellParisCoin(mId, etNum.getEditableText().toString(), "");
        } else if (show == InputPwdDialogUtils.STATUS_SHOW) {
            new InputPayPwdDialog(this)
                    .setOnConfirmSuccessListener(new InputPayPwdDialog.OnConfirmSuccessListener() {
                        @Override
                        public void onConfirmSuccess(String pay_pwd) {
                            model.sellParisCoin(mId, etNum.getEditableText().toString(), pay_pwd);
                        }
                    })
                    .builder()
                    .show();
        }
    }

}
