package com.fmtch.module_bourse.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.utils.ScreenUtils;
import com.fmtch.module_bourse.utils.SizeUtils;

/**
 * Created by wtc on 2019/5/27
 */
public class KLineSetPopup implements View.OnClickListener, PopupWindow.OnDismissListener {


    private Context context;
    private KlineSetOnClickListener listener;
    private OnDismissListener dismissListener;

    public static final String MA = "MA";
    public static final String BOLL = "BOLL";
    public static final String MACD = "MACD";
    public static final String KDJ = "KDJ";
    public static final String RSI = "RSI";
    public static final String WR = "WR";
    public static final String MAIN_GONE = "MAIN_GONE";
    public static final String SECOND_GONE = "SECOND_GONE";

    private int mainSelectedPosition = 0;
    private int secondSelectedPosition = -1;
    private boolean mainIvVisible = true;
    private boolean secondIvVisible = true;

    private final PopupWindow popupWindow;
    private final TextView tvMA;
    private final TextView tvBoll;
    private final TextView tvMACD;
    private final TextView tvKDJ;
    private final TextView tvRSI;
    private final TextView tvWR;
    private final ImageView ivMainVisible;
    private final ImageView ivSecondVisible;

    public KLineSetPopup(Context context) {
        this.context = context;
        popupWindow = new PopupWindow(ScreenUtils.getScreenWidth(context) - SizeUtils.dp2px(30), LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_k_line_set, null);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(view);

        tvMA = view.findViewById(R.id.tv_ma);
        tvMA.setTextColor(context.getResources().getColor(R.color.theme));
        tvBoll = view.findViewById(R.id.tv_boll);
        tvMACD = view.findViewById(R.id.tv_macd);
        tvKDJ = view.findViewById(R.id.tv_kdj);
        tvRSI = view.findViewById(R.id.tv_rsi);
        tvWR = view.findViewById(R.id.tv_wr);
        ivMainVisible = view.findViewById(R.id.iv_main_visible);
        ivSecondVisible = view.findViewById(R.id.iv_second_visible);

        tvMA.setOnClickListener(this);
        tvBoll.setOnClickListener(this);
        tvMACD.setOnClickListener(this);
        tvKDJ.setOnClickListener(this);
        tvRSI.setOnClickListener(this);
        ivMainVisible.setOnClickListener(this);
        ivSecondVisible.setOnClickListener(this);
        tvWR.setOnClickListener(this);

        popupWindow.setOnDismissListener(this);
    }

    public void showAsDropDown(View view) {
        popupWindow.showAsDropDown(view, SizeUtils.dp2px(15), 0);
    }


    private void updateMainText(TextView textView) {
        tvMA.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
        tvBoll.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
        textView.setTextColor(context.getResources().getColor(R.color.theme));
        ivMainVisible.setImageResource(R.mipmap.icon_k_line_visible);
        mainIvVisible = true;
    }

    private void updateSecondText(TextView textView) {
        tvMACD.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
        tvRSI.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
        tvWR.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
        tvKDJ.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
        textView.setTextColor(context.getResources().getColor(R.color.theme));
        ivSecondVisible.setImageResource(R.mipmap.icon_k_line_visible);
        secondIvVisible = true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_ma) {
            mainSelectedPosition = 0;
            updateMainText(tvMA);
            this.listener.klineSetTvOnClick(MA);
        } else if (id == R.id.tv_boll) {
            mainSelectedPosition = 1;
            updateMainText(tvBoll);
            this.listener.klineSetTvOnClick(BOLL);
        } else if (id == R.id.tv_macd) {
            secondSelectedPosition = 0;
            updateSecondText(tvMACD);
            this.listener.klineSetTvOnClick(MACD);
        } else if (id == R.id.tv_kdj) {
            secondSelectedPosition = 1;
            updateSecondText(tvKDJ);
            this.listener.klineSetTvOnClick(KDJ);
        } else if (id == R.id.tv_rsi) {
            secondSelectedPosition = 2;
            updateSecondText(tvRSI);
            this.listener.klineSetTvOnClick(RSI);
        } else if (id == R.id.tv_wr) {
            secondSelectedPosition = 3;
            updateSecondText(tvWR);
            this.listener.klineSetTvOnClick(WR);
        } else if (id == R.id.iv_main_visible) {
            if (mainSelectedPosition == -1) {
                popupWindow.dismiss();
                this.listener.klineSetHide(MAIN_GONE);
                return;
            }
            mainIvVisible = !mainIvVisible;
            ivMainVisible.setImageResource(mainIvVisible ? R.mipmap.icon_k_line_visible : R.mipmap.icon_k_line_gone);
            this.listener.klineSetHide(MAIN_GONE);
            if (!mainIvVisible) {
                tvMA.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
                tvBoll.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
                mainSelectedPosition = -1;
            }
        } else if (id == R.id.iv_second_visible) {
            if (secondSelectedPosition == -1) {
                popupWindow.dismiss();
                this.listener.klineSetHide(SECOND_GONE);
                return;
            }
            secondIvVisible = !secondIvVisible;
            ivSecondVisible.setImageResource(secondIvVisible ? R.mipmap.icon_k_line_visible : R.mipmap.icon_k_line_gone);
            this.listener.klineSetHide(SECOND_GONE);
            if (!secondIvVisible) {
                tvMACD.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
                tvRSI.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
                tvWR.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
                tvKDJ.setTextColor(context.getResources().getColor(R.color.dialog_text_color));
                secondSelectedPosition = -1;
            }
        }

        popupWindow.dismiss();
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }


    public boolean isShowing() {
        if (popupWindow != null) {
            return popupWindow.isShowing();
        }
        return false;
    }

    @Override
    public void onDismiss() {
        if (dismissListener != null) {
            dismissListener.OnPopupDismissListener();
        }
    }

    public interface OnDismissListener {
        void OnPopupDismissListener();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }

    public interface KlineSetOnClickListener {
        void klineSetTvOnClick(String tvType);

        void klineSetHide(String ivType);
    }

    public void addKlineSetOnClickListener(KlineSetOnClickListener listener) {
        this.listener = listener;
    }
}
