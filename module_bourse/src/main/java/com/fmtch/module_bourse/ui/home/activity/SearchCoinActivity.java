package com.fmtch.module_bourse.ui.home.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.widget.ClearEditText;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.utils.KeyboardUtils;
import com.fmtch.module_bourse.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;

@Route(path = RouterMap.SEARCH_COIN)
public class SearchCoinActivity extends BaseActivity implements TextView.OnEditorActionListener {

    @BindView(R2.id.search)
    ClearEditText search;
    @BindView(R2.id.rv)
    RecyclerView rv;

    private List<SymbolBean> list;
    private List<SymbolBean> listCopy;
    private BaseQuickPageStateAdapter<SymbolBean, BaseViewHolder> adapter;
    private RealmResults<SymbolBean> symbols;
    private boolean finish;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_coin;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        list = new ArrayList<>();
        search.requestFocus();
        search.setOnEditorActionListener(this);
        KeyboardUtils.toggleSoftInput();
        initAdapter();
        initMyChooseList();
        finish = getIntent().getBooleanExtra(PageConstant.FINISH_WITH_DATA, false);
    }

    /**
     * 从本地缓存中获取所有的交易对
     */
    private void initMyChooseList() {
        symbols = Realm.getDefaultInstance().where(SymbolBean.class).findAll();
        list.addAll(symbols);
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<SymbolBean, BaseViewHolder>(this, R.layout.item_search_coin) {
            @Override
            protected void convert(BaseViewHolder helper, SymbolBean item) {
                helper.setText(R.id.tv_name, item.getSymbol());
                helper.addOnClickListener(R.id.iv_add);
                helper.setImageResource(R.id.iv_add, item.isStar() ? R.mipmap.icon_add_small_selected : R.mipmap.icon_add_small);
            }
        };
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                KeyboardUtils.hideSoftInput(SearchCoinActivity.this);
                if (finish) {
                    Intent intent = new Intent();
                    intent.putExtra(PageConstant.SYMBOL, list.get(position).getSymbol());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return;
                }
                ARouter.getInstance().build(RouterMap.K_LINE)
                        .withString(PageConstant.SYMBOL, list.get(position).getSymbol())
                        .navigation();
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                final SymbolBean symbolBean = list.get(position);
                ImageView ivAdd = (ImageView) view;
                ivAdd.setImageResource(!symbolBean.isStar() ? R.mipmap.icon_add_small_selected : R.mipmap.icon_add_small);
                ToastUtils.showShortToast(!symbolBean.isStar() ? R.string.add_my_choose_success : R.string.delete_my_choose_success);

                //更新本地缓存
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        symbolBean.setStar(!symbolBean.isStar());
                    }
                });

            }
        });
    }

    @OnClick({R2.id.tv_search, R2.id.iv_back})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_search) {
            searchCoin(search.getText().toString());
        } else if (id == R.id.iv_back) {
            KeyboardUtils.hideSoftInput(this);
            finish();
        }
    }

    private void searchCoin(String input) {
        if (TextUtils.isEmpty(input)) {
            ToastUtils.showShortToast(R.string.search_tip);
            return;
        }
        try {
            //输入特殊字符可能导致crash
            list.clear();
            Pattern pattern = Pattern.compile(input, Pattern.CASE_INSENSITIVE);
            for (SymbolBean coinBean : symbols) {
                Matcher matcherName = pattern.matcher(coinBean.getSymbol());
                if (matcherName.find()) {
                    list.add(coinBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size() == 0) {
            adapter.showEmptyPage();
        } else {
            adapter.setNewData(list);
        }
    }

    /**
     * 根据用户输入时时搜索
     */
    @OnTextChanged(value = R2.id.search, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            list.clear();
            adapter.notifyDataSetChanged();
        } else {
            searchCoin(s.toString());
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {//按下搜索
            searchCoin(v.getText().toString());
            return true;
        }
        return false;
    }

}
