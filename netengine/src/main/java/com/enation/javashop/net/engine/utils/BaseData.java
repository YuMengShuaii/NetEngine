package com.enation.javashop.net.engine.utils;

/**
 * 数据Model基本接口
 */

public abstract class BaseData {

    /**
     * 获取服务器信息
     * @return  服务器信息
     */
    public abstract String getMessage();

    /**
     * 获取服务器返回状态
     * @return    状态码
     */
    public abstract int getResult();

}
