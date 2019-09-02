package com.fmtch.module_bourse.ui.trade.model;


import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.request.AddPaymentRequest;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.AddPaymentActivity;
import com.fmtch.module_bourse.ui.trade.activity.PaymentSettingActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddPaymentModel {

    private AddPaymentActivity view;

    public AddPaymentModel(AddPaymentActivity view) {
        this.view = view;
    }

    public void addPaymentBank(String name,String account,String bank_name,String branch_name,String sms_code) {
        AddPaymentRequest addPaymentRequest = new AddPaymentRequest();
        addPaymentRequest.setName(name);
        addPaymentRequest.setAccount(account);
        addPaymentRequest.setBank_name(bank_name);
        addPaymentRequest.setBranch_name(branch_name);
        addPaymentRequest.setSms_code(sms_code);
        RetrofitManager.getInstance().create(ApiService.class)
                .addPaymentBank(addPaymentRequest)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showMessage(R.string.add_success);
                        view.setResult(PaymentSettingActivity.REQUEST_CODE);
                        view.finish();
                    }
                });
    }

    public void addPaymentZFB(String name,String alipay_account,String alipay_qr_code,String sms_code) {
        AddPaymentRequest addPaymentRequest = new AddPaymentRequest();
        addPaymentRequest.setName(name);
        addPaymentRequest.setAlipay_account(alipay_account);
        addPaymentRequest.setAlipay_qr_code(alipay_qr_code);
        addPaymentRequest.setSms_code(sms_code);
        RetrofitManager.getInstance().create(ApiService.class)
                .addPaymentZFB(addPaymentRequest)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showMessage(R.string.add_success);
                        view.setResult(PaymentSettingActivity.REQUEST_CODE);
                        view.finish();
                    }
                });
    }

    public void addPaymentWechat(String wechat_nickname,String wechat_account,String wechat_qr_code,String sms_code) {
        AddPaymentRequest addPaymentRequest = new AddPaymentRequest();
        addPaymentRequest.setWechat_nickname(wechat_nickname);
        addPaymentRequest.setWechat_account(wechat_account);
        addPaymentRequest.setWechat_qr_code(wechat_qr_code);
        addPaymentRequest.setSms_code(sms_code);
        RetrofitManager.getInstance().create(ApiService.class)
                .addPaymentWechat(addPaymentRequest)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showMessage(R.string.add_success);
                        view.setResult(PaymentSettingActivity.REQUEST_CODE);
                        view.finish();
                    }
                });
    }


}
