package com.fmtch.module_bourse.ui.trade.model;


import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.net.API;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.StringUtil;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.OrderBookBean;
import com.fmtch.module_bourse.bean.ParisBuySellBean;
import com.fmtch.module_bourse.bean.PaymentBean;
import com.fmtch.module_bourse.bean.response.ParisBuySellResponse;
import com.fmtch.module_bourse.bean.response.PaymentResponse;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.ParisBuyActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.math.BigDecimal;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParisBuyModel {

    private ParisBuyActivity view;

    public ParisBuyModel(ParisBuyActivity view) {
        this.view = view;
    }

    public void uploadParisBuyInfo(int id) {
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisBuyInfo(id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<ParisBuySellResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ParisBuySellResponse>() {
                    @Override
                    public void onSuccess(ParisBuySellResponse data) {
                        if (data == null)
                            return;
                        view.orderInfo = data.getOrder_book();
                        view.sellInfo = data.getSell_info();
                        if (view.orderInfo != null) {
                            String coin_name = StringUtils.subBeforeStr(view.orderInfo.getSymbol(), "/");
                            String unit_name = StringUtils.subAfterStr(view.orderInfo.getSymbol(), "/");

                            view.price = new BigDecimal(view.orderInfo.getPrice());
                            view.canBuyNum = new BigDecimal(view.orderInfo.getNumber());
                            view.minMoney = new BigDecimal(view.orderInfo.getMin_cny());
                            view.maxMoney = new BigDecimal(view.orderInfo.getMax_cny());

                            view.tvTitle.setText(String.format(view.getString(R.string.paris_buy), coin_name));
                            view.tvPriceUnit.setText(StringUtils.addBrackets(unit_name));
                            view.tvNumUnit.setText(StringUtils.addBrackets(coin_name));
                            view.tvMoneyUnit.setText(StringUtils.addBrackets(unit_name));
                            view.ivPayBankCard.setVisibility(view.orderInfo.getBank() == 1 ? View.VISIBLE : View.GONE);
                            view.ivPayZfb.setVisibility(view.orderInfo.getAlipay() == 1 ? View.VISIBLE : View.GONE);
                            view.ivPayWechat.setVisibility(view.orderInfo.getWechat() == 1 ? View.VISIBLE : View.GONE);
                            view.tvPrice.setText(view.orderInfo.getPrice());
                            view.etNum.setHint(String.format(view.getString(R.string.can_buy_num), view.orderInfo.getNumber()));
                            view.etMoney.setHint(view.orderInfo.getMin_cny() + "-" + view.orderInfo.getMax_cny());
                        }
                        if (view.sellInfo != null) {
                            view.tvName.setText(view.sellInfo.getUsername());
                            view.tvUserDealNum.setText("(" + view.sellInfo.getOrder_count() + "/" + view.sellInfo.getFinish_percent() + ")");
                            view.tvRegisterTime.setText(view.sellInfo.getCreated_at());
                            view.tvAuthLevel.setText("KYC" + view.sellInfo.getKyc_status());
                            view.tvReleaseCoinTime.setText(StringUtils.formatSeconds(Long.parseLong(view.sellInfo.getAvg_confirm_time())));
                        }
                    }
                });
    }

    public void buyParisCoin(int id, String buy_number) {
        RetrofitManager.getInstance().create(ApiService.class)
                .parisCoinBuy(id, buy_number)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<PaymentBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PaymentBean>() {
                    @Override
                    public void onSuccess(PaymentBean data) {
                        ARouter.getInstance().build(RouterMap.PARIS_BUY_ORDER)
                                .withInt(PageConstant.ID, data.getId())
                                .navigation();
                        view.finish();
                    }
                });
    }

}
