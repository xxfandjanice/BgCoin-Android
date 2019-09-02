package com.fmtch.base.intercepter;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.SpUtils;

/**
 * Created by wtc on 2019/5/29
 */
@Interceptor(priority = 1)
public class LoginInterceptor implements IInterceptor {

    private static final String TAG = "LoginInterceptor";

    private Context mContext;

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        //给需要跳转的页面添加值为Constant.LOGIN_NEEDED的extra参数，用来标记是否需要用户先登录才可以访问该页面
        //先判断需不需要
        if (postcard.getExtra() == PageConstant.LOGIN_NEEDED_CONSTANT) {

            boolean isLogin = !TextUtils.isEmpty((CharSequence) SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN, ""));

            //判断用户的登录情况，可以把值保存在sp中
            if (isLogin) {
                callback.onContinue(postcard);
            } else {//没有登录,注意需要传入context
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation(mContext);
            }
        } else {//没有extra参数时则继续执行，不做拦截
            callback.onContinue(postcard);
        }

    }

    @Override
    public void init(Context context) {
        mContext = context;
    }
}
