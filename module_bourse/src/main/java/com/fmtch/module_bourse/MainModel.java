package com.fmtch.module_bourse;


import android.Manifest;
import android.text.TextUtils;

import com.fmtch.base.interfaces.PermissionListener;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserGesturesPwdInfo;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.pojo.response.CoinToUSDT;
import com.fmtch.base.pojo.response.RateInfo;
import com.fmtch.base.utils.AppInfoUtils;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.widget.dialog.CheckVersionDialog;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.module_bourse.bean.response.PaymentStatusResponse;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.utils.AppUtils;
import com.fmtch.module_bourse.websocket.WebSocketManager;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by wtc on 2019/5/14
 * Bourse_MainActivity 逻辑
 */
public class MainModel {

    private Bourse_MainActivity view;

    MainModel(Bourse_MainActivity view) {
        this.view = view;
    }

    void initData() {
        //权限申请
        requestPermissions();
        //初始化WebSocket
        WebSocketManager.getInstance().initWebSocket(null);
        //刷新汇率
        updateRate();
        //获取所有交易对
        getSymbols();
        //获取用户信息
        updateUserInfo();
        //版本更新检查
        updateVersion();
        //获取用户收付款设置
        getPaymentStatus();
    }

    //后台获取用户信息
    void updateUserInfo() {
        if (!AppUtils.isLogin()) {
            return;
        }
        RequestUtil.requestGet(API.GET_USER_INFO, new OnResponseListenerImpl() {
            @Override
            public void onNext(final SuperResponse response) {
                super.onNext(response);
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UserLoginInfo userLoginInfo = UserLoginInfo.objectFromData(new Gson().toJson(response));
                        realm.copyToRealmOrUpdate(userLoginInfo);
                        UserGesturesPwdInfo userGesturesPwdInfo = realm.where(UserGesturesPwdInfo.class)
                                .equalTo("id", userLoginInfo.getId())
                                .findFirst();
                        if (userGesturesPwdInfo == null) {
                            realm.createObject(UserGesturesPwdInfo.class, userLoginInfo.getId());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        refreshUserView();
                    }
                });
            }
        }, view);
    }

    void refreshUserView() {
        if (view.mineDrawerFragment != null) {
            view.mineDrawerFragment.refreshUserView();
        }
    }

    /**
     * 获取所有的交易对,缓存到数据库
     */
    private void getSymbols() {
        RetrofitManager.getInstance().create(ApiService.class)
                .getSymbols("")
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<SymbolBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<SymbolBean>>() {
                    @Override
                    public void onSuccess(final List<SymbolBean> data) {
                        if (data == null || data.size() <= 0) {
                            return;
                        }
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (SymbolBean symbolBean : data) {
                                    SymbolBean symbol = realm.where(SymbolBean.class).equalTo("symbol", symbolBean.getSymbol()).findFirst();
                                    if (symbol != null) {
                                        //update
                                        symbol.setStatus(symbolBean.getStatus());
                                        symbol.setCoin_name(symbolBean.getCoin_name());
                                        symbol.setCoin_id(symbolBean.getCoin_id());
                                        symbol.setMarket_name(symbolBean.getMarket_name());
                                        symbol.setMarket_id(symbolBean.getMarket_id());
                                        symbol.setPrice_decimals(symbolBean.getPrice_decimals());
                                        symbol.setPrice_step(symbolBean.getPrice_step());
                                        symbol.setNumber_decimals(symbolBean.getNumber_decimals());
                                        symbol.setNumber_step(symbolBean.getNumber_step());
                                        symbol.setNumber_min(symbolBean.getNumber_min());
                                        symbol.setTotal_min(symbolBean.getTotal_min());
                                    } else {
                                        //add
                                        SymbolBean bean = realm.copyToRealmOrUpdate(symbolBean);
                                    }
                                }
                            }
                        });
                    }
                });
    }

    //获取用户收付款设置
    void getPaymentStatus() {
        if (!AppUtils.isLogin()) {
            return;
        }
        RetrofitManager.getInstance().create(ApiService.class)
                .getPaymentStatus()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<PaymentStatusResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<PaymentStatusResponse>() {
                    @Override
                    public void onSuccess(PaymentStatusResponse data) {
                        if (data == null) {
                            return;
                        }
                        //设置收付款方式状态
                        if (data.getAlipay() == 1 || data.getBank() == 1 || data.getWechat() == 1) {
                            SpUtils.put(KeyConstant.KEY_PAYMENT_STATUS, true);
                        } else {
                            SpUtils.put(KeyConstant.KEY_PAYMENT_STATUS, false);
                        }
                    }
                });
    }


    void requestPermissions() {
        view.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, new PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        });
    }

    void updateVersion() {
        //版本更新
        RequestUtil.requestGet(API.CHECK_VERSION + "?app_name=ETF&&mobile_system=1", new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (!TextUtils.isEmpty(response.getVersion_code())) {
                    int localVersionNum = AppInfoUtils.getVersionNo(view);
                    int netVersionNum = Integer.parseInt(response.getVersion_code());
                    if (netVersionNum > localVersionNum) {
                        new CheckVersionDialog(view)
                                .setTitle(view.getResources().getString(R.string.version_update))
                                .setContent(response.getUpgrade_point().replace("\\n", "\n"))
                                .setVersionCode(response.getVersion_name())
                                .setDownApkUrl(response.getApk_url())
                                //type为1选择更新  为2时强制更新
                                .setCancelable(response.getType() == 1)
                                .builder()
                                .show();
                    }
                }
            }
        }, view);
    }


    //获取刷新汇率
    void updateRate() {
        RetrofitManager.getInstance().create(ApiService.class)
                .getRate()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<RateInfo>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<RateInfo>() {
                    @Override
                    public void onSuccess(final RateInfo data) {
                        if (data == null)
                            return;
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RateInfo rateInfo = realm.where(RateInfo.class).findFirst();
                                if (rateInfo == null) {
                                    rateInfo = realm.createObject(RateInfo.class);
                                }
                                rateInfo.setBtc_to_cny(data.getBtc_to_cny());
                                rateInfo.setUsdt_to_cny(data.getUsdt_to_cny());
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                RetrofitManager.getInstance().create(ApiService.class)
                                        .getCoinToBTC()
                                        .subscribeOn(Schedulers.io())
                                        .compose(view.<BaseResponse<RealmList<CoinToBTC>>>bindUntilEvent(ActivityEvent.DESTROY))
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new BaseObserver<RealmList<CoinToBTC>>() {
                                            @Override
                                            public void onSuccess(final RealmList<CoinToBTC> data) {
                                                if (data == null || data.size() < 1)
                                                    return;
                                                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        RateInfo rateInfo = realm.where(RateInfo.class).findFirst();
                                                        RealmList<CoinToBTC> coinToBTCS = rateInfo.getCoin_to_btc_list();
                                                        coinToBTCS.clear();
                                                        coinToBTCS.addAll(data);
                                                    }
                                                });
                                            }
                                        });
                                RetrofitManager.getInstance().create(ApiService.class)
                                        .getCoinToUSDT()
                                        .subscribeOn(Schedulers.io())
                                        .compose(view.<BaseResponse<RealmList<CoinToUSDT>>>bindUntilEvent(ActivityEvent.DESTROY))
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new BaseObserver<RealmList<CoinToUSDT>>() {
                                            @Override
                                            public void onSuccess(final RealmList<CoinToUSDT> data) {
                                                if (data == null || data.size() < 1)
                                                    return;
                                                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        RateInfo rateInfo = realm.where(RateInfo.class).findFirst();
                                                        RealmList<CoinToUSDT> coinToBTCS = rateInfo.getCoin_to_usdt_list();
                                                        coinToBTCS.clear();
                                                        coinToBTCS.addAll(data);
                                                    }
                                                });
                                            }
                                        });
                            }
                        });
                    }
                });
    }


}
