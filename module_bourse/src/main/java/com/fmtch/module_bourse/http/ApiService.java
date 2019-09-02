package com.fmtch.module_bourse.http;

import com.fmtch.base.net.API;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.pojo.response.CoinToUSDT;
import com.fmtch.base.pojo.response.RateInfo;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.bean.BannerBean;
import com.fmtch.module_bourse.bean.BlackOrAttentionBean;
import com.fmtch.module_bourse.bean.CoinAddressBean;
import com.fmtch.module_bourse.bean.CoinIntroduceBean;
import com.fmtch.module_bourse.bean.DeepBean;
import com.fmtch.module_bourse.bean.DelegationOrderBean;
import com.fmtch.module_bourse.bean.KChartBean;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.bean.MerchantHomePageBean;
import com.fmtch.module_bourse.bean.MyPendOrderBean;
import com.fmtch.module_bourse.bean.NoticeBean;
import com.fmtch.module_bourse.bean.ParisCoinBean;
import com.fmtch.module_bourse.bean.ParisCoinInfo;
import com.fmtch.module_bourse.bean.ParisCoinMarketBean;
import com.fmtch.module_bourse.bean.ParisOrderStateBean;
import com.fmtch.module_bourse.bean.PaymentBean;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.module_bourse.bean.response.ParisBuySellResponse;
import com.fmtch.module_bourse.bean.response.ParisOrderDetailResponse;
import com.fmtch.module_bourse.bean.response.PaymentResponse;
import com.fmtch.module_bourse.bean.ParisDelegationOrderDetailBean;
import com.fmtch.module_bourse.bean.RiseFallBean;
import com.fmtch.module_bourse.bean.TodayMarketStateBean;
import com.fmtch.module_bourse.bean.DealOkBean;
import com.fmtch.module_bourse.bean.request.AddCoinAddressRequest;
import com.fmtch.module_bourse.bean.request.AddPaymentRequest;
import com.fmtch.module_bourse.bean.request.ParisDelegationOrderBuyRequest;
import com.fmtch.module_bourse.bean.request.ParisDelegationOrderSellRequest;
import com.fmtch.module_bourse.bean.request.WithdrawRequest;
import com.fmtch.module_bourse.bean.response.PaymentStatusResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.realm.RealmList;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by wtc on 2017/11/13.
 */

public interface ApiService {

    @GET(API.HOME_BANNER)
    Observable<BaseResponse<List<BannerBean>>> getBanners();

    @GET(API.HOME_SYSTEM_NOTICE)
    Observable<BaseResponse<List<NoticeBean>>> getSystemNotices();

    @GET(API.HOME_RECOMMEND_SYMBOL)
    Observable<BaseResponse<List<MarketBean>>> getSystemRecommendList();

    @GET(API.HOME_RISE_FALL)
    Observable<BaseResponse<List<RiseFallBean>>> getRiseFallList();

    @GET(API.HOME_TRADE)
    Observable<BaseResponse<List<RiseFallBean>>> getTradeList();

    @GET(API.HOME_NEW_COIN)
    Observable<BaseResponse<List<RiseFallBean>>> getNewCoinList();

    @GET(API.HOME_TODAY_MARKET_PREDICT_VALUE)
    Observable<BaseResponse<TodayMarketStateBean>> getTodayMarketState();

    @POST(API.HOME_TODAY_MARKET_PREDICT_VALUE_FORECAST)
    Observable<BaseResponse<TodayMarketStateBean>> postTodayMarketForecast(@Path("type") String type);

    @GET(API.MARKET_COIN)
    Observable<BaseResponse<List<MarketBean>>> getMarketCoin(@Query("symbol") String symbol);

    @GET(API.TRADE_MY_PEND_ORDER)
    Observable<BaseResponse<List<MyPendOrderBean>>> getMyPendOrders(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_MY_WALLET)
    Observable<BaseResponse<List<AccountBean>>> getMyWallet();

    @GET(API.PROPERTY_COIN_COIN)
    Observable<BaseResponse<List<AccountBean>>> getCoinCoinList();

    @GET(API.PROPERTY_PARIS_COIN)
    Observable<BaseResponse<List<AccountBean>>> getParisCoinList();

    @GET(API.PROPERTY_MY_WALLET_LOG)
    Observable<BaseResponse<List<AccountDetailBean>>> getMyWalletLog(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_COIN_COIN_LOG)
    Observable<BaseResponse<List<AccountDetailBean>>> getCoinCoinLog(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_PARIS_COIN_LOG)
    Observable<BaseResponse<List<AccountDetailBean>>> getParisCoinLog(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_BRING_LOG)
    Observable<BaseResponse<List<AccountDetailBean>>> getBringLog(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_RECHARGE_LOG)
    Observable<BaseResponse<List<AccountDetailBean>>> getRechargeLog(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_TRANSFER_LOG)
    Observable<BaseResponse<List<AccountDetailBean>>> getTransferLog(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_MY_WALLET_LOG_NEW)
    Observable<BaseResponse<List<AccountDetailBean>>> getMyWalletLogNew(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_COIN_COIN_LOG_NEW)
    Observable<BaseResponse<List<AccountDetailBean>>> getCoinCoinLogNew(@QueryMap Map<String, String> map);

    @GET(API.PROPERTY_PARIS_COIN_LOG_NEW)
    Observable<BaseResponse<List<AccountDetailBean>>> getParisCoinLogNew(@QueryMap Map<String, String> map);

    @GET(API.K_LINE_DEEP)
    Observable<BaseResponse<DeepBean>> getDeepList(@QueryMap Map<String, String> map);

    @GET(API.K_LINE_TRADE)
    Observable<BaseResponse<List<DealOkBean>>> getDealOkList(@QueryMap Map<String, String> map);

    @GET(API.K_LINE_COIN_INTRODUCE)
    Observable<BaseResponse<CoinIntroduceBean>> getCoinIntroduce(@QueryMap Map<String, String> map);

    @DELETE(API.TRADE_CANCEL_SINGLE_ORDER)
    Observable<BaseResponse<String>> cancelSingleOrder(@Path("order") String orderId);

    @DELETE(API.TRADE_CANCEL_ALL_ORDER)
    Observable<BaseResponse<String>> cancelAllOrder();

    @GET(API.K_LINE_CHART)
    Observable<BaseResponse<List<KChartBean>>> getKLineChart(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST(API.TRADE_CREATE_ORDER)
    Observable<BaseResponse<String>> createOrder(@FieldMap Map<String, String> map);

    @Multipart
    @POST(HttpConstants.LOGIN)
    Observable<BaseResponse<String>> uploadFile(@Url String url, @PartMap Map<String, RequestBody> map);


    @GET(API.PROPERTY_MY_WALLET)
    Observable<BaseResponse<ArrayList<AccountBean>>> getCoinList();

    @GET(API.PROPERTY_COIN_COIN)
    Observable<BaseResponse<ArrayList<AccountBean>>> getCoinCoinListData();

    @GET(API.PROPERTY_PARIS_COIN)
    Observable<BaseResponse<ArrayList<AccountBean>>> getParisCoinListData();

    @GET(API.PROPERTY_MY_WALLET)
    Observable<BaseResponse<AccountBean>> getCoinList(@Query("coin_id") String coin_id);

    @GET(API.PROPERTY_COIN_COIN)
    Observable<BaseResponse<AccountBean>> getCoinCoinData(@Query("coin_id") String coin_id);

    @GET(API.PROPERTY_PARIS_COIN)
    Observable<BaseResponse<AccountBean>> getParisCoinData(@Query("coin_id") String coin_id);

    @POST(API.COIN_ADDRESS)
    Observable<BaseResponse<String>> addCoinAddress(@Body AddCoinAddressRequest request);

    @GET
    Observable<BaseResponse<List<CoinAddressBean>>> getCoinAddressList(@Url String url);

    @DELETE()
    Observable<BaseResponse<String>> deleteCoinAddress(@Url String url);

    @POST(API.WITHDRAW)
    Observable<BaseResponse<String>> withdraw(@Body WithdrawRequest request);

    @POST(API.TRANSFER)
    Observable<BaseResponse<String>> transfer(@QueryMap Map<String, String> map);

    @GET(API.GET_RATE)
    Observable<BaseResponse<RateInfo>> getRate();

    @GET(API.GET_COIN_TO_BTC)
    Observable<BaseResponse<RealmList<CoinToBTC>>> getCoinToBTC();

    @GET(API.GET_COIN_TO_USDT)
    Observable<BaseResponse<RealmList<CoinToUSDT>>> getCoinToUSDT();

    @GET(API.TRADE_PAYMENT_LIST)
    Observable<BaseResponse<PaymentResponse>> getPaymentList();

    @GET(API.TRADE_PAYMENT_STATUS)
    Observable<BaseResponse<PaymentStatusResponse>> getPaymentStatus();

    @POST
    Observable<BaseResponse<String>> changePayment(@Url String url);

    @POST
    Observable<BaseResponse<String>> unbindPayment(@Url String url);

    @POST(API.TRADE_PAYMENT_ADD_BANK)
    Observable<BaseResponse<String>> addPaymentBank(@Body AddPaymentRequest request);

    @POST(API.TRADE_PAYMENT_ADD_ZFB)
    Observable<BaseResponse<String>> addPaymentZFB(@Body AddPaymentRequest request);

    @POST(API.TRADE_PAYMENT_ADD_WECHAT)
    Observable<BaseResponse<String>> addPaymentWechat(@Body AddPaymentRequest request);

    @GET(API.TRADE_GET_BUY_INFO)
    Observable<BaseResponse<ParisBuySellResponse>> getParisBuyInfo(@Query("id") int id);

    @POST(API.TRADE_PARIS_BUY)
    Observable<BaseResponse<PaymentBean>> parisCoinBuy(@Query("id") int id, @Query("buy_number") String buy_number);

    @GET(API.TRADE_GET_SELL_INFO)
    Observable<BaseResponse<ParisBuySellResponse>> getParisSellInfo(@Query("id") int id);

    @POST(API.TRADE_PARIS_SELL)
    Observable<BaseResponse<PaymentBean>> parisCoinSell(@Query("id") int id, @Query("sell_number") String sell_number, @Query("pay_password") String pay_password);

    @GET(API.TRADE_PARIS_ORDER_DETAIL)
    Observable<BaseResponse<ParisOrderDetailResponse>> getParisOrderDetail(@Query("id") int id);

    @POST(API.TRADE_PARIS_ORDER_PAY)
    Observable<BaseResponse<PaymentBean>> parisOrderPay(@Query("id") int id, @Query("payment_account_id") int payment_account_id);

    @POST(API.TRADE_PARIS_ORDER_CANCEL)
    Observable<BaseResponse<PaymentBean>> parisOrderCancel(@Query("id") int id);

    @POST(API.TRADE_PARIS_ORDER_FINISH)
    Observable<BaseResponse<PaymentBean>> parisOrderFinish(@Query("id") int id,@Query("pay_password") String pay_password);

    @GET(API.PARIS_COIN_KIND)
    Observable<BaseResponse<List<ParisCoinBean>>> getParisCoinKindList();

    @GET(API.PARIS_COIN_MARKET_LIST)
    Observable<BaseResponse<List<ParisCoinMarketBean>>> getParisMarketList(@QueryMap Map<String, String> map);

    @POST(API.PARIS_COIN_BUY_IN)
    Observable<BaseResponse<String>> publishDelegationOrderBuy(@Body ParisDelegationOrderBuyRequest request);

    @POST(API.PARIS_COIN_SELL_OUY)
    Observable<BaseResponse<String>> publishDelegationOrderSell(@Body ParisDelegationOrderSellRequest request);

    @GET(API.PARIS_COIN_INFO)
    Observable<BaseResponse<ParisCoinInfo>> getParisCoinInfo(@Query("coin_id") String coin_id);

    @GET(API.PARIS_COIN_DELEGATION_ORDER)
    Observable<BaseResponse<List<DelegationOrderBean>>> getParisDelegationOrders(@QueryMap Map<String, String> map);

    @POST(API.PARIS_COIN_CANCEL_ORDER)
    Observable<BaseResponse<String>> cancelDelegationOrder(@Path("book") String order_id);

    @POST(API.PARIS_COIN_PAUSE_ORDER)
    Observable<BaseResponse<Integer>> pauseDelegationOrder();

    @GET(API.PARIS_COIN_ORDER_DETAIL)
    Observable<BaseResponse<ParisDelegationOrderDetailBean>> getParisDelegationOrderDetail(@Path("book") String id);

    @GET(API.PARIS_COIN_ORDER_STATE)
    Observable<BaseResponse<Integer>> getParisOrderSwitchState();


    @GET(API.PARIS_COIN_MY_ORDER)
    Observable<BaseResponse<List<ParisOrderStateBean>>> getParisOrders(@QueryMap Map<String, String> map);

    @GET(API.PARIS_COIN_MERCHANT_HOME_PAGE)
    Observable<BaseResponse<MerchantHomePageBean>> getMerchantHomePage(@Query("user_id") String user_id);

    @GET(API.PARIS_COIN_BLACK_LIST)
    Observable<BaseResponse<List<BlackOrAttentionBean>>> getBlackList(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST(API.PARIS_COIN_BLACK)
    Observable<BaseResponse<String>> addOrReleaseBlack(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(API.PARIS_COIN_ATTENTION)
    Observable<BaseResponse<String>> addOrReleaseAttention(@FieldMap Map<String, String> map);

    @GET(API.GET_SYMBOLS)
    Observable<BaseResponse<List<SymbolBean>>> getSymbols(@Query("symbol") String symbol);

    @GET(API.GET_MARKET)
    Observable<BaseResponse<List<MarketBean>>> getMarkets(@Query("symbol") String symbol);
}

