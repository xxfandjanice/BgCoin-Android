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

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.OrderBookBean;
import com.fmtch.module_bourse.bean.ParisBuySellBean;
import com.fmtch.module_bourse.ui.trade.model.ParisBuyModel;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.PARIS_BUY, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class ParisBuyActivity extends BaseActivity {

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

    @Autowired
    int mId;//订单id
    @Autowired
    int mCoinScale;//币种保留位数

    private ParisBuyModel model;
    public OrderBookBean orderInfo;
    public ParisBuySellBean sellInfo;

    public BigDecimal price;//价格
    public BigDecimal canBuyNum;//最大能购买数量
    public BigDecimal minMoney;//最小可输入金额
    public BigDecimal maxMoney;//最大可输入金额

    @Override
    protected int getLayoutId() {
        return R.layout.activity_paris_buy;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mId = getIntent().getIntExtra(PageConstant.ID, 0);
        mCoinScale = getIntent().getIntExtra(PageConstant.SCALE,8);//默认保留八位
        model = new ParisBuyModel(this);
        model.uploadParisBuyInfo(mId);
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
                tvBuy.setEnabled(true);
                BigDecimal numBigDecimal = new BigDecimal(s.toString()).setScale(mCoinScale, BigDecimal.ROUND_DOWN);
//                //判断是否超过订单设置上限
//                if (numBigDecimal.compareTo(canBuyNum) > 0) {
//                    numBigDecimal = canBuyNum;
//                    ToastUtils.showMessage(String.format(getString(R.string.can_buy_num), numBigDecimal.stripTrailingZeros().toPlainString()));
//                }
//                numBigDecimal = numBigDecimal.setScale(mCoinScale, BigDecimal.ROUND_DOWN);
                etNum.setText(numBigDecimal.stripTrailingZeros().toPlainString());
                etNum.setSelection(etNum.getEditableText().length());
                etMoney.setText(numBigDecimal.multiply(price).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                etMoney.setSelection(etMoney.getEditableText().length());
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
                tvBuy.setEnabled(true);
                BigDecimal moneyBigDecimal = new BigDecimal(s.toString()).setScale(2, BigDecimal.ROUND_DOWN);
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
                etNum.setText(canBuyNum.stripTrailingZeros().toPlainString());
            }
        } else if (id == R.id.tv_total_money) {
            if (maxMoney != null) {
                etMoney.setText(maxMoney.stripTrailingZeros().toPlainString());
            }
        } else if (id == R.id.tv_buy) {
            BigDecimal numBigDecimal = new BigDecimal(etNum.getEditableText().toString());
            //判断是否超过可购买数量
            if (numBigDecimal.compareTo(canBuyNum) > 0) {
                ToastUtils.showMessage(String.format(getString(R.string.can_buy_num), canBuyNum.stripTrailingZeros().toPlainString()));
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
            model.buyParisCoin(mId,etNum.getEditableText().toString());
        }
    }

}
