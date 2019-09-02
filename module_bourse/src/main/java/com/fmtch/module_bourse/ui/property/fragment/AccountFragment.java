package com.fmtch.module_bourse.ui.property.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.pojo.response.CoinToUSDT;
import com.fmtch.base.pojo.response.RateInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.AccountAdapter;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.ui.property.model.AccountModel;
import com.fmtch.module_bourse.utils.KeyboardUtils;
import com.fmtch.module_bourse.widget.AssetsPercentDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by wtc on 2019/5/14
 * 资产页面-我的钱包、币币账户、法币账户
 */
public class AccountFragment extends LazyBaseFragment implements View.OnClickListener, TextView.OnEditorActionListener {

    public TextView tvTotalCoin;
    public TextView tvTotalMoney;
    TextView tvTotalMoneyRate;
    @BindView(R2.id.rv)
    RecyclerView rv;
    ImageView ivHideFewCoinKind;
    ImageView tvPropertyDis;
    EditText etSearch;

    public boolean isHideFenCoinKind = false;  //是否隐藏小币种
    public boolean isHideMoney = false;    //是否明文显示总资产
    public String hideText = "*****";
    public BaseQuickPageStateAdapter<AccountBean, BaseViewHolder> adapter;
    public List<AccountBean> list;//全部列表
    public List<AccountBean> haveMoneyList;//有钱列表
    public List<AccountBean> coinListCopy;  //备份全部数据
    public List<AccountBean> haveMoneyListCopy;  //备份有钱数据
    private AccountModel model;
    private int fragmentType;

    public BigDecimal totalBTC;
    public RealmList<CoinToBTC> coinToBTCList;
    public RealmList<CoinToUSDT> coinToUSDTList;
    private View headerView;

    public static AccountFragment newInstance(Bundle bundle) {
        AccountFragment fragment = new AccountFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_account;
    }

    @Override
    protected void initData() {
        super.initData();
        initHeaderView();
        totalBTC = BigDecimal.ZERO;
        RateInfo rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        if (rateInfo != null) {
            coinToBTCList = rateInfo.getCoin_to_btc_list();
            coinToUSDTList = rateInfo.getCoin_to_usdt_list();
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            list = new ArrayList<>();
            haveMoneyList = new ArrayList<>();
            coinListCopy = new ArrayList<>();
            haveMoneyListCopy = new ArrayList<>();
            fragmentType = bundle.getInt(FRAGMENT_KEY);
            if (fragmentType == PropertyFragment.PROPERTY_MY_ACCOUNT) {
                tvTotalMoneyRate.setText(R.string.account_total_money_rate);
            } else if (fragmentType == PropertyFragment.PROPERTY_COIN_COIN_ACCOUNT) {
                tvTotalMoneyRate.setText(R.string.coin_total_money_rate);
                tvPropertyDis.setVisibility(View.GONE);
            } else if (fragmentType == PropertyFragment.PROPERTY_PARIS_COIN_ACCOUNT) {
                tvTotalMoneyRate.setText(R.string.paris_total_money_rate);
                tvPropertyDis.setVisibility(View.GONE);
            }

            model = new AccountModel(this);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        initAdapter();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (model != null && !TextUtils.isEmpty((String) SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN, ""))) {
            etSearch.setText("");
            etSearch.clearFocus();
            KeyboardUtils.hideSoftInput(getActivity());
            model.getData(fragmentType);
        }
    }

    private void initHeaderView() {
        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_top_account, null);
        tvTotalMoneyRate = headerView.findViewById(R.id.tv_total_money_rate);
        tvTotalMoney = headerView.findViewById(R.id.tv_total_money);
        tvTotalCoin = headerView.findViewById(R.id.tv_total_coin);
        tvPropertyDis = headerView.findViewById(R.id.tv_property_dis);
        ivHideFewCoinKind = headerView.findViewById(R.id.iv_hide_few_coin_kind);
        etSearch = headerView.findViewById(R.id.search);
        etSearch.addTextChangedListener(textWatcher);
        etSearch.setOnEditorActionListener(this);
        tvPropertyDis.setOnClickListener(this);
        ivHideFewCoinKind.setOnClickListener(this);
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AccountAdapter(getActivity(), R.layout.item_account, coinToBTCList, coinToUSDTList);
        adapter.setHeaderAndEmpty(true);
        adapter.addHeaderView(headerView);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AccountBean bean = list.get(position);
                if (isHideFenCoinKind) {
                    bean = haveMoneyList.get(position);
                }
                ARouter.getInstance().build(RouterMap.ACCOUNT_DETAIL)
                        .withSerializable("detail", bean)
                        .withInt(FRAGMENT_KEY, fragmentType)
                        .navigation();
            }
        });
    }

    /**
     * 账户资产是否明文显示
     */
    public void setMoneyVisible(boolean isHide) {
        this.isHideMoney = isHide;
        if (tvTotalCoin != null && tvTotalMoney != null) {
            tvTotalCoin.setText(isHideMoney ? hideText : totalBTC.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
            tvTotalMoney.setText(isHideMoney ? hideText : "≈ " + NumberUtils.getBTCToMoneyWithUnit(totalBTC));
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_property_dis) {
            new AssetsPercentDialog(getContext())
                    .setTotalNum(totalBTC)
                    .setData(list)
                    .builder()
                    .show();
        } else if (id == R.id.iv_hide_few_coin_kind) {
            ivHideFewCoinKind.setImageResource(isHideFenCoinKind ? R.mipmap.icon_hide_few_coin_kind_unselect : R.mipmap.icon_hide_few_coin_kind_selected);
            isHideFenCoinKind = !isHideFenCoinKind;
            if (etSearch != null)
                etSearch.setText("");
            if (adapter != null) {
                if (isHideFenCoinKind) {
                    haveMoneyList.clear();
                    haveMoneyList.addAll(haveMoneyListCopy);
                    adapter.setNewData(haveMoneyList);
                }else {
                    list.clear();
                    list.addAll(coinListCopy);
                    adapter.setNewData(list);
                }
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchCoin(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void searchCoin(String s) {
        if (isHideFenCoinKind) {
            haveMoneyList.clear();
            try {
                //输入特殊字符可能导致crash
                Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                for (AccountBean coinBean : haveMoneyListCopy) {
                    Matcher matcherName = pattern.matcher(coinBean.getCoin().getName());
                    if (matcherName.find()) {
                        haveMoneyList.add(coinBean);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        } else {
            list.clear();
            try {
                //输入特殊字符可能导致crash
                Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                for (AccountBean coinBean : coinListCopy) {
                    Matcher matcherName = pattern.matcher(coinBean.getCoin().getName());
                    if (matcherName.find()) {
                        list.add(coinBean);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {//按下搜索
            searchCoin(etSearch.getText().toString());
            return true;
        }
        return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event.getTag() == EventType.CHANGE_UNIT) {
            tvTotalMoney.setText(isHideMoney ? hideText : "≈ " + NumberUtils.getBTCToMoneyWithUnit(totalBTC));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

}
