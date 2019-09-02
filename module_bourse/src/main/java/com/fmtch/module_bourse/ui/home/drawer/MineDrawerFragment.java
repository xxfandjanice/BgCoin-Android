package com.fmtch.module_bourse.ui.home.drawer;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseFragment;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.utils.UrlConstant;
import com.fmtch.base.utils.UserInfoIntercept;
import com.fmtch.base.widget.CircleImageView;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.ui.home.fragment.DealOkFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;

/**
 * Created by wtc on 2019/7/30
 */
public class MineDrawerFragment extends BaseFragment {
    @BindView(R2.id.civ_head)
    CircleImageView civHead;
    @BindView(R2.id.tv_name)
    TextView tvName;

    private UserLoginInfo mUserLoginInfo;
    private  DrawerLayout drawerLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_drawer_mine;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    /**
     * 刷新页面信息
     */
    public void refreshUserView() {
        mUserLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo == null) {
            tvName.setText(R.string.login_now);
            civHead.setImageResource(R.drawable.icon_default_avatar);
        } else {
            if (!TextUtils.isEmpty(mUserLoginInfo.getUsername())) {
                tvName.setText(mUserLoginInfo.getUsername());
            } else if (!TextUtils.isEmpty(mUserLoginInfo.getMobile())) {
                tvName.setText(StringUtils.hiddenMobile(mUserLoginInfo.getMobile()));
            } else if (!TextUtils.isEmpty(mUserLoginInfo.getEmail())) {
                tvName.setText(StringUtils.hiddenEmail(mUserLoginInfo.getEmail()));
            }
            GlideLoadUtils.getInstance().glideLoad(getActivity(), mUserLoginInfo.getAvatar(), civHead, R.drawable.icon_default_avatar);
        }
    }

    @OnClick({R2.id.tv_name, R2.id.drawer_recharge, R2.id.drawer_draw_coin, R2.id.drawer_transform, R2.id.drawer_invite_friends, R2.id.drawer_safety_center, R2.id.drawer_about, R2.id.drawer_problem_feedback, R2.id.drawer_set})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_name && mUserLoginInfo == null) {
            //跳转登录
            ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
        } else if (id == R.id.drawer_recharge) {
            //充币
            ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                    .withInt(PageConstant.TYPE, 1)
                    .withBoolean(PageConstant.JUMP, true)
                    .navigation();
        } else if (id == R.id.drawer_draw_coin) {
            //提币
            if (UserInfoIntercept.userAuth(getActivity())){
                //已经实名认证
                ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                        .withInt(PageConstant.TYPE, 2)
                        .withBoolean(PageConstant.JUMP, true)
                        .navigation();
            }
        } else if (id == R.id.drawer_transform) {
            //划转
            ARouter.getInstance().build(RouterMap.TRANSFER_COIN).navigation();
        } else if (id == R.id.drawer_invite_friends) {
            //邀请好友
            ARouter.getInstance().build(RouterMap.INVITE_FRIENDS).navigation();
        } else if (id == R.id.drawer_safety_center) {
            //安全中心
            ARouter.getInstance().build(RouterMap.SAFE_CENTER).navigation();
        } else if (id == R.id.drawer_about) {
            //关于我们
            ARouter.getInstance().build(RouterMap.ABOUT_US).navigation();
        } else if (id == R.id.drawer_problem_feedback) {
            //问题反馈
            ARouter.getInstance().build(RouterMap.WEB_VIEW).withString(PageConstant.TARGET_URL, UrlConstant.TROUBLE_FEEDBACK).navigation();
        } else if (id == R.id.drawer_set) {
            //设置
            ARouter.getInstance().build(RouterMap.SETTING).navigation();
        }
    }
}
