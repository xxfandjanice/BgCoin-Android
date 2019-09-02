package com.fmtch.base.router;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;

/**
 * Created by wtc on 2019/7/22
 */
public class ArouterNavCallback extends NavCallback {

    private Activity activity;

    public ArouterNavCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onArrival(Postcard postcard) {
        if (activity != null) {
            activity.finish();
        }
    }
}
