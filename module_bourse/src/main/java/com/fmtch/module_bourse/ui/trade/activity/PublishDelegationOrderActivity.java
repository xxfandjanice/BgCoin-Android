package com.fmtch.module_bourse.ui.trade.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.event.BottomDialogItem;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.widget.ClearEditText;
import com.fmtch.base.widget.dialog.BottomListDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.ParisCoinBean;
import com.fmtch.module_bourse.ui.trade.model.PublishDelegationOrderModel;
import com.fmtch.module_bourse.utils.TimeUtils;
import com.fmtch.module_bourse.utils.ToastUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

@Route(path = RouterMap.PUBLISH_DELEGATION_ORDER, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class PublishDelegationOrderActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R2.id.tv_Handicap_price)
    public TextView tvHandicapPrice;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.amount)
    TextView amount;
    @BindView(R2.id.tv_pricing_type)
    TextView tvPricingType;
    @BindView(R2.id.iv_pricing_type)
    ImageView ivPricingType;
    @BindView(R2.id.et_transaction_price)
    EditText etTransactionPrice;
    @BindView(R2.id.et_amount)
    public EditText etAmount;
    @BindView(R2.id.et_price)
    public TextView etPrice;
    @BindView(R2.id.iv_competitor_limit)
    ImageView ivCompetitorLimit;
    @BindView(R2.id.tv_pay_spend_time)
    TextView tvPaySpendTime;
    @BindView(R2.id.iv_pay_spend_time)
    ImageView ivPaySpendTime;
    @BindView(R2.id.tv_auth_level)
    TextView tvAuthLevel;
    @BindView(R2.id.iv_auth_level)
    ImageView ivAuthLevel;
    @BindView(R2.id.tv_register_time)
    ClearEditText tvRegisterTime;
    @BindView(R2.id.iv_register_time)
    ImageView ivRegisterTime;
    @BindView(R2.id.et_transaction_description)
    EditText etTransactionDescription;
    @BindView(R2.id.btn_publish)
    Button btnPublish;
    @BindView(R2.id.tv_security_deposit)
    TextView tvSecurityDeposit;
    @BindView(R2.id.tv_service_fee)
    TextView tvServiceFee;
    @BindView(R2.id.tv_amount_all)
    TextView tvAmountAll;
    @BindView(R2.id.ll_pay_spend_time)
    LinearLayout llPaySpendTime;
    @BindView(R2.id.ll_auth_level)
    LinearLayout llAuthLevel;
    @BindView(R2.id.ll_register_time)
    LinearLayout llRegisterTime;
    @BindView(R2.id.ll_transaction_description)
    LinearLayout llTransactionDescription;
    @BindView(R2.id.et_change_rate)
    EditText etChangeRate;
    @BindView(R2.id.ll_change_rate)
    LinearLayout llChangeRate;
    @BindView(R2.id.ll_competitor_limit)
    LinearLayout llCompetitorLimit;
    @BindView(R2.id.tv_transaction_price)
    TextView tvTransactionPrice;
    @BindView(R2.id.tv_market_price)
    public TextView tvMarketPrice;
    @BindView(R2.id.divider_change_rate)
    View dividerChangeRate;
    @BindView(R2.id.divider_pay_spend_time)
    View dividerPaySpendTime;

    private List<BottomDialogItem> dialogItems = new ArrayList<>();
    public int tradeType = 0;                 //0 我要购买  1 我要出售
    private int pricingType = 1;              //定价方式（1-固定价格 2-浮动价格）
    private int authLevel = 1;                //认证等级（1-KYC1  2-KYC2   3-KYC3）
    private int paySpendTime = 10;            //付款时间（10-10分钟    15-15分钟    20-20分钟 ）
    private String[] pricingTypes;
    private String[] paySpendTimes;
    private String[] authLevels;
    private boolean isHideCompetitorLimit = false;  //是否隐藏对手限制
    private PublishDelegationOrderModel model;
    private ParisCoinBean coinBean;
    private String inputRegisterTime;
    public String market_price = ""; //市场价(用于浮动价格计算)
    public BigDecimal minFloatRate;      //浮动价格 最小浮动比例
    public BigDecimal maxFloatRate;      //浮动价格 最大浮动比例
    public BigDecimal minMoney = new BigDecimal("400");    //最小交易金额
    public BigDecimal maxMoney = new BigDecimal("50000");  //最大交易金额

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_delegation_order;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        pricingTypes = new String[]{getResources().getString(R.string.fixed_price), getResources().getString(R.string.change_price)};
        paySpendTimes = new String[]{getResources().getString(R.string.pay_10_min), getResources().getString(R.string.pay_15_min), getResources().getString(R.string.pay_20_min)};
        authLevels = new String[]{getResources().getString(R.string.auth_level_KYC1), getResources().getString(R.string.auth_level_KYC2), getResources().getString(R.string.auth_level_KYC3)};
        //0 购买  1 出售
        tradeType = getIntent().getIntExtra(PageConstant.PARIS_TRADE_TYPE, 0);
        //币种信息
        coinBean = (ParisCoinBean) getIntent().getSerializableExtra(PageConstant.COIN);
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
        tvTitle.setText(String.format(getResources().getString(R.string.publish_delegation_order), coinBean.getName()));
        amount.setText(String.format(getResources().getString(R.string.amount_coin), coinBean.getName()));
        tvSecurityDeposit.setText(String.format(getResources().getString(R.string.security_deposit), NumberUtils.stripMoneyZeros(coinBean.getOtc_deposit()) + "", coinBean.getName()));
        tvServiceFee.setText(String.format(getResources().getString(R.string.service_fee), NumberUtils.bigDecimal2Percent(coinBean.getOtc_fee())));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.want_to_buy));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.want_to_sell));
        minFloatRate = new BigDecimal("100").subtract(new BigDecimal(coinBean.getOtc_float_range()));
        maxFloatRate = new BigDecimal("100").add(new BigDecimal(coinBean.getOtc_float_range()));
        etChangeRate.setHint(String.format(getResources().getString(R.string.hint_change_rate), String.valueOf(minFloatRate.stripTrailingZeros().toPlainString()), String.valueOf(maxFloatRate.stripTrailingZeros().toPlainString())));
        etPrice.setText(String.format(getResources().getString(R.string.input_price_limit), minMoney.stripTrailingZeros().toPlainString(), maxMoney.stripTrailingZeros().toPlainString()));
        updateTabStyle(tradeType);
        tabLayout.getTabAt(tradeType).select();

        model = new PublishDelegationOrderModel(this);
        model.getExtraInfo(coinBean.getId());
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabStyle(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void updateTabStyle(int position) {
        tradeType = position;
        if (position == 0) {   //我要购买
            tabLayout.setTabTextColors(getResources().getColor(R.color.cl_333333), getResources().getColor(R.color.theme));
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.theme));
            etAmount.setHint(R.string.please_input_buy_amount);
//            llPaySpendTime.setVisibility(View.GONE);
//            dividerPaySpendTime.setVisibility(View.GONE);
            etTransactionDescription.setHint(R.string.hint_description_want_buy);
            llTransactionDescription.setVisibility(View.GONE);
            tvAmountAll.setVisibility(View.GONE);
            tvSecurityDeposit.setVisibility(View.VISIBLE);
        } else {       //我要出售
            tabLayout.setTabTextColors(getResources().getColor(R.color.cl_333333), getResources().getColor(R.color.cl_f55758));
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.cl_f55758));
            etAmount.setHint(R.string.please_input_sell_amount);
//            llPaySpendTime.setVisibility(isHideCompetitorLimit ? View.GONE : View.VISIBLE);
//            dividerPaySpendTime.setVisibility(isHideCompetitorLimit ? View.GONE : View.VISIBLE);
            llTransactionDescription.setVisibility(View.VISIBLE);
            etTransactionDescription.setHint(R.string.hint_description_want_sell);
            tvAmountAll.setVisibility(View.VISIBLE);
            tvSecurityDeposit.setVisibility(View.INVISIBLE);
        }
    }

    //底部选择弹窗
    @OnClick({R2.id.iv_pricing_type, R2.id.iv_competitor_limit, R2.id.iv_pay_spend_time, R2.id.iv_auth_level, R2.id.iv_register_time, R2.id.btn_publish})
    public void onViewClickedByDialog(View view) {
        final int id = view.getId();
        dialogItems.clear();
        if (id == R.id.iv_pricing_type) {
            //定价方式
            for (String pricingType : pricingTypes) {
                BottomDialogItem item = new BottomDialogItem(pricingType, false);
                if (tvPricingType.getText().equals(pricingType)) {
                    item.setSelected(true);
                }
                dialogItems.add(item);
            }
        } else if (id == R.id.iv_pay_spend_time) {
            //付费时间
            for (String paySpendTime : paySpendTimes) {
                BottomDialogItem item = new BottomDialogItem(paySpendTime, false);
                if (tvPaySpendTime.getText().equals(paySpendTime)) {
                    item.setSelected(true);
                }
                dialogItems.add(item);
            }
        } else if (id == R.id.iv_auth_level) {
            //认证等级
            for (String authLevel : authLevels) {
                BottomDialogItem item = new BottomDialogItem(authLevel, false);
                if (tvAuthLevel.getText().equals(authLevel)) {
                    item.setSelected(true);
                }
                dialogItems.add(item);
            }
        }
        BottomListDialog.showBottomListDialog(this, dialogItems, new BottomListDialog.BottomDialogItemOnClickListener() {
            @Override
            public void itemOnClick(View view, int position) {
                if (id == R.id.iv_pricing_type) {
                    tvPricingType.setText(pricingTypes[position]);
                    llChangeRate.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                    dividerChangeRate.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                    tvTransactionPrice.setTextColor(position == 0 ? getResources().getColor(R.color.cl_333333) : getResources().getColor(R.color.cl_999999));
                    etTransactionPrice.setTextColor(position == 0 ? getResources().getColor(R.color.cl_333333) : getResources().getColor(R.color.cl_c4c8d0));
                    if (position == 0) {
                        etTransactionPrice.setFocusable(true);
                        etTransactionPrice.setFocusableInTouchMode(true);
                    } else {
                        etTransactionPrice.setFocusable(false);
                    }
                    etTransactionPrice.setText(position == 0 ? "" : market_price);
                    pricingType = position + 1;
                } else if (id == R.id.iv_pay_spend_time) {
                    tvPaySpendTime.setText(paySpendTimes[position]);
                    paySpendTime = position == 0 ? 10 : (position == 1 ? 15 : 20);
                } else if (id == R.id.iv_auth_level) {
                    tvAuthLevel.setText(authLevels[position]);
                    authLevel = position + 1;
                }
            }
        });
    }

    //全部(我要出售)
    @OnClick(R2.id.tv_amount_all)
    public void onViewClickedByAmountAll(View view) {
        model.getAvailableByCoin(coinBean.getId());
    }

    //对手限制
    @OnClick(R2.id.iv_competitor_limit)
    public void onViewClickedByCompetitorLimit(View view) {
        isHideCompetitorLimit = !isHideCompetitorLimit;
        llCompetitorLimit.setVisibility(isHideCompetitorLimit ? View.GONE : View.VISIBLE);
        if (tradeType == 1) {
            llPaySpendTime.setVisibility(isHideCompetitorLimit ? View.GONE : View.VISIBLE);
            dividerPaySpendTime.setVisibility(isHideCompetitorLimit ? View.GONE : View.VISIBLE);
        }
    }

    //选择注册时间
    @OnClick(R2.id.iv_register_time)
    public void onViewClickedByRegisterTime(View view) {
        showDateDialog();
    }

    //发布委托单
    @OnClick(R2.id.btn_publish)
    public void onViewClickedByPublish(View view) {
        String price = etTransactionPrice.getText().toString();
        if (pricingType == 2) {
            //浮动价格 price为浮动比例
            String inputRate = etChangeRate.getText().toString();
            price = TextUtils.isEmpty(inputRate) ? "100" : inputRate;
        }
        String amount = etAmount.getText().toString();
        String remark = etTransactionDescription.getText().toString();
        //校验输入
        model.checkInputLegal(coinBean.getId(), pricingType, price, amount, paySpendTime, authLevel, inputRegisterTime, remark, coinBean.getName());
    }

    /**
     * 日期选择器
     */
    public void showDateDialog() {
        String currentDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd");
        String[] split = currentDate.split("-");
        int year = Integer.valueOf(split[0]);
        int month = Integer.valueOf(split[1]);
        int day = Integer.valueOf(split[2]);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                inputRegisterTime = year + "-" + (month + 1) + "-" + dayOfMonth;
                tvRegisterTime.setText(String.format("%s 前注册", inputRegisterTime));
            }
        }, year, month - 1, day);
        datePickerDialog.show();
    }

    //输入浮动比例
    @OnTextChanged(value = R2.id.et_change_rate, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChangedOnChangeRate(Editable e) {
        String rate = "100";
        if (TextUtils.isEmpty(market_price)) {
            ToastUtils.showShortToast("最新市场价获取失败,请稍后重试");
            return;
        }
        if (e.toString().trim().startsWith(".")) {
            e.insert(0, "0");
        }
        e = NumberUtils.setInputScale(e, 2);
        if (!TextUtils.isEmpty(e.toString())) {
            rate = e.toString();
        }
        String ratePrice = new BigDecimal(rate).multiply(new BigDecimal(market_price)).divide(new BigDecimal("100"), BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        etTransactionPrice.setText(ratePrice);
    }

    //输入交易价格
    @OnTextChanged(value = R2.id.et_transaction_price, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChangedOnPrice(Editable e) {
        setBtnPublishEnable(e, 2, etAmount);
    }

    //输入交易数量
    @OnTextChanged(value = R2.id.et_amount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChangedOnAmount(Editable e) {
        setBtnPublishEnable(e, coinBean.getOtc_num_decimals(), etTransactionPrice);
    }

    //发布按钮是否可用
    private void setBtnPublishEnable(Editable e, int scale, EditText editText) {
        String input = e.toString();
        if (!TextUtils.isEmpty(input) && input.trim().startsWith(".")) {
            e.insert(0, "0");
        }
        e = NumberUtils.setInputScale(e, scale);
        if (!TextUtils.isEmpty(e.toString()) && !TextUtils.isEmpty(editText.getText())) {
            BigDecimal totalMoney = new BigDecimal(e.toString()).multiply(new BigDecimal(editText.getText().toString())).stripTrailingZeros();
            if (totalMoney.compareTo(BigDecimal.ZERO) <= 0) {
                btnPublish.setEnabled(false);
            } else {
                btnPublish.setEnabled(true);
            }
            etPrice.setText(totalMoney.toPlainString());
        }
    }
}
