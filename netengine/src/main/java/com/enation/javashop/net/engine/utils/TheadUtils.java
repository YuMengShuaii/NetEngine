package com.enation.javashop.net.engine.utils;


import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
