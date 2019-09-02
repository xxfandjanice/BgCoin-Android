package com.fmtch.module_bourse.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fmtch.module_bourse.R;

/**
 * Created by wtc on 2019/5/13
 */
public class PercentNumberView extends LinearLayout implements View.OnClickListener {

    private TextView tvPercent25;
    private TextView tvPercent50;
    private TextView tvPercent75;
    private TextView tvPercent100;

    private String selectedPercentNumber;

    private PercentNumberSelectedListener listener;

    public PercentNumberView(Context context) {
        super(context);
        initView(context);
    }

    public PercentNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PercentNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_percent_number_view, this);
        tvPercent25 = view.findViewById(R.id.tv_percent_25);
        tvPercent50 = view.findViewById(R.id.tv_percent_50);
        tvPercent75 = view.findViewById(R.id.tv_percent_75);
        tvPercent100 = view.findViewById(R.id.tv_percent_100);

        tvPercent25.setOnClickListener(this);
        tvPercent50.setOnClickListener(this);
        tvPercent75.setOnClickListener(this);
        tvPercent100.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        tvPercent25.setTextColor(getResources().getColor(R.color.cl_999999));
        tvPercent50.setTextColor(getResources().getColor(R.color.cl_999999));
        tvPercent75.setTextColor(getResources().getColor(R.color.cl_999999));
        tvPercent100.setTextColor(getResources().getColor(R.color.cl_999999));
        int id = v.getId();
        if (id == R.id.tv_percent_25) {
            selectedPercentNumber = "0.25";
            tvPercent25.setTextColor(getResources().getColor(R.color.theme));
        } else if (id == R.id.tv_percent_50) {
            selectedPercentNumber = "0.50";
            tvPercent50.setTextColor(getResources().getColor(R.color.theme));
        } else if (id == R.id.tv_percent_75) {
            selectedPercentNumber = "0.75";
            tvPercent75.setTextColor(getResources().getColor(R.color.theme));
        } else if (id == R.id.tv_percent_100) {
            selectedPercentNumber = "1";
            tvPercent100.setTextColor(getResources().getColor(R.color.theme));
        }

        if (this.listener != null) {
            this.listener.onClickPercentNumber(this, selectedPercentNumber);
        }
    }

    public void resetPercentNumberView() {
        selectedPercentNumber = "";
        tvPercent25.setTextColor(getResources().getColor(R.color.cl_999999));
        tvPercent50.setTextColor(getResources().getColor(R.color.cl_999999));
        tvPercent75.setTextColor(getResources().getColor(R.color.cl_999999));
        tvPercent100.setTextColor(getResources().getColor(R.color.cl_999999));
    }

    public String getSelectedPercentNumber() {
        return selectedPercentNumber;
    }

    public void setPercentNumberSelectedListener(PercentNumberSelectedListener listener) {
        this.listener = listener;
    }

    public interface PercentNumberSelectedListener {
        void onClickPercentNumber(PercentNumberView view, String selectedPercentNumber);
    }
}
