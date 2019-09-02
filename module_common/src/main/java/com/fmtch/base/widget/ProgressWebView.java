package com.fmtch.base.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class ProgressWebView extends WebView {
    private ProgressBar progressbar;
    private OnTitleReceivedListener onTitleReceivedListener;

    //构造方法
    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //动态创建ProgressBar
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        //设置ProgressBar的宽高和横纵坐标，坐标为0,0在最上方显示
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                5, 0, 0));
        //将ProgressBar加入WebView
        addView(progressbar);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new webViewClient());
    }

    //WebChromeClient监听进度
    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //当进度为100时，将进度条隐藏
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else { //不为100时，实时设置ProgressBar进度
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    //WebViewClient监听WebView重定向和加载完成后显示标题
    class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //当加载完毕后，调用view.getTitle()获取网页的标题
            if(onTitleReceivedListener != null){
                onTitleReceivedListener.onTitleReceived(view.getTitle());
            }
        }
    }

    //定义显示标题的接口
    public interface  OnTitleReceivedListener{
        void onTitleReceived(String title);
    }

    //接口set方法
    public void setOnTitleReceivedListener(OnTitleReceivedListener onTitleReceivedListener) {
        this.onTitleReceivedListener = onTitleReceivedListener;
    }

    //设置ProgressBar的样式，可以自定义
    public void setProgressbarDrawable(Drawable drawable){
        if(progressbar != null){
            progressbar.setProgressDrawable(drawable);
        }
    }

    //设置ProgressBar的高
    public void setProgressbarHeight(int height){
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.height = height;
        progressbar.setLayoutParams(lp);
        invalidate();
    }

    //最后重写onScrollChanged方法，当滑动时重新计算ProgressBar的位置
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
