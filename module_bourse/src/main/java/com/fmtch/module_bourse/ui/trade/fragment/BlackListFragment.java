package com.fmtch.module_bourse.ui.trade.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.imageloader.CircleTransformation;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.widget.dialog.CustomDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.bean.BlackOrAttentionBean;
import com.fmtch.module_bourse.ui.trade.model.BlackListModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by wtc on 2019/6/25
 */
public class BlackListFragment extends LazyBaseFragment {

    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;

    public List<BlackOrAttentionBean> list;
    public BaseQuickPageStateAdapter<BlackOrAttentionBean, BaseViewHolder> adapter;
    private BlackListModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_black_list;
    }


    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        refreshLayout.autoRefresh();
    }

    @Override
    protected void initData() {
        super.initData();
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseQuickPageStateAdapter<BlackOrAttentionBean, BaseViewHolder>(getActivity(), R.layout.item_black_list) {
            @Override
            protected void convert(BaseViewHolder helper, BlackOrAttentionBean item) {
                helper.addOnClickListener(R.id.tv_release);
                helper.addOnClickListener(R.id.iv_avatar);
                GlideLoadUtils.getInstance().glideLoad(getActivity(), item.getAvatar(), (ImageView) helper.getView(R.id.iv_avatar), new CircleTransformation(getActivity()), R.drawable.icon_default_avatar);
                helper.setText(R.id.tv_name, item.getUsername());
                helper.setText(R.id.tv_id, String.format(getResources().getString(R.string.paris_user_id), item.getUser_id() + ""));
                helper.setText(R.id.tv_time, String.format(getResources().getString(R.string.add_black_time), item.getCreated_at().split(" ")[0]));
            }
        };
        rv.setAdapter(adapter);
        model = new BlackListModel(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        //刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                model.getData(true);
            }
        });
        //加载更多
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                model.getData(false);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if (view.getId() == R.id.iv_avatar) {
                    ARouter.getInstance().build(RouterMap.MERCHANT_HOMEPAGE)
                            .withInt(PageConstant.ID, list.get(position).getUser_id())
                            .navigation();
                } else if (view.getId() == R.id.tv_release) {
                    //解除拉黑
                    new CustomDialog(getActivity())
                            .setContent("解除拉黑 "+list.get(position).getUsername())
                            .setLeftBtnStr(R.string.cancel)
                            .setRightBtnStr(R.string.sure)
                            .setSubmitListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    model.releaseBlackState(position);
                                }
                            })
                            .builder()
                            .show();
                }
            }
        });
    }
}
