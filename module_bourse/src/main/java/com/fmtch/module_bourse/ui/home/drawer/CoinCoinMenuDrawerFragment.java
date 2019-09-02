package com.fmtch.module_bourse.ui.home.drawer;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseFragment;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.ui.home.fragment.DealOkFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wtc on 2019/7/30
 */
public class CoinCoinMenuDrawerFragment extends BaseFragment {

    private DrawerLayout drawerLayout;
    private String symbol;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_drawer_coin_coin_menu;
    }


    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @OnClick({R2.id.drawer_trade_menu_close, R2.id.drawer_trade_menu_transform_money, R2.id.drawer_trade_menu_pend_order, R2.id.drawer_trade_menu_history_delegation, R2.id.drawer_trade_menu_bill})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.drawer_trade_menu_close) {
            //关闭抽屉
            drawerLayout.closeDrawers();
        } else if (id == R.id.drawer_trade_menu_transform_money) {
            //资金划转
            ARouter.getInstance().build(RouterMap.TRANSFER_COIN).navigation();
        } else if (id == R.id.drawer_trade_menu_pend_order) {
            //我的挂单
            ARouter.getInstance().build(RouterMap.MY_PEND_ORDER).navigation();
        } else if (id == R.id.drawer_trade_menu_history_delegation) {
            //历史委托
            ARouter.getInstance().build(RouterMap.HISTORY_DELEGATION)
                    .withString(PageConstant.SYMBOL, symbol)
                    .navigation();
        } else if (id == R.id.drawer_trade_menu_bill) {
            //账单
        }
    }
}
