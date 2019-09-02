package com.fmtch.module_bourse.ui.home.model;

import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.KChartBean;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.activity.FullScreenKlineActivity;
import com.fmtch.module_bourse.ui.home.activity.KLineActivity;
import com.fmtch.module_bourse.websocket.BaseMessage;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.TopicType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.icechao.klinelib.adapter.KLineChartAdapter;
import com.icechao.klinelib.formatter.DateFormatter;
import com.icechao.klinelib.utils.DateUtil;
import com.icechao.klinelib.utils.Status;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/28
 */
public class FullScreenKlineModel {
    private FullScreenKlineActivity view;
    private OnMessageCallBack onMessageCallBack;
    private SubscribeCallBack subscribeCallBack;
    private WebSocketConnectStateListener socketConnectListener;


    public FullScreenKlineModel(FullScreenKlineActivity view) {
        this.view = view;
    }

    /**
     * 初始化K线图
     */
    public void initKLineChartView(String k_line_type) {
        view.adapter = new KLineChartAdapter<>();
        //初始化K线图
        view.k_lineChartView.setAdapter(view.adapter)
                //loading anim
                .setAnimLoadData(false)
                //set right can over range
                .setOverScrollRange(view.getWindowManager().getDefaultDisplay().getWidth() / 5)
                .showLoading()
                //set date label formater
                .setDateTimeFormatter(new DateFormatter() {
                    @Override
                    public String format(Date date) {
                        return DateUtil.HHMMTimeFormat.format(date);
                    }
                });
        //加载数据
        getKLineChart(k_line_type);
    }


    /**
     * 获取K线图数据
     */
    private void getKLineChart(final String k_line_type) {
        if (TextUtils.isEmpty(view.symbol)) {
            return;
        }
        if (Integer.valueOf(k_line_type) <= Integer.valueOf(KLineActivity.K_LINE_4_HOUR)) {
            view.k_lineChartView.setDateTimeFormatter(new DateFormatter() {
                @Override
                public String format(Date date) {
                    return DateUtil.MMddHHmmTimeFormat.format(date);
                }
            });
        } else {
            view.k_lineChartView.setDateTimeFormatter(new DateFormatter() {
                @Override
                public String format(Date date) {
                    return DateUtil.yyyyMMddFormat.format(date);
                }
            });
        }
        Map<String, String> map = new HashMap<>();
        map.put("symbol", view.symbol);
        map.put("range", k_line_type);
        RetrofitManager.getInstance().create(ApiService.class)
                .getKLineChart(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<KChartBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<KChartBean>>() {
                    @Override
                    public void onSuccess(List<KChartBean> data) {
                        if (data != null) {
                            view.subscribe(view.klineTopic);
                            //更新K线图
                            Collections.reverse(data);
                            view.adapter.resetData(data);
                            view.k_lineChartView.hideLoading();
                        }
                    }
                });
    }


    /**
     * 订阅主题的回调
     *
     * @param topics
     * @return
     */
    public SubscribeCallBack getSubscribeCallBack(final List<String> topics) {
        if (subscribeCallBack == null) {
            subscribeCallBack = new SubscribeCallBack() {
                @Override
                public void callBack(String topic, String action, boolean result) {
                    if (view.klineTopic.equals(topic)) {
                        //K线主题订阅成功
                    } else if (view.tickerTopic.equals(topic)) {
                        //行情主题订阅成功
                        view.tickerSubscribeResult = true;
                    }
                }
            };
        }
        return subscribeCallBack;
    }

    /**
     * 订阅主题的消息
     *
     * @param topics
     * @return
     */
    public OnMessageCallBack getOnMessageCallBack(final List<String> topics) {
        if (onMessageCallBack == null) {
            onMessageCallBack = new OnMessageCallBack() {

                @Override
                public void onMessage(String message) {
                    Gson gson = new GsonBuilder().create();
                    BaseMessage baseMessage = gson.fromJson(message, BaseMessage.class);
                    String channel = baseMessage.getChannel();
                    if (view.tickerTopic.equals(channel)) {
                        //行情主题消息
                        BaseMessage<MarketBean> baseResponse = gson.fromJson(message, new TypeToken<BaseMessage<MarketBean>>() {
                        }.getType());
                        MarketBean data = baseResponse.getData();
                        updateTopUI(data);
                    } else if (view.klineTopic.equals(channel)) {
                        //K线主题订阅消息
                        if (view.adapter.getDatas() == null || view.adapter.getDatas().size() <= 0) {
                            return;
                        }
                        BaseMessage<List<String>> baseResponse = gson.fromJson(message, new TypeToken<BaseMessage<List<String>>>() {
                        }.getType());
                        List<String> data = baseResponse.getData();
                        KChartBean kChartBean = new KChartBean();
                        kChartBean.setTime_open(Long.valueOf(data.get(0)));//时间
                        kChartBean.setPrice_open(Float.valueOf(data.get(1)));//开盘价
                        kChartBean.setPrice_high(Float.valueOf(data.get(2)));//最高价
                        kChartBean.setPrice_low(Float.valueOf(data.get(3)));//最低价
                        kChartBean.setPrice_close(Float.valueOf(data.get(4)));//收盘价
                        kChartBean.setTrades_number(Float.valueOf(data.get(5)));//成交量
                        kChartBean.setTrades_total(data.get(6));//成交金额
                        kChartBean.setTrades_count(data.get(7));//成交笔数
                        KChartBean lastData = view.adapter.getDatas().get(view.adapter.getCount() - 1);
                        if (kChartBean.getTime_open() > lastData.getTime_open()) {
                            //新增
                            view.adapter.addLast(kChartBean);
                        } else if (kChartBean.getTime_open() == lastData.getTime_open()) {
                            //更新
                            view.adapter.changeItem(view.adapter.getCount() - 1, kChartBean);
                        }
                    }

                }
            };
        }
        return onMessageCallBack;
    }

    /**
     * socket连接断开
     *
     * @return
     */
    public WebSocketConnectStateListener getSocketConnectListener() {
        if (socketConnectListener == null) {
            socketConnectListener = new WebSocketConnectStateListener() {
                @Override
                public void OnSocketConnectState(boolean isConnect) {
                    if (!isConnect) {
                        view.tickerSubscribeResult = false;
                    }
                }
            };
        }
        return socketConnectListener;
    }

    /**
     * 切换不同时间的K线图
     *
     * @param timeType 时间类型
     */
    public void switchKLineTimeType(String timeType) {
        view.k_lineChartView.hideSelectData();
        //分时图
        if (timeType.equals(KLineActivity.K_LINE_MIN_HOUR)) {
            view.k_lineChartView.setKlineState(Status.KlineStatus.TIME_LINE);
            view.adapter.resetData(view.adapter.getDatas());
            return;
        }
        view.k_lineChartView.setKlineState(Status.KlineStatus.K_LINE);
        //先加载接口数据
        getKLineChart(timeType);
        //再解除之前的K线主题订阅，订阅新的主题
        view.unSubscribe(view.klineTopic);
        view.topics.remove(view.klineTopic);
        switch (timeType) {
            case KLineActivity.K_LINE_1_MIN:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "1");
                break;
            case KLineActivity.K_LINE_5_MIN:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "5");
                break;
            case KLineActivity.K_LINE_15_MIN:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "15");
                break;
            case KLineActivity.K_LINE_30_MIN:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "30");
                break;
            case KLineActivity.K_LINE_1_HOUR:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "60");
                break;
            case KLineActivity.K_LINE_4_HOUR:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "240");
                break;
            case KLineActivity.K_LINE_1_DAY:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "1440");
                break;
            case KLineActivity.K_LINE_1_WEEK:
                view.klineTopic = String.format(TopicType.KLINE, view.symbol, "10080");
                break;
        }
        view.topics.add(view.klineTopic);
        view.subscribe(view.klineTopic);
    }

    /**
     * 更新头部数据
     */
    public void updateTopData() {
        if (view.tickerSubscribeResult) {
            return;
        }
        if (TextUtils.isEmpty(view.symbol)) {
            return;
        }
        RetrofitManager.getInstance().create(ApiService.class)
                .getMarkets(view.symbol)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<MarketBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MarketBean>>() {
                    @Override
                    public void onSuccess(List<MarketBean> data) {
                        view.subscribe(view.tickerTopic);
                        updateTopUI(data.get(0));
                    }
                });

    }

    private void updateTopUI(MarketBean marketBean) {
        if (marketBean == null || view == null || view.tvClose == null) {
            return;
        }
        view.tvClose.setText(NumberUtils.stripMoneyZeros(String.valueOf(marketBean.getClose())));
        view.tvTransformCoin.setText(NumberUtils.transform2CnyOrUsd(view.marketName, String.valueOf(marketBean.getClose())));
        BigDecimal rate = NumberUtils.getRate(String.valueOf(marketBean.getClose()), String.valueOf(marketBean.getOpen()));
        if (rate.compareTo(BigDecimal.ZERO) > 0) {
            view.tvClose.setTextColor(view.getResources().getColor(R.color.cl_03c087));
        } else {
            view.tvClose.setTextColor(view.getResources().getColor(R.color.cl_f55758));
        }
        view.tvHighPrice.setText(NumberUtils.stripMoneyZeros(String.valueOf(marketBean.getHigh())));
        view.tvLowPrice.setText(NumberUtils.stripMoneyZeros(String.valueOf(marketBean.getLow())));
    }
}
