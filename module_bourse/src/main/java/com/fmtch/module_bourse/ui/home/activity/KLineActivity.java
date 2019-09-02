package com.fmtch.module_bourse.ui.home.activity;


import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ShotViewUtil;
import com.fmtch.base.utils.StatusBarUtils;
import com.fmtch.base.widget.dialog.ShareDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.MyTabPagerAdapter;
import com.fmtch.module_bourse.base.ReFreshDataBaseActivity;
import com.fmtch.module_bourse.bean.KChartBean;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.bean.TabEntity;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.fragment.DealOkFragment;
import com.fmtch.module_bourse.ui.home.fragment.DeepFragment;
import com.fmtch.module_bourse.ui.home.fragment.IntroduceFragment;
import com.fmtch.module_bourse.ui.home.model.KLineModel;
import com.fmtch.module_bourse.ui.trade.fragment.Coin_coinFragment;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.utils.ScreenUtils;
import com.fmtch.module_bourse.utils.SizeUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.TopicType;
import com.fmtch.module_bourse.widget.KLineSetPopup;
import com.fmtch.module_bourse.widget.dialog.CustomPopupWindow;
import com.icechao.klinelib.adapter.KLineChartAdapter;
import com.icechao.klinelib.utils.LogUtil;
import com.icechao.klinelib.utils.Status;
import com.icechao.klinelib.view.KLineChartView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.K_LINE)
public class KLineActivity extends ReFreshDataBaseActivity implements View.OnClickListener, KLineSetPopup.KlineSetOnClickListener {

    @BindView(R2.id.kLineView)
    public KLineChartView k_lineChartView;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R2.id.tab_time)
    CommonTabLayout tabTime;
    @BindView(R2.id.tab_below)
    SlidingTabLayout tabBelow;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;
    @BindView(R2.id.tv_buy_in)
    TextView tvBuyIn;
    @BindView(R2.id.tv_sell_out)
    TextView tvSellOut;
    @BindView(R2.id.iv_k_line_set)
    ImageView ivKlineSet;
    @BindView(R2.id.iv_add)
    public ImageView ivAdd;
    @BindView(R2.id.tv_close)
    public TextView tvClose;
    @BindView(R2.id.tv_rate)
    public TextView tvRate;
    @BindView(R2.id.tv_transform_coin)
    public TextView tvTransformCoin;
    @BindView(R2.id.tv_high)
    public TextView tvHigh;
    @BindView(R2.id.tv_low)
    public TextView tvLow;
    @BindView(R2.id.tv_trade_amount)
    public TextView tvTradeAmount;
    @BindView(R2.id.ll_tab_time)
    public LinearLayout llTabTime;

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> tabBelowTitles = new ArrayList<>();
    private KLineModel kLineModel;
    public String symbol;
    private CustomPopupWindow moreTimeDialog;
    private KLineSetPopup setDialog;
    private boolean isShowSetDialog;
    public KLineChartAdapter<KChartBean> adapter;
    List<String> timeTitles;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    String[] tabKlineType = {K_LINE_MIN_HOUR, K_LINE_15_MIN, K_LINE_1_HOUR, K_LINE_4_HOUR, K_LINE_1_DAY};

    public static final String K_LINE_MIN_HOUR = "0";
    public static final String K_LINE_1_MIN = "60000";
    public static final String K_LINE_5_MIN = "300000";
    public static final String K_LINE_15_MIN = "900000";
    public static final String K_LINE_30_MIN = "1800000";
    public static final String K_LINE_1_HOUR = "3600000";
    public static final String K_LINE_4_HOUR = "14400000";
    public static final String K_LINE_1_DAY = "86400000";
    public static final String K_LINE_1_WEEK = "604800000";
    public String marketName;
    public String tickerTopic;                          //行情主题名称
    public String klineTopic;                           //K线主题名称
    public boolean tickerSubscribeResult = false;       //行情是否订阅成功
    public ArrayList<String> topics = new ArrayList<>();
    public DeepFragment deepFragment;
    public DealOkFragment dealOkFragment;
    public SymbolBean symbolBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_kline;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initToolbar();
        timeTitles = new ArrayList<>();
        timeTitles.add(getString(R.string.time_sharing));
        timeTitles.add(getString(R.string.fifteen_min));
        timeTitles.add(getString(R.string.one_hour));
        timeTitles.add(getString(R.string.four_hour));
        timeTitles.add(getString(R.string.one_day));
        timeTitles.add(getString(R.string.more));
        symbol = getIntent().getStringExtra(PageConstant.SYMBOL);
        if (TextUtils.isEmpty(symbol)) {
            return;
        }
        symbolBean = Realm.getDefaultInstance().where(SymbolBean.class).equalTo("symbol", symbol).findFirst();
        if (symbolBean == null) {
            return;
        }
        marketName = symbolBean.getMarket_name();
        tvTitle.setText(symbol);
        ivAdd.setImageResource(symbolBean.isStar() ? R.mipmap.icon_k_line_add_selected : R.mipmap.icon_k_line_add);
        kLineModel = new KLineModel(this);
        //初始化头部数据
        kLineModel.updateTopData();
        //初始化K线图
        kLineModel.initKLineChartView(K_LINE_15_MIN);
        //订阅的主题,默认15min
        klineTopic = String.format(TopicType.KLINE, symbol, "15");
        tickerTopic = TopicType.TICKER_ASSIGN + symbol;
        topics.add(klineTopic);
        topics.add(tickerTopic);
    }

    @Override
    protected void initView() {
        super.initView();
        for (String time : timeTitles) {
            mTabEntities.add(new TabEntity(time, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        }
        tabTime.setTabData(mTabEntities);
        tabTime.setCurrentTab(1);

        tabBelowTitles.add(getResources().getString(R.string.deep));
        tabBelowTitles.add(getString(R.string.deal_ok));
        tabBelowTitles.add(getString(R.string.introduce));

        Bundle chooseBundle = new Bundle();
        chooseBundle.putString(PageConstant.SYMBOL, symbol);
        deepFragment = DeepFragment.newInstance(chooseBundle);
        dealOkFragment = DealOkFragment.newInstance(chooseBundle);
        fragments.add(deepFragment);
        fragments.add(dealOkFragment);
        fragments.add(IntroduceFragment.newInstance(chooseBundle));
        viewpager.setAdapter(new MyTabPagerAdapter(getSupportFragmentManager(), fragments, tabBelowTitles));
        viewpager.setOffscreenPageLimit(tabBelowTitles.size());
        tabBelow.setViewPager(viewpager);
    }

    private void initToolbar() {
        StatusBarUtils.StatusBarDarkLightMode(this, false, false);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.k_line_bg_top));
        setSupportActionBar(toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.icon_back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //动态设置K线图的高度(初始页面仅让深度、成交、简介TAB显示)
        int screenHeight = ScreenUtils.getScreenHeight(this);
        int klineViewHeight = screenHeight - SizeUtils.dp2px(45 + 90 + 40 + 10 + 40 + 65) - ScreenUtils.getStatusHeight(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) k_lineChartView.getLayoutParams();
        params.height = klineViewHeight;
        k_lineChartView.setLayoutParams(params);
    }

    @Override
    public void pollingData() {
        kLineModel.updateTopData();
    }

    @Override
    public List<String> getTopics() {
        return topics;
    }

    @Override
    public SubscribeCallBack getSubscribeCallBack() {
        return kLineModel.getSubscribeCallBack(topics);
    }

    @Override
    public OnMessageCallBack getOnMessageCallBack() {
        return kLineModel.getOnMessageCallBack(topics);
    }

    @Override
    public WebSocketConnectStateListener getSocketConnectListener() {
        return kLineModel.getSocketConnectListener();
    }

    @Override
    public String getSubscribeKey() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        tabTime.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == tabTime.getTabCount() - 1) {
                    //更多
                    showMoreTimeDialog();
                } else {
                    //K线
                    kLineModel.switchKLineTimeType(tabKlineType[position]);
                    tabTime.getTitleView(tabTime.getTabCount() - 1).setText(getString(R.string.more));
                }
            }

            @Override
            public void onTabReselect(int position) {
                if (position == tabTime.getTabCount() - 1) {
                    if (moreTimeDialog == null || !moreTimeDialog.isShowing()) {
                        showMoreTimeDialog();
                    } else {
                        moreTimeDialog.dismiss();
                    }
                }
            }
        });
    }

    @OnClick({R2.id.iv_share, R2.id.iv_fullscreen, R2.id.iv_add, R2.id.tv_buy_in, R2.id.tv_sell_out, R2.id.iv_k_line_set})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_add) {                //添加/取消自选
            kLineModel.addOrCancelChoose();
        } else if (id == R.id.iv_fullscreen) {  //全屏
            ARouter.getInstance().build(RouterMap.K_LINE_FULL_SCREEN)
                    .withString(PageConstant.SYMBOL, symbol)
                    .navigation();
        } else if (id == R.id.iv_share) {       //分享
            new ShareDialog(this)
                    .setShareBitmap(ShotViewUtil.getBitmapByView(coordinator))
                    .builder()
                    .show();
        } else if (id == R.id.tv_buy_in || id == R.id.tv_sell_out) {      //买入 卖出
            EventBean<MarketBean> eventBean = new EventBean<>(EventType.SELECT_SYMBOL);
            MarketBean bean = new MarketBean();
            bean.setBuyOrSell(id == R.id.tv_buy_in ? Coin_coinFragment.TRADE_BUY_IN:Coin_coinFragment.TRADE_SELL_OUT);
            bean.setSymbol(symbol);
            eventBean.setData(bean);
            EventBus.getDefault().post(eventBean);
            ARouter.getInstance().build(RouterMap.MAIN_PAGE)
                    .withInt(PageConstant.PAGE_INDEX, 2)
                    .navigation();
        } else if (id == R.id.iv_k_line_set) {   //设置
            showKLineSetDialog();
        }
    }

    /**
     * K线设置弹窗
     */
    private void showKLineSetDialog() {
        if (setDialog != null) {
            if (!isShowSetDialog) {
                setDialog.showAsDropDown(llTabTime);
                isShowSetDialog = true;
            } else {
                setDialog.dismiss();
                isShowSetDialog = false;
            }
            return;
        }
        setDialog = new KLineSetPopup(this);
        setDialog.addKlineSetOnClickListener(this);
        setDialog.showAsDropDown(llTabTime);
        isShowSetDialog = true;
    }

    /**
     * 更多弹窗
     */
    private void showMoreTimeDialog() {
        View moreView = LayoutInflater.from(KLineActivity.this).inflate(R.layout.layout_pop_k_line_more, null);
        moreView.findViewById(R.id.tv_one_min).setOnClickListener(this);
        moreView.findViewById(R.id.tv_five_min).setOnClickListener(this);
        moreView.findViewById(R.id.tv_thirty_min).setOnClickListener(this);
        moreView.findViewById(R.id.tv_one_week).setOnClickListener(this);
        moreTimeDialog = new CustomPopupWindow.Builder()
                .setContext(KLineActivity.this)
                .setContentView(moreView) //设置布局文件
                .setwidth(ScreenUtils.getScreenWidth(this) - SizeUtils.dp2px(30)) //设置宽度，
                .setheight(LinearLayout.LayoutParams.WRAP_CONTENT) //设置高度
                .setOutSideCancel(false) //设置点击外部取消
                .builder()
                .showAsLocation(llTabTime, Gravity.CENTER_HORIZONTAL, SizeUtils.dp2px(15), 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        /*************更多*****************/
        if (id == R.id.tv_one_min) {
            kLineModel.switchKLineTimeType(K_LINE_1_MIN);
            tabTime.getTitleView(tabTime.getTabCount() - 1).setText(getString(R.string.one_min));
            moreTimeDialog.dismiss();
        } else if (id == R.id.tv_five_min) {
            kLineModel.switchKLineTimeType(K_LINE_5_MIN);
            tabTime.getTitleView(tabTime.getTabCount() - 1).setText(getString(R.string.five_min));
            moreTimeDialog.dismiss();
        } else if (id == R.id.tv_thirty_min) {
            kLineModel.switchKLineTimeType(K_LINE_30_MIN);
            tabTime.getTitleView(tabTime.getTabCount() - 1).setText(getString(R.string.thirty_min));
            moreTimeDialog.dismiss();
        } else if (id == R.id.tv_one_week) {
            kLineModel.switchKLineTimeType(K_LINE_1_WEEK);
            tabTime.getTitleView(tabTime.getTabCount() - 1).setText(getString(R.string.one_week));
            moreTimeDialog.dismiss();
        }
    }

    @Override
    public void klineSetTvOnClick(String tvType) {
        k_lineChartView.hideSelectData();
        isShowSetDialog = false;
        switch (tvType) {
            case KLineSetPopup.MA:
                k_lineChartView.changeMainDrawType(Status.MainStatus.MA);
                break;
            case KLineSetPopup.BOLL:
                k_lineChartView.changeMainDrawType(Status.MainStatus.BOLL);
                break;
            case KLineSetPopup.MACD:
                k_lineChartView.setIndexDraw(Status.IndexStatus.MACD);
                break;
            case KLineSetPopup.KDJ:
                k_lineChartView.setIndexDraw(Status.IndexStatus.KDJ);
                break;
            case KLineSetPopup.RSI:
                k_lineChartView.setIndexDraw(Status.IndexStatus.RSI);
                break;
            case KLineSetPopup.WR:
                k_lineChartView.setIndexDraw(Status.IndexStatus.WR);
                break;
        }
    }

    @Override
    public void klineSetHide(String ivType) {
        isShowSetDialog = false;
        if (ivType.equals(KLineSetPopup.MAIN_GONE)) {
            k_lineChartView.changeMainDrawType(Status.MainStatus.NONE);
        } else if (ivType.equals(KLineSetPopup.SECOND_GONE)) {
            k_lineChartView.setIndexDraw(Status.IndexStatus.NONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Drawable upArrow = getResources().getDrawable(R.drawable.icon_back);
        upArrow.setColorFilter(getResources().getColor(R.color.cl_333333), PorterDuff.Mode.SRC_ATOP);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }
}
