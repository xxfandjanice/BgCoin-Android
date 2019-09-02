package com.fmtch.module_bourse.ui.trade.model;


import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.OrderBookBean;
import com.fmtch.module_bourse.bean.PaymentBean;
import com.fmtch.module_bourse.bean.response.ParisBuySellResponse;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.ParisSellActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParisSellModel {

    private ParisSellActivity view;

    public ParisSellModel(ParisSellActivity view) {
        this.view = view;
    }

    public void uploadParisSellInfo(int id) {
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisSellInfo(id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<ParisBuySellResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ParisBuySellResponse>() {
                    @Override
                    public void onSuccess(ParisBuySellResponse data) {
                        if (data == null)
                            return;
                        view.orderInfo = data.getOrder_book();
                        view.buyInfo = data.getBuy_info();
                        if (view.orderInfo != null) {
                            String coin_name = StringUtils.subBeforeStr(view.orderInfo.getSymbol(), "/");
                            String unit_name = StringUtils.subAfterStr(view.orderInfo.getSymbol(), "/");

                            view.price = new BigDecimal(view.orderInfo.getPrice());
                            view.canBuyNum = new BigDecimal(view.orderInfo.getNumber());
                            view.minMoney = new BigDecimal(view.orderInfo.getMin_cny());
                            view.maxMoney = new BigDecimal(view.orderInfo.getMax_cny());

                            getParisCoinInfo(view.orderInfo.getCoin_id(),coin_name,view.price);

                            view.tvTitle.setText(String.format(view.getString(R.string.paris_sell), coin_name));
                            view.tvPriceUnit.setText(StringUtils.addBrackets(unit_name));
                            view.tvNumUnit.setText(StringUtils.addBrackets(coin_name));
                            view.tvMoneyUnit.setText(StringUtils.addBrackets(unit_name));
                            view.ivPayBankCard.setVisibility(view.orderInfo.getBank() == 1 ? View.VISIBLE : View.GONE);
                            view.ivPayZfb.setVisibility(view.orderInfo.getAlipay() == 1 ? View.VISIBLE : View.GONE);
                            view.ivPayWechat.setVisibility(view.orderInfo.getWechat() == 1 ? View.VISIBLE : View.GONE);
                            view.tvPrice.setText(view.orderInfo.getPrice());
                            view.etNum.setHint(String.format(view.getString(R.string.can_sell_num), view.orderInfo.getNumber()));
                            view.etMoney.setHint(view.orderInfo.getMin_cny() + "-" + view.orderInfo.getMax_cny());
                        }
                        if (view.buyInfo != null) {
                            view.tvName.setText(view.buyInfo.getUsername());
                            view.tvUserDealNum.setText("(" + view.buyInfo.getOrder_count() + "/" + view.buyInfo.getFinish_percent() + ")");
                            view.tvRegisterTime.setText(view.buyInfo.getCreated_at());
                            view.tvAuthLevel.setText("KYC" + view.buyInfo.getKyc_status());
                            view.tvReleaseCoinTime.setText(StringUtils.formatSeconds(Long.parseLong(view.buyInfo.getAvg_payment_time())));
                        }
                    }
                });
    }

    public void getParisCoinInfo(String coin_id, final String coin_name, final BigDecimal price){
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisCoinData(coin_id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<AccountBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AccountBean>() {
                    @Override
                    public void onSuccess(AccountBean data) {
                        if (data != null) {
                            BigDecimal availableNum = new BigDecimal(data.getAvailable());
                            if (view.canBuyNum.compareTo(availableNum) > 0 ){
                                view.canBuyNum = availableNum;
                                view.etNum.setHint(String.format(view.getString(R.string.can_sell_num)
                                        , view.canBuyNum.setScale(view.mCoinScale, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString()));
                            }
                            view.canBuyMoney = view.canBuyNum.multiply(price);
                            view.tvUsefulMoney.setText(String.format(view.getString(R.string.paris_account_can_use)
                                    ,availableNum.stripTrailingZeros().toPlainString() + coin_name));
                        }
                    }
                });
    }

    public void sellParisCoin(int id, String sell_number,final String pay_password) {
        RetrofitManager.getInstance().create(ApiService.class)
                .parisCoinSell(id, sell_number,pay_password)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<PaymentBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PaymentBean>() {
                    @Override
                    public void onSuccess(PaymentBean data) {
                        if (!TextUtils.isEmpty(pay_password)){
                            //保存支付密码校验成功时间
                            SpUtils.put(KeyConstant.KEY_PAY_PWD_SUCCESS_TIME, System.currentTimeMillis());
                        }
                        ARouter.getInstance().build(RouterMap.PARIS_SELL_ORDER)
                                .withInt(PageConstant.ID, data.getId())
                                .navigation();
                        view.finish();
                    }
                });
    }

}
