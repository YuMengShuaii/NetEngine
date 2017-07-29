package com.enation.javashop.net.engine.core;

import android.util.Log;
import android.widget.Toast;

import com.enation.javashop.net.engine.config.NetEngineConfig;
import com.enation.javashop.net.engine.utils.BaseData;
import com.enation.javashop.net.engine.utils.ErrorBody;
import com.enation.javashop.net.engine.utils.TheadUtils;
import com.enation.javashop.net.engine.utils.ThreadFromUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *  数据获取工具类
 */

public class RxDataUtils {

    /**
     * 用来适配 错误信息存储在ErrorBody的适配方法
     * @param call           call对象
     * @param getData        监听
     * @param <T>            泛型
     */
    public static <T> void RxGetProcessingData(Call<T> call, final GetListener<T> getData){
        getData.start();
        call.enqueue(new Callback<T>() {
               @Override
               public void onResponse(Call<T> call, final Response<T> response) {
                   TheadUtils.MainThread().schedule(new Action0() {
                       @Override
                       public void call() {
                           try {
                               if (response.isSuccessful()){
                                   getData.success(response.body());
                               }else{
                                   String jsonstring = String.valueOf(response.errorBody().string());
                                   logger("ProcessingError:"+jsonstring);
                                   JSONObject jsonArray = new JSONObject(jsonstring);
                                   if (jsonArray.has("error_message")) {
                                       getData.failed(new Gson().fromJson(jsonstring,ErrorBody.class));
                                   }
                               }
                           } catch (Exception e) {
                               getData.failed(new ErrorBody(e.getMessage()));
                           }
                       }
                   });
               }

               @Override
               public void onFailure(Call<T> call, final Throwable throwable) {
                    TheadUtils.MainThread().schedule(new Action0() {
                        @Override
                        public void call() {
                            getData.failed(new ErrorBody(throwable.getMessage()));
                        }
                    });
               }
           });
    }


    /**
     * 处理Retrofit注册后的Observable
     * @param observable api观察者
     * @param getData    数据获取接口
     * @param OutType    自定义线程处理
     * @param <T>        Json字符串转换的类 必须继承BaseData 这样才可以进行错误处理
     * @return           订阅者 防止内存泄漏
     */
    public static <T extends BaseData> Subscription RxGet(Observable<T> observable, Observable.Transformer<T, T> OutType, final RxDataListener<T> getData){
        //线程处理
        return  observable.compose(OutType)
                //结果回调
                .subscribe(new Subscriber<T>() {

                    @Override
                    public void onStart() {
                        logger("开始请求网络！");
                        //开始请求网络！
                        getData.start();
                    }

                    @Override
                    public void onCompleted() {
                        logger("网络请求完成！");
                    }
                    //错误处理
                    @Override
                    public void onError(Throwable paramThrowable) {
                        if ((paramThrowable instanceof SocketTimeoutException) || (paramThrowable.getMessage().contains("Failed")) || (paramThrowable.getMessage().contains("502")) || (paramThrowable.getMessage().contains("404"))){
                            ToastS("网络故障！");
                        }
                        logger("请求错误："+ paramThrowable.getMessage());
                        getData.failed(paramThrowable);
                    }
                    //错误处理，返回结果！
                    @Override
                    public void onNext(T o) {
                        logger("请求成功！");
                        if (o.getResult()==1){
                            getData.success(o);
                        }else{
                            getData.failed(new Throwable("获取数据失败！原因："+o.getMessage()));
                        }
                    }
                });
    }

    /**
     * 处理Retrofit注册后的Observable 该类不需要传递线程处理参数，使用默认线程参数  observaon(Mian) subscribeOn(IO)
     * @param observable    API观察者
     * @param rxLisener     数据监听接口
     * @param <T>           Json字符串转换的类 必须继承BaseData 这样才可以进行错误处理
     * @return              订阅者防止内存泄漏
     */
    public static <T extends BaseData> Subscription RxGet(Observable<T> observable, final RxDataListener<T> rxLisener){
        return RxGet(observable, ThreadFromUtils.<T>defaultSchedulers(),rxLisener);
    }

    /**
     * 执行下载操作 并写入本地
     * @param observable       观察者
     * @param downLoadManager  写入本地操作接口，本地有实现类，可以实现该类自定义本地操作接口！
     * @param get              监听
     * @return                 订阅者防止内存泄漏
     */
    public static Subscription RxDownLoad(Observable<ResponseBody> observable , final BaseDownLoadManager downLoadManager, final DownloadListener get){
       return observable
               .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
               .observeOn(Schedulers.io()) //指定线程保存文件
               .map(new Func1<ResponseBody, File>() {
                    @Override
                    public File call(ResponseBody responseBody) {
                        return downLoadManager.writeResponseBodyToDisk(NetEngineConfig.getContext(),responseBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {

                    @Override
                    public void onStart() {
                        get.start();
                        logger("开始下载");
                    }

                    @Override
                    public void onCompleted() {
                        logger("下载完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("下载错误："+e.getMessage());
                        get.failed(e);
                    }

                    @Override
                    public void onNext(File file) {
                        logger("下载成功："+file.getAbsolutePath());
                        get.success(file);
                    }
                });
    }

    /**
     * 数据获取接口
     * @param <T> Gson解析类
     */
    public interface RxDataListener< T extends BaseData> extends Start{
        void success(T data);
        void failed(Throwable e);
    }

    public interface GetListener<T> extends Start {
        void success(T data);
        void failed(ErrorBody error);
    }

    /**
     * 下载监听接口
     */
    public interface DownloadListener extends Start {
        void success(File file);
        void failed(Throwable e);
    }
    /**
     * 开始请求接口
     */
    public interface Start{
        void start();
    }

    /**
     * Toast提示
     * @param message toast的消息
     */
    private static void ToastS(String message){
        Toast.makeText(NetEngineConfig.getContext(),message,Toast.LENGTH_SHORT).show();
    }

    private static void logger(String message){
        if (NetEngineConfig.isOpenLogger()){
            Log.e("RxGet",message);
        }
    }
}

