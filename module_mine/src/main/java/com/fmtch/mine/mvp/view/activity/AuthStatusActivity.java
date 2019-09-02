package com.fmtch.mine.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.AUTH_STATUS)
public class AuthStatusActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.iv_auth_status)
    ImageView ivAuthStatus;
    @BindView(R2.id.tv_auth_status)
    TextView tvAuthStatus;
    @BindView(R2.id.tv_auth_content)
    TextView tvAuthContent;
    @BindView(R2.id.btn_submit)
    Button btnSubmit;


    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_auth_status;
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
        initAuthStatus();
    }


    private void initAuthStatus() {
        //实名认证状态: 0:未实名认证 1:已实名认证 2:审核中 3:失败
        UserLoginInfo mUserLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo == null)
            return;
        switch (mUserLoginInfo.getKyc_status()) {
            case 1:
                btnSubmit.setVisibility(View.GONE);
                ivAuthStatus.setImageResource(R.drawable.mine_bg_auth_checking);
                tvAuthStatus.setText(R.string.mine_info_check_success);
                tvAuthContent.setText(R.string.mine_info_auth_success);
                break;
            case 2:
                btnSubmit.setVisibility(View.GONE);
                ivAuthStatus.setImageResource(R.drawable.mine_bg_auth_checking);
                tvAuthStatus.setText(R.string.mine_info_checking);
                tvAuthContent.setText(R.string.mine_info_submit_success);
                break;
            case 3:
                btnSubmit.setVisibility(View.VISIBLE);
                ivAuthStatus.setImageResource(R.drawable.mine_bg_auth_fail);
                tvAuthStatus.setText(R.string.mine_auth_fail);
                tvAuthContent.setText(R.string.mine_please_check_id_card_info);
                break;
            default:
                btnSubmit.setVisibility(View.GONE);
                ivAuthStatus.setImageResource(R.drawable.mine_bg_auth_checking);
                tvAuthStatus.setText(R.string.mine_info_checking);
                tvAuthContent.setText(R.string.mine_info_submit_success);
                break;
        }
    }

    /**
     * 重新上传
     */
    @OnClick(R2.id.btn_submit)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        ARouter.getInstance().build(RouterMap.AUTH).navigation();
        finish();
    }
}
