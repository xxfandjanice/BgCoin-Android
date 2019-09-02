package com.fmtch.mine.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.ADVICE)
public class AdviceActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.et_advice)
    EditText etAdvice;
    @BindView(R2.id.tv_num)
    TextView tvNum;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_advice;
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
        etAdvice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (tvNum != null && !StringUtils.isEmpty(etAdvice.getText().toString())) {
                    tvNum.setText(etAdvice.getText().toString().length() + "/500");
                } else {
                    tvNum.setText("0/500");
                }
            }
        });
    }

    /**
     * 提交
     */
    @OnClick(R2.id.tv_submit)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        String name = etAdvice.getEditableText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showLongToast(R.string.mine_please_write_advice);
            return;
        }
        finish();

    }
}
