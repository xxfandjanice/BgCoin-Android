package com.fmtch.base.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.R;
import com.fmtch.base.R2;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.widget.ProgressWebView;
import com.fmtch.base.widget.dialog.ShowDialog;

import butterknife.BindView;

@Route(path = RouterMap.WEB_VIEW)
public class WebViewActivity extends BaseActivity {
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.web_view)
    ProgressWebView progressWebView;

    @Autowired
    String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        url = getIntent().getStringExtra(PageConstant.TARGET_URL);
    }

    @Override
    protected void initView() {
        super.initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressWebView != null && progressWebView.canGoBack()) {
                    progressWebView.goBack();
                } else {
                    finish();
                }
            }
        });
        ShowDialog.showDialog(this, getString(R.string.loading), true, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(url)) {
                    initWebViewProgress();
                    setWebView(url);
                }
            }
        }, 300);

    }


    private void initWebViewProgress() {
        //设置ProgressBar样式
        progressWebView.setProgressbarDrawable(getResources().getDrawable(R.drawable.webview_horizontal_progress));
        //设置ProgressBar高度
        progressWebView.setProgressbarHeight(5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将WebView清空，防止内存泄漏
        if (progressWebView != null) {
            progressWebView.loadDataWithBaseURL(null, "", "html", "utf-8", null);
            ((ViewGroup) progressWebView.getParent()).removeView(progressWebView);
            progressWebView.clearCache(true);
            progressWebView.destroy();
            progressWebView = null;
        }
    }

    @Override
    public void onBackPressed() {
        //返回键处理
        if (progressWebView != null && progressWebView.canGoBack()) {
            progressWebView.goBack();
        } else {
            finish();
        }
    }

    private void setWebView(String Url) {
        WebSettings settings = progressWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
        settings.setAllowFileAccess(true);
        settings.setSupportMultipleWindows(true);
        settings.setDefaultFontSize(14);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true); // 支持缩放
        settings.setLoadWithOverviewMode(true); // 初始加载时，是web页面自适应屏幕
        int screenDensity = getResources().getDisplayMetrics().densityDpi;
        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
        switch (screenDensity) {
            case DisplayMetrics.DENSITY_LOW:
                zoomDensity = WebSettings.ZoomDensity.CLOSE;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                zoomDensity = WebSettings.ZoomDensity.MEDIUM;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                zoomDensity = WebSettings.ZoomDensity.FAR;
                break;
        }
        settings.setDefaultZoom(zoomDensity);
        progressWebView.requestFocus();
        progressWebView.requestFocusFromTouch();
        //设置title
        progressWebView.setOnTitleReceivedListener(new ProgressWebView.OnTitleReceivedListener() {
            @Override
            public void onTitleReceived(String title) {
                if (!TextUtils.isEmpty(title) && tvTitle != null) {
                    tvTitle.setText(title);
                    ShowDialog.dissmiss();
                }
            }
        });
        progressWebView.loadUrl(Url);
    }
}
