package com.fmtch.module_bourse.ui.trade.model;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.widget.dialog.CommonDialog;
import com.fmtch.base.widget.dialog.CustomDialog;
import com.fmtch.base.widget.dialog.ShowDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.ParisCoinInfo;
import com.fmtch.module_bourse.bean.request.ParisDelegationOrderBuyRequest;
import com.fmtch.module_bourse.bean.request.ParisDelegationOrderSellRequest;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.PublishDelegationOrderActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.math.BigDecimal;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/6/27
 */
public class PublishDelegationOrderModel {
    private PublishDelegationOrderActivity view;


    public PublishDelegationOrderModel(PublishDelegationOrderActivity view) {
        this.view = view;
    }

    /**
     * 获取交易所需的信息
     * 市场价、盘口价
     */
    public void getExtraInfo(int coin_id) {
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisCoinInfo(String.valueOf(coin_id))
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<ParisCoinInfo>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ParisCoinInfo>() {
                    @Override
                    public void onSuccess(ParisCoinInfo data) {
                        view.tvHandicapPrice.setText(String.format(view.getResources().getString(R.string.Handicap_price), data.getClose_price()));
                        view.market_price = data.getMarket_price();
                        view.tvMarketPrice.setText(String.format("￥%s", NumberUtils.stripMoneyZeros(data.getMarket_price())));
                    }
                });
    }

    /**
     * 出售
     * 获取指定币种的可用数量
     *
     * @param coin_id id
     */
    public void getAvailableByCoin(int coin_id) {
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisCoinData(String.valueOf(coin_id))
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<AccountBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AccountBean>() {
                    @Override
                    public void onSuccess(AccountBean data) {
                        //可出售数量
                        String available = data.getAvailable();
                        if (TextUtils.isEmpty(available)) {
                            available = "0";
                        }
                        BigDecimal subtract = new BigDecimal(available);
                        if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
                            view.etAmount.setText("0.00");
                        } else {
                            view.etAmount.setText(subtract.stripTrailingZeros().toPlainString());
                        }
                    }
                });
    }

    /**
     * 校验输入是否合法
     *
     * @param id           币种ID
     * @param pricingType  定价方式（1-固定价格 2-浮动价格)
     * @param price        出售价格
     * @param amount       出售数量
     * @param paySpendTime 付款时间（10-10分钟  15-15分钟  20-20分钟 ）
     * @param authLevel    认证等级（1-KYC1  2-KYC2  3-KYC3）
     * @param registerTime 注册时间
     * @param remark       交易说明
     */
    public void checkInputLegal(final int id, final int pricingType, final String price, final String amount, final int paySpendTime, final int authLevel, final String registerTime, final String remark, String coinName) {

        final CommonDialog tipDialog = new CommonDialog(view);
        tipDialog.setCanceledOnTouchOutside(false);

        //定价方式为浮动价格,校验浮动比例
        if (pricingType == 2) {
            BigDecimal decimalPrice = new BigDecimal(price);
            if (decimalPrice.compareTo(view.minFloatRate) < 0) {
                tipDialog.showMsg("最小浮动比例" + view.minFloatRate, true);
                tipDialog.show();
                return;
            }

            if (decimalPrice.compareTo(view.maxFloatRate) > 0) {
                tipDialog.showMsg("最大浮动比例" + view.maxFloatRate, true);
                tipDialog.show();
                return;
            }
        }

        //校验交易金额
        String money = view.etPrice.getText().toString();
        BigDecimal decimalMoney = new BigDecimal(money);
        if (decimalMoney.compareTo(view.minMoney) < 0) {
            tipDialog.showMsg("最小金额" + view.minMoney, true);
            tipDialog.show();
            return;
        }
        if (decimalMoney.compareTo(view.maxMoney) > 0) {
            tipDialog.showMsg("最大金额" + view.maxMoney, true);
            tipDialog.show();
            return;
        }

        View contentView = LayoutInflater.from(view).inflate(R.layout.dialog_paris_delegation_sure, null);
        TextView txtPrice = contentView.findViewById(R.id.txt_price);
        TextView tvPrice = contentView.findViewById(R.id.tv_price);
        TextView tvAmount = contentView.findViewById(R.id.tv_amount);
        tvPrice.setText(String.format("%sCNY", price));
        tvAmount.setText(String.format("%s%s", amount, coinName));
        if (pricingType == 2) {
            txtPrice.setText(view.getResources().getString(R.string.change_price));
        }
        new CustomDialog(view)
                .setTitle(String.format(view.getResources().getString(R.string.tip_sure_buy_paris), coinName))
                .setContentView(contentView)
                .setLeftBtnStr(R.string.cancel)
                .setRightBtnStr(R.string.sure)
                .setCancelable(false)
                .setSubmitListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //发布委托单
                        publishDelegationOrder(id, pricingType, price, amount, paySpendTime, authLevel,registerTime,remark);
                    }
                })
                .builder()
                .show();
    }

    /**
     * 发布委托单
     *
     * @param id           币种ID
     * @param pricingType  定价方式（1-固定价格 2-浮动价格)
     * @param price        出售价格
     * @param amount       出售数量
     * @param paySpendTime 付款时间（10-10分钟  15-15分钟  20-20分钟 ）
     * @param authLevel    认证等级（1-KYC1  2-KYC2  3-KYC3）
     * @param registerTime 注册时间
     * @param remark       交易说明
     */
    private void publishDelegationOrder(int id, int pricingType, String price, String amount, int paySpendTime, int authLevel, String registerTime, String remark) {

        final ShowDialog dialog = ShowDialog.showDialog(view, view.getResources().getString(R.string.loading), true, null);
        Observable<BaseResponse<String>> observable = null;
        if (view.tradeType == 0) {
            //购买
//            ParisDelegationOrderBuyRequest buyRequest = new ParisDelegationOrderBuyRequest(id, pricingType, price, amount, authLevel, registerTime, remark);
            ParisDelegationOrderBuyRequest buyRequest = new ParisDelegationOrderBuyRequest(id, pricingType, price, amount, 1, "", "");
            observable = RetrofitManager.getInstance().create(ApiService.class)
                    .publishDelegationOrderBuy(buyRequest);
        } else if (view.tradeType == 1) {
            //出售
//            ParisDelegationOrderSellRequest sellRequest = new ParisDelegationOrderSellRequest(id, pricingType, price, amount, authLevel, registerTime, paySpendTime, remark);
            ParisDelegationOrderSellRequest sellRequest = new ParisDelegationOrderSellRequest(id, pricingType, price, amount, 1, "", 10, remark);
            observable = RetrofitManager.getInstance().create(ApiService.class)
                    .publishDelegationOrderSell(sellRequest);
        }
        if (observable == null) {
            return;
        }
        observable.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        dialog.dismiss();
                        view.finish();
                        ARouter.getInstance().build(RouterMap.DELEGATION_ORDER).navigation();
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        dialog.dismiss();
                        CommonDialog tipDialog = new CommonDialog(view);
                        tipDialog.setCanceledOnTouchOutside(false);
                        tipDialog.showMsg(response.getMessage(), true);
                        tipDialog.show();
                    }

                    @Override
                    public void onException(Throwable e) {
                        super.onException(e);
                        dialog.dismiss();
                    }
                });
    }
}
