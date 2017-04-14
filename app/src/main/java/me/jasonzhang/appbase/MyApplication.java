package me.jasonzhang.appbase;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import me.jasonzhang.appbase.net.API;
import me.jasonzhang.appbase.net.core.NetManager;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

/**
 * Created by JifengZhang on 2017/4/6.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Stetho.initializeWithDefaults(this);
        initNetManager();
    }

    private void initNetManager() {
        NetManager manager = NetManager.get(API.BASE_URL);
        manager.setCommonParameter(getCommonParams());
        manager.addNetworkInterceptor(new StethoInterceptor());
        manager.setLogLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private HashMap<String, String> getCommonParams() {
        HashMap<String, String> map = new HashMap<>();
        map.put("device","LETV_X443");
        map.put("letvReleaseVersion","5.9.055S_0227");
        map.put("letvSwVersion","V2401RCN02C059055D02271S");
        try {
            map.put("mac", URLEncoder.encode("b0:1b:d2:09:b1:71", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("letvUiVersion","5.9");
        map.put("store","LETV");
        map.put("letvCarrier","3");
        map.put("imei","");
        map.put("letvDeviceType","1");
        map.put("letvPlatform","3");
        map.put("osVersion","6.0");
        map.put("letvHwVersion","H2000");
        map.put("deviceInfo","X4-43");
        map.put("letvUiType","cibn");
        map.put("Authorization","bearer");
        return map;
    }
}
