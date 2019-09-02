package com.fmtch.module_bourse.ui.home.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.CoinIntroduceBean;
import com.fmtch.module_bourse.bean.DealOkBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/17
 * K线图-简介页面
 */
public class IntroduceFragment extends LazyBaseFragment {


    @BindView(R2.id.tv_crowd_funding_price)
    TextView tvCrowdFundingPrice;
    @BindView(R2.id.tv_white_book)
    TextView tvWhiteBook;
    @BindView(R2.id.tv_official_website)
    TextView tvOfficialWebsite;
    @BindView(R2.id.tv_block_chain_query)
    TextView tvBlockChainQuery;
    @BindView(R2.id.tv_introduce)
    TextView tvIntroduce;
    private String symbol;
    private String coinName;

    public static IntroduceFragment newInstance(Bundle bundle) {
        IntroduceFragment fragment = new IntroduceFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_introduce;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            symbol = bundle.getString(PageConstant.SYMBOL);
            coinName = symbol.split("/")[0];
        }
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        getData(coinName);
    }

    public void getData(String coinName) {
        if (TextUtils.isEmpty(coinName)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("coin_name", coinName);
        RetrofitManager.getInstance().create(ApiService.class).getCoinIntroduce(map)
                .subscribeOn(Schedulers.io())
                .compose(this.<BaseResponse<CoinIntroduceBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<CoinIntroduceBean>() {
                    @Override
                    public void onSuccess(CoinIntroduceBean data) {
                        if (data != null) {
                            tvCrowdFundingPrice.setText(data.getPrice());
                            tvWhiteBook.setText(data.getPaper());
                            tvOfficialWebsite.setText(data.getWebsite());
                            tvBlockChainQuery.setText(data.getHash());
                            tvIntroduce.setText(data.getIntroduce());
                        }

                    }
                });
    }
}
