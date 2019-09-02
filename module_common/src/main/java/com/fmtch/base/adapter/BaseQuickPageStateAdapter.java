package com.fmtch.base.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.R;

import java.util.List;

/**
 * Created by wtc on 2019/5/11
 */
public abstract class BaseQuickPageStateAdapter<T, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    private Context context;
    private View pageView;
    private TextView tvTip;
    private ProgressBar progressBar;
    private ImageView ivEmpty;

    public BaseQuickPageStateAdapter(Context context, @Nullable List<T> data) {
        super(data);
        initPageView(context);
    }

    public BaseQuickPageStateAdapter(Context context, int layoutResId) {
        super(layoutResId);
        initPageView(context);
    }

    public BaseQuickPageStateAdapter(Context context, int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
        initPageView(context);
    }

    private void initPageView(Context context) {
        this.context = context;
        pageView = LayoutInflater.from(context).inflate(R.layout.layout_empty, null);
        tvTip = pageView.findViewById(R.id.tv_tip);
        progressBar = pageView.findViewById(R.id.progressbar);
        ivEmpty = pageView.findViewById(R.id.iv_empty);
    }


    public void showLoadingPage() {
        ivEmpty.setVisibility(View.GONE);
        tvTip.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        setEmptyView(pageView);
    }

    public void showEmptyPage() {
        ivEmpty.setVisibility(View.VISIBLE);
        tvTip.setVisibility(View.VISIBLE);
        tvTip.setText(context.getString(R.string.no_record));
        progressBar.setVisibility(View.GONE);

        notifyDataSetChanged();
        setEmptyView(pageView);
    }

    public void showErrorPage(String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg)) {
            tvTip.setText(errorMsg);
        } else {
            tvTip.setText(R.string.net_connect_error);
        }
        tvTip.setVisibility(View.VISIBLE);
        ivEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        setEmptyView(pageView);
    }
}
