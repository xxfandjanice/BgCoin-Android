package com.fmtch.module_bourse.ui.home.drawer;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.ui.BaseFragment;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.event.ParisCoinFilterEvent;
import com.fmtch.module_bourse.widget.PayMethodView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wtc on 2019/7/30
 */
public class ParisCoinFilterDrawerFragment extends BaseFragment {

    @BindView(R2.id.et_paris_trade_min_money)
    EditText etParisTradeMinMoney;
    @BindView(R2.id.pay_method_view)
    PayMethodView payMethodView;
    private DrawerLayout drawerLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_drawer_paris_trade_filter;
    }


    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    /**
     * 法币交易页面-抽屉-筛选点击事件
     */
    @OnClick({R2.id.tv_clear, R2.id.tv_complete})
    public void onViewClickedByParisFilter(View view) {
        if (view.getId() == R.id.tv_clear) {
            //清除
            payMethodView.clearSelectedPayMethod();
            etParisTradeMinMoney.setText("");
        } else if (view.getId() == R.id.tv_complete) {
            //完成
            drawerLayout.closeDrawers();
            EventBean<ParisCoinFilterEvent> event = new EventBean<>();
            ParisCoinFilterEvent data = new ParisCoinFilterEvent();
            data.setMinMoney(etParisTradeMinMoney.getText().toString());
            data.setSelectedPayMethods(payMethodView.getSelectedPayMethod());
            event.setData(data);
            event.setTag(EventType.PARIS_COIN_FILTER);
            EventBus.getDefault().post(event);
        }
    }
}
