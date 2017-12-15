package com.enation.javashop.net.engine.plugin.rxbus;

import android.util.Log;

import com.enation.javashop.net.engine.utils.ThreadFromUtils;

import io.reactivex.BackpressureStrategy;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * RxBus
 */
public class RxBus {
    /**
     * 单例对象
     */
    private static volatile RxBus defaultInstance;

    /**
     * bus对象
     */
    private final Subject<Object> bus;
    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者

    public RxBus() {
        bus = PublishSubject.create().toSerialized();
    }

    // 单例RxBus
    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RxBus();
                }
            }
        }
        return defaultInstance ;
    }

    // 发送一个新的事件
    public void post (Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public  <T> Disposable register(final Class<T> cl, @BusType.Type int type, final RxBusEvent<T> rxevent) {
        return bus.toFlowable(BackpressureStrategy.BUFFER).ofType(cl)
                .compose(type==BusType.MAIN? ThreadFromUtils.<T>defaultFolwSchedulers():ThreadFromUtils.<T>Flow_all_io())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(@NonNull T t) throws Exception {
                        rxevent.event(t);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
                        Log.e("RxBus","订阅"+cl.getClass().getName()+"的Activity出现问题，问题原因："+e.getMessage()+"请快速定位问题！");
                    }
                });
    }
    /**
     * 重写默认U线程方法
     * @param cl         注册事件
     * @param rxevent    回调
     * @param <T>        注册泛型
     * @return           观察者
     */
    public  <T> Disposable register(final Class<T> cl , final RxBusEvent<T> rxevent){
           return register(cl,BusType.MAIN,rxevent);
    }
    /**
     * 事件接受接口
     * @param <T> 事件类
     */
    public interface RxBusEvent<T>{

        void event(T data);

    }
}