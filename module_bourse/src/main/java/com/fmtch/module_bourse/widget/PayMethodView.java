package com.fmtch.module_bourse.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fmtch.module_bourse.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wtc on 2019/5/13
 */
public class PayMethodView extends LinearLayout implements View.OnClickListener {


    private TextView tvAll;
    private TextView tvBank;
    private TextView tvWechat;
    private TextView tvZfb;
    public List<String> selectedPays = new ArrayList<>();
    private String bank = "0";
    private String zfb = "1";
    private String wechat = "2";

    public PayMethodView(Context context) {
        super(context);
        initView(context);
    }

    public PayMethodView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PayMethodView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_pay_method, this);
        tvAll = view.findViewById(R.id.tv_all);
        tvBank = view.findViewById(R.id.tv_bank);
        tvWechat = view.findViewById(R.id.tv_wechat);
        tvZfb = view.findViewById(R.id.tv_zfb);

        tvAll.setSelected(false);
        tvBank.setSelected(false);
        tvWechat.setSelected(false);
        tvZfb.setSelected(false);

        tvAll.setOnClickListener(this);
        tvBank.setOnClickListener(this);
        tvWechat.setOnClickListener(this);
        tvZfb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_all) {
            tvAll.setSelected(!tvAll.isSelected());
            tvBank.setSelected(false);
            tvWechat.setSelected(false);
            tvZfb.setSelected(false);
            selectedPays.clear();
        } else if (id == R.id.tv_bank) {
            tvAll.setSelected(false);
            tvBank.setSelected(!tvBank.isSelected());
            if (tvBank.isSelected()) {
                selectedPays.add(bank);
            } else {
                selectedPays.remove(bank);
            }
        } else if (id == R.id.tv_wechat) {
            tvAll.setSelected(false);
            tvWechat.setSelected(!tvWechat.isSelected());
            if (tvWechat.isSelected()) {
                selectedPays.add(wechat);
            } else {
                selectedPays.remove(wechat);
            }
        } else if (id == R.id.tv_zfb) {
            tvAll.setSelected(false);
            tvZfb.setSelected(!tvZfb.isSelected());
            if (tvZfb.isSelected()) {
                selectedPays.add(zfb);
            } else {
                selectedPays.remove(zfb);
            }
        }
    }

    public void clearSelectedPayMethod() {
        tvAll.setSelected(false);
        tvBank.setSelected(false);
        tvWechat.setSelected(false);
        tvZfb.setSelected(false);
        selectedPays.clear();
    }

    public List<String> getSelectedPayMethod() {
        return selectedPays;
    }

}
