package com.fmtch.module_bourse.ui.trade.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.StatusBarUtils;
import com.fmtch.base.utils.UserInfoIntercept;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.MerchantHomePageAdapter;
import com.fmtch.module_bourse.bean.MerchantHomePageBean;
import com.fmtch.module_bourse.ui.trade.model.MerchantHomePageModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.MERCHANT_HOMEPAGE, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class MerchantHomePageActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    public Toolbar toolbar;
    @BindView(R2.id.app_bar)
    public AppBarLayout appBar;
    @BindView(R2.id.rv)
    public RecyclerView rv;
    @BindView(R2.id.iv_avatar)
    public ImageView ivAvatar;
    @BindView(R2.id.iv_add_black)
    public ImageView ivAddBlack;
    @BindView(R2.id.tv_attention)
    public TextView tvAttention;
    @BindView(R2.id.tv_name)
    public TextView tvName;
    @BindView(R2.id.tv_register_time)
    public TextView tvRegisterTime;
    @BindView(R2.id.tv_certification_level)
    public TextView tvCertificationLevel;
    @BindView(R2.id.tv_order_total)
    public TextView tvOrderTotal;
    @BindView(R2.id.tv_complete_rate)
    public TextView tvCompleteRate;
    @BindView(R2.id.tv_pay_time)
    public TextView tvPayTime;
    @BindView(R2.id.tv_put_coin_time)
    public TextView tvPutCoinTime;
    @BindView(R2.id.iv_bg)
    public ImageView ivBg;
    @BindView(R2.id.tv_release_black)
    public TextView tvReleaseBlack;
    @BindView(R2.id.txt_order_total)
    public TextView txtOrderTotal;
    @BindView(R2.id.txt_complete_rate)
    public TextView txtCompleteRate;
    @BindView(R2.id.txt_pay_time)
    public TextView txtPayTime;
    @BindView(R2.id.txt_put_coin_time)
    public TextView txtPutCoinTime;

    public List<MerchantHomePageBean.BuyOrSellBean> list;
    public BaseQuickPageStateAdapter<MerchantHomePageBean.BuyOrSellBean, BaseViewHolder> adapter;
    public MerchantHomePageModel model;
    public boolean isAttention = false;  //是否关注
    public boolean isBlack = false;       //是否拉黑
    public int userId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_merchant_home_page;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        list = new ArrayList<>();
        initAdapter();
        model = new MerchantHomePageModel(this);
        userId = getIntent().getIntExtra(PageConstant.ID, -1);
        model.getData(userId);
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setTransparentForImageView(this, null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MerchantHomePageAdapter(this, R.layout.item_merchant_home_page);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MerchantHomePageBean.BuyOrSellBean item = list.get(position);
                if (UserInfoIntercept.userBindMobile(MerchantHomePageActivity.this) && UserInfoIntercept.userAuth(MerchantHomePageActivity.this)) {
                    //已经绑定手机号并实名认证
                    if (item.getSide().equals("SELL")) {
                        ARouter.getInstance().build(RouterMap.PARIS_BUY)
                                .withInt(PageConstant.ID, item.getId())
                                .withInt(PageConstant.SCALE, item.getOtc_num_decimals())
                                .navigation();
                    } else if (item.getSide().equals("BUY")) {
                        if (UserInfoIntercept.havePayment(MerchantHomePageActivity.this)) {
                            //用户设置收付款方式
                            ARouter.getInstance().build(RouterMap.PARIS_SELL)
                                    .withInt(PageConstant.ID, item.getId())
                                    .withInt(PageConstant.SCALE, item.getOtc_num_decimals())
                                    .navigation();
                        }
                    }
                }
            }
        });
    }


    @OnClick({R2.id.iv_back, R2.id.iv_add_black, R2.id.tv_attention, R2.id.tv_release_black})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.iv_add_black || view.getId() == R.id.tv_release_black) {
            //拉黑/解除拉黑
            model.addOrReleaseBlack(userId);
        } else if (view.getId() == R.id.tv_attention) {
            //加关注/取消关注
            model.attention(userId);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //appBarLayout滑动监听
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                toolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.cl_2D3341), Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange()));
            }
        });
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }
}
