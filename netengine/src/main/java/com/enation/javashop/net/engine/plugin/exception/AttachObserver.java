package com.enation.javashop.net.engine.plugin.exception;

import android.content.Context;

import com.enation.javashop.net.engine.model.NetState;
import com.enation.javashop.net.engine.model.NetStateEvent;
import com.enation.javashop.net.engine.plugin.rxbus.RxBus;
import com.enation.javashop.net.engine.utils.VoiNetTool;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

/**
 * Created by LDD on 2017/12/14.
 */

public abstract class AttachObserver<T> implements Observer<T> {

    private Context context;

    public AttachObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        if (VoiNetTool.getAPNType(context) == VoiNetTool.netType.noneNet){
            disposable.dispose();
            onNoneNet();
        }else{
            onStart();
            attachSubscribe(disposable);
        }
    }

    @Override
    public void onError(Throwable e) {
        if(e instanceof Exception){
            //访问获得对应的Exception
            onError(ExceptionHandle.handleException(e));
        }else {
            //将Throwable 和 未知错误的status code返回
            onError(new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    public abstract void onError(ExceptionHandle.ResponeThrowable responeThrowable);

    public abstract void onStart();

    public abstract void attachSubscribe(Disposable disposable);

    public void onNoneNet(){}

    @Override
    public void onComplete() {}

}
