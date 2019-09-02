package com.fmtch.module_bourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StatusBarUtils;
import com.fmtch.module_bourse.adapter.MyPagerAdapter;
import com.fmtch.module_bourse.ui.home.drawer.CoinCoinMenuDrawerFragment;
import com.fmtch.module_bourse.ui.home.drawer.CoinCoinTradeDrawerFragment;
import com.fmtch.module_bourse.ui.home.drawer.MineDrawerFragment;
import com.fmtch.module_bourse.ui.home.drawer.ParisCoinFilterDrawerFragment;
import com.fmtch.module_bourse.ui.home.drawer.ParisCoinMenuDrawerFragment;
import com.fmtch.module_bourse.ui.home.fragment.HomeFragment;
import com.fmtch.module_bourse.ui.market.fragment.MarketFragment;
import com.fmtch.module_bourse.ui.property.fragment.PropertyFragment;
import com.fmtch.module_bourse.ui.trade.fragment.TradeFragment;
import com.fmtch.module_bourse.utils.KeyboardUtils;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.utils.SizeUtils;
import com.fmtch.module_bourse.widget.NoScrollViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

@Route(path = RouterMap.MAIN_PAGE)
public class Bourse_MainActivity extends BaseActivity {

    private static final String PAGE_INDEX = "pageIndex";

    @BindView(R2.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R2.id.tab_home)
    TextView tabHome;
    @BindView(R2.id.tab_market)
    TextView tabMarket;
    @BindView(R2.id.tab_trade)
    TextView tabTrade;
    @BindView(R2.id.tab_property)
    TextView tabProperty;
    @BindView(R2.id.drawer_layout_home)
    DrawerLayout drawerLayoutHome;
    @BindView(R2.id.drawer_left_container)
    FrameLayout drawerLeftContainer;
    @BindView(R2.id.drawer_right_container)
    FrameLayout drawerRightContainer;

    public static final int DRAWER_MINE = 0;
    public static final int DRAWER_COIN_COIN_TRADE_FILTER = 1;
    public static final int DRAWER_COIN_COIN_TRADE_MENU = 3;
    public static final int DRAWER_PARIS_COIN_TRADE_FILTER = 4;
    public static final int DRAWER_PARIS_COIN_TRADE_MENU = 5;


    private List<Fragment> fragments = new ArrayList<>();
    private MainModel mainModel;
    private TradeFragment tradeFragment;
    private TextView selectedTab;
    public MineDrawerFragment mineDrawerFragment;
    private CoinCoinTradeDrawerFragment coinCoinTradeDrawerFragment;
    private CoinCoinMenuDrawerFragment coinCoinMenuDrawerFragment;
    private ParisCoinMenuDrawerFragment parisCoinMenuDrawerFragment;
    private ParisCoinFilterDrawerFragment parisCoinFilterDrawerFragment;
    private MarketFragment marketFragment;
    private int pageIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bourse_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (savedInstanceState != null) {
            pageIndex = savedInstanceState.getInt(PAGE_INDEX);
        }
        mainModel = new MainModel(this);
        mainModel.initData();

        fragments.add(new HomeFragment());
        marketFragment = new MarketFragment();
        fragments.add(marketFragment);
        tradeFragment = new TradeFragment();
        fragments.add(tradeFragment);
        fragments.add(new PropertyFragment());
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setTransparentForImageView(this, null);
        StatusBarUtils.StatusBarDarkLightMode(this, true, true);
        viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments));
        setCurrentPage(pageIndex);
        viewpager.setOffscreenPageLimit(4);
        //禁止手势滑动关闭/打开抽屉
        drawerLayoutHome.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        drawerLayoutHome.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //抽屉关闭时隐藏软键盘
                KeyboardUtils.hideSoftInput(Bourse_MainActivity.this);
            }
        });
    }

    /**
     * 打开抽屉
     */
    public void openDrawerLayout(int drawerType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideDrawerFragment(transaction);
        int gravity = Gravity.START;
        ViewGroup.LayoutParams layoutParams = drawerLeftContainer.getLayoutParams();
        //抽屉-个人中心
        if (drawerType == DRAWER_MINE) {
            layoutParams.width = SizeUtils.dp2px(300);
            if (mineDrawerFragment == null) {
                mainModel.updateUserInfo();
                mineDrawerFragment = new MineDrawerFragment();
                mineDrawerFragment.setDrawerLayout(drawerLayoutHome);
                transaction.add(R.id.drawer_left_container, mineDrawerFragment);
            } else {
                transaction.show(mineDrawerFragment);
            }
            //抽屉-币币交易页面-币种筛选
        } else if (drawerType == DRAWER_COIN_COIN_TRADE_FILTER) {
            layoutParams.width = SizeUtils.dp2px(300);
            if (coinCoinTradeDrawerFragment == null) {
                coinCoinTradeDrawerFragment = new CoinCoinTradeDrawerFragment();
                coinCoinTradeDrawerFragment.setDrawerLayout(drawerLayoutHome);
                transaction.add(R.id.drawer_left_container, coinCoinTradeDrawerFragment);
            } else {
                transaction.show(coinCoinTradeDrawerFragment);
            }
            //抽屉-币币交易页面-菜单
        } else if (drawerType == DRAWER_COIN_COIN_TRADE_MENU) {
            layoutParams.width = SizeUtils.dp2px(160);
            if (coinCoinMenuDrawerFragment == null) {
                coinCoinMenuDrawerFragment = new CoinCoinMenuDrawerFragment();
                coinCoinMenuDrawerFragment.setDrawerLayout(drawerLayoutHome);
                transaction.add(R.id.drawer_left_container, coinCoinMenuDrawerFragment);
            } else {
                transaction.show(coinCoinMenuDrawerFragment);
            }
            coinCoinMenuDrawerFragment.setSymbol(tradeFragment.getCoinCoinSymbol());
            //抽屉-法币交易页面-菜单
        } else if (drawerType == DRAWER_PARIS_COIN_TRADE_MENU) {
            layoutParams.width = SizeUtils.dp2px(160);
            if (parisCoinMenuDrawerFragment == null) {
                parisCoinMenuDrawerFragment = new ParisCoinMenuDrawerFragment();
                parisCoinMenuDrawerFragment.setDrawerLayout(drawerLayoutHome);
                transaction.add(R.id.drawer_left_container, parisCoinMenuDrawerFragment);
            } else {
                transaction.show(parisCoinMenuDrawerFragment);
            }
            //抽屉-法币交易页面-筛选
        } else if (drawerType == DRAWER_PARIS_COIN_TRADE_FILTER) {
            gravity = Gravity.END;
            if (parisCoinFilterDrawerFragment == null) {
                parisCoinFilterDrawerFragment = new ParisCoinFilterDrawerFragment();
                parisCoinFilterDrawerFragment.setDrawerLayout(drawerLayoutHome);
                transaction.add(R.id.drawer_right_container, parisCoinFilterDrawerFragment);
            } else {
                transaction.show(parisCoinFilterDrawerFragment);
            }
        }
        if (gravity == Gravity.START) {
            drawerLeftContainer.setLayoutParams(layoutParams);
        }
        transaction.commitAllowingStateLoss();
        drawerLayoutHome.openDrawer(gravity);
    }

    private void hideDrawerFragment(FragmentTransaction transaction) {
        if (mineDrawerFragment != null) {
            transaction.hide(mineDrawerFragment);
        }
        if (coinCoinTradeDrawerFragment != null) {
            transaction.hide(coinCoinTradeDrawerFragment);
        }
        if (coinCoinMenuDrawerFragment != null) {
            transaction.hide(coinCoinMenuDrawerFragment);
        }
        if (parisCoinMenuDrawerFragment != null) {
            transaction.hide(parisCoinMenuDrawerFragment);
        }
        if (parisCoinFilterDrawerFragment != null) {
            transaction.hide(parisCoinFilterDrawerFragment);
        }
    }

    /**
     * 底部tab按钮-点击事件
     *
     * @param view
     */
    @OnClick({R2.id.tab_home, R2.id.tab_market, R2.id.tab_trade, R2.id.tab_property})
    public void onViewClickedByTab(View view) {
        int id = view.getId();
        if (id == R.id.tab_home) {
            pageIndex = 0;
        } else if (id == R.id.tab_market) {
            pageIndex = 1;
        } else if (id == R.id.tab_trade) {
            pageIndex = 2;
        } else if (id == R.id.tab_property) {
            String token = (String) SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN, "");
            if (TextUtils.isEmpty(token)) {
                selectedTab.setSelected(true);
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
                return;
            }
            pageIndex = 3;
        }
        setCurrentPage(pageIndex);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event.getTag() == EventType.USER_LOGIN) {
            mainModel.updateUserInfo();
            mainModel.getPaymentStatus();
        } else if (event.getTag() == EventType.UPLOAD_USER) {
            mainModel.updateUserInfo();
        } else if (event.getTag() == EventType.USER_EXIT) {
            //退出登录清空账号信息
            SpUtils.put(KeyConstant.KEY_LOGIN_TOKEN, "");
            SpUtils.put(KeyConstant.KEY_NO_REMIND_CREATE_ORDER, false);
            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<UserLoginInfo> userList = realm.where(UserLoginInfo.class).findAll();
                    userList.deleteAllFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    mainModel.refreshUserView();
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //非一级页面跳主页并指定页面
        pageIndex = intent.getIntExtra(PageConstant.PAGE_INDEX, -1);
        setCurrentPage(pageIndex);
    }

    private void setCurrentPage(int pageIndex) {
        if (pageIndex > -1 && pageIndex <= 3) {
            if (selectedTab != null) {
                selectedTab.setSelected(false);
            }
            switch (pageIndex) {
                case 0:
                    selectedTab = tabHome;
                    break;
                case 1:
                    selectedTab = tabMarket;
                    break;
                case 2:
                    selectedTab = tabTrade;
                    break;
                case 3:
                    selectedTab = tabProperty;
                    break;
            }
            selectedTab.setSelected(true);
            viewpager.setCurrentItem(pageIndex, false);
        }
    }


    public MarketFragment getMarketFragment() {
        return marketFragment;
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), getString(R.string.one_again_exit), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_INDEX, pageIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("Bourse_MainActivity", "onDestroy");
    }
}
