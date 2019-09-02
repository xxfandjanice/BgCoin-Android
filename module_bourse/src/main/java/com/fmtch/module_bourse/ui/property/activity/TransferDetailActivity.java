package com.fmtch.module_bourse.ui.property.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountDetailBean;

import butterknife.BindView;

@Route(path = RouterMap.TRANSFER_DETAIL)
public class TransferDetailActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_add_or_sub)
    TextView tvAddOrSub;
    @BindView(R2.id.tv_num)
    TextView tvNum;
    @BindView(R2.id.tv_unit)
    TextView tvUnit;
    @BindView(R2.id.tv_type)
    TextView tvType;
    @BindView(R2.id.tv_status)
    TextView tvStatus;
    @BindView(R2.id.tv_trade_id)
    TextView tvTradeId;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.tv_text1)
    TextView tvText1;
    @BindView(R2.id.tv_text2)
    TextView tvText2;
    @BindView(R2.id.tv_orientation)
    TextView tvOrientation;
    @BindView(R2.id.ll_hash)
    LinearLayout llHash;

    @Autowired
    String coin_name;
    @Autowired
    AccountDetailBean transferInfo;

    @Autowired
    int type; // 1：充币    2：提币   3：划转
    @Autowired
    public String account_type;//类型（ account：总账户划转记录 spot_account：币币账户划转记录 otc_account：法币账户划转记录）


    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_detail;
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
        if (transferInfo != null) {
            tvNum.setText(NumberUtils.getFormatMoney(transferInfo.getNumber()));
            tvUnit.setText(coin_name);


            // 1：充币    2：提币   3：划转
            if (type == 1) {
                tvTitle.setText(getString(R.string.recharge_detail));
                //充值状态: 0:确认中 1:已完成
                if (NumberUtils.equalsInteger(transferInfo.getStatus(),0)){
                    tvStatus.setText(R.string.sure_ing);
                }else if (NumberUtils.equalsInteger(transferInfo.getStatus(),1)){
                    tvStatus.setText(R.string.done);
                }
                tvType.setText(R.string.drawer_recharge);
                tvTradeId.setText(transferInfo.getHash());
                tvAddOrSub.setText("+");
                tvTime.setText(TextUtils.isEmpty(transferInfo.getCreated_at()) ? "" : transferInfo.getCreated_at());
            } else if (type == 2) {
                tvTitle.setText(getString(R.string.draw_detail));
                // 提现状态: 0:审核中 1:已完成 2:已撤销  3和4: 处理中
                if (NumberUtils.equalsInteger(transferInfo.getStatus(),0)){
                    tvStatus.setText(R.string.checking);
                }else if (NumberUtils.equalsInteger(transferInfo.getStatus(),1)){
                    tvStatus.setText(R.string.done);
                }else if (NumberUtils.equalsInteger(transferInfo.getStatus(),2)){
                    tvStatus.setText(R.string.canceled_);
                }else {
                    tvStatus.setText(R.string.deal_ing);
                }
                tvType.setText(R.string.drawer_draw_coin);
                tvTradeId.setText(transferInfo.getHash());
                tvAddOrSub.setText("-");
                tvAddOrSub.setTextColor(getResources().getColor(R.color.color_common_text33));
                tvNum.setTextColor(getResources().getColor(R.color.color_common_text33));
                tvUnit.setTextColor(getResources().getColor(R.color.color_common_text33));
                tvTime.setText(TextUtils.isEmpty(transferInfo.getCreated_at()) ? "" : transferInfo.getCreated_at());
            } else if (type == 3) {
                tvTitle.setText(getString(R.string.transform_detail));
                tvType.setText(TextUtils.isEmpty(transferInfo.getType_text()) ? "" : transferInfo.getType_text());
                if (!TextUtils.isEmpty(transferInfo.getSign())) {
                    tvAddOrSub.setText(transferInfo.getSign());
                    if (TextUtils.equals(transferInfo.getSign(), "-")) {
                        tvAddOrSub.setTextColor(getResources().getColor(R.color.color_common_text33));
                        tvNum.setTextColor(getResources().getColor(R.color.color_common_text33));
                        tvUnit.setTextColor(getResources().getColor(R.color.color_common_text33));
                    }
                }
                tvText1.setText(getString(R.string.out_account));
                tvText2.setText(getString(R.string.in_account));
                if (transferInfo.getFrom_account().equals("account")) {
                    tvStatus.setText(getString(R.string.my_wallet));
                } else if (transferInfo.getFrom_account().equals("spot_account")) {
                    tvStatus.setText(getString(R.string.coin_coin_account));
                } else if (transferInfo.getFrom_account().equals("otc_account")) {
                    tvStatus.setText(getString(R.string.paris_coin_account));
                }
                if (transferInfo.getTo_account().equals("account")) {
                    tvTradeId.setText(getString(R.string.my_wallet));
                } else if (transferInfo.getTo_account().equals("spot_account")) {
                    tvTradeId.setText(getString(R.string.coin_coin_account));
                } else if (transferInfo.getTo_account().equals("otc_account")) {
                    tvTradeId.setText(getString(R.string.paris_coin_account));
                }
                tvTime.setText(TextUtils.isEmpty(transferInfo.getTime()) ? "" : transferInfo.getTime());
//                if (type == 12 || type == 13) {
//                    tvStatus.setText(getString(R.string.my_wallet));
//                    tvTradeId.setText(type == 12 ? R.string.paris_coin_account : R.string.coin_coin_account);
//                } else {
//                    tvStatus.setText(type == 21 ? R.string.paris_coin_account : R.string.coin_coin_account);
//                    tvTradeId.setText(getString(R.string.my_wallet));
//                }
            }
//            else if (type == 100) {
//                tvTitle.setText(getString(R.string.system));
//                llHash.setVisibility(View.GONE);
//                tvStatus.setText(transferInfo.getStatus());
//                tvOrientation.setVisibility(View.VISIBLE);
//                tvOrientation.setText(transferInfo.getDetail().getType_tip());
//            }

        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        type = getIntent().getIntExtra(PageConstant.TYPE, 3);// 默认划转
        account_type = getIntent().getStringExtra(PageConstant.ACCOUNT_TYPE);
        coin_name = getIntent().getStringExtra(PageConstant.COIN_NAME);
        transferInfo = (AccountDetailBean) getIntent().getSerializableExtra(PageConstant.TRANSFER_INFO);
    }

}
