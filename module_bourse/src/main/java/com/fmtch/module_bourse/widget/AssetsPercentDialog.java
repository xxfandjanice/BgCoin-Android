package com.fmtch.module_bourse.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.utils.AndroidShare;
import com.fmtch.base.utils.FileUtils;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.ShotViewUtil;
import com.fmtch.base.widget.NoScrollRecyclerView;
import com.fmtch.base.widget.dialog.ShareDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.utils.ToastUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class AssetsPercentDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局

    private Dialog dialog;                          //构建的弹窗
    private boolean mCancelable = true;//是否弹窗可关闭（默认可关闭）

    private List<AccountBean> mData = new ArrayList<>();
    private BigDecimal mTotalNum;
    private boolean mIsSort = false;//是否截取过数据

    private Random random = new Random();


    public AssetsPercentDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_assets_percent, null);
    }

    //设置不可关闭时为强制更新不可点击取消
    public AssetsPercentDialog setCancelable(boolean cancel) {
        this.mCancelable = cancel;
        return this;
    }

    public AssetsPercentDialog setTotalNum(BigDecimal totalNum) {
        this.mTotalNum = totalNum;
        return this;
    }

    public AssetsPercentDialog setData(List<AccountBean> list) {
        if (list != null) {
            mData.clear();
            mData.addAll(list);
        }
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(mCancelable);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
        PercentPieView percentPieView = mDialogLayout.findViewById(R.id.pv_assets);
        NoScrollRecyclerView noScrollRv = mDialogLayout.findViewById(R.id.rv);

        //先排序取出前9条数据加上一条其他
        // 降序 排序     升序  -1 和 1 调换下
        Collections.sort(mData, new Comparator<AccountBean>() {
            @Override
            public int compare(AccountBean o1, AccountBean o2) {
                if (o1.getBtc_num().compareTo(o2.getBtc_num()) > 0) {
                    return -1;
                } else if (o1.getBtc_num().compareTo(o2.getBtc_num()) < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        //超过9条只取排名靠前前九条
        if (mData.size() > 9) {
            mIsSort = true;
            mData = mData.subList(0, 9);
        }
        List<BigDecimal> data = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        BigDecimal otherPercent = BigDecimal.ONE;
        for (AccountBean accountBean : mData) {
            BigDecimal percent = BigDecimal.ZERO;
            if (mTotalNum.compareTo(BigDecimal.ZERO) > 0) {
                percent = accountBean.getBtc_num().divide(mTotalNum, 8, BigDecimal.ROUND_DOWN);
            }
            accountBean.setRate_percent(percent);
            data.add(percent);
            otherPercent.subtract(percent);
            int color = randomColor();
            colors.add(color);
            accountBean.setTag_color(color);
        }
        if (mIsSort) {
            //截取后补充其他
            AccountBean accountBean = new AccountBean();
            AccountBean.Coin coin = new AccountBean.Coin();
            coin.setName(mContext.getResources().getString(R.string.other));
            accountBean.setCoin(coin);
            accountBean.setRate_percent(otherPercent);
            int otherColor = randomColor();
            accountBean.setTag_color(otherColor);
            mData.add(accountBean);
            data.add(otherPercent);
            colors.add(otherColor);
        }

        percentPieView.setData(data, colors);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        noScrollRv.setFocusable(false);
        noScrollRv.setLayoutManager(gridLayoutManager);
        noScrollRv.setNestedScrollingEnabled(false);
        BaseQuickPageStateAdapter<AccountBean, BaseViewHolder> adapter = new BaseQuickPageStateAdapter<AccountBean, BaseViewHolder>(mContext, R.layout.item_assets_percent) {

            @Override
            protected void convert(BaseViewHolder helper, AccountBean item) {
                helper.setText(R.id.tv_coin_name, item.getCoin().getName())
                        .setBackgroundColor(R.id.view_tag, item.getTag_color());
                if (item.getRate_percent().compareTo(BigDecimal.ZERO) > 0) {
                    helper.setText(R.id.tv_percent, item.getRate_percent().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "%");
                } else {
                    helper.setText(R.id.tv_percent, "0.00%");
                }
            }
        };
        noScrollRv.setAdapter(adapter);
        adapter.setNewData(mData);
        mDialogLayout.findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareDialog(mContext)
                        .setShareBitmap(ShotViewUtil.getViewBp(mDialogLayout))
                        .builder()
                        .show();
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.8); //设置宽度
//        lp.height = (int) (lp.width * 1.23); //设置高度
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    /**
     * 生成随机颜色
     */
    private int randomColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

}
