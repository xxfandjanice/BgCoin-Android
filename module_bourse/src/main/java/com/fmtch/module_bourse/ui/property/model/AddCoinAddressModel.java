package com.fmtch.module_bourse.ui.property.model;


import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.bean.request.AddCoinAddressRequest;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.activity.AddCoinAddressActivity;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;


import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCoinAddressModel {

    private AddCoinAddressActivity view;

    public AddCoinAddressModel(AddCoinAddressActivity view) {
        this.view = view;
    }

    public void addCoinAddress(AddCoinAddressRequest request){
        RetrofitManager.getInstance().create(ApiService.class)
                .addCoinAddress(request)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showLongToast(R.string.add_success);
                        view.finish();
                    }
                });
    }

}
