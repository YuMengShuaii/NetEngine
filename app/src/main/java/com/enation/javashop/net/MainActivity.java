package com.enation.javashop.net;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.enation.javashop.net.engine.core.DownLoadManager;
import com.enation.javashop.net.engine.core.DownloadProgressEvent;
import com.enation.javashop.net.engine.config.NetEngineConfig;
import com.enation.javashop.net.engine.core.NetEngineFactory;
import com.enation.javashop.net.engine.utils.ErrorBody;
import com.enation.javashop.net.engine.core.RxDataUtils;
import com.enation.javashop.net.engine.rxbus.BusType;
import com.enation.javashop.net.engine.rxbus.RxBus;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.textview);
        final TextView textView2 = (TextView) findViewById(R.id.textview2);
        NetEngineConfig.init(getBaseContext()).openLogger();
        RxBus.getDefault().register(DownloadProgressEvent.class, BusType.MAIN, new RxBus.RxBusEvent<DownloadProgressEvent>() {
            @Override
            public void event(final DownloadProgressEvent data) {
                        textView.setText(data.getProgressPercent()+"");
            }
        });
        ApiService apiService = NetEngineFactory.getInstance().createService(ApiService.class,"http://192.168.1.178:5555/api/");
        RxDataUtils.RxGetProcessingData(apiService.getCat("0"), new RxDataUtils.GetListener<List<CatModel>>() {
            @Override
            public void success(List<CatModel> data) {
                textView.setText(data.get(0).getName());
            }

            @Override
            public void failed(ErrorBody error) {
                Log.e("eeee",error.getError_message());
            }

            @Override
            public void start() {
                Log.e("start","start");
            }
        });
        RxDataUtils.RxDownLoad(apiService.download("http://onghqryqs.bkt.clouddn.com/QQ_708.apk"), new DownLoadManager(), new RxDataUtils.DownloadListener() {

            @Override
            public void start() {

            }

            @Override
            public void success(File file) {

            }

            @Override
            public void failed(Throwable e) {

            }
        });
    }

}
