package me.jasonzhang.appbase.net.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by JifengZhang on 2017/4/5.
 */

public class NetManager {
    private RetrofitProxy retrofitProxy;
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
                    manager.retrofitProxy = RetrofitProxy.get(baseUrl);
                    sInstanceMap.put(baseUrl, manager);
                }
            }
        }
        return manager;
    }

    public <T> T getApiService(Class<T> cls) {
        return retrofitProxy.getRetrofit().create(cls);
    }

    /**
     * 设置log等级
     *
     * @param level
     */
    public void setLogLevel(HttpLoggingInterceptor.Level level) {
        retrofitProxy.setLogLevel(level);
    }

    /**
     * 设置公共参数
     *
     * @param paraMap
     */
    public void setCommonParameter(Map<String, String> paraMap) {
        retrofitProxy.setCommonParameter(paraMap);
    }

}
