package com.fmtch.base.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmtch.base.mvp.presenter.BasePresenter;

public abstract class BaseMvpFragment<P extends BasePresenter> extends BaseFragment {
    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachView(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract P createPresenter();

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
