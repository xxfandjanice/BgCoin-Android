package com.fmtch.module_bourse.widget.popup;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.widget.TriangleDrawable;


public class DealPopup extends BasePopup<DealPopup> {

    private OnItemClickListener mOnItemClickListener;

    private Context mContext;

    private SparseArray<TextView> textViewMap;


    public static DealPopup create(Context context) {
        return new DealPopup(context);
    }

    public DealPopup(Context context) {
        this.mContext = context;
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.popup_deal, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //是否允许点击PopupWindow之外的地方消失
        setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(true)
                //变暗的透明度(0-1)，0为完全透明
                .setDimValue(0.4f)
                //变暗的背景颜色
//                    .setDimColor(Color.YELLOW)
                .setAnimationStyle(R.style.TopPopAnim);
    }

    @Override
    protected void initViews(View view, DealPopup basePopup) {
        View arrowView = view.findViewById(R.id.view_arrow);
        arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.WHITE));
        textViewMap = new SparseArray<>();
        textViewMap.put(1, (TextView) findViewById(R.id.tv_1));
        textViewMap.put(2, (TextView) findViewById(R.id.tv_2));
//        textViewMap.put(3, (TextView) findViewById(R.id.tv_3));
//        textViewMap.put(4, (TextView) findViewById(R.id.tv_4));
//        textViewMap.put(5, (TextView) findViewById(R.id.tv_5));
//        textViewMap.put(6, (TextView) findViewById(R.id.tv_6));
        if (null != mOnItemClickListener) {
            for (int i = 1; i <= 2; i++) {
                final int pos = i;
                if (null != textViewMap.get(i)) {
                    textViewMap.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemClickListener.itemClick(v, pos);
                            dismiss();
                        }
                    });
                }
            }
        }
    }

    public void updatePos(int pos) {
        for (int i = 1; i <= 2; i++) {
            if (null != textViewMap.get(i)) {
                if (i == pos) {
                    textViewMap.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_text_select));
                } else {
                    textViewMap.get(i).setTextColor(mContext.getResources().getColor(R.color.cl_333333));
                }
            }
        }
    }

    public DealPopup setIsBuy(boolean is_buy) {
        if (is_buy) {
            textViewMap.get(1).setText(R.string.limit_price_buy);
            textViewMap.get(2).setText(R.string.market_fast_buy);
        } else {
            textViewMap.get(1).setText(R.string.limit_price_sell);
            textViewMap.get(2).setText(R.string.market_fast_sell);
        }
        return this;
    }

    public DealPopup setMyOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        return this;
    }

    public String getNowText(int pos) {
        if (null != textViewMap && null != textViewMap.get(pos))
            return textViewMap.get(pos).getText().toString();
        else
            return mContext.getResources().getString(R.string.limit_price_buy);
    }

    public interface OnItemClickListener {
        void itemClick(View view, int pos);
    }

}
