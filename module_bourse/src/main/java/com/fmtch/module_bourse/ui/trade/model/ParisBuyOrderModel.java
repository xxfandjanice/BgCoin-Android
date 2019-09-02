package com.fmtch.module_bourse.ui.trade.model;


import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.CustomDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.PaymentBean;
import com.fmtch.module_bourse.bean.response.ParisOrderDetailResponse;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.ParisBuyOrderActivity;
import com.fmtch.module_bourse.utils.TimeUtils;
import com.fmtch.module_bourse.widget.dialog.ConfirmPayDialog;
import com.fmtch.module_bourse.widget.dialog.PaymentWaySelectDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParisBuyOrderModel {

    private ParisBuyOrderActivity view;

    private static final long REFRESH_TIME = 5000;

    private List<PaymentBean> paymentBeans;

    private PaymentBean mPaymentBean; //当前选中支付方式数据

    private CountDownTimer countDownTimer, countDownTimer2;

    private Dialog confirmPayDialog;

    private Handler mHandler;
    private Runnable mRunnable;

    public ParisBuyOrderModel(ParisBuyOrderActivity view) {
        this.view = view;
    }

    public void uploadParisBuyOrderInfo() {
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisOrderDetail(view.mId)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<ParisOrderDetailResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ParisOrderDetailResponse>() {
                    @Override
                    public void onSuccess(ParisOrderDetailResponse data) {
                        if (data == null)
                            return;
                        ParisOrderDetailResponse.OrderInfo orderInfo = data.getOrder_info();
                        paymentBeans = data.getPayment_account();
                        if (orderInfo != null) {

                            String coin_name = StringUtils.subBeforeStr(orderInfo.getSymbol(), "/");
                            String unit_name = StringUtils.subAfterStr(orderInfo.getSymbol(), "/");
                            String money = new BigDecimal(orderInfo.getPrice())
                                    .multiply(new BigDecimal(orderInfo.getNumber()))
                                    .setScale(2, RoundingMode.HALF_DOWN).toPlainString();

                            view.tvTitle.setText(String.format(view.getString(R.string.paris_buy), coin_name));
                            view.tvOrderMoneyUnit.setText(String.format(view.getString(R.string.order_money_unit), unit_name));
                            view.tvNumUnit.setText(String.format(view.getString(R.string.coin_num), coin_name));
                            view.tvSinglePriceUnit.setText(String.format(view.getString(R.string.single_price_unit), unit_name));
                            view.tvOrderNo.setText(orderInfo.getOrder_no());
                            view.tvUserName.setText(data.getOther_info().getName());
                            view.tvOrderMoney.setText(money);
                            view.tvOrderNum.setText(orderInfo.getNumber());
                            view.tvSinglePrice.setText(new BigDecimal(orderInfo.getPrice()).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                            view.tvOrderTime.setText(orderInfo.getCreated_at());

                            if (orderInfo.getStatus() == 1) {
                                //待付款
                                view.tvOrderExplain.setVisibility(View.GONE);
                                view.tvCancelOrder.setVisibility(View.VISIBLE);
                                view.tvStatusThird.setTextColor(view.getResources().getColor(R.color.cl_838383));
                                view.ivStatusThird.setImageResource(R.drawable.icon_order_status_gray);
                                view.tvStatusFour.setTextColor(view.getResources().getColor(R.color.cl_838383));
                                view.ivStatusFour.setImageResource(R.drawable.icon_order_status_gray);
                                view.tvOrderStatus.setText(R.string.wait_pay);
                                view.tvOrderStatus.setTextColor(view.getResources().getColor(R.color.cl_f5a623));
                                view.llPayment.setVisibility(View.VISIBLE);
                                view.llReceive.setVisibility(View.GONE);
                                view.llTradeWarn.setVisibility(View.VISIBLE);
                                view.llWaitPay.setVisibility(View.VISIBLE);
                                view.tvChatOnlineSecond.setVisibility(View.GONE);

                                if (paymentBeans != null && paymentBeans.size() > 0) {
                                    PaymentBean paymentBean = paymentBeans.get(0);
                                    if (paymentBean != null) {
                                        //0:银行卡   1：支付宝  2：微信
                                        refreshPaymentInfo(paymentBean);
                                    }
                                }
                                //截至时间 = 创建订单时间 + 限制付款时间
//                                deadTime = TimeUtils.string2Millis(orderInfo.getCreated_at()) + orderInfo.getLimit_pay_time() * 60 * 1000;
//                                //剩余时间
//                                long timeSpan = TimeUtils.getTimeSpan2(deadTime, System.currentTimeMillis(), ConstUtils.TimeUnit.SEC);
                                view.tvConfirm.setText(String.format(view.getString(R.string.already_pay_please_release_coin_time), TimeUtils.getTimeSpanFormat(orderInfo.getLimit_pay_time())));
                                //初始化确定弹窗
                                initConfirmPayDialog(orderInfo.getLimit_pay_time());
                                if (orderInfo.getLimit_pay_time() > 0 && countDownTimer == null) {
                                    countDownTimer = new CountDownTimer(orderInfo.getLimit_pay_time() * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            if (view != null && view.tvConfirm != null)
                                                view.tvConfirm.setText(String.format(view.getString(R.string.already_pay_please_release_coin_time), TimeUtils.getTimeSpanFormat(millisUntilFinished / 1000)));
                                        }

                                        @Override
                                        public void onFinish() {
                                            //开启定时刷新
                                            startRefresh();
                                        }
                                    };
                                    countDownTimer.start();
                                }

                            } else if (orderInfo.getStatus() == 2) {
                                //已付款
                                view.tvOrderExplain.setVisibility(View.VISIBLE);
                                view.tvCancelOrder.setVisibility(View.GONE);
                                view.tvStatusThird.setTextColor(view.getResources().getColor(R.color.white));
                                view.ivStatusThird.setImageResource(R.drawable.icon_order_status_white);
                                view.tvStatusFour.setTextColor(view.getResources().getColor(R.color.white));
                                view.ivStatusFour.setImageResource(R.drawable.icon_order_status_white);
                                view.tvOrderStatus.setText(R.string.wait_other_side_release_coin);
                                view.tvOrderStatus.setTextColor(view.getResources().getColor(R.color.theme));
                                view.llPayment.setVisibility(View.GONE);
                                view.llReceive.setVisibility(View.VISIBLE);
                                view.llTradeWarn.setVisibility(View.GONE);
                                view.llWaitPay.setVisibility(View.GONE);
                                view.tvChatOnlineSecond.setVisibility(View.VISIBLE);
                                //截至时间 = 创建订单时间 + 限制放币时间
//                                long deadline = TimeUtils.string2Millis(orderInfo.getCreated_at()) + orderInfo.getLimit_finish_time() * 60 * 1000;
//                                //剩余时间
//                                long timeSpan = TimeUtils.getTimeSpan2(deadline, System.currentTimeMillis(), ConstUtils.TimeUnit.SEC);

                                if (orderInfo.getLimit_finish_time() > 0) {
                                    if (countDownTimer2 == null) {
                                        view.tvOrderExplain.setText(String.format(view.getString(R.string.time_end_release_coin), TimeUtils.getTimeSpanFormat(orderInfo.getLimit_finish_time())));
                                        countDownTimer2 = new CountDownTimer(orderInfo.getLimit_finish_time() * 1000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                if (view != null && view.tvOrderExplain != null)
                                                    view.tvOrderExplain.setText(String.format(view.getString(R.string.time_end_release_coin), TimeUtils.getTimeSpanFormat(millisUntilFinished / 1000)));
                                            }

                                            @Override
                                            public void onFinish() {
                                                //开启定时刷新
                                                startRefresh();
                                            }
                                        };
                                        countDownTimer2.start();
                                    }
                                }else {
                                    if (countDownTimer2 != null)
                                        countDownTimer2.cancel();
                                    view.tvOrderExplain.setText(String.format(view.getString(R.string.time_end_release_coin), TimeUtils.getTimeSpanFormat(orderInfo.getLimit_finish_time())));
                                }
                                switch (orderInfo.getPayment_method()) {
                                    case 0:
                                        view.tvReceiveWay.setText(orderInfo.getPayment_bank());
                                        break;
                                    case 1:
                                        view.tvReceiveWay.setText(view.getString(R.string.zfb));
                                        break;
                                    case 2:
                                        view.tvReceiveWay.setText(view.getString(R.string.wechat));
                                        break;
                                }
                                view.tvReceiveAccount.setText(StringUtils.addBrackets(orderInfo.getPayment_account()));
                                view.tvReceiveName.setText(orderInfo.getPayment_name());
                                view.tvPayAmount.setText(money + " " + unit_name);
                                view.tvPayTime.setText(orderInfo.getPayment_at());

                                startRefresh();
                            } else {
                                //订单已取消或已完成
                                ARouter.getInstance().build(RouterMap.PARIS_COIN_ORDER_DETAIL)
                                        .withInt(PageConstant.ID, view.mId)
                                        .navigation();
                                view.finish();
                            }
                        }
                    }
                });
    }

    //开启定时刷新，若已开启则不刷新
    private void startRefresh(){
        if (mHandler == null || mRunnable == null) {
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    uploadParisBuyOrderInfo();
                    mHandler.postDelayed(this, REFRESH_TIME);
                }
            };
            mHandler.postDelayed(mRunnable, REFRESH_TIME);
        }
    }

    //打开支付方式选择弹窗
    public void openSelectPaymentWayDialog() {
        if (paymentBeans != null && paymentBeans.size() > 0) {
            new PaymentWaySelectDialog(view)
                    .setPayments(paymentBeans)
                    .setSelectPos(view.mPayMentWay)
                    .setSelectPayWayListener(new PaymentWaySelectDialog.SelectPayWayListener() {

                        @Override
                        public void selectPayment(int type, PaymentBean paymentBean) {
                            refreshPaymentInfo(paymentBean);
                        }
                    })
                    .builder()
                    .show();
        }
    }

    //取消订单弹窗
    public void openCancelOrderDialog() {
        new CustomDialog(view)
                .setTitle(R.string.cancel_order)
                .setContent(R.string.cancel_order_dialog_explain)
                .setLeftBtnStr(R.string.think_about_again)
                .setRightBtnStr(R.string.cancel_order)
                .setCancelable(false)
                .setSubmitListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RetrofitManager.getInstance().create(ApiService.class)
                                .parisOrderCancel(view.mId)
                                .subscribeOn(Schedulers.io())
                                .compose(view.<BaseResponse<PaymentBean>>bindUntilEvent(ActivityEvent.DESTROY))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseObserver<PaymentBean>() {
                                    @Override
                                    public void onSuccess(PaymentBean data) {
                                        ToastUtils.showMessage(R.string.cancel_success);
                                        ARouter.getInstance().build(RouterMap.PARIS_COIN_ORDER_DETAIL)
                                                .withInt(PageConstant.ID, view.mId)
                                                .navigation();
                                        view.finish();
                                    }
                                });
                    }
                })
                .builder()
                .show();
    }

    public void initConfirmPayDialog(long deadTime) {
        if (confirmPayDialog == null) {
            if (mPaymentBean == null)
                return;
            String pay_way;
            if (mPaymentBean.getType() == 0) {
                pay_way = view.tvBankName.getText().toString();
            } else {
                pay_way = view.tvPayWay.getText().toString();
            }
            confirmPayDialog = new ConfirmPayDialog(view)
                    .setPaymentInfo(deadTime, pay_way, mPaymentBean.getAccount(), view.tvOrderMoney.getText().toString())
                    .setConfirmListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //确认付款
                            confirmPay();
                        }
                    })
                    .builder();
        }
    }

    //确认已付款弹窗
    public void openConfirmPayDialog() {
        if (confirmPayDialog != null)
            confirmPayDialog.show();
    }

    //确认付款
    private void confirmPay() {
        RetrofitManager.getInstance().create(ApiService.class)
                .parisOrderPay(view.mId, mPaymentBean.getId())
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<PaymentBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PaymentBean>() {
                    @Override
                    public void onSuccess(PaymentBean data) {
                        ToastUtils.showMessage(R.string.confirm_pay_success);
                        uploadParisBuyOrderInfo();
                    }
                });
    }

    //刷新付款方式
    public void refreshPaymentInfo(PaymentBean paymentBean) {
        if (paymentBean == null)
            return;
        mPaymentBean = paymentBean;
        view.mPayMentWay = paymentBean.getType();
        switch (mPaymentBean.getType()) {
            case 0:
                view.tvPayWay.setText(R.string.bank_transfer);
                view.tvPayWay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_bank_card, 0, R.drawable.icon_arrow_right_gray, 0);
                view.llBank.setVisibility(View.VISIBLE);
                view.llZfb.setVisibility(View.GONE);
                view.llWechat.setVisibility(View.GONE);
                view.tvBankName.setText(mPaymentBean.getBank_name());
                view.tvBankReceiver.setText(mPaymentBean.getName());
                view.tvBankCardNo.setText(mPaymentBean.getAccount());
                view.tvBankBelong.setText(mPaymentBean.getBranch_name());
                break;
            case 1:
                view.tvPayWay.setText(R.string.zfb);
                view.tvPayWay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_zfb, 0, R.drawable.icon_arrow_right_gray, 0);
                view.llBank.setVisibility(View.GONE);
                view.llZfb.setVisibility(View.VISIBLE);
                view.llWechat.setVisibility(View.GONE);
                view.tvZfbReceiver.setText(mPaymentBean.getName());
                view.tvZfbAccount.setText(mPaymentBean.getAccount());
                GlideLoadUtils.getInstance().glideLoad(view, mPaymentBean.getQr_code(), view.ivZfbReceivePic);
                break;
            case 2:
                view.tvPayWay.setText(R.string.wechat);
                view.tvPayWay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_wechat, 0, R.drawable.icon_arrow_right_gray, 0);
                view.llBank.setVisibility(View.GONE);
                view.llZfb.setVisibility(View.GONE);
                view.llWechat.setVisibility(View.VISIBLE);
                view.tvWechatReceiver.setText(mPaymentBean.getName());
                view.tvWechatAccount.setText(mPaymentBean.getAccount());
                GlideLoadUtils.getInstance().glideLoad(view, mPaymentBean.getQr_code(), view.ivWechatReceivePic);
                break;
        }
    }

    public void cancelAll() {
        if (mHandler != null && mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        if (countDownTimer != null)
            countDownTimer.cancel();
        if (countDownTimer2 != null)
            countDownTimer2.cancel();
    }

}
