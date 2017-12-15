package com.enation.javashop.net.engine.core;

import com.enation.javashop.net.engine.plugin.rxbus.RxBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 监听下载进度的自定义ResponseBody
 */

public class ProgressResponseBody extends ResponseBody
{
    /**
     * 用来计算下载进度的ResponseBody
     */
    private final ResponseBody responseBody;

    /**
     * 数据缓冲区
     */
    private BufferedSource bufferedSource;

    /**
     * 构造
     * @param responseBody 返回体
     */
    public ProgressResponseBody(ResponseBody responseBody){
        this.responseBody = responseBody;
    }

    /**
     * 获取数据类型
     * @return    数据类型
     */
    @Override
    public MediaType contentType()
    {
        return responseBody.contentType();
    }

    /**
     * 获取数据长度
     * @return    数据长度
     */
    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    /**
     * 处理source
     * @return  数据缓冲区
     */
    @Override
    public BufferedSource source()
    {
        if(bufferedSource == null)
        {
            //构建文件缓冲源
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 对数据检测 发射进度
     * @param source 数据
     * @return       数据
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long mProgress = 0;
            private long mLastSendTime = 0;
            //读取下载byte
            @Override
            public long read(Buffer sink, long byteCount) throws IOException
            {
                long bytesRead = super.read(sink, byteCount);
                mProgress += bytesRead == -1 ? 0 : bytesRead;
                if (contentType()!=null&&!contentType().toString().contains("application/json")&&!contentType().toString().contains("image")){
                if (System.currentTimeMillis() - mLastSendTime > 500) {
                    RxBus.getDefault().post(new DownloadProgressEvent(contentLength(), mProgress));
                     mLastSendTime = System.currentTimeMillis();
                 } else if (mProgress == contentLength()) {
                    Observable.just(mProgress).delaySubscription(500, TimeUnit.MILLISECONDS, Schedulers.io()).subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            RxBus.getDefault().post(new DownloadProgressEvent(contentLength(), mProgress));
                        }
                    });
                 }
                }

                return bytesRead;
            }
        };
    }
}
