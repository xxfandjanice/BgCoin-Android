package com.fmtch.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.fmtch.base.R;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.interfaces.PermissionListener;
import com.fmtch.base.manager.ActivityManager;
import com.fmtch.base.utils.LocaleManager;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends RxAppCompatActivity {
    private static final String TAG = "BaseActivity";

    private static PermissionListener mPermissionListener;
    private static final int CODE_REQUEST_PERMISSION = 1;

    protected Unbinder mUnbinder;
    protected Bundle mBundle;
    private boolean isFlag = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.setLocale(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getLayoutId());
        setStatusBar();

        mUnbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        ActivityManager.getInstance().addActivity(this);
        initData(savedInstanceState);
        initView();
        initEvent();
    }

    /**
     * 获取子Activity布局
     * @return
     */
    protected abstract int getLayoutId();

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(R.layout.activity_base,null);
        //设置填充activity_base布局
        super.setContentView(view);
       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setFitsSystemWindows(true);
        }*/
        //加载子类Activity的布局
        addContentView(layoutResID);
    }

    /**
     * 初始化数据
     * @param savedInstanceState
     */
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(intent != null) {
            mBundle = intent.getExtras();
        }
    }

    /**
     * 初始化视图
     */
    protected  void initView() {

    }

    /**
     * 初始化事件
     */
    protected  void initEvent() {

    }

    /**
     * 添加子类Activity的布局
     * @param layoutResId
     */
    private void addContentView(int layoutResId) {
        FrameLayout container = findViewById(R.id.fl_activity_child_container);
        View childView = LayoutInflater.from(this).inflate(layoutResId,null);
        container.addView(childView,0);
    }

    /**
     * 申请权限
     * @param permissions 需要申请的权限(数组)
     * @param listener 权限回调接口
     */
    public static void requestPermissions(String[] permissions, PermissionListener listener) {
        Activity activity = ActivityManager.getInstance().getTopActivity();
        if(null == activity) {
            return;
        }
        mPermissionListener = listener;
        List<String> permissionList = new ArrayList<>();
        for(String permission : permissions) {
            //权限没有授权
            if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if(!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]),CODE_REQUEST_PERMISSION);
        }else {
            mPermissionListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE_REQUEST_PERMISSION:
                if(grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length ; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        }
                    }

                    if(deniedPermissions.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermissions);
                    }
                }
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        EventBus.getDefault().unregister(this);
        ActivityManager.getInstance().removeActivity(this);
    }


    protected void setStatusBar() {
        Window window = getWindow();
        if(isFlag) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup contentView = window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
            contentView.getChildAt(0).setFitsSystemWindows(false);
        }else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(android.R.color.white));
            }
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void setFullScreen(boolean isFlag) {
        this.isFlag = isFlag;
    }

    /**
     * @param event
     * 		eventbus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {

    }
}
