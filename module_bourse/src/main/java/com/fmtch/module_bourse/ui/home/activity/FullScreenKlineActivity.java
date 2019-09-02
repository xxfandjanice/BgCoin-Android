package com.fmtch.module_bourse.ui.home.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.base.ReFreshDataBaseActivity;
import com.fmtch.module_bourse.bean.KChartBean;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.model.FullScreenKlineModel;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.TopicType;
import com.icechao.klinelib.adapter.KLineChartAdapter;
import com.icechao.klinelib.utils.Status;
import com.icechao.klinelib.view.KLineChartView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.K_LINE_FULL_SCREEN)
public class FullScreenKlineActivity extends ReFreshDataBaseActivity {


    @BindView(R2.id.tv_name)
    public TextView tvName;
    @BindView(R2.id.tv_close)
    public TextView tvClose;
    @BindView(R2.id.tv_transform_coin)
    public TextView tvTransformCoin;
    @BindView(R2.id.tv_high_price)
    public TextView tvHighPrice;
    @BindView(R2.id.tv_low_price)
    public TextView tvLowPrice;
    @BindView(R2.id.iv_finish)
    ImageView ivFinish;
    @BindView(R2.id.kLineView)
    public KLineChartView k_lineChartView;
    @BindView(R2.id.tv_ma)
    TextView tvMa;
    @BindView(R2.id.tv_boll)
    TextView tvBoll;
    @BindView(R2.id.iv_main_visible)
    ImageView ivMainVisible;
    @BindView(R2.id.tv_macd)
    TextView tvMacd;
    @BindView(R2.id.tv_kdj)
    TextView tvKdj;
    @BindView(R2.id.tv_rsi)
    TextView tvRsi;
    @BindView(R2.id.tv_wr)
    TextView tvWr;
    @BindView(R2.id.iv_second_visible)
    ImageView ivSecondVisible;
    @BindView(R2.id.tab_time)
    TabLayout tabTime;
    @BindView(R2.id.iv_k_line_set)
    ImageView ivKLineSet;
    public String symbol;


    List<String> timeTitles ;
    String[] tabKlineType = {KLineActivity.K_LINE_MIN_HOUR, KLineActivity.K_LINE_1_MIN, KLineActivity.K_LINE_5_MIN, KLineActivity.K_LINE_15_MIN, KLineActivity.K_LINE_30_MIN, KLineActivity.K_LINE_1_HOUR, KLineActivity.K_LINE_4_HOUR, KLineActivity.K_LINE_1_DAY, KLineActivity.K_LINE_1_WEEK};
    public String marketName;
    private FullScreenKlineModel model;
    public KLineChartAdapter<KChartBean> adapter;
    public String tickerTopic;                          //行情主题名称
    public String klineTopic;                           //K线主题名称
    public boolean tickerSubscribeResult = false;       //行情是否订阅成功
    public ArrayList<String> topics = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_full_screen_kline;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        //全屏
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        //设置夜晚主题模式
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initTimeTitles();
        symbol = getIntent().getStringExtra(PageConstant.SYMBOL);
        if (TextUtils.isEmpty(symbol)) {
            ToastUtils.showShortToast("交易对不能为空");
            return;
        }
        tvName.setText(symbol);
        String[] split = symbol.split("/");
        marketName = split[1];
        initTab();
        model = new FullScreenKlineModel(this);
        //初始化头部数据
        model.updateTopData();
        //初始化K线图
        model.initKLineChartView(KLineActivity.K_LINE_15_MIN);
        //订阅的主题,默认15min
        klineTopic = String.format(TopicType.KLINE, symbol, "15");
        tickerTopic = TopicType.TICKER_ASSIGN + symbol;
        topics.add(klineTopic);
        topics.add(tickerTopic);
    }

    private void initTimeTitles() {
        timeTitles = new ArrayList<>();
        timeTitles.add(getString(R.string.time_sharing));
        timeTitles.add(getString(R.string.one_min));
        timeTitles.add(getString(R.string.five_min));
        timeTitles.add(getString(R.string.fifteen_min));
        timeTitles.add(getString(R.string.thirty_min));
        timeTitles.add(getString(R.string.one_hour));
        timeTitles.add(getString(R.string.four_hour));
        timeTitles.add(getString(R.string.one_day));
        timeTitles.add(getString(R.string.one_week));
    }

    private void initTab() {
        for (String title : timeTitles) {
            tabTime.addTab(tabTime.newTab().setText(title));
        }
        tabTime.getTabAt(3).select();
        tabTime.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                model.switchKLineTimeType(tabKlineType[position]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    @OnClick({R2.id.iv_finish, R2.id.tv_ma, R2.id.tv_boll, R2.id.iv_main_visible, R2.id.tv_macd, R2.id.tv_kdj, R2.id.tv_rsi, R2.id.tv_wr, R2.id.iv_second_visible, R2.id.iv_k_line_set})
    public void onViewClicked(View view) {
        int id = view.getId();
        k_lineChartView.hideSelectData();
        int mainChartSelectedPosition = 0;
        int secondChartSelectedPosition = -1;
        if (id == R.id.iv_finish) {
            finish();
        } else if (id == R.id.tv_ma) {
            updateMainChartText(mainChartSelectedPosition, tvMa);
            k_lineChartView.changeMainDrawType(Status.MainStatus.MA);
        } else if (id == R.id.tv_boll) {
            mainChartSelectedPosition = 1;
            updateMainChartText(mainChartSelectedPosition, tvBoll);
            k_lineChartView.changeMainDrawType(Status.MainStatus.BOLL);
        } else if (id == R.id.iv_main_visible) {
            mainChartSelectedPosition = -1;
            updateMainChartText(mainChartSelectedPosition, null);
            k_lineChartView.changeMainDrawType(Status.MainStatus.NONE);
        } else if (id == R.id.tv_macd) {
            secondChartSelectedPosition = 0;
            k_lineChartView.setIndexDraw(Status.IndexStatus.MACD);
            updateSecondChartText(secondChartSelectedPosition, tvMacd);
        } else if (id == R.id.tv_kdj) {
            secondChartSelectedPosition = 1;
            k_lineChartView.setIndexDraw(Status.IndexStatus.KDJ);
            updateSecondChartText(secondChartSelectedPosition, tvKdj);
        } else if (id == R.id.tv_rsi) {
            secondChartSelectedPosition = 2;
            k_lineChartView.setIndexDraw(Status.IndexStatus.RSI);
            updateSecondChartText(secondChartSelectedPosition, tvRsi);
        } else if (id == R.id.tv_wr) {
            secondChartSelectedPosition = 3;
            k_lineChartView.setIndexDraw(Status.IndexStatus.WR);
            updateSecondChartText(secondChartSelectedPosition, tvWr);
        } else if (id == R.id.iv_second_visible) {
            updateSecondChartText(secondChartSelectedPosition, null);
            k_lineChartView.setIndexDraw(Status.IndexStatus.NONE);
        } else if (id == R.id.iv_k_line_set) {

        }
    }

    private void updateMainChartText(int selectedPosition, TextView textView) {
        tvBoll.setTextColor(getResources().getColor(R.color.dialog_text_color));
        tvMa.setTextColor(getResources().getColor(R.color.dialog_text_color));
        ivMainVisible.setImageResource(selectedPosition == -1 ? R.mipmap.icon_k_line_gone : R.mipmap.icon_k_line_visible);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.theme));
        }
    }

    private void updateSecondChartText(int selectedPosition, TextView textView) {
        tvMacd.setTextColor(getResources().getColor(R.color.dialog_text_color));
        tvRsi.setTextColor(getResources().getColor(R.color.dialog_text_color));
        tvKdj.setTextColor(getResources().getColor(R.color.dialog_text_color));
        tvWr.setTextColor(getResources().getColor(R.color.dialog_text_color));
        ivSecondVisible.setImageResource(selectedPosition == -1 ? R.mipmap.icon_k_line_gone : R.mipmap.icon_k_line_visible);
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.theme));
        }
    }

    @Override
    public void pollingData() {
        model.updateTopData();
    }

    @Override
    public List<String> getTopics() {
        return topics;
    }

    @Override
    public SubscribeCallBack getSubscribeCallBack() {
        return model.getSubscribeCallBack(topics);
    }

    @Override
    public OnMessageCallBack getOnMessageCallBack() {
        return model.getOnMessageCallBack(topics);
    }

    @Override
    public WebSocketConnectStateListener getSocketConnectListener() {
        return model.getSocketConnectListener();
    }

    @Override
    public String getSubscribeKey() {
        return this.getClass().getSimpleName();
    }
}
