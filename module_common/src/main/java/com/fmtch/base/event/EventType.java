package com.fmtch.base.event;

/**
 * Created by Maker on 16/10/20.
 */

public interface EventType {

    /**
     * 登录
     */
    int USER_LOGIN = 99;

    /**
     * 退出登录
     */
    int USER_EXIT = 199;

    /**
     * 修改用户信息后刷新用户
     */
    int UPLOAD_USER = 299;

    /**
     * 刷新地址本
     */
    int UPLOAD_ADDRESS_BOOK = 399;

    /**
     * 删除地址本
     */
    int DELETE_ADDRESS_BOOK = 499;

    /**
     * 选择交易对
     */
    int SELECT_SYMBOL = 500;

    /**
     * 切换货币单位
     */
    int CHANGE_UNIT = 599;

    /**
     * 法币交易筛选条件
     */
    int PARIS_COIN_FILTER = 600;

}
