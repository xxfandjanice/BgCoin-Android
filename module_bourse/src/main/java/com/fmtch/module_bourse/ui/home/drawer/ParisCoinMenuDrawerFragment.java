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
import com.fmtch.base.utils.UserInfoIntercept;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wtc on 2019/7/30
 */
public class ParisCoinMenuDrawerFragment extends BaseFragment {

    private DrawerLayout drawerLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_drawer_paris_menu;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    @OnClick({R2.id.drawer_paris_menu_close, R2.id.drawer_paris_menu_delegation, R2.id.drawer_paris_menu_my_order, R2.id.drawer_paris_menu_pay_receive, R2.id.drawer_paris_menu_transform, R2.id.drawer_paris_menu_blacklist})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.drawer_paris_menu_close) {
            //关闭抽屉
            drawerLayout.closeDrawers();
        } else if (id == R.id.drawer_paris_menu_delegation) {
            //委托单
            ARouter.getInstance().build(RouterMap.DELEGATION_ORDER).navigation();
        } else if (id == R.id.drawer_paris_menu_my_order) {
            //我的订单
            ARouter.getInstance().build(RouterMap.PARIS_COIN_ORDER).navigation();
        } else if (id == R.id.drawer_paris_menu_pay_receive) {
            //收付款设置
            if (UserInfoIntercept.userBindMobile(getActivity()) && UserInfoIntercept.userAuth(getActivity())) {
                //已经绑定手机号并实名认证
                ARouter.getInstance().build(RouterMap.PAYMENT_SETTING).navigation();
            }
        } else if (id == R.id.drawer_paris_menu_transform) {
            //资金划转
            ARouter.getInstance().build(RouterMap.TRANSFER_COIN)
                    .withInt(PageConstant.COIN_TYPE, 5)//法币账户
                    .navigation();
        } else if (id == R.id.drawer_paris_menu_blacklist) {
            //黑名单/关注
            ARouter.getInstance().build(RouterMap.BLACKLIST_ATTENTION).navigation();
        }
    }
}
