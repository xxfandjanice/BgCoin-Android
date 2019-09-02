package com.fmtch.module_bourse.ui.trade.model;


import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.net.API;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.module_bourse.bean.PaymentBean;
import com.fmtch.module_bourse.bean.response.PaymentResponse;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.PaymentSettingActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PaymentSettingModel {

    private PaymentSettingActivity view;

    public PaymentSettingModel(PaymentSettingActivity view) {
        this.view = view;
    }

    public void uploadPaymentList() {
        RetrofitManager.getInstance().create(ApiService.class)
                .getPaymentList()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<PaymentResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PaymentResponse>() {
                    @Override
                    public void onSuccess(PaymentResponse data) {
                        if (data == null) {
                            view.showEmptyView();
                            return;
                        }
                        view.list.clear();
                        view.list.addAll(data.getBank());
                        view.list.addAll(data.getAlipay());
                        view.list.addAll(data.getWechat());
                        view.mAdapter.setNewData(view.list);
                        if (view.list.size() < 1){
                            view.showEmptyView();
                        }else {
                            //判断用户是否有打开收付款设置
                            for (PaymentBean paymentBean : view.list){
                                if (paymentBean.getIs_open() == 1){
                                    SpUtils.put(KeyConstant.KEY_PAYMENT_STATUS,true);
                                    return;
                                }
                            }
                            SpUtils.put(KeyConstant.KEY_PAYMENT_STATUS,false);
                        }
                    }
                });
    }

    public void openOrClosePayment(int payment_id){
        RetrofitManager.getInstance().create(ApiService.class)
                .changePayment(API.TRADE_PAYMENT_CHANGE + payment_id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        uploadPaymentList();
                    }
                });
    }

    public void unbindPayment(int payment_id){
        RetrofitManager.getInstance().create(ApiService.class)
                .unbindPayment(API.TRADE_PAYMENT_UNBIND + payment_id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        uploadPaymentList();
                    }
                });
    }

}
