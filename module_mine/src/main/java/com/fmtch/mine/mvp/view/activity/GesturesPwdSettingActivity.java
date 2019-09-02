package com.fmtch.mine.mvp.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.pojo.UserGesturesPwdInfo;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.GesturesFingerPwdUtils;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.mine.mvp.view.customview.LockPainter;
import com.wangnan.library.GestureLockThumbnailView;
import com.wangnan.library.GestureLockView;
import com.wangnan.library.listener.OnGestureLockListener;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.GESTURES_PWD_SETTING)
public class GesturesPwdSettingActivity extends BaseActivity implements OnGestureLockListener {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.gl_top_view)
    GestureLockThumbnailView glTopView;
    @BindView(R2.id.tv_explain)
    TextView tvExplain;
    @BindView(R2.id.gl_view)
    GestureLockView glView;
    @BindView(R2.id.tv_reset_draw)
    TextView tvResetDraw;

    private String gesture_pwd;
    private int MIN_POINT = 4;//最低设置密目条目

    private Realm mRealm;
    private int user_id;//用户ID

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_gestures_setting;
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
        //轨迹是否开启
        if (GesturesFingerPwdUtils.getGesturesTrackIsOpen()) {
            glTopView.setVisibility(View.VISIBLE);
        } else {
            glTopView.setVisibility(View.INVISIBLE);
        }
        glView.setPainter(new LockPainter());
        glView.setGestureLockListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        UserLoginInfo mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            user_id = mUserLoginInfo.getId();
        }
    }

    /**
     * 解锁开始监听方法
     */
    @Override
    public void onStarted() {

    }

    /**
     * 解锁密码改变监听方法
     */
    @Override
    public void onProgress(String progress) {
        glTopView.setThumbnailView(progress, 0xFF007AFF);
    }

    /**
     * 解锁完成监听方法
     */
    @Override
    public void onComplete(final String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        //设置手势密码
        if (StringUtils.isEmpty(gesture_pwd)) {
            //第一次绘制
            if (result.length() < MIN_POINT) {
                tvExplain.setText(String.format(getString(R.string.mine_gesture_min_point), String.valueOf(MIN_POINT)));
                tvExplain.setTextColor(GesturesPwdSettingActivity.this.getResources().getColor(R.color.color_common_FFF74D4D));
                glView.showErrorStatus(400);
            } else {
                gesture_pwd = result;
                tvExplain.setText(R.string.mine_gesture_draw_again);
                tvExplain.setTextColor(GesturesPwdSettingActivity.this.getResources().getColor(R.color.color_common_text44));
                tvResetDraw.setVisibility(View.VISIBLE);
                glView.clearView();
            }
        } else {
            //之后绘制
            if (gesture_pwd.equals(result)) {
                //绘制成功
                glView.clearView();
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //先查找后得到User对象
                        UserGesturesPwdInfo userGesturesPwdInfo = realm.where(UserGesturesPwdInfo.class)
                                .equalTo("id", user_id)
                                .findFirst();
                        if (userGesturesPwdInfo == null) {
                            userGesturesPwdInfo = realm.createObject(UserGesturesPwdInfo.class, user_id);
                        }
                        userGesturesPwdInfo.setGestures_pwd_status(true);
                        userGesturesPwdInfo.setGestures_pwd(result);
                        realm.copyToRealmOrUpdate(userGesturesPwdInfo);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        SpUtils.put(KeyConstant.KEY_GESTURE_PWD_TIMES, 5);
                        ToastUtils.showLongToast(R.string.mine_set_success);
                        finish();
                    }
                });
            } else {
                tvExplain.setText(R.string.mine_gesture_different);
                tvExplain.setTextColor(GesturesPwdSettingActivity.this.getResources().getColor(R.color.color_common_FFF74D4D));
                glView.showErrorStatus(400);
            }
        }
    }

    @OnClick(R2.id.tv_reset_draw)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        gesture_pwd = "";
        tvResetDraw.setVisibility(View.VISIBLE);
        tvExplain.setText(R.string.mine_gestures_explain);
        tvExplain.setTextColor(GesturesPwdSettingActivity.this.getResources().getColor(R.color.color_common_text44));
    }
}
