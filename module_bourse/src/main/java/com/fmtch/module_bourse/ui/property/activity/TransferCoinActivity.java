package com.fmtch.module_bourse.ui.property.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.DialogUtils;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.ui.property.model.TransferCoinModel;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.TRANSFER_COIN, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class TransferCoinActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_my_wallet)
    TextView tvMyWallet;
    @BindView(R2.id.tv_coin_account)
    TextView tvCoinAccount;
    @BindView(R2.id.tv_select_coin)
    public TextView tvSelectCoin;
    @BindView(R2.id.et_num)
    EditText etNum;
    @BindView(R2.id.tv_max_transfer)
    public TextView tvMaxTransfer;
    @BindView(R2.id.tv_sure)
    TextView tvSure;

    @Autowired
    int account_type;//账户类型(总账户0 币币账户1 法币账户2)

    public ArrayList<AccountBean> coinList;
    public AccountBean coinInfo;

    private TransferCoinModel model;

    private boolean isMyWalletUp = true;//默认我的钱包在上
    public int type; // 4:转账币币账户 5:转账法币账户

    private static final int REQUEST_CODE_COIN = 777;

    private BottomSheetDialog changeDialog;
    private boolean is_first = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_coin;
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
        type = getIntent().getIntExtra(PageConstant.COIN_TYPE, 4);//4:转账币币账户 5:转账法币账户
        account_type = getIntent().getIntExtra(PageConstant.ACCOUNT_TYPE, 0);//账户类型(总账户0 币币账户1 法币账户2)
        if (account_type != 0) {
            if (account_type == 1) {
                type = 4;
            } else {
                type = 5;
            }
            //切换位置
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onIvTransferClicked();
                }
            }, 200);
        }
        if (type == 4) {
            tvCoinAccount.setText(R.string.coin_coin_account);
        } else {
            tvCoinAccount.setText(R.string.paris_coin_account);
        }
        coinList = new ArrayList<>();
        model = new TransferCoinModel(this);
        if (getIntent().getSerializableExtra(PageConstant.COIN_INFO) != null)
            coinInfo = (AccountBean) getIntent().getSerializableExtra(PageConstant.COIN_INFO);
        else
            model.getCoinList(account_type);
    }

    //刷新币种信息
    private void updateView() {
        if (coinInfo != null && coinInfo.getCoin() != null) {
            AccountBean.Coin coin = coinInfo.getCoin();
            if (!TextUtils.isEmpty(coin.getName())) {
                tvSelectCoin.setText(coin.getName());
            }
            tvMaxTransfer.setText(String.format(getString(R.string.max_can_transfer_num), NumberUtils.getFormatMoney(coinInfo.getAvailable()), coin.getName()));
        }
    }

    //划转记录
    @OnClick(R2.id.iv_records)
    public void onViewClicked() {
        String account_type = "account";
        if (!isMyWalletUp){
            if (type == 4){
                account_type = "spot_account";
            }else if (type == 5){
                account_type = "otc_account";
            }
        }
        ARouter.getInstance().build(RouterMap.TRANSFER_COIN_RECORDS)
                .withInt(PageConstant.TYPE, 3)
                .withString(PageConstant.ACCOUNT_TYPE,account_type)
                .withSerializable(PageConstant.COIN_LIST, coinList)
                .withSerializable(PageConstant.COIN_INFO, coinInfo)
                .navigation();
    }

    //选择币币账户法币账户
    @OnClick({R2.id.tv_coin_account, R2.id.tv_my_wallet})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_coin_account && isMyWalletUp) {
            showChangeDialog();
        } else if (id == R.id.tv_my_wallet && !isMyWalletUp) {
            showChangeDialog();
        }
    }

    private void showChangeDialog() {
        if (changeDialog == null) {
            View view = LayoutInflater.from(TransferCoinActivity.this).inflate(R.layout.dialog_select_account, null);
            changeDialog = DialogUtils.showDetailBottomDialog(TransferCoinActivity.this, view);
            view.findViewById(R.id.tv_first).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //法币账户
                    changeDialog.dismiss();
                    type = 5;
                    tvCoinAccount.setText(R.string.paris_coin_account);
                    if (isMyWalletUp)
                        account_type = 0;
                    else
                        account_type = 2;
                    model.getCoinList(account_type);
//                    if (coinInfo.getCoin().getIs_otc() != 1) {
//                        //如果当前币种不支持法币交易
//                        for (AccountBean accountBean : coinList) {
//                            if (accountBean.getCoin() != null) {
//                                AccountBean.Coin coin = accountBean.getCoin();
//                                if (coin.getIs_otc() == 1) {
//                                    //支持法币交易
//                                    coinInfo = accountBean;
//                                    if (!TextUtils.isEmpty(coin.getName())) {
//                                        tvSelectCoin.setText(coin.getName());
//                                        tvMaxTransfer.setText(String.format(getString(R.string.max_can_transfer_num), NumberUtils.getFormatMoney(coinInfo.getAvailable()), coin.getName()));
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//                    }
                }
            });
            view.findViewById(R.id.tv_second).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //币币账户
                    changeDialog.dismiss();
                    type = 4;
                    tvCoinAccount.setText(R.string.coin_coin_account);
                    if (isMyWalletUp)
                        account_type = 0;
                    else
                        account_type = 1;
                    model.getCoinList(account_type);
//                    if (coinInfo.getCoin().getIs_spot() != 1) {
//                        //如果当前币种不支持币币交易
//                        for (AccountBean accountBean : coinList) {
//                            if (accountBean.getCoin() != null) {
//                                AccountBean.Coin coin = accountBean.getCoin();
//                                if (coin.getIs_spot() == 1) {
//                                    //支持币币交易
//                                    coinInfo = accountBean;
//                                    if (!TextUtils.isEmpty(coin.getName())) {
//                                        tvSelectCoin.setText(coin.getName());
//                                        tvMaxTransfer.setText(String.format(getString(R.string.max_can_transfer_num), NumberUtils.getFormatMoney(coinInfo.getAvailable()), coin.getName()));
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//                    }
                }
            });
            view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeDialog.dismiss();
                }
            });
        }
        changeDialog.show();
    }

    //切换账户
    @OnClick(R2.id.iv_transfer)
    public void onIvTransferClicked() {
        int height = tvMyWallet.getMeasuredHeight();
        TranslateAnimation walletAnimation;
        TranslateAnimation coinAnimation;
        if (isMyWalletUp) {
            isMyWalletUp = false;
            walletAnimation = new TranslateAnimation(0, 0, 0, height);
            coinAnimation = new TranslateAnimation(0, 0, 0, -height);
            if (type == 4) {
                //到币币账户
                account_type = 1;
            } else if (type == 5) {
                //到法币账户
                account_type = 2;
            }
        } else {
            isMyWalletUp = true;
            walletAnimation = new TranslateAnimation(0, 0, height, 0);
            coinAnimation = new TranslateAnimation(0, 0, -height, 0);
            //到总账户
            account_type = 0;
        }
        //重新获取数据
        if (!is_first)
            model.getCoinList(account_type);
        is_first = false;

        walletAnimation.setDuration(500);//动画持续的时间为1s
        walletAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
        walletAnimation.setFillAfter(true);//不回到起始位置
        coinAnimation.setDuration(500);//动画持续的时间为1s
        coinAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
        coinAnimation.setFillAfter(true);//不回到起始位置
        tvMyWallet.startAnimation(walletAnimation);
        tvCoinAccount.startAnimation(coinAnimation);
    }

    //选择币种
    @OnClick(R2.id.tv_select_coin)
    public void onTvSelectCoinClicked() {
        ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                .withInt(PageConstant.ACCOUNT_TYPE, account_type)
                .withInt(PageConstant.TYPE, type)
                .withSerializable(PageConstant.COIN_LIST, coinList)
                .navigation(this, REQUEST_CODE_COIN);
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
        String number = etNum.getEditableText().toString();
        if (TextUtils.isEmpty(number)) {
            ToastUtils.showLongToast(R.string.please_input_transfer_num);
            return;
        }
        if (new BigDecimal(number).compareTo(new BigDecimal(coinInfo.getAvailable())) == 1) {
            ToastUtils.showLongToast(tvMaxTransfer.getText().toString());
            return;
        }
        //（account：总账户，spot_account：币币账户，otc_account：法币账户）
        // 4:转账币币账户 5:转账法币账户
        String from_account, to_account;
        if (isMyWalletUp) {
            from_account = "account";
            if (type == 4) {
                to_account = "spot_account";
            } else {
                to_account = "otc_account";
            }
        } else {
            to_account = "account";
            if (type == 4) {
                from_account = "spot_account";
            } else {
                from_account = "otc_account";
            }
        }
        model.transfer(coinInfo.getCoin_id(), from_account, to_account, number);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == REQUEST_CODE_COIN && data.getSerializableExtra(PageConstant.COIN_INFO) != null) {
            coinInfo = (AccountBean) data.getSerializableExtra(PageConstant.COIN_INFO);
            updateView();
        }
    }

}
