package me.jasonzhang.appbase.net.core;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

import me.jasonzhang.appbase.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 功能：
 *  1、初始化retrofit, 使用OkHttp，GsonConvert，RxJava2
 *  2、设置公共参数
 *  3、添加log拦截器，输出不同类型的log
 * Created by JifengZhang on 2017/4/12.
 */

public class RetrofitProxy {
    private static volatile RetrofitProxy sInstance;
    private Retrofit retrofit;
    private HttpLoggingInterceptor loggingInterceptor;
    private Map<String, String> commonParamMap = null;
    private RetrofitProxy(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClient())
                .build();
        initCommonParamMap();
    }

    static RetrofitProxy get(String baseUrl) {
        if (sInstance ==null) {
            synchronized (RetrofitProxy.class) {
                if (sInstance ==null) {
                    sInstance = new RetrofitProxy(baseUrl);
                }
            }
        }
        return sInstance;
    }

    Retrofit getRetrofit() {
        return retrofit;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void initCommonParamMap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            commonParamMap = new android.util.ArrayMap<>(10);
        } else {
            commonParamMap = new HashMap<>(10);
        }
    }
    private OkHttpClient getClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //添加一个网络的拦截器
        clientBuilder.addInterceptor(commonParaInterceptor);
        //添加log拦截器
        loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        clientBuilder.addInterceptor(loggingInterceptor);
        return clientBuilder.build();
    }

    /**
     * 公共参数拦截器
     */
    private Interceptor commonParaInterceptor = chain -> {
        Request originalRequest = chain.request();
        Request newRequest;
        if (commonParamMap.size()>0) {//如果公共参数个数大约0，生成新的request
            HttpUrl originalHttpUrl = originalRequest.url();
            HttpUrl.Builder builder = originalHttpUrl.newBuilder();
            for (String key : commonParamMap.keySet()) {
                builder.addQueryParameter(key, commonParamMap.get(key));
            }
            builder.addQueryParameter("timeStamp", String.valueOf(System.currentTimeMillis()));
            newRequest = originalRequest.newBuilder()
                    .url(builder.build())
                    .method(originalRequest.method(), originalRequest.body())
                    .build();
        } else {
            newRequest = originalRequest;
        }
        return chain.proceed(newRequest);
    };
    /**
     * 设置log等级
     * @param level
     */
    void setLogLevel(HttpLoggingInterceptor.Level level) {
        loggingInterceptor.setLevel(level);
    }

    void setCommonParameter(Map<String, String> paraMap) {
        commonParamMap.clear();
        commonParamMap.putAll(paraMap);
    }
}
