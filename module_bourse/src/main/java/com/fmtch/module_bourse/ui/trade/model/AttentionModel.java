package com.fmtch.module_bourse.ui.trade.model;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.bean.BlackOrAttentionBean;
import com.fmtch.module_bourse.bean.RiseFallBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.fragment.AttentionFragment;
import com.fmtch.module_bourse.ui.trade.fragment.BlackListFragment;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/6/25
 */
public class AttentionModel {

    private AttentionFragment view;
    private int page = 1;
    private int limit = 15;

    public AttentionModel(AttentionFragment view) {
        this.view = view;
    }

    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            ++page;
        }
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("per_page", limit + "");
        map.put("type", "1"); //1 关注  2黑名单
        RetrofitManager.getInstance().create(ApiService.class)
                .getBlackList(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<BlackOrAttentionBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<BlackOrAttentionBean>>() {
                    @Override
                    public void onSuccess(List<BlackOrAttentionBean> data) {
                        if (data == null) {
                            return;
                        }
                        refreshOrLoadMore(isRefresh, true, data);
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        refreshOrLoadMore(isRefresh, false, null);
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        refreshOrLoadMore(isRefresh, false, null);
                    }
                });

    }

    /**
     * @param isRefresh 刷新/加载更多
     * @param isSuccess 是否成功
     * @param data      数据
     */
    private void refreshOrLoadMore(boolean isRefresh, boolean isSuccess, List<BlackOrAttentionBean> data) {
        if (isRefresh) {
            view.refreshLayout.finishRefresh(isSuccess);
            if (isSuccess) {
                view.list.clear();
                if (data.size() == 0) {
                    view.adapter.showEmptyPage();
                } else {
                    view.list.addAll(data);
                    view.adapter.setNewData(view.list);
                }
            }
        } else {
            if (isSuccess) {
                view.list.addAll(data);
                if (data.size() < limit) {
                    if (data.size() == 0) {
                        page--;
                    }
                    view.refreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    view.refreshLayout.finishRefresh(true);
                }
                view.adapter.setNewData(view.list);
            } else {
                page--;
                view.refreshLayout.finishLoadMore(false);
            }
        }
    }
}
