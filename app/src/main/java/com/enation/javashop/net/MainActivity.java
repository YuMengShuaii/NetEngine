package com.enation.javashop.net;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.enation.javashop.net.engine.config.NetEngineConfig;
import com.enation.javashop.net.engine.core.NetEngineFactory;
import com.enation.javashop.net.engine.plugin.exception.AttachObserver;
import com.enation.javashop.net.engine.plugin.exception.ExceptionHandle;
import com.enation.javashop.net.engine.utils.ThreadFromUtils;
import com.google.gson.Gson;

import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    protected CompositeDisposable compositeDisposable = null;

    protected void unDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }

    }

    private void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Destory","Destory");
        unDisposable();
        if(compositeDisposable != null)
        Log.e("Diaposable",compositeDisposable.size()+"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.textview);
        final TextView textView2 = (TextView) findViewById(R.id.textview2);
        NetEngineConfig.init(getBaseContext()).openLogger();
        //NetEngineConfig.getInstance().addNetInterceptor(new RestfulExceptionInterceptor());
        ApiService apiService = NetEngineFactory.getInstance().createService(ApiService.class,"http://www.magicblog.club:8080/");
        apiService.islogin()
                  .compose(ThreadFromUtils.<Map>defaultSchedulers())
                  .subscribe(new AttachObserver<Map>(this) {
                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable responeThrowable) {
                        Log.e("ERROR",responeThrowable.customMessage);
                    }

                    @Override
                    public void onStart() {
                        Log.e("START","START");
                    }

                    @Override
                    public void attachSubscribe(Disposable disposable) {
                        Log.e("addSubscribe","add");
                        addDisposable(disposable);
                    }

                    @Override
                    public void onNext(Map responseBodyResponse) {
                        Log.e("NEXT",new Gson().toJson(responseBodyResponse));
                    }
                });
//        RxBus.getDefault().register(DownloadProgressEvent.class, BusType.MAIN, new RxBus.RxBusEvent<DownloadProgressEvent>() {
//            @Override
//            public void event(final DownloadProgressEvent data) {
//                        textView.setText(data.getProgressPercent()+"");
//            }
//        });
//        ApiService apiService = NetEngineFactory.getInstance().createService(ApiService.class,"http://192.168.1.178:5555/api/");
//        RxDataUtils.RxGetProcessingData(apiService.getCat("0"), new RxDataUtils.GetListener<List<CatModel>>() {
//            @Override
//            public void success(List<CatModel> data) {
//                textView.setText(data.get(0).getName());
//            }
//
//            @Override
//            public void failed(ErrorBody error) {
//                Log.e("eeee",error.getError_message());
//            }
//
//            @Override
//            public void start() {
//                Log.e("start","start");
//            }
//        });
//        RxDataUtils.RxDownLoad(apiService.download("http://onghqryqs.bkt.clouddn.com/QQ_708.apk"), new DownLoadManager(), new RxDataUtils.DownloadListener() {
//
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void success(File file) {
//
//            }
//
//            @Override
//            public void failed(Throwable e) {
//
//            }
//        });
    }

}
