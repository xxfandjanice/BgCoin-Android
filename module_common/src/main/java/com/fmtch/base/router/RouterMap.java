package com.fmtch.base.router;

public class RouterMap {

    public static final String MAIN_PAGE = "/bourse/MainActivity";//首页
    public static final String RECHARGE_COIN = "/bourse/recharge_coin";                             //充币
    public static final String BRING_COIN = "/bourse/bring_coin";                                   //提币
    public static final String TRANSFER_COIN = "/bourse/transfer_coin";                             //划转
    public static final String TRANSFER_COIN_RECORDS = "/bourse/transfer_coin_records";             //充币提币划转记录
    public static final String ADD_COIN_ADDRESS = "/bourse/add_coin_address";                       //添加币种地址
    public static final String COIN_ADDRESS_BOOK = "/bourse/coin_address_book";                     //币种地址列表
    public static final String TRANSFER_DETAIL = "/bourse/transfer_detail";                         //充币提币划转详情
    public static final String CHOOSE_COIN = "/home/ChooseCoinActivity";//选择币种
    public static final String SEARCH_COIN = "/home/SearchCoinActivity";//搜索币种
    public static final String TRADE_RISE_FALL_DISTRIBUTION = "/home/RiseFallDistributionActivity"; //交易对涨跌分布
    public static final String K_LINE = "/home/KLineActivity";   //K线图
    public static final String K_LINE_FULL_SCREEN = "/home/FullScreenKlineActivity"; //全屏K线
    public static final String ACCOUNT_DETAIL = "/property/AccountDetailActivity";  //资产详情
    public static final String WEB_VIEW = "/common/web_view";

    /** mine **/
    public static final String ACCOUNT_LOGIN = "/mine/account_login";                               //账户登录
    public static final String FORGET_PASSWORD = "/mine/forget_password";                           //忘记密码
    public static final String REGISTER = "/mine/register";                                         //注册
    public static final String SELECT_COUNTRY_CODE = "/mine/select_country_code";                   //选择国家区号
    public static final String SAFE_CENTER = "/mine/safe_center";                                   //安全中心
    public static final String SETTING = "/mine/setting";                                           //设置
    public static final String BIND_RELEASE = "/mine/bind_release";                                 //解绑手机号或邮箱
    public static final String BIND = "/mine/bind";                                                 //绑定手机号或邮箱
    public static final String SET_USER_NAME = "/mine/set_user_name";                               //设置用户名
    public static final String MODIFY_LOGIN_PWD = "/mine/modify_login_pwd";                         //修改登录密码
    public static final String SET_ASSETS_PWD = "/mine/set_assets_pwd";                             //设置修改资金密码
    public static final String GESTURES_PWD = "/mine/gestures_pwd";                                 //手势密码
    public static final String GESTURES_PWD_SETTING = "/mine/gestures_pwd_setting";                 //设置手势密码
    public static final String CHECK_GESTURES_PWD = "/mine/check_gestures_pwd";                     //验证手势密码
    public static final String CHECK_FINGER_PWD = "/mine/check_finger_pwd";                         //验证指纹密码
    public static final String SET_GOOGLE_CHECK = "/mine/set_google_check";                         //绑定与解绑谷歌
    public static final String GOOGLE_CHECK = "/mine/google_check";                                 //绑定验证谷歌
    public static final String AUTH = "/mine/auth";                                                 //实名认证
    public static final String AUTH_STATUS = "/mine/auth_status";                                   //实名认证状态
    public static final String ADVICE = "/mine/advice";                                             //建议反馈
    public static final String INVITE_FRIENDS = "/mine/invite_friends";                             //邀请好友
    public static final String SETTING_CURRENCY_UNIT = "/mine/setting_currency_unit";               //设置货币单位
    public static final String SETTING_LANGUAGE = "/mine/setting_language";                         //设置语言
    public static final String ABOUT_US = "/mine/about_us";                                         //关于我们
    public static final String VERSION_LOG = "/mine/version_log";                                   //版本日志



    /** trade **/
    public static final String MY_PEND_ORDER = "/trade/MyPendOrderActivity";  //我的挂单
    public static final String HISTORY_DELEGATION = "/trade/HistoryDelegationActivity";  //历史委托
    public static final String PARIS_BUY = "/trade/paris_buy";  //法币购买
    public static final String PARIS_BUY_ORDER = "/trade/paris_buy_order";  //法币购买订单
    public static final String PARIS_SELL = "/trade/paris_sell";  //法币出售
    public static final String PARIS_SELL_ORDER = "/trade/paris_sell_order";  //法币出售订单
    public static final String PAYMENT_SETTING = "/trade/payment_setting";  //收付款设置
    public static final String ADD_PAYMENT = "/trade/add_payment";  //添加收付款

    public static final String MERCHANT_HOMEPAGE = "/trade/MerchantHomePageActivity";//商家主页
    public static final String PUBLISH_DELEGATION_ORDER = "/trade/PublishDelegationOrderActivity";//发布委托单
    public static final String DELEGATION_ORDER = "/trade/DelegationOrderActivity"; //委托单
    public static final String DELEGATION_ORDER_DETAIL = "/trade/DelegationOrderDetailActivity";//委托单详情
    public static final String PARIS_COIN_ORDER = "/trade/ParisCoinOrderActivity"; //法币订单
    public static final String PARIS_COIN_ORDER_DETAIL = "/trade/ParisCoinOrderDetailActivity"; //法币订单详情
    public static final String BLACKLIST_ATTENTION = "/trade/BlacklistAttentionActivity";  //黑名单/关注
}
