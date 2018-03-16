package com.explame.testreactnative.http;

import com.explame.testreactnative.utils.LogUtils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Retrofit的接口管理类
 * <p/>
 * Created by ZhengSheng on 2017/3/20.
 */

public class RetrofitServiceManager {


    public static final boolean isDebug = true;
    private static int DEFAULT_CONNECT_TIME_OUT = 10; // 超时时间 10s
    private static int DEFAULT_READ_TIME_OUT = 10;

    private OkHttpClient.Builder httpClientBuilder;

    private OkHttpClient sClient;

    private RetrofitServiceManager() {
//        ToastUtils.showLongToast("连接超时时间:" + DEFAULT_CONNECT_TIME_OUT);
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        httpClientBuilder.connectTimeout(DEFAULT_CONNECT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        httpClientBuilder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

        if (true) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    if (message.startsWith("<--") || message.startsWith("{")) {
                        LogUtils.i(message);
                    }
                }
            });
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder
                    .addInterceptor(logging)
                    .addNetworkInterceptor(new StethoInterceptor());
        }
    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    /**
     * 获取RetrofitServiceManager
     *
     * @return
     */
    public static RetrofitServiceManager getInstance() {
        return new RetrofitServiceManager();
//        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的ApiService
     *
     * @return
     */
    public ApiService getApiService() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        return new Retrofit.Builder()
                .baseUrl("http://192.168.123.178/")
//                .client(httpClientBuilder.build())
                .client(lgnoreHttps())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiService.class);
    }


    /**
     * 忽略https验证
     */
    public OkHttpClient lgnoreHttps() {
        sClient = httpClientBuilder.build();
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        HostnameVerifier hv1 = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        String workerClassName = "okhttp3.OkHttpClient";

        try {
            Class workerClass = Class.forName(workerClassName);
            Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(sClient, hv1);

            Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            sslSocketFactory.set(sClient, sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sClient;
    }


}