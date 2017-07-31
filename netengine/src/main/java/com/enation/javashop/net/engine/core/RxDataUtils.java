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
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                   TheadUtils.MainThread().schedule(new Runnable() {
                       @Override
                       public void run() {
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
                    TheadUtils.MainThread().schedule(new Runnable() {
                        @Override
                        public void run() {
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
    public static <T extends BaseData> Disposable RxGet(Observable<T> observable, ObservableTransformer<T, T> OutType, final RxDataListener<T> getData){
                //开始请求网络！
                getData.start();
                logger("开始请求网络！");
                //线程处理
              return observable.compose(OutType)
                //结果回调
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(@NonNull T result) throws Exception {
                        logger("请求成功！");
                        if (result.getResult()==1){
                            getData.success(result);
                        }else{
                            getData.failed(new Throwable("获取数据失败！原因："+result.getMessage()));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
                        if ((e instanceof SocketTimeoutException) || (e.getMessage().contains("Failed")) || (e.getMessage().contains("502")) || (e.getMessage().contains("404"))){
                            ToastS("网络故障！");
                        }
                        logger("请求错误："+ e.getMessage());
                        getData.failed(e);
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
    public static <T extends BaseData> Disposable RxGet(Observable<T> observable, final RxDataListener<T> rxLisener){
        return RxGet(observable, ThreadFromUtils.<T>defaultSchedulers(),rxLisener);
    }

    /**
     * 执行下载操作 并写入本地
     * @param observable       观察者
     * @param downLoadManager  写入本地操作接口，本地有实现类，可以实现该类自定义本地操作接口！
     * @param get              监听
     * @return                 订阅者防止内存泄漏
     */
    public static Disposable RxDownLoad(Observable<ResponseBody> observable , final BaseDownLoadManager downLoadManager, final DownloadListener get){
                get.start();
                logger("开始下载");
        return observable
               .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
               .observeOn(Schedulers.io()) //指定线程保存文件
               .map(new Function<ResponseBody, File>() {
                   @Override
                   public File apply(ResponseBody responseBody) throws Exception {
                       return downLoadManager.writeResponseBodyToDisk(NetEngineConfig.getContext(),responseBody);
                   }
               })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(@NonNull File file) throws Exception {
                        logger("下载成功："+file.getAbsolutePath());
                        get.success(file);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
                        logger("下载错误："+e.getMessage());
                        get.failed(e);
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

