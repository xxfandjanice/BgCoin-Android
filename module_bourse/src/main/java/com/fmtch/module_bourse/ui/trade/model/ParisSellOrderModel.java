package com.fmtch.module_bourse.ui.trade.model;


import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.InputPwdDialogUtils;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.CommonDialog;
import com.fmtch.base.widget.dialog.InputPayPwdDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.PaymentBean;
import com.fmtch.module_bourse.bean.response.ParisOrderDetailResponse;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.ParisSellOrderActivity;
import com.fmtch.module_bourse.utils.TimeUtils;
import com.fmtch.module_bourse.widget.dialog.ConfirmReceiveDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;


import java.math.BigDecimal;
import java.math.RoundingMode;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParisSellOrderModel {

    private ParisSellOrderActivity view;

    private static final long REFRESH_TIME = 5000;

    private ParisOrderDetailResponse.OrderInfo orderInfo;

    private Dialog confrimReceiveDialog;
    private CountDownTimer countDownTimer, countDownTimer2;

    private Handler mHandler;
    private Runnable mRunnable;

    public ParisSellOrderModel(ParisSellOrderActivity view) {
        this.view = view;
    }

    public void uploadParisSellOrderInfo() {
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
                        orderInfo = data.getOrder_info();
                        if (orderInfo != null) {

                            String coin_name = StringUtils.subBeforeStr(orderInfo.getSymbol(), "/");
                            String unit_name = StringUtils.subAfterStr(orderInfo.getSymbol(), "/");

                            view.tvTitle.setText(String.format(view.getString(R.string.paris_sell), coin_name));
                            view.tvOrderMoneyUnit.setText(String.format(view.getString(R.string.order_money_unit), unit_name));
                            view.tvNumUnit.setText(String.format(view.getString(R.string.coin_num), coin_name));
                            view.tvSinglePriceUnit.setText(String.format(view.getString(R.string.single_price_unit), unit_name));
                            view.tvOrderNo.setText(orderInfo.getOrder_no());
                            view.tvUserNick.setText(data.getOther_info().getUsername());
                            view.tvUserName.setText(data.getOther_info().getName());
                            String money = new BigDecimal(orderInfo.getPrice())
                                    .multiply(new BigDecimal(orderInfo.getNumber()))
                                    .setScale(2, RoundingMode.HALF_DOWN).toPlainString();
                            view.tvOrderMoney.setText(money);
                            view.tvOrderNum.setText(orderInfo.getNumber());
                            view.tvSinglePrice.setText(new BigDecimal(orderInfo.getPrice()).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                            view.tvOrderTime.setText(orderInfo.getCreated_at());
                            view.tvOrderRemark.setText(orderInfo.getRemark());

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

                            if (orderInfo.getStatus() == 1) {
                                //等待对方支付
                                view.tvStatusThird.setTextColor(view.getResources().getColor(R.color.cl_838383));
                                view.ivStatusThird.setImageResource(R.drawable.icon_order_status_gray);
                                view.tvOrderStatus.setText(R.string.wait_other_side_pay);
                                view.tvOrderStatus.setTextColor(view.getResources().getColor(R.color.cl_f5a623));
                                view.tvConfirm.setEnabled(false);
                                view.tvConfirm.setBackgroundResource(R.color.cl_4D007AFF);
                                view.llReceive.setVisibility(View.GONE);
                                view.llAskServer.setVisibility(View.GONE);
                                view.tvWarn.setVisibility(View.GONE);
                                view.tvCallPhone.setVisibility(View.GONE);
                                if (orderInfo.getLimit_pay_time() > 0) {
                                    if (countDownTimer == null) {
                                        view.tvConfirm.setText(String.format(view.getString(R.string.confirm_receive_go_release_coin_time)
                                                , TimeUtils.getTimeSpanFormat(orderInfo.getLimit_pay_time())));
                                        countDownTimer = new CountDownTimer(orderInfo.getLimit_pay_time() * 1000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                if (view != null && view.tvConfirm != null)
                                                    view.tvConfirm.setText(String.format(view.getString(R.string.confirm_receive_go_release_coin_time), TimeUtils.getTimeSpanFormat(millisUntilFinished / 1000)));
                                            }

                                            @Override
                                            public void onFinish() {
                                                uploadParisSellOrderInfo();
                                            }
                                        };
                                        countDownTimer.start();
                                    }
                                }else {
                                    if (countDownTimer != null)
                                        countDownTimer.cancel();
                                    view.tvConfirm.setText(String.format(view.getString(R.string.confirm_receive_go_release_coin_time)
                                            , TimeUtils.getTimeSpanFormat(orderInfo.getLimit_pay_time())));
                                }

                                if (mHandler == null || mRunnable == null) {
                                    mHandler = new Handler();
                                    mRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            uploadParisSellOrderInfo();
                                            mHandler.postDelayed(this, REFRESH_TIME);
                                        }
                                    };
                                    mHandler.postDelayed(mRunnable, REFRESH_TIME);
                                }
                            } else if (orderInfo.getStatus() == 2) {
                                //对方已支付
                                view.tvStatusThird.setTextColor(view.getResources().getColor(R.color.white));
                                view.ivStatusThird.setImageResource(R.drawable.icon_order_status_white);
                                view.tvOrderStatus.setText(R.string.other_side_payed);
                                view.tvOrderStatus.setTextColor(view.getResources().getColor(R.color.theme));
                                view.tvConfirm.setEnabled(true);
                                view.tvConfirm.setBackgroundResource(R.color.theme);
                                view.llReceive.setVisibility(View.VISIBLE);
                                view.llAskServer.setVisibility(View.VISIBLE);
                                view.tvWarn.setVisibility(View.VISIBLE);
                                view.tvCallPhone.setVisibility(View.VISIBLE);
                                //当前时间
//                                long currentTimeMillis = System.currentTimeMillis();
//                                //截至时间 = 创建订单时间 + 限制放币时间
//                                deadTime = TimeUtils.string2Millis(orderInfo.getCreated_at()) + orderInfo.getLimit_finish_time() * 60 * 1000;
//                                //剩余时间
//                                long timeSpan = TimeUtils.getTimeSpan2(deadTime, currentTimeMillis, ConstUtils.TimeUnit.SEC);
                                view.tvConfirm.setText(String.format(view.getString(R.string.confirm_receive_go_release_coin_time), TimeUtils.getTimeSpanFormat(orderInfo.getLimit_finish_time())));
                                initConfrimReceiveDialog(orderInfo.getLimit_finish_time());
                                if (countDownTimer2 == null) {
                                    countDownTimer2 = new CountDownTimer(orderInfo.getLimit_finish_time() * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            if (view != null && view.tvConfirm != null)
                                                view.tvConfirm.setText(String.format(view.getString(R.string.confirm_receive_go_release_coin_time), TimeUtils.getTimeSpanFormat(millisUntilFinished / 1000)));
                                        }

                                        @Override
                                        public void onFinish() {
                                           uploadParisSellOrderInfo();
                                        }
                                    };
                                    countDownTimer2.start();
                                }
                            }else {
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

    public void initConfrimReceiveDialog(long deadTime){
        if (confrimReceiveDialog == null){
            confrimReceiveDialog =  new ConfirmReceiveDialog(view)
                    .setPaymentInfo(deadTime, view.tvReceiveWay.getText().toString(), view.tvReceiveAccount.getText().toString(), view.tvOrderMoney.getText().toString())
                    .setConfirmListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //确认收款
                            showInputPayPwdDialog();
                        }
                    })
                    .builder();
        }
    }

    //确认已收款弹窗
    public void openConfirmReceiveDialog() {
        if (confrimReceiveDialog != null)
            confrimReceiveDialog.show();
    }

    /**
     * 输入资金密码的弹窗
     */
    private void showInputPayPwdDialog() {
        int show = InputPwdDialogUtils.showPwdDialog();
        if (show == InputPwdDialogUtils.STATUS_UN_SET_PAY_PWD) {
            CommonDialog dialog = new CommonDialog(view);
            dialog.showMsg(view.getString(com.fmtch.base.R.string.no_set_pay_pwd), false);
            dialog.setBtnConfirmText(view.getString(R.string.go_set));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.SET_ASSETS_PWD).navigation();
                }
            });
        } else if (show == InputPwdDialogUtils.STATUS_UN_SHOW) {
            confirmReceive("");
        } else if (show == InputPwdDialogUtils.STATUS_SHOW) {
            new InputPayPwdDialog(view)
                    .setOnConfirmSuccessListener(new InputPayPwdDialog.OnConfirmSuccessListener() {
                        @Override
                        public void onConfirmSuccess(String pay_pwd) {
                            confirmReceive(pay_pwd);
                        }
                    })
                    .builder()
                    .show();
        }
    }

    //确定收款
    private void confirmReceive(final String pay_pwd) {
        RetrofitManager.getInstance().create(ApiService.class)
                .parisOrderFinish(view.mId, pay_pwd)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<PaymentBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PaymentBean>() {
                    @Override
                    public void onSuccess(PaymentBean data) {
                        if (!TextUtils.isEmpty(pay_pwd)) {
                            //保存支付密码校验成功时间
                            SpUtils.put(KeyConstant.KEY_PAY_PWD_SUCCESS_TIME, System.currentTimeMillis());
                        }
                        ToastUtils.showMessage(R.string.confirm_receive_success);
                        ARouter.getInstance().build(RouterMap.PARIS_COIN_ORDER_DETAIL)
                                .withInt(PageConstant.ID, view.mId)
                                .navigation();
                        view.finish();
                    }
                });
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
