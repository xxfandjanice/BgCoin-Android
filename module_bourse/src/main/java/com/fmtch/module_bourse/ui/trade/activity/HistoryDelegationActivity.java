package com.fmtch.module_bourse.ui.trade.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.MyPendOrderBean;
import com.fmtch.module_bourse.ui.trade.model.HistoryDelegationModel;
import com.fmtch.module_bourse.utils.RiseFallFormatUtils;
import com.fmtch.module_bourse.utils.ScreenUtils;
import com.fmtch.module_bourse.utils.SizeUtils;
import com.fmtch.module_bourse.utils.TimeUtils;
import com.fmtch.module_bourse.widget.dialog.CustomPopupWindow;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.HISTORY_DELEGATION, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class HistoryDelegationActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 100;
    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;
    @BindView(R2.id.toolbar)
    public Toolbar toolbar;
    @BindView(R2.id.ll_coin_filter)
    LinearLayout llCoinFilter;
    @BindView(R2.id.ll_time_filter)
    LinearLayout llTimeFilter;
    @BindView(R2.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R2.id.tv_symbol)
    TextView tvSymbol;

    public List<MyPendOrderBean> list;
    public BaseQuickPageStateAdapter<MyPendOrderBean, BaseViewHolder> adapter;
    private HistoryDelegationModel model;
    private CustomPopupWindow popupWindow;
    private boolean isShowPop;
    private TextView tvStartTime;
    private TextView tvEndTime;
    public String symbol;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history_delegation;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        list = new ArrayList<>();
        initAdapter();
        model = new HistoryDelegationModel(this);
        symbol = getIntent().getStringExtra(PageConstant.SYMBOL);
    }

    @Override
    protected void initView() {
        super.initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!TextUtils.isEmpty(symbol)) {
            tvSymbol.setText(symbol);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //自动刷新
        refreshLayout.autoRefresh(400);
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        //刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                model.getData(true, null, null, null);
            }
        });
        //加载更多
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                model.getData(false, null, null, null);
            }
        });
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<MyPendOrderBean, BaseViewHolder>(this, R.layout.item_history) {
            @Override
            protected void convert(BaseViewHolder helper, MyPendOrderBean item) {
                helper.setText(R.id.tv_state, item.getSide().equals("BUY") ? R.string.buy : R.string.sell);
                helper.setBackgroundRes(R.id.tv_state, item.getSide().equals("BUY") ? R.drawable.bg_circle_green : R.drawable.bg_circle_red);
                helper.setText(R.id.tv_name, item.getSymbol());
                String time = RiseFallFormatUtils.formatTime(item.getCreated_at());
                helper.setText(R.id.tv_time, time);
                helper.setText(R.id.tv_delegation_price, NumberUtils.stripMoneyZeros(item.getPrice()));
                helper.setText(R.id.tv_delegation_amounts, NumberUtils.stripMoneyZeros(item.getNumber()));
                helper.setText(R.id.tv_trade_amounts, NumberUtils.stripMoneyZeros(item.getDeal_number()));
                helper.setText(R.id.tv_trade_total, NumberUtils.stripMoneyZeros(item.getTotal()));
                BigDecimal divide = BigDecimal.ZERO;
                BigDecimal decimal = new BigDecimal(item.getDeal_number());
                if (decimal.compareTo(BigDecimal.ZERO) != 0) {
                    divide = new BigDecimal(item.getTotal()).divide(decimal, BigDecimal.ROUND_DOWN);
                }
                helper.setText(R.id.tv_trade_price, divide.stripTrailingZeros().toPlainString());
                int status = item.getStatus();
                //1-完全成交，2-已撤销
                helper.setText(R.id.tv_revoke, status == 2 ? R.string.revoked : R.string.traded);
                helper.setTextColor(R.id.tv_revoke, status == 2 ? getResources().getColor(R.color.cl_999999) : getResources().getColor(R.color.cl_333333));
                helper.setBackgroundRes(R.id.tv_revoke, status == 2 ? R.drawable.shape_rectangle_line_radius_gray : R.drawable.shape_rectangle_line_radius_black);
            }
        };
        rv.setAdapter(adapter);
    }


    @OnClick({R2.id.ll_coin_filter, R2.id.ll_time_filter})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.ll_coin_filter) {
            ARouter.getInstance().build(RouterMap.SEARCH_COIN)
                    .withBoolean(PageConstant.FINISH_WITH_DATA,true)
                    .navigation(this,REQUEST_CODE);
        } else if (id == R.id.ll_time_filter) {
            if (!isShowPop) {
                showFilterPop();
            }
        }
    }


    /**
     * 条件筛选弹窗
     */
    public void showFilterPop() {
        String currentDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy/MM/dd");
        popupWindow = new CustomPopupWindow.Builder()
                .setContext(this)
                .setContentView(R.layout.layout_pop_filter) //设置布局文件
                .setwidth(LinearLayout.LayoutParams.MATCH_PARENT) //设置宽度，
                .setheight(ScreenUtils.getScreenHeight(this) - SizeUtils.dp2px(95)) //设置高度
                .setOutSideCancel(false) //设置点击外部取消
//                    .setAnimationStyle(R.style.PopupAnimation) //设置popupwindow动画
//                    .setBackGroudAlpha(this, 0.7f)
                .builder() //
                .showAsLocation(llTimeFilter, Gravity.CENTER_HORIZONTAL, 0, 0);

        tvStartTime = (TextView) popupWindow.getItemView(R.id.tv_start_time);
        tvEndTime = (TextView) popupWindow.getItemView(R.id.tv_end_time);
        tvStartTime.setText(currentDate);
        tvEndTime.setText(currentDate);
        popupWindow.getItemView(R.id.tv_reset).setOnClickListener(this);
        popupWindow.getItemView(R.id.tv_sure).setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        popupWindow.setOnDismissListener(new CustomPopupWindow.OnDismissListener() {
            @Override
            public void OnPopupDismissListener() {
                isShowPop = false;
            }
        });
        isShowPop = true;
    }

    /**
     * 日期选择器
     *
     * @param id
     */
    public void showDateDialog(final int id) {
        int year = 0;
        int month = 0;
        int day = 0;
        if (id == R.id.tv_start_time) {
            String startTime = tvStartTime.getText().toString();
            String[] start = startTime.split("/");
            year = Integer.valueOf(start[0]);
            month = Integer.valueOf(start[1]);
            day = Integer.valueOf(start[2]);
        } else if (id == R.id.tv_end_time) {
            String endTime = tvEndTime.getText().toString();
            String[] end = endTime.split("/");
            year = Integer.valueOf(end[0]);
            month = Integer.valueOf(end[1]);
            day = Integer.valueOf(end[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "/" + (month + 1) + "/" + dayOfMonth;
                if (id == R.id.tv_start_time) {
                    tvStartTime.setText(date);
                } else if (id == R.id.tv_end_time) {
                    tvEndTime.setText(date);
                }
            }
        }, year, month - 1, day);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_start_time || id == R.id.tv_end_time) {
            showDateDialog(id);
        } else if (id == R.id.tv_reset) {
            String currentDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy/MM/dd");
            tvStartTime.setText(currentDate);
            tvEndTime.setText(currentDate);
        } else if (id == R.id.tv_sure) {
            String startTime = tvStartTime.getText().toString();
            String endTime = tvEndTime.getText().toString();
            popupWindow.dismiss();
            refreshLayout.autoRefreshAnimationOnly();
            model.getData(true, startTime, endTime, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String symbol = data.getStringExtra(PageConstant.SYMBOL);
            if (!TextUtils.isEmpty(symbol)) {
                this.symbol = symbol;
                tvSymbol.setText(symbol);
                refreshLayout.autoRefreshAnimationOnly();
                model.getData(true,null,null,symbol);
            }
        }
    }
}


