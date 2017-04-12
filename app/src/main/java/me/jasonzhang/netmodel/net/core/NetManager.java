package me.jasonzhang.netmodel.net.core;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.ArrayMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.jasonzhang.netmodel.net.model.InstallNeceModel;
import me.jasonzhang.netmodel.net.model.UpgradeModel;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by JifengZhang on 2017/4/5.
 */

public class NetManager {
    private RxApiService rxApiService = null;
    private HttpLoggingInterceptor loggingInterceptor;
    private Map<String, String> commonParamMap = null;
    private static volatile NetManager sInstance;
    private NetManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClient())
                .build();
        rxApiService = retrofit.create(RxApiService.class);
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
        loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);
        return clientBuilder.build();
    }
    public static NetManager get() {
        if (sInstance==null) {
            synchronized (NetManager.class) {
                if (sInstance==null) {
                    sInstance = new NetManager();
                }
            }
        }
        return sInstance;
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
    public void setLogLevel(HttpLoggingInterceptor.Level level) {
        loggingInterceptor.setLevel(level);
    }

    public void setCommonParameter(Map<String, String> paraMap) {
        commonParamMap.clear();
        commonParamMap.putAll(paraMap);
    }

    public Observable<BaseResponse<UpgradeModel>> checkUpgrade(int versionCode, String channel) {
        return rxApiService.checkUpgrade(versionCode, channel);
    }
    public Observable<BaseResponse<List<InstallNeceModel>>> getInstallDeceDetail() {
        return rxApiService.getInstallNeceDetail();
    }
}
