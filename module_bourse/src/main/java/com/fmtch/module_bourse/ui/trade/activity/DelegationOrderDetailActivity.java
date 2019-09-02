package com.fmtch.module_bourse.ui.trade.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.widget.dialog.ShowDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.DelegationOrderBean;
import com.fmtch.module_bourse.bean.ParisDelegationOrderDetailBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.math.BigDecimal;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = RouterMap.DELEGATION_ORDER_DETAIL, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class DelegationOrderDetailActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_state)
    TextView tvState;
    @BindView(R2.id.tv_currency_info)
    TextView tvCurrencyInfo;
    @BindView(R2.id.tv_delegation_type)
    TextView tvDelegationType;
    @BindView(R2.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R2.id.iv_type)
    ImageView ivType;
    @BindView(R2.id.tv_delegation_price_unit)
    TextView tvDelegationPriceUnit;
    @BindView(R2.id.tv_delegation_amount_success)
    TextView tvDelegationAmountSuccess;
    @BindView(R2.id.txt_delegation_amount_success)
    TextView txtDelegationAmountSuccess;
    @BindView(R2.id.tv_delegation_money)
    TextView tvDelegationMoney;
    @BindView(R2.id.tv_service_fee)
    TextView tvServiceFee;
    @BindView(R2.id.tv_single_order_limit)
    TextView tvSingleOrderLimit;
    @BindView(R2.id.tv_competitor_identity)
    TextView tvCompetitorIdentity;
    @BindView(R2.id.tv_competitor_auth_level)
    TextView tvCompetitorAuthLevel;

    private DelegationOrderBean orderBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_delegation_order_detail;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        orderBean = (DelegationOrderBean) getIntent().getSerializableExtra(PageConstant.DELEGATION_ORDER_DETAIL);
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
        //加载数据
        getOrderDetail();
    }

    private void getOrderDetail() {
        final ShowDialog dialog = ShowDialog.showDialog(this, getResources().getString(R.string.loading), true, null);
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisDelegationOrderDetail(String.valueOf(orderBean.getId()))
                .subscribeOn(Schedulers.io())
                .compose(this.<BaseResponse<ParisDelegationOrderDetailBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ParisDelegationOrderDetailBean>() {
                    @Override
                    public void onSuccess(ParisDelegationOrderDetailBean data) {
                        dialog.dismiss();
                        if (orderBean.getIs_pause() == 1) {
                            tvState.setText(getResources().getString(R.string.stop));
                        }
                        tvCurrencyInfo.setText(data.getSymbol());
                        tvOrderId.setText(data.getOrder_no());
                        if (data.getSide().equals("BUY")) {
                            tvDelegationType.setText("购买委托");
                            tvCurrencyInfo.setTextColor(getResources().getColor(R.color.theme));
                            ivType.setImageResource(R.mipmap.icon_buy_order);
                        } else if (data.getSide().equals("SELL")) {
                            tvDelegationType.setText("出售委托");
                            tvCurrencyInfo.setTextColor(getResources().getColor(R.color.cl_f55758));
                            ivType.setImageResource(R.mipmap.icon_sell_order);
                        }
                        tvDelegationPriceUnit.setText(data.getPrice());
                        tvDelegationAmountSuccess.setText(String.format("%s(%s)", getResources().getString(R.string.delegation_amount_success), orderBean.getSymbol().split("/")[0]));
                        tvDelegationAmountSuccess.setText(String.format("%s/%s", NumberUtils.stripMoneyZeros(data.getNumber()), NumberUtils.stripMoneyZeros(data.getDeal_number())));
                        tvDelegationMoney.setText(new BigDecimal(data.getPrice()).multiply(new BigDecimal(data.getNumber())).stripTrailingZeros().toPlainString());
                        tvServiceFee.setText(String.format("%s%%", new BigDecimal(data.getFee()).multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString()));
                        tvSingleOrderLimit.setText(String.format("%s - %s", NumberUtils.stripMoneyZeros(data.getMin_cny()), NumberUtils.stripMoneyZeros(data.getMax_cny())));
                        tvCompetitorAuthLevel.setText(data.getLimit_kyc_level());
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        dialog.dismiss();
                    }

                    @Override
                    public void onException(Throwable e) {
                        super.onException(e);
                        dialog.dismiss();
                    }
                });
    }
}
