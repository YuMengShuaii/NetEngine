package com.enation.javashop.net.engine.utils;


import com.enation.javashop.net.engine.lib.rxandroid.schedulers.AndroidSchedulers;

import io.reactivex.Scheduler;

/**
 * 线程工具类
 */

public class TheadUtils {

    /**
     * UI线程
     * @return UI线程对象
     */
    public static Scheduler.Worker MainThread(){
        return AndroidSchedulers.mainThread().createWorker();
    }

}
