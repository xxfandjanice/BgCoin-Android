package com.fmtch.mine.mvp.view.activity;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.mine.widget.InviteShareDialog;
import com.fmtch.mine.widget.InviteSharePicDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.INVITE_FRIENDS, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class InviteActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_invite_code)
    TextView tvInviteCode;

    private UserLoginInfo mUserLoginInfo;

    private String invite_code;

    private Dialog shareDialog;
    private Dialog sharePicDiaog;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_invite;
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
        if (mUserLoginInfo != null) {
            invite_code = mUserLoginInfo.getPid();
            tvInviteCode.setText(invite_code);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mUserLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        RequestUtil.requestGet(API.GET_INVITE_INFO, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                if (!TextUtils.isEmpty(response.getLink())) {
                    shareDialog =  new InviteShareDialog(InviteActivity.this)
                            .setInviteUrl(response.getLink())
                            .builder();
                    sharePicDiaog = new InviteSharePicDialog(InviteActivity.this)
                            .setInviteUrl(response.getLink())
                            .builder();
                }
            }
        },this);
    }

    //复制邀请码
    @OnClick(R2.id.tv_copy)
    public void onTvCopyClicked() {
        if (!TextUtils.isEmpty(invite_code)) {
            ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(invite_code);
            ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + invite_code);
        }
    }

    //分享给好友
    @OnClick(R2.id.tv_invite_friends)
    public void onTvInviteFriendsClicked() {
        if (shareDialog != null){
            shareDialog.show();
        }else {
            RequestUtil.requestGet(API.GET_INVITE_INFO, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    if (!TextUtils.isEmpty(response.getLink())) {
                        shareDialog =  new InviteShareDialog(InviteActivity.this)
                                .setInviteUrl(response.getLink())
                                .builder();
                        shareDialog.show();
                    }
                }
            },this);
        }
    }

    //分享海报
    @OnClick(R2.id.tv_share_pic)
    public void onTvSharePicClicked() {
        if (sharePicDiaog != null){
            sharePicDiaog.show();
        }else {
            RequestUtil.requestGet(API.GET_INVITE_INFO, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    if (!TextUtils.isEmpty(response.getLink())) {
                        sharePicDiaog = new InviteSharePicDialog(InviteActivity.this)
                                .setInviteUrl(response.getLink())
                                .builder();
                        sharePicDiaog.show();
                    }
                }
            },this);
        }
    }
}
