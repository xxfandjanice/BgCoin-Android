package com.fmtch.module_bourse.ui.trade.model;

import android.view.View;

import com.fmtch.base.imageloader.CircleTransformation;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.widget.dialog.CustomDialog;
import com.fmtch.base.widget.dialog.ShowDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.MerchantHomePageBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.MerchantHomePageActivity;
import com.fmtch.module_bourse.utils.TimeUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/6/28
 */
public class MerchantHomePageModel {
    private MerchantHomePageActivity view;
    private MerchantHomePageBean.StatisticsBean statistics;

    public MerchantHomePageModel(MerchantHomePageActivity view) {
        this.view = view;
    }

    /**
     * 加载数据
     */
    public void getData(int userId) {
        RetrofitManager.getInstance().create(ApiService.class)
                .getMerchantHomePage(String.valueOf(userId))
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<MerchantHomePageBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MerchantHomePageBean>() {
                    @Override
                    public void onSuccess(MerchantHomePageBean data) {
                        if (data == null) {
                            return;
                        }
                        MerchantHomePageBean.BaseInfoBean baseInfo = data.getBase_info();
                        statistics = data.getStatistics();
                        List<MerchantHomePageBean.BuyOrSellBean> buy_list = data.getBuy_list();
                        List<MerchantHomePageBean.BuyOrSellBean> sell_list = data.getSell_list();

                        initBaseInfo(baseInfo);
                        initBuyAndSell(sell_list, buy_list);
                    }
                });
    }


    /**
     * 初始化基本信息
     */
    private void initBaseInfo(MerchantHomePageBean.BaseInfoBean baseInfo) {
        GlideLoadUtils.getInstance().glideLoad(view, baseInfo.getAvatar(), view.ivAvatar, new CircleTransformation(view), R.drawable.icon_default_avatar);
        view.tvName.setText(baseInfo.getUsername());
        view.tvRegisterTime.setText(String.format("注册时间 %s", baseInfo.getCreated_at()));
        view.isBlack = baseInfo.getIs_black() == 1;
        view.isAttention = baseInfo.getIs_star() == 1;
        updateAttentionStyle();
        updatePageStyle();
    }


    /**
     * 买入/出售列表
     *
     * @param sell_list 出售
     * @param buy_list  买入
     */
    private void initBuyAndSell(List<MerchantHomePageBean.BuyOrSellBean> sell_list, List<MerchantHomePageBean.BuyOrSellBean> buy_list) {
        if (buy_list != null) {
            view.list.addAll(buy_list);
        }
        if (sell_list != null) {
            view.list.addAll(sell_list);
        }
        view.adapter.setNewData(view.list);
    }

    /**
     * 关注/取消关注
     *
     * @param userId
     */
    public void attention(final int userId) {
        //拉黑状态下加关注,自动解除拉黑
        if (!view.isAttention && view.isBlack) {
            new CustomDialog(view)
                    .setTitle(R.string.add_attention)
                    .setContent(R.string.add_attention_tip)
                    .setLeftBtnStr(R.string.think_about_again)
                    .setRightBtnStr(R.string.sure)
                    .setSubmitListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addOrReleaseAttention(userId, true);
                        }
                    })
                    .builder()
                    .show();
            return;
        }
        addOrReleaseAttention(userId, false);
    }


    /**
     * @param userId               用户id
     * @param toAttentionWithBlack 在拉黑状态下关注
     */
    private void addOrReleaseAttention(int userId, final boolean toAttentionWithBlack) {
        final ShowDialog dialog = ShowDialog.showDialog(view, view.getResources().getString(R.string.loading), true, null);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId + "");
        map.put("type", view.isAttention ? "2" : "1");  //1 关注  2取消关注
        RetrofitManager.getInstance().create(ApiService.class)
                .addOrReleaseAttention(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        dialog.dismiss();
                        view.isAttention = !view.isAttention;
                        updateAttentionStyle();
                        if (toAttentionWithBlack) {
                            view.isBlack = false;
                            updatePageStyle();
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        dialog.dismiss();
                    }

                    @Override
                    public void onException(Throwable e) {
                        super.onException(e);
                        dialog.dismiss();
                    }
                });
    }

    /**
     * 更新关注按钮样式
     */
    private void updateAttentionStyle() {
        view.tvAttention.setSelected(view.isAttention);
        view.tvAttention.setText(view.isAttention ? R.string.has_attention : R.string.add_attention);
    }

    /**
     * 拉黑/解除拉黑
     *
     * @param userId
     */
    public void addOrReleaseBlack(final int userId) {
        //拉黑自动取消关注
        if (!view.isBlack) {
            new CustomDialog(view)
                    .setTitle(R.string.add_black)
                    .setContent(view.isAttention ? R.string.add_black_attention_tip : R.string.add_black_tip)
                    .setLeftBtnStr(R.string.think_about_again)
                    .setRightBtnStr(R.string.sure)
                    .setSubmitListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            releaseBlackState(userId, view.isAttention);
                        }
                    })
                    .builder()
                    .show();
            return;
        }
        releaseBlackState(userId, false);
    }

    /**
     * @param userId               用户id
     * @param toBlackWithAttention 在关注状态下拉黑
     */
    private void releaseBlackState(int userId, final boolean toBlackWithAttention) {
        final ShowDialog dialog = ShowDialog.showDialog(view, view.getResources().getString(R.string.loading), true, null);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId + "");
        map.put("type", view.isBlack ? "2" : "1");  //1 拉黑  2取消拉黑
        RetrofitManager.getInstance().create(ApiService.class)
                .addOrReleaseBlack(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        dialog.dismiss();
                        view.isBlack = !view.isBlack;
                        updatePageStyle();
                        if (toBlackWithAttention) {
                            view.isAttention = false;
                            updateAttentionStyle();
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        dialog.dismiss();
                    }

                    @Override
                    public void onException(Throwable e) {
                        super.onException(e);
                        dialog.dismiss();
                    }
                });
    }

    /**
     * 更新拉黑和未拉黑状态下页面的样式
     */
    private void updatePageStyle() {
        view.ivBg.setBackgroundResource(view.isBlack ? R.mipmap.bg_merchant_home_page_black : R.mipmap.bg_merchant_home_page);
        view.ivAddBlack.setVisibility(view.isBlack ? View.GONE : View.VISIBLE);
        view.tvReleaseBlack.setVisibility(view.isBlack ? View.VISIBLE : View.GONE);
        view.txtOrderTotal.setTextColor(view.isBlack ? view.getResources().getColor(R.color.cl_C6C6C6) : view.getResources().getColor(R.color.cl_999999));
        view.txtCompleteRate.setTextColor(view.isBlack ? view.getResources().getColor(R.color.cl_C6C6C6) : view.getResources().getColor(R.color.cl_999999));
        view.txtPayTime.setTextColor(view.isBlack ? view.getResources().getColor(R.color.cl_C6C6C6) : view.getResources().getColor(R.color.cl_999999));
        view.txtPutCoinTime.setTextColor(view.isBlack ? view.getResources().getColor(R.color.cl_C6C6C6) : view.getResources().getColor(R.color.cl_999999));
        view.tvOrderTotal.setText(view.isBlack ? "-,-" : statistics.getOrder_count() + "");
        view.tvCompleteRate.setText(view.isBlack ? "-,-" : statistics.getFinish_percent() + "");
        view.tvPayTime.setText(view.isBlack ? "-,-" : TimeUtils.getTimeSpanFormat(statistics.getAvg_payment_time()));
        view.tvPutCoinTime.setText(view.isBlack ? "-,-" : TimeUtils.getTimeSpanFormat(statistics.getAvg_confirm_time()));
        view.tvCertificationLevel.setTextColor(view.isBlack ? view.getResources().getColor(R.color.cl_999999) : view.getResources().getColor(R.color.cl_F5A623));
        view.tvCertificationLevel.setBackgroundResource(view.isBlack ? R.drawable.shape_bg_certification_level_black : R.drawable.shape_bg_certification_level);
        view.rv.setVisibility(view.isBlack ? View.GONE : View.VISIBLE);
        if (view.isBlack) {
            //拉黑后appBar展开
            view.appBar.setExpanded(true, true);
        }
    }

}
