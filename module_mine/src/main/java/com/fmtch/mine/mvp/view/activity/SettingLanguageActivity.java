package com.fmtch.mine.mvp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.manager.ActivityManager;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.LocaleManager;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.WaitingOpenDialog;
import com.fmtch.mine.MainActivity;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = RouterMap.SETTING_LANGUAGE)
public class SettingLanguageActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.iv_chinese)
    ImageView ivChinese;
    @BindView(R2.id.iv_english)
    ImageView ivEnglish;

    private boolean isChineseChecked;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_setting_language;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        isChineseChecked = TextUtils.equals(LocaleManager.getLanguage(this), LocaleManager.LANGUAGE_CHINESE);
        refreshChecked();
    }

    private void refreshChecked() {
        if (isChineseChecked) {
            ivChinese.setImageResource(R.drawable.icon_checked);
            ivEnglish.setImageResource(R.drawable.icon_unchecked);
        } else {
            ivEnglish.setImageResource(R.drawable.icon_checked);
            ivChinese.setImageResource(R.drawable.icon_unchecked);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick(R2.id.tv_save)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        if (isChineseChecked) {
            //中文
            LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_CHINESE);
        } else {
            LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_ENGLISH);
        }
        ActivityManager.getInstance().removeAllActivity();
        ARouter.getInstance().build(RouterMap.MAIN_PAGE)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation();
    }

    @OnClick({R2.id.ll_chinese, R2.id.ll_english})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.ll_chinese) {
            if (!isChineseChecked) {
                isChineseChecked = true;
                refreshChecked();
            }
        } else if (id == R.id.ll_english) {
//            new WaitingOpenDialog(this).builder().show();
            if (isChineseChecked) {
                isChineseChecked = false;
                refreshChecked();
            }
        }
    }
}
