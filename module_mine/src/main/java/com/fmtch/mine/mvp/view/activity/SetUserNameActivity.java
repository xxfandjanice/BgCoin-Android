package com.fmtch.mine.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.SET_USER_NAME)
public class SetUserNameActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.et_user_name)
    EditText etUserName;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_set_user_name;
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
    }

    @OnClick(R2.id.btn_confirm)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        final String user_name = etUserName.getEditableText().toString();
        if (TextUtils.isEmpty(user_name)) {
            ToastUtils.showLongToast(R.string.mine_please_input_user_name);
            return;
        }
        SuperRequest superRequest = new SuperRequest();
        superRequest.setUsername(user_name);
        RequestUtil.requestPost(API.SET_USER_NAME, superRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UserLoginInfo userLoginInfo = realm.where(UserLoginInfo.class).findFirst();
                        if (userLoginInfo != null) {
                            userLoginInfo.setUsername(user_name);
                            realm.copyToRealmOrUpdate(userLoginInfo);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        ToastUtils.showLongToast(R.string.mine_set_success);
                        finish();
                    }
                });
            }
        }, SetUserNameActivity.this, true, getString(R.string.mine_loading_set));
    }

}
