package me.jasonzhang.netmodel.net.core;

import android.annotation.SuppressLint;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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
    private Retrofit retrofit = null;
    private ApiService apiService = null;
    private RxApiService rxApiService = null;
    private HttpLoggingInterceptor loggingInterceptor;
    @SuppressLint("NewApi")
    private ArrayMap<String, String> commonParamMap = new ArrayMap<>(10);
    private static volatile NetManager sInstance;
    private NetManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClient())
                .build();
        apiService = retrofit.create(ApiService.class);
        rxApiService = retrofit.create(RxApiService.class);
    }
    private OkHttpClient getClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //添加一个网络的拦截器
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request request;
                if (commonParamMap.size()>0) {//如果公共参数个数大约0，生成新的request
                    HttpUrl originalHttpUrl = originalRequest.url();
                    HttpUrl.Builder builder = originalHttpUrl.newBuilder();
                    for (String key : commonParamMap.keySet()) {
                        builder.addQueryParameter(key, commonParamMap.get(key));
                    }
                    builder.addQueryParameter("timeStamp", String.valueOf(System.currentTimeMillis()));
                    request = originalRequest.newBuilder()
                            .url(builder.build())
                            .method(originalRequest.method(), originalRequest.body())
                            .build();
                } else {
                    request = originalRequest;
                }
                return chain.proceed(request);
            }
        });
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

    public Response<BaseResponse<UpgradeModel>> checkUpgrade(int versionCode, String channel) throws IOException {
        return apiService.checkUpgrade(versionCode, channel).execute();
    }
    public Call<BaseResponse<UpgradeModel>> checkUpgradeAsync(int versionCode, String channel, Callback<BaseResponse<UpgradeModel>> callback) {
        Call<BaseResponse<UpgradeModel>> call = apiService.checkUpgrade(versionCode, channel);
        call.enqueue(callback);
        return call;
    }

    public Response<BaseResponse<List<InstallNeceModel>>> getInstallDeceDetail() throws IOException {
        return apiService.getInstallNeceDetail().execute();
    }

    public Call<BaseResponse<List<InstallNeceModel>>> getInstallDeceDetailAsync(Callback<BaseResponse<List<InstallNeceModel>>> callback) {
        Call<BaseResponse<List<InstallNeceModel>>> call = apiService.getInstallNeceDetail();
        call.enqueue(callback);
        return call;
    }

    public Observable<BaseResponse<UpgradeModel>> checkUpgradeRx(int versionCode, String channel) {
        return rxApiService.checkUpgrade(versionCode, channel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Observable<BaseResponse<List<InstallNeceModel>>> getInstallDeceDetailRx() {
        return rxApiService.getInstallNeceDetail();
    }
}
