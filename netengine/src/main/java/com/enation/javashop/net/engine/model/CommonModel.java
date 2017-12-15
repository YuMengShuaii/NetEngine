package com.enation.javashop.net.engine.model;

/**
 * 数据Model基本接口
 */

public class CommonModel<T> {

    /**
     * 信息
     */
    private String message;
    /**
     * 状态
     */
    private int    result;
    /**
     * 数据内容
     */
    private T      data;


    /**
     * 获取服务器信息
     * @return  服务器信息
     */
    public String getMessage(){
        return message;
    }

    /**
     * 获取服务器返回状态
     * @return    状态码
     */
    public int getResult(){
        return result;
    }

    /**
     * 获取T数据
     * @return T
     */
    public T getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setData(T data) {
        this.data = data;
    }
}
