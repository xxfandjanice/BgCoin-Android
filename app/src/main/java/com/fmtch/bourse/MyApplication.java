package com.fmtch.bourse;

import com.fmtch.base.app.BaseApplication;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.websocket.WebSocketManager;


/**
 * Created by wtc on 2019/5/7
 */
public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitManager.BASE_URL = BuildConfig.ENVIRONMENT;
        WebSocketManager.WS = BuildConfig.ENVIRONMENT_WS;
    }
}
