package com.fmtch.base.app;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fmtch.base.BuildConfig;
import com.fmtch.base.R;
import com.fmtch.base.imageloader.GlideImageLoader;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.utils.Constant;
import com.fmtch.base.utils.LocaleManager;
import com.isseiaoki.simplecropview.FreeCropImageView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.qiyukf.unicorn.api.YSFOptions;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.uuzuche.lib_zxing.ZApplication;


import io.realm.Realm;
import io.realm.RealmConfiguration;


public class BaseApplication extends ZApplication {
    private static Context mContext;
    public static RealmConfiguration config;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        //设置夜晚主题模式
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //ARouter
        ARouter.openDebug();
        ARouter.openLog();
        ARouter.printStackTrace();
        ARouter.init(this);
        //Bugly
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), Constant.BUGLY_APP_ID, false);
        }
        //Realm
        Realm.init(this);
        config = new RealmConfiguration.Builder()
                .name("etf.realm")
                .schemaVersion(1)
//                .migration(new DbMigration())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        //初始化网易七鱼客服
        initUnicorn();
    }

    //初始化网易七鱼客服
    private void initUnicorn(){
        Unicorn.init(this, Constant.UNICORN_APP_KEY, options(), new UnicornGlideImageLoader(this));
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
    }

    // 如果返回值为null，则全部使用默认参数。
    private YSFOptions options() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        return options;
    }

    public class UnicornGlideImageLoader implements UnicornImageLoader {
        private Context context;

        public UnicornGlideImageLoader(Context context) {
            this.context = context.getApplicationContext();
        }

        @Nullable
        @Override
        public Bitmap loadImageSync(String uri, int width, int height) {
            return null;
        }

        @Override
        public void loadImage(String uri, int width, int height, final ImageLoaderListener listener) {
            if (width <= 0 || height <= 0) {
                width = height = Integer.MIN_VALUE;
            }

            Glide.with(context).load(uri).asBitmap().into(new SimpleTarget<Bitmap>(width, height) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (listener != null) {
                        listener.onLoadComplete(resource);
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    if (listener != null) {
                        listener.onLoadFailed(e);
                    }
                }
            });
        }
    }


    public static Context getApplication() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
        MultiDex.install(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.cl_f7f7f7, R.color.cl_9f9f9f);//全局设置主题颜色
                return new ClassicsHeader(context).setEnableLastTime(false);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });

        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setFreeCrop(false, FreeCropImageView.CropMode.CIRCLE);//新版添加,自由裁剪，优先于setCrop
        imagePicker.setSaveRectangle(false); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setMultiMode(false);  //单选
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

}
