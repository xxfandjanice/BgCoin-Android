package com.fmtch.base.net;

import com.fmtch.base.manager.RetrofitManager;

public interface API {
    

    /**
     * 用户手机号注册
     */
    String REGISTER_MOBILE = RetrofitManager.BASE_URL + "/api/auth/register/mobile";
    /**
     * 用户邮箱注册
     */
    String REGISTER_EMAIL = RetrofitManager.BASE_URL + "/api/auth/register/email";

    /**
     * 发送短信验证码
     */
    String SEND_SMS_CODE = RetrofitManager.BASE_URL + "/api/common/sms";
    /**
     * 发送邮件验证码
     */
    String SEND_EMAIL_CODE = RetrofitManager.BASE_URL + "/api/common/email";

    /**
     * 用户手机号登录
     */
    String LOGIN_MOBILE = RetrofitManager.BASE_URL + "/api/auth/login/mobile";
    /**
     * 用户邮箱登录
     */
    String LOGIN_EMAIL = RetrofitManager.BASE_URL + "/api/auth/login/email";

    /**
     * 登录二次验证
     */
    String LOGIN_TWICE_CHECK = RetrofitManager.BASE_URL + "/api/auth/tfa";

    /**
     * 判断用户是否注册
     */
    String CHECK_ACCOUNT = RetrofitManager.BASE_URL + "/api/auth/register_confirm";

    /**
     * 获取二次验证类型
     */
    String GET_SECOND_CHECK_TYPE = RetrofitManager.BASE_URL + "/api/auth/tfa_type";

    /**
     * 手机找回登录密码
     */
    String FIND_LOGIN_PWD_MOBILE = RetrofitManager.BASE_URL + "/api/auth/login_password/reset/mobile";

    /**
     * 邮箱找回登录密码
     */
    String FIND_LOGIN_PWD_EMAIL = RetrofitManager.BASE_URL + "/api/auth/login_password/reset/email";

    /**
     * 获取用户信息
     */
    String GET_USER_INFO = RetrofitManager.BASE_URL + "/api/auth/info";

    /**
     * 用户退出登录
     */
    String USER_EXIT_LOGIN = RetrofitManager.BASE_URL + "/api/auth/logout";

    /**
     * 用户绑定解绑手机号
     */
    String BIND_MOBILE = RetrofitManager.BASE_URL + "/api/auth/mobile";

    /**
     * 用户绑定解绑邮箱
     */
    String BIND_EMAIL = RetrofitManager.BASE_URL + "/api/auth/email";

    /**
     * 实名认证提交
     */
    String SUBMIT_KYC = RetrofitManager.BASE_URL + "/api/auth/kyc";

    /**
     * 设置用户名
     */
    String SET_USER_NAME = RetrofitManager.BASE_URL + "/api/auth/username";

    /**
     * 修改登录密码
     */
    String MODIFY_LOGIN_PWD = RetrofitManager.BASE_URL + "/api/auth/login_password/update";

    /**
     * 设置交易密码
     */
    String SET_PAY_PWD = RetrofitManager.BASE_URL + "/api/auth/pay_password/set";

    /**
     * 修改交易密码
     */
    String MODIFY_PAY_PWD = RetrofitManager.BASE_URL + "/api/auth/pay_password/update";

    /**
     * 修改交易密码验证类型
     */
    String MODIFY_PAY_PWD_TYPE = RetrofitManager.BASE_URL + "/api/auth/pay_password_type";

    /**
     * 绑定解绑谷歌（获取谷歌密钥）
     */
    String GOOGLE_CHECK = RetrofitManager.BASE_URL + "/api/auth/google";

    /**
     * 登录状态下判断用户密码是否正确
     */
    String CHECK_LOGIN_PWD = RetrofitManager.BASE_URL + "/api/auth/password_confirm";

    /**
     * 上传头像
     */
    String UPLOAD_AVATAR = "/api/auth/avatar";

    /**
     * 阿里云OSS Token
     */
    String GET_OSS_TOKEN = "/api/auth/oss";

    /**
     * APP版本升级检测
     */
    String CHECK_VERSION = RetrofitManager.BASE_URL + "/api/home/appVersion";

    /**
     * 获取邀请好友信息
     */
    String GET_INVITE_INFO = RetrofitManager.BASE_URL + "/api/auth/invitation";

    /**
     * APP版本日志
     */
    String VERSION_LOG = RetrofitManager.BASE_URL + "/api/home/appVersionLog";

    /**
     * 币种列表
     */
    String COIN_TYPE_LIST = RetrofitManager.BASE_URL + "/api/spot/currencies";

    /**
     * 地址相关(添加删除列表)
     */
    String COIN_ADDRESS = "/api/account/address";

    /**
     * 提现
     */
    String WITHDRAW = "/api/account/withdraw";

    /**
     * 资金划转
     */
    String TRANSFER = "/api/account/transfer";

    /**
     * 首页-轮播图
     */
    String HOME_BANNER = "/api/home/banner";

    /**
     * 首页-系统公告
     */
    String HOME_SYSTEM_NOTICE = "/api/home/notice";

    /**
     * 首页-推荐位交易对
     */
    String HOME_RECOMMEND_SYMBOL = "/api/home/recommendSymbol";

    /**
     * 首页-涨幅榜
     */
    String HOME_RISE_FALL = "/api/home/changeRank";

    /**
     * 首页-成交额榜
     */
    String HOME_TRADE = "/api/home/tradesTotalRank";

    /**
     * 首页-新币榜
     */
    String HOME_NEW_COIN = "/api/home/newCoinRank";

    /**
     * 首页-今日市场情绪统计值
     */
    String HOME_TODAY_MARKET_PREDICT_VALUE = "/api/home/todayMarketPredictValue";

    /**
     * 首页-今日市场情绪预测
     */
    String HOME_TODAY_MARKET_PREDICT_VALUE_FORECAST = "/api/home/todayMarketPredict/{type}";

    /**
     * 所有币种对USDT价格
     */
    String GET_COIN_TO_USDT = "/api/home/coin_to_usdt";

    /**
     * 所有币种对BTC价格
     */
    String GET_COIN_TO_BTC = "/api/home/coin_to_btc";

    /**
     * USDT对人民币价格，BTC对人民币价格
     */
    String GET_RATE = "/api/home/cny";

    /**
     * 行情-行情
     */
    String MARKET_COIN = "/api/spot/symbols";

    /**
     * 交易-我的挂单
     */
    String TRADE_MY_PEND_ORDER = "/api/spot/orders";

    /**
     * 交易-撤销单个订单
     */
    String TRADE_CANCEL_SINGLE_ORDER = "/api/spot/orders/{order}";

    /**
     * 交易-撤销全部订单
     */
    String TRADE_CANCEL_ALL_ORDER = "/api/spot/orders/all";

    /**
     * 交易-收付款设置列表
     */
    String TRADE_PAYMENT_LIST = "/api/c2c/account/paymentList";

    /**
     * 交易-收付款设置状态
     */
    String TRADE_PAYMENT_STATUS = "/api/c2c/account/paymentStatus";

    /**
     * 交易-收付款打开/关闭
     */
    String TRADE_PAYMENT_CHANGE = "/api/c2c/account/changeOpen/";

    /**
     * 交易-收付款解绑
     */
    String TRADE_PAYMENT_UNBIND = "/api/c2c/account/delete/";

    /**
     * 交易-收付款新增或修改-银行卡
     */
    String TRADE_PAYMENT_ADD_BANK = "/api/c2c/account/addBankCard";

    /**
     * 交易-收付款新增或修改-支付宝
     */
    String TRADE_PAYMENT_ADD_ZFB = "/api/c2c/account/addAlipayQrCode";

    /**
     * 交易-收付款新增或修改-微信
     */
    String TRADE_PAYMENT_ADD_WECHAT = "/api/c2c/account/addWechatQrCode";

    /**
     * 交易-购买详情页
     */
    String TRADE_GET_BUY_INFO = "/api/c2c/buyDetail";

    /**
     * 交易-法币购买提交
     */
    String TRADE_PARIS_BUY = "/api/c2c/doBuyDetail";

    /**
     * 交易-出售详情页
     */
    String TRADE_GET_SELL_INFO = "/api/c2c/sellerDetail";

    /**
     * 交易-法币出售提交
     */
    String TRADE_PARIS_SELL = "/api/c2c/doSellerDetail";

    /**
     * 交易-法币订单详情页
     */
    String TRADE_PARIS_ORDER_DETAIL = "/api/c2c/orderDetail";

    /**
     * 交易-法币订单支付
     */
    String TRADE_PARIS_ORDER_PAY = "/api/c2c/pay";

    /**
     * 交易-法币订单取消
     */
    String TRADE_PARIS_ORDER_CANCEL = "/api/c2c/cancel";

    /**
     * 交易-法币订单确认收款
     */
    String TRADE_PARIS_ORDER_FINISH = "/api/c2c/finish";

    /**
     * 交易-创建订单
     */
    String TRADE_CREATE_ORDER = "/api/spot/orders";

    /**
     * 资产-我的钱包
     */
    String PROPERTY_MY_WALLET = "/api/account";

    /**
     * 资产-币币资产列表
     */
    String PROPERTY_COIN_COIN = "/api/spot/account";

    /**
     * 资产-法币资产列表
     */
    String PROPERTY_PARIS_COIN = "/api/c2c/account";

    /**
     * 资产-总账户资金流水
     */
    String PROPERTY_MY_WALLET_LOG = "/api/account/log";

    /**
     * 资产-币币账户资金流水
     */
    String PROPERTY_COIN_COIN_LOG = "/api/spot/account/log";

    /**
     * 资产-法币账户资金流水
     */
    String PROPERTY_PARIS_COIN_LOG = "/api/c2c/account/log";

    /**
     * 资产-充币记录
     */
    String PROPERTY_RECHARGE_LOG = "/api/account/recharge";

    /**
     * 资产-提币记录
     */
    String PROPERTY_BRING_LOG = "/api/account/withdraw";

    /**
     * 资产-划转记录
     */
    String PROPERTY_TRANSFER_LOG = "/api/account/transfer";

    /**
     * 资产-总账户资金流水
     */
    String PROPERTY_MY_WALLET_LOG_NEW = "/api/account/capitalflow";

    /**
     * 资产-币币账户资金流水
     */
    String PROPERTY_COIN_COIN_LOG_NEW = "/api/spot/account/capitalflow";

    /**
     * 资产-法币账户资金流水
     */
    String PROPERTY_PARIS_COIN_LOG_NEW = "/api/c2c/account/capitalflow";

    /**
     * K线-图表
     */
    String K_LINE_CHART = "/api/spot/market/kline";

    /**
     * K线-深度
     */
    String K_LINE_DEEP = "/api/spot/market/depth";

    /**
     * K线-成交
     */
    String K_LINE_TRADE = "/api/spot/market/trades";

    /**
     * K线-币种简介
     */
    String K_LINE_COIN_INTRODUCE = "/api/spot/introduce";
    /**
     * 法币交易-支持币种
     */
    String PARIS_COIN_KIND = "/api/c2c/coins";

    /**
     * 法币交易-首页列表
     */
    String PARIS_COIN_MARKET_LIST = "/api/c2c/marketBookList";

    /**
     * 法币交易-委托买入
     */
    String PARIS_COIN_BUY_IN = "/api/c2c/bookBuy";

    /**
     * 法币交易-委托卖出
     */
    String PARIS_COIN_SELL_OUY = "/api/c2c/bookSeller";

    /**
     * 法币交易-价格行情
     */
    String PARIS_COIN_INFO = "/api/c2c/priceTicker";
    /**
     * 法币交易-委托单
     */
    String PARIS_COIN_DELEGATION_ORDER = "/api/c2c/bookList";

    /**
     * 法币交易-撤销委托单
     */
    String PARIS_COIN_CANCEL_ORDER = "/api/c2c/cancelSingleBook/{book}";

    /**
     * 法币交易-暂停接单
     */
    String PARIS_COIN_PAUSE_ORDER = "/api/c2c/bookPause";

    /**
     * 法币交易-委托单详情
     */
    String PARIS_COIN_ORDER_DETAIL = "/api/c2c/bookDetail/{book}";

    /**
     * 法币交易-全部暂停开关状态
     */
    String PARIS_COIN_ORDER_STATE = "/api/c2c/isPause";

    /**
     * 法币交易-全部暂停开关状态
     */
    String PARIS_COIN_MY_ORDER = "/api/c2c/orderList";

    /**
     * 法币交易-商家主页
     */
    String PARIS_COIN_MERCHANT_HOME_PAGE = "/api/c2c/merchantHome";

    /**
     * 法币交易-黑名单/关注列表
     */
    String PARIS_COIN_BLACK_LIST = "/api/c2c/blackList";

    /**
     * 法币交易-关注/取消关注
     */
    String PARIS_COIN_ATTENTION = "/api/c2c/starOp";

    /**
     * 法币交易-拉黑/取消拉黑
     */
    String PARIS_COIN_BLACK = "/api/c2c/blackOp";

    /**
     * 获取所有的交易对
     */
    String GET_SYMBOLS = "/api/spot/symbols/v2";

    /**
     * 获取所有的交易对
     */
    String GET_MARKET = "/api/spot/tickers";
}