package com.enation.javashop.net.engine.utils;


import com.enation.javashop.net.engine.lib.rxandroid.schedulers.AndroidSchedulers;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 线程控制类
 */
public class ThreadFromUtils
{
    /**
     * 默认线程 处理数据在io线程 ，接收数据后的处理在主线程
     * @param <T> Gson转换处理的泛型
     * @return    线程控制对象
     */
    public static <T> ObservableTransformer<T, T> defaultSchedulers() {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        };
    }

    /**
     * 全部在Io线程
     * @param <T> Gson转换处理的泛型
     * @return    线程控制对象
     */
    public static <T> ObservableTransformer<T, T> all_io() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            }
        };
    }

    /**
     * 默认线程 处理数据在io线程 ，接收数据后的处理在主线程
     * @param <T> Gson转换处理的泛型
     * @return    线程控制对象
     */
    public static <T> FlowableTransformer<T, T> defaultFolwSchedulers() {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        };
    }

    /**
     * 全部在Io线程
     * @param <T> Gson转换处理的泛型
     * @return    线程控制对象
     */
    public static <T> FlowableTransformer<T, T> Flow_all_io() {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            }
        };
    }
}