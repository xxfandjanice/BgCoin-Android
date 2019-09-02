package com.fmtch.module_bourse.ui.home.model;


import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.BannerBean;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.bean.NoticeBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.glide.GlideImageLoader;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.home.fragment.HomeFragment;
import com.fmtch.module_bourse.utils.ScreenUtils;
import com.fmtch.module_bourse.utils.SizeUtils;
import com.fmtch.module_bourse.widget.pagerlayoutmanager.PagerGridLayoutManager;
import com.fmtch.module_bourse.widget.pagerlayoutmanager.PagerGridSnapHelper;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/9
 */
public class HomeModel {

    private HomeFragment view;
    private BaseQuickAdapter<MarketBean, BaseViewHolder> dataAdapter;

    public HomeModel(HomeFragment homeFragment) {
        this.view = homeFragment;
    }

    /**
     * 加载数据
     */
    public void loadData() {
        initTopBanner();
        initNoticeView();
        initDataBanner();
    }

    /**
     * 轮播图
     */
    private void initTopBanner() {

        CardView.LayoutParams layoutParams = (CardView.LayoutParams) view.banner.getLayoutParams();
        int width = ScreenUtils.getScreenWidth(view.getActivity()) - SizeUtils.dp2px(15) * 2;
        int height = (int) (width / 2.65);
        layoutParams.width = width;
        layoutParams.height = height;
        view.banner.setLayoutParams(layoutParams);

        RetrofitManager.getInstance().create(ApiService.class).getBanners()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<BannerBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<BannerBean>>() {
                    @Override
                    public void onSuccess(List<BannerBean> banners) {
                        if (banners != null && banners.size() > 0) {
                            view.bannerUrls.clear();
                            view.bannerUrls.addAll(banners);
                            view.banner.setImageLoader(new GlideImageLoader());
                            List<String> imageUrls = new ArrayList<>();
                            for (BannerBean bean : view.bannerUrls) {
                                imageUrls.add(bean.getPicture());
                            }
                            view.banner.setImages(imageUrls);
                            view.banner.start();
                        }
                    }
                });
    }

    /**
     * 系统公告
     */
    private void initNoticeView() {
        RetrofitManager.getInstance().create(ApiService.class).getSystemNotices()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<NoticeBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<NoticeBean>>() {
                    @Override
                    public void onSuccess(List<NoticeBean> notices) {
                        if (notices != null && notices.size() > 0 && view.noticeView != null) {
                            view.notices.clear();
                            view.notices.addAll(notices);
                            view.noticeView.addNotice(view.notices);
                            view.noticeView.startFlipping();
                        }
                    }
                });
    }

    /**
     * 涨跌-banner
     */
    private void initDataBanner() {
        if (dataAdapter == null) {
            PagerGridLayoutManager layoutManager = new PagerGridLayoutManager(1, 3, PagerGridLayoutManager.HORIZONTAL);
            view.dataRv.setLayoutManager(layoutManager);
            dataAdapter = new BaseQuickAdapter<MarketBean, BaseViewHolder>(R.layout.item_home_horizo_data_banner, view.dataBanners) {
                @Override
                protected void convert(BaseViewHolder helper, MarketBean item) {
                    helper.setText(R.id.tv_name, item.getSymbol());
                    helper.setText(R.id.tv_amounts, NumberUtils.stripMoneyZeros(item.getClose()));
                    BigDecimal rate = NumberUtils.getRate(item.getClose(), item.getOpen());
                    if (rate.compareTo(BigDecimal.ZERO) > 0) {
                        helper.setTextColor(R.id.tv_percent, view.getResources().getColor(R.color.cl_03c087));
                        helper.setTextColor(R.id.tv_amounts, view.getResources().getColor(R.color.cl_03c087));
                    } else {
                        helper.setTextColor(R.id.tv_percent, view.getResources().getColor(R.color.cl_f55758));
                        helper.setTextColor(R.id.tv_amounts, view.getResources().getColor(R.color.cl_f55758));
                    }
                    helper.setText(R.id.tv_percent, NumberUtils.bigDecimal2Percent(rate));
                    helper.setText(R.id.tv_coin, NumberUtils.transform2CnyOrUsd(item.getMarket_name(),item.getClose()));
                }
            };
            view.dataRv.setAdapter(dataAdapter);
            PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
            pageSnapHelper.attachToRecyclerView(view.dataRv);
            //item点击事件
            dataAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View v, int position) {
                    ARouter.getInstance().build(RouterMap.K_LINE)
                            .withString(PageConstant.SYMBOL, view.dataBanners.get(position).getSymbol())
                            .navigation();
                }
            });
        }

        RetrofitManager.getInstance().create(ApiService.class).getSystemRecommendList()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<MarketBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MarketBean>>() {
                    @Override
                    public void onSuccess(List<MarketBean> data) {
                        if (data != null && data.size() > 0) {
                            view.dataBanners.clear();
                            view.dataBanners.addAll(data);
                            dataAdapter.setNewData(view.dataBanners);
                        }
                    }
                });
    }

    public void updateDataBanner() {
        if (dataAdapter != null) {
            dataAdapter.notifyDataSetChanged();
        }
    }
}
