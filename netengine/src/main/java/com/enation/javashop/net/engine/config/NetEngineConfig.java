package com.enation.javashop.net.engine.config;

import android.content.Context;

/**
 * NetEnigine配置类
 */

public class NetEngineConfig {
    /**
     * 上下文
     */
    private static Context context;

    /**
     * 开启日志标记
     */
    private static boolean isOpenLog = false;

    /**
     * Net静态配置类
     */
    private static NetEngineConfig netConfig;

    /**
     * 初始化
     * @param mcontext 上下文
     * @return         NetEngineConfig
     */
    public static NetEngineConfig init(Context mcontext){
        if (netConfig==null){
            netConfig = new NetEngineConfig();
        }
        context = mcontext;
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
     * 获取上下文
     * @return  上下文
     */
    public static Context getContext(){
        return context;
    }

    /**
     * 获取开启日志标记
     * @return      标记
     */
    public static boolean isOpenLogger(){
        return isOpenLog;
    }
}
