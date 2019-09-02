package com.fmtch.module_bourse.ui.trade.model;


import android.text.TextUtils;
import android.view.View;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.widget.dialog.CustomDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.response.ParisOrderDetailResponse;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.ParisOrderDetailActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParisOrderDetailModel {

    private ParisOrderDetailActivity view;

    private ParisOrderDetailResponse.OtherInfo otherInfo;

    public ParisOrderDetailModel(ParisOrderDetailActivity view) {
        this.view = view;
    }

    public void uploadParisOrderInfo() {
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
                        otherInfo = data.getOther_info();
                        if (otherInfo != null) {
                            view.tvUserName.setText(otherInfo.getName());
                            if (otherInfo.getIs_black() == 0) {
                                //未拉黑
                                view.ivBlack.setVisibility(View.VISIBLE);
                                view.tvReleaseBlack.setVisibility(View.GONE);
                                view.tvChatOnline.setBackgroundResource(R.color.theme);
                                view.tvChatOnline.setEnabled(true);
                            } else {
                                //已拉黑
                                view.ivBlack.setVisibility(View.GONE);
                                view.tvReleaseBlack.setVisibility(View.VISIBLE);
                                view.tvChatOnline.setBackgroundResource(R.color.cl_4D007AFF);
                                view.tvChatOnline.setEnabled(false);
                            }
                        }
                        ParisOrderDetailResponse.OrderInfo orderInfo = data.getOrder_info();
                        if (orderInfo != null) {

                            String coin_name = StringUtils.subBeforeStr(orderInfo.getSymbol(), "/");
                            String unit_name = StringUtils.subAfterStr(orderInfo.getSymbol(), "/");

                            if (TextUtils.equals(orderInfo.getSide(), "BUY")) {
                                view.tvTitle.setText(String.format(view.getString(R.string.paris_buy), coin_name));
                            } else {
                                view.tvTitle.setText(String.format(view.getString(R.string.paris_sell), coin_name));
                            }
                            view.tvOrderMoneyUnit.setText(String.format(view.getString(R.string.order_money_unit), unit_name));
                            view.tvNumUnit.setText(String.format(view.getString(R.string.coin_num), coin_name));
                            view.tvSinglePriceUnit.setText(String.format(view.getString(R.string.single_price_unit), unit_name));

                            //订单金额
                            BigDecimal moneyBigDecimal = new BigDecimal(orderInfo.getPrice()).multiply(new BigDecimal(orderInfo.getNumber()));
                            String money = moneyBigDecimal.setScale(2, RoundingMode.HALF_DOWN).toPlainString();

                            if (orderInfo.getStatus() == 0) {
                                //已取消
                                view.llServiceFee.setVisibility(View.GONE);
                                view.llTruePrice.setVisibility(View.GONE);
                                view.llReceive.setVisibility(View.GONE);
                                //1-主动取消，2-超时未支付取消，3-管理员取消
                                switch (orderInfo.getCancel_type()) {
                                    case 1:
                                        view.tvStatus.setText(R.string.buyer_cancel);
                                        break;
                                    case 2:
                                        view.tvStatus.setText(R.string.time_out_un_pay_cancel);
                                        break;
                                    case 3:
                                        view.tvStatus.setText(R.string.seller_cancel);
                                        break;
                                    default:
                                        view.tvStatus.setText(R.string.buyer_cancel);
                                        break;
                                }
                                view.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_close_white, 0, 0, 0);
                                view.tvStatusExplain.setText(R.string.order_already_cancel);
                            } else if (orderInfo.getStatus() == 4) {

                                //平台服务费小于等于0时不显示
                                if (new BigDecimal(orderInfo.getFee()).compareTo(BigDecimal.ZERO) < 1) {
                                    view.llServiceFee.setVisibility(View.GONE);
                                    view.llTruePrice.setVisibility(View.GONE);
                                } else {
                                    view.llServiceFee.setVisibility(View.VISIBLE);
                                    view.llTruePrice.setVisibility(View.VISIBLE);
                                    BigDecimal feeBigDecimal = new BigDecimal(orderInfo.getFee());
                                    view.tvServiceFee.setText(feeBigDecimal.stripTrailingZeros().toPlainString());
                                    if (TextUtils.equals(orderInfo.getSide(), "BUY")) {
                                        view.tvTruePriceTitle.setText(R.string.true_in);
                                        view.tvTruePrice.setText(new BigDecimal(orderInfo.getNumber()).subtract(feeBigDecimal).stripTrailingZeros().toPlainString());
                                    } else {
                                        view.tvTruePriceTitle.setText(R.string.true_out);
                                        view.tvTruePrice.setText(new BigDecimal(orderInfo.getNumber()).add(feeBigDecimal).stripTrailingZeros().toPlainString());
                                    }
                                }


                                view.llReceive.setVisibility(View.VISIBLE);
                                view.tvStatus.setText(R.string.deal_done);
                                view.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_down_white, 0, 0, 0);
                                view.tvStatusExplain.setText(R.string.deal_already_done);
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
                                view.tvReleaseTime.setText(orderInfo.getFinish_at());
                            }

                            view.tvOrderNo.setText(orderInfo.getOrder_no());
                            view.tvOrderMoney.setText(money);
                            view.tvOrderNum.setText(orderInfo.getNumber());
                            view.tvSinglePrice.setText(new BigDecimal(orderInfo.getPrice()).setScale(2, RoundingMode.HALF_DOWN).toPlainString());
                            view.tvOrderTime.setText(orderInfo.getCreated_at());

                        }

                    }
                });
    }

    //拉黑或解除拉黑
    public void addOrReleaseBlack(boolean is_black) {
        if (is_black) {
            new CustomDialog(view)
                    .setTitle(R.string.add_black)
                    .setContent(R.string.are_you_sure_black_he)
                    .setLeftBtnStr(R.string.think_about_again)
                    .setRightBtnStr(R.string.sure)
                    .setCancelable(false)
                    .setSubmitListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            releaseBlackState(true);
                        }
                    })
                    .builder()
                    .show();
            return;
        }
        releaseBlackState(false);
    }

    private void releaseBlackState(final boolean is_black) {
        if (otherInfo == null)
            return;
        Map<String, String> map = new HashMap<>();
        map.put("user_id", otherInfo.getUser_id());
        map.put("type", is_black ? "1" : "2");  //1 拉黑  2取消拉黑
        RetrofitManager.getInstance().create(ApiService.class)
                .addOrReleaseBlack(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        uploadParisOrderInfo();
                    }

                });
    }


}
