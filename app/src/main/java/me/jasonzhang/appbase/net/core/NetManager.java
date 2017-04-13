package me.jasonzhang.appbase.net.core;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 * Created by JifengZhang on 2017/4/5.
 */

public class NetManager {
    private Retrofit retrofit = null;
    private HttpLoggingInterceptor loggingInterceptor = null;
    private Map<String, String> commonParamMap = null;
    private Object apiService = null;
    private static ConcurrentHashMap<String, NetManager> sInstanceMap = new ConcurrentHashMap<>();
    private NetManager() {
    }
    public static NetManager get(String baseUrl) {
        NetManager manager = sInstanceMap.get(baseUrl);
        if (manager==null) {
            synchronized (NetManager.class) {
                manager = sInstanceMap.get(baseUrl);
                if (manager==null) {
                    manager = new NetManager();
                    manager.createRetrofit(baseUrl);
                    sInstanceMap.put(baseUrl, manager);
                }
            }
        }
        return manager;
    }

    public <T> T getApiService(Class<T> cls) {
        if (apiService==null) {
            synchronized (NetManager.class) {
                if (apiService==null) {
                    apiService = retrofit.create(cls);
                }
            }
        }
        return (T)apiService;
    }

    /**
     * 设置log等级
     * @param level
     */
    public void setLogLevel(HttpLoggingInterceptor.Level level) {
        loggingInterceptor.setLevel(level);
    }

    /**
     * 设置公共参数
     * @param paraMap
     */
    public void setCommonParameter(Map<String, String> paraMap) {
        commonParamMap.clear();
        commonParamMap.putAll(paraMap);
    }

    /********************************内部实现**************************************************/
    private void createRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClient())
                .build();
        initCommonParamMap();
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

}
