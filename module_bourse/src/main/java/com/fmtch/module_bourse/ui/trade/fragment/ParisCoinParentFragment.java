package com.fmtch.module_bourse.ui.trade.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.DialogUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.Bourse_MainActivity;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.MyTabPagerAdapter;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.bean.ParisCoinBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.PaymentSettingActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/6/25
 */
public class ParisCoinParentFragment extends LazyBaseFragment implements View.OnClickListener {
    @BindView(R2.id.tv_buy_sell)
    TextView tvBuySell;
    @BindView(R2.id.iv_filter)
    ImageView ivFilter;
    @BindView(R2.id.tab_layout)
    SlidingTabLayout tabLayout;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;

    private List<ParisCoinBean> tabs;
    private List<Fragment> fragments;
    private BottomSheetDialog buyOrSellDialog;
    public static String PARIS_TRADE_BUY = "SELL";  //我要购买(相对商家就是出售,所以接口传SELL)
    public static String PARIS_TRADE_SELL = "BUY";  //我要出售(同上)
    private String tradeType = ParisCoinParentFragment.PARIS_TRADE_BUY;  //出售或购买(默认购买)

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_parent_paris_coin;
    }

    @Override
    protected void initData() {
        super.initData();
        tabs = new ArrayList<>();
        fragments = new ArrayList<>();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        getParisCoinKindList();
    }

    @OnClick({R2.id.tv_buy_sell, R2.id.iv_filter})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_buy_sell) {
            showBuyOrSellDialog();
        } else if (id == R.id.iv_filter) {
            ((Bourse_MainActivity) getActivity()).openDrawerLayout(Bourse_MainActivity.DRAWER_PARIS_COIN_TRADE_FILTER);
        }
    }

    /**
     * 购买或出售dialog
     */
    public void showBuyOrSellDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_paris_switch_buy_sell, null);
        TextView tvWantBuy = view.findViewById(R.id.tv_want_buy);
        tvWantBuy.setOnClickListener(this);
        TextView tvWantSell = view.findViewById(R.id.tv_want_sell);
        tvWantSell.setOnClickListener(this);
        if (tradeType.equals(PARIS_TRADE_BUY)) {
            tvWantBuy.setTextColor(getResources().getColor(R.color.theme));
        } else {
            tvWantSell.setTextColor(getResources().getColor(R.color.theme));
        }
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        buyOrSellDialog = DialogUtils.showDetailBottomDialog(getActivity(), view);
        buyOrSellDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_want_buy) {
            tvBuySell.setText(R.string.want_to_buy);
            tradeType = PARIS_TRADE_BUY;
        } else if (id == R.id.tv_want_sell) {
            tvBuySell.setText(R.string.want_to_sell);
            tradeType = PARIS_TRADE_SELL;
        }
        for (Fragment fragment : fragments) {
            ((ParisCoinChildrenFragment) fragment).switchBuyOrSell(tradeType);
        }
        buyOrSellDialog.dismiss();
    }

    /**
     * 获取支持法币交易的币种
     */
    private void getParisCoinKindList() {
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisCoinKindList()
                .subscribeOn(Schedulers.io())
                .compose(this.<BaseResponse<List<ParisCoinBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ParisCoinBean>>() {
                    @Override
                    public void onSuccess(List<ParisCoinBean> data) {
                        if (data == null || data.size() <= 0) {
                            return;
                        }
                        tabs.addAll(data);
                        List<String> titles = new ArrayList<>();
                        for (ParisCoinBean tab : tabs) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PageConstant.KEY,tab);
                            fragments.add(ParisCoinChildrenFragment.newInstance(bundle));
                            titles.add(tab.getName());
                        }
                        viewpager.setAdapter(new MyTabPagerAdapter(getChildFragmentManager(), fragments, titles));
                        tabLayout.setViewPager(viewpager, titles.toArray(new String[0]));
                        viewpager.setOffscreenPageLimit(fragments.size());
                    }
                });
    }
}
