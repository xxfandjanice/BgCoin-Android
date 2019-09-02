package com.fmtch.base.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fmtch.base.mvp.presenter.BasePresenter;

public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity {
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachView(this);
        super.onCreate(savedInstanceState);
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
