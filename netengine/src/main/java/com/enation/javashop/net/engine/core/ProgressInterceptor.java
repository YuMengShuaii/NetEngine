package com.enation.javashop.net.engine.core;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 下载进度监听拦截器
 */

public class ProgressInterceptor implements Interceptor
{
    /**
     * 构造方法
     */
    public ProgressInterceptor() {

    }

    /**
     * @param  chain 数据链
     * @return 返回体
     * @throws IOException io异常
     */
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException
    {
        //从数据链中获取Respose
        Response originalResponse = chain.proceed(chain.request());
        //使用集成Respone
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body())).build();
    }
}
