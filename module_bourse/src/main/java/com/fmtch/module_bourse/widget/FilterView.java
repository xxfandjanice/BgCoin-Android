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

/**
 * Created by wtc on 2019/5/13
 */
public class FilterView extends LinearLayout implements View.OnClickListener {

    private TextView tvName;
    private ImageView ivFilter;
    public static int FILTER_NORMAL = -1;
    public static int FILTER_UP = 0;
    public static int FILTER_DOWN = 1;

    private int colorText;
    private String textTitle;
    private int textSize;
    private int state = FILTER_NORMAL;

    private onFilterListener filterListener;

    public FilterView(Context context) {
        super(context);
        initView(context);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.FilterView);
        colorText=ta.getColor(R.styleable.FilterView_textColor,getResources().getColor(R.color.cl_333333));
        textSize = ta.getInt(R.styleable.FilterView_textSize, 13);
        textTitle=ta.getString(R.styleable.FilterView_titleText);
        ta.recycle();

        tvName.setText(textTitle);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        tvName.setTextColor(colorText);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_filter_view, this);
        tvName = view.findViewById(R.id.tv_name);
        ivFilter = view.findViewById(R.id.iv_filter);

        view.findViewById(R.id.ll_filter).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (state == FILTER_NORMAL) {
            if (this.filterListener != null) {
                this.filterListener.onClickFilter(this,FILTER_UP);
            }
            ivFilter.setImageResource(R.mipmap.icon_filter_up);
            state = FILTER_UP;
        } else if (state == FILTER_UP) {
            if (this.filterListener != null) {
                this.filterListener.onClickFilter(this, FILTER_DOWN);
            }
            ivFilter.setImageResource(R.mipmap.icon_filter_down);
            state = FILTER_DOWN;
        } else {
            if (this.filterListener != null) {
                this.filterListener.onClickFilter(this, FILTER_NORMAL);
            }
            ivFilter.setImageResource(R.mipmap.icon_filter_normal);
            state = FILTER_NORMAL;
        }
    }

    public void resetFilter() {
        ivFilter.setImageResource(R.mipmap.icon_filter_normal);
        state = FILTER_NORMAL;
    }

    public void setFilterListener(onFilterListener listener) {
        this.filterListener = listener;
    }

    public interface onFilterListener {
        void onClickFilter(FilterView view ,int type);
    }
}
