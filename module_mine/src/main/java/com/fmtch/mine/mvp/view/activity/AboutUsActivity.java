package com.fmtch.mine.mvp.view.activity;


import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.AppInfoUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.utils.UrlConstant;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.base.widget.dialog.CheckVersionDialog;


import butterknife.BindView;
import butterknife.OnClick;


@Route(path = RouterMap.ABOUT_US)
public class AboutUsActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_version_no)
    TextView tvVersionNo;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_about_us;
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
        tvVersionNo.setText("v " + AppInfoUtils.getVersionName(this));
    }

    @OnClick({R2.id.tv_use_agreement, R2.id.tv_privacy_policy, R2.id.tv_version_log, R2.id.tv_version_update, R2.id.tv_official_website})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_use_agreement) {
            //使用协议
            ARouter.getInstance().build(RouterMap.WEB_VIEW).withString(PageConstant.TARGET_URL, UrlConstant.USE_AGREEMENT).navigation();
        } else if (id == R.id.tv_privacy_policy) {
            //隐私条款
            ARouter.getInstance().build(RouterMap.WEB_VIEW).withString(PageConstant.TARGET_URL, UrlConstant.PRIVACY_POLICY).navigation();
        } else if (id == R.id.tv_version_log) {
            //版本日志
            ARouter.getInstance().build(RouterMap.VERSION_LOG).navigation();
        } else if (id == R.id.tv_version_update) {
            //版本更新
            RequestUtil.requestGet(API.CHECK_VERSION + "?app_name=ETF&&mobile_system=1", new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    if (!TextUtils.isEmpty(response.getVersion_code())) {
                        int localVersionNum = AppInfoUtils.getVersionNo(AboutUsActivity.this);
                        int netVersionNum = Integer.parseInt(response.getVersion_code());
                        if (netVersionNum > localVersionNum) {
                            new CheckVersionDialog(AboutUsActivity.this)
                                    .setTitle(getString(R.string.version_update))
                                    .setContent(response.getUpgrade_point().replace("\\n", "\n"))
                                    .setVersionCode(response.getVersion_name())
                                    .setDownApkUrl(response.getApk_url())
                                    //type为1选择更新  为2时强制更新
                                    .setCancelable(response.getType() == 1)
                                    .builder()
                                    .show();
                        } else {
                            ToastUtils.showLongToast(R.string.mine_version_new);
                        }
                    }
                }
            }, AboutUsActivity.this, true, getString(R.string.mine_loading_checking));
        } else if (id == R.id.tv_official_website) {
            //官方网站
            ARouter.getInstance().build(RouterMap.WEB_VIEW).withString(PageConstant.TARGET_URL, UrlConstant.OFFICIAL_WEBSITE).navigation();
        }
    }
}
