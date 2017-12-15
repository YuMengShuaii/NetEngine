package com.enation.javashop.net.engine.config;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

/**
 * NetEnigine配置类
 */

public class NetEngineConfig {
    /**
     * 上下文
     */
    private Context context;

    /**
     * 开启日志标记
     */
    private boolean isOpenLog = false;

    /**
     * Net静态配置类
     */
    private static NetEngineConfig netConfig;

    /**
     * Net拦截器集合
     */
    private List<Interceptor> netInterceptors;
    /**
     * 初始化
     * @param mcontext 上下文
     * @return         NetEngineConfig
     */
    public static NetEngineConfig init(Context mcontext){
        if (netConfig==null){
            netConfig = new NetEngineConfig();
        }
        netConfig.context = mcontext;
        return netConfig;
    }

    public static NetEngineConfig getInstance(){
        return netConfig;
    }

    /**
     * 开启日志
     * @return NetEngineConfig
     */
    public NetEngineConfig openLogger(){
        isOpenLog = true;
        return this;
    }

    /**
     * 添加拦截器
     * @param interceptors
     * @return NetEngineConfig
     */
    public NetEngineConfig addNetInterceptor(Interceptor... interceptors){
        if(netInterceptors == null) netInterceptors = new ArrayList<>();
        for (Interceptor interceptor : interceptors) {
             netInterceptors.add(interceptor);
        }
        return this;
    }

    public NetEngineConfig clearNetInterceptor(){
        netInterceptors.clear();
        return this;
    }

    /**
     * 获取拦截器集合
     * @return
     */
    public List<Interceptor> getNetInterceptors(){
        return netInterceptors;
    }

    /**
     * 获取上下文
     * @return  上下文
     */
    public Context getContext(){
        return context;
    }

    /**
     * 获取开启日志标记
     * @return      标记
     */
    public boolean isOpenLogger(){
        return isOpenLog;
    }
}
