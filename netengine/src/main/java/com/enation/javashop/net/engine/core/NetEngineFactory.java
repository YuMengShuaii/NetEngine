package com.enation.javashop.net.engine.core;

import android.util.Log;

import com.enation.javashop.net.engine.config.NetEngineConfig;
import com.enation.javashop.net.engine.lib.cookies.ClearableCookieJar;
import com.enation.javashop.net.engine.lib.cookies.PersistentCookieJar;
import com.enation.javashop.net.engine.lib.cookies.cache.SetCookieCache;
import com.enation.javashop.net.engine.lib.cookies.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LDD on 17/7/28.
 */

public class NetEngineFactory {
    /**
     * 默认超时时间
     */
    private final static long DEFAULT_TIMEOUT = 10;

    /**
     * Gson对象
     */
    private final Gson mGsonFormat;

    /**
     * 构造方法，初始化Gson对象
     */
    private NetEngineFactory() {
        mGsonFormat = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
    }

    /**
     * 静态化生成单例对象
     */
    private static class SingletonHolder {
        private static final NetEngineFactory INSTANCE = new NetEngineFactory();
    }

    /**
     * 获取队里对象
     * @return 网络服务对象
     */
    public static NetEngineFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 配置Logger
     * @return   okhttpLogger对象
     */
    private HttpLoggingInterceptor getLog(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("NetEngine",message);
            }
        });
        return logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    /**
     * 构造网络服务Service
     * @param serviceClass  API接口
     * @param baseurl       服务器BaseUrl
     * @param <S>           返回服务对象
     * @return              服务接口
     */
    public <S> S createService(Class<S> serviceClass,String baseurl) {
        //创建CallBack线程池，最大值为5
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        //构建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                //服务器BaseUrl
                .baseUrl(baseurl)
                //配置OkHttpClint
                .client(getOkHttpClient())
                //配置回调线程池
                .callbackExecutor(executorService)
                //添加Gson解析
                .addConverterFactory(GsonConverterFactory.create(mGsonFormat))
                //添加RxJava函数式变成
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //构建
                .build();
        //把接口注册到Retrofit中
        return retrofit.create(serviceClass);
    }

    /**
     * 配置OkHttpClint 此方法需要配置下载进度监听接口 如不需要 请调用其他重载方法
     * @return  okHttpClint对象
     */
    private OkHttpClient getOkHttpClient() {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(NetEngineConfig.getContext().getApplicationContext()));
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (NetEngineConfig.isOpenLogger()){
            //添加Logger
            httpClientBuilder.addInterceptor(getLog());
        }
        //维护Cookies
        httpClientBuilder.cookieJar(cookieJar);
        //设置连接超时时间
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //设置写超时时间
        httpClientBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //设置读超时时间
        httpClientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //设置缓存路径 名称
        File httpCacheDirectory = new File(NetEngineConfig.getContext().getApplicationContext().getCacheDir(), "OkHttpCache");
        //设置缓存大小
        httpClientBuilder.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024));
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            httpClientBuilder.sslSocketFactory(sslSocketFactory).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //添加拦截器，设置下载进度监听监听器
        httpClientBuilder.addInterceptor(new ProgressInterceptor());
        return httpClientBuilder.build();
    }
}
