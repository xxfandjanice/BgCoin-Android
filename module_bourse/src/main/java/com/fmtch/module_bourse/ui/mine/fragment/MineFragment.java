package com.fmtch.module_bourse.ui.mine.fragment;

import com.fmtch.base.ui.BaseFragment;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.utils.LogUtils;

/**
 * Created by wtc on 2019/5/8
 */
public class MineFragment extends LazyBaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        LogUtils.e(this.getClass().getSimpleName(),"第一次可见");
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        LogUtils.e(this.getClass().getSimpleName(),"可见");
    }
}
