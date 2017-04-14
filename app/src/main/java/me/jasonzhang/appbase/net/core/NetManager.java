package me.jasonzhang.appbase.net.core;

import android.annotation.TargetApi;
import android.os.Build;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    private String baseUrl = null;
    private List<Interceptor> interceptors;
    private List<Interceptor> networkInterceptors;
    private static ConcurrentHashMap<String, NetManager> sInstanceMap = new ConcurrentHashMap<>();
    private NetManager(String baseUrl) {
        this.baseUrl = baseUrl;
        initCommonParamMap();
        interceptors = new ArrayList<>();
        networkInterceptors = new ArrayList<>();
    }

    public static NetManager get(String baseUrl) {
        NetManager manager = sInstanceMap.get(baseUrl);
        if (manager==null) {
            synchronized (NetManager.class) {
                manager = sInstanceMap.get(baseUrl);
                if (manager==null) {
                    manager = new NetManager(baseUrl);
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
                    apiService = getRetrofit(baseUrl).create(cls);
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
        if (retrofit!=null) {
            throw new IllegalStateException("Call this method must before getApiService");
        }
        if (loggingInterceptor==null) {
            synchronized (NetManager.class) {
                if (loggingInterceptor==null) {
                    loggingInterceptor = new HttpLoggingInterceptor();
                }
            }
        }
        loggingInterceptor.setLevel(level);
    }

    /**
     * 设置公共参数
     * @param paraMap
     */
    public void setCommonParameter(Map<String, String> paraMap) {
        if (retrofit!=null) {
            throw new IllegalStateException("Call this method must before getApiService");
        }
        commonParamMap.clear();
        commonParamMap.putAll(paraMap);
    }

    public void addInterceptor(Interceptor interceptor) {
        if (retrofit!=null) {
            throw new IllegalStateException("Call this method must before getApiService");
        }
        interceptors.add(interceptor);
    }
    public void addNetworkInterceptor(Interceptor interceptor) {
        if (retrofit!=null) {
            throw new IllegalStateException("Call this method must before getApiService");
        }
        networkInterceptors.add(interceptor);
    }

    /********************************内部实现**************************************************/
    private Retrofit getRetrofit(String baseUrl) {
        if (retrofit==null) {
            synchronized (NetManager.class) {
                if (retrofit==null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(getClient())
                            .build();
                }
            }
        }
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(commonParaInterceptor);
        for (Interceptor interceptor: interceptors) {
            builder.addInterceptor(interceptor);
        }
        for (Interceptor interceptor: networkInterceptors) {
            builder.addNetworkInterceptor(interceptor);
        }
        if (loggingInterceptor!=null) {
            builder.addInterceptor(loggingInterceptor);
        }
        return builder.build();
    }

    /**
     * 公共参数拦截器
     */
    private Interceptor commonParaInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request newRequest;
            if (commonParamMap.size()>0) {//如果公共参数个数大约0，生成新的request
                HttpUrl originalHttpUrl = originalRequest.url();
                HttpUrl.Builder builder = originalHttpUrl.newBuilder();
                for (String key : commonParamMap.keySet()) {
                    builder.addQueryParameter(key, commonParamMap.get(key));
                }
                newRequest = originalRequest.newBuilder()
                        .url(builder.build())
                        .method(originalRequest.method(), originalRequest.body())
                        .build();
            } else {
                newRequest = originalRequest;
            }
            return chain.proceed(newRequest);
        }
    };

}
