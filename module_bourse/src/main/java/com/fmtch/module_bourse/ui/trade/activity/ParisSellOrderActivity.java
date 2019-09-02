package com.fmtch.module_bourse.ui.trade.activity;


import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.StatusBarUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.WaitingOpenDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.ui.trade.model.ParisSellOrderModel;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;


import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.PARIS_SELL_ORDER, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class ParisSellOrderActivity extends BaseActivity {


    @BindView(R2.id.tv_title)
    public TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_status_third)
    public TextView tvStatusThird;
    @BindView(R2.id.iv_status_third)
    public ImageView ivStatusThird;
    @BindView(R2.id.tv_order_no)
    public TextView tvOrderNo;
    @BindView(R2.id.tv_order_status)
    public TextView tvOrderStatus;
    @BindView(R2.id.tv_user_nick)
    public TextView tvUserNick;
    @BindView(R2.id.tv_user_name)
    public TextView tvUserName;
    @BindView(R2.id.tv_order_money)
    public TextView tvOrderMoney;
    @BindView(R2.id.tv_order_num)
    public TextView tvOrderNum;
    @BindView(R2.id.tv_single_price)
    public TextView tvSinglePrice;
    @BindView(R2.id.tv_order_time)
    public TextView tvOrderTime;
    @BindView(R2.id.tv_order_remark)
    public TextView tvOrderRemark;
    @BindView(R2.id.tv_receive_way)
    public TextView tvReceiveWay;
    @BindView(R2.id.tv_receive_account)
    public TextView tvReceiveAccount;
    @BindView(R2.id.tv_receive_name)
    public TextView tvReceiveName;
    @BindView(R2.id.tv_pay_amount)
    public TextView tvPayAmount;
    @BindView(R2.id.tv_pay_time)
    public TextView tvPayTime;
    @BindView(R2.id.ll_receive)
    public LinearLayout llReceive;
    @BindView(R2.id.ll_ask_server)
    public LinearLayout llAskServer;
    @BindView(R2.id.tv_warn)
    public TextView tvWarn;
    @BindView(R2.id.tv_chat_online)
    public TextView tvChatOnline;
    @BindView(R2.id.tv_call_phone)
    public TextView tvCallPhone;
    @BindView(R2.id.tv_confirm)
    public TextView tvConfirm;
    @BindView(R2.id.tv_order_money_unit)
    public TextView tvOrderMoneyUnit;
    @BindView(R2.id.tv_num_unit)
    public TextView tvNumUnit;
    @BindView(R2.id.tv_single_price_unit)
    public TextView tvSinglePriceUnit;

    public int mId;//订单id
    private ParisSellOrderModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_paris_sell_order;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mId = getIntent().getIntExtra(PageConstant.ID, 0);
        model = new ParisSellOrderModel(this);
        model.uploadParisSellOrderInfo();
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setColor(this, getResources().getColor(R.color.cl_2d3341));
        StatusBarUtils.StatusBarDarkMode(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R2.id.tv_order_no, R2.id.tv_chat_online, R2.id.tv_confirm, R2.id.ll_ask_server, R2.id.tv_call_phone})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_order_no) {
            if (!TextUtils.isEmpty(tvOrderNo.getText())) {
                ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tvOrderNo.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvOrderNo.getText());
            }
        } else if (id == R.id.ll_ask_server) {
            //申请客服
            UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
            if (userLoginInfo != null) {
                YSFUserInfo userInfo = new YSFUserInfo();
                // App 的用户 ID
                userInfo.userId = String.valueOf(userLoginInfo.getId());
                // 当且仅当开发者在管理后台开启了 authToken 校验功能时，该字段才有效
                userInfo.authToken = "auth-token-from-user-server";
                String userName = "";
                if (!TextUtils.isEmpty(userLoginInfo.getUsername())){
                    userName = userLoginInfo.getUsername();
                }
                // CRM 扩展字
                userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + userName + "\"},"
                        + "{\"key\":\"avatar\", \"value\":\"" + userLoginInfo.getAvatar() + "\"},"
                        + "{\"key\":\"mobile_phone\", \"value\":\"" + userLoginInfo.getMobile() + "\"},"
                        + "{\"key\":\"email\", \"value\":\"" + userLoginInfo.getEmail() + "\"},"
                        + "{\"index\":0, \"key\":\"account\", \"label\":\"账号\", \"value\":\"" + userName + "\", \"href\":\"" + userLoginInfo.getAvatar() + "\"}"
                        + "]";

                Unicorn.setUserInfo(userInfo);
            }
            Unicorn.openServiceActivity(this, "客服",null);
        } else if (id == R.id.tv_chat_online) {
            //在线闲聊
            new WaitingOpenDialog(this).builder().show();
        } else if (id == R.id.tv_call_phone) {
            //打电话
            new WaitingOpenDialog(this).builder().show();
        } else if (id == R.id.tv_confirm) {
            model.openConfirmReceiveDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (model != null)
            model.cancelAll();
    }
}
