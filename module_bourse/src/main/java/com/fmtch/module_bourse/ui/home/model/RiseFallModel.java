package com.fmtch.module_bourse.ui.home.model;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.NoticeBean;
import com.fmtch.module_bourse.bean.RiseFallBean;
import com.fmtch.module_bourse.bean.TodayMarketStateBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.home.fragment.HomeFragment;
import com.fmtch.module_bourse.ui.home.fragment.RiseOrFallListFragment;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.fmtch.module_bourse.widget.PercentView;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/17
 */
public class RiseFallModel {
    private RiseOrFallListFragment view;
    private TextView tvBulishPercent;
    private TextView tvBearishPercent;
    private PercentView percentView;
    private ImageView ivBulish;
    private ImageView ivBearish;

    public RiseFallModel(RiseOrFallListFragment view) {
        this.view = view;
    }

    /**
     * 获取列表数据
     *
     * @param fragmentType 不同的榜单
     */
    public void getData(int fragmentType) {

        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        Observable<BaseResponse<List<RiseFallBean>>> observable = null;

        if (fragmentType == HomeFragment.HOME_RISE_FALL_LIST) {
            //涨幅榜
            observable = apiService.getRiseFallList();
        } else if (fragmentType == HomeFragment.HOME_TRANSACTION_LIST) {
            //成交额帮
            observable = apiService.getTradeList();
        } else if (fragmentType == HomeFragment.HOME_NEW_COIN_LIST) {
            //新币榜
            observable = apiService.getNewCoinList();
        }
        if (observable == null) {
            return;
        }
        view.adapter.showLoadingPage();
        observable.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<RiseFallBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<RiseFallBean>>() {
                    @Override
                    public void onSuccess(List<RiseFallBean> data) {
                        if (data != null) {
                            view.list.clear();
                            if (data.size() == 0) {
                                view.adapter.showEmptyPage();
                            } else {
                                view.list.addAll(data);
                                view.adapter.setNewData(view.list);
                            }
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        view.adapter.showErrorPage(TextUtils.isEmpty(response.getMessage()) ? "" : response.getMessage());
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        view.adapter.showErrorPage("");
                    }
                });
    }

    /**
     * 获取今日市场情绪
     */
    public void getTodayMarketState() {
        RetrofitManager.getInstance().create(ApiService.class).getTodayMarketState()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<TodayMarketStateBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<TodayMarketStateBean>() {
                    @Override
                    public void onSuccess(final TodayMarketStateBean bean) {
                        if (bean != null && !TextUtils.isEmpty(bean.getFall_percent()) && !TextUtils.isEmpty(bean.getRise_percent())) {
                            //底部进度百分比
                            View percentLayout = LayoutInflater.from(view.getActivity()).inflate(R.layout.item_home_percent_view, null);
                            view.adapter.addFooterView(percentLayout);

                            tvBulishPercent = percentLayout.findViewById(R.id.tv_bulish_percent);
                            ivBulish = percentLayout.findViewById(R.id.iv_bulish);
                            tvBearishPercent = percentLayout.findViewById(R.id.tv_bearish_percent);
                            ivBearish = percentLayout.findViewById(R.id.iv_bearish);
                            percentView = percentLayout.findViewById(R.id.percent_view);
                            //更新百分比
                            updateTodayMarketValue(bean);

                            //点击看多
                            percentLayout.findViewById(R.id.ll_bulish).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    forecastTodayMarketState("1", bean.getStatus());
                                }
                            });
                            //点击看空
                            percentLayout.findViewById(R.id.ll_bearish).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    forecastTodayMarketState("2", bean.getStatus());
                                }
                            });
                        }
                    }
                });
    }

    /**
     * 预测今日市场情绪
     */
    private void forecastTodayMarketState(String type, int status) {
        String token = (String) SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN, "");
        if (TextUtils.isEmpty(token)) {
            ToastUtils.showShortToast(R.string.please_login);
            return;
        }
        if (status != 0) {
            ToastUtils.showShortToast(R.string.can_not_forecase_too);
            return;
        }
        //1-看多 2-看空
        RetrofitManager.getInstance().create(ApiService.class).postTodayMarketForecast(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<TodayMarketStateBean>() {
                    @Override
                    public void onSuccess(TodayMarketStateBean bean) {
                        if (bean != null && !TextUtils.isEmpty(bean.getFall_percent()) && !TextUtils.isEmpty(bean.getRise_percent())) {
                            updateTodayMarketValue(bean);
                        }
                    }
                });
    }


    private void updateTodayMarketValue(TodayMarketStateBean bean) {
        String rise_percent = bean.getRise_percent();
        String fall_percent = bean.getFall_percent();
        //0-没有预测过 1-预测了看多  2-预测了看空
        int status = bean.getStatus();

        if (status == 1) {
            ivBulish.setImageResource(R.mipmap.icon_bulish_selected);
        } else if (status == 2) {
            ivBearish.setImageResource(R.mipmap.icon_bearish_selected);
        }
        tvBulishPercent.setText(TextUtils.isEmpty(rise_percent) ? "" : rise_percent);
        tvBearishPercent.setText(TextUtils.isEmpty(fall_percent) ? "" : fall_percent);

        float rise = Float.parseFloat(rise_percent.replace("%", ""));
        float fall = Float.parseFloat(fall_percent.replace("%", ""));
        percentView.setINum(rise);
        percentView.setONum(fall);
    }
}
